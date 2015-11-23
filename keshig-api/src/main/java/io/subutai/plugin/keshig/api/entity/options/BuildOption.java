package io.subutai.plugin.keshig.api.entity.options;


import java.util.List;

import com.google.common.collect.Lists;

import io.subutai.plugin.keshig.api.entity.Command;
import io.subutai.plugin.keshig.api.entity.OperationType;


public class BuildOption
{

    private boolean cleanInstall;
    private boolean runTests;
    private int timeOut;
    private String name;
    private boolean active;




    private OperationType type = OperationType.BUILD;


    public BuildOption()
    {
    }


    public BuildOption( final boolean cleanInstall, final boolean runTests, final String name, final boolean isActive )
    {
        this.cleanInstall = cleanInstall;
        this.runTests = runTests;
        this.name = name;
        this.active = isActive;
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
        return active;
    }


    public void setIsActive( final boolean isActive )
    {
        this.active = isActive;
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


    public void setName( String name )
    {
        this.name = name;
    }


    public List<String> getArgs()
    {
        return Lists.newArrayList( Command.tests, String.valueOf( isRunTests() ),
                Command.clean, String.valueOf( isCleanInstall() ) );
    }


    public OperationType getType()
    {
        return type;
    }


    @Override
    public String toString()
    {
        return "BuildOption{" +
                "cleanInstall=" + cleanInstall +
                ", runTests=" + runTests +
                ", name='" + name + '\'' +
                ", isActive=" + active +
                ", timeOut=" + timeOut +
                ", type=" + type +
                '}';
    }
}
