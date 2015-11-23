package io.subutai.plugin.keshig.cli.builds;

import io.subutai.plugin.keshig.api.Keshig;
import io.subutai.plugin.keshig.api.entity.Build;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import java.util.List;

@Command(scope = "keshigqd", name = "list-builds", description = "list builds")
public class ListBuilds extends OsgiCommandSupport {
    private Keshig keshig;

    public Keshig getKeshig() {
        return this.keshig;
    }

    public void setKeshig(final Keshig keshig) {
        this.keshig = keshig;
    }

    protected Object doExecute() throws Exception {
        final List<Build> buildList = (List<Build>) this.keshig.getBuilds();
        for (final Build build : buildList) {
            System.out.println(String.format("Build ID:(%s)\t Build Name:(%s)\t Build Version:(%s)\t Build Date:(%s)", build.getId(), build.getName(), build.getVersion(), build.getDate().toString()));
        }
        return null;
    }
}
