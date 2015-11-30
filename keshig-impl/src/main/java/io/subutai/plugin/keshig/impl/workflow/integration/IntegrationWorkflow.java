package io.subutai.plugin.keshig.impl.workflow.integration;

import io.subutai.common.command.RequestBuilder;
import io.subutai.common.tracker.TrackerOperation;
import io.subutai.plugin.keshig.api.KeshigConfig;
import io.subutai.plugin.keshig.api.Profile;
import io.subutai.plugin.keshig.api.entity.Command;
import io.subutai.plugin.keshig.api.entity.History;
import io.subutai.plugin.keshig.api.entity.Server;
import io.subutai.plugin.keshig.api.entity.options.BuildOption;
import io.subutai.plugin.keshig.api.entity.options.CloneOption;
import io.subutai.plugin.keshig.api.entity.options.DeployOption;
import io.subutai.plugin.keshig.api.entity.options.TestOption;
import io.subutai.plugin.keshig.impl.KeshigImpl;
import io.subutai.plugin.keshig.impl.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.subutai.plugin.keshig.api.entity.OperationType.*;
import static io.subutai.plugin.keshig.api.entity.ServerType.*;

public class IntegrationWorkflow implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger((Class) IntegrationWorkflow.class);

    private final KeshigImpl keshigQD;
    private final TrackerOperation operationTracker;

    private final Profile profile;

    private CloneOption cloneOption;
    private BuildOption buildOption;
    private DeployOption deployOption;
    private TestOption testOption;

    private Server cloneServer;
    private Server deployServer;
    private Server testServer;


    public IntegrationWorkflow(final KeshigImpl keshigQD) {

        this.keshigQD = keshigQD;
        operationTracker = keshigQD.getTracker().createTrackerOperation(KeshigConfig.PRODUCT_KEY,
            String.format("Creating %s tracker object for Integration Workflow", KeshigConfig.PRODUCT_KEY));
        //get default options
        cloneOption = (CloneOption) keshigQD.getActiveOption(CLONE);
        buildOption = (BuildOption) keshigQD.getActiveOption(BUILD);
        deployOption = (DeployOption) keshigQD.getActiveOption(DEPLOY);
        testOption = (TestOption) keshigQD.getActiveOption(TEST);
        //get default servers
        cloneServer = keshigQD.getServers(BUILD_SERVER).get(0);
        deployServer = keshigQD.getServers(DEPLOY_SERVER).get(0);
        testServer = keshigQD.getServers(TEST_SERVER).get(0);
        profile = null;
    }

    public IntegrationWorkflow(final KeshigImpl keshigQD, Profile profile) {

        operationTracker = keshigQD.getTracker().createTrackerOperation(KeshigConfig.PRODUCT_KEY,
                String.format("Creating %s tracker object for Integration Workflow with profile %s ",
                        KeshigConfig.PRODUCT_KEY,
                        profile.getName()));

        this.profile = profile;
        this.keshigQD = keshigQD;

        //profile set options
        cloneOption = (CloneOption) keshigQD.getOption(profile.getCloneOption(), CLONE);
        buildOption = (BuildOption) keshigQD.getOption(profile.getBuildOption(), BUILD);
        deployOption = (DeployOption) keshigQD.getOption(profile.getDeployOption(), DEPLOY);
        testOption = (TestOption) keshigQD.getOption(profile.getTestOption(), TEST);

        //profile set servers
        cloneServer = keshigQD.getServer(profile.getCbServer());
        deployServer = keshigQD.getServer(profile.getDeployServer());
        testServer = keshigQD.getServer(profile.getTestServer());


    }

    @Override
    public void run() {

        start();
    }

    private void start() {

        if (!init()) {
            IntegrationWorkflow.LOG.info("Init step failed");
            operationTracker.addLogFailed("Init step failed");
            return;
        }

        operationTracker.addLog("Init step succeeded");

        if (!fetch()) {
            IntegrationWorkflow.LOG.info("Cloning repo step failed");
            operationTracker.addLogFailed("Clone step failed");
            return;
        }

        operationTracker.addLog("Clone step succeeded");

        if (!build()) {
            IntegrationWorkflow.LOG.info("Build step failed");
            operationTracker.addLogFailed("Build step failed");
            return;
        }

        operationTracker.addLog("Build step succeeded");

        if (!deploy()) {
            IntegrationWorkflow.LOG.info("Deploy step failed");
            operationTracker.addLogFailed("Deploy step failed");
            return;
        }

        operationTracker.addLog("Deploy step succeeded");

        if (!test()) {
            IntegrationWorkflow.LOG.info("Test step failed");
            operationTracker.addLogFailed("Test step failed");
        }

        operationTracker.addLog("Test step succeeded");

        operationTracker.addLogDone("Successfully completed");

    }


    public boolean init() {

        operationTracker.addLog("Starting Keshig Integration workflow\n");

        return true;
    }

    public boolean fetch() {

        operationTracker.addLog("Starting clone operation");
        final CloneOperationHandler cloneOperation = new CloneOperationHandler(cloneServer.getServerId(),cloneOption,keshigQD);
        cloneOperation.run();

        return !keshigQD.getTracker().getTrackerOperation(KeshigConfig.PRODUCT_KEY, cloneOperation.getTrackerId()).getState().toString().equalsIgnoreCase("FAILED");
    }

    public boolean build() {

        operationTracker.addLog("Starting build operation");
        final BuildOperationHandler operationHandler = new BuildOperationHandler(cloneServer.getServerId(),buildOption,keshigQD);

        operationHandler.run();

        return !keshigQD.getTracker().getTrackerOperation(KeshigConfig.PRODUCT_KEY, operationHandler.getTrackerId()).getState().toString().equalsIgnoreCase("FAILED");
    }

    public boolean deploy() {

        operationTracker.addLog("Starting deploy");

        if (deployOption.getBuildName().equalsIgnoreCase("latest")) {
            IntegrationWorkflow.LOG.info(String.format("Using build name :%s", keshigQD.getLatestBuild().getId()));
            deployOption.setBuildName(keshigQD.getLatestBuild().getId());
        }

        final DeployOperationHandler operationHandler = new DeployOperationHandler(deployServer.getServerId(), deployOption, keshigQD);

        operationHandler.run();

        if (keshigQD.getTracker().getTrackerOperation(KeshigConfig.PRODUCT_KEY, operationHandler.getTrackerId()).getState().toString().equalsIgnoreCase("FAILED")) {
            return false;
        }
        if (keshigQD.getTracker().getTrackerOperation(KeshigConfig.PRODUCT_KEY, operationHandler.getTrackerId()).getState().toString().equalsIgnoreCase("SUCCEEDED")) {
            extractServers(keshigQD.getTracker().getTrackerOperation(KeshigConfig.PRODUCT_KEY, operationHandler.getTrackerId()).getLog(), deployOption.getBuildName());
        }
        return true;
    }

    public boolean test() {
        operationTracker.addLog("Starting tests");
        if (testOption.getTargetIps().toString().toLowerCase().contains("latest")) {
            testOption.getTargetIps().clear();
            final List<Server> serverList = keshigQD.getServers(PEER_SERVER);

            for (final Server server : serverList) {
                if (server.getDescription().contains(keshigQD.getLatestBuild().getId())) {
                    testOption.getTargetIps().add(server.getServerAddress());
                }
            }
        }

        final TestOperationHandler operationHandler = new TestOperationHandler(testServer.getServerId(), testOption, keshigQD);

        operationHandler.run();

        if (keshigQD.getTracker().getTrackerOperation(KeshigConfig.PRODUCT_KEY, operationHandler.getTrackerId()).getState().toString().equalsIgnoreCase("FAILED")) {

            return false;
        }
        return true;
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
            final Server server = new Server(s[1].trim(), s[0].trim(), s[1].trim(), PEER_SERVER, String.format("Auto-deployed Subutai Management Server:%s", buildName));
            IntegrationWorkflow.LOG.info(String.format("Saving server info: %s", server.toString()));
            try {
                keshigQD.addServer(server);
            } catch (Exception e) {
                e.printStackTrace();
            }
            found = true;
        }
        if (!found) {
            operationTracker.addLog("Could not find deployed managements' ip addresses ");
            IntegrationWorkflow.LOG.error("No match found");
        }
    }

}
