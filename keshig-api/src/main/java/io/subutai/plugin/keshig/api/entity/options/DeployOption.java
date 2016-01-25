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
    private int timeOut = 1000;


    public DeployOption()
    {
    }


    @JsonIgnore
    public String getType()
    {
        return "DEPLOY";
    }


    @JsonIgnore
    public int getTimeOut()
    {
        return timeOut;
    }


    public void setTimeOut( final int timeOut )
    {
        this.timeOut = timeOut;
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
