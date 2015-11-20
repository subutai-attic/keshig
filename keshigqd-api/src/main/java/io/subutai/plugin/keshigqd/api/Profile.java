package io.subutai.plugin.keshigqd.api;


import java.util.List;

import io.subutai.plugin.keshigqd.api.entity.Server;
import io.subutai.plugin.keshigqd.api.entity.options.BuildOption;
import io.subutai.plugin.keshigqd.api.entity.options.CloneOption;
import io.subutai.plugin.keshigqd.api.entity.options.DeployOption;
import io.subutai.plugin.keshigqd.api.entity.options.TestOption;


public class Profile
{
    private String name;

    private CloneOption cloneOption;
    private BuildOption buildOption;
    private DeployOption deployOption;

    private TestOption testOption;
    private List<Server> serverList;


    public String getName()
    {
        return name;
    }


    public void setName( final String name )
    {
        this.name = name;
    }


    public CloneOption getCloneOption()
    {
        return cloneOption;
    }


    public void setCloneOption( final CloneOption cloneOption )
    {
        this.cloneOption = cloneOption;
    }


    public BuildOption getBuildOption()
    {
        return buildOption;
    }


    public void setBuildOption( final BuildOption buildOption )
    {
        this.buildOption = buildOption;
    }


    public DeployOption getDeployOption()
    {
        return deployOption;
    }


    public void setDeployOption( final DeployOption deployOption )
    {
        this.deployOption = deployOption;
    }


    public TestOption getTestOption()
    {
        return testOption;
    }


    public void setTestOption( final TestOption testOption )
    {
        this.testOption = testOption;
    }


    public List<Server> getServerList()
    {
        return serverList;
    }


    public void setServerList( final List<Server> serverList )
    {
        this.serverList = serverList;
    }
}
