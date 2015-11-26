package io.subutai.plugin.keshig.impl.handler;


import io.subutai.common.command.CommandException;
import io.subutai.common.command.RequestBuilder;
import io.subutai.common.peer.HostNotFoundException;
import io.subutai.common.peer.ResourceHost;
import io.subutai.common.tracker.TrackerOperation;
import io.subutai.plugin.keshig.api.KeshigConfig;
import io.subutai.plugin.keshig.api.entity.Build;
import io.subutai.plugin.keshig.api.entity.Command;
import io.subutai.plugin.keshig.api.entity.History;
import io.subutai.plugin.keshig.api.entity.Server;
import io.subutai.plugin.keshig.api.entity.options.BuildOption;
import io.subutai.plugin.keshig.api.entity.options.CloneOption;
import io.subutai.plugin.keshig.impl.KeshigImpl;

import java.util.UUID;

import static io.subutai.plugin.keshig.api.KeshigConfig.PRODUCT_KEY;
import static io.subutai.plugin.keshig.api.entity.OperationType.BUILD;
import static io.subutai.plugin.keshig.api.entity.OperationType.CLONE;
import static io.subutai.plugin.keshig.api.entity.ServerType.DEPLOY_SERVER;

public class BuildOperationHandler implements Runnable {

    private BuildOption buildOption;
    private String serverId;
    private History history;
    private TrackerOperation trackerOperation;
    private KeshigImpl keshig;
    private Server server;

    public BuildOperationHandler(String serverId, BuildOption buildOption, KeshigImpl keshig) {

        this.keshig = keshig;
        this.serverId = serverId;
        this.buildOption = buildOption;
        history = new History();
        trackerOperation = keshig.getTracker().createTrackerOperation(PRODUCT_KEY,
                String.format("Creating %s Build tracker object", PRODUCT_KEY));
    }

    @Override
    public void run() {

        if (serverId == null) {

            server = keshig.getServer(DEPLOY_SERVER);
            trackerOperation.addLog("Target server not selected. Using first Build server available");

        }

        if (server == null && serverId == null) {
            trackerOperation.addLogFailed("Failed to obtain Build server");
            return;
        }

        try {
            trackerOperation.addLog(String.format("Starting %s", BUILD));

            String id = UUID.randomUUID().toString();

            history = new History(id, BUILD.name(), System.currentTimeMillis(), (serverId == null) ? server.getServerId() : serverId);

            history.setType("BUILD");

            keshig.getPluginDAO().saveInfo(KeshigConfig.PRODUCT_HISTORY, history.getId(), history);

            final ResourceHost host = keshig.getPeerManager().getLocalPeer().getResourceHostById((serverId == null) ? server.getServerId() : serverId);

            trackerOperation.addLog(String.format("Server used %s", host.getHostname()));

            host.execute(new RequestBuilder(Command.getBuildCommand())
                            .withCmdArgs(buildOption.getArgs())
                            .withTimeout(buildOption.getTimeOut()),

                    (response, commandResult) -> {

                        if (commandResult.hasCompleted()) {
                            if (commandResult.hasSucceeded()) {
                                trackerOperation.addLogDone(((response.getStdOut() == null) ? "" : (response.getStdOut() + "\n"))
                                        + ((response.getStdErr() == null) ? "" : response.getStdErr()));
                            } else {
                                trackerOperation.addLogFailed(((response.getStdOut() == null) ? "" : (response.getStdOut() + "\n"))
                                        + ((response.getStdErr() == null) ? "" : response.getStdErr()));
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

    public BuildOption getBuildOption() {
        return buildOption;
    }

    public void setBuildOption(BuildOption buildOption) {
        this.buildOption = buildOption;
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


