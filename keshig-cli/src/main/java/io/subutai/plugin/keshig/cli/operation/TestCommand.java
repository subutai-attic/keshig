

package io.subutai.plugin.keshig.cli.operation;

import java.util.List;

import com.google.common.collect.Lists;
import io.subutai.common.command.RequestBuilder;
import io.subutai.plugin.keshig.api.Keshig;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "keshigqd", name = "test")
public class TestCommand extends OsgiCommandSupport {
    @Argument(index = 0, name = "all", description = "run all tests @param all", required = true, multiValued = false)
    String tests;
    @Argument(index = 1, name = "target", description = "target server id", required = false)
    String target;
    private Keshig keshig;

    public Keshig getKeshig() {
        return this.keshig;
    }

    public void setKeshig(final Keshig keshig) {
        this.keshig = keshig;
    }

    protected Object doExecute() throws Exception {
        this.keshig.test(new RequestBuilder(io.subutai.plugin.keshig.api.entity.Command.getTestComand()).withCmdArgs((List) Lists.newArrayList(this.tests)).withTimeout(900), this.target);
        return null;
    }
}
