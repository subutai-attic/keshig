package io.subutai.plugin.keshigqd.cli.operation;


import java.util.List;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import com.google.common.collect.Lists;

import io.subutai.common.command.RequestBuilder;
import io.subutai.plugin.keshigqd.api.KeshigQD;


@Command( scope = "keshigqd", name = "build", description = "build specific branch/tag/commit " )
public class BuildCommand extends OsgiCommandSupport
{
    @Argument( index = 0, name = "tests", description = "run with tests true/false", required = false, multiValued =
            false )
    String tests;

    @Argument( index = 1, name = "clean", description = "clean run true/false", required = false, multiValued = false )
    String clean;

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
        List<String> args = Lists.newArrayList( io.subutai.plugin.keshigqd.api.entity.Command.tests, tests,
                io.subutai.plugin.keshigqd.api.entity.Command.clean, clean );

        keshig.build( new RequestBuilder( io.subutai.plugin.keshigqd.api.entity.Command.getBuildCommand() )
                .withCmdArgs( args ).withTimeout( 600 ), target );

        return null;
    }
}
