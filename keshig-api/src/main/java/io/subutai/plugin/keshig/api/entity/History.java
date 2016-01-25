package io.subutai.plugin.keshig.api.entity;


public class History
{
    private String id;
    private String type;
    private Long startTime;
    private Long endTime;

    private String exitCode;
    private String server;
    private String url;


    public History()
    {

    }


    public History( String id, String type, Long startTime, String server )
    {
        this.id = id;
        this.type = type;
        this.startTime = startTime;
        this.server = server;
    }


    public History( String id, String type, Long startTime, Long endTime, String exitCode, String server, String stdOut,
                    String stdErr )
    {
        this.id = id;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.exitCode = exitCode;
        this.server = server;
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


    public String getUrl()
    {
        return url;
    }


    public void setUrl( final String url )
    {
        this.url = url;
    }


    @Override
    public String toString()
    {
        return "History{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", exitCode='" + exitCode + '\'' +
                ", server='" + server + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
