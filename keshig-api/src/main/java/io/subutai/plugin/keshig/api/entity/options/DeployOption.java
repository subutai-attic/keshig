package io.subutai.plugin.keshig.api.entity.options;


import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;


import com.google.common.collect.Lists;




@JsonIgnoreProperties( ignoreUnknown = true )
public class DeployOption implements Option
{


    private String name;
    private String branch;
    @JsonIgnore
    private String runAs = "ubuntu";
    @JsonIgnore
    private int timeout = 3000;


    public DeployOption()
    {
    }


    public DeployOption( final String name, final String branch )
    {
        this.name = name;
        this.branch = branch;
    }


    @JsonIgnore
    public String getType()
    {
        return "DEPLOY";
    }


    @JsonIgnore
    public String getRunAs()
    {
        return runAs;
    }


    @JsonIgnore
    public int getTimeOut()
    {
        return timeout;
    }


    public void setTimeOut( final int timeOut )
    {
        this.timeout = timeOut;
    }


    public String getName()
    {
        return name;
    }


    public void setName( final String name )
    {
        this.name = name;
    }


    public String getBranch()
    {
        return branch;
    }


    public void setBranch( final String branch )
    {
        this.branch = branch;
    }


    @JsonIgnore
    public String getCommand()
    {
        return "keshig";
    }


    @JsonIgnore
    public List<String> getArgs()
    {
        return Lists.newArrayList( this.branch);
    }
}
