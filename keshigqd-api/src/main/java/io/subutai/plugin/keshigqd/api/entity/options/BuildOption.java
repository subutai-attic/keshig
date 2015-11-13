package io.subutai.plugin.keshigqd.api.entity.options;


public class BuildOption
{

    private boolean cleanInstall;
    private boolean runTests;
    private String name;
    private boolean isActive;


    public BuildOption( final boolean cleanInstall, final boolean runTests, final String name, final boolean isActive )
    {
        this.cleanInstall = cleanInstall;
        this.runTests = runTests;
        this.name = name;
        this.isActive = isActive;
    }


    public boolean isActive()
    {
        return isActive;
    }


    public void setIsActive( final boolean isActive )
    {
        this.isActive = isActive;
    }


    public boolean isCleanInstall()
    {
        return cleanInstall;
    }


    public void setCleanInstall( final boolean cleanInstall )
    {
        this.cleanInstall = cleanInstall;
    }


    public boolean isRunTests()
    {
        return runTests;
    }


    public void setRunTests( final boolean runTests )
    {
        this.runTests = runTests;
    }


    public String getName()
    {
        return name;
    }


    public void setName( final String name )
    {
        this.name = name;
    }


    @Override
    public String toString()
    {
        return "BuildOptions{" +
                "cleanInstall=" + cleanInstall +
                ", runTests=" + runTests +
                ", name='" + name + '\'' +
                '}';
    }
}
