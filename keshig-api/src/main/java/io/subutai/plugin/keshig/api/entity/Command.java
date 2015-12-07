package io.subutai.plugin.keshig.api.entity;


public class Command
{
    //@formatter:off

    //clone flags
    public static final String repoOpt   = "-r";
    public static final String branchOpt = "-b";

    //build flags
    public static final String tests     = "-t";
    public static final String clean     = "-c";
    public static final String list      = "-l";

    //deploy flags
    public static final String target    = "-t";
    public static final String build     = "-b";
    public static final String folder    = "-f";

    //test flags
    public static final String all       = "-a";
    public static final String specific  = "-s";

    public static final String ouput     = "-o";

    public static final String export = "-x";
    //@formatter:on


    public static String getCloneCommand()
    {
        return "/home/ubuntu/clone";
    }


    public static String getBuildCommand()
    {
        return "/home/ubuntu/build";
    }


    public static String getDeployCommand()
    {
        return "subutai deploy";
    }


    public static String getTestComand()
    {
        return "/home/ubuntu/playbooks-newui/run_tests.sh";
    }
}
