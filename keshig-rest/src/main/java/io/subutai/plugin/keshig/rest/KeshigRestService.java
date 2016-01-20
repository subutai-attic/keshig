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
    public Response listServers();

    @GET
    @Path( "server/{serverId}" )
    @Produces( { MediaType.APPLICATION_JSON } )
    public Response getServer( @PathParam( "serverId" ) String serverName );

    @POST
    @Path( "server/{serverId}" )
    public Response addServer( @PathParam( "serverId" ) String serverId );

    @DELETE
    @Path( "server/{serverId}" )
    public Response deleteServer( @PathParam( "serverId" ) String serverId );

    @PUT
    @Path( "server/{hostname}/{status}" )
    public Response updateNightlyBuildStatus( @PathParam( "hostname" ) String hostname,
                                              @PathParam( "status" ) boolean status );

    //OPTION CRUD
    //CLONE,BUILD,DEPLOY,TEST

    @GET
    @Path( "option" )
    @Produces( {MediaType.APPLICATION_JSON })
    public Response listOptions();

    @GET
    @Path( "option/types" )
    @Produces( {MediaType.APPLICATION_JSON })
    public Response getOptionTypes();

    @GET
    @Path( "option/type/{type}" )
    @Produces( {MediaType.APPLICATION_JSON })
    public Response getOptionsByType( @PathParam( "type" ) String type );

    @GET
    @Path( "option/{type}/{optionName}" )
    @Produces( {MediaType.APPLICATION_JSON })
    public Response getOption( @PathParam( "type" ) String type, @PathParam( "optionName" ) String optionName );


    @GET
    @Path( "option/{type}/{optionName}/start/{serverId}" )
    @Produces( {MediaType.APPLICATION_JSON })
    public Response runOptionOnTargetServer( @PathParam( "type" ) String type,
                                             @PathParam( "optionName" ) String optionName,
                                             @PathParam( "serverId" ) String serverId );

    @GET
    @Path( "export/{serverId}/{buildName}/start" )
    public Response export( @PathParam( "serverId" ) String serverId, @PathParam( "buildName" ) String buildName );

    @POST
    @Produces( {MediaType.APPLICATION_JSON })
    @Path( "option/test" )
    @Consumes( {MediaType.APPLICATION_JSON })
    public Response addTestOption( TestOption option );

    @POST
    @Produces( {MediaType.APPLICATION_JSON })
    @Path( "option/deploy" )
    @Consumes( {MediaType.APPLICATION_JSON })
    public Response addDeployOption( DeployOption option );

    @PUT
    @Path( "option/test" )
    @Consumes( {MediaType.APPLICATION_JSON })
    public Response updateTestOption( TestOption option );

    @PUT
    @Path( "option/deploy" )
    @Consumes( {MediaType.APPLICATION_JSON })
    public Response updateDeployOption( DeployOption option );

    @DELETE
    @Path( "option/{optionName}" )
    public Response deleteOption( @PathParam( "optionName" ) String optionName );

    @GET
    @Path( "tests" )
    public Response getTests();

    //UPDATE STATUS
    @PUT
    @Path( "statuses" )
    public Response updateStatuses();

    @POST
    @Path( "statuses" )
    public Response updateReserved( @FormParam( "hostname" ) String hostName, @FormParam( "serverIp" ) String serverIp,
                                    @FormParam( "usedBy" ) String usedBy, @FormParam( "comment" ) String comment );

    @DELETE
    @Path( "statuses/{hostname}/{serverIp}" )
    public Response deleteReservation( @PathParam( "hostname" ) String hostname,
                                       @PathParam( "serverIp" ) String serverIp );

    @GET
    @Path( "statuses" )
    @Produces( {MediaType.APPLICATION_JSON })
    public Response getStatuses();

    //HISTORY CRUD
    @GET
    @Path( "history" )
    @Produces( {MediaType.APPLICATION_JSON })
    public Response listHistory();

    @GET
    @Path( "history/{id}" )
    @Produces( {MediaType.APPLICATION_JSON })
    public Response getHistory( @PathParam( "id" ) String id );

    //Run profiles

    @GET
    @Path( "profiles/{profileName}/start" )
    @Produces( {MediaType.APPLICATION_JSON })
    public Response runProfile( @PathParam( "profileName" ) String profileName );

    @GET
    @Path( "profiles" )
    @Produces( {MediaType.APPLICATION_JSON })
    public Response listProfiles();

    @GET
    @Path( "profiles/{profileName}" )
    @Produces( {MediaType.APPLICATION_JSON })
    public Response getProfile( @PathParam( "profileName" ) String profileName );

    @POST
    @Path( "profiles" )
    @Consumes( {MediaType.APPLICATION_JSON })
    public Response addProfile( Profile profile );

    @PUT
    @Path( "profiles" )
    @Consumes( {MediaType.APPLICATION_JSON })
    public Response updateProfile( Profile profile );

    @DELETE
    @Path( "profiles/{profileName}" )
    public Response deleteProfile( @PathParam( "profileName" ) String profileName );
}
