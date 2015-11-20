package io.subutai.plugin.keshigqd.rest;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import io.subutai.plugin.keshigqd.api.KeshigQD;
import io.subutai.plugin.keshigqd.api.Profile;
import io.subutai.plugin.keshigqd.api.entity.OperationType;
import io.subutai.plugin.keshigqd.api.entity.Server;
import io.subutai.plugin.keshigqd.api.entity.ServerType;
import io.subutai.plugin.keshigqd.api.entity.options.BuildOption;
import io.subutai.plugin.keshigqd.api.entity.options.CloneOption;
import io.subutai.plugin.keshigqd.api.entity.options.DeployOption;
import io.subutai.plugin.keshigqd.api.entity.options.TestOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.Response;
import java.util.*;

import static io.subutai.plugin.keshigqd.api.entity.OperationType.*;

public class KeshigRestServiceImpl implements KeshigRestService {

    private static final Logger LOG = LoggerFactory.getLogger(KeshigRestServiceImpl.class);

    private KeshigQD keshig;

    public KeshigQD getKeshig() {
        return keshig;
    }

    public void setKeshig(KeshigQD keshig) {
        this.keshig = keshig;
    }


    public KeshigRestServiceImpl() {
        LOG.warn("Init keshig");
    }

    @Override
    public Response listServers() {

        List<Server> serverList = keshig.getServers();

        List<Server> availableServers = new ArrayList<>();

        for (Server server : serverList) {
            if (!server.getType().equals(ServerType.PEER_SERVER))
                availableServers.add(server);
        }

        Response response = Response.ok(availableServers).build();

        return response;
    }

    @Override
    public Response getServer(String serverId) {

        if (Strings.isNullOrEmpty(serverId)) {
            Response response = Response.status(Response.Status.BAD_REQUEST).entity("Invalid server id").build();
            return response;
        }
        Server server = keshig.getServer(serverId);

        if (server == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(String.format("Server with id: %s not found", serverId)).build();
        }

        Response response = Response.ok(keshig.getServer(serverId)).build();

        return response;
    }

    @Override
    public Response addServer(String serverId,String serverName, String serverType) {

        if (Strings.isNullOrEmpty(serverId)) {
            Response response = Response.status(Response.Status.BAD_REQUEST).entity("Invalid server id").build();
            return response;
        }

        if (Strings.isNullOrEmpty(serverName)) {
            Response response = Response.status(Response.Status.BAD_REQUEST).entity("Invalid server name").build();
            return response;
        }

        if (Strings.isNullOrEmpty(serverType)) {
            Response response = Response.status(Response.Status.BAD_REQUEST).entity("Invalid server type").build();
            return response;
        }

        keshig.setServer(serverId,serverType,serverName);

        return null;

    }

    @Override
    public Response updateServer(String serverId, String serverName, String serverType) {

        keshig.setServer(serverId,serverType,serverName);

        return Response.ok().build();
    }

    @Override
    public Response deleteServer(String serverId) {

        if (Strings.isNullOrEmpty(serverId)) {
            Response response = Response.status(Response.Status.BAD_REQUEST).entity("Invalid server id").build();
            return response;
        }
        keshig.removeServer(serverId);

        return Response.ok().build();
    }

    @Override
    public Response listOptions() {

        List<DeployOption> deployOptions = (List<DeployOption>) keshig.allOptionsByType(DEPLOY);
        List<TestOption> testOptions = (List<TestOption>) keshig.allOptionsByType(TEST);
        List<CloneOption> cloneOptionList = (List<CloneOption>) keshig.allOptionsByType(CLONE);
        List<BuildOption> buildOptions = (List<BuildOption>) keshig.allOptionsByType(BUILD);

        Map<String, List<?>> allOptions = new HashMap<>();
        allOptions.put(CLONE.toString(), cloneOptionList);
        allOptions.put(BUILD.toString(), buildOptions);
        allOptions.put(DEPLOY.toString(), deployOptions);
        allOptions.put(TEST.toString(), testOptions);

        return Response.ok().entity(allOptions).build();
    }

    @Override
    public Response getOptionTypes() {

        Set<Object> options = Sets.newHashSet();

        options.add(BUILD);
        options.add(CLONE);
        options.add(DEPLOY);
        options.add(TEST);

        return Response.ok(options).build();
    }

    public Response getServerType(){

        Set<Object> serverTypes = Sets.newHashSet();

        serverTypes.add(ServerType.DEPLOY_SERVER);
        serverTypes.add(ServerType.BUILD_SERVER);
        serverTypes.add(ServerType.TEST_SERVER);


        return Response.ok(serverTypes).build();
    }

    @Override
    public Response getOptionsByType(String type) {

        OperationType operationType = valueOf(type.toUpperCase());

        switch (operationType) {

            case CLONE: {
                List<CloneOption> cloneOptionList = (List<CloneOption>) keshig.allOptionsByType(CLONE);
                return Response.ok().entity(cloneOptionList).build();
            }
            case BUILD: {
                List<BuildOption> buildOptions = (List<BuildOption>) keshig.allOptionsByType(BUILD);
                return Response.ok().entity(buildOptions).build();
            }
            case DEPLOY: {
                List<DeployOption> deployOptions = (List<DeployOption>) keshig.allOptionsByType(DEPLOY);
                return Response.ok().entity(deployOptions).build();
            }
            case TEST: {
                List<TestOption> testOptions = (List<TestOption>) keshig.allOptionsByType(TEST);
                return Response.ok().entity(testOptions).build();
            }
            default: {
                Response response = Response.status(Response.Status.BAD_REQUEST).entity("Invalid option type").build();
                return response;
            }
        }

    }

    @Override
    public Response getOption(String type, String optionName) {

        if (Strings.isNullOrEmpty(type)) {

            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid option type").build();

        }
        if (Strings.isNullOrEmpty(optionName)) {

            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid option name").build();
        }

        Response response = Response.ok().entity(keshig.getOption(optionName, OperationType.valueOf(type.toUpperCase()))).build();

        return response;
    }

    @Override
    public Response addOption(String optionType, Object option) {

        keshig.saveOption(option, OperationType.valueOf(optionType.toUpperCase()));

        return Response.ok().build();

    }

    @Override
    public Response updateOption(String optionType, Object option) {

        if (Strings.isNullOrEmpty(optionType)) {

            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid option type").build();

        }
        if (option==null) {

            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid option").build();
        }

        return addOption(optionType, option);
    }

    @Override
    public Response deleteOption(String type, String optionName) {

        if (Strings.isNullOrEmpty(type)) {

            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid option type").build();

        }
        if (Strings.isNullOrEmpty(optionName)) {

            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid option name").build();
        }

        keshig.deleteOption(optionName,OperationType.valueOf(type.toUpperCase()));

        return Response.ok().build();
    }

    @Override
    public Response listHistory() {
        return null;
    }

    @Override
    public Response getHistory(String id) {
        return null;
    }

    @Override
    public Response listProfiles() {
        return null;
    }

    @Override
    public Response getProfile(String profileName) {
        return null;
    }

    @Override
    public Response addProfile(Profile profile) {
        return null;
    }

    @Override
    public Response updateProfile(Profile profile) {
        return null;
    }

    @Override
    public Response deleteProfile(String profileName) {
        return null;
    }
}
