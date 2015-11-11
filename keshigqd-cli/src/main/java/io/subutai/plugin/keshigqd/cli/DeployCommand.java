package io.subutai.plugin.keshigqd.cli;


import java.util.HashMap;
import java.util.Map;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import io.subutai.plugin.keshigqd.api.KeshigQD;


@Command( scope = "keshigqd", name = "deploy" ,description = "deploy peers ")
public class DeployCommand extends OsgiCommandSupport
{
    @Argument( index = 0, name = "target", description = "target deployment " )
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

        Map<String, String> args = new HashMap<>();
//        args.put( io.subutai.plugin.keshigqd.api.entity.Command.target, target );
//        args.put( io.subutai.plugin.keshigqd.api.entity.Command.build, build );
        // /home/ubuntu/deploy -f <build_name>
        args.put( io.subutai.plugin.keshigqd.api.entity.Command.folder,target );

        keshig.deploy( args );

        return null;
    }
}
