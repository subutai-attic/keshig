package io.subutai.plugin.keshig.api.entity;


import java.util.Date;
import java.util.Map;


public class KeshigServer
{

    private String hostname;
    private String serverId;
    private Map<String, PeerInfo> peers;
    private Date lastUpdated;

    private boolean nightlyBuild = true;

    public KeshigServer()
    {
    }


    public String getServerId()
    {
        return serverId;
    }


    public void setServerId( final String serverId )
    {
        this.serverId = serverId;
    }


    public KeshigServer( final String hostname )
    {
        this.hostname = hostname;
    }


    public boolean isNightlyBuild()
    {
        return nightlyBuild;
    }


    public void setNightlyBuild( final boolean nightlyBuild )
    {
        this.nightlyBuild = nightlyBuild;
    }


    public Map<String, PeerInfo> getPeers()
    {
        return peers;
    }

    public void setPeers( final Map<String, PeerInfo> peers )
    {
        this.peers = peers;
    }


    public Date getLastUpdated()
    {
        return lastUpdated;
    }


    public void setLastUpdated( final Date lastUpdated )
    {
        this.lastUpdated = lastUpdated;
    }


    public String getHostname()
    {
        return hostname;
    }


    public void setHostname( final String hostname )
    {
        this.hostname = hostname;
    }
}
