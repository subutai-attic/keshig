
package io.subutai.plugin.keshigqd.impl.handler;

import org.slf4j.LoggerFactory;
import io.subutai.common.command.CommandResult;
import io.subutai.common.command.Response;
import io.subutai.common.peer.ResourceHost;
import io.subutai.common.command.CommandException;
import io.subutai.common.peer.HostNotFoundException;
import java.util.UUID;
import io.subutai.plugin.keshigqd.api.entity.ServerType;
import org.slf4j.Logger;
import io.subutai.plugin.keshigqd.api.entity.OperationType;
import io.subutai.common.command.RequestBuilder;
import io.subutai.plugin.keshigqd.api.entity.History;
import io.subutai.plugin.keshigqd.api.entity.Server;
import io.subutai.plugin.keshigqd.impl.KeshigQDImpl;
import io.subutai.common.tracker.TrackerOperation;

public class OperationHandler implements Runnable
{
    private TrackerOperation trackerOperation;
    private KeshigQDImpl keshig;
    private String serverId;
    private Server server;
    private History history;
    private RequestBuilder command;
    private OperationType operationType;
    private static final Logger LOG = LoggerFactory.getLogger(OperationHandler.class);;

    public OperationHandler(final KeshigQDImpl keshig, final RequestBuilder command, final OperationType operationType, final String serverId) {
        this.operationType = operationType;
        this.command = command;
        this.serverId = serverId;
        this.keshig = keshig;
        this.trackerOperation = keshig.getTracker().createTrackerOperation("KESHIGQD", String.format("Creating %s %s tracker object", "KESHIGQD", operationType.toString()));
    }

    @Override
    public void run() {
        if (this.serverId == null) {
            switch (this.operationType) {
                case BUILD: {
                    this.server = this.keshig.getServer(ServerType.BUILD_SERVER);
                    break;
                }
                case DEPLOY: {
                    this.server = this.keshig.getServer(ServerType.DEPLOY_SERVER);
                    break;
                }
                case TEST: {
                    this.server = this.keshig.getServer(ServerType.TEST_SERVER);
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
            this.keshig.getPluginDAO().saveInfo("KESHIGQD_HISTORY", this.history.getId(), this.history);
            final ResourceHost buildHost = this.keshig.getPeerManager().getLocalPeer().getResourceHostById((this.serverId == null) ? this.server.getServerId() : this.serverId);
            this.trackerOperation.addLog(String.format("Server used %s", buildHost.toString()));
            buildHost.execute(this.command, (response, commandResult) -> {
                if (commandResult.hasCompleted()) {
                    if (commandResult.hasSucceeded()) {
                        this.trackerOperation.addLogDone(response.getStdOut());
                    }
                    else {
                        this.trackerOperation.addLogFailed(response.getStdErr());
                    }
                    this.history.setExitCode(commandResult.getExitCode().toString());
                    this.history.setStdOut(commandResult.getStdOut());
                    this.history.setStdErr((response.getStdErr() == null) ? "" : response.getStdErr());
                    this.history.setEndTime(System.currentTimeMillis());
                    this.keshig.getPluginDAO().saveInfo("KESHIGQD_HISTORY", this.history.getId(), this.history);
                    return;
                }
                this.trackerOperation.addLog(((response.getStdOut() == null) ? "" : (response.getStdOut() + "\n")) + ((response.getStdErr() == null) ? "" : response.getStdErr()));
            });
        }
        catch (HostNotFoundException | CommandException e) {
            e.printStackTrace();
        }
    }

    public UUID getTrackerId() {
        return this.trackerOperation.getId();
    }

}
