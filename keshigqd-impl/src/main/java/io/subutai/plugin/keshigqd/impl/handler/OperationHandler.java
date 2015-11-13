package io.subutai.plugin.keshigqd.impl.handler;


import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.subutai.common.command.CommandException;
import io.subutai.common.command.RequestBuilder;
import io.subutai.common.peer.HostNotFoundException;
import io.subutai.common.peer.ResourceHost;
import io.subutai.common.tracker.TrackerOperation;
import io.subutai.plugin.keshigqd.api.KeshigQDConfig;
import io.subutai.plugin.keshigqd.api.entity.Command;
import io.subutai.plugin.keshigqd.api.entity.History;
import io.subutai.plugin.keshigqd.api.entity.OperationType;
import io.subutai.plugin.keshigqd.api.entity.Server;
import io.subutai.plugin.keshigqd.api.entity.ServerType;
import io.subutai.plugin.keshigqd.impl.KeshigQDImpl;


public class OperationHandler implements Runnable
{

    private TrackerOperation trackerOperation;
    private KeshigQDImpl keshig;
    private List<String> args;
    private String serverId;
    private Server server;
    private History history;
    private RequestBuilder command;
    private static final Logger LOG = LoggerFactory.getLogger( OperationHandler.class );


    public OperationHandler( final KeshigQDImpl keshig, final RequestBuilder command, final List<String> args,
                             final String serverId )
    {
        this.command = command;
        this.serverId = serverId;
        this.keshig = keshig;
        this.args = args;
        this.trackerOperation = keshig.getTracker().createTrackerOperation( KeshigQDConfig.PRODUCT_KEY,
                String.format( "Creating %s Build tracker object", KeshigQDConfig.PRODUCT_KEY ) );
    }


    @Override
    public void run()
    {
        if ( serverId == null )
        {
            server = keshig.getServer( ServerType.BUILD_SERVER );
            LOG.info( "Target Server not selected. Using first build server available" );
        }

        ResourceHost buildHost;

        if ( server == null )
        {
            trackerOperation.addLogFailed( "Failed to obtain build server" );
            return;
        }

        try
        {
            history = new History( UUID.randomUUID().toString(), OperationType.BUILD.toString(),
                    System.currentTimeMillis(), Command.getBuildCommand(), server.getServerId(), args );

            keshig.getPluginDAO().saveInfo( KeshigQDConfig.PRODUCT_HISTORY, history.getId(), history );

            buildHost = keshig.getPeerManager().getLocalPeer().getResourceHostById( server.getServerId() );

            buildHost.execute( new RequestBuilder( Command.getBuildCommand() ).withCmdArgs( args ).withTimeout( 900 ),
                    ( response, commandResult ) -> {
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

                            keshig.getPluginDAO().saveInfo( KeshigQDConfig.PRODUCT_HISTORY, history.getId(), history );

                            return;
                        }
                        else
                        {
                            trackerOperation.addLog(
                                    ( response.getStdOut() == null ? "" : response.getStdOut() + "\n" ) + (
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
