package io.subutai.plugin.keshigqd.impl.handler;


import java.util.List;
import java.util.UUID;

import io.subutai.common.command.CommandException;
import io.subutai.common.command.RequestBuilder;
import io.subutai.common.peer.HostNotFoundException;
import io.subutai.common.peer.ResourceHost;
import io.subutai.common.tracker.OperationState;
import io.subutai.common.tracker.TrackerOperation;
import io.subutai.common.tracker.TrackerOperationView;
import io.subutai.core.tracker.api.Tracker;
import io.subutai.plugin.keshigqd.api.KeshigQDConfig;
import io.subutai.plugin.keshigqd.api.entity.Server;
import io.subutai.plugin.keshigqd.api.entity.ServerType;
import io.subutai.plugin.keshigqd.impl.KeshigQDImpl;
import io.subutai.plugin.keshigqd.api.entity.Command;


public class BuildOperationHandler implements Runnable
{

    private TrackerOperation trackerOperation;
    private KeshigQDImpl keshig;
    private List<String> args;


    public BuildOperationHandler( final KeshigQDImpl keshig, final List<String> args )
    {
        this.keshig = keshig;
        this.args = args;
        this.trackerOperation = keshig.getTracker().createTrackerOperation( KeshigQDConfig.PRODUCT_KEY,
                String.format( "Creating %s Build tracker object", KeshigQDConfig.PRODUCT_KEY ) );
    }


    @Override
    public void run()
    {
        Server server = keshig.getServer( ServerType.BUILD_SERVER );
        ResourceHost buildHost;

        if ( server == null )
        {
            trackerOperation.addLogFailed( "Failed to obtain build server" );
            return;
        }

        try
        {
            buildHost = keshig.getPeerManager().getLocalPeer().getResourceHostById( server.getServerId() );

            buildHost.execute( new RequestBuilder( Command.getBuildCommand() ).withCmdArgs( args ).withTimeout( 600 ) );
        }
        catch ( HostNotFoundException | CommandException e )
        {
            e.printStackTrace();
        }
    }


    protected static OperationState waitUntilOperationFinish( Tracker tracker, UUID uuid )
    {
        OperationState state = null;
        long start = System.currentTimeMillis();
        while ( !Thread.interrupted() )
        {
            TrackerOperationView po = tracker.getTrackerOperation( KeshigQDConfig.PRODUCT_KEY, uuid );
            if ( po != null )
            {
                if ( po.getState() != OperationState.RUNNING )
                {
                    state = po.getState();
                    break;
                }
            }
            try
            {
                Thread.sleep( 1000 );
            }
            catch ( InterruptedException ex )
            {
                break;
            }
            if ( System.currentTimeMillis() - start > ( 30 + 3 ) * 1000 )
            {
                break;
            }
        }
        return state;
    }


    public UUID getTrackerId()
    {
        return this.trackerOperation.getId();
    }
}
