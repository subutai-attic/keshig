package io.subutai.plugin.keshigqd.impl.handler;


import java.util.List;
import java.util.UUID;

import io.subutai.common.command.CommandException;
import io.subutai.common.command.RequestBuilder;
import io.subutai.common.peer.HostNotFoundException;
import io.subutai.common.peer.ResourceHost;
import io.subutai.common.tracker.TrackerOperation;
import io.subutai.plugin.keshigqd.api.KeshigQDConfig;
import io.subutai.plugin.keshigqd.api.entity.Command;
import io.subutai.plugin.keshigqd.api.entity.Server;
import io.subutai.plugin.keshigqd.api.entity.ServerType;
import io.subutai.plugin.keshigqd.impl.KeshigQDImpl;


public class TestOperationHandler implements Runnable
{
    private TrackerOperation trackerOperation;
    private KeshigQDImpl keshig;
    private List<String> args;


    public TestOperationHandler( final KeshigQDImpl keshig, final List<String> args )
    {
        this.keshig = keshig;
        this.args = args;
        this.trackerOperation = keshig.getTracker().createTrackerOperation( KeshigQDConfig.PRODUCT_KEY,
                String.format( "Creating %s Test tracker object", KeshigQDConfig.PRODUCT_KEY ) );
    }


    @Override
    public void run()
    {

        trackerOperation.addLog( String.format( "Starting tests with args %s", args.toString() ) );

        Server buildServer = keshig.getServer( ServerType.BUILD_SERVER );

        if ( buildServer == null )
        {
            trackerOperation.addLogFailed( "Failed to obtain test server" );
            return;
        }
        ResourceHost buildHost;
        try
        {
            buildHost = keshig.getPeerManager().getLocalPeer().getResourceHostById( buildServer.getServerId() );

            buildHost.execute( new RequestBuilder( Command.getTestComand() ).withCmdArgs( args ).withTimeout( 600 ),
                    ( response, commandResult ) -> {
                        if ( commandResult.hasCompleted() )
                        {
                            if ( commandResult.hasSucceeded() )
                            {
                                trackerOperation.addLogDone( response.getStdOut() );
                                return;
                            }
                            else
                            {
                                trackerOperation.addLogFailed( response.getStdErr() );
                                return;
                            }
                        }
                        else
                        {
                            trackerOperation.addLog( response.getStdOut() == null ? "" :
                                                     response.getStdOut() + "\n" + response.getStdErr() == null ? "" :
                                                     response.getStdErr() );
                        }
                    } );
        }
        catch ( CommandException | HostNotFoundException e )
        {
            e.printStackTrace();
        }
    }


    public UUID getTrackerId()
    {
        return this.trackerOperation.getId();
    }
}

