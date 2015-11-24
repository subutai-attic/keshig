package io.subutai.plugin.keshig.rest;


import io.subutai.plugin.keshig.api.Profile;
import io.subutai.plugin.keshig.api.entity.options.BuildOption;
import io.subutai.plugin.keshig.api.entity.options.CloneOption;
import io.subutai.plugin.keshig.api.entity.options.DeployOption;
import io.subutai.plugin.keshig.api.entity.options.TestOption;

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
    @Path("server/{serverName}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getServer(@PathParam("serverName") String serverName);

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
    @Path("server/{serverName}")
    public Response deleteServer(@PathParam("serverName") String serverName);

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

    //run options
    @GET
    @Path("option/{type}/{optionName}/start")
    @Produces(MediaType.APPLICATION_JSON)
    public Response runOption(@PathParam("type") String type, @PathParam("optionName") String optionName);

    //---------------------------------------------------------------------------------------------------//
    @POST
    @Path("option/clone")
    @Consumes( MediaType.APPLICATION_JSON )
    public Response addCloneOption(CloneOption option);

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("option/clone")
    @Consumes( MediaType.APPLICATION_JSON )
    public Response updateCloneOption(CloneOption option);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("option/build")
    @Consumes( MediaType.APPLICATION_JSON )
    public Response addBuildOption(BuildOption option);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("option/test")
    @Consumes( MediaType.APPLICATION_JSON )
    public Response addTestOption(TestOption option);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("option/deploy")
    @Consumes( MediaType.APPLICATION_JSON )
    public Response addDeployOption(DeployOption option);



    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("option/build")
    @Consumes( MediaType.APPLICATION_JSON )
    public Response updateBuildOption(BuildOption option);

    @PUT
    @Path("option/test")
    @Consumes( MediaType.APPLICATION_JSON )
    public Response updateTestOption(TestOption option);

    @PUT
    @Path("option/deploy")
    @Consumes( MediaType.APPLICATION_JSON )
    public Response updateDeployOption(DeployOption option);

    @DELETE
    @Path("option/{type}/{optionName}")
    public Response deleteOption(@PathParam("type") String type, @PathParam("optionName") String optionName);

    //BUILDS
    @GET
    @Path("build")
    public Response getBuilds();
    //TESTS

    @GET
    @Path("tests")
    public Response getTests();


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
    @Path("profiles/{profileName}/start")
    @Produces(MediaType.APPLICATION_JSON)
    public Response runProfile(@PathParam("profileName") String profileName);

    @GET
    @Path("profiles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listProfiles();

    @GET
    @Path("profiles/{profileName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfile(@PathParam("profileName") String profileName);

    @POST
    @Path("profiles")
    @Consumes( MediaType.APPLICATION_JSON )
    public Response addProfile(Profile profile);

    @PUT
    @Path("profiles")
    @Consumes( MediaType.APPLICATION_JSON )
    public Response updateProfile(Profile profile);

    @DELETE
    @Path("profiles/{profileName}")
    public Response deleteProfile(@PathParam("profileName") String profileName);

    //Serenity Results
    @GET
    @Path("serenity/{id}")
    @Produces( { MediaType.APPLICATION_JSON } )
    public Response getIndexHtml(@PathParam("id") String id);
}
