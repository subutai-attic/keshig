package io.subutai.plugin.keshigqd.cli;


import java.util.List;

import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import io.subutai.plugin.keshigqd.api.KeshigQD;
import io.subutai.plugin.keshigqd.api.entity.Build;


@Command( scope = "keshigqd", name = "list-builds", description = "list builds" )
public class ListBuilds extends OsgiCommandSupport
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
        List<Build> buildList = keshig.getBuilds();

        for ( Build build : buildList )
        {
            System.out.println( String.format( "Build ID:(%s)\t Build Name:(%s)\t Build Version:(%s)\t Build Date:(%s)",
                            build.getId(), build.getName(), build.getVersion(), build.getDate().toString() ) );
        }

        return null;
    }
}
