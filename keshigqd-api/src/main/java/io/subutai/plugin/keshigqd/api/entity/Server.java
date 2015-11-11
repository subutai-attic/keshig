package io.subutai.plugin.keshigqd.api.entity;


public class Server
{
    private String serverId;

    private String serverName;

    private String serverAddress;

    private String type;

    private String description;


    public Server( final String serverId, final String serverName, final String serverAddress, final String type,
                   final String description )
    {
        this.serverId = serverId;
        this.serverName = serverName;
        this.serverAddress = serverAddress;
        this.type = type;
        this.description = description;
    }


    public String getDescription()
    {
        return description;
    }


    public void setDescription( final String description )
    {
        this.description = description;
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


    public String getServerAddress()
    {
        return serverAddress;
    }


    public void setServerAddress( final String serverAddress )
    {
        this.serverAddress = serverAddress;
    }


    public String getType()
    {
        return type;
    }


    public void setType( final String type )
    {
        this.type = type;
    }


    @Override
    public String toString()
    {
        return "Server{" +
                "serverId='" + serverId + '\'' +
                ", serverName='" + serverName + '\'' +
                ", serverAddress='" + serverAddress + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
