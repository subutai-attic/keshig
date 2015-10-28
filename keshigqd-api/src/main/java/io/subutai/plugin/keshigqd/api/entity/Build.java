package io.subutai.plugin.keshigqd.api.entity;


import java.util.Date;


public class Build
{

    private String name;
    private String version;
    private Date date;


    public Build( final String name, final String version, final Date date )
    {
        this.name = name;
        this.version = version;
        this.date = date;
    }


    public String getName()
    {
        return name;
    }


    public void setName( final String name )
    {
        this.name = name;
    }


    public String getVersion()
    {
        return version;
    }


    public void setVersion( final String version )
    {
        this.version = version;
    }


    public Date getDate()
    {
        return date;
    }


    public void setDate( final Date date )
    {
        this.date = date;
    }


    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Build ) )
        {
            return false;
        }

        final Build build = ( Build ) o;

        if ( name != null ? !name.equals( build.name ) : build.name != null )
        {
            return false;
        }
        if ( version != null ? !version.equals( build.version ) : build.version != null )
        {
            return false;
        }
        return !( date != null ? !date.equals( build.date ) : build.date != null );
    }


    @Override
    public int hashCode()
    {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + ( version != null ? version.hashCode() : 0 );
        result = 31 * result + ( date != null ? date.hashCode() : 0 );
        return result;
    }


    @Override
    public String toString()
    {
        return "Build{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", date=" + date +
                '}';
    }
}
