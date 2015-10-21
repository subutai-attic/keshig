package io.subutai.keshigqd.impl;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import io.subutai.core.environment.api.EnvironmentManager;
import io.subutai.core.network.api.NetworkManager;
import io.subutai.core.peer.api.PeerManager;
import io.subutai.core.tracker.api.Tracker;
import io.subutai.keshigqd.api.KeshigQD;
import io.subutai.keshigqd.api.KeshigQDConfig;
import io.subutai.keshigqd.api.entity.Server;
import io.subutai.keshigqd.impl.entity.Command;
import io.subutai.plugin.common.api.PluginDAO;


public class KeshigQDImpl implements KeshigQD
{

    private static final Logger LOG = LoggerFactory.getLogger( KeshigQDImpl.class.getName() );

    private Tracker tracker;
    private ExecutorService executor;
    private EnvironmentManager environmentManager;
    private PluginDAO pluginDAO;
    private PeerManager peerManager;
    private NetworkManager networkManager;


    public KeshigQDImpl( PluginDAO pluginDAO )
    {
        this.pluginDAO = pluginDAO;
    }


    @Override
    public void addServer( final Server server ) throws Exception
    {
        Preconditions.checkNotNull( server );
        if ( !getPluginDAO().saveInfo( server.getServerAddress(), server.toString(), server ) )
        {
            throw new Exception( "Could not save server info" );
        }
    }


    @Override
    public void removeServer( final Server server )
    {
        pluginDAO.deleteInfo( KeshigQDConfig.PRODUCT_KEY, server.getServerAddress() );
    }


    @Override
    public Server getServer( final Server server )
    {
        Preconditions.checkNotNull( server );
        return pluginDAO.getInfo( KeshigQDConfig.PRODUCT_KEY, server.toString(), Server.class );
    }


    @Override
    public List<Server> getServers( final String serverIp )
    {
        return pluginDAO.getInfo( KeshigQDConfig.PRODUCT_KEY, Server.class );
    }


    @Override
    public String deploy( Map<String, String> opts )
    {

        return null;
    }


    @Override
    public String test( Map<String, String> opts )
    {
        return null;
    }


    @Override
    public String build( Map<String, String> opts )
    {
        return null;
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
