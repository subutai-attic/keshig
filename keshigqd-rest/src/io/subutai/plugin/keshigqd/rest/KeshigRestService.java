package io.subutai.plugin.keshigqd.rest;


import io.subutai.plugin.keshigqd.api.Profile;
import io.subutai.plugin.keshigqd.api.entity.Server;

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

    @POST
    @Path("server")
    public Response addServer(Server server);

    @PUT
    @Path("server")
    public Response updateServer(Server server);

    @DELETE
    @Path("server/{serverId}")
    public Response deleteServer(@PathParam("serverId") String serverId);

    //OPTION CRUD

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

    @POST
    @Path("option/{optionType}")
    public Response addOption(@PathParam("optionType") String optionType, Object option);

    @PUT
    @Path("option/{optionType}")
    public Response updateOption(@PathParam("optionType") String optionType, Object option);

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
