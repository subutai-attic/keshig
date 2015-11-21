package io.subutai.plugin.keshigqd.rest;


import io.subutai.plugin.keshigqd.api.Profile;
import io.subutai.plugin.keshigqd.api.entity.options.BuildOption;
import io.subutai.plugin.keshigqd.api.entity.options.CloneOption;
import io.subutai.plugin.keshigqd.api.entity.options.DeployOption;
import io.subutai.plugin.keshigqd.api.entity.options.TestOption;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface KeshigRestService {

    //SERVER CRUD
    @GET
    @Path("server")
    @Produces({MediaType.APPLICATION_JSON})
    public Response listServers();

    @GET
    @Path("server/{serverId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getServer(@PathParam("serverId") String serverId);

    @GET
    @Path("server/types")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getServerType();


    @POST
    @Path("server")
    public Response addServer(@FormParam("serverId") String serverId,
                              @FormParam("serverName") String serverName,
                              @FormParam("serverType") String serverType);

    @PUT
    @Path("server")
    public Response updateServer(@FormParam("serverId") String serverId,
                                 @FormParam("serverName") String serverName,
                                 @FormParam("serverType") String serverType);

    @DELETE
    @Path("server/{serverId}")
    public Response deleteServer(@PathParam("serverId") String serverId);

    //OPTION CRUD
    //CLONE,BUILD,DEPLOY,TEST

    @GET
    @Path("option")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listOptions();

    @GET
    @Path("option/types")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOptionTypes();

    @GET
    @Path("option/type/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOptionsByType(@PathParam("type") String type);

    @GET
    @Path("option/{type}/{optionName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOption(@PathParam("type") String type, @PathParam("optionName") String optionName);
    //---------------------------------------------------------------------------------------------------//
    @POST
    @Path("option/clone")
    public Response addCloneOption(CloneOption option);

    @POST
    @Path("option/build")
    public Response addBuildOption(BuildOption option);

    @POST
    @Path("option/test")
    public Response addTestOption(TestOption option);

    @POST
    @Path("option/deploy")
    public Response addDeployOption(DeployOption option);

    @PUT
    @Path("option/clone")
    public Response updateCloneOption(CloneOption option);

    @PUT
    @Path("option/build")
    public Response updateBuildOption(BuildOption option);

    @PUT
    @Path("option/test")
    public Response updateTestOption(TestOption option);

    @PUT
    @Path("option/deploy")
    public Response updateDeployOption(DeployOption option);

    @DELETE
    @Path("option/{type}/{optionName}")
    public Response deleteOption(@PathParam("type") String type, @PathParam("optionName") String optionName);

    //HISTORY CRUD

    @GET
    @Path("history")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listHistory();

    @GET
    @Path("history/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistory(@PathParam("id") String id);

    //Run profiles

    @GET
    @Path("profiles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listProfiles();

    @GET
    @Path("profile/{profileName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfile(@PathParam("profileName") String profileName);

    @POST
    @Path("profile")
    public Response addProfile(Profile profile);

    @PUT
    @Path("profile")
    public Response updateProfile(Profile profile);

    @DELETE
    @Path("profile/{profileName}")
    public Response deleteProfile(@PathParam("profileName") String profileName);


}
