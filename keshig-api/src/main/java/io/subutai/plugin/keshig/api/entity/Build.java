package io.subutai.plugin.keshig.api.entity;


import java.util.Date;


public class Build implements Comparable<Build>
{
    private String id;
    private String name;
    private String version;
    private Date date;



    public String getId()
    {
        return id;
    }


    public void setId( final String id )
    {
        this.id = id;
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


    public Build( final String id, final String name, final String version, final Date date )
    {
        this.id = id;
        this.name = name;
        this.version = version;
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

        if ( id != null ? !id.equals( build.id ) : build.id != null )
        {
            return false;
        }
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
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + ( name != null ? name.hashCode() : 0 );
        result = 31 * result + ( version != null ? version.hashCode() : 0 );
        result = 31 * result + ( date != null ? date.hashCode() : 0 );
        return result;
    }


    @Override
    public String toString()
    {
        return "Build{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", date=" + date +
                '}';
    }


    @Override
    public int compareTo( final Build o )
    {
        return getDate().compareTo( o.getDate() );
    }
}
