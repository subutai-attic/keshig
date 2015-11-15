package io.subutai.plugin.keshigqd.cli.option;


import java.util.Arrays;
import java.util.List;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import io.subutai.plugin.keshigqd.api.KeshigQD;
import io.subutai.plugin.keshigqd.api.entity.OperationType;
import io.subutai.plugin.keshigqd.api.entity.options.TestOption;


@Command( scope = "keshigqd", name = "add-test", description = "add test option" )
public class AddTestOption extends OsgiCommandSupport
{

    @Argument( index = 0, name = "name", required = true, description = "option name" )
    private String name;

    @Argument( index = 1, name = "all", required = false, description = "run all playbooks" )
    private String all;

    @Argument( index = 2, name = "hosts", required = true, description = "target IPs" )
    private String hosts;

    @Argument( index = 3, name = "playbooks", required = true, description = "target playbooks" )
    private String playbooks;

    @Argument( index = 4, name = "active", required = true, description = "option is active" )
    private String active;

    @Argument( index = 5, name = "timeout", required = true, description = "option timeout" )
    private int timeOut;


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
        TestOption testOption = new TestOption();

        testOption.setTargetIps( Arrays.asList( hosts.split( "," ) ) );
        testOption.setName( name );
        testOption.setTimeOut( timeOut );
        if ( active.equalsIgnoreCase( "true" ) )
        {
            testOption.setIsActive( true );
            List<TestOption> list = ( List<TestOption> ) keshig.allOptionsByType( testOption.getType() );
            for ( TestOption opt : list )
            {
                if ( opt.isActive() )
                {
                    System.out.println( String.format( "Deactivating option (%s)", list.toString() ) );
                    keshig.deactivate( opt.getName(), OperationType.TEST );
                }
            }
        }
        else if ( active.equalsIgnoreCase( "false" ) )
        {
            testOption.setIsActive( false );
        }
        else
        {
            throw new Exception( "Invalid arg for active @param true/false" );
        }
        if ( all.equalsIgnoreCase( "true" ) )
        {
            testOption.setAll( true );
        }
        else
        {
            testOption.setAll( false );
            testOption.setPlaybooks( Arrays.asList( playbooks.split( "," ) ) );
        }
        keshig.saveOption( testOption, OperationType.TEST );
        return null;
    }
}
