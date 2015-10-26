package io.subutai.plugin.keshigqd.api.entity;


public class Command
{
    //@formatter:off

    //clone flags
    public static final String repoOpt   = "-r";
    public static final String branchOpt = "-b";
    //build flags
    public static final String tests     = "-t";
    public static final String clean     = "-c";
    //deploy flags
    public static final String target    = "-t";
    public static final String build    = "-b";
    //test flags
    public static final String all       = "-a";
    public static final String specific  = "-s";

    //commands

    //@formatter:on

    public static String getCloneCommand()
    {
        return "subutai keshigqd clone";
    }


    public static String getBuildCommand()
    {
        return "subutai keshigqd build";
    }


    public static String getDeployCommand()
    {
        return "subutai keshigqd deploy";
    }


    public static String getTestComand()
    {
        return "subutai keshigqd test";
    }

}
