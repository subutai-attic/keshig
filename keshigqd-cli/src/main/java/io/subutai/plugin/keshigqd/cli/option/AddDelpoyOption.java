package io.subutai.plugin.keshigqd.cli.option;


import java.util.List;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import io.subutai.plugin.keshigqd.api.KeshigQD;
import io.subutai.plugin.keshigqd.api.entity.OperationType;
import io.subutai.plugin.keshigqd.api.entity.options.DeployOption;


@Command( scope = "keshigqd", name = "add-deploy", description = "deploy option" )
public class AddDelpoyOption extends OsgiCommandSupport
{

    @Argument( index = 0, name = "name", required = true, description = "option name" )
    private String name;
    @Argument( index = 1, name = "peer", required = true, description = "number of peers to be deployed" )
    private int numberOfPeers;
    @Argument( index = 2, name = "rh", required = true, description = "number of rh to be deployed per peer" )
    private int numberOfRhsPerPeer;
    @Argument( index = 3, name = "build", required = true, description = "build to deploy" )
    private String buildName;
    @Argument( index = 4, name = "active", required = true, description = "active" )
    private String active;
    @Argument( index = 5, name = "timeout", required = true, description = "timeout" )
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
        DeployOption deployOption = new DeployOption();

        deployOption.setName( name );
        deployOption.setTimeOut( timeOut );
        deployOption.setNumberOfPeers( numberOfPeers );
        deployOption.setNumberOfRhsPerPeer( numberOfRhsPerPeer );
        deployOption.setBuildName( buildName );

        if ( active.equalsIgnoreCase( "true" ) )
        {
            List<DeployOption> list = ( List<DeployOption> ) keshig.allOptionsByType( deployOption.getType() );
            for ( DeployOption opt : list )
            {
                if ( opt.isActive() )
                {
                    System.out.println( String.format( "Deactivating option (%s)", list.toString() ) );
                    keshig.deactivate( opt.getName(), OperationType.DEPLOY );
                }
            }
            deployOption.setIsActive( true );
        }
        else if ( active.equalsIgnoreCase( "false" ) )
        {
            deployOption.setIsActive( false );
        }
        else
        {
            throw new Exception( "Invalid arg for active @param true/false" );
        }

        keshig.saveOption( deployOption, OperationType.DEPLOY );
        return null;
    }
}
