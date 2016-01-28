package io.subutai.plugin.keshig.impl.handler;


import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import io.subutai.common.command.CommandException;
import io.subutai.common.command.RequestBuilder;
import io.subutai.common.peer.HostNotFoundException;
import io.subutai.common.peer.ResourceHost;
import io.subutai.common.tracker.TrackerOperation;
import io.subutai.plugin.keshig.api.KeshigConfig;
import io.subutai.plugin.keshig.api.entity.History;
import io.subutai.plugin.keshig.api.entity.options.Option;
import io.subutai.plugin.keshig.api.entity.options.TestOption;
import io.subutai.plugin.keshig.impl.KeshigImpl;


public class OperationHandler implements Runnable
{

    private static final Logger LOG = LoggerFactory.getLogger( OperationHandler.class );


    private TrackerOperation trackerOperation;

    private KeshigImpl keshig;
    private String serverId;

    private History history;
    private Option option;


    public OperationHandler( final KeshigImpl keshig, final String serverId, Option option )
    {
        this.serverId = serverId;
        this.keshig = keshig;
        this.option = option;
        this.trackerOperation = keshig.getTracker().createTrackerOperation( KeshigConfig.PRODUCT_KEY,
                String.format( "Creating %s %s tracker object", KeshigConfig.PRODUCT_KEY, option.getType() ) );
    }


    @Override
    public void run()
    {
        try
        {
            this.trackerOperation.addLog( String.format( "Starting %s", option.getType() ) );

            this.history = new History( UUID.randomUUID().toString(), option.getType(), System.currentTimeMillis(),
                    this.serverId );

            this.keshig.getPluginDAO().saveInfo( KeshigConfig.PRODUCT_HISTORY, this.history.getId(), this.history );

            final ResourceHost buildHost = this.keshig.getPeerManager().getLocalPeer().getResourceHostById( serverId );

            this.trackerOperation.addLog( String.format( "Server used %s", buildHost.toString() ) );

            if ( option.getType().equalsIgnoreCase( "TEST" ) )
            {
                keshig.getPeerManager().getLocalPeer().getManagementHost().execute( new RequestBuilder( "mkdir" )
                        .withCmdArgs( Lists.newArrayList( String.format( "/home/ubuntu/repo/%s", history.getId() ) ) )
                        .withRunAs( "ubuntu" ) );
                ( ( TestOption ) option ).setOutputPath( history.getId() );
            }

            buildHost.execute( new RequestBuilder( option.getCommand() ).withCmdArgs( option.getArgs() )
                                                                        .withTimeout( option.getTimeOut() )
                                                                        .withRunAs( "root" ),
                    ( response, commandResult ) -> {
                        if ( commandResult.hasCompleted() )
                        {
                            if ( commandResult.hasSucceeded() )
                            {
                                if ( option.getType().equalsIgnoreCase( "TEST" ) )
                                {
                                    history.setUrl( String.format( "/%s/serenity/index.html", history.getId() ) );
                                }
                                this.trackerOperation.addLogDone( response.getStdOut() );
                            }
                            else
                            {
                                this.trackerOperation.addLogFailed( response.getStdErr() );
                            }

                            this.history.setExitCode( commandResult.getExitCode().toString() );
                            this.history.setEndTime( System.currentTimeMillis() );


                            this.keshig.getPluginDAO()
                                       .saveInfo( KeshigConfig.PRODUCT_HISTORY, this.history.getId(), this.history );


                            return;
                        }
                        this.trackerOperation.addLog(
                                ( ( response.getStdOut() == null ) ? "" : ( response.getStdOut() + "\n" ) ) + (
                                        ( response.getStdErr() == null ) ? "" : response.getStdErr() ) );
                    } );
        }
        catch ( HostNotFoundException | CommandException e )
        {
            e.printStackTrace();
        }
    }


    public History getHistory()
    {
        return history;
    }


    public UUID getTrackerId()
    {
        return this.trackerOperation.getId();
    }
}
