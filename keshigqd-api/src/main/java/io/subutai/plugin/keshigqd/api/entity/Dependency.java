package io.subutai.plugin.keshigqd.api.entity;


import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Dependency
{
    private String name;
    private String version;
    private String arch;
    private String description;


    public Dependency( final String name, final String version, final String arch, final String description )
    {
        this.name = name;
        this.version = version;
        this.arch = arch;
        this.description = description;
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


    public String getArch()
    {
        return arch;
    }


    public void setArch( final String arch )
    {
        this.arch = arch;
    }


    public String getDescription()
    {
        return description;
    }


    public void setDescription( final String description )
    {
        this.description = description;
    }


    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Dependency ) )
        {
            return false;
        }

        final Dependency aDependency = ( Dependency ) o;

        if ( name != null ? !name.equals( aDependency.name ) : aDependency.name != null )
        {
            return false;
        }
        if ( version != null )
        {
            Pattern pattern = Pattern.compile( version );
            Matcher matcher = pattern.matcher( aDependency.version );
            return matcher.matches();
        }
        return false;
    }


    @Override
    public int hashCode()
    {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + ( version != null ? version.hashCode() : 0 );
        result = 31 * result + ( arch != null ? arch.hashCode() : 0 );
        result = 31 * result + ( description != null ? description.hashCode() : 0 );
        return result;
    }


    @Override
    public String toString()
    {
        return "Package{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", arch='" + arch + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
