// 
// Decompiled by Procyon v0.5.30
// 

package io.subutai.plugin.keshigqd.cli.operation;

import io.subutai.plugin.keshigqd.api.KeshigQD;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "keshigqd", name = "run-default", description = "run defaults")
public class RunDefaultsCommand extends OsgiCommandSupport
{
    private KeshigQD keshig;
    
    public KeshigQD getKeshig() {
        return this.keshig;
    }
    
    public void setKeshig(final KeshigQD keshig) {
        this.keshig = keshig;
    }
    
    protected Object doExecute() throws Exception {
        System.out.println("Starting Keshig Integration");
        this.keshig.runDefaults();
        return null;
    }
}
