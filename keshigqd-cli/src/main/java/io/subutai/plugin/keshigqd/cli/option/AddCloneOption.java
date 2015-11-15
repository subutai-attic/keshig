package io.subutai.plugin.keshigqd.cli.option;


import java.util.List;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import io.subutai.plugin.keshigqd.api.KeshigQD;
import io.subutai.plugin.keshigqd.api.entity.OperationType;
import io.subutai.plugin.keshigqd.api.entity.options.CloneOption;


@Command( scope = "keshigqd", name = "add-clone", description = "add clone/build/deploy/test option" )
public class AddCloneOption extends OsgiCommandSupport
{
    @Argument( index = 0, name = "name", description = "clone option name" )
    String name;

    @Argument( index = 1, name = "url", description = "clone url" )
    String url;

    @Argument( index = 2, name = "branch", description = "clone branch" )
    String branch;

    @Argument( index = 3, name = "active", description = "option is active" )
    String active;

    @Argument( index = 4, name = "timeout", description = "option timeout" )
    int timeOut;


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

        if ( active.equalsIgnoreCase( "true" ) )
        {
            CloneOption cloneOption = new CloneOption( name, timeOut, true, "", branch, url );

            List<CloneOption> list = ( List<CloneOption> ) keshig.allOptionsByType( OperationType.CLONE );
            System.out.println( String.format( "Found (%s)", list.toString() ) );

            for ( CloneOption opt : list )
            {
                if ( opt.isActive() )
                {
                    System.out.println( String.format( "Deactivating option (%s)", list.toString() ) );
                    keshig.deactivate( opt.getName(), OperationType.CLONE );
                }
            }
            keshig.saveOption( cloneOption, OperationType.CLONE );
        }
        else if ( active.equalsIgnoreCase( "false" ) )
        {
            CloneOption cloneOption = new CloneOption( name, timeOut, false, "", branch, url );
            keshig.saveOption( cloneOption, OperationType.CLONE );
        }
        else
        {
            throw new Exception( "Invalid arg for active @param true/false" );
        }
        return null;
    }
}
