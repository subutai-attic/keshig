package io.subutai.plugin.keshigqd.api.entity.options;


import java.util.List;

import com.google.common.collect.Lists;

import io.subutai.plugin.keshigqd.api.entity.OperationType;


public class TestOption
{
    private String name;
    private boolean all;
    private List<String> targetIps;
    private List<String> playbooks;
    private boolean isActive;
    private int timeOut;

    private OperationType type = OperationType.TEST;


    public TestOption()
    {

    }


    public TestOption( final String name, final boolean all, final List<String> targetIps, final boolean isActive )
    {
        this.name = name;
        this.all = all;
        this.targetIps = targetIps;
        this.isActive = isActive;
    }


    public TestOption( final List<String> targetIps, final List<String> playbooks, final boolean isActive,
                       final String name )
    {
        this.targetIps = targetIps;
        this.playbooks = playbooks;
        this.isActive = isActive;
        this.name = name;
    }


    public int getTimeOut()
    {
        return timeOut;
    }


    public void setTimeOut( final int timeOut )
    {
        this.timeOut = timeOut;
    }



    public OperationType getType()
    {
        return type;
    }


    public boolean isActive()
    {
        return isActive;
    }


    public void setIsActive( final boolean isActive )
    {
        this.isActive = isActive;
    }


    public String getName()
    {
        return name;
    }


    public void setName( final String name )
    {
        this.name = name;
    }


    public boolean isAll()
    {
        return all;
    }


    public void setAll( final boolean all )
    {
        this.all = all;
    }


    public List<String> getTargetIps()
    {
        return targetIps;
    }


    public void setTargetIps( final List<String> targetIps )
    {
        this.targetIps = targetIps;
    }


    public List<String> getPlaybooks()
    {
        return playbooks;
    }


    public void setPlaybooks( final List<String> playbooks )
    {
        this.playbooks = playbooks;
    }


    public List<String> getArgs()
    {
        if ( all )
        {
            return Lists.newArrayList( String.join( " ", targetIps ), "general_playbooks/*.story" );
        }
        return Lists.newArrayList( String.join( " ", targetIps ), String.join( " ", playbooks ) );
    }


    @Override
    public String toString()
    {
        return "TestOption{" +
                "name='" + name + '\'' +
                ", all=" + all +
                ", targetIps=" + targetIps +
                ", playbooks=" + playbooks +
                ", isActive=" + isActive +
                ", timeOut=" + timeOut +
                ", type=" + type +
                '}';
    }
}
