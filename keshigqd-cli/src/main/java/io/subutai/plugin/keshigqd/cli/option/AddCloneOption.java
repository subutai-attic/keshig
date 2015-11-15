package io.subutai.plugin.keshigqd.cli.option;


import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import io.subutai.plugin.keshigqd.api.KeshigQD;


@Command(scope = "keshigqd", name = "add",description = "add clone/build/deploy/test option")
public class AddCloneOption extends OsgiCommandSupport
{

    String type;

    String

    private KeshigQD keshigQD;


    public KeshigQD getKeshigQD()
    {
        return keshigQD;
    }


    public void setKeshigQD( final KeshigQD keshigQD )
    {
        this.keshigQD = keshigQD;
    }


    @Override
    protected Object doExecute() throws Exception
    {
        return null;
    }
}
