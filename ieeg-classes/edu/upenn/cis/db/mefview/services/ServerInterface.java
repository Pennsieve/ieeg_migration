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

import static com.google.common.collect.Maps.newConcurrentMap;

import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.codec.digest.DigestUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

/**
 * Abstract base class for REST server interfaces
 * 
 * @author zives
 * 
 */
public abstract class ServerInterface {
	private String username;
	private ResteasyWebTarget rtarget;

	protected void setUserAndPassword() {
		UserAndPassword.set(
				username,
				usernames2HashedPasswords.get(username));
	}

	protected ResteasyWebTarget getResteasyWebTarget() {
		return rtarget;
	}

	protected String getUsername() {
		return username;
	}

	private ClientBuilder clientBuilder = null;

	private static Map<String, String> usernames2HashedPasswords = newConcurrentMap();

	public String getHashedPassword(String username) {
		return usernames2HashedPasswords.get(username);
	}

	protected ClientBuilder getClientBuilder() {
		return clientBuilder;
	}

	/**
	 * Constructor for remote server via Web services
	 * 
	 * @param url
	 * @param username
	 * @param password
	 */
	public ServerInterface(
			String url,
			String username,
			String password) {

		clientBuilder = ClientBuilder.newBuilder();

		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
		clientBuilder.hostnameVerifier(hostnameVerifier);
		SigClientRequestFilter sigClientRequestFilter =
				new SigClientRequestFilter();
		clientBuilder.register(sigClientRequestFilter);
		// clientBuilder.register(DebuggingClientResponseFilter.class);

		// resteasy-jaxrs providers
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.DataSourceProvider.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.DocumentProvider.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.DefaultTextPlain.class);
		clientBuilder.register(
				org.jboss.resteasy.plugins.providers.StringTextStar.class);
		clientBuilder.register(
				org.jboss.resteasy.plugins.providers.SourceProvider.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.InputStreamProvider.class);
		clientBuilder.register(
				org.jboss.resteasy.plugins.providers.ReaderProvider.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.ByteArrayProvider.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.FormUrlEncodedProvider.class);
		clientBuilder.register(
				org.jboss.resteasy.plugins.providers.FileProvider.class);
		clientBuilder.register(
				org.jboss.resteasy.plugins.providers.FileRangeWriter.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.StreamingOutputProvider.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.IIOImageProvider.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.SerializableProvider.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.interceptors.CacheControlFeature.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.interceptors.encoding.AcceptEncodingGZIPInterceptor.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.interceptors.encoding.AcceptEncodingGZIPFilter.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.interceptors.encoding.ClientContentEncodingAnnotationFeature.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.interceptors.encoding.GZIPDecodingInterceptor.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.interceptors.encoding.GZIPEncodingInterceptor.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.interceptors.encoding.ServerContentEncodingAnnotationFeature.class);

		// resteasy-jaxb-provider providers
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlSeeAlsoProvider.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlRootElementProvider.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.jaxb.JAXBElementProvider.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlTypeProvider.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.jaxb.CollectionProvider.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.jaxb.MapProvider.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.jaxb.XmlJAXBContextFinder.class);

		// resteasy-jettison-provider providers
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.jaxb.json.JsonCollectionProvider.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.jaxb.json.JsonMapProvider.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.jaxb.json.JsonJAXBContextFinder.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.jaxb.json.JettisonElementProvider.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.jaxb.json.JettisonXmlRootElementProvider.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.jaxb.json.JettisonXmlSeeAlsoProvider.class);
		clientBuilder
				.register(
				org.jboss.resteasy.plugins.providers.jaxb.json.JettisonXmlTypeProvider.class);

		this.username = username;
		String hashedPassword = DigestUtils.md5Hex(password);
		usernames2HashedPasswords.put(username, hashedPassword);

		final String proxyHost = System.getProperty("http.proxyHost");
		if (proxyHost != null) {
			ResteasyClientBuilder rClientBuilder = (ResteasyClientBuilder) clientBuilder;
			final String proxyPortStr = System.getProperty(
					"http.proxyPort",
					"-1");
			int proxyPort = -1;
			try {
				proxyPort = Integer.valueOf(proxyPortStr);
			} catch (NumberFormatException e) {
				System.err
						.println("Ignoring invalid value for http.proxyPort: ["
								+ proxyPortStr + "]");
			}
			System.out.println("HTTP proxy host: " + proxyHost);
			if (proxyPort != -1) {
				System.out.println("HTTP proxy port: " + proxyPort);
			}
			rClientBuilder.defaultProxy(proxyHost, proxyPort, "http");
		}
		Client client = clientBuilder.build();
		WebTarget target = client.target(url);
		rtarget = (ResteasyWebTarget) target;

		System.out.println("URL: " + url);
		System.out.println("Client user: " + username);
		System.out.println("Client password: ****");
	}

}
