package io.subutai.plugin.keshigqd.api.entity;



import io.subutai.common.command.RequestBuilder;


public class History
{
    private String id;
    private String type;
    private Long startTime;
    private Long endTime;
    private RequestBuilder requestBuilder;
    private String exitCode;
    private String server;
    private String stdOut;
    private String stdErr;


    public History( final String id, final String type, final Long startTime, final RequestBuilder requestBuilder,
                    final String server )
    {
        this.id = id;
        this.type = type;
        this.startTime = startTime;
        this.requestBuilder = requestBuilder;
        this.server = server;
    }


    public String getStdOut()
    {
        return stdOut;
    }


    public void setStdOut( final String stdOut )
    {
        this.stdOut = stdOut;
    }


    public String getStdErr()
    {
        return stdErr;
    }


    public void setStdErr( final String stdErr )
    {
        this.stdErr = stdErr;
    }


    public String getId()
    {
        return id;
    }


    public void setId( final String id )
    {
        this.id = id;
    }


    public String getType()
    {
        return type;
    }


    public void setType( final String type )
    {
        this.type = type;
    }


    public Long getStartTime()
    {
        return startTime;
    }


    public void setStartTime( final Long startTime )
    {
        this.startTime = startTime;
    }


    public Long getEndTime()
    {
        return endTime;
    }


    public void setEndTime( final Long endTime )
    {
        this.endTime = endTime;
    }

    public String getExitCode()
    {
        return exitCode;
    }


    public void setExitCode( final String exitCode )
    {
        this.exitCode = exitCode;
    }


    public String getServer()
    {
        return server;
    }


    public void setServer( final String server )
    {
        this.server = server;
    }


    public RequestBuilder getRequestBuilder()
    {
        return requestBuilder;
    }


    public void setRequestBuilder( final RequestBuilder requestBuilder )
    {
        this.requestBuilder = requestBuilder;
    }


    @Override
    public String toString()
    {
        return "History{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", requestBuilder=" + requestBuilder +
                ", exitCode='" + exitCode + '\'' +
                ", server='" + server + '\'' +
                ", stdOut='" + stdOut + '\'' +
                ", stdErr='" + stdErr + '\'' +
                '}';
    }
}
