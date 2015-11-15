package io.subutai.plugin.keshigqd.cli.operation;


import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import com.google.common.collect.Lists;

import io.subutai.common.command.RequestBuilder;
import io.subutai.plugin.keshigqd.api.KeshigQD;


@Command( scope = "keshigqd", name = "test" )
public class TestCommand extends OsgiCommandSupport
{
    @Argument( index = 0, name = "all", description = "run all tests @param all", required = true,
            multiValued = false )
    String tests;

    @Argument( index = 1, name = "target", description = "target server id",required = false)
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
        keshig.test( new RequestBuilder( io.subutai.plugin.keshigqd.api.entity.Command.getTestComand() )
                .withCmdArgs( Lists.newArrayList(tests) ).withTimeout( 900 ),target );

        return null;
    }
}
