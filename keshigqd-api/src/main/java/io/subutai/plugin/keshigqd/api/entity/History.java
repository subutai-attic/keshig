package io.subutai.plugin.keshigqd.api.entity;


import java.util.List;


public class History
{
    private String id;
    private String type;
    private Long startTime;
    private Long endTime;
    private String command;
    private String exitCode;
    private String server;
    private List<String> args;
    private String stdOut;
    private String stdErr;

    public History( final String id, final String type, final Long startTime, final String command, final String server,
                    final List<String> args )
    {
        this.id = id;
        this.type = type;
        this.startTime = startTime;
        this.command = command;
        this.server = server;
        this.args = args;
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


    public String getCommand()
    {
        return command;
    }


    public void setCommand( final String command )
    {
        this.command = command;
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


    public List<String> getArgs()
    {
        return args;
    }


    public void setArgs( final List<String> args )
    {
        this.args = args;
    }


    @Override
    public String toString()
    {
        return "History{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", command='" + command + '\'' +
                ", exitCode='" + exitCode + '\'' +
                ", server='" + server + '\'' +
                ", args=" + args +
                '}';
    }
}
