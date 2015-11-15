package io.subutai.plugin.keshigqd.cli.operation;


import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import com.google.common.collect.Lists;

import io.subutai.common.command.RequestBuilder;
import io.subutai.plugin.keshigqd.api.KeshigQD;


@Command( scope = "keshigqd", name = "deploy", description = "deploy peers " )
public class DeployCommand extends OsgiCommandSupport
{
    @Argument( index = 0, name = "build", description = "target build deployment " )
    String build;

    @Argument( index = 1, name = "target", description = "target server id", required = false )
    String target;

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
        keshig.deploy( new RequestBuilder( io.subutai.plugin.keshigqd.api.entity.Command.getBuildCommand() )
                .withCmdArgs( Lists.newArrayList( io.subutai.plugin.keshigqd.api.entity.Command.folder, build ) )
                .withTimeout( 900 ), target );
        return null;
    }
}
