package io.subutai.plugin.keshig.api.entity;

public class History
{
    private String id;
    private String type;
    private Long startTime;
    private Long endTime;

    private String exitCode;
    private String server;
    private String stdOut;
    private String stdErr;

    public History() {

    }

    public History(String id, String type, Long startTime, String server) {
        this.id = id;
        this.type = type;
        this.startTime = startTime;
        this.server = server;
    }

    public History(String id, String type, Long startTime, Long endTime, String exitCode, String server, String stdOut, String stdErr) {
        this.id = id;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.exitCode = exitCode;
        this.server = server;
        this.stdOut = stdOut;
        this.stdErr = stdErr;
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

    @Override
    public String toString() {
        return "History{" +
                "stdErr='" + stdErr + '\'' +
                ", stdOut='" + stdOut + '\'' +
                ", server='" + server + '\'' +
                ", exitCode='" + exitCode + '\'' +
                ", endTime=" + endTime +
                ", startTime=" + startTime +
                ", type='" + type + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
