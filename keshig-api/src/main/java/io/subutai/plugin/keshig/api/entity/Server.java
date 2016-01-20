package io.subutai.plugin.keshig.api.entity;


public class Server
{
    private String serverId;
    private String serverName;


    public Server( final String serverId, final String serverName )
    {
        this.serverId = serverId;
        this.serverName = serverName;
    }


    public Server()
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


    public String getServerName()
    {
        return serverName;
    }


    public void setServerName( final String serverName )
    {
        this.serverName = serverName;
    }


    @Override
    public String toString()
    {
        return "Server{" +
                "serverId='" + serverId + '\'' +
                ", serverName='" + serverName + '\'' +
                '}';
    }
}
