package io.subutai.plugin.keshigqd.api.entity.options;


import java.util.List;

import io.subutai.plugin.keshigqd.api.entity.OperationType;


public class BuildOption
{

    private boolean cleanInstall;
    private boolean runTests;
    private String name;
    private boolean isActive;
    private int timeOut;


    private OperationType type = OperationType.BUILD;


    public BuildOption()
    {
    }


    public BuildOption( final boolean cleanInstall, final boolean runTests, final String name, final boolean isActive )
    {
        this.cleanInstall = cleanInstall;
        this.runTests = runTests;
        this.name = name;
        this.isActive = isActive;
    }


    public int getTimeOut()
    {
        return timeOut;
    }

    public void setTimeOut( final int timeOut )
    {
        this.timeOut = timeOut;
    }


    public boolean isActive()
    {
        return isActive;
    }


    public void setIsActive( final boolean isActive )
    {
        this.isActive = isActive;
    }


    public boolean isCleanInstall()
    {
        return cleanInstall;
    }


    public void setCleanInstall( final boolean cleanInstall )
    {
        this.cleanInstall = cleanInstall;
    }


    public boolean isRunTests()
    {
        return runTests;
    }


    public void setRunTests( final boolean runTests )
    {
        this.runTests = runTests;
    }


    public String getName()
    {
        return name;
    }



    public void setName(String name)
    {
        this.name = name;
    }



    public List<String> getArgs()
    {
        return null;
    }



    public OperationType getType()
    {
        return type;
    }


    @Override
    public String toString()
    {
        return "BuildOptions{" +
                "cleanInstall=" + cleanInstall +
                ", runTests=" + runTests +
                ", name='" + name + '\'' +
                '}';
    }
}
