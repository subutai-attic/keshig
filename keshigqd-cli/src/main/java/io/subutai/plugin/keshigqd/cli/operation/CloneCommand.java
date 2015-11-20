

package io.subutai.plugin.keshigqd.cli.operation;

import java.util.List;

import io.subutai.common.command.RequestBuilder;
import com.google.common.collect.Lists;
import io.subutai.plugin.keshigqd.api.KeshigQD;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "keshigqd", name = "clone", description = "clone specific branch/tag/commit")
public class CloneCommand extends OsgiCommandSupport {
    @Argument(index = 0, name = "repo", description = "Repo url", required = true, multiValued = false)
    String repo;
    @Argument(index = 1, name = "branch", description = "branch/tag/commit", required = false, multiValued = false)
    String branch;
    @Argument(index = 1, name = "target", description = "target server id", required = false)
    String target;
    private KeshigQD keshig;

    public KeshigQD getKeshig() {
        return this.keshig;
    }

    public void setKeshig(final KeshigQD keshig) {
        this.keshig = keshig;
    }

    protected Object doExecute() throws Exception {
        final List<String> args = (List<String>) Lists.newArrayList("-r", this.repo, "-b", this.branch);
        this.keshig.clone(new RequestBuilder(io.subutai.plugin.keshigqd.api.entity.Command.getCloneCommand()).withCmdArgs((List) args).withTimeout(900), this.target);
        return null;
    }
}
