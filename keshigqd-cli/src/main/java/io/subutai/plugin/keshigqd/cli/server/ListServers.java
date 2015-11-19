// 
// Decompiled by Procyon v0.5.30
// 

package io.subutai.plugin.keshigqd.cli.server;

import io.subutai.plugin.keshigqd.api.entity.Server;
import java.util.List;
import io.subutai.plugin.keshigqd.api.KeshigQD;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "keshigqd", name = "list-server", description = "List servers")
public class ListServers extends OsgiCommandSupport
{
    private KeshigQD keshig;
    
    public KeshigQD getKeshig() {
        return this.keshig;
    }
    
    public void setKeshig(final KeshigQD keshig) {
        this.keshig = keshig;
    }
    
    protected Object doExecute() throws Exception {
        final List<Server> serverList = (List<Server>)this.keshig.getServers();
        System.out.println("\n Servers: " + serverList.toString());
        return null;
    }
}
