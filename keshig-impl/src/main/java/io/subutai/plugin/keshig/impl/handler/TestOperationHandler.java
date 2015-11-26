package io.subutai.plugin.keshig.impl.handler;


import com.google.common.collect.Lists;
import io.subutai.common.command.CommandException;
import io.subutai.common.command.RequestBuilder;
import io.subutai.common.peer.HostNotFoundException;
import io.subutai.common.peer.ResourceHost;
import io.subutai.common.tracker.TrackerOperation;
import io.subutai.plugin.keshig.api.KeshigConfig;
import io.subutai.plugin.keshig.api.entity.Command;
import io.subutai.plugin.keshig.api.entity.History;
import io.subutai.plugin.keshig.api.entity.OperationType;
import io.subutai.plugin.keshig.api.entity.Server;
import io.subutai.plugin.keshig.api.entity.options.TestOption;
import io.subutai.plugin.keshig.impl.KeshigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.UUID;

import static io.subutai.plugin.keshig.api.KeshigConfig.PRODUCT_KEY;
import static io.subutai.plugin.keshig.api.entity.OperationType.TEST;
import static io.subutai.plugin.keshig.api.entity.ServerType.PEER_SERVER;
import static io.subutai.plugin.keshig.api.entity.ServerType.TEST_SERVER;

public class TestOperationHandler implements Runnable {

    private TestOption testOption;
    private String serverId;
    private History history;
    private TrackerOperation trackerOperation;
    private KeshigImpl keshig;
    private Server server;

    public TestOperationHandler(String serverId, TestOption testOption, KeshigImpl keshig) {

        this.keshig = keshig;
        this.serverId = serverId;
        this.testOption = testOption;
        history = new History();

        trackerOperation = keshig.getTracker().createTrackerOperation(PRODUCT_KEY,
                String.format("Creating %s Test tracker object", PRODUCT_KEY));
    }

    @Override
    public void run() {
        if (serverId == null) {

            server = keshig.getServer(TEST_SERVER);
            trackerOperation.addLog("Target Server not selected. Using first TEST server available");

        }
        if (server == null && serverId == null) {
            trackerOperation.addLogFailed("Failed to obtain TEST server");
            return;
        }

        try {
            trackerOperation.addLog(String.format("Starting %s", TEST));

            String id = UUID.randomUUID().toString();

            history = new History(id, TEST.name(), System.currentTimeMillis(), (serverId == null) ? server.getServerId() : serverId);

            keshig.getPluginDAO().saveInfo(KeshigConfig.PRODUCT_HISTORY, history.getId(), history);

            final ResourceHost host = keshig.getPeerManager().getLocalPeer().getResourceHostById((serverId == null) ? server.getServerId() : serverId);

            trackerOperation.addLog(String.format("Server used %s", host.getHostname()));

            if (testOption.getTargetIps().size() == 1) {
                if(testOption.getTargetIps().contains("LATEST")){

                    testOption.getTargetIps().clear();

                    final List<Server> serverList = keshig.getServers(PEER_SERVER);

                    for (final Server server : serverList) {
                        if (server.getDescription().contains(keshig.getLatestBuild().getId())) {
                            testOption.getTargetIps().add(server.getServerAddress());
                        }
                    }
                }

            }

            testOption.setTestId(id);

            keshig.getPeerManager().getLocalPeer().getManagementHost().execute(new RequestBuilder("mkdir")
                    .withCmdArgs(Lists.newArrayList(String.format(testOption.getOutputPath(), id))).withRunAs("ubuntu"));

            host.execute(new RequestBuilder(Command.getTestComand())
                            .withCmdArgs(testOption.getArgs())
                            .withTimeout(testOption.getTimeOut()),

                    (response, commandResult) -> {

                        if (commandResult.hasCompleted()) {
                            if (commandResult.hasSucceeded()) {
                                trackerOperation.addLogDone(((response.getStdOut() == null) ? "" : (response.getStdOut() + "\n")) + ((response.getStdErr() == null) ? "" : response.getStdErr()));
                                history.setStdOut(String.format("/%s/serenity/index.html", history.getId()));
                            } else {
                                trackerOperation.addLogFailed(((response.getStdOut() == null) ? "" : (response.getStdOut() + "\n")) + ((response.getStdErr() == null) ? "" : response.getStdErr()));
                                history.setStdErr("ERROR. More details in tracker");
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

    public TestOption getTestOption() {
        return testOption;
    }

    public void setTestOption(TestOption testOption) {
        this.testOption = testOption;
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
