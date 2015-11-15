//package io.subutai.plugin.keshigqd.impl;
//
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.ExecutorService;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.google.common.base.Preconditions;
//import com.google.common.collect.Lists;
//
//import io.subutai.common.command.CommandException;
//import io.subutai.common.command.CommandResult;
//import io.subutai.common.command.RequestBuilder;
//import io.subutai.common.mdc.SubutaiExecutors;
//import io.subutai.common.peer.HostNotFoundException;
//import io.subutai.common.peer.ResourceHost;
//import io.subutai.common.tracker.TrackerOperation;
//import io.subutai.core.environment.api.EnvironmentManager;
//import io.subutai.core.network.api.NetworkManager;
//import io.subutai.core.peer.api.PeerManager;
//import io.subutai.core.tracker.api.Tracker;
//import io.subutai.plugin.common.api.PluginDAO;
//import io.subutai.plugin.keshigqd.api.KeshigQD;
//import io.subutai.plugin.keshigqd.api.KeshigQDConfig;
//import io.subutai.plugin.keshigqd.api.entity.Build;
//import io.subutai.plugin.keshigqd.api.entity.Command;
//import io.subutai.plugin.keshigqd.api.entity.Dependencies;
//import io.subutai.plugin.keshigqd.api.entity.Dependency;
//import io.subutai.plugin.keshigqd.api.entity.OperationType;
//import io.subutai.plugin.keshigqd.api.entity.Server;
//import io.subutai.plugin.keshigqd.api.entity.ServerType;
//import io.subutai.plugin.keshigqd.api.entity.options.BuildOption;
//import io.subutai.plugin.keshigqd.api.entity.options.CloneOption;
//import io.subutai.plugin.keshigqd.api.entity.options.DeployOption;
//import io.subutai.plugin.keshigqd.api.entity.options.TestOption;
//import io.subutai.plugin.keshigqd.impl.handler.OperationHandler;
//
//
//public class KeshigQDImpl implements KeshigQD
//{
//
//    private static final Logger LOG = LoggerFactory.getLogger( KeshigQDImpl.class.getName() );
//
//    private Tracker tracker;
//    private ExecutorService executor;
//    private EnvironmentManager environmentManager;
//    private PluginDAO pluginDAO;
//    private PeerManager peerManager;
//    private NetworkManager networkManager;
//    private TrackerOperation trackerOperation;
//
//
//    public void init()
//    {
//        executor = SubutaiExecutors.newCachedThreadPool();
//    }
//
//
//    public void destroy()
//    {
//        executor.shutdown();
//    }
//
//
//    public KeshigQDImpl( PluginDAO pluginDAO )
//    {
//        this.pluginDAO = pluginDAO;
//    }
//
//
//    @Override
//    public void addServer( final Server server ) throws Exception
//    {
//        Preconditions.checkNotNull( server );
//        if ( !getPluginDAO().saveInfo( KeshigQDConfig.PRODUCT_KEY, server.getServerId(), server ) )
//        {
//            throw new Exception( "Could not save server info" );
//        }
//    }
//
//
//    @Override
//    public void removeServer( final String serverId )
//    {
//        pluginDAO.deleteInfo( KeshigQDConfig.PRODUCT_KEY, serverId );
//    }
//
//
//    @Override
//    public List<Server> getServers( ServerType type )
//    {
//        List<Server> servers = getServers();
//
//        List<Server> typedServers = new ArrayList<>();
//
//        LOG.info( String.format( "Found servers: %s", servers.toString() ) );
//
//        for ( Server server : servers )
//        {
//            LOG.info( String.format( "Server (%s) with type: (%s)", server.toString(), server.getType() ) );
//
//            if ( server.getType().toString().equalsIgnoreCase( type.toString() ) )
//            {
//                typedServers.add( server );
//            }
//        }
//
//        return typedServers;
//    }
//
//
//    @Override
//    public Server getServer( final String serverId )
//    {
//        Preconditions.checkNotNull( serverId );
//        return pluginDAO.getInfo( KeshigQDConfig.PRODUCT_KEY, serverId, Server.class );
//    }
//
//
//    @Override
//    public List<Server> getServers()
//    {
//        return pluginDAO.getInfo( KeshigQDConfig.PRODUCT_KEY, Server.class );
//    }
//
//
//    @Override
//    public List<Build> getBuilds()
//    {
//        Server buildServer = getServer( ServerType.DEPLOY_SERVER.toString() );
//
//        if ( buildServer == null )
//        {
//            LOG.error( "Failed to obtain build server" );
//            return null;
//        }
//
//        ResourceHost buildHost;
//        try
//        {
//            buildHost = getPeerManager().getLocalPeer().getResourceHostById( buildServer.getServerId() );
//
//            CommandResult result = buildHost.execute( new RequestBuilder( Command.getDeployCommand() )
//                    .withCmdArgs( Lists.newArrayList( Command.list, "list" ) ).withTimeout( 30 ) );
//            if ( result.hasSucceeded() )
//            {
//                return parseBuilds( result.getStdOut() );
//            }
//        }
//        catch ( CommandException | HostNotFoundException e )
//        {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//    @Override
//    public Build getLatestBuild()
//    {
//        List<Build> builds = getBuilds();
//
//        return builds.get( builds.size() - 1 );
//    }
//
//
//    private List<Build> parseBuilds( String stdout )
//    {
//        List<Build> list = new ArrayList<>();
//        String[] builds = stdout.split( System.getProperty( "line.separator" ) );
//
//        for ( String line : builds )
//        {
//            String[] build = line.split( "_" );
//            Date date = new Date( Long.valueOf( build[2] ) * 1000 );
//            list.add( new Build( line, build[0], build[1], date ) );
//        }
//        return list;
//    }
//
//
//    @Override
//    public UUID deploy( final String optionName, final String serverId )
//    {
//        return null;
//    }
//
//
//    @Override
//    public UUID deploy( final RequestBuilder requestBuilder, final String serverId )
//    {
//        OperationHandler operationHandler =
//                new OperationHandler( this, requestBuilder, OperationType.DEPLOY, serverId );
//
//        executor.execute( operationHandler );
//
//        return operationHandler.getTrackerId();
//    }
//
//
//    @Override
//    public UUID test( RequestBuilder requestBuilder, String serverId )
//    {
//
//        OperationHandler operationHandler = new OperationHandler( this, requestBuilder, OperationType.TEST, serverId );
//
//        executor.execute( operationHandler );
//
//        return operationHandler.getTrackerId();
//    }
//
//
//    @Override
//    public UUID test( final String optionName, final String serverId )
//    {
//        return null;
//    }
//
//
//    @Override
//    public UUID build( RequestBuilder requestBuilder, String serverId )
//    {
//
//        OperationHandler operationHandler = new OperationHandler( this, requestBuilder, OperationType.BUILD, serverId );
//
//        executor.execute( operationHandler );
//
//        return operationHandler.getTrackerId();
//    }
//
//
//    @Override
//    public UUID build( final String optionName, final String serverId )
//    {
//        return null;
//    }
//
//
//    @Override
//    public UUID clone( RequestBuilder requestBuilder, String serverId )
//    {
//
//        OperationHandler cloneOperation = new OperationHandler( this, requestBuilder, OperationType.CLONE, serverId );
//
//        executor.execute( cloneOperation );
//
//        return cloneOperation.getTrackerId();
//    }
//
//
//    @Override
//    public UUID clone( final String optionName, final String serverId )
//    {
//        return null;
//    }
//
//
//    @Override
//    public Map<String, List<Dependency>> getAllPackages()
//    {
//        List<Server> servers = getServers();
//
//        Map<String, List<Dependency>> packages = new HashMap<>();
//
//        for ( Server server : servers )
//        {
//            packages.put( server.getServerId(), getPackages( server.getServerId() ) );
//        }
//        return packages;
//    }
//
//
//    @Override
//    public List<Dependency> getPackages( final String serverId )
//    {
//        List<Dependency> dependencyList = null;
//        try
//        {
//            ResourceHost targetHost = getPeerManager().getLocalPeer().getResourceHostById( serverId );
//            CommandResult result = targetHost.execute( new RequestBuilder( Command.getInstalledPackagesCommand() ) );
//            if ( result.hasSucceeded() )
//            {
//                dependencyList = parsePackages( result.getStdOut() );
//            }
//        }
//        catch ( HostNotFoundException | CommandException e )
//        {
//            e.printStackTrace();
//        }
//
//        return dependencyList;
//    }
//
//
//    private List<Dependency> parsePackages( String stdOut )
//    {
//        List<Dependency> dependencyList = new ArrayList<>();
//        String[] packages = stdOut.split( System.getProperty( "line.separator" ) );
//        for ( String line : packages )
//        {
//            if ( line.startsWith( "ii" ) )
//            {
//                String[] attrs = line.split( "\\s+" );
//                dependencyList.add( new Dependency( attrs[1], attrs[2], attrs[3], attrs[4] ) );
//            }
//        }
//        return dependencyList;
//    }
//
//
//    @Override
//    public List<Dependency> getRequiredPackages( final ServerType serverType )
//    {
//        List<Dependency> dependencies = null;
//
//        switch ( serverType )
//        {
//            case BUILD_SERVER:
//                dependencies = Dependencies.KeshigCloneServer.requiredPackages();
//                break;
//            case DEPLOY_SERVER:
//                dependencies = Dependencies.KeshigDeployServer.requiredPackages();
//                break;
//            case TEST_SERVER:
//                dependencies = Dependencies.KeshigTestServer.requiredPackages();
//                break;
//        }
//        return dependencies;
//    }
//
//
//    @Override
//    public List<Dependency> getMissingPackages( final Server server )
//    {
//        return getMissingPackages( server.getServerId(), server.getType() );
//    }
//
//
//    @Override
//    public List<Dependency> getMissingPackages( final String serverId, final ServerType serverType )
//    {
//        List<Dependency> existingDependencies = getPackages( serverId );
//        List<Dependency> requiredDependencies = getRequiredPackages( serverType );
//        return Dependencies.missingDependencies( existingDependencies, requiredDependencies );
//    }
//
//
//    private void addToList( String k, String v, List<String> arg )
//    {
//        arg.add( k );
//        arg.add( v );
//    }
//
//
//    @Override
//    public void saveOption( final Object option, final OperationType type )
//    {
//        switch ( type )
//        {
//            case CLONE:
//                CloneOption cloneOption = ( CloneOption ) option;
//                getPluginDAO().saveInfo( cloneOption.getType().toString(), cloneOption.getName(), CloneOption.class );
//                break;
//            case BUILD:
//                BuildOption buildOption = ( BuildOption ) option;
//                getPluginDAO().saveInfo( buildOption.getType().toString(), buildOption.getName(), BuildOption.class );
//                break;
//            case DEPLOY:
//                DeployOption deployOption = ( DeployOption ) option;
//                getPluginDAO()
//                        .saveInfo( deployOption.getType().toString(), deployOption.getName(), DeployOption.class );
//                break;
//            case TEST:
//                TestOption testOption = ( TestOption ) option;
//                getPluginDAO().saveInfo( testOption.getType().toString(), testOption.getName(), TestOption.class );
//                break;
//        }
//    }
//
//
//    public Object getActiveOption( final OperationType type )
//    {
//        switch ( type )
//        {
//            case CLONE:
//                List<CloneOption> cloneOptions = getPluginDAO().getInfo( type.toString(), CloneOption.class );
//                for ( CloneOption option : cloneOptions )
//                {
//                    if ( option.isActive() )
//                    {
//                        return option;
//                    }
//                }
//                break;
//            case BUILD:
//                List<BuildOption> buildOptions = getPluginDAO().getInfo( type.toString(), BuildOption.class );
//                for ( BuildOption option : buildOptions )
//                {
//                    if ( option.isActive() )
//                    {
//                        return option;
//                    }
//                }
//                break;
//            case DEPLOY:
//                List<DeployOption> deployOptions = getPluginDAO().getInfo( type.toString(), DeployOption.class );
//                for ( DeployOption option : deployOptions )
//                {
//                    if ( option.isActive() )
//                    {
//                        return option;
//                    }
//                }
//                break;
//            case TEST:
//                List<TestOption> testOptions = getPluginDAO().getInfo( type.toString(), TestOption.class );
//                for ( TestOption option : testOptions )
//                {
//                    if ( option.isActive() )
//                    {
//                        return option;
//                    }
//                }
//                break;
//        }
//        return null;
//    }
//
//
//    @Override
//    public void updateOption( final Object option, final OperationType type )
//    {
//        saveOption( option, type );
//    }
//
//
//    @Override
//    public Object getOption( final String optionName, final OperationType type )
//    {
//        switch ( type )
//        {
//            case CLONE:
//                return getPluginDAO().getInfo( type.toString(), optionName, CloneOption.class );
//
//            case BUILD:
//                return getPluginDAO().getInfo( type.toString(), optionName, BuildOption.class );
//
//            case DEPLOY:
//                return getPluginDAO().getInfo( type.toString(), optionName, DeployOption.class );
//
//            case TEST:
//                return getPluginDAO().getInfo( type.toString(), optionName, TestOption.class );
//        }
//        return null;
//    }
//
//
//    @Override
//    public void deleteOption( final String optionName, final OperationType type )
//    {
//        getPluginDAO().deleteInfo( type.toString(), optionName );
//    }
//
//
//    @Override
//    public List<?> allOptionsByType( final OperationType type )
//    {
//        switch ( type )
//        {
//            case CLONE:
//                return getPluginDAO().getInfo( type.toString(), CloneOption.class );
//
//            case BUILD:
//                return getPluginDAO().getInfo( type.toString(), BuildOption.class );
//
//            case DEPLOY:
//                return getPluginDAO().getInfo( type.toString(), DeployOption.class );
//
//            case TEST:
//                return getPluginDAO().getInfo( type.toString(), TestOption.class );
//        }
//        return null;
//    }
//
//
//    @Override
//    public void setActive( final String optionName, final OperationType type )
//    {
//        switch ( type )
//        {
//            case CLONE:
//                CloneOption cloneOption = ( CloneOption ) getOption( optionName, type );
//                if ( !cloneOption.isActive() )
//                {
//                    cloneOption.setIsActive( true );
//                    saveOption( cloneOption, cloneOption.getType() );
//                }
//                break;
//            case BUILD:
//                BuildOption buildOption = ( BuildOption ) getOption( optionName, type );
//                if ( !buildOption.isActive() )
//                {
//                    buildOption.setIsActive( true );
//                    saveOption( buildOption, buildOption.getType() );
//                }
//                break;
//            case DEPLOY:
//                DeployOption deployOption = ( DeployOption ) getOption( optionName, type );
//                if ( !deployOption.isActive() )
//                {
//                    deployOption.setIsActive( true );
//                    saveOption( deployOption, deployOption.getType() );
//                }
//                break;
//            case TEST:
//                TestOption testOption = ( TestOption ) getOption( optionName, type );
//                if ( !testOption.isActive() )
//                {
//                    testOption.setIsActive( true );
//                    saveOption( testOption, testOption.getType() );
//                }
//                break;
//        }
//    }
//
//
//    @Override
//    public void deactivate( final String optionName, final OperationType type )
//    {
//        switch ( type )
//        {
//            case CLONE:
//                CloneOption cloneOption = ( CloneOption ) getOption( optionName, type );
//                if ( cloneOption.isActive() )
//                {
//                    cloneOption.setIsActive( false );
//                    saveOption( cloneOption, cloneOption.getType() );
//                }
//                break;
//            case BUILD:
//                BuildOption buildOption = ( BuildOption ) getOption( optionName, type );
//                if ( buildOption.isActive() )
//                {
//                    buildOption.setIsActive( false );
//                    saveOption( buildOption, buildOption.getType() );
//                }
//                break;
//            case DEPLOY:
//                DeployOption deployOption = ( DeployOption ) getOption( optionName, type );
//                if ( deployOption.isActive() )
//                {
//                    deployOption.setIsActive( false );
//                    saveOption( deployOption, deployOption.getType() );
//                }
//                break;
//            case TEST:
//                TestOption testOption = ( TestOption ) getOption( optionName, type );
//                if ( testOption.isActive() )
//                {
//                    testOption.setIsActive( false );
//                    saveOption( testOption, testOption.getType() );
//                }
//                break;
//        }
//    }
//
//
//    public Tracker getTracker()
//    {
//        return tracker;
//    }
//
//
//    public void setTracker( final Tracker tracker )
//    {
//        this.tracker = tracker;
//    }
//
//
//    public ExecutorService getExecutor()
//    {
//        return executor;
//    }
//
//
//    public void setExecutor( final ExecutorService executor )
//    {
//        this.executor = executor;
//    }
//
//
//    public EnvironmentManager getEnvironmentManager()
//    {
//        return environmentManager;
//    }
//
//
//    public void setEnvironmentManager( final EnvironmentManager environmentManager )
//    {
//        this.environmentManager = environmentManager;
//    }
//
//
//    public PluginDAO getPluginDAO()
//    {
//        return pluginDAO;
//    }
//
//
//    public void setPluginDAO( final PluginDAO pluginDAO )
//    {
//        this.pluginDAO = pluginDAO;
//    }
//
//
//    public PeerManager getPeerManager()
//    {
//        return peerManager;
//    }
//
//
//    public void setPeerManager( final PeerManager peerManager )
//    {
//        this.peerManager = peerManager;
//    }
//
//
//    public NetworkManager getNetworkManager()
//    {
//        return networkManager;
//    }
//
//
//    public void setNetworkManager( final NetworkManager networkManager )
//    {
//        this.networkManager = networkManager;
//    }
//}
