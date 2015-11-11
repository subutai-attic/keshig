package io.subutai.plugin.keshigqd.cli;


import java.util.HashMap;
import java.util.Map;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import io.subutai.plugin.keshigqd.api.KeshigQD;


@Command( scope = "keshigqd", name = "test" )
public class TestCommand extends OsgiCommandSupport
{
    @Argument( index = 0, name = "all", description = "run all tests @param all", required = true,
            multiValued = false )
    String tests;

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

        args.put( io.subutai.plugin.keshigqd.api.entity.Command.tests, tests );

        keshig.test( args );

        return null;
    }
}
