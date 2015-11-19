package io.subutai.plugin.keshigqd.impl.workflow.installation;


public class InstallDependenciesWorkflow {

    public InstallDependenciesWorkflow() {

    }


    public enum Phase {
        INIT,
        FETCH_DEPENDENCIES,
        INSTALL,
        FINALIZE
    }
}
