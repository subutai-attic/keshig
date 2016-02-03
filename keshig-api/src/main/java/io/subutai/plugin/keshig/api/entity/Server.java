package io.subutai.plugin.keshig.api.entity;


public class Server
{
    private String serverId;
    private String serverName;


    public boolean isAdded()
    {
        return added;
    }


    public void setAdded( final boolean added )
    {
        this.added = added;
    }

    private boolean added;

    public Server( final String serverId, final String serverName )
    {
        this.serverId = serverId;
        this.serverName = serverName;
    }


    public Server( final String serverId, final String serverName, final boolean added )
    {
        this.serverId = serverId;
        this.serverName = serverName;
        this.added = added;
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


    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Server ) )
        {
            return false;
        }

        final Server server = ( Server ) o;

        if ( !serverId.equals( server.serverId ) )
        {
            return false;
        }
        return serverName.equals( server.serverName );
    }


    @Override
    public int hashCode()
    {
        int result = serverId != null ? serverId.hashCode() : 0;
        result = 31 * result + ( serverName != null ? serverName.hashCode() : 0 );
        result = 31 * result + ( added ? 1 : 0 );
        return result;
    }
}
