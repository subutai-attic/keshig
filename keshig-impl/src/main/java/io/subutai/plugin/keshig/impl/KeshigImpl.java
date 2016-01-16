package io.subutai.plugin.keshig.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import io.subutai.common.command.CommandException;
import io.subutai.common.command.CommandResult;
import io.subutai.common.command.RequestBuilder;
import io.subutai.common.mdc.SubutaiExecutors;
import io.subutai.common.peer.HostNotFoundException;
import io.subutai.common.peer.ManagementHost;
import io.subutai.common.peer.ResourceHost;
import io.subutai.common.tracker.TrackerOperation;
import io.subutai.core.environment.api.EnvironmentManager;
import io.subutai.core.network.api.NetworkManager;
import io.subutai.core.peer.api.PeerManager;
import io.subutai.core.tracker.api.Tracker;
import io.subutai.plugin.common.api.PluginDAO;
import io.subutai.plugin.keshig.api.Keshig;
import io.subutai.plugin.keshig.api.KeshigConfig;
import io.subutai.plugin.keshig.api.Profile;
import io.subutai.plugin.keshig.api.entity.Build;
import io.subutai.plugin.keshig.api.entity.Command;
import io.subutai.plugin.keshig.api.entity.History;
import io.subutai.plugin.keshig.api.entity.KeshigServer;
import io.subutai.plugin.keshig.api.entity.OperationType;
import io.subutai.plugin.keshig.api.entity.PeerInfo;
import io.subutai.plugin.keshig.api.entity.Server;
import io.subutai.plugin.keshig.api.entity.ServerType;
import io.subutai.plugin.keshig.api.entity.options.BuildOption;
import io.subutai.plugin.keshig.api.entity.options.CloneOption;
import io.subutai.plugin.keshig.api.entity.options.DeployOption;
import io.subutai.plugin.keshig.api.entity.options.TestOption;
import io.subutai.plugin.keshig.impl.handler.BuildOperationHandler;
import io.subutai.plugin.keshig.impl.handler.CloneOperationHandler;
import io.subutai.plugin.keshig.impl.handler.DeployOperationHandler;
import io.subutai.plugin.keshig.impl.handler.ExportOperationHandler;
import io.subutai.plugin.keshig.impl.handler.OperationHandler;
import io.subutai.plugin.keshig.impl.handler.ServerStatusUpdateHandler;
import io.subutai.plugin.keshig.impl.handler.TestOperationHandler;
import io.subutai.plugin.keshig.impl.workflow.integration.IntegrationWorkflow;

import static io.subutai.plugin.keshig.api.KeshigConfig.KESHIG_SERVER;
import static io.subutai.plugin.keshig.api.KeshigConfig.PRODUCT_HISTORY;
import static io.subutai.plugin.keshig.api.KeshigConfig.PRODUCT_KEY;
import static io.subutai.plugin.keshig.api.KeshigConfig.PROFILE;
import static io.subutai.plugin.keshig.api.entity.OperationType.BUILD;
import static io.subutai.plugin.keshig.api.entity.OperationType.CLONE;
import static io.subutai.plugin.keshig.api.entity.OperationType.DEPLOY;
import static io.subutai.plugin.keshig.api.entity.OperationType.TEST;
import static io.subutai.plugin.keshig.api.entity.OperationType.TPR;
import static io.subutai.plugin.keshig.api.entity.ServerType.BUILD_SERVER;
import static io.subutai.plugin.keshig.api.entity.ServerType.DEPLOY_SERVER;
import static io.subutai.plugin.keshig.api.entity.ServerType.TEST_SERVER;


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


    public List<Server> getServers( final ServerType type )
    {

        final List<Server> servers = this.getServers();
        final List<Server> typedServers = new ArrayList<Server>();

        LOG.info( String.format( "Found servers: %s", servers.toString() ) );

        for ( final Server server : servers )
        {

            LOG.info( String.format( "Server (%s) with type: (%s)", server.toString(), server.getType() ) );

            if ( server.getType().equals( type ) )
            {
                typedServers.add( server );
            }
        }
        return typedServers;
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
    public void updateServer( Server server ) throws Exception
    {
        addServer( server );
    }


    @Override
    public void setServer( String serverId, String serverType, String serverName )
    {

        ResourceHost resourceHost;

        try
        {
            resourceHost = this.getPeerManager().getLocalPeer().getResourceHostById( serverId );
            Server server = new Server( serverId, serverName, resourceHost.getInterfaceByName( "br-int" ).getIp(),
                    ServerType.valueOf( serverType.toUpperCase() ), resourceHost.getHostname() );
            addServer( server );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
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
    public void updateReserved( final String hostName, final String serverIp, final String usedBy )
    {
        KeshigServer keshigServer = getKeshigServer( hostName );
        PeerInfo peerInfo = keshigServer.getPeers().get( serverIp );

        peerInfo.setFree( false );
        peerInfo.setUsedBy(usedBy);

        keshigServer.getPeers().put( peerInfo.getIp(),peerInfo );

        updateKeshigServer( keshigServer );

    }


    public Server getServer( final ServerType serverType )
    {

        final List<Server> serverList = this.getServers( serverType );

        for ( final Server server : serverList )
        {
            if ( server.getType().equals( serverType ) )
            {
                return server;
            }
        }
        return null;
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
        ExportOperationHandler operationHandler = new ExportOperationHandler( serverId, buildName, this );

        executor.execute( operationHandler );
    }


    @Override
    public void publish( String boxName, String serverId )
    {

    }


    @Override
    public void tpr( String serverId )
    {

        try
        {

            final ResourceHost host = getPeerManager().getLocalPeer().getResourceHostById( serverId );
            String id = UUID.randomUUID().toString();
            History history = new History( id, TPR.name(), System.currentTimeMillis(), host.getPeerId() );

            getPluginDAO().saveInfo( KeshigConfig.PRODUCT_HISTORY, history.getId(), history );
            String tprDir = String.format( "/home/ubuntu/repo/%s", id );
            host.execute(
                    new RequestBuilder( "mkdir" ).withCmdArgs( Lists.newArrayList( tprDir ) ).withRunAs( "ubuntu" ) );

            host.execute(
                    new RequestBuilder( "/home/ubuntu/export-tpr" ).withCmdArgs( Lists.newArrayList( "-o", tprDir ) ),
                    ( ( response, commandResult ) -> {

                        if ( commandResult.hasCompleted() )
                        {
                            history.setStdOut( String.format( "/%s/acceptance-tests.pdf", history.getId() ) );
                        }
                        else
                        {
                            history.setStdErr( "ERROR. More details in tracker" );
                        }
                        history.setExitCode( commandResult.getExitCode().toString() );
                        history.setEndTime( System.currentTimeMillis() );
                        getPluginDAO().saveInfo( KeshigConfig.PRODUCT_HISTORY, history.getId(), history );

                        return;
                    } ) );
        }
        catch ( HostNotFoundException | CommandException e )
        {
            e.printStackTrace();
        }
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


    public List<Build> getBuilds()
    {

        final Server buildServer = this.getServer( DEPLOY_SERVER );

        if ( buildServer == null )
        {
            KeshigImpl.LOG.error( "Failed to obtain build server" );
            return null;
        }
        try
        {
            final ResourceHost buildHost =
                    this.getPeerManager().getLocalPeer().getResourceHostById( buildServer.getServerId() );
            final CommandResult result = buildHost.execute( new RequestBuilder( "ls /var/qnd/SNAPS/" ) );

            if ( result.hasSucceeded() )
            {
                return this.parseBuilds( result.getStdOut() );
            }

            return null;
        }
        catch ( CommandException | HostNotFoundException e )
        {

            e.printStackTrace();
            return null;
        }
    }


    public List<String> getPlaybooks()
    {

        final Server testServer = this.getServer( TEST_SERVER );
        if ( testServer == null )
        {
            KeshigImpl.LOG.error( "Failed to obtain build server" );
            return null;
        }
        try
        {
            final ResourceHost testHost =
                    this.getPeerManager().getLocalPeer().getResourceHostById( testServer.getServerId() );

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


    public Build getLatestBuild()
    {

        final List<Build> builds = this.getBuilds();
        return builds.get( 0 );
    }


    @Override
    public UUID runCloneOption( String serverId, String optionName )
    {

        CloneOption cloneOption = ( CloneOption ) getOption( optionName, CLONE );
        CloneOperationHandler cloneOperationHandler = new CloneOperationHandler( serverId, cloneOption, this );
        executor.execute( cloneOperationHandler );
        return cloneOperationHandler.getTrackerId();
    }


    @Override
    public UUID runBuildOption( String serverId, String optionName )
    {

        BuildOption buildOption = ( BuildOption ) getOption( optionName, BUILD );
        BuildOperationHandler buildOperationHandler = new BuildOperationHandler( serverId, buildOption, this );
        executor.execute( buildOperationHandler );
        return buildOperationHandler.getTrackerId();
    }


    @Override
    public UUID runDeployOption( String serverId, String optionName )
    {

        DeployOption deployOption = ( DeployOption ) getOption( optionName, DEPLOY );

        DeployOperationHandler deployOperationHandler = new DeployOperationHandler( serverId, deployOption, this );

        executor.execute( deployOperationHandler );

        return deployOperationHandler.getTrackerId();
    }


    @Override
    public UUID runTestOption( String serverId, String optionName )
    {
        TestOption testOption = ( TestOption ) getOption( optionName, TEST );
        TestOperationHandler testOperationHandler = new TestOperationHandler( serverId, testOption, this );
        executor.execute( testOperationHandler );
        return testOperationHandler.getTrackerId();
    }


    private List<Build> parseBuilds( final String stdout )
    {

        final List<Build> list = new ArrayList<Build>();

        final String[] split = stdout.split( System.getProperty( "line.separator" ) );

        for ( final String line : split )
        {
            final String[] build = line.split( "_" );
            if ( build.length == 3 )
            {
                final Date date = new Date( Long.valueOf( build[2] ) * 1000L );
                LOG.info( "Adding following build  : %s ", line );
                list.add( new Build( line, build[0], build[1], date ) );
            }
        }
        return list;
    }


    public UUID deploy( final RequestBuilder requestBuilder, final String serverId )
    {

        final OperationHandler operationHandler = new OperationHandler( this, requestBuilder, DEPLOY, serverId );
        this.executor.execute( operationHandler );
        return operationHandler.getTrackerId();
    }


    public UUID test( final RequestBuilder requestBuilder, final String serverId )
    {

        final OperationHandler operationHandler = new OperationHandler( this, requestBuilder, TEST, serverId );
        this.executor.execute( operationHandler );
        return operationHandler.getTrackerId();
    }


    public UUID build( final RequestBuilder requestBuilder, final String serverId )
    {

        final OperationHandler operationHandler = new OperationHandler( this, requestBuilder, BUILD, serverId );
        this.executor.execute( operationHandler );
        return operationHandler.getTrackerId();
    }


    public UUID clone( final RequestBuilder requestBuilder, final String serverId )
    {

        final OperationHandler cloneOperation = new OperationHandler( this, requestBuilder, CLONE, serverId );
        this.executor.execute( cloneOperation );
        return cloneOperation.getTrackerId();
    }


    public void runDefaults()
    {

        final IntegrationWorkflow integrationWorkflow = new IntegrationWorkflow( this );

        this.executor.execute( integrationWorkflow );
    }


    @Override
    public void runOption( String optionName, String optionType )
    {

        OperationType type = OperationType.valueOf( optionType.toUpperCase() );

        switch ( type )
        {

            case CLONE:
            {

                final CloneOption cloneOption =
                        ( CloneOption ) getOption( optionName, OperationType.valueOf( optionType.toUpperCase() ) );
                Server cloneServer = getServers( BUILD_SERVER ).get( 0 );
                CloneOperationHandler cloneOperation =
                        new CloneOperationHandler( cloneServer.getServerId(), cloneOption, this );
                executor.execute( cloneOperation );

                break;
            }
            case BUILD:
            {

                final BuildOption buildOption =
                        ( BuildOption ) getOption( optionName, OperationType.valueOf( optionType.toUpperCase() ) );
                Server buildServer = getServers( BUILD_SERVER ).get( 0 );
                final BuildOperationHandler operationHandler =
                        new BuildOperationHandler( buildServer.getServerId(), buildOption, this );
                executor.execute( operationHandler );

                break;
            }
            case DEPLOY:
            {

                final DeployOption deployOption =
                        ( DeployOption ) getOption( optionName, OperationType.valueOf( optionType.toUpperCase() ) );
                Server deployServer = getServers( DEPLOY_SERVER ).get( 0 );
                final DeployOperationHandler operationHandler =
                        new DeployOperationHandler( deployServer.getServerId(), deployOption, this );
                executor.execute( operationHandler );

                break;
            }
            case TEST:
            {

                final TestOption testOption =
                        ( TestOption ) getOption( optionName, OperationType.valueOf( optionType.toUpperCase() ) );
                Server testServer = getServers( TEST_SERVER ).get( 0 );
                final TestOperationHandler operationHandler =
                        new TestOperationHandler( testServer.getServerId(), testOption, this );
                executor.execute( operationHandler );
                break;
            }
        }
    }


    @Override
    public void runProfile( String profileName )
    {

        Profile profile = this.getProfile( profileName );

        IntegrationWorkflow integrationWorkflow = new IntegrationWorkflow( this, profile );

        executor.execute( integrationWorkflow );
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


    private void addToList( final String k, final String v, final List<String> arg )
    {
        arg.add( k );
        arg.add( v );
    }


    public void saveOption( final Object option, final OperationType type )
    {

        switch ( type )
        {
            case CLONE:
            {
                final CloneOption cloneOption = ( CloneOption ) option;
                this.getPluginDAO().saveInfo( cloneOption.getType().toString(), cloneOption.getName(), cloneOption );
                break;
            }
            case BUILD:
            {
                final BuildOption buildOption = ( BuildOption ) option;
                this.getPluginDAO().saveInfo( buildOption.getType().toString(), buildOption.getName(), buildOption );
                break;
            }
            case DEPLOY:
            {
                final DeployOption deployOption = ( DeployOption ) option;
                this.getPluginDAO().saveInfo( deployOption.getType().toString(), deployOption.getName(), deployOption );
                break;
            }
            case TEST:
            {
                final TestOption testOption = ( TestOption ) option;
                this.getPluginDAO().saveInfo( testOption.getType().toString(), testOption.getName(), testOption );
                break;
            }
        }
    }


    public Object getActiveOption( final OperationType type )
    {
        switch ( type )
        {
            case CLONE:
            {
                final List<CloneOption> cloneOptions =
                        this.getPluginDAO().getInfo( type.toString(), CloneOption.class );
                for ( final CloneOption option : cloneOptions )
                {
                    if ( option.isActive() )
                    {
                        return option;
                    }
                }
                break;
            }
            case BUILD:
            {
                final List<BuildOption> buildOptions =
                        this.getPluginDAO().getInfo( type.toString(), BuildOption.class );
                for ( final BuildOption buildOption : buildOptions )
                {
                    if ( buildOption.isActive() )
                    {
                        return buildOption;
                    }
                }
                break;
            }
            case DEPLOY:
            {
                final List<DeployOption> deployOptions =
                        this.getPluginDAO().getInfo( type.toString(), DeployOption.class );
                for ( final DeployOption deployOption : deployOptions )
                {
                    if ( deployOption.isActive() )
                    {
                        return deployOption;
                    }
                }
                break;
            }
            case TEST:
            {
                final List<TestOption> testOptions = this.getPluginDAO().getInfo( type.toString(), TestOption.class );
                for ( final TestOption testOption : testOptions )
                {
                    if ( testOption.isActive() )
                    {
                        return testOption;
                    }
                }
                break;
            }
        }
        return null;
    }


    public void updateOption( final Object option, final OperationType type )
    {
        this.saveOption( option, type );
    }


    public Object getOption( final String optionName, final OperationType type )
    {
        switch ( type )
        {
            case CLONE:
            {
                return this.getPluginDAO().getInfo( type.toString(), optionName, CloneOption.class );
            }
            case BUILD:
            {
                return this.getPluginDAO().getInfo( type.toString(), optionName, BuildOption.class );
            }
            case DEPLOY:
            {
                return this.getPluginDAO().getInfo( type.toString(), optionName, DeployOption.class );
            }
            case TEST:
            {
                return this.getPluginDAO().getInfo( type.toString(), optionName, TestOption.class );
            }
            default:
            {
                return null;
            }
        }
    }


    public void deleteOption( final String optionName, final OperationType type )
    {
        this.getPluginDAO().deleteInfo( type.toString(), optionName );
    }


    public List<?> allOptionsByType( final OperationType type )
    {
        switch ( type )
        {
            case CLONE:
            {
                return this.getPluginDAO().getInfo( type.toString(), CloneOption.class );
            }
            case BUILD:
            {
                return this.getPluginDAO().getInfo( type.toString(), BuildOption.class );
            }
            case DEPLOY:
            {
                return this.getPluginDAO().getInfo( type.toString(), DeployOption.class );
            }
            case TEST:
            {
                return this.getPluginDAO().getInfo( type.toString(), TestOption.class );
            }
            default:
            {
                return null;
            }
        }
    }


    public void setActive( final String optionName, final OperationType type )
    {
        switch ( type )
        {
            case CLONE:
            {
                final CloneOption cloneOption = ( CloneOption ) this.getOption( optionName, type );
                if ( !cloneOption.isActive() )
                {
                    cloneOption.setIsActive( true );
                    this.saveOption( cloneOption, cloneOption.getType() );
                    break;
                }
                break;
            }
            case BUILD:
            {
                final BuildOption buildOption = ( BuildOption ) this.getOption( optionName, type );
                if ( !buildOption.isActive() )
                {
                    buildOption.setIsActive( true );
                    this.saveOption( buildOption, buildOption.getType() );
                    break;
                }
                break;
            }
            case DEPLOY:
            {
                final DeployOption deployOption = ( DeployOption ) this.getOption( optionName, type );
                if ( !deployOption.isActive() )
                {
                    deployOption.setIsActive( true );
                    this.saveOption( deployOption, deployOption.getType() );
                    break;
                }
                break;
            }
            case TEST:
            {
                final TestOption testOption = ( TestOption ) this.getOption( optionName, type );
                if ( !testOption.isActive() )
                {
                    testOption.setIsActive( true );
                    this.saveOption( testOption, testOption.getType() );
                    break;
                }
                break;
            }
        }
    }


    public void deactivate( final String opts, final OperationType type )
    {
        final String optionName = opts.toUpperCase();
        switch ( type )
        {
            case CLONE:
            {
                final CloneOption cloneOption = ( CloneOption ) this.getOption( optionName, type );
                if ( cloneOption != null && cloneOption.isActive() )
                {

                    LOG.info( String.format( "Retrieved %s", cloneOption.toString() ) );
                    cloneOption.setIsActive( false );

                    this.saveOption( cloneOption, cloneOption.getType() );
                    break;
                }
                break;
            }
            case BUILD:
            {
                final BuildOption buildOption = ( BuildOption ) this.getOption( optionName, type );
                if ( buildOption != null && buildOption.isActive() )
                {

                    LOG.info( String.format( "Retrieved %s", buildOption.toString() ) );
                    buildOption.setIsActive( false );

                    this.saveOption( buildOption, buildOption.getType() );
                    break;
                }
                break;
            }
            case DEPLOY:
            {
                final DeployOption deployOption = ( DeployOption ) this.getOption( optionName, type );
                if ( deployOption != null && deployOption.isActive() )
                {

                    deployOption.setIsActive( false );
                    this.saveOption( deployOption, deployOption.getType() );
                    break;
                }
                break;
            }
            case TEST:
            {
                final TestOption testOption = ( TestOption ) this.getOption( optionName, type );
                if ( testOption != null && testOption.isActive() )
                {
                    testOption.setIsActive( false );
                    this.saveOption( testOption, testOption.getType() );
                    break;
                }
                break;
            }
        }
    }


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
