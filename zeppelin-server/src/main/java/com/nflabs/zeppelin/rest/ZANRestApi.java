package com.nflabs.zeppelin.rest;

import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.nflabs.zeppelin.scheduler.Job;
import com.nflabs.zeppelin.server.JsonResponse;
import com.nflabs.zeppelin.server.ZANJob;
import com.nflabs.zeppelin.server.ZANJobManager;
import com.nflabs.zeppelin.server.ZQLJob;
import com.nflabs.zeppelin.zan.Info;
import com.nflabs.zeppelin.zan.ZANException;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/zan")
@Api( value = "/zan", description = "Zeppelin Archive Network is library sharing network" )
public class ZANRestApi {
	Logger logger = Logger.getLogger(ZANRestApi.class);
	private com.nflabs.zeppelin.zan.ZAN zan;
	private ZANJobManager jobManager;

	/**
	 * This is required by Swagger
	 */
    public ZANRestApi() {
      super();
    }

	public ZANRestApi(com.nflabs.zeppelin.zan.ZAN zan, ZANJobManager zanJobManager){
		this.zan = zan;
		this.jobManager = zanJobManager;
	}

	public static class SearchRequest{
		public String query;
	}

    /**
     * List all Zan application fron git repository
     *
     * @return List of Zan lib
     */
    @GET
    @ApiOperation(httpMethod = "GET", value = "Get list of Zan libraries", response = Response.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "List of Zan libraries"),
        @ApiResponse(code = 500, message = "Couln't get the list of Zan application")})
    @Produces("application/json")
    public Response getZanLibList() {
      try {
        return new JsonResponse<List<Info>>(Status.OK, "", zan.list()).build();
      } catch (ZANException e) {
        logger.error("Listing error", e);
        return new JsonResponse<Object>(Status.INTERNAL_SERVER_ERROR, e.getMessage()).build();
      }
    }

    @POST
    @Path("/search")
    @ApiOperation(httpMethod = "POST", value = "Search Zeppelin Lib", response = Response.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "List of lib"),@ApiResponse(code = 500, message = "Search failled")})
    @Produces("application/json")
    public Response search(String json) {
    	Gson gson = new Gson();
    	SearchRequest sr = gson.fromJson(json, SearchRequest.class);
    	// TODO implement search instead of list
    	try {
			return new JsonResponse<List<Info>>(Status.OK, "", zan.list()).build();
		} catch (ZANException e) {
			logger.error("Search failed", e);
			return new JsonResponse<Object>(Status.INTERNAL_SERVER_ERROR, e.getMessage()).build();
		}
    }

    @PUT
    @Path("/update")
    @ApiOperation(httpMethod = "PUT", value = "Update Zeppelin Lib", response = Response.class)
    @ApiResponses(value = {@ApiResponse(code = 202, message = "Updtate submitted")})
    @Produces("application/json")
    public Response update() {
    	jobManager.update();
    	return new JsonResponse<Object>(Status.ACCEPTED, "Job submitted").build();
    }

    @PUT
    @Path("/update/{libName}")
    @ApiOperation(httpMethod = "PUT", value = "Upgrate Zeppelin Lib", response = Response.class)
    @ApiResponses(value = {@ApiResponse(code = 202, message = "Upgrate done")})
    @Produces("application/json")
    public Response upgrade(@ApiParam(value = "Lib name", required = true) @PathParam("libName") String libName){
        jobManager.upgrade(libName);
        return new JsonResponse<List<ZANJob>>(Status.ACCEPTED, "").build();
    }

    @GET
    @Path("/running")
    @ApiOperation(httpMethod = "GET", value = "run Zeppelin Lib", response = Response.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "run submitted")})
    @Produces("application/json")
    public Response getJobsRunning(){
    	List<ZANJob> job = jobManager.getJobsRunning();
    	return new JsonResponse<List<ZANJob>>(Status.OK, "", job).build();
    }

    @POST
    @Path("/install/{libName}")
    @ApiOperation(httpMethod = "POST", value = "Install Zeppelin Lib", response = Response.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Install done")})
    @Produces("application/json")
    public Response install(@ApiParam(value = "Lib name", required = true) @PathParam("libName") String libName){
    	jobManager.install(libName);
    	return new JsonResponse<List<ZANJob>>(Status.OK, "").build();
    }

    @DELETE
    @Path("/uninstall/{libName}")
    @ApiOperation(httpMethod = "GET", value = "Uninstall Zeppelin Lib", response = Response.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Uninstall done")})
    @Produces("application/json")
    public Response uninstall(@ApiParam(value = "Lib name", required = true) @PathParam("libName") String libName){
    	jobManager.uninstall(libName);
    	return new JsonResponse<List<ZANJob>>(Status.OK, "").build();
    }
}
