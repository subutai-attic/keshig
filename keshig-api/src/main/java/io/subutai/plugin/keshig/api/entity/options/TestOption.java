package io.subutai.plugin.keshig.api.entity.options;


import java.util.List;


import com.google.common.collect.Lists;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;


@JsonIgnoreProperties( ignoreUnknown = true )
public class TestOption implements Option
{

    @JsonIgnore
    private String indexHtml = "/home/ubuntu/repo/%s";

    @JsonIgnore
    private String testId;

    private String name;
    private boolean all;

    private List<String> targetIps;
    private List<String> playbooks;

    private int timeOut = 1000;


    public TestOption()
    {

    }


    public int getTimeOut()
    {
        return timeOut;
    }


    @JsonIgnore
    public String getType()
    {
        return "TEST";
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


    public boolean isAll()
    {
        return all;
    }


    public void setAll( final boolean all )
    {
        this.all = all;
    }


    public List<String> getTargetIps()
    {
        return targetIps;
    }


    public void setTargetIps( final List<String> targetIps )
    {
        this.targetIps = targetIps;
    }


    public List<String> getPlaybooks()
    {
        return playbooks;
    }


    public void setPlaybooks( final List<String> playbooks )
    {
        this.playbooks = playbooks;
    }


    public String getTestId()
    {
        return testId;
    }


    public void setTestId( String testId )
    {
        this.testId = testId;
    }


    @JsonIgnore
    public String getOutputPath()
    {
        return indexHtml;
    }


    public void setOutputPath( String outputPath )
    {
        this.indexHtml = outputPath;
    }


    @JsonIgnore
    public String getCommand()
    {
        return null;
    }


    @JsonIgnore
    public List<String> getArgs()
    {
        if ( targetIps == null )
        {
            return Lists.newArrayList( " " );
        }
        List<String> args = Lists.newArrayList();

        if ( targetIps.size() > 0 )
        {
            args.add( "-m" );
            args.add( targetIps.get( 0 ) );
        }
        if ( targetIps.size() > 1 )
        {
            args.add( "-M" );
            args.add( targetIps.get( 1 ) );
        }

        args.add( "-s" );
        if ( all )
        {

            args.add( "all" );
        }
        else
        {
            args.add( String.format( "\"%s\"", String.join( " ", playbooks ) ) );
        }

        args.add( "-r" );

        args.add( "-o" );

        args.add( String.format( indexHtml, getTestId() ) );

        return args;
    }
}
