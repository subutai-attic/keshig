package io.subutai.plugin.keshigqd.cli.option;


import java.util.List;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import com.google.common.base.Strings;

import io.subutai.plugin.keshigqd.api.KeshigQD;
import io.subutai.plugin.keshigqd.api.entity.OperationType;
import io.subutai.plugin.keshigqd.api.entity.options.BuildOption;
import io.subutai.plugin.keshigqd.api.entity.options.CloneOption;
import io.subutai.plugin.keshigqd.api.entity.options.DeployOption;
import io.subutai.plugin.keshigqd.api.entity.options.TestOption;


@Command( scope = "keshigqd", name = "list-option", description = "list clone/build/deploy/test option" )
public class ListOptions extends OsgiCommandSupport
{

    @Argument( index = 0, name = "type", description = "list options by type" )
    String type;

    private KeshigQD keshig;


    public KeshigQD getKeshig()
    {
        return keshig;
    }


    public void setKeshig( final KeshigQD keshig )
    {
        this.keshig = keshig;
    }


    @Override
    protected Object doExecute() throws Exception
    {
        if ( type.equalsIgnoreCase( "all" ) )
        {
            List<CloneOption> co = ( List<CloneOption> ) keshig.allOptionsByType( OperationType.CLONE );
            printCloneOption( co );

            List<BuildOption> bo = ( List<BuildOption> ) keshig.allOptionsByType( OperationType.BUILD );
            printBuildOption( bo );

            List<TestOption> to = ( List<TestOption> ) keshig.allOptionsByType( OperationType.TEST );
            printTestOption( to );

            List<DeployOption> dop = ( List<DeployOption> ) keshig.allOptionsByType( OperationType.DEPLOY );
            printDeployOption( dop );

            return null;
        }
        switch ( OperationType.valueOf( type.toUpperCase() ) )
        {
            case CLONE:
                List<CloneOption> list = ( List<CloneOption> ) keshig.allOptionsByType( OperationType.CLONE );
                printCloneOption( list );
                break;
            case BUILD:
                List<BuildOption> buildOptions = ( List<BuildOption> ) keshig.allOptionsByType( OperationType.BUILD );
                printBuildOption( buildOptions );
                break;
            case DEPLOY:
                List<DeployOption> deployOptions =
                        ( List<DeployOption> ) keshig.allOptionsByType( OperationType.DEPLOY );
                printDeployOption( deployOptions );
                break;
            case TEST:
                List<TestOption> testOptions = ( List<TestOption> ) keshig.allOptionsByType( OperationType.TEST );
                printTestOption( testOptions );
                break;
        }
        return null;
    }


    public void printCloneOption( List<CloneOption> cloneOptions )
    {
        System.out.format( "%62s\n", "*******" );
        System.out.format( "%62s\n", "*CLONE*" );
        System.out.format( "%62s\n", "*******" );
        System.out.println( Strings.repeat( "-", 114 ) );
        System.out.format( "|%19s|%50s|%12s|%7s|%9s|%10s|\n", "NAME", "URL", "BRANCH", "ACTIVE", "TYPE", "TIMEOUT" );
        System.out.println( Strings.repeat( "-", 114 ) );
        for ( CloneOption option : cloneOptions )
        {
            System.out.format( "|%15s|%50s|%12s|%7s|%9s|%10s|\n", option.getName(), option.getUrl(), option.getBranch(),
                    String.valueOf( option.isActive() ), option.getType().toString(), option.getTimeOut() );
            System.out.println( Strings.repeat( "-", 114 ) );
        }
    }


    public void printBuildOption( List<BuildOption> buildOptions )
    {
        System.out.format( "%62s\n", "*******" );
        System.out.format( "%62s\n", "*BUILD*" );
        System.out.format( "%62s\n", "*******" );
        System.out.println( Strings.repeat( "-", 114 ) );
        System.out.format( "|%19s|%27s|%27s|%10s|%9s|%10s|\n", "NAME", "RUN TESTS", "CLEAN INSTALL", "ACTIVE", "TYPE",
                "TIMEOUT" );
        System.out.println( Strings.repeat( "-", 114 ) );
        for ( BuildOption option : buildOptions )
        {
            System.out
                    .format( "|%19s|%27s|%27s|%10s|%9s|%10s|\n", option.getName(), String.valueOf( option.isRunTests() ),
                            String.valueOf( option.isCleanInstall() ), String.valueOf( option.isActive() ),
                            option.getType().toString(), option.getTimeOut() );
            System.out.println( Strings.repeat( "-", 114 ) );
        }
    }


    public void printDeployOption( List<DeployOption> deployOptions )
    {
        System.out.format( "%62s\n", "********" );
        System.out.format( "%62s\n", "*DEPLOY*" );
        System.out.format( "%62s\n", "********" );
        System.out.println( Strings.repeat( "-", 114 ) );
        System.out.format( "|%19s|%25s|%15s|%19s|%10s|%9s|%10s|\n", "NAME", "BUILD NAME", "# OF PEERS", "# OF RHS",
                "TYPE", "ACTIVE", "TIMEOUT" );
        System.out.println( Strings.repeat( "-", 114 ) );
        for ( DeployOption option : deployOptions )
        {
            System.out.format( "|%19s|%25s|%15s|%19s|%10s|%9s|%10s|\n", option.getName(), option.getBuildName(),
                    option.getNumberOfPeers(), option.getNumberOfRhsPerPeer(), option.getType(),
                    String.valueOf( option.isActive() ), option.getTimeOut() );

            System.out.println( Strings.repeat( "-", 114 ) );
        }
    }


    public void printTestOption( List<TestOption> testOptions )
    {
        System.out.format( "%62s\n", "*******" );
        System.out.format( "%62s\n", "* TEST*" );
        System.out.format( "%62s\n", "*******" );
        System.out.println( Strings.repeat( "-", 121 ) );
        System.out
                .format( "|%15s|%5s|%35s|%30s|%10s|%9s|%10s|\n", "NAME", "ALL", "PLAYBOOKS", "HOSTS", "TYPE", "ACTIVE",
                        "TIMEOUT" );
        System.out.println( Strings.repeat( "-", 121 ) );
        for ( TestOption testOption : testOptions )
        {
            System.out.format( "|%15s|%5s|%35s|%19s|%10s|%9s|%10s|\n", testOption.getName(),
                    String.valueOf( testOption.isAll() ),
                    ( testOption.getPlaybooks() == null ? "" : testOption.getPlaybooks().toString() ),
                    testOption.getTargetIps().toString(), testOption.getType(), testOption.isActive(),
                    testOption.getTimeOut() );
            System.out.println( Strings.repeat( "-", 121 ) );
        }
    }
}
