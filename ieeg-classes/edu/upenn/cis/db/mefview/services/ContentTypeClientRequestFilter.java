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

import java.io.IOException;
import java.net.URLConnection;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ContentTypeClientRequestFilter implements ClientRequestFilter {

	private final Logger logger =
			LoggerFactory.getLogger(getClass());

	@Override
	public void filter(ClientRequestContext context) throws IOException {
		final String m = "filter(...)";
		String path = context.getUri().getPath();
		logger.debug(
				"{}: path: [{}]",
				m,
				path);
		if (context.getMethod().equals(HttpMethod.POST)
				&& path.matches(IDatasetResource.CREATE_OBJECT_URL_REG_EXP_STR)) {
			final String filename = context.getHeaderString("filename");
			final String mediaTypeGuess = URLConnection
					.guessContentTypeFromName(filename);
			final MediaType contentType = mediaTypeGuess == null
					? MediaType.APPLICATION_OCTET_STREAM_TYPE
					: MediaType.valueOf(mediaTypeGuess);
			logger.debug(
					"{}: Setting content type to [{}]",
					m,
					contentType);
			context.getHeaders().putSingle(
					HttpHeaders.CONTENT_TYPE,
					contentType);
		}
	}
}
