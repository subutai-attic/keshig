
package io.subutai.plugin.keshig.cli.option;

import com.google.common.base.Strings;
import io.subutai.plugin.keshig.api.Keshig;
import io.subutai.plugin.keshig.api.entity.options.DeployOption;
import io.subutai.plugin.keshig.api.entity.options.TestOption;
import io.subutai.plugin.keshig.api.entity.options.BuildOption;
import io.subutai.plugin.keshig.api.entity.options.CloneOption;
import java.util.List;
import io.subutai.plugin.keshig.api.entity.OperationType;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "keshigqd", name = "list-option", description = "list clone/build/deploy/test option")
public class ListOptions extends OsgiCommandSupport
{
    @Argument(index = 0, name = "type", description = "list options by type")
    String type;
    private Keshig keshig;
    
    public Keshig getKeshig() {
        return this.keshig;
    }
    
    public void setKeshig(final Keshig keshig) {
        this.keshig = keshig;
    }
    
    protected Object doExecute() throws Exception {
        if (this.type.equalsIgnoreCase("all")) {
            final List<CloneOption> co = (List<CloneOption>)this.keshig.allOptionsByType(OperationType.CLONE);
            this.printCloneOption(co);
            final List<BuildOption> bo = (List<BuildOption>)this.keshig.allOptionsByType(OperationType.BUILD);
            this.printBuildOption(bo);
            final List<TestOption> to = (List<TestOption>)this.keshig.allOptionsByType(OperationType.TEST);
            this.printTestOption(to);
            final List<DeployOption> dop = (List<DeployOption>)this.keshig.allOptionsByType(OperationType.DEPLOY);
            this.printDeployOption(dop);
            return null;
        }
        switch (OperationType.valueOf(this.type.toUpperCase())) {
            case CLONE: {
                final List<CloneOption> list = (List<CloneOption>)this.keshig.allOptionsByType(OperationType.CLONE);
                this.printCloneOption(list);
                break;
            }
            case BUILD: {
                final List<BuildOption> buildOptions = (List<BuildOption>)this.keshig.allOptionsByType(OperationType.BUILD);
                this.printBuildOption(buildOptions);
                break;
            }
            case DEPLOY: {
                final List<DeployOption> deployOptions = (List<DeployOption>)this.keshig.allOptionsByType(OperationType.DEPLOY);
                this.printDeployOption(deployOptions);
                break;
            }
            case TEST: {
                final List<TestOption> testOptions = (List<TestOption>)this.keshig.allOptionsByType(OperationType.TEST);
                this.printTestOption(testOptions);
                break;
            }
        }
        return null;
    }
    
    public void printCloneOption(final List<CloneOption> cloneOptions) {
        System.out.format("%62s\n", "*******");
        System.out.format("%62s\n", "*CLONE*");
        System.out.format("%62s\n", "*******");
        System.out.println(Strings.repeat("-", 114));
        System.out.format("|%19s|%50s|%12s|%7s|%9s|%10s|\n", "NAME", "URL", "BRANCH", "ACTIVE", "TYPE", "TIMEOUT");
        System.out.println(Strings.repeat("-", 114));
        for (final CloneOption option : cloneOptions) {
            System.out.format("|%15s|%50s|%12s|%7s|%9s|%10s|\n", option.getName(), option.getUrl(), option.getBranch(), String.valueOf(option.isActive()), option.getType().toString(), option.getTimeOut());
            System.out.println(Strings.repeat("-", 114));
        }
    }
    
    public void printBuildOption(final List<BuildOption> buildOptions) {
        System.out.format("%62s\n", "*******");
        System.out.format("%62s\n", "*BUILD*");
        System.out.format("%62s\n", "*******");
        System.out.println(Strings.repeat("-", 114));
        System.out.format("|%19s|%27s|%27s|%10s|%9s|%10s|\n", "NAME", "RUN TESTS", "CLEAN INSTALL", "ACTIVE", "TYPE", "TIMEOUT");
        System.out.println(Strings.repeat("-", 114));
        for (final BuildOption option : buildOptions) {
            System.out.format("|%19s|%27s|%27s|%10s|%9s|%10s|\n", option.getName(), String.valueOf(option.isRunTests()), String.valueOf(option.isCleanInstall()), String.valueOf(option.isActive()), option.getType().toString(), option.getTimeOut());
            System.out.println(Strings.repeat("-", 114));
        }
    }
    
    public void printDeployOption(final List<DeployOption> deployOptions) {
        System.out.format("%62s\n", "********");
        System.out.format("%62s\n", "*DEPLOY*");
        System.out.format("%62s\n", "********");
        System.out.println(Strings.repeat("-", 114));
        System.out.format("|%19s|%25s|%15s|%19s|%10s|%9s|%10s|\n", "NAME", "BUILD NAME", "# OF PEERS", "# OF RHS", "TYPE", "ACTIVE", "TIMEOUT");
        System.out.println(Strings.repeat("-", 114));
        for (final DeployOption option : deployOptions) {
            System.out.format("|%19s|%25s|%15s|%19s|%10s|%9s|%10s|\n", option.getName(), option.getBuildName(), option.getNumberOfPeers(), option.getNumberOfRhsPerPeer(), option.getType(), String.valueOf(option.isActive()), option.getTimeOut());
            System.out.println(Strings.repeat("-", 114));
        }
    }
    
    public void printTestOption(final List<TestOption> testOptions) {
        System.out.format("%62s\n", "*******");
        System.out.format("%62s\n", "* TEST*");
        System.out.format("%62s\n", "*******");
        System.out.println(Strings.repeat("-", 121));
        System.out.format("|%15s|%5s|%35s|%30s|%10s|%9s|%10s|\n", "NAME", "ALL", "PLAYBOOKS", "HOSTS", "TYPE", "ACTIVE", "TIMEOUT");
        System.out.println(Strings.repeat("-", 121));
        for (final TestOption testOption : testOptions) {
            System.out.format("|%15s|%5s|%35s|%19s|%10s|%9s|%10s|\n", testOption.getName(), String.valueOf(testOption.isAll()), (testOption.getPlaybooks() == null) ? "" : testOption.getPlaybooks().toString(), testOption.getTargetIps().toString(), testOption.getType(), testOption.isActive(), testOption.getTimeOut());
            System.out.println(Strings.repeat("-", 121));
        }
    }
}
