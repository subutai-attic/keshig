package io.subutai.plugin.keshig.impl.handler;

import io.subutai.common.command.CommandException;
import io.subutai.common.command.RequestBuilder;
import io.subutai.common.peer.HostNotFoundException;
import io.subutai.common.peer.ResourceHost;
import io.subutai.common.tracker.TrackerOperation;
import io.subutai.plugin.keshig.api.KeshigQDConfig;
import io.subutai.plugin.keshig.api.entity.History;
import io.subutai.plugin.keshig.api.entity.OperationType;
import io.subutai.plugin.keshig.api.entity.Server;
import io.subutai.plugin.keshig.api.entity.ServerType;
import io.subutai.plugin.keshig.impl.KeshigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.subutai.plugin.keshig.api.entity.ServerType.*;

import java.util.UUID;

public class OperationHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(OperationHandler.class);
    private TrackerOperation trackerOperation;
    private KeshigImpl keshig;
    private String serverId;
    private Server server;
    private History history;
    private RequestBuilder command;
    private OperationType operationType;
    ;

    public OperationHandler(final KeshigImpl keshig, final RequestBuilder command, final OperationType operationType, final String serverId) {
        this.operationType = operationType;
        this.command = command;
        this.serverId = serverId;
        this.keshig = keshig;
        this.trackerOperation = keshig.getTracker().createTrackerOperation(KeshigQDConfig.PRODUCT_KEY, String.format("Creating %s %s tracker object", KeshigQDConfig.PRODUCT_KEY, operationType.toString()));
    }

    @Override
    public void run() {

        if (this.serverId == null) {
            switch (this.operationType) {
                case BUILD: {
                    this.server = this.keshig.getServer(BUILD_SERVER);
                    break;
                }
                case DEPLOY: {
                    this.server = this.keshig.getServer(DEPLOY_SERVER);
                    break;
                }
                case TEST: {
                    this.server = this.keshig.getServer(TEST_SERVER);
                    break;
                }
            }
            LOG.info(String.format("Target Server not selected. Using first %s server available", this.operationType.toString()));
        }
        if (this.server == null && this.serverId == null) {
            this.trackerOperation.addLogFailed(String.format("Failed to obtain %s server", this.operationType));
            return;
        }
        try {
            this.trackerOperation.addLog(String.format("Starting %s", this.operationType));
            this.history = new History(UUID.randomUUID().toString(), this.operationType.toString(), System.currentTimeMillis(), this.command, (this.serverId == null) ? this.server.getServerId() : this.serverId);

            this.keshig.getPluginDAO().saveInfo(KeshigQDConfig.PRODUCT_HISTORY, this.history.getId(), this.history);
            final ResourceHost buildHost = this.keshig.getPeerManager().getLocalPeer().getResourceHostById((this.serverId == null) ? this.server.getServerId() : this.serverId);
            this.trackerOperation.addLog(String.format("Server used %s", buildHost.toString()));

            buildHost.execute(this.command, (response, commandResult) -> {
                if (commandResult.hasCompleted()) {
                    if (commandResult.hasSucceeded()) {
                        this.trackerOperation.addLogDone(response.getStdOut());
                    } else {
                        this.trackerOperation.addLogFailed(response.getStdErr());
                    }

                    this.history.setExitCode(commandResult.getExitCode().toString());
                    this.history.setStdOut(commandResult.getStdOut());
                    this.history.setStdErr((response.getStdErr() == null) ? "" : response.getStdErr());
                    this.history.setEndTime(System.currentTimeMillis());
                    this.keshig.getPluginDAO().saveInfo(KeshigQDConfig.PRODUCT_HISTORY, this.history.getId(), this.history);

                    return;
                }
                this.trackerOperation.addLog(((response.getStdOut() == null) ? "" : (response.getStdOut() + "\n")) + ((response.getStdErr() == null) ? "" : response.getStdErr()));
            });
        } catch (HostNotFoundException | CommandException e) {
            e.printStackTrace();
        }
    }

    public UUID getTrackerId() {
        return this.trackerOperation.getId();
    }

}
