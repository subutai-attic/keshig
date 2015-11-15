package io.subutai.plugin.keshigqd.cli.builds;


import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import com.google.common.base.Strings;

import io.subutai.plugin.keshigqd.api.KeshigQD;
import io.subutai.plugin.keshigqd.api.entity.Dependency;
import io.subutai.plugin.keshigqd.api.entity.ServerType;


@Command( scope = "keshigqd", name = "list-packages", description = "list installed packages on targer server" )
public class ListDependencies extends OsgiCommandSupport
{

    @Argument( index = 0, name = "target", description = ""
            + "all              - shows all installed packages on each server\n"
            + "target server id - shows all installed packages on target server\n"
            + "build/deploy/test- shows all required packages by server type" )


    private String arg;


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
        switch ( arg )
        {
            case "all":
                printable( keshig.getAllPackages() );
                break;
            case "build":
                printable( keshig.getRequiredPackages( ServerType.BUILD_SERVER ) );
                break;
            case "deploy":
                printable( keshig.getRequiredPackages( ServerType.DEPLOY_SERVER ) );
                break;
            case "test":
                printable( keshig.getRequiredPackages( ServerType.TEST_SERVER ) );
                break;
            default:
                Pattern pattern = Pattern.compile( "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}" );
                Matcher matcher = pattern.matcher( arg.toLowerCase() );
                if ( matcher.matches() )
                {
                    printable( keshig.getPackages( arg ) );
                }
                else
                {
                    System.out.println( "Unknown argument provided. Please refer to help" );
                }
        }
        return null;
    }


    private void printable( List<Dependency> dependencyList )
    {
        System.out.format( "%20s%10s%5s%32s", "Name", "Version", "Arch", "Description\n" );

        System.out.println( Strings.repeat( "-", 67 ) );

        for ( Dependency pkg : dependencyList )
        {
            System.out
                    .format( "%20s%10s%5s%32s", pkg.getName(), pkg.getVersion(), pkg.getArch(), pkg.getDescription() );
        }
    }


    private void printable( Map<String, List<Dependency>> pkgs )
    {
        for ( Map.Entry<String, List<Dependency>> entry : pkgs.entrySet() )
        {
            System.out.println( String.format( "Server: %s", entry.getKey() ) );
            printable( entry.getValue() );
        }
    }
}
