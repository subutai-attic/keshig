package io.subutai.plugin.keshigqd.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import io.subutai.common.command.CommandException;
import io.subutai.common.command.CommandResult;
import io.subutai.common.command.RequestBuilder;
import io.subutai.common.mdc.SubutaiExecutors;
import io.subutai.common.peer.HostNotFoundException;
import io.subutai.common.peer.ResourceHost;
import io.subutai.common.tracker.TrackerOperation;
import io.subutai.core.environment.api.EnvironmentManager;
import io.subutai.core.network.api.NetworkManager;
import io.subutai.core.peer.api.PeerManager;
import io.subutai.core.tracker.api.Tracker;
import io.subutai.plugin.common.api.PluginDAO;
import io.subutai.plugin.keshigqd.api.KeshigQD;
import io.subutai.plugin.keshigqd.api.KeshigQDConfig;
import io.subutai.plugin.keshigqd.api.entity.Build;
import io.subutai.plugin.keshigqd.api.entity.Command;
import io.subutai.plugin.keshigqd.api.entity.Server;
import io.subutai.plugin.keshigqd.api.entity.ServerType;
import io.subutai.plugin.keshigqd.impl.handler.BuildOperationHandler;
import io.subutai.plugin.keshigqd.impl.handler.CloneOperationHandler;
import io.subutai.plugin.keshigqd.impl.handler.DeployOperationHandler;
import io.subutai.plugin.keshigqd.impl.handler.TestOperationHandler;


public class KeshigQDImpl implements KeshigQD
{

    private static final Logger LOG = LoggerFactory.getLogger( KeshigQDImpl.class.getName() );

    private Tracker tracker;
    private ExecutorService executor;
    private EnvironmentManager environmentManager;
    private PluginDAO pluginDAO;
    private PeerManager peerManager;
    private NetworkManager networkManager;
    private TrackerOperation trackerOperation;


    public void init()
    {
        executor = SubutaiExecutors.newCachedThreadPool();
    }


    public void destroy()
    {
        executor.shutdown();
    }


    public KeshigQDImpl( PluginDAO pluginDAO )
    {
        this.pluginDAO = pluginDAO;
    }


    @Override
    public void addServer( final Server server ) throws Exception
    {
        Preconditions.checkNotNull( server );
        if ( !getPluginDAO().saveInfo( KeshigQDConfig.PRODUCT_KEY, server.getServerId(), server.toString() ) )
        {
            throw new Exception( "Could not save server info" );
        }
    }


    @Override
    public void removeServer( final Server server )
    {
        pluginDAO.deleteInfo( KeshigQDConfig.PRODUCT_KEY, server.getServerId() );
    }


    @Override
    public Server getServer( final Server server )
    {
        Preconditions.checkNotNull( server );
        return pluginDAO.getInfo( KeshigQDConfig.PRODUCT_KEY, server.getServerId(), Server.class );
    }


    @Override
    public List<Server> getServers()
    {
        return pluginDAO.getInfo( KeshigQDConfig.PRODUCT_KEY, Server.class );
    }


    @Override
    public UUID deploy( Map<String, String> opts )
    {
        List<String> args = new ArrayList<>();

        opts.forEach( ( k, v ) -> addToList( k, v, args ) );

        DeployOperationHandler operationHandler = new DeployOperationHandler( this, args );

        executor.execute( operationHandler );

        return operationHandler.getTrackerId();
    }


    @Override
    public List<Build> getBuilds()
    {
        Server buildServer = getServer( ServerType.DEPLOY_SERVER );

        if ( buildServer == null )
        {
            LOG.error( "Failed to obtain build server" );
            return null;
        }

        ResourceHost buildHost;
        try
        {
            buildHost = getPeerManager().getLocalPeer().getResourceHostById( buildServer.getServerId() );

            CommandResult result = buildHost.execute(
                    new RequestBuilder( Command.getBuilds() ).withCmdArgs( Lists.newArrayList( Command.list ) )
                                                             .withTimeout( 30 ) );
            if ( result.hasSucceeded() )
            {
                return parseBuilds( result.getStdOut() );
            }
        }
        catch ( CommandException | HostNotFoundException e )
        {
            e.printStackTrace();
        }
        return null;
    }


    private List<Build> parseBuilds( String stdout )
    {
        List<Build> list = new ArrayList<>();
        String[] builds = stdout.split( System.getProperty( "line.separator" ) );

        for ( String line : builds )
        {
            String[] build = line.split( "_" );
            Date date = new Date( Long.valueOf( build[2] ) * 100 );
            list.add( new Build( build[0], build[1], date ) );
        }
        return list;
    }


    public Server getServer( ServerType type )
    {
        List<Server> servers = getServers();

        for ( Server server : servers )
        {
            if ( server.getType().name().equalsIgnoreCase( type.name() ) )
            {
                return server;
            }
        }

        return null;
    }


    @Override
    public UUID test( Map<String, String> opts )
    {
        List<String> args = new ArrayList<>();

        opts.forEach( ( k, v ) -> addToList( k, v, args ) );

        TestOperationHandler operationHandler = new TestOperationHandler( this, args );

        executor.execute( operationHandler );

        return operationHandler.getTrackerId();
    }


    @Override
    public UUID build( Map<String, String> opts )
    {
        List<String> args = new ArrayList<>();

        opts.forEach( ( k, v ) -> addToList( k, v, args ) );

        BuildOperationHandler operationHandler = new BuildOperationHandler( this, args );

        executor.execute( operationHandler );

        return operationHandler.getTrackerId();
    }


    @Override
    public UUID clone( final Map<String, String> opts )
    {
        List<String> args = new ArrayList<>();

        opts.forEach( ( k, v ) -> addToList( k, v, args ) );

        CloneOperationHandler cloneOperation = new CloneOperationHandler( this, args );

        executor.execute( cloneOperation );

        return cloneOperation.getTrackerId();
    }


    private void addToList( String k, String v, List<String> arg )
    {
        arg.add( k );
        arg.add( v );
    }


    public Tracker getTracker()
    {
        return tracker;
    }


    public void setTracker( final Tracker tracker )
    {
        this.tracker = tracker;
    }


    public ExecutorService getExecutor()
    {
        return executor;
    }


    public void setExecutor( final ExecutorService executor )
    {
        this.executor = executor;
    }


    public EnvironmentManager getEnvironmentManager()
    {
        return environmentManager;
    }


    public void setEnvironmentManager( final EnvironmentManager environmentManager )
    {
        this.environmentManager = environmentManager;
    }


    public PluginDAO getPluginDAO()
    {
        return pluginDAO;
    }


    public void setPluginDAO( final PluginDAO pluginDAO )
    {
        this.pluginDAO = pluginDAO;
    }


    public PeerManager getPeerManager()
    {
        return peerManager;
    }


    public void setPeerManager( final PeerManager peerManager )
    {
        this.peerManager = peerManager;
    }


    public NetworkManager getNetworkManager()
    {
        return networkManager;
    }


    public void setNetworkManager( final NetworkManager networkManager )
    {
        this.networkManager = networkManager;
    }
}
