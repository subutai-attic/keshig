package io.subutai.plugin.keshigqd.impl.workflow.installation;


import org.apache.servicemix.beanflow.Workflow;


public class InstallDependenciesWorkflow extends Workflow<InstallDependenciesWorkflow.Phase>
{
    public InstallDependenciesWorkflow()
    {
        super( Phase.INIT );
    }


    public enum Phase
    {
        INIT,
        FETCH_DEPENDENCIES,
        INSTALL,
        FINALIZE
    }
}
