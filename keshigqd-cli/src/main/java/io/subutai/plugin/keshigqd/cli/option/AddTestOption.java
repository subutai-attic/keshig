
package io.subutai.plugin.keshigqd.cli.option;

import java.util.Iterator;
import io.subutai.plugin.keshigqd.api.entity.OperationType;
import java.util.List;
import java.util.Arrays;
import io.subutai.plugin.keshigqd.api.entity.options.TestOption;
import io.subutai.plugin.keshigqd.api.KeshigQD;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "keshigqd", name = "add-test", description = "add test option")
public class AddTestOption extends OsgiCommandSupport
{
    @Argument(index = 0, name = "name", required = true, description = "option name")
    private String name;
    @Argument(index = 1, name = "all", required = false, description = "run all playbooks")
    private String all;
    @Argument(index = 2, name = "hosts", required = true, description = "target IPs")
    private String hosts;
    @Argument(index = 3, name = "playbooks", required = true, description = "target playbooks")
    private String playbooks;
    @Argument(index = 4, name = "active", required = true, description = "option is active")
    private String active;
    @Argument(index = 5, name = "timeout", required = true, description = "option timeout")
    private int timeOut;
    private KeshigQD keshig;
    
    public KeshigQD getKeshig() {
        return this.keshig;
    }
    
    public void setKeshig(final KeshigQD keshig) {
        this.keshig = keshig;
    }
    
    protected Object doExecute() throws Exception {
        final TestOption testOption = new TestOption();
        testOption.setTargetIps((List)Arrays.asList(this.hosts.split(",")));
        testOption.setName(this.name);
        testOption.setTimeOut(this.timeOut);
        if (this.active.equalsIgnoreCase("true")) {
            testOption.setIsActive(true);
            final List<TestOption> list = (List<TestOption>)this.keshig.allOptionsByType(testOption.getType());
            for (final TestOption opt : list) {
                if (opt.isActive()) {
                    System.out.println(String.format("Deactivating option (%s)", list.toString()));
                    this.keshig.deactivate(opt.getName(), OperationType.TEST);
                }
            }
        }
        else {
            if (!this.active.equalsIgnoreCase("false")) {
                throw new Exception("Invalid arg for active @param true/false");
            }
            testOption.setIsActive(false);
        }
        if (this.all.equalsIgnoreCase("true")) {
            testOption.setAll(true);
        }
        else {
            testOption.setAll(false);
            testOption.setPlaybooks((List)Arrays.asList(this.playbooks.split(",")));
        }
        this.keshig.saveOption(testOption, OperationType.TEST);
        return null;
    }
}
