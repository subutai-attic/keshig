package io.subutai.plugin.keshigqd.api.entity.options;


public class DeployOption
{
    private String name;
    private int numberOfPeers;
    private int numberOfRhsPerPeer;
    private String buildName;
    private boolean isActive;


    public DeployOption( final String name, final int numberOfPeers, final int numberOfRhsPerPeer,
                         final String buildName, final boolean isActive )
    {
        this.name = name;
        this.numberOfPeers = numberOfPeers;
        this.numberOfRhsPerPeer = numberOfRhsPerPeer;
        this.buildName = buildName;
        this.isActive = isActive;
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
        return "DeployOptions{" +
                "name='" + name + '\'' +
                ", numberOfPeers=" + numberOfPeers +
                ", numberOfRhsPerPeer=" + numberOfRhsPerPeer +
                ", buildName='" + buildName + '\'' +
                '}';
    }
}
