package io.subutai.plugin.keshigqd.impl.handler;


import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


public class DeployOperationHandler implements Runnable
{
    private TrackerOperation trackerOperation;
    private KeshigQDImpl keshig;
    private List<String> args;

    private static final Logger LOG = LoggerFactory.getLogger( DeployOperationHandler.class );


    public DeployOperationHandler( final KeshigQDImpl manager, final List<String> args )
    {
        this.keshig = manager;
        this.args = args;
        this.trackerOperation = manager.getTracker().createTrackerOperation( KeshigQDConfig.PRODUCT_KEY,
                String.format( "Creating %s Deploy tracker object", KeshigQDConfig.PRODUCT_KEY ) );
    }


    @Override
    public void run()
    {
        trackerOperation.addLog( String.format( "Starting deploy operation with args %s", args.toString() ) );

        LOG.info( "Starting deployment process" );

        Server buildServer = keshig.getServer( ServerType.DEPLOY_SERVER );

        if ( buildServer == null )
        {
            trackerOperation.addLogFailed( "Failed to obtain build server" );
            return;
        }
        ResourceHost buildHost;
        try
        {
            buildHost = keshig.getPeerManager().getLocalPeer().getResourceHostById( buildServer.getServerId() );
            buildHost.execute( new RequestBuilder( Command.getDeployCommand() ).withCmdArgs( args ).withTimeout( 1800 )
                                                                               .withRunAs( "ubuntu" ),
                    ( response, commandResult ) -> {
                        if ( commandResult.hasCompleted() )
                        {
                            if ( commandResult.hasSucceeded() )
                            {
                                trackerOperation.addLogDone( response.getStdOut() );
                                extractServers( commandResult.getStdOut() );
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


    private void extractServers( String stdOut )
    {

        LOG.info( String.format( "Extracting server info: %s", stdOut ) );
        final String m_pattern = "management\\d=.*";
        Pattern pattern = Pattern.compile( m_pattern );

        Matcher matcher = pattern.matcher( stdOut );

        boolean found = false;

        while ( matcher.find() )
        {
            String match = matcher.group();

            LOG.info( String.format( "Found Server address" + " \"%s\" starting at " +
                    "index and ending at index ", match ) );

            String[] s = match.split( "=" );

            Server server = new Server( s[1].trim(), s[0].trim(), s[1].trim(), ServerType.PEER_SERVER,
                    "Auto-deployed Subutai Management Server" );

            LOG.info( String.format( "Saving server info: %s", server.toString() ) );

            try
            {
                keshig.addServer( server );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
            found = true;
        }
        if ( !found )
        {
            LOG.error( "No match found" );
        }
    }


    public UUID getTrackerId()
    {
        return this.trackerOperation.getId();
    }
}
