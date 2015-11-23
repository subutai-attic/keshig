
package io.subutai.plugin.keshig.cli.operation;

import io.subutai.plugin.keshig.api.Keshig;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "keshigqd", name = "run-default", description = "run defaults")
public class RunDefaultsCommand extends OsgiCommandSupport
{
    private Keshig keshig;
    
    public Keshig getKeshig() {
        return this.keshig;
    }
    
    public void setKeshig(final Keshig keshig) {
        this.keshig = keshig;
    }
    
    protected Object doExecute() throws Exception {
        System.out.println("Starting Keshig Integration");
        this.keshig.runDefaults();
        return null;
    }
}