package io.subutai.plugin.keshigqd.api.entity.options;


import java.util.List;

import com.google.common.collect.Lists;

import io.subutai.plugin.keshigqd.api.entity.OperationType;


public class DeployOption
{
    private String name;
    private int numberOfPeers;
    private int numberOfRhsPerPeer;

    private String buildName;

    private boolean isActive;
    private OperationType type = OperationType.DEPLOY;
    private int timeOut;

    public DeployOption()
    {
    }


    public DeployOption( final String name, final int numberOfPeers, final int numberOfRhsPerPeer,
                         final String buildName, final boolean isActive )
    {
        this.name = name;
        this.numberOfPeers = numberOfPeers;
        this.numberOfRhsPerPeer = numberOfRhsPerPeer;
        this.buildName = buildName;
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


    public List<String> getArgs()
    {
        return Lists.newArrayList( io.subutai.plugin.keshigqd.api.entity.Command.folder, buildName);
    }


    public void setName( final String name )
    {
        this.name = name;
    }


    public int getNumberOfPeers()
    {
        return numberOfPeers;
    }


    public void setNumberOfPeers( final int numberOfPeers )
    {
        this.numberOfPeers = numberOfPeers;
    }


    public int getNumberOfRhsPerPeer()
    {
        return numberOfRhsPerPeer;
    }


    public void setNumberOfRhsPerPeer( final int numberOfRhsPerPeer )
    {
        this.numberOfRhsPerPeer = numberOfRhsPerPeer;
    }


    public String getBuildName()
    {
        return buildName;
    }


    public void setBuildName( final String buildName )
    {
        this.buildName = buildName;
    }


    @Override
    public String toString()
    {
        return "DeployOption{" +
                "name='" + name + '\'' +
                ", numberOfPeers=" + numberOfPeers +
                ", numberOfRhsPerPeer=" + numberOfRhsPerPeer +
                ", buildName='" + buildName + '\'' +
                ", isActive=" + isActive +
                ", type=" + type +
                ", timeOut=" + timeOut +
                '}';
    }
}
