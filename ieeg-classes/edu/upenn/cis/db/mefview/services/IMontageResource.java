/*
 * Copyright (C) 2014 Trustees of the University of Pennsylvania
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
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.ietf.annotations.PATCH;

@Path("montages")
public interface IMontageResource {
//
//	@POST
//	@Path("{montageId}/layers")
//	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
//	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
//	Response createLayer(
//			@PathParam("montageId") long montageId,
//			Layer layer);
//
//	@DELETE
//	@Path("{montageId}")
//	void deleteMontage(
//			@HeaderParam("If-Match") @Nullable String eTag,
//			@PathParam("montageId") long montageId);
//
//	@GET
//	@Path("{montageId}/layers")
//	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
//	LayerInfos getLayerInfos(
//			@PathParam("montageId") long montageId);
//
//	@GET
//	@Path("{montageId}")
//	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
//	Response getMontage(
//			@PathParam("montageId") long montageId);
//
//	@PATCH
//	@Path("{montageId}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	Montage updateMontage(
//			@PathParam("montageId") Long montageId,
//			@HeaderParam("If-Match") String eTag,
//			Montage montage);

}
