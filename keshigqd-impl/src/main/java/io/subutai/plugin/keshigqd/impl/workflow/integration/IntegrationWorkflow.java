package io.subutai.plugin.keshigqd.impl.workflow.integration;

import io.subutai.common.command.RequestBuilder;
import io.subutai.common.tracker.TrackerOperation;
import io.subutai.plugin.keshigqd.api.KeshigQDConfig;
import io.subutai.plugin.keshigqd.api.entity.Command;
import io.subutai.plugin.keshigqd.api.entity.OperationType;
import io.subutai.plugin.keshigqd.api.entity.Server;
import io.subutai.plugin.keshigqd.api.entity.ServerType;
import io.subutai.plugin.keshigqd.api.entity.options.BuildOption;
import io.subutai.plugin.keshigqd.api.entity.options.CloneOption;
import io.subutai.plugin.keshigqd.api.entity.options.DeployOption;
import io.subutai.plugin.keshigqd.api.entity.options.TestOption;
import io.subutai.plugin.keshigqd.impl.KeshigQDImpl;
import io.subutai.plugin.keshigqd.impl.handler.OperationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntegrationWorkflow {
    private static final Logger LOG = LoggerFactory.getLogger((Class) IntegrationWorkflow.class);
    private final KeshigQDImpl keshigQD;
    private final TrackerOperation operationTracker;
    ;
    private CloneOption cloneOption;
    private BuildOption buildOption;
    private DeployOption deployOption;
    private TestOption testOption;
    private Server cloneServer;

    public IntegrationWorkflow(final KeshigQDImpl keshigQD) {
        this.keshigQD = keshigQD;
        this.operationTracker = keshigQD.getTracker().createTrackerOperation(KeshigQDConfig.PRODUCT_KEY, String.format("Creating %s tracker object for Integration Workflow", KeshigQDConfig.PRODUCT_KEY));
    }

    public void run() {
        if (!this.init()) {
            IntegrationWorkflow.LOG.info("Init step failed");
            this.operationTracker.addLogFailed("Init step failed");
            return;
        }
        this.operationTracker.addLog("Init step succeeded");
        if (!this.fetch()) {
            IntegrationWorkflow.LOG.info("Cloning repo step failed");
            this.operationTracker.addLogFailed("Clone step failed");
            return;
        }
        this.operationTracker.addLog("Clone step succeeded");
        if (!this.build()) {
            IntegrationWorkflow.LOG.info("Build step failed");
            this.operationTracker.addLogFailed("Build step failed");
            return;
        }
        this.operationTracker.addLog("Build step succeeded");
        if (!this.deploy()) {
            IntegrationWorkflow.LOG.info("Deploy step failed");
            this.operationTracker.addLogFailed("Deploy step failed");
            return;
        }
        this.operationTracker.addLog("Deploy step succeeded");
        if (!this.test()) {
            IntegrationWorkflow.LOG.info("Test step failed");
            this.operationTracker.addLogFailed("Test step failed");
        }
        this.operationTracker.addLog("Test step succeeded");
        this.operationTracker.addLogDone("Successfully completed");
    }

    public boolean init() {
        this.cloneOption = (CloneOption) this.keshigQD.getActiveOption(OperationType.CLONE);
        this.buildOption = (BuildOption) this.keshigQD.getActiveOption(OperationType.BUILD);
        this.deployOption = (DeployOption) this.keshigQD.getActiveOption(OperationType.DEPLOY);
        this.testOption = (TestOption) this.keshigQD.getActiveOption(OperationType.TEST);
        this.operationTracker.addLog("Starting Keshig Integration workflow\n");
        return true;
    }

    public boolean fetch() {
        this.cloneServer = this.keshigQD.getServers(ServerType.BUILD_SERVER).get(0);
        final OperationHandler cloneOperation = new OperationHandler(this.keshigQD, new RequestBuilder(Command.getCloneCommand()).withCmdArgs(this.cloneOption.getArgs()).withTimeout(this.cloneOption.getTimeOut()), OperationType.CLONE, this.cloneServer.getServerId());
        cloneOperation.run();

        return !this.keshigQD.getTracker().getTrackerOperation(KeshigQDConfig.PRODUCT_KEY, cloneOperation.getTrackerId()).getState().toString().equalsIgnoreCase("FAILED");
    }

    public boolean build() {
        this.cloneServer = this.keshigQD.getServers(ServerType.BUILD_SERVER).get(0);
        final OperationHandler operationHandler = new OperationHandler(this.keshigQD, new RequestBuilder(Command.getBuildCommand()).withCmdArgs(this.buildOption.getArgs()).withTimeout(this.buildOption.getTimeOut()), OperationType.BUILD, this.cloneServer.getServerId());
        operationHandler.run();
        return !this.keshigQD.getTracker().getTrackerOperation(KeshigQDConfig.PRODUCT_KEY, operationHandler.getTrackerId()).getState().toString().equalsIgnoreCase("FAILED");
    }

    public boolean deploy() {
        if (this.deployOption.getBuildName().equalsIgnoreCase("latest")) {
            IntegrationWorkflow.LOG.info(String.format("Using build name :%s", this.keshigQD.getLatestBuild().getId()));
            this.deployOption.setBuildName(this.keshigQD.getLatestBuild().getId());
        }
        final Server deployServer = this.keshigQD.getServers(ServerType.DEPLOY_SERVER).get(0);
        final OperationHandler operationHandler = new OperationHandler(this.keshigQD, new RequestBuilder(Command.getDeployCommand()).withCmdArgs(this.deployOption.getArgs()).withTimeout(this.deployOption.getTimeOut()).withRunAs("ubuntu"), OperationType.DEPLOY, deployServer.getServerId());
        operationHandler.run();
        
        if (this.keshigQD.getTracker().getTrackerOperation(KeshigQDConfig.PRODUCT_KEY, operationHandler.getTrackerId()).getState().toString().equalsIgnoreCase("FAILED")) {
            return false;
        }
        if (this.keshigQD.getTracker().getTrackerOperation(KeshigQDConfig.PRODUCT_KEY, operationHandler.getTrackerId()).getState().toString().equalsIgnoreCase("SUCCEEDED")) {
            this.extractServers(this.keshigQD.getTracker().getTrackerOperation(KeshigQDConfig.PRODUCT_KEY, operationHandler.getTrackerId()).getLog(), this.deployOption.getBuildName());
        }
        return true;
    }

    public boolean test() {
        if (this.testOption.getTargetIps().toString().contains("latest")) {
            this.testOption.getTargetIps().clear();
            final List<Server> serverList = this.keshigQD.getServers(ServerType.PEER_SERVER);
            for (final Server server : serverList) {
                if (server.getDescription().contains(this.keshigQD.getLatestBuild().getId())) {
                    this.testOption.getTargetIps().add(server.getServerAddress());
                }
            }
        }
        final Server testServer = this.keshigQD.getServers(ServerType.TEST_SERVER).get(0);
        final OperationHandler operationHandler = new OperationHandler(this.keshigQD, new RequestBuilder(Command.getTestComand()).withCmdArgs(this.testOption.getArgs()).withTimeout(this.testOption.getTimeOut()), OperationType.TEST, testServer.getServerId());
        operationHandler.run();

        return !this.keshigQD.getTracker().getTrackerOperation(KeshigQDConfig.PRODUCT_KEY, operationHandler.getTrackerId()).getState().toString().equalsIgnoreCase("FAILED");
    }

    private void extractServers(final String stdOut, final String buildName) {
        IntegrationWorkflow.LOG.info(String.format("Extracting server info: %s", stdOut));

        final Pattern pattern = Pattern.compile("management\\d=.*");
        final Matcher matcher = pattern.matcher(stdOut);

        boolean found = false;

        while (matcher.find()) {
            final String match = matcher.group();
            IntegrationWorkflow.LOG.info(String.format("Found Server address \"%s\" starting at index and ending at index ", match));
            final String[] s = match.split("=");
            final Server server = new Server(s[1].trim(), s[0].trim(), s[1].trim(), ServerType.PEER_SERVER, String.format("Auto-deployed Subutai Management Server:%s", buildName));
            IntegrationWorkflow.LOG.info(String.format("Saving server info: %s", server.toString()));
            try {
                this.keshigQD.addServer(server);
            } catch (Exception e) {
                e.printStackTrace();
            }
            found = true;
        }
        if (!found) {
            this.operationTracker.addLog("Could not find deployed managements' ip addresses ");
            IntegrationWorkflow.LOG.error("No match found");
        }
    }
}
