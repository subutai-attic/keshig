package io.subutai.plugin.keshig.impl.handler;


import io.subutai.common.command.CommandException;
import io.subutai.common.command.RequestBuilder;
import io.subutai.common.peer.HostNotFoundException;
import io.subutai.common.peer.ResourceHost;
import io.subutai.common.tracker.TrackerOperation;
import io.subutai.plugin.keshig.api.KeshigConfig;
import io.subutai.plugin.keshig.api.entity.Command;
import io.subutai.plugin.keshig.api.entity.History;
import io.subutai.plugin.keshig.api.entity.Server;
import io.subutai.plugin.keshig.api.entity.options.DeployOption;
import io.subutai.plugin.keshig.api.entity.options.TestOption;
import io.subutai.plugin.keshig.impl.KeshigImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.subutai.plugin.keshig.api.KeshigConfig.PRODUCT_KEY;
import static io.subutai.plugin.keshig.api.entity.OperationType.DEPLOY;
import static io.subutai.plugin.keshig.api.entity.OperationType.TEST;
import static io.subutai.plugin.keshig.api.entity.ServerType.DEPLOY_SERVER;
import static io.subutai.plugin.keshig.api.entity.ServerType.PEER_SERVER;
import static io.subutai.plugin.keshig.api.entity.ServerType.TEST_SERVER;

public class DeployOperationHandler implements Runnable {

    private DeployOption deployOption;
    private String serverId;
    private History history;
    private TrackerOperation trackerOperation;
    private KeshigImpl keshig;
    private Server server;

    public DeployOperationHandler(String serverId, DeployOption deployOption, KeshigImpl keshig) {

        this.keshig = keshig;
        this.serverId = serverId;
        this.deployOption = deployOption;
        history = new History();
        trackerOperation = keshig.getTracker().createTrackerOperation(PRODUCT_KEY,
                String.format("Creating %s Deploy tracker object", PRODUCT_KEY));
    }


    @Override
    public void run() {

        if (serverId == null) {

            server = keshig.getServer(DEPLOY_SERVER);
            trackerOperation.addLog("Target server not selected. Using first Deploy server available");

        }

        if (server == null && serverId == null) {
            trackerOperation.addLogFailed("Failed to obtain Deploy server");
            return;
        }

        try {
            trackerOperation.addLog(String.format("Starting %s", DEPLOY));

            String id = UUID.randomUUID().toString();

            history = new History(id, DEPLOY.name(), System.currentTimeMillis(), (serverId == null) ? server.getServerId() : serverId);

            history.setType("DEPLOY");

            keshig.getPluginDAO().saveInfo(KeshigConfig.PRODUCT_HISTORY, history.getId(), history);

            final ResourceHost host = keshig.getPeerManager().getLocalPeer().getResourceHostById((serverId == null) ? server.getServerId() : serverId);

            if (deployOption.getBuildName().equalsIgnoreCase("latest")) {
                deployOption.setBuildName(keshig.getLatestBuild().getId());
            }

            trackerOperation.addLog(String.format("Server used %s", host.getHostname()));

            host.execute(new RequestBuilder(Command.getDeployCommand())
                            .withCmdArgs(deployOption.getArgs())
                            .withTimeout(deployOption.getTimeOut())
                            .withRunAs("ubuntu"),

                    (response, commandResult) -> {

                        if (commandResult.hasCompleted()) {

                            if (commandResult.hasSucceeded()) {

                                trackerOperation.addLogDone(((response.getStdOut() == null) ? "" : (response.getStdOut() + "\n"))
                                        + ((response.getStdErr() == null) ? "" : response.getStdErr()));
                                history.setStdOut("SUCCESS:"+extractServers(commandResult.getStdOut(), deployOption.getBuildName()).toString());

                            } else {

                                trackerOperation.addLogFailed(((response.getStdOut() == null) ? "" : (response.getStdOut() + "\n"))
                                        + ((response.getStdErr() == null) ? "" : response.getStdErr()));
                                history.setStdErr("FAILURE. CHECK TRACKER");
                            }


                            history.setExitCode(commandResult.getExitCode().toString());
                            history.setEndTime(System.currentTimeMillis());

                            keshig.getPluginDAO().saveInfo(KeshigConfig.PRODUCT_HISTORY, history.getId(), history);



                            return;

                        }

                        trackerOperation.addLog(((response.getStdOut() == null) ? "" : (response.getStdOut() + "\n")) + ((response.getStdErr() == null) ? "" : response.getStdErr()));
                    });

        } catch (HostNotFoundException | CommandException e) {
            e.printStackTrace();
        }

    }

    public List<String> extractServers(final String stdOut, final String buildName) {

        List<String> ips = new ArrayList<>();

        final Pattern pattern = Pattern.compile("management\\d=.*");

        final Matcher matcher = pattern.matcher(stdOut);

        boolean found = false;

        while (matcher.find()) {

            final String match = matcher.group();
            final String[] s = match.split("=");
            final Server server = new Server(s[1].trim(), s[0].trim(), s[1].trim(), PEER_SERVER, String.format("Auto-deployed Subutai Management Server:%s", buildName));

            try {
                ips.add(server.getServerAddress());
                keshig.addServer(server);
            } catch (Exception e) {
                e.printStackTrace();
            }
            found = true;
        }
        if (!found) {
            trackerOperation.addLogDone("Deployed server(s) not found");
        }
        return ips;
    }

    public TrackerOperation getTrackerOperation() {
        return trackerOperation;
    }

    public void setTrackerOperation(TrackerOperation trackerOperation) {
        this.trackerOperation = trackerOperation;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public DeployOption getDeployOption() {
        return deployOption;
    }

    public void setDeployOption(DeployOption deployOption) {
        this.deployOption = deployOption;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public UUID getTrackerId() {
        return this.trackerOperation.getId();
    }
}
