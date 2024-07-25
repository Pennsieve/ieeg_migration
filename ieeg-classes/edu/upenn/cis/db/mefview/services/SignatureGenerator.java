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

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates a signature for web services.
 * 
 * @author John Frommeyer
 */
public class SignatureGenerator {
	private static Logger logger =
			LoggerFactory.getLogger(SignatureGenerator.class);

	/**
	 * Returns a base 64 encoded signature on the non-{@code algorithm} args
	 * using {@code algorithm}.
	 * 
	 * @throws IllegalArgumentException if {@code AlgorithmMethod} is not
	 *             available
	 */
	public String getRequestSignature(
			String algorithm,
			String username,
			String md5Passwd,
			String httpMethod,
			String host,
			String path,
			String queryString,
			String reqTimeISO8601,
			String payload) {
		final String m = "getRequestSignature(...)";

		checkNotNull(algorithm);
		checkNotNull(username);
		checkNotNull(md5Passwd);
		checkNotNull(httpMethod);
		checkNotNull(host);
		checkNotNull(path);
		checkNotNull(queryString);
		checkNotNull(reqTimeISO8601);
		checkNotNull(payload);

		MessageDigest hash = null;
		try {
			hash = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		}
		String payloadHash =
				Base64.encodeBase64String(hash.digest(payload.getBytes()));
		String toBeHashed =
				username
						+ '\n'
						+ md5Passwd
						+ '\n'
						+ httpMethod
						+ '\n'
						+ host
						+ '\n'
						+ path
						+ '\n'
						+ queryString
						+ '\n'
						+ reqTimeISO8601
						+ '\n'
						+ payloadHash;
		logger.debug("{}: string to be signed: [{}]", m, toBeHashed);
		String signature =
				Base64.encodeBase64String(hash.digest(toBeHashed.getBytes()));
		return signature;
	}
}
