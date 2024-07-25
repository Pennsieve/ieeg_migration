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

import javax.ws.rs.core.Response;

/**
 * Error codes returned by web services
 * 
 * @author John Frommeyer
 */
public enum IeegWsError {
	// Following the example of AWS S3 where both authc and authz errors are
	// mapped to Forbidden.
	INTERNAL_ERROR(
			"InternalError",
			Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()),
	AUTHENTICATION_FAILURE(
			"AuthenticationFailure",
			Response.Status.FORBIDDEN.getStatusCode()),
	AUTHENTICATION_FAILURE_TIMESTAMP_IN_RANGE(
			"AuthenticationFailureSigExpired",
			Response.Status.FORBIDDEN.getStatusCode()),
	AUTHORIZATION_FAILURE(
			"AuthorizationFailure",
			Response.Status.FORBIDDEN.getStatusCode()),
	NO_SUCH_DATA_SNAPSHOT(
			"NoSuchDataSnapshot",
			Response.Status.NOT_FOUND.getStatusCode()),
	NO_SUCH_RECORDING(
			"NoSuchRecording",
			Response.Status.NOT_FOUND.getStatusCode()),
	NO_SUCH_TIME_SERIES("TimeSeries.NoSuchTimeSeries",
			Response.Status.NOT_FOUND.getStatusCode()),
	STALE_DATA_EXCEPTION(
			"StaleDataException",
			Response.Status.CONFLICT.getStatusCode()),
	DUPLICATE_NAME(
			"DuplicateName",
			Response.Status.CONFLICT.getStatusCode()),
	TOO_MUCH_DATA_REQUESTED(
			"TooMuchDataRequested",
			Response.Status.FORBIDDEN.getStatusCode()),
	STALE_MEF(
			"StaleMEFData",
			Response.Status.GONE.getStatusCode()),
	SERVER_TIMEOUT(
			"ServerTimeout",
			Response.Status.SERVICE_UNAVAILABLE.getStatusCode()),
	BAD_TS_ANN_TIME(
			"BadTsAnnTime",
			Response.Status.BAD_REQUEST.getStatusCode()),
	TOO_MANY_REQUESTS("TooManyRequests",
			Response.Status.SERVICE_UNAVAILABLE.getStatusCode()),
	SERVER_BUSY(
			"ServerBusy",
			Response.Status.SERVICE_UNAVAILABLE.getStatusCode()),
	PRECONDITION_FAILED(
			"PreconditionFailed",
			Response.Status.PRECONDITION_FAILED.getStatusCode()),
	BAD_OBJECT_NAME(
			"BadObjectName",
			Response.Status.BAD_REQUEST.getStatusCode()),
	BAD_DIGEST(
			"BadDigest",
			Response.Status.BAD_REQUEST.getStatusCode()),
	UPLOAD_IN_PROGRESS(
			"UploadInProgress",
			Response.Status.CONFLICT.getStatusCode()),
	NO_SUCH_RECORDING_OBJECT(
			"NoSuchRecordingObject",
			Response.Status.NOT_FOUND.getStatusCode()),
	INVALID_RANGE(
			"InvalidRange",
			Response.Status.REQUESTED_RANGE_NOT_SATISFIABLE.getStatusCode()),
	INBOX_CREDENTIALS_CONFLICT(
			"InboxCredentialsConflict",
			Response.Status.CONFLICT.getStatusCode()),
	DATASET_DELETION_CONFLICT(
			"DatasetDeletionConflict",
			Response.Status.CONFLICT.getStatusCode()),
	NO_SUCH_MONTAGE(
			"NoSuchMontage",
			Response.Status.NOT_FOUND.getStatusCode()), ;

	private final String code;
	private final int httpStatus;

	IeegWsError(String code, int httpStatus) {
		this.code = code;
		this.httpStatus = httpStatus;
	}

	public String getCode() {
		return code;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

}
