/*
 * Copyright 2014 Trustees of the University of Pennsylvania
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

import javax.annotation.Nullable;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("objects")
public interface IObjectResource {
	@GET
	@Path("{objectId}")
	@Produces(MediaType.WILDCARD)
	Response getObject(
			@PathParam("objectId") String objectId,
			@HeaderParam("If-Match") @Nullable String eTag,
			@HeaderParam("Range") @Nullable String range,
			@QueryParam("disposition") @Nullable String disposition);

	@GET
	@Path("/file/{datasetId}/{objectId}")
	@Produces(MediaType.WILDCARD)
	Response getFile(
			@PathParam("datasetId") String datasetId,
			@PathParam("objectId") String objectId,
			@HeaderParam("If-Match") @Nullable String eTag,
			@HeaderParam("Range") @Nullable String range,
			@QueryParam("disposition") @Nullable String disposition);

	@DELETE
	@Path("{objectId}")
	void deleteObject(
			@HeaderParam("If-Match") @Nullable String eTag,
			@PathParam("objectId") long objectId);

}
