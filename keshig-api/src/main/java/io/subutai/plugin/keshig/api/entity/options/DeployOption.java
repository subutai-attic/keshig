package io.subutai.plugin.keshig.api.entity.options;


import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import io.subutai.plugin.keshig.api.entity.Command;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeployOption implements Option
{


    private String name;
    private String url;
    private String branch;

    private String type = "DEPLOY";

    @JsonIgnore
    private int timeOut = 1000;


    public DeployOption()
    {
    }

    @JsonIgnore
    public String getType()
    {
        return this.type;
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


    public String getUrl()
    {
        return url;
    }


    public void setUrl( final String url )
    {
        this.url = url;
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
        return null;
    }

    @JsonIgnore
    public List<String> getArgs()
    {

        return Lists.newArrayList( Command.branchOpt, this.branch, Command.repoOpt, this.url );
    }
}
