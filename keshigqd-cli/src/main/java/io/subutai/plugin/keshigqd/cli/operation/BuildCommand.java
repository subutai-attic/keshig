// 
// Decompiled by Procyon v0.5.30
// 

package io.subutai.plugin.keshigqd.cli.operation;

import java.util.List;
import io.subutai.common.command.RequestBuilder;
import com.google.common.collect.Lists;
import io.subutai.plugin.keshigqd.api.KeshigQD;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "keshigqd", name = "build", description = "build specific branch/tag/commit ")
public class BuildCommand extends OsgiCommandSupport
{
    @Argument(index = 0, name = "tests", description = "run with tests true/false", required = false, multiValued = false)
    String tests;
    @Argument(index = 1, name = "clean", description = "clean run true/false", required = false, multiValued = false)
    String clean;
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
        final List<String> args = (List<String>)Lists.newArrayList("-t", this.tests, "-c", this.clean);
        this.keshig.build(new RequestBuilder(io.subutai.plugin.keshigqd.api.entity.Command.getBuildCommand()).withCmdArgs((List)args).withTimeout(600), this.target);
        return null;
    }
}
