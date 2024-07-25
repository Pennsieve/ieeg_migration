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

import java.io.InputStream;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;

import edu.upenn.cis.db.mefview.shared.EEGMontage;
import edu.upenn.cis.db.mefview.shared.EEGMontages;
import edu.upenn.cis.db.mefview.shared.RecordingObjects;

@Path("datasets")
public interface IDatasetResource {

	public static final String CREATE_OBJECT_URL_REG_EXP_STR = ".*/datasets/[^/]+/objects$";

	@PUT
	@Path("{datasetId}/montages")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	Response createEditMontage(
			@PathParam("datasetId") String datasetId,
			EEGMontage montage);

	@GET
	@Path("{datasetId}/timeseries")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	DatasetFileInfos getDatasetFileInfos(
			@PathParam("datasetId") String datasetId);

	@GET
	@Path("{datasetId}/montages")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	EEGMontages getMontages(
			@PathParam("datasetId") String datasetId);

	// currently only supports xml
	@GZIP
	@GET
	@Path("{datasetId}/annotations")
	@Produces(MediaType.APPLICATION_XML)
	Response getTsAnnotations(
			@PathParam("datasetId") String datasetId);

	@GET
	@Path("{datasetId}/objects")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	RecordingObjects getRecordingObjects(
			@PathParam("datasetId") String datasetId);

	@POST
	@Path("{datasetId}/objects")
	@Consumes(MediaType.WILDCARD)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	Response createRecordingObject(
			@PathParam("datasetId") String datasetId,
			InputStream input,
			@HeaderParam("filename") String filename,
			@HeaderParam("Content-MD5") String md5Base64,
			@QueryParam("test") boolean test);

	@DELETE
	@Path("{datasetId}")
	void deleteDataset(
			@PathParam("datasetId") String datasetId,
			@Nullable @HeaderParam("If-Match") String eTag,
			@Nullable @QueryParam("deleteEmptySubject") Boolean deleteEmptySubject);

}
