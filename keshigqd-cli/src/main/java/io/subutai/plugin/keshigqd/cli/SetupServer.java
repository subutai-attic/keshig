package io.subutai.plugin.keshigqd.cli;


import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import io.subutai.plugin.keshigqd.api.KeshigQD;
import io.subutai.plugin.keshigqd.api.entity.Server;
import io.subutai.plugin.keshigqd.api.entity.ServerType;


@Command( scope = "keshigqd", name = "set-server", description = "Set build/deploy/test server" )
public class SetupServer extends OsgiCommandSupport
{
    @Argument( index = 0, name = "id", description = "Server UUID", required = true, multiValued = false )
    String serverId;
    @Argument( index = 1, name = "name", description = "Server Name", required = true, multiValued = false )
    String serverName;
    @Argument( index = 2, name = "address", description = "Server Address", required = true, multiValued = false )
    String serverAddress;
    @Argument( index = 3, name = "type", description = "Server Type", required = true, multiValued = false )
    String serverType;
    @Argument( index = 4, name = "description", description = "Description", required = true, multiValued = false )
    String description;

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
        String type;
        if ( serverType.equalsIgnoreCase( "test" ) )
        {
            type = ServerType.TEST_SERVER;
        }
        else if ( serverType.equalsIgnoreCase( "deploy" ) )
        {
            type = ServerType.DEPLOY_SERVER;
        }
        else if ( serverType.equalsIgnoreCase( "build" ) )
        {
            type = ServerType.BUILD_SERVER;
        }
        else
        {
            throw new Exception( "\nInvalid server type :\n Enter one of the following:\nTest\nDeploy\nBuild" );
        }

        Server server = new Server( serverId, serverName, serverAddress, type, description );

        keshig.addServer( server );

        System.out.println( String.format( "Server (%s) added", server.toString() ) );

        return null;
    }
}
