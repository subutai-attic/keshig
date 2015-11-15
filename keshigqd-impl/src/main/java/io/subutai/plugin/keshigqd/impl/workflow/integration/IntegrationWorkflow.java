package io.subutai.plugin.keshigqd.impl.workflow.integration;


import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.LoggerFactory;

import org.apache.servicemix.beanflow.Workflow;

import io.subutai.common.command.RequestBuilder;
import io.subutai.common.tracker.TrackerOperation;
import io.subutai.plugin.keshigqd.api.KeshigQDConfig;
import io.subutai.plugin.keshigqd.api.entity.Build;
import io.subutai.plugin.keshigqd.api.entity.Command;
import io.subutai.plugin.keshigqd.api.entity.OperationType;
import io.subutai.plugin.keshigqd.api.entity.Server;
import io.subutai.plugin.keshigqd.api.entity.ServerType;
import io.subutai.plugin.keshigqd.api.entity.options.BuildOption;
import io.subutai.plugin.keshigqd.api.entity.options.CloneOption;
import io.subutai.plugin.keshigqd.api.entity.options.DeployOption;
import io.subutai.plugin.keshigqd.api.entity.options.TestOption;
import io.subutai.plugin.keshigqd.impl.KeshigQDImpl;


public class IntegrationWorkflow extends Workflow<IntegrationWorkflow.IntegrationWorkflowPhase>
{

    private final KeshigQDImpl keshigQD;
    private final TrackerOperation operationTracker;

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger( IntegrationWorkflow.class );

    private CloneOption cloneOption;
    private BuildOption buildOption;
    private DeployOption deployOption;
    private TestOption testOption;

    private Server cloneServer;
    private Server deployServer;
    private Server testServer;


    public IntegrationWorkflow( final KeshigQDImpl keshigQD )
    {
        super( IntegrationWorkflowPhase.INIT );
        this.keshigQD = keshigQD;
        this.operationTracker = keshigQD.getTracker().createTrackerOperation( KeshigQDConfig.PRODUCT_KEY,
                String.format( "Creating %s tracker object for Integration Workflow", KeshigQDConfig.PRODUCT_KEY ) );

        cloneOption = ( CloneOption ) keshigQD.getActiveOption( OperationType.CLONE );
        buildOption = ( BuildOption ) keshigQD.getActiveOption( OperationType.BUILD );
        deployOption = ( DeployOption ) keshigQD.getActiveOption( OperationType.DEPLOY );
        testOption = ( TestOption ) keshigQD.getActiveOption( OperationType.TEST );
    }


    public enum IntegrationWorkflowPhase
    {
        INIT,
        CLONE,
        BUILD,
        DEPLOY,
        TEST,
        FINALIZE
    }


    public IntegrationWorkflowPhase INIT()
    {
        operationTracker.addLog( "Starting Keshig Integration workflow\n" );


        if ( cloneOption == null )
        {
            operationTracker.addLogFailed( "Could not fetch Clone Option info" );
            return null;
        }
        operationTracker.addLog( String.format( "%s:%s\n", cloneOption.getType().toString(), cloneOption.toString() ) );

        if ( buildOption == null )
        {
            operationTracker.addLogFailed( "Could not fetch Build Option info" );
            return null;
        }
        operationTracker.addLog( String.format( "%s%s\n", buildOption.getType().toString(), buildOption.toString() ) );



        if ( deployOption == null )
        {
            operationTracker.addLogFailed( "Could not fetch Deploy Option info" );
            return null;
        }
        operationTracker.addLog( String.format( "%s%s\n", deployOption.getType().toString(), deployOption.toString() ) );



        if ( buildOption == null )
        {
            operationTracker.addLogFailed( "Could not fetch Build Option info" );
            return null;
        }
        operationTracker.addLog( String.format( "%s%s\n", deployOption.getType().toString(), deployOption.toString() ) );

        cloneServer = keshigQD.getServers( ServerType.BUILD_SERVER ).get( 0 );
        if ( cloneServer == null )
        {
            operationTracker.addLogFailed( "Could not fetch Clone Server info" );
            return null;
        }
        operationTracker.addLog( String.format( "Using:%s\nServer Details:%s", ServerType.BUILD_SERVER.toString(),
                cloneServer.toString() ) );

        deployServer = keshigQD.getServers( ServerType.DEPLOY_SERVER ).get( 0 );
        if ( deployServer == null )
        {
            operationTracker.addLogFailed( "Could not fetch Deploy Server info" );
            return null;
        }
        operationTracker.addLog( String.format( "Using:%s\nServer Details:%s", ServerType.DEPLOY_SERVER.toString(),
                cloneServer.toString() ) );

        testServer = keshigQD.getServers( ServerType.TEST_SERVER ).get( 0 );
        if ( testServer == null )
        {
            operationTracker.addLogFailed( "Could not fetch Test Server info" );
            return null;
        }
        operationTracker.addLog( String.format( "Using:%s\nServer Details:%s", ServerType.TEST_SERVER.toString(),
                cloneServer.toString() ) );

        return IntegrationWorkflowPhase.CLONE;
    }


    public IntegrationWorkflowPhase CLONE()
    {

        UUID tracker = keshigQD.clone(
                new RequestBuilder( Command.getCloneCommand() ).withCmdArgs( cloneOption.getArgs() )
                                                               .withTimeout( cloneOption.getTimeOut() ),
                cloneServer.getServerId() );

        if ( keshigQD.getTracker().getTrackerOperation( KeshigQDConfig.PRODUCT_KEY, tracker ).getState().toString()
                     .equalsIgnoreCase( "FAILED" ) )
        {
            return null;
        }
        return IntegrationWorkflowPhase.BUILD;
    }


    public IntegrationWorkflowPhase BUILD()
    {
        UUID tracker = keshigQD.build(
                new RequestBuilder( Command.getBuildCommand() ).withCmdArgs( buildOption.getArgs() )
                                                               .withTimeout( buildOption.getTimeOut() ),
                cloneServer.getServerId() );

        if ( keshigQD.getTracker().getTrackerOperation( KeshigQDConfig.PRODUCT_KEY, tracker ).getState().toString()
                     .equalsIgnoreCase( "FAILED" ) )
        {
            return null;
        }
        return IntegrationWorkflowPhase.DEPLOY;
    }


    public IntegrationWorkflowPhase DEPLOY()
    {

        if ( deployOption.getBuildName().equalsIgnoreCase( "latest" ) )
        {
            List<Build> builds = keshigQD.getBuilds();
            Collections.sort( builds );
            deployOption.setBuildName( keshigQD.getLatestBuild().getId() );
        }
        UUID tracker = keshigQD.deploy(
                new RequestBuilder( Command.getDeployCommand() ).withCmdArgs( deployOption.getArgs() )
                                                                .withTimeout( deployOption.getTimeOut() )
                                                                .withRunAs( "ubuntu" ), deployServer.getServerId() );

        if ( keshigQD.getTracker().getTrackerOperation( KeshigQDConfig.PRODUCT_KEY, tracker ).getState().toString()
                     .equalsIgnoreCase( "FAILED" ) )
        {
            return null;
        }
        else if ( keshigQD.getTracker().getTrackerOperation( KeshigQDConfig.PRODUCT_KEY, tracker ).getState().toString()
                          .equalsIgnoreCase( "SUCCEEDED" ) )
        {
            extractServers( keshigQD.getTracker().getTrackerOperation( KeshigQDConfig.PRODUCT_KEY, tracker ).getLog(),
                    deployOption.getBuildName() );
        }
        return IntegrationWorkflowPhase.TEST;
    }


    public IntegrationWorkflowPhase TEST()
    {

        if ( testOption.getTargetIps().toString().contains( "latest" ) )
        {
            testOption.getTargetIps().clear();
            List<Server> serverList = keshigQD.getServers( ServerType.PEER_SERVER );
            for ( Server server : serverList )
            {
                if ( server.getDescription().contains( keshigQD.getLatestBuild().getId() ) )
                {

                    testOption.getTargetIps().add( server.getServerAddress() );
                }
            }
        }
        UUID tracker = keshigQD.test( new RequestBuilder( Command.getTestComand() ).withCmdArgs( testOption.getArgs() )
                                                                                   .withTimeout(
                                                                                           testOption.getTimeOut() ),
                testServer.getServerId() );
        if ( keshigQD.getTracker().getTrackerOperation( KeshigQDConfig.PRODUCT_KEY, tracker ).getState().toString()
                     .equalsIgnoreCase( "FAILED" ) )
        {
            return null;
        }
        return IntegrationWorkflowPhase.FINALIZE;
    }


    public void FINALIZE()
    {
        stop();
    }


    private void extractServers( String stdOut, String buildName )
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
                    String.format( "Auto-deployed Subutai Management Server:%s", buildName ) );

            LOG.info( String.format( "Saving server info: %s", server.toString() ) );

            try
            {
                keshigQD.addServer( server );
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
}

