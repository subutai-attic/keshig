
package io.subutai.plugin.keshigqd.cli.option;

import java.util.Iterator;
import java.util.List;
import io.subutai.plugin.keshigqd.api.entity.OperationType;
import io.subutai.plugin.keshigqd.api.entity.options.CloneOption;
import io.subutai.plugin.keshigqd.api.KeshigQD;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "keshigqd", name = "add-clone", description = "add clone/build/deploy/test option")
public class AddCloneOption extends OsgiCommandSupport
{
    @Argument(index = 0, name = "name", description = "clone option name")
    String name;
    @Argument(index = 1, name = "url", description = "clone url")
    String url;
    @Argument(index = 2, name = "branch", description = "clone branch")
    String branch;
    @Argument(index = 3, name = "active", description = "option is active")
    String active;
    @Argument(index = 4, name = "timeout", description = "option timeout")
    int timeOut;
    private KeshigQD keshig;
    
    public KeshigQD getKeshig() {
        return this.keshig;
    }
    
    public void setKeshig(final KeshigQD keshig) {
        this.keshig = keshig;
    }
    
    protected Object doExecute() throws Exception {
        if (this.active.equalsIgnoreCase("true")) {
            final CloneOption cloneOption = new CloneOption(this.name, this.timeOut, true, "", this.branch, this.url);
            final List<CloneOption> list = (List<CloneOption>)this.keshig.allOptionsByType(OperationType.CLONE);
            System.out.println(String.format("Found (%s)", list.toString()));
            for (final CloneOption opt : list) {
                if (opt.isActive()) {
                    System.out.println(String.format("Deactivating option (%s)", list.toString()));
                    this.keshig.deactivate(opt.getName(), OperationType.CLONE);
                }
            }
            this.keshig.saveOption(cloneOption, OperationType.CLONE);
        }
        else {
            if (!this.active.equalsIgnoreCase("false")) {
                throw new Exception("Invalid arg for active @param true/false");
            }
            final CloneOption cloneOption = new CloneOption(this.name, this.timeOut, false, "", this.branch, this.url);
            this.keshig.saveOption(cloneOption, OperationType.CLONE);
        }
        return null;
    }
}
