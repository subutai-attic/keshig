package io.subutai.plugin.keshigqd.cli;


import java.util.HashMap;
import java.util.Map;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import io.subutai.plugin.keshigqd.api.KeshigQD;


@Command( scope = "keshigqd", name = "deploy" )
public class DeployCommand extends OsgiCommandSupport
{
    @Argument( index = 0, name = "target", description = "target deployment " )
    String target;

    private KeshigQD keshig;
    @Argument( index = 0, name = "build", description = "build to use as a source" )
    String build;


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

        Map<String, String> args = new HashMap<>();
        args.put( io.subutai.plugin.keshigqd.api.entity.Command.target, target );
        args.put( io.subutai.plugin.keshigqd.api.entity.Command.build, build );
        keshig.build( args );


        return null;
    }
}
