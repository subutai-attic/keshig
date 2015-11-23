package io.subutai.plugin.keshig.cli.server;

import io.subutai.plugin.keshig.api.Keshig;
import io.subutai.plugin.keshig.api.entity.Server;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import java.util.List;

@Command(scope = "keshigqd", name = "list-server", description = "List servers")
public class ListServers extends OsgiCommandSupport {
    private Keshig keshig;

    public Keshig getKeshig() {
        return this.keshig;
    }

    public void setKeshig(final Keshig keshig) {
        this.keshig = keshig;
    }

    protected Object doExecute() throws Exception {
        final List<Server> serverList = (List<Server>) this.keshig.getServers();
        for (Server server : serverList) {
            System.out.println(String.format("ID:%s\tName:%s\tAddress:%s\tType:%s\tDescription:%s\t",
                    server.getServerId(), server.getServerName(), server.getServerAddress(), server.getType().toString(), server.getDescription()));
        }
        return null;
    }
}
