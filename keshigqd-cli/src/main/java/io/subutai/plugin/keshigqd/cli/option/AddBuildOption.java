package io.subutai.plugin.keshigqd.cli.option;


import java.util.List;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import io.subutai.plugin.keshigqd.api.KeshigQD;
import io.subutai.plugin.keshigqd.api.entity.OperationType;
import io.subutai.plugin.keshigqd.api.entity.options.BuildOption;


@Command( scope = "keshigqd", name = "add-build", description = "add build option" )
public class AddBuildOption extends OsgiCommandSupport
{
    @Argument( index = 0, name = "name", required = true, description = "build option name" )
    private String name;
    @Argument( index = 1, name = "clean-install", required = true, description = "clean install" )
    private String cleanInstall;
    @Argument( index = 2, name = "run-tests", required = true, description = "run tests" )
    private String runTests;
    @Argument( index = 3, name = "active", required = true, description = "active" )
    private String active;
    @Argument( index = 4, name = "timeout", required = true, description = "timeout" )
    private int timeout;

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
        BuildOption buildOption = new BuildOption();
        buildOption.setName( name );
        buildOption.setTimeOut( timeout );
        buildOption.setCleanInstall( Boolean.valueOf( cleanInstall ) );
        buildOption.setRunTests( Boolean.valueOf( runTests ) );
        if ( active.equalsIgnoreCase( "true" ) )
        {
            buildOption.setIsActive( true );
            List<BuildOption> list = ( List<BuildOption> ) keshig.allOptionsByType( buildOption.getType() );
            for ( BuildOption opt : list )
            {
                if ( opt.isActive() )
                {
                    System.out.println( String.format( "Deactivating option (%s)", list.toString() ) );
                    keshig.deactivate( opt.getName(), OperationType.DEPLOY );
                }
            }
        }
        else if ( active.equalsIgnoreCase( "false" ) )
        {
            buildOption.setIsActive( false );
        }
        else
        {
            throw new Exception( "Invalid arg for active @param true/false" );
        }
        keshig.saveOption( buildOption, OperationType.BUILD );
        return null;
    }
}
