package io.subutai.plugin.keshig.impl.handler;


import java.util.UUID;


import io.subutai.common.command.CommandException;
import io.subutai.common.command.RequestBuilder;
import io.subutai.common.peer.HostNotFoundException;
import io.subutai.common.peer.ResourceHost;
import io.subutai.common.tracker.TrackerOperation;
import io.subutai.plugin.keshig.api.KeshigConfig;
import io.subutai.plugin.keshig.api.entity.Command;
import io.subutai.plugin.keshig.api.entity.History;
import io.subutai.plugin.keshig.api.entity.Server;
import io.subutai.plugin.keshig.impl.KeshigImpl;
import com.google.common.collect.Lists;

import static io.subutai.plugin.keshig.api.KeshigConfig.PRODUCT_KEY;
import static io.subutai.plugin.keshig.api.entity.OperationType.CLONE;
import static io.subutai.plugin.keshig.api.entity.OperationType.EXPORT;
import static io.subutai.plugin.keshig.api.entity.ServerType.DEPLOY_SERVER;


public class ExportOperationHandler implements Runnable
{

    private String serverId;
    private String buildName;
    private KeshigImpl keshig;
    private TrackerOperation trackerOperation;
    private History history;


    public ExportOperationHandler( final String serverId, final String buildName, final KeshigImpl keshig )
    {
        this.serverId = serverId;
        this.buildName = buildName;
        this.keshig = keshig;
        history = new History();
        trackerOperation = keshig.getTracker().createTrackerOperation( PRODUCT_KEY,
                String.format( "Creating %s Export tracker object", PRODUCT_KEY ) );
    }


    @Override
    public void run()
    {
        Server deployServer = null;

        if ( serverId == null || serverId.length() == 0 )
        {
            deployServer = keshig.getServer( DEPLOY_SERVER );
            this.serverId = deployServer.getServerId();
        }



        trackerOperation.addLog( String.format( "Starting %s", EXPORT ) );

        String id = UUID.randomUUID().toString();

        history = new History( id, CLONE.name(), System.currentTimeMillis(), serverId );

        history.setType( EXPORT.name() );

        keshig.getPluginDAO().saveInfo( KeshigConfig.PRODUCT_HISTORY, history.getId(), history );

        try
        {
            final ResourceHost deployHost = keshig.getPeerManager().getLocalPeer().getResourceHostById( serverId );
            deployHost.execute( new RequestBuilder( Command.getDeployCommand() )
                            .withCmdArgs( Lists.newArrayList(Command.export, this.buildName )).withTimeout( 7200 ),
                    ( response, commandResult ) -> {

                        if ( commandResult.hasCompleted() )
                        {
                            if ( commandResult.hasSucceeded() )
                            {
                                trackerOperation.addLogDone(
                                        ( ( response.getStdOut() == null ) ? "" : ( response.getStdOut() + "\n" ) ) + (
                                                ( response.getStdErr() == null ) ? "" : response.getStdErr() ) );
                                history.setStdOut( "SUCCESS. Uploaded to Atlas Cloud" );
                            }
                            else
                            {
                                trackerOperation.addLogFailed(
                                        ( ( response.getStdOut() == null ) ? "" : ( response.getStdOut() + "\n" ) ) + (
                                                ( response.getStdErr() == null ) ? "" : response.getStdErr() ) );
                                history.setStdErr( "Failure. Check Tracker Operation for more details." );
                            }

                            history.setExitCode( commandResult.getExitCode().toString() );
                            history.setEndTime( System.currentTimeMillis() );

                            keshig.getPluginDAO().saveInfo( KeshigConfig.PRODUCT_HISTORY, history.getId(), history );

                            return;
                        }

                        trackerOperation.addLog(
                                ( ( response.getStdOut() == null ) ? "" : ( response.getStdOut() + "\n" ) ) + (
                                        ( response.getStdErr() == null ) ? "" : response.getStdErr() ) );
                    } );
        }
        catch ( HostNotFoundException | CommandException e )
        {
            e.printStackTrace();
        }
    }


    public String getServerId()
    {
        return serverId;
    }


    public void setServerId( final String serverId )
    {
        this.serverId = serverId;
    }


    public String getBuildName()
    {
        return buildName;
    }


    public void setBuildName( final String buildName )
    {
        this.buildName = buildName;
    }


    public KeshigImpl getKeshig()
    {
        return keshig;
    }


    public void setKeshig( final KeshigImpl keshig )
    {
        this.keshig = keshig;
    }
}
