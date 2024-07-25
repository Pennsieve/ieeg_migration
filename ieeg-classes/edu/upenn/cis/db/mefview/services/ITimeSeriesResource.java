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

import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/timeseries")
public interface ITimeSeriesResource {

	public static final String START_TIMES_UUTC = "start-times-uutc";
	public static final String VOLTAGE_CONVERSION_FACTORS_MV = "voltage-conversion-factors-mv";
	public static final String ROW_LENGTHS = "row-lengths";
	public static final String SAMPLE_RATES = "sample-rates";
	public static final String SAMPLES_PER_ROW = "samples-per-row";
	public static final String MEF_BLOCK_NOS = "mef-block-nos";

	// /////////////////////// Lookup for dataset contents

	@GET
	@Path("getDataSnapshotTimeSeries/{datasnapshot}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	TimeSeriesList getDataSnapshotTimeSeries(
			@PathParam("datasnapshot") String dataSnapshot);

	@GET
	@Path("getDataSnapshotTimeSeriesDetails/{datasnapshot}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	TimeSeriesDetailList getDataSnapshotTimeSeriesDetails(
			@PathParam("datasnapshot") String dataSnapshot);

	// /////////////////////// Retrieve time series

	@POST
	@Path("getUnscaledTimeSeriesSetBinaryRaw/{datasnapshot}")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	Response getUnscaledTimeSeriesSetBinaryRaw(
			@PathParam("datasnapshot") String dataSnapshot,
			TimeSeriesIdAndDChecks timeSeriesIdAndDChecks,
			@QueryParam("start") double start,
			@QueryParam("duration") double duration);
	
	@POST
	@Path("getUnscaledTimeSeriesSetBinary/{datasnapshot}")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	Response getUnscaledTimeSeriesSetBinary(
			@PathParam("datasnapshot") String dataSnapshot,
			TimeSeriesRequestList seriesNames,
			@QueryParam("start") double start,
			@QueryParam("duration") double duration,
			@QueryParam("frequency") Double frequency);

	@POST
	@Path("getUnscaledTimeSeriesSetBinary/{datasnapshot}")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	Response getUnscaledTimeSeriesSetBinary(
			@PathParam("datasnapshot") String dataSnapshot,
			TimeSeriesRequestList seriesNames,
			@QueryParam("start") double start,
			@QueryParam("duration") double duration,
			@QueryParam("frequency") Double frequency,
			@QueryParam("processing") String processing);

	@POST
	@Path("getUnscaledTimeSeriesSetRawRed/{datasetId}")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	Response getUnscaledTimeSeriesSetRawRed(
			@PathParam("datasetId") String datasetId,
			TimeSeriesIdAndDChecks timeSeriesIdAndDChecks,
			@QueryParam("start") double start,
			@QueryParam("duration") double duration);
	
	@GET
	@Path("getEdfHeaderRaw/{datasetId}")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	Response getEdfHeaderRaw(
			@PathParam("datasetId") String datasetId);

	@POST
	@Path("deriveDataSnapshotFull/{previousRev}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	String deriveDataSnapshot(
			@QueryParam("friendlyName") String friendlyName,
			@PathParam("previousRev") String previousRevId,
			@QueryParam("toolName") String toolName);

	@POST
	@Path("deriveEmptyDataSnapshot/{friendlyName}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	String deriveEmptyDataSnapshot(
			@PathParam("friendlyName") String friendlyName,
			@QueryParam("previousRev") String previousRevId,
			@QueryParam("toolName") String toolName);

	@POST
	@Path("deriveDataSnapshotPartial/{friendlyName}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	String deriveDataSnapshot(
			@PathParam("friendlyName") String friendlyName,
			RevisionIdList revisionIdList,
			@QueryParam("previousRev") String previousRevId,
			@QueryParam("toolName") String toolName);

	@POST
	@Path("removeTsAnnotationsByLayer/{datasetId}/{layer}")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	TsAnnotationsDeleted removeTsAnnotationsByLayer(
			@PathParam("datasetId") String dataSnapshot,
			@PathParam("layer") String layer);

	@POST
	@Path("addAnnotationsToDataSnapshot/{datasnapshot}")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	String addAnnotationsToDataSnapshot(
			@PathParam("datasnapshot") String dataSnapshot,
			TimeSeriesAnnotationList annotations);

	@GET
	@Path("getAnnotationsListCSV/{datasnapshot}.csv")
	@Produces("text/csv")
	Response getAnnotationsListCSV(
			@QueryParam("timestamp") long timestamp,
			@QueryParam("signature") String signature,
			@PathParam("datasnapshot") String dataSnapshot);

	@GET
	@Path("getTraceListCSV/{datasnapshot}.csv")
	@Produces("application/gzip")
	Response getTraceListCSV(
			@PathParam("datasnapshot") String dataSnapshot,
			@QueryParam("list") String channelList,
			@QueryParam("start") long start,
			@QueryParam("duration") long duration,
			@QueryParam("frequency") double frequency,
			@QueryParam("filterType") String filterType,
			@QueryParam("filter") int filter,
			@QueryParam("poles") int numPoles,
			@QueryParam("bpLo") int bandpassLowCutoff,
			@QueryParam("bpHi") int bandpassHighCutoff,
			@QueryParam("bsLo") int bandstopLowCutoff,
			@QueryParam("bsHi") int bandstopHighCutoff
			);

	@GET
	@Path("getTraceListRawCSV/{datasnapshot}.csv")
	@Produces("application/gzip")
	Response getTraceListRawCSV(
			@PathParam("datasnapshot") String dataSnapshot,
			@QueryParam("list") String channelList,
			@QueryParam("start") long start,
			@QueryParam("duration") long duration,
			@QueryParam("sampleFactor") int sampleFactor
			);

	@GET
	@Path("test/{parm}")
	String test(@PathParam("parm") String parm);

	@GET
	@Path("getIdByDataSnapshotName/{datasnapshotname}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	String getDataSnapshotIdByName(
			@PathParam("datasnapshotname") String dsName);

	@GET
	@Path("getOwnedDataSnapshotNames/{owner}")
	String getOwnedDataSnapshotNames(
			@PathParam("owner") String owner);

	@GET
	@Path("getCountsByLayer/{dsId}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	CountsByLayer getCountsByLayer(
			@PathParam("dsId") String datasetId);

	@GET
	@Path("getTsAnnotations/{dsId}/{layer}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	TimeSeriesAnnotationList getTsAnnotations(
			@PathParam("dsId") String dsId,
			@QueryParam("startOffsetUsecs") long startOffsetUsecs,
			@PathParam("layer") String layer,
			@QueryParam("backwards") @Nullable Boolean ltStartOffsetUsecs,
			@QueryParam("firstResult") int firstResult,
			@QueryParam("maxResults") int maxResults);

	@POST
	@Path("setDataSnapshotName/{dataSnapshotId}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	void setDataSnapshotName(
			@PathParam("dataSnapshotId") String dataSnapshotId,
			@QueryParam("originalName") String originalName,
			@QueryParam("newName") String newName);

	@GET
	@Path("versionOkay/{matlabClientVersion}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	VersionOkay isVersionOkay(
			@PathParam("matlabClientVersion") String matlabClientVersion);

	@POST
	@Path("datasets/{datasetId}/tsAnnotations/{fromLayerName}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	TsAnnotationsMoved moveTsAnnotations(
			@PathParam("datasetId") String datasetId,
			@PathParam("fromLayerName") String fromLayerName,
			@QueryParam("toLayerName") String toLayerName);

	@GET
	@Path("{timeSeriesId}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	Response getTimeSeriesContent(
			@PathParam("timeSeriesId") String timeSeriesId,
			@HeaderParam("If-Match") @Nullable String eTag,
			@HeaderParam("Range") @Nullable String range);

}
