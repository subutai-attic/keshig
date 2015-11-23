package io.subutai.plugin.keshig.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.subutai.common.command.CommandException;
import io.subutai.common.command.CommandResult;
import io.subutai.common.command.RequestBuilder;
import io.subutai.common.mdc.SubutaiExecutors;
import io.subutai.common.peer.HostNotFoundException;
import io.subutai.common.peer.ResourceHost;
import io.subutai.common.tracker.TrackerOperation;
import io.subutai.core.environment.api.EnvironmentManager;
import io.subutai.core.network.api.NetworkManager;
import io.subutai.core.peer.api.PeerManager;
import io.subutai.core.tracker.api.Tracker;
import io.subutai.plugin.common.api.PluginDAO;
import io.subutai.plugin.keshig.api.Keshig;
import io.subutai.plugin.keshig.api.Profile;
import io.subutai.plugin.keshig.api.entity.*;
import io.subutai.plugin.keshig.api.entity.options.BuildOption;
import io.subutai.plugin.keshig.api.entity.options.CloneOption;
import io.subutai.plugin.keshig.api.entity.options.DeployOption;
import io.subutai.plugin.keshig.api.entity.options.TestOption;
import io.subutai.plugin.keshig.impl.handler.OperationHandler;
import io.subutai.plugin.keshig.impl.workflow.integration.IntegrationWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static io.subutai.plugin.keshig.api.KeshigQDConfig.*;
import static io.subutai.plugin.keshig.api.KeshigQDConfig.PRODUCT_HISTORY;

public class KeshigImpl implements Keshig {

    private static final Logger LOG = LoggerFactory.getLogger(KeshigImpl.class.getName());

    //@formatter:off
    private Tracker             tracker;
    private ExecutorService     executor;
    private EnvironmentManager environmentManager;
    private PluginDAO           pluginDAO;
    private PeerManager         peerManager;
    private NetworkManager      networkManager;
    private TrackerOperation    trackerOperation;
    //@formatter:on

    public KeshigImpl(final PluginDAO pluginDAO) {
        this.pluginDAO = pluginDAO;
    }

    public void init() {
        this.executor = SubutaiExecutors.newCachedThreadPool();
    }

    public void destroy() {
        this.executor.shutdown();
    }

    public void addServer(final Server server) throws Exception {

        Preconditions.checkNotNull(server);

        if (!this.getPluginDAO().saveInfo(PRODUCT_KEY, server.getServerId(), server)) {
            throw new Exception("Could not save server info");
        }
    }

    public void removeServer(final String serverId) {

        this.pluginDAO.deleteInfo(PRODUCT_KEY, serverId);

    }

    public List<Server> getServers(final ServerType type) {

        final List<Server> servers = this.getServers();
        final List<Server> typedServers = new ArrayList<Server>();

        LOG.info(String.format("Found servers: %s", servers.toString()));

        for (final Server server : servers) {

            LOG.info(String.format("Server (%s) with type: (%s)", server.toString(), server.getType()));

            if (server.getType().equals(type)) {
                typedServers.add(server);
            }
        }
        return typedServers;
    }

    public Server getServer(final String serverId) {

        Preconditions.checkNotNull(serverId);
        return (Server) this.pluginDAO.getInfo(PRODUCT_KEY, serverId, Server.class);
    }

    public List<Server> getServers() {
        return (List<Server>) this.pluginDAO.getInfo(PRODUCT_KEY, Server.class);
    }

    @Override
    public void updateServer(Server server) throws Exception {
        addServer(server);
    }

    @Override
    public void setServer(String serverId, String serverType, String serverName) {

        ResourceHost resourceHost;

        try {
            resourceHost = this.getPeerManager().getLocalPeer().getResourceHostById(serverId);
            Server server = new Server(serverId, serverName, resourceHost.getInterfaceByName("br-int").getIp(),
                    ServerType.valueOf(serverType.toUpperCase()), resourceHost.getHostname());
            addServer(server);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Server getServer(final ServerType serverType) {

        final List<Server> serverList = this.getServers(serverType);

        for (final Server server : serverList) {
            if (server.getType().equals(serverType)) {
                return server;
            }
        }
        return null;
    }

    public List<Profile> getAllProfiles() {
        return this.pluginDAO.getInfo(PROFILE, Profile.class);
    }

    public void addProfile(final Profile profile) throws Exception {

        Preconditions.checkNotNull(profile);

        if (!this.getPluginDAO().saveInfo(PROFILE, profile.getName(), profile)) {

            throw new Exception("Could not save server info");

        }
    }

    public void deleteProfile(final String profileName) {

        this.pluginDAO.deleteInfo(PROFILE, profileName);

    }

    public void updateProfile(final Profile profile) {

        try {

            addProfile(profile);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public Profile getProfile(final String profileName) {

        return this.pluginDAO.getInfo(PROFILE, profileName, Profile.class);

    }

    public List<Build> getBuilds() {

        final Server buildServer = this.getServer(DEPLOY_SERVER);
        if (buildServer == null) {
            KeshigImpl.LOG.error("Failed to obtain build server");
            return null;
        }
        try {
            final ResourceHost buildHost = this.getPeerManager().getLocalPeer().getResourceHostById(buildServer.getServerId());
            final CommandResult result = buildHost.execute(new RequestBuilder(Command.getDeployCommand())
                    .withCmdArgs(Lists.newArrayList("-l", "list"))
                    .withTimeout(30));

            if (result.hasSucceeded()) {
                return this.parseBuilds(result.getStdOut());
            }

            return null;
        } catch (CommandException | HostNotFoundException e) {

            e.printStackTrace();
            return null;
        }
    }

    public Build getLatestBuild() {

        final List<Build> builds = this.getBuilds();
        return builds.get(builds.size() - 1);

    }

    private List<Build> parseBuilds(final String stdout) {

        final List<Build> list = new ArrayList<Build>();

        final String[] split = stdout.split(System.getProperty("line.separator"));

        for (final String line : split) {
            final String[] build = line.split("_");
            if (build.length == 3) {
                final Date date = new Date(Long.valueOf(build[2]) * 1000L);
                LOG.info("Adding following build  : %s ", line);
                list.add(new Build(line, build[0], build[1], date));
            }
        }
        return list;
    }

    public UUID deploy(final RequestBuilder requestBuilder, final String serverId) {

        final OperationHandler operationHandler = new OperationHandler(this, requestBuilder, DEPLOY, serverId);
        this.executor.execute(operationHandler);
        return operationHandler.getTrackerId();

    }

    public UUID test(final RequestBuilder requestBuilder, final String serverId) {

        final OperationHandler operationHandler = new OperationHandler(this, requestBuilder, TEST, serverId);
        this.executor.execute(operationHandler);
        return operationHandler.getTrackerId();

    }

    public UUID build(final RequestBuilder requestBuilder, final String serverId) {

        final OperationHandler operationHandler = new OperationHandler(this, requestBuilder, BUILD, serverId);
        this.executor.execute(operationHandler);
        return operationHandler.getTrackerId();

    }

    public UUID clone(final RequestBuilder requestBuilder, final String serverId) {

        final OperationHandler cloneOperation = new OperationHandler(this, requestBuilder, CLONE, serverId);
        this.executor.execute(cloneOperation);
        return cloneOperation.getTrackerId();

    }

    public void runDefaults() {

        final IntegrationWorkflow integrationWorkflow = new IntegrationWorkflow(this);

        this.executor.execute(integrationWorkflow);

    }

    @Override
    public void runOption(String optionName, String optionType) {

        OperationType type = OperationType.valueOf(optionType.toUpperCase());

        switch (type) {
            
            case CLONE: {
                final CloneOption cloneOption = (CloneOption) getOption(optionName, OperationType.valueOf(optionType.toUpperCase()));

                break;
            }
            case BUILD: {
                final BuildOption buildOption = (BuildOption) getOption(optionName, OperationType.valueOf(optionType.toUpperCase()));
                break;
            }
            case DEPLOY: {
                final DeployOption deployOption = (DeployOption) getOption(optionName, OperationType.valueOf(optionType.toUpperCase()));
                break;
            }
            case TEST: {
                final TestOption testOption = (TestOption) getOption(optionName, OperationType.valueOf(optionType.toUpperCase()));
                break;
            }
        }
    }

    @Override
    public List<History> listHistory() {
        return this.pluginDAO.getInfo(PRODUCT_HISTORY, History.class);
    }

    @Override
    public History getHistory(String historyId) {
        return this.pluginDAO.getInfo(PRODUCT_HISTORY, historyId, History.class);
    }

    @Override
    public List<Profile> listProfiles() {
        return this.pluginDAO.getInfo(PROFILE, Profile.class);
    }

    private void addToList(final String k, final String v, final List<String> arg) {
        arg.add(k);
        arg.add(v);
    }

    public void saveOption(final Object option, final OperationType type) {

        switch (type) {
            case CLONE: {
                final CloneOption cloneOption = (CloneOption) option;
                this.getPluginDAO().saveInfo(cloneOption.getType().toString(), cloneOption.getName(), cloneOption);
                break;
            }
            case BUILD: {
                final BuildOption buildOption = (BuildOption) option;
                this.getPluginDAO().saveInfo(buildOption.getType().toString(), buildOption.getName(), buildOption);
                break;
            }
            case DEPLOY: {
                final DeployOption deployOption = (DeployOption) option;
                this.getPluginDAO().saveInfo(deployOption.getType().toString(), deployOption.getName(), deployOption);
                break;
            }
            case TEST: {
                final TestOption testOption = (TestOption) option;
                this.getPluginDAO().saveInfo(testOption.getType().toString(), testOption.getName(), testOption);
                break;
            }
        }
    }

    public Object getActiveOption(final OperationType type) {
        switch (type) {
            case CLONE: {
                final List<CloneOption> cloneOptions = (List<CloneOption>) this.getPluginDAO().getInfo(type.toString(), CloneOption.class);
                for (final CloneOption option : cloneOptions) {
                    if (option.isActive()) {
                        return option;
                    }
                }
                break;
            }
            case BUILD: {
                final List<BuildOption> buildOptions = (List<BuildOption>) this.getPluginDAO().getInfo(type.toString(), BuildOption.class);
                for (final BuildOption option2 : buildOptions) {
                    if (option2.isActive()) {
                        return option2;
                    }
                }
                break;
            }
            case DEPLOY: {
                final List<DeployOption> deployOptions = (List<DeployOption>) this.getPluginDAO().getInfo(type.toString(), DeployOption.class);
                for (final DeployOption option3 : deployOptions) {
                    if (option3.isActive()) {
                        return option3;
                    }
                }
                break;
            }
            case TEST: {
                final List<TestOption> testOptions = (List<TestOption>) this.getPluginDAO().getInfo(type.toString(), TestOption.class);
                for (final TestOption option4 : testOptions) {
                    if (option4.isActive()) {
                        return option4;
                    }
                }
                break;
            }
        }
        return null;
    }

    public void updateOption(final Object option, final OperationType type) {
        this.saveOption(option, type);
    }

    public Object getOption(final String optionName, final OperationType type) {
        switch (type) {
            case CLONE: {
                return this.getPluginDAO().getInfo(type.toString(), optionName, CloneOption.class);
            }
            case BUILD: {
                return this.getPluginDAO().getInfo(type.toString(), optionName, BuildOption.class);
            }
            case DEPLOY: {
                return this.getPluginDAO().getInfo(type.toString(), optionName, DeployOption.class);
            }
            case TEST: {
                return this.getPluginDAO().getInfo(type.toString(), optionName, TestOption.class);
            }
            default: {
                return null;
            }
        }
    }

    public void deleteOption(final String optionName, final OperationType type) {
        this.getPluginDAO().deleteInfo(type.toString(), optionName);
    }

    public List<?> allOptionsByType(final OperationType type) {
        switch (type) {
            case CLONE: {
                return (List<?>) this.getPluginDAO().getInfo(type.toString(), CloneOption.class);
            }
            case BUILD: {
                return (List<?>) this.getPluginDAO().getInfo(type.toString(), BuildOption.class);
            }
            case DEPLOY: {
                return (List<?>) this.getPluginDAO().getInfo(type.toString(), DeployOption.class);
            }
            case TEST: {
                return (List<?>) this.getPluginDAO().getInfo(type.toString(), TestOption.class);
            }
            default: {
                return null;
            }
        }
    }

    public void setActive(final String optionName, final OperationType type) {
        switch (type) {
            case CLONE: {
                final CloneOption cloneOption = (CloneOption) this.getOption(optionName, type);
                if (!cloneOption.isActive()) {
                    cloneOption.setIsActive(true);
                    this.saveOption(cloneOption, cloneOption.getType());
                    break;
                }
                break;
            }
            case BUILD: {
                final BuildOption buildOption = (BuildOption) this.getOption(optionName, type);
                if (!buildOption.isActive()) {
                    buildOption.setIsActive(true);
                    this.saveOption(buildOption, buildOption.getType());
                    break;
                }
                break;
            }
            case DEPLOY: {
                final DeployOption deployOption = (DeployOption) this.getOption(optionName, type);
                if (!deployOption.isActive()) {
                    deployOption.setIsActive(true);
                    this.saveOption(deployOption, deployOption.getType());
                    break;
                }
                break;
            }
            case TEST: {
                final TestOption testOption = (TestOption) this.getOption(optionName, type);
                if (!testOption.isActive()) {
                    testOption.setIsActive(true);
                    this.saveOption(testOption, testOption.getType());
                    break;
                }
                break;
            }
        }
    }

    public void deactivate(final String opts, final OperationType type) {
        final String optionName = opts.toUpperCase();
        switch (type) {
            case CLONE: {
                final CloneOption cloneOption = (CloneOption) this.getOption(optionName, type);
                if (cloneOption != null && cloneOption.isActive()) {

                    LOG.info(String.format("Retrieved %s", cloneOption.toString()));
                    cloneOption.setIsActive(false);

                    this.saveOption(cloneOption, cloneOption.getType());
                    break;
                }
                break;
            }
            case BUILD: {
                final BuildOption buildOption = (BuildOption) this.getOption(optionName, type);
                if (buildOption != null && buildOption.isActive()) {

                    LOG.info(String.format("Retrieved %s", buildOption.toString()));
                    buildOption.setIsActive(false);

                    this.saveOption(buildOption, buildOption.getType());
                    break;
                }
                break;
            }
            case DEPLOY: {
                final DeployOption deployOption = (DeployOption) this.getOption(optionName, type);
                if (deployOption != null && deployOption.isActive()) {

                    deployOption.setIsActive(false);
                    this.saveOption(deployOption, deployOption.getType());
                    break;
                }
                break;
            }
            case TEST: {
                final TestOption testOption = (TestOption) this.getOption(optionName, type);
                if (testOption != null && testOption.isActive()) {
                    testOption.setIsActive(false);
                    this.saveOption(testOption, testOption.getType());
                    break;
                }
                break;
            }
        }
    }

    public Tracker getTracker() {
        return this.tracker;
    }

    public void setTracker(final Tracker tracker) {
        this.tracker = tracker;
    }

    public ExecutorService getExecutor() {
        return this.executor;
    }

    public void setExecutor(final ExecutorService executor) {
        this.executor = executor;
    }

    public EnvironmentManager getEnvironmentManager() {
        return this.environmentManager;
    }

    public void setEnvironmentManager(final EnvironmentManager environmentManager) {
        this.environmentManager = environmentManager;
    }

    public PluginDAO getPluginDAO() {
        return this.pluginDAO;
    }

    public void setPluginDAO(final PluginDAO pluginDAO) {
        this.pluginDAO = pluginDAO;
    }

    public PeerManager getPeerManager() {
        return this.peerManager;
    }

    public void setPeerManager(final PeerManager peerManager) {
        this.peerManager = peerManager;
    }

    public NetworkManager getNetworkManager() {
        return this.networkManager;
    }

    public void setNetworkManager(final NetworkManager networkManager) {
        this.networkManager = networkManager;
    }
}
