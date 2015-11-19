// 
// Decompiled by Procyon v0.5.30
// 

package io.subutai.plugin.keshigqd.cli.option;

import java.util.Iterator;
import java.util.List;
import io.subutai.plugin.keshigqd.api.entity.OperationType;
import io.subutai.plugin.keshigqd.api.entity.options.DeployOption;
import io.subutai.plugin.keshigqd.api.KeshigQD;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "keshigqd", name = "add-deploy", description = "deploy option")
public class AddDelpoyOption extends OsgiCommandSupport
{
    @Argument(index = 0, name = "name", required = true, description = "option name")
    private String name;
    @Argument(index = 1, name = "peer", required = true, description = "number of peers to be deployed")
    private int numberOfPeers;
    @Argument(index = 2, name = "rh", required = true, description = "number of rh to be deployed per peer")
    private int numberOfRhsPerPeer;
    @Argument(index = 3, name = "build", required = true, description = "build to deploy")
    private String buildName;
    @Argument(index = 4, name = "active", required = true, description = "active")
    private String active;
    @Argument(index = 5, name = "timeout", required = true, description = "timeout")
    private int timeOut;
    private KeshigQD keshig;
    
    public KeshigQD getKeshig() {
        return this.keshig;
    }
    
    public void setKeshig(final KeshigQD keshig) {
        this.keshig = keshig;
    }
    
    protected Object doExecute() throws Exception {
        final DeployOption deployOption = new DeployOption();
        deployOption.setName(this.name);
        deployOption.setTimeOut(this.timeOut);
        deployOption.setNumberOfPeers(this.numberOfPeers);
        deployOption.setNumberOfRhsPerPeer(this.numberOfRhsPerPeer);
        deployOption.setBuildName(this.buildName);
        if (this.active.equalsIgnoreCase("true")) {
            final List<DeployOption> list = (List<DeployOption>)this.keshig.allOptionsByType(deployOption.getType());
            for (final DeployOption opt : list) {
                if (opt.isActive()) {
                    System.out.println(String.format("Deactivating option (%s)", list.toString()));
                    this.keshig.deactivate(opt.getName(), OperationType.DEPLOY);
                }
            }
            deployOption.setIsActive(true);
        }
        else {
            if (!this.active.equalsIgnoreCase("false")) {
                throw new Exception("Invalid arg for active @param true/false");
            }
            deployOption.setIsActive(false);
        }
        this.keshig.saveOption(deployOption, OperationType.DEPLOY);
        return null;
    }
}
