/*
 * Copyright 2012 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.db.mefview.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/jobtracker")
public interface IJobTracker {

	@GET
	@Path("getNewJob/{workerID}")
	@Produces("text/plain")
	public String getNewJob(
			@HeaderParam("userid") final String userId, 
			@HeaderParam("timestamp") long timestamp, 
			@HeaderParam("signature") final String signature, 
			@PathParam("workerID") final String workerId);

	@GET
	@Path("getNewJobForTool/{toolID}/{workerID}")
	@Produces("text/plain")
	public String getNewJobForTool(
			@HeaderParam("userid") final String userId, 
			@HeaderParam("timestamp") long timestamp, 
			@HeaderParam("signature") final String signature, 
			@PathParam("toolID") final String snapshotId, 
			@PathParam("workerID") final String workerId);
	
	@POST
	@Path("scheduleJob/{snapshotID}")
	@Produces("text/plain")
	public Boolean scheduleJob(
			@HeaderParam("userid") final String userId, 
			@HeaderParam("timestamp") long timestamp, 
			@HeaderParam("signature") final String signature, 
			@PathParam("snapshotID") final String dataSnapshotRevId);
	
	@GET
	@Path("getFirstTimeSeriesTask/{workerID}/{snapshotID}")
	@Produces("application/json")
	public TimeSeriesTask getFirstTimeSeriesTask(
			@HeaderParam("userid") final String userid,
			@HeaderParam("timestamp") long timestamp,
			@HeaderParam("signature") final String signature,
			@PathParam("snapshotID") final String snapshotId,
			@PathParam("workerID") final String workerId);
	
	@GET
	@Path("getNextTimeSeriesTask/{workerID}/{snapshotID}")
	@Produces("application/json")
	public TimeSeriesTask getNextTimeSeriesTask(
			@HeaderParam("userid") final String userid,
			@HeaderParam("timestamp") long timestamp,
			@HeaderParam("signature") final String signature,
			@PathParam("snapshotID") final String snapshotId,
			@PathParam("workerID") final String workerId,
			@QueryParam("completedTask") final String completedTask);

	@GET
	@Path("getTaskDetails/{snapshotID}")
	@Produces("application/json")
	public String getTaskXML(
			@HeaderParam("userid") final String userid,
			@HeaderParam("timestamp") long timestamp,
			@HeaderParam("signature") final String signature,
			@PathParam("snapshotID") final String snapshotId);
	
	@GET
	@Path("getTaskList/{snapshotID}")
	@Produces("application/json")
	public TimeSeriesTaskList getTaskList(
			@HeaderParam("userid") final String userid,
			@HeaderParam("timestamp") long timestamp,
			@HeaderParam("signature") final String signature,
			@PathParam("snapshotID") final String snapshotId);

	@POST
	@Path("addOutputAnnotations/{snapshotID}")
	@Consumes("application/xml")
	public String addOutputAnnotations(
			@HeaderParam("userid") final String userid,
			@HeaderParam("timestamp") long timestamp,
			@HeaderParam("signature") final String signature,
			@PathParam("snapshotID") final String snapshotId,
			final TimeSeriesAnnotationList annotations);

	@POST
	@Path("addOutputTimeSeries/{snapshotID}/{channelID}")
	@Produces("text/plain")
	public String addOutputTimeSeries(
			@HeaderParam("userid") final String userid,
			@HeaderParam("timestamp") long timestamp,
			@HeaderParam("signature") final String signature,
			@PathParam("snapshotID") final String snapshotId,
			@PathParam("channelID") final String channelId);
	
	@POST
	@Path("removeOutputAnnotations/{snapshotID}")
	@Produces("text/plain")
	public String removeOutputAnnotations(
			@HeaderParam("userid") final String userid,
			@HeaderParam("timestamp") long timestamp,
			@HeaderParam("signature") final String signature,
			@PathParam("snapshotID") final String snapshotId,
			@QueryParam("annotations") final String[] annotations);

	@POST
	@Path("removeOutputTimeSeries/{snapshotID}/{channelID}")
	@Produces("text/plain")
	public String removeOutputTimeSeries(
			@HeaderParam("userid") final String userid,
			@HeaderParam("timestamp") long timestamp,
			@HeaderParam("signature") final String signature,
			@PathParam("snapshotID") final String snapshotId,
			@PathParam("channelID") final String channelId);
	
	@POST
	@Path("getActiveJobs")
	@Produces("application/json")
	public JobInformationList getActiveJobs(
			@HeaderParam("userid") final String userId, 
			@HeaderParam("timestamp") long timestamp, 
			@HeaderParam("signature") String signature);

	@POST
	@Path("terminateJob/{snapshotID}")
	@Produces("application/json")
	public TerminateJobResult terminateJob(
			@HeaderParam("userid") final String userId, 
			@HeaderParam("timestamp") long timestamp, 
			@HeaderParam("signature") final String signature, 
			@PathParam("snapshotID") final String dataSnapshotRevId);
	
	@POST
	@Path("getJobTasksRemaining/{snapshotID}")
	@Produces("application/json")
	public TimeSeriesTaskList getJobTasksRemaining(
			@HeaderParam("userid") final String userId, 
			@HeaderParam("timestamp") long timestamp, 
			@HeaderParam("signature") final String signature, 
			@PathParam("snapshotID") final String dataSnapshotRevId);

	@POST
	@Path("initializeJobTaskList/{snapshotID}")
	@Produces("application/json")
	public TimeSeriesTaskList initializeJobTaskList(
			@HeaderParam("userid") final String userId, 
			@HeaderParam("timestamp") long timestamp, 
			@HeaderParam("signature") final String signature, 
			@PathParam("snapshotID") final String dataSnapshotRevId);
	
	@GET
	@Path("ping")
	@Produces("text/plain")
	public String ping();
	
}
