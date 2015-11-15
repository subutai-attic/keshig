package io.subutai.plugin.keshigqd.impl.handler;


import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.subutai.common.command.CommandException;
import io.subutai.common.command.RequestBuilder;
import io.subutai.common.peer.HostNotFoundException;
import io.subutai.common.peer.ResourceHost;
import io.subutai.common.tracker.TrackerOperation;
import io.subutai.plugin.keshigqd.api.KeshigQDConfig;
import io.subutai.plugin.keshigqd.api.entity.History;
import io.subutai.plugin.keshigqd.api.entity.OperationType;
import io.subutai.plugin.keshigqd.api.entity.Server;
import io.subutai.plugin.keshigqd.api.entity.ServerType;
import io.subutai.plugin.keshigqd.impl.KeshigQDImpl;


public class OperationHandler implements Runnable
{

    private TrackerOperation trackerOperation;
    private KeshigQDImpl keshig;
    private String serverId;
    private Server server;
    private History history;
    private RequestBuilder command;
    private OperationType operationType;

    private static final Logger LOG = LoggerFactory.getLogger( OperationHandler.class );


    public OperationHandler( final KeshigQDImpl keshig, final RequestBuilder command, OperationType operationType,
                             final String serverId )
    {
        this.operationType = operationType;
        this.command = command;
        this.serverId = serverId;
        this.keshig = keshig;
        this.trackerOperation = keshig.getTracker().createTrackerOperation( KeshigQDConfig.PRODUCT_KEY,
                String.format( "Creating %s %s tracker object", KeshigQDConfig.PRODUCT_KEY,
                        operationType.toString() ) );
    }


    @Override
    public void run()
    {
        //target server was not provided
        //fetch first available server by type
        if ( serverId == null )
        {
            switch ( operationType )
            {
                case BUILD:
                    server = keshig.getServer( ServerType.BUILD_SERVER.toString() );
                    break;
                case DEPLOY:
                    server = keshig.getServer( ServerType.DEPLOY_SERVER.toString() );
                    break;
                case TEST:
                    server = keshig.getServer( ServerType.TEST_SERVER.toString() );
                    break;
            }

            LOG.info( String.format( "Target Server not selected. Using first %s server available",
                    operationType.toString() ) );
        }

        ResourceHost buildHost;
        //server with specified type was not found
        if ( server == null )
        {
            trackerOperation.addLogFailed( String.format( "Failed to obtain %s server", operationType ) );

            return;
        }

        try
        {
            history = new History( UUID.randomUUID().toString(), operationType.toString(), System.currentTimeMillis(),
                    command, server.getServerId() );

            keshig.getPluginDAO().saveInfo( KeshigQDConfig.PRODUCT_HISTORY, history.getId(), history );

            buildHost = keshig.getPeerManager().getLocalPeer().getResourceHostById( server.getServerId() );

            buildHost.execute( command, ( response, commandResult ) -> {
                if ( commandResult.hasCompleted() )
                {
                    if ( commandResult.hasSucceeded() )
                    {
                        trackerOperation.addLogDone( response.getStdOut() );
                    }
                    else
                    {
                        trackerOperation.addLogFailed( response.getStdErr() );
                    }

                    history.setExitCode( commandResult.getExitCode().toString() );
                    history.setStdOut( commandResult.getStdOut() );
                    history.setStdErr( response.getStdErr() == null ? "" : response.getStdErr() );
                    history.setEndTime( System.currentTimeMillis() );

                    keshig.getPluginDAO().saveInfo( KeshigQDConfig.PRODUCT_HISTORY, history.getId(), history );

                    return;
                }
                else
                {
                    trackerOperation.addLog( ( response.getStdOut() == null ? "" : response.getStdOut() + "\n" ) + (
                            response.getStdErr() == null ? "" : response.getStdErr() ) );
                }
            } );
        }
        catch ( HostNotFoundException | CommandException e )
        {
            e.printStackTrace();
        }
    }


    public UUID getTrackerId()
    {
        return this.trackerOperation.getId();
    }
}
