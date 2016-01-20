package io.subutai.plugin.keshig.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bouncycastle.util.test.Test;
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
import io.subutai.plugin.keshig.api.Keshig;
import io.subutai.plugin.keshig.api.Profile;
import io.subutai.plugin.keshig.api.entity.Command;
import io.subutai.plugin.keshig.api.entity.History;
import io.subutai.plugin.keshig.api.entity.KeshigServer;
import io.subutai.plugin.keshig.api.entity.PeerInfo;
import io.subutai.plugin.keshig.api.entity.Server;
import io.subutai.plugin.keshig.api.entity.options.DeployOption;
import io.subutai.plugin.keshig.api.entity.options.Option;
import io.subutai.plugin.keshig.api.entity.options.TestOption;
import io.subutai.plugin.keshig.impl.handler.ServerStatusUpdateHandler;

import static io.subutai.plugin.keshig.api.KeshigConfig.KESHIG_SERVER;
import static io.subutai.plugin.keshig.api.KeshigConfig.PRODUCT_HISTORY;
import static io.subutai.plugin.keshig.api.KeshigConfig.PRODUCT_KEY;
import static io.subutai.plugin.keshig.api.KeshigConfig.PROFILE;


public class KeshigImpl implements Keshig
{

    private static final Logger LOG = LoggerFactory.getLogger( KeshigImpl.class.getName() );

    //@formatter:off
    private Tracker tracker;
    private ExecutorService executor;
    private EnvironmentManager environmentManager;
    private PluginDAO pluginDAO;
    private PeerManager peerManager;
    private NetworkManager networkManager;
    private TrackerOperation trackerOperation;
    //@formatter:on


    public KeshigImpl( final PluginDAO pluginDAO )
    {
        this.pluginDAO = pluginDAO;
    }


    public void init()
    {
        this.executor = SubutaiExecutors.newCachedThreadPool();
    }


    public void destroy()
    {
        this.executor.shutdown();
    }


    public void addOption( final Option option )
    {
        this.pluginDAO.saveInfo( PRODUCT_KEY, option.getName(), option );
    }


    public TestOption getTestOption( final String optionName )
    {
        return this.pluginDAO.getInfo( PRODUCT_KEY, optionName, TestOption.class );
    }


    public DeployOption getDeployOption( final String name )
    {
        return this.pluginDAO.getInfo( PRODUCT_KEY, name, DeployOption.class );
    }


    @Override
    public List<TestOption> getAllTestOptions()
    {
        return this.pluginDAO.getInfo( PRODUCT_KEY, TestOption.class );
    }


    @Override
    public List<DeployOption> getAllDeployOptions()
    {
        return this.pluginDAO.getInfo( PRODUCT_KEY, DeployOption.class );
    }


    @Override
    public void deleteOption( String name )
    {
        this.pluginDAO.deleteInfo( PRODUCT_KEY, name );
    }


    public void addServer( final Server server ) throws Exception
    {

        Preconditions.checkNotNull( server );

        if ( !this.getPluginDAO().saveInfo( PRODUCT_KEY, server.getServerName(), server ) )
        {
            throw new Exception( "Could not save server info" );
        }
    }


    public void removeServer( final String serverName )
    {

        this.pluginDAO.deleteInfo( PRODUCT_KEY, serverName );
    }


    public Server getServer( final String serverName )
    {
        Preconditions.checkNotNull( serverName );
        return this.pluginDAO.getInfo( PRODUCT_KEY, serverName, Server.class );
    }


    public List<Server> getServers()
    {
        return this.pluginDAO.getInfo( PRODUCT_KEY, Server.class );
    }


    @Override
    public void addKeshigServer( final KeshigServer server ) throws Exception
    {
        Preconditions.checkNotNull( server );

        if ( !this.getPluginDAO().saveInfo( KESHIG_SERVER, server.getHostname(), server ) )
        {
            throw new Exception( "Could not save server info" );
        }
    }


    @Override
    public void removeKeshigServer( final String hostname )
    {
        this.pluginDAO.deleteInfo( KESHIG_SERVER, hostname );
    }


    @Override
    public void updateKeshigServer( final KeshigServer keshigServer )
    {
        try
        {
            addKeshigServer( keshigServer );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }


    @Override
    public KeshigServer getKeshigServer( final String hostname )
    {
        return this.pluginDAO.getInfo( KESHIG_SERVER, hostname, KeshigServer.class );
    }


    @Override
    public List<KeshigServer> getAllKeshigServers()
    {
        List<KeshigServer> keshigServer = Lists.newArrayList();
        keshigServer = this.pluginDAO.getInfo( KESHIG_SERVER, KeshigServer.class );
        return keshigServer;
    }


    @Override
    public void dropAllServers()
    {
        List<KeshigServer> existingServer = getAllKeshigServers();
        for ( KeshigServer server : existingServer )
        {
            removeKeshigServer( server.getHostname() );
        }
    }


    @Override
    public void addKeshigServers( final List<KeshigServer> servers )
    {
        for ( KeshigServer keshigServer : servers )
        {
            try
            {
                addKeshigServer( keshigServer );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void updateReserved( final String hostName, final String serverIp, final String usedBy,
                                final String comment )
    {

        KeshigServer keshigServer = getKeshigServer( hostName );

        PeerInfo peerInfo = keshigServer.getPeers().get( serverIp );

        peerInfo.setFree( false );
        peerInfo.setUsedBy( usedBy );
        peerInfo.setComment( comment );

        keshigServer.getPeers().put( peerInfo.getIp(), peerInfo );

        updateKeshigServer( keshigServer );
    }


    @Override
    public void runOption( final String optionName, final String serverId )
    {

    }


    @Override
    public void runProfile( final String profileName )
    {

    }


    @Override
    public void freeReserved( final String hostname, final String serverIp )
    {
        KeshigServer keshigServer = getKeshigServer( hostname );

        PeerInfo peerInfo = keshigServer.getPeers().get( serverIp );

        peerInfo.setFree( true );

        keshigServer.getPeers().put( peerInfo.getIp(), peerInfo );

        updateKeshigServer( keshigServer );
    }


    public List<Profile> getAllProfiles()
    {
        return this.pluginDAO.getInfo( PROFILE, Profile.class );
    }


    public void addProfile( final Profile profile ) throws Exception
    {

        Preconditions.checkNotNull( profile );

        if ( !this.getPluginDAO().saveInfo( PROFILE, profile.getName(), profile ) )
        {

            throw new Exception( "Could not save server info" );
        }
    }


    @Override
    public void export( String buildName, String serverId )
    {

        //        OperationHandler operationHandler = new OperationHandler( this, serverId, buildName, this );
        //
        //        executor.execute( operationHandler );
    }


    @Override
    public void publish( String boxName, String serverId )
    {

    }


    public void deleteProfile( final String profileName )
    {

        this.pluginDAO.deleteInfo( PROFILE, profileName );
    }


    public void updateProfile( final Profile profile )
    {

        try
        {

            addProfile( profile );
        }
        catch ( Exception e )
        {

            e.printStackTrace();
        }
    }


    public Profile getProfile( final String profileName )
    {

        return this.pluginDAO.getInfo( PROFILE, profileName, Profile.class );
    }


    public List<String> getPlaybooks()
    {

        try
        {
            final ResourceHost testHost = this.getPeerManager().getLocalPeer().getResourceHostById( " " );

            final CommandResult result = testHost.execute(
                    new RequestBuilder( Command.getTestComand() ).withCmdArgs( Lists.newArrayList( "-l" ) ) );

            if ( result.hasSucceeded() )
            {

                return this.parsePlaybooks( result.getStdOut() );
            }

            return Lists.newArrayList( "" );
        }
        catch ( CommandException | HostNotFoundException e )
        {

            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void saveHistory( History history )
    {

        Preconditions.checkNotNull( history );

        if ( !this.getPluginDAO().saveInfo( PRODUCT_HISTORY, history.getId(), history ) )
        {

            try
            {
                throw new Exception( "Could not save server info" );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }
    }


    private List<String> parsePlaybooks( String stdOut )
    {
        String playbookRegEx = ".*.story";
        final Pattern pattern = Pattern.compile( playbookRegEx );
        final Matcher matcher = pattern.matcher( stdOut );
        List<String> playbooks = new ArrayList<>();

        while ( matcher.find() )
        {
            final String match = matcher.group();
            playbooks.add( match );
        }
        return playbooks;
    }


    @Override
    public void updateKeshigServerStatuses()
    {
        ServerStatusUpdateHandler serverStatusUpdateHandler = new ServerStatusUpdateHandler( this );
        serverStatusUpdateHandler.run();
    }


    @Override
    public List<History> listHistory()
    {
        List<History> allHistory = Lists.newArrayList();
        allHistory = this.pluginDAO.getInfo( PRODUCT_HISTORY, History.class );
        return allHistory;
    }


    @Override
    public History getHistory( String historyId )
    {
        return this.pluginDAO.getInfo( PRODUCT_HISTORY, historyId, History.class );
    }


    @Override
    public List<Profile> listProfiles()
    {
        return this.pluginDAO.getInfo( PROFILE, Profile.class );
    }


    //getters and setters
    public Tracker getTracker()
    {
        return this.tracker;
    }


    public void setTracker( final Tracker tracker )
    {
        this.tracker = tracker;
    }


    public ExecutorService getExecutor()
    {
        return this.executor;
    }


    public void setExecutor( final ExecutorService executor )
    {
        this.executor = executor;
    }


    public EnvironmentManager getEnvironmentManager()
    {
        return this.environmentManager;
    }


    public void setEnvironmentManager( final EnvironmentManager environmentManager )
    {
        this.environmentManager = environmentManager;
    }


    public PluginDAO getPluginDAO()
    {
        return this.pluginDAO;
    }


    public void setPluginDAO( final PluginDAO pluginDAO )
    {
        this.pluginDAO = pluginDAO;
    }


    public PeerManager getPeerManager()
    {
        return this.peerManager;
    }


    public void setPeerManager( final PeerManager peerManager )
    {
        this.peerManager = peerManager;
    }


    public NetworkManager getNetworkManager()
    {
        return this.networkManager;
    }


    public void setNetworkManager( final NetworkManager networkManager )
    {
        this.networkManager = networkManager;
    }
}
