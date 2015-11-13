package io.subutai.plugin.keshigqd.impl.workflow.integration;


import org.apache.servicemix.beanflow.Workflow;


public class IntegrationWorkflow extends Workflow<IntegrationWorkflow.IntegrationWorkflowPhase>
{

    public IntegrationWorkflow()
    {
        super( IntegrationWorkflowPhase.INIT );
    }


    public enum IntegrationWorkflowPhase
    {
        INIT
    }
}

