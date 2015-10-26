package io.subutai.plugin.keshigqd.api.entity;


public class Server
{
    private String serverId;

    private String serverName;

    private String serverAddress;

    private ServerType type;


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


    public String getServerAddress()
    {
        return serverAddress;
    }


    public void setServerAddress( final String serverAddress )
    {
        this.serverAddress = serverAddress;
    }


    public ServerType getType()
    {
        return type;
    }


    public void setType( final ServerType type )
    {
        this.type = type;
    }


    public Server( final String serverId, final String serverName, final String serverAddress, final ServerType type )
    {
        this.serverId = serverId;
        this.serverName = serverName;
        this.serverAddress = serverAddress;
        this.type = type;
    }


    @Override
    public String toString()
    {
        return "Server{" +
                "serverName='" + serverName + '\'' +
                ", serverAddress='" + serverAddress + '\'' +
                ", type=" + type +
                '}';
    }
}
