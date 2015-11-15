package io.subutai.plugin.keshigqd.cli.server;


import java.util.List;

import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import io.subutai.plugin.keshigqd.api.KeshigQD;
import io.subutai.plugin.keshigqd.api.entity.Server;


@Command( scope = "keshigqd", name = "list-server", description = "List servers" )
public class ListServers extends OsgiCommandSupport
{
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
        List<Server> serverList = keshig.getServers();
        System.out.println( "\n Servers: " + serverList.toString() );
        return null;
    }
}
