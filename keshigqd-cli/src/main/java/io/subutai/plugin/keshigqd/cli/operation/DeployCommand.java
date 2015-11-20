

package io.subutai.plugin.keshigqd.cli.operation;

import java.util.List;

import com.google.common.collect.Lists;
import io.subutai.common.command.RequestBuilder;
import io.subutai.plugin.keshigqd.api.KeshigQD;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "keshigqd", name = "deploy", description = "deploy peers ")
public class DeployCommand extends OsgiCommandSupport {
    @Argument(index = 0, name = "build", description = "target build deployment ")
    String build;
    @Argument(index = 1, name = "target", description = "target server id")
    String target;
    private KeshigQD keshig;

    public KeshigQD getKeshig() {
        return this.keshig;
    }

    public void setKeshig(final KeshigQD keshig) {
        this.keshig = keshig;
    }

    protected Object doExecute() throws Exception {
        this.keshig.deploy(new RequestBuilder(io.subutai.plugin.keshigqd.api.entity.Command.getDeployCommand()).withCmdArgs((List) Lists.newArrayList("-f", this.build)).withTimeout(900).withRunAs("ubuntu"), this.target);
        return null;
    }
}
