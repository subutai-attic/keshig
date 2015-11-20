package io.subutai.plugin.keshigqd.cli.builds;

import io.subutai.plugin.keshigqd.api.KeshigQD;
import io.subutai.plugin.keshigqd.api.entity.Build;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import java.util.List;

@Command(scope = "keshigqd", name = "list-builds", description = "list builds")
public class ListBuilds extends OsgiCommandSupport {
    private KeshigQD keshig;

    public KeshigQD getKeshig() {
        return this.keshig;
    }

    public void setKeshig(final KeshigQD keshig) {
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
