package io.subutai.plugin.keshigqd.api.entity.options;


import java.util.List;

import com.google.common.collect.Lists;

import io.subutai.plugin.keshigqd.api.entity.Command;


public class CloneOption
{
    private String name;
    private String url;
    private String branch;
    private String output;
    private boolean isActive;


    public CloneOption( final String name, final String url, final String branch, final String output,
                        final boolean isActive )
    {
        this.name = name;
        this.url = url;
        this.branch = branch;
        this.output = output;
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


    public String getOutput()
    {
        return output;
    }


    public void setOutput( final String output )
    {
        this.output = output;
    }


    public List<String> getArgs()
    {

        return Lists
                .newArrayList( Command.branchOpt, this.branch, Command.repoOpt, this.url, Command.ouput, this.output );
    }


    @Override
    public String toString()
    {
        return "CloneOptions{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", branch='" + branch + '\'' +
                ", output='" + output + '\'' +
                '}';
    }
}
