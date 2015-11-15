package io.subutai.plugin.keshigqd.cli.operation;


import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import io.subutai.plugin.keshigqd.api.KeshigQD;


@Command( scope = "keshigqd", name = "run-default",description = "run defaults")
public class RunDefaultsCommand extends OsgiCommandSupport
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
        System.out.println("Starting Keshig Integration");

        keshig.runDefaults();

        return null;
    }
}
