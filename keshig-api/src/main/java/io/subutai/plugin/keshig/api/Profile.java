package io.subutai.plugin.keshig.api;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile {

    private String name;

    private String deployOption;
    private String testOption;

    private String deployServer;
    private String testServer;

    public Profile() {
    }


    public Profile( final String name, final String deployOption, final String testOption, final String deployServer,
                    final String testServer )
    {
        this.name = name;
        this.deployOption = deployOption;
        this.testOption = testOption;
        this.deployServer = deployServer;
        this.testServer = testServer;
    }


    public String getName()
    {
        return name;
    }


    public void setName( final String name )
    {
        this.name = name;
    }


    public String getDeployOption()
    {
        return deployOption;
    }


    public void setDeployOption( final String deployOption )
    {
        this.deployOption = deployOption;
    }


    public String getTestOption()
    {
        return testOption;
    }


    public void setTestOption( final String testOption )
    {
        this.testOption = testOption;
    }


    public String getDeployServer()
    {
        return deployServer;
    }


    public void setDeployServer( final String deployServer )
    {
        this.deployServer = deployServer;
    }


    public String getTestServer()
    {
        return testServer;
    }


    public void setTestServer( final String testServer )
    {
        this.testServer = testServer;
    }


    @Override
    public String toString()
    {
        return "Profile{" +
                "name='" + name + '\'' +
                ", deployOption='" + deployOption + '\'' +
                ", testOption='" + testOption + '\'' +
                ", deployServer='" + deployServer + '\'' +
                ", testServer='" + testServer + '\'' +
                '}';
    }
}
