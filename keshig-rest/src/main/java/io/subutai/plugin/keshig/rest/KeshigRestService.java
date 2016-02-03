package io.subutai.plugin.keshig.rest;


import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.subutai.plugin.keshig.api.Profile;
import io.subutai.plugin.keshig.api.entity.options.DeployOption;
import io.subutai.plugin.keshig.api.entity.options.TestOption;


public interface KeshigRestService
{

    //SERVER CRUD
    @GET
    @Path( "server" )
    @Produces( { MediaType.APPLICATION_JSON } )
    Response listServers();

    @GET
    @Path( "server/{serverId}" )
    @Produces( { MediaType.APPLICATION_JSON } )
    Response getServer( @PathParam( "serverId" ) String serverName );

    @POST
    @Path( "server/{serverId}" )
    Response addServer( @PathParam( "serverId" ) String serverId );

    @DELETE
    @Path( "server/{serverId}" )
    Response deleteServer( @PathParam( "serverId" ) String serverId );

    @PUT
    @Path( "server/{hostname}/{status}" )
    Response updateNightlyBuildStatus( @PathParam( "hostname" ) String hostname,
                                       @PathParam( "status" ) boolean status );

    @GET
    @Path( "option" )
    @Produces( { MediaType.APPLICATION_JSON } )
    Response listOptions();

    @GET
    @Path( "option/types" )
    @Produces( { MediaType.APPLICATION_JSON } )
    Response getOptionTypes();

    @GET
    @Path( "option/type/{type}" )
    @Produces( { MediaType.APPLICATION_JSON } )
    Response getOptionsByType( @PathParam( "type" ) String type );

    @GET
    @Path( "option/{type}/{optionName}" )
    @Produces( { MediaType.APPLICATION_JSON } )
    Response getOption( @PathParam( "type" ) String type, @PathParam( "optionName" ) String optionName );


    @GET
    @Path( "option/{type}/{optionName}/start/{serverId}" )
    @Produces( { MediaType.APPLICATION_JSON } )
    Response runOptionOnTargetServer( @PathParam( "type" ) String type, @PathParam( "optionName" ) String optionName,
                                      @PathParam( "serverId" ) String serverId );

    @GET
    @Path( "test/{gitId}/{playbooks}/start/{serverId}" )
    Response runPlaybooks( @PathParam( "gitId" ) String gitId, @PathParam( "playbooks" ) String playbooks,
                           @PathParam( "serverId" ) String serverId );

    @GET
    @Path( "export/{serverId}/{buildName}/start" )
    Response export( @PathParam( "serverId" ) String serverId, @PathParam( "buildName" ) String buildName );

    @POST
    @Produces( { MediaType.APPLICATION_JSON } )
    @Path( "option/test" )
    @Consumes( { MediaType.APPLICATION_JSON } )
    Response addTestOption( TestOption option );

    @POST
    @Produces( { MediaType.APPLICATION_JSON } )
    @Path( "option/deploy" )
    @Consumes( { MediaType.APPLICATION_JSON } )
    Response addDeployOption( DeployOption option );

    @PUT
    @Path( "option/test" )
    @Consumes( { MediaType.APPLICATION_JSON } )
    Response updateTestOption( TestOption option );

    @PUT
    @Path( "option/deploy" )
    @Consumes( { MediaType.APPLICATION_JSON } )
    Response updateDeployOption( DeployOption option );

    @DELETE
    @Path( "option/{type}/{optionName}" )
    Response deleteOption( @PathParam( "type" ) String type, @PathParam( "optionName" ) String optionName );

    @GET
    @Path( "tests" )
    @Produces( { MediaType.APPLICATION_JSON } )
    Response getTests();

    //UPDATE STATUS
    @PUT
    @Path( "statuses" )
    @Produces( { MediaType.APPLICATION_JSON } )
    Response updateStatuses();

    @POST
    @Path( "statuses" )
    @Produces( { MediaType.APPLICATION_JSON } )
    Response updateReserved( @FormParam( "hostname" ) String hostName, @FormParam( "serverIp" ) String serverIp,
                             @FormParam( "usedBy" ) String usedBy, @FormParam( "comment" ) String comment );

    @DELETE
    @Path( "statuses/{hostname}/{serverIp}" )
    Response deleteReservation( @PathParam( "hostname" ) String hostname, @PathParam( "serverIp" ) String serverIp );

    @GET
    @Path( "statuses" )
    @Produces( { MediaType.APPLICATION_JSON } )
    Response getStatuses();

    //HISTORY CRUD
    @GET
    @Path( "history" )
    @Produces( { MediaType.APPLICATION_JSON } )
    Response listHistory();

    @GET
    @Path( "history/{id}" )
    @Produces( { MediaType.APPLICATION_JSON } )
    Response getHistory( @PathParam( "id" ) String id );

    //Run profiles

    @GET
    @Path( "profiles/{profileName}/start" )
    @Produces( { MediaType.APPLICATION_JSON } )
    Response runProfile( @PathParam( "profileName" ) String profileName );

    @GET
    @Path( "profiles" )
    @Produces( { MediaType.APPLICATION_JSON } )
    Response listProfiles();

    @GET
    @Path( "profiles/{profileName}" )
    @Produces( { MediaType.APPLICATION_JSON } )
    Response getProfile( @PathParam( "profileName" ) String profileName );

    @POST
    @Path( "profiles" )
    @Consumes( { MediaType.APPLICATION_JSON } )
    Response addProfile( Profile profile );

    @PUT
    @Path( "profiles" )
    @Consumes( { MediaType.APPLICATION_JSON } )
    Response updateProfile( Profile profile );

    @DELETE
    @Path( "profiles/{profileName}" )
    Response deleteProfile( @PathParam( "profileName" ) String profileName );
}
