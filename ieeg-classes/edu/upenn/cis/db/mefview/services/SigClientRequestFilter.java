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

import static com.google.common.collect.Maps.newHashMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

@Provider
public class SigClientRequestFilter implements ClientRequestFilter {

	private static Logger logger =
			LoggerFactory.getLogger(SigClientRequestFilter.class);
	private final String signatureAlgorithm = "SHA-256";
	private SignatureGenerator sigGenerator = new SignatureGenerator();

	@SuppressWarnings("unchecked")
	@Override
	public void filter(ClientRequestContext context) throws IOException {
		final String m = "filter(...)";
		try {
			logger.debug("{}: method: [{}]", m, context.getMethod());
			String host = context.getUri().getHost();
			logger.debug("{}: host: [{}]", m, host);
			logger.debug("{}: path: [{}]",
					m,
					context.getUri().getPath());
			logger.debug("{}: query string: [{}]",
					m,
					context.getUri().getQuery());
			logger.debug("{}: properties: {}",
					m,
					context.getPropertyNames());
			TimeZone tz = TimeZone.getTimeZone("UTC");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			df.setTimeZone(tz);
			String nowAsISO = df.format(new Date());
			context.getHeaders().put(
					"timestamp",
					Collections.<Object> singletonList(nowAsISO));
			logger.debug("{}: timestamp: {}",
					m,
					nowAsISO);

			Optional<String> usernameOpt = UserAndPassword.getUsername();
			if (!usernameOpt.isPresent()) {

			} else {
				String username = usernameOpt.get();
				context.getHeaders().put(
						"username",
						Collections.<Object> singletonList(usernameOpt.get()));
				String hashedPassword =
						UserAndPassword.getHashedPassword().get();

				logger.debug("{}: username: [{}]",
						m,
						usernameOpt);
				final boolean isCreateObject = context
						.getUri()
						.getPath()
						.matches(IDatasetResource.CREATE_OBJECT_URL_REG_EXP_STR)
						&& context
								.getMethod()
								.equals(HttpMethod.POST);
				String payload = "";
				if (isCreateObject) {
					payload = context.getHeaderString("Content-MD5");
					logger.debug(
							"{}: Using Content-MD5 value for payload of create recording object payload",
							m);
				} else if (context.getEntity() != null) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					ClientConfiguration clientConfig =
							(ClientConfiguration) context.getConfiguration();

					@SuppressWarnings("rawtypes")
					MessageBodyWriter writer =
							clientConfig
									.getMessageBodyWriter(
											context.getEntityClass(),
											context.getEntityType(),
											context.getEntityAnnotations(),
											context.getMediaType());
					writer.writeTo(
							context.getEntity(),
							context.getEntityClass(),
							context.getEntityType(),
							context.getEntityAnnotations(),
							context.getMediaType(),
							context.getHeaders(),
							out);
					payload = new String(out.toByteArray());
				}
				logger.debug("{}: payload: [{}]", m, payload);

				String queryString = context.getUri().getQuery();
				if (queryString == null) {
					queryString = "";
				}

				String sig = sigGenerator.getRequestSignature(
						signatureAlgorithm,
						username,
						hashedPassword,
						context.getMethod(),
						host,
						context.getUri().getPath(),
						queryString,
						nowAsISO,
						payload);
				logger.debug("{}: sig: [{}]", m, sig);
				context.getHeaders().put(
						"signature",
						Collections.<Object> singletonList(sig));
			}
			logger.debug("{}: headers: {}", m,
					newHashMap(context.getStringHeaders()));
		} finally {
			// So subsequent requests do not retain the username and password
			// use case: they may want to not send it.
			UserAndPassword.clear();
		}
	}
}
