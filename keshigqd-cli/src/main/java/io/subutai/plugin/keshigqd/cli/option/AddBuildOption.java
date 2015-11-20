package io.subutai.plugin.keshigqd.cli.option;

import io.subutai.plugin.keshigqd.api.KeshigQD;
import io.subutai.plugin.keshigqd.api.entity.OperationType;
import io.subutai.plugin.keshigqd.api.entity.options.BuildOption;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import java.util.List;

@Command(scope = "keshigqd", name = "add-build", description = "add build option")
public class AddBuildOption extends OsgiCommandSupport {
    @Argument(index = 0, name = "name", required = true, description = "build option name")
    private String name;
    @Argument(index = 1, name = "clean-install", required = true, description = "clean install")
    private String cleanInstall;
    @Argument(index = 2, name = "run-tests", required = true, description = "run tests")
    private String runTests;
    @Argument(index = 3, name = "active", required = true, description = "active")
    private String active;
    @Argument(index = 4, name = "timeout", required = true, description = "timeout")
    private int timeout;
    private KeshigQD keshig;

    public KeshigQD getKeshig() {
        return this.keshig;
    }

    public void setKeshig(final KeshigQD keshig) {
        this.keshig = keshig;
    }

    protected Object doExecute() throws Exception {
        final BuildOption buildOption = new BuildOption();
        buildOption.setName(this.name);
        buildOption.setTimeOut(this.timeout);
        buildOption.setCleanInstall((boolean) Boolean.valueOf(this.cleanInstall));
        buildOption.setRunTests((boolean) Boolean.valueOf(this.runTests));
        if (this.active.equalsIgnoreCase("true")) {
            buildOption.setIsActive(true);
            final List<BuildOption> list = (List<BuildOption>) this.keshig.allOptionsByType(buildOption.getType());
            for (final BuildOption opt : list) {
                if (opt.isActive()) {
                    System.out.println(String.format("Deactivating option (%s)", list.toString()));
                    this.keshig.deactivate(opt.getName(), OperationType.DEPLOY);
                }
            }
        } else {
            if (!this.active.equalsIgnoreCase("false")) {
                throw new Exception("Invalid arg for active @param true/false");
            }
            buildOption.setIsActive(false);
        }
        this.keshig.saveOption(buildOption, OperationType.BUILD);
        return null;
    }
}
