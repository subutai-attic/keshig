package io.subutai.plugin.keshig.api.entity;


import java.util.Date;
import java.util.Map;


public class KeshigServer
{

    private String hostname;
    private Map<String, PeerInfo> peers;
    private Date lastUpdated;

    public KeshigServer()
    {
    }


    public KeshigServer( final String hostname )
    {
        this.hostname = hostname;
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
