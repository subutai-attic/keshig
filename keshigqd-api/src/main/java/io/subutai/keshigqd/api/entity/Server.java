package io.subutai.keshigqd.api.entity;


public class Server
{

    private String serverName;

    private String serverAddress;

    private ServerType type;


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
