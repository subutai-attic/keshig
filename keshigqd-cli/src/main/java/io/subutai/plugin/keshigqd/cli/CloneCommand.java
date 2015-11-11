package io.subutai.plugin.keshigqd.cli;


import java.util.HashMap;
import java.util.Map;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import io.subutai.plugin.keshigqd.api.KeshigQD;


@Command( scope = "keshigqd", name = "clone", description = "clone specific branch/tag/commit" )
public class CloneCommand extends OsgiCommandSupport
{
    @Argument( index = 0, name = "repo", description = "Repo url", required = true, multiValued = false )
    String repo;

    @Argument( index = 1, name = "branch", description = "branch/tag/commit", required = false, multiValued = false )
    String branch;

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
        args.put( io.subutai.plugin.keshigqd.api.entity.Command.repoOpt, repo );
        args.put( io.subutai.plugin.keshigqd.api.entity.Command.branchOpt,branch );
        keshig.clone( args );

        return null;
    }
}
