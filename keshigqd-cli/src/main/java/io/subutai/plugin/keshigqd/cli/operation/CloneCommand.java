package io.subutai.plugin.keshigqd.cli.operation;


import java.util.List;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import com.google.common.collect.Lists;

import io.subutai.common.command.RequestBuilder;
import io.subutai.plugin.keshigqd.api.KeshigQD;


@Command( scope = "keshigqd", name = "clone", description = "clone specific branch/tag/commit" )
public class CloneCommand extends OsgiCommandSupport
{
    @Argument( index = 0, name = "repo", description = "Repo url", required = true, multiValued = false )
    String repo;

    @Argument( index = 1, name = "branch", description = "branch/tag/commit", required = false, multiValued = false )
    String branch;

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

        List<String> args = Lists.newArrayList( io.subutai.plugin.keshigqd.api.entity.Command.repoOpt, repo,
                io.subutai.plugin.keshigqd.api.entity.Command.branchOpt, branch );

        keshig.clone( new RequestBuilder( io.subutai.plugin.keshigqd.api.entity.Command.getCloneCommand() )
                .withCmdArgs( args ).withTimeout( 900 ), target );

        return null;
    }
}
