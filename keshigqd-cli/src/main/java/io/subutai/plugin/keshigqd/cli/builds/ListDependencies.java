

package io.subutai.plugin.keshigqd.cli.builds;

import com.google.common.base.Strings;
import io.subutai.plugin.keshigqd.api.KeshigQD;
import io.subutai.plugin.keshigqd.api.entity.Dependency;
import io.subutai.plugin.keshigqd.api.entity.ServerType;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Command(scope = "keshigqd", name = "list-packages", description = "list installed packages on targer server")
public class ListDependencies extends OsgiCommandSupport {

    @Argument(index = 0, name = "target", description = "all - shows all installed packages on each server\ntarget server id - shows all installed packages on target server\nbuild/deploy/test- shows all required packages by server type")
    private String arg;

    private KeshigQD keshig;

    public KeshigQD getKeshig() {
        return this.keshig;
    }

    public void setKeshig(final KeshigQD keshig) {
        this.keshig = keshig;
    }

    protected Object doExecute() throws Exception {

        final String arg = this.arg;
        switch (arg) {
            case "all": {
                this.printable(this.keshig.getAllPackages());
                break;
            }
            case "build": {
                this.printable(this.keshig.getRequiredPackages(ServerType.BUILD_SERVER));
                break;
            }
            case "deploy": {
                this.printable(this.keshig.getRequiredPackages(ServerType.DEPLOY_SERVER));
                break;
            }
            case "test": {
                this.printable(this.keshig.getRequiredPackages(ServerType.TEST_SERVER));
                break;
            }
            default: {
                final Pattern pattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
                final Matcher matcher = pattern.matcher(this.arg.toLowerCase());
                if (matcher.matches()) {
                    this.printable(this.keshig.getPackages(this.arg));
                    break;
                }
                System.out.println("Unknown argument provided. Please refer to help");
                break;
            }
        }
        return null;
    }

    private void printable(final List<Dependency> dependencyList) {
        System.out.format("%50s%10s%5s%32s", "Name", "Version", "Arch", "Description\n");
        System.out.println(Strings.repeat("-", 67));
        for (final Dependency pkg : dependencyList) {
            System.out.format("%50s%10s%5s%32s\n", pkg.getName(), pkg.getVersion(), pkg.getArch(), pkg.getDescription());
        }
    }

    private void printable(final Map<String, List<Dependency>> pkgs) {
        for (final Map.Entry<String, List<Dependency>> entry : pkgs.entrySet()) {
            System.out.println(String.format("Server: %s", entry.getKey()));
            this.printable(entry.getValue());
        }
    }
}
