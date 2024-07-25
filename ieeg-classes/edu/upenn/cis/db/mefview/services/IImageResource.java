/*******************************************************************************
 * Copyright 2010 Trustees of the University of Pennsylvania
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package edu.upenn.cis.db.mefview.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/images")
public interface IImageResource {

	// /////////////////////// Retrieve image

	@GET
	@Path("getDataSnapshotImage/{datasnapshot}/{imageName}/{index}")
	@Produces("image/png")
	public Response getDataSnapshotImage(
			@QueryParam("userid") final String userid,
			@QueryParam("timestamp") final long timestamp,
			@QueryParam("signature") final String signature,
			@PathParam("datasnapshot") final String dataSnapshot,
			@PathParam("imageName") final String imageName)
			throws IllegalArgumentException;

//	@GET
//	@Path("getDataSnapshotDicom/{datasnapshot}/{imageName}/{index}")
//	@Produces("image/png")
//	public Response getDataSnapshotDicom(
//			@QueryParam("userid") final String userid,
//			@QueryParam("timestamp") final long timestamp,
//			@QueryParam("signature") final String signature,
//			@PathParam("datasnapshot") final String dataSnapshot,
//			@PathParam("imageName") final String imageName,
//			@PathParam("index") final int index)
//			throws IllegalArgumentException;

	@GET
	@Path("getDataSnapshotScaledImage/{datasnapshot}/{imageName}/{index}")
	@Produces("image/png")
	public Response getDataSnapshotScaledImage(
			@QueryParam("userid") final String userid,
			@QueryParam("timestamp") final long timestamp,
			@QueryParam("signature") final String signature,
			@PathParam("datasnapshot") final String dataSnapshot,
			@PathParam("imageName") final String imageName,
			@QueryParam("width") final int width,
			@QueryParam("height") final int height
			)
			throws IllegalArgumentException;

	@GET
	@Path("addImageToDataset/{dataset}/{imageName}")
	public String addImageToDataSnapshot(
//			@CookieParam("userid") final String userid,
//			@CookieParam("timestamp") long timestamp,
//			@CookieParam("signature") final String signature,
			@PathParam("datasnapshot") final String dataSnapshot, 
			@PathParam("imageName") final String imageName
			);
			
}
