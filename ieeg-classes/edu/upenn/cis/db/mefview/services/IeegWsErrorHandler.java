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

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.concurrent.ThreadSafe;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper methods to convert error responses into {@link IeegWsRemoteException}.
 * 
 * @author John Frommeyer
 * 
 */
@ThreadSafe
public final class IeegWsErrorHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * For services which return a Response.
	 * 
	 * @param response
	 */
	public void handleErrorClientResponse(Response response) {
		if (response.getStatus() >= 400) {
			throw responseToException(response);
		}
	}

	/**
	 * For services which do not return a Response.
	 * 
	 * @param e
	 * @return
	 */
	public IeegWsRemoteException handleWebApplicationException(
			WebApplicationException e) {
		Response response = e.getResponse();
		return responseToException(response);
	}

	private IeegWsRemoteException responseToException(Response response) {
		final String m = "responseToException(...)";
		checkNotNull(response);
		final int status = response.getStatus();
		try {
			final IeegWsExceptionBody error = response
					.readEntity(IeegWsExceptionBody.class);

			return new IeegWsRemoteAppException(
					error.getMessage(),
					error.getErrorCode(),
					error.getErrorId(),
					error.getHostname());
		} catch (Exception e) {
			logger.debug(
					"{}: The server returned an error we could not read as an IeegWsExceptionBody. "
							+ "Here we are logging our failure to read. The original error will be thrown "
							+ "as an IeegWsRemoteException. Original Exception: {}: {}",
					m,
					e.getClass().getName(),
					e.getMessage());
			String msg = "An error response with status "
					+ response.getStatus()
					+ " ("
					+ Response.Status.fromStatusCode(status)
							.getReasonPhrase()
					+ ") was returned from the server";
			return new IeegWsRemoteException(
					msg);
		}
	}

}
