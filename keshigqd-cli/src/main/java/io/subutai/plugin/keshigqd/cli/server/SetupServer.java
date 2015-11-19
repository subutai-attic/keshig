// 
// Decompiled by Procyon v0.5.30
// 

package io.subutai.plugin.keshigqd.cli.server;

import io.subutai.plugin.keshigqd.api.entity.Server;
import io.subutai.plugin.keshigqd.api.entity.ServerType;
import io.subutai.plugin.keshigqd.api.KeshigQD;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "keshigqd", name = "set-server", description = "Set build/deploy/test server")
public class SetupServer extends OsgiCommandSupport
{
    @Argument(index = 0, name = "id", description = "Server UUID", required = true, multiValued = false)
    String serverId;
    @Argument(index = 1, name = "name", description = "Server Name", required = true, multiValued = false)
    String serverName;
    @Argument(index = 2, name = "address", description = "Server Address", required = true, multiValued = false)
    String serverAddress;
    @Argument(index = 3, name = "type", description = "Server Type", required = true, multiValued = false)
    String serverType;
    @Argument(index = 4, name = "description", description = "Description", required = true, multiValued = false)
    String description;
    private KeshigQD keshig;
    
    public KeshigQD getKeshig() {
        return this.keshig;
    }
    
    public void setKeshig(final KeshigQD keshig) {
        this.keshig = keshig;
    }
    
    protected Object doExecute() throws Exception {
        ServerType type;
        if (this.serverType.equalsIgnoreCase("test")) {
            type = ServerType.TEST_SERVER;
        }
        else if (this.serverType.equalsIgnoreCase("deploy")) {
            type = ServerType.DEPLOY_SERVER;
        }
        else {
            if (!this.serverType.equalsIgnoreCase("build")) {
                throw new Exception("\nInvalid server type :\n Enter one of the following:\nTest\nDeploy\nBuild");
            }
            type = ServerType.BUILD_SERVER;
        }
        final Server server = new Server(this.serverId, this.serverName, this.serverAddress, type, this.description);
        this.keshig.addServer(server);
        System.out.println(String.format("Server (%s) added", server.toString()));
        return null;
    }
}
