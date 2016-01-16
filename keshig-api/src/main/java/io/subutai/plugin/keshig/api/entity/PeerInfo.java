package io.subutai.plugin.keshig.api.entity;


import java.util.HashMap;


public class PeerInfo
{

    private String ip;
    private String status;
    private boolean isFree;
    private HashMap details;
    private String usedBy;


    public PeerInfo()
    {
    }


    public PeerInfo( final String ip, final String status, final boolean isFree, final HashMap details )
    {
        this.ip = ip;
        this.status = status;
        this.isFree = isFree;
        this.details = details;
    }


    public String getUsedBy()
    {
        return usedBy;
    }


    public void setUsedBy( final String usedBy )
    {
        this.usedBy = usedBy;
    }


    public String getIp()
    {
        return ip;
    }


    public void setIp( final String ip )
    {
        this.ip = ip;
    }


    public String getStatus()
    {
        return status;
    }


    public void setStatus( final String status )
    {
        this.status = status;
    }


    public boolean isFree()
    {
        return isFree;
    }


    public void setFree( final boolean free )
    {
        isFree = free;
    }


    public HashMap getDetails()
    {
        return details;
    }


    public void setDetails( final HashMap details )
    {
        this.details = details;
    }


    @Override
    public String toString()
    {
        return "PeerInfo{" +
                "ip='" + ip + '\'' +
                ", status='" + status + '\'' +
                ", isFree=" + isFree +
                ", details=" + details +
                '}';
    }
}
