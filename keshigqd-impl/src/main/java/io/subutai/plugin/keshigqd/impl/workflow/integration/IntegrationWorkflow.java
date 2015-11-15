package io.subutai.plugin.keshigqd.impl.workflow.integration;


import org.apache.servicemix.beanflow.Workflow;

import io.subutai.common.tracker.TrackerOperation;
import io.subutai.plugin.keshigqd.api.entity.OperationType;
import io.subutai.plugin.keshigqd.api.entity.Server;
import io.subutai.plugin.keshigqd.api.entity.options.BuildOption;
import io.subutai.plugin.keshigqd.api.entity.options.CloneOption;
import io.subutai.plugin.keshigqd.api.entity.options.DeployOption;
import io.subutai.plugin.keshigqd.api.entity.options.TestOption;
import io.subutai.plugin.keshigqd.impl.KeshigQDImpl;
import io.subutai.plugin.keshigqd.impl.handler.OptionsHandlerImpl;


public class IntegrationWorkflow extends Workflow<IntegrationWorkflow.IntegrationWorkflowPhase>
{

    private final KeshigQDImpl keshigQD;
    private final TrackerOperation operationTracker;

    private CloneOption cloneOption;
    private BuildOption buildOption;
    private DeployOption deployOption;
    private TestOption testOption;

    private Server cloneServer;
    private Server deployServer;
    private Server testServer;

    private OptionsHandlerImpl optionsHandler;



    public IntegrationWorkflow( final KeshigQDImpl keshigQD, final TrackerOperation operationTracker )
    {
        super( IntegrationWorkflowPhase.INIT );
        this.keshigQD = keshigQD;
        this.operationTracker = operationTracker;
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

        optionsHandler = new OptionsHandlerImpl( keshigQD );

        cloneOption = ( CloneOption ) optionsHandler.getActiveOption( OperationType.CLONE );
        buildOption = ( BuildOption ) optionsHandler.getActiveOption( OperationType.BUILD );
        deployOption = ( DeployOption ) optionsHandler.getActiveOption( OperationType.DEPLOY );
        testOption = ( TestOption ) optionsHandler.getActiveOption( OperationType.TEST );


        return IntegrationWorkflowPhase.CLONE;
    }


    public IntegrationWorkflowPhase CLONE()
    {
        return IntegrationWorkflowPhase.BUILD;
    }


    public IntegrationWorkflowPhase BUILD()
    {
        return IntegrationWorkflowPhase.DEPLOY;
    }


    public IntegrationWorkflowPhase DEPLOY()
    {

        return IntegrationWorkflowPhase.TEST;
    }


    public IntegrationWorkflowPhase TEST()
    {

        return IntegrationWorkflowPhase.FINALIZE;
    }


    public void FINALIZE()
    {
        stop();
    }
}

