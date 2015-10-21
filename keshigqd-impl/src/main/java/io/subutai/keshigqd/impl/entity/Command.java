package io.subutai.keshigqd.impl.entity;


import java.util.HashMap;
import java.util.Map;


public class Command
{
    //@formatter:off

    //clone flags
    private final String repoOpt   = "-r";
    private final String branchOpt = "-b";
    //build flags
    private final String tests     = "-t";
    private final String clean     = "-c";
    //deploy flags
    private final String target    = "-t";
    //test flags
    private final String all       = "-a";
    private final String specific  = "-s";

    //@formatter:on

    public Map<String, String> opts = new HashMap<>();
    public String command;


    private Command( CloneCommandBuilder cmd )
    {
        this.command = "subutai keshigqd clone";
        this.opts.put( repoOpt, cmd.repoUrl );
        this.opts.put( branchOpt, cmd.branchName );
    }


    private Command( BuildCommandBuilder cmd )
    {
        this.command = "subutai keshigqd build";
        this.opts.put( tests, cmd.runTests );
        this.opts.put( clean, cmd.cleanRun );
    }


    private Command( DeployCommandBuilder cmd )
    {
        this.command = "subutai keshigqd deploy";
        this.opts.put( target, cmd.host );
    }


    private Command( TestCommandBuilder cmd )
    {
        this.command = "subutai keshigqd test";

        if ( cmd.all.equalsIgnoreCase( "true" ) )
        {
            this.opts.put( all, cmd.all );
        }
        else
        {
            this.opts.put( specific, cmd.specific );
        }
    }


    /*
    *   Clone Command To Build:
    *   subutai keshigqd clone -r <repo> -b <branch>
    * */
    public class CloneCommandBuilder
    {

        private String repoUrl;
        private String branchName;


        public CloneCommandBuilder( final String repoUrl, final String branchName )
        {
            this.repoUrl = repoUrl;
            this.branchName = branchName;
        }


        public CloneCommandBuilder repoUrl( final String repoUrl )
        {
            this.repoUrl = repoUrl;
            return this;
        }


        public CloneCommandBuilder branchName( final String branchName )
        {
            this.branchName = branchName;
            return this;
        }


        public Command build()
        {
            return new Command( this );
        }
    }

    /*
    *   Build Command To Build:
    *   subutai keshigqd build -t <true/false> -c <true/false>
    * */
    public class BuildCommandBuilder
    {
        //default build options are set to true
        private String runTests = "true";
        private String cleanRun = "true";


        public BuildCommandBuilder( final Boolean runTests, final Boolean cleanRun )
        {
            this.runTests = runTests.toString();
            this.cleanRun = cleanRun.toString();
        }


        public BuildCommandBuilder runTest( final Boolean runTests )
        {
            this.runTests = runTests.toString();
            return this;
        }


        public BuildCommandBuilder cleanRun( final Boolean cleanRun )
        {
            this.cleanRun = cleanRun.toString();
            return this;
        }


        public Command build()
        {
            return new Command( this );
        }
    }

    /*
    *   Build Command To Build:
    *   subutai keshigqd deploy -t <target>
    * */
    public class DeployCommandBuilder
    {
        //target host machine to deploy
        private String host = "localhost";


        public DeployCommandBuilder( final String target )
        {
            this.host = target;
        }


        public DeployCommandBuilder target( final String target )
        {
            this.host = target;
            return this;
        }


        public Command build()
        {
            return new Command( this );
        }
    }

    /*
    *   Test Command To Build:
    *   subutai keshigqd test -t <target> -all <true/false>/-s <specific_test>
    *   all tests are default option
    *   if specific test is present will ignore all option
    * */
    public class TestCommandBuilder
    {
        private String target = "localhost";
        private String all = "true";
        private String specific;


        public TestCommandBuilder( final String target, final Boolean all, final String specific )
        {
            this.target = target;
            this.all = all.toString();
            this.specific = specific;
        }


        public TestCommandBuilder target( final String target )
        {
            this.target = target;
            return this;
        }


        public TestCommandBuilder all( final Boolean all )
        {
            this.all = all.toString();
            return this;
        }


        public TestCommandBuilder specific( final String specific )
        {
            this.specific = specific;
            return this;
        }


        public Command build()
        {
            return new Command( this );
        }
    }
}
