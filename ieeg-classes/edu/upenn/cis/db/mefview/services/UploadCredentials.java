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

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@Immutable
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public final class UploadCredentials {

	private String id;
	private String secretKey;
	private String sessionToken;
	private String authorizedBucket;
	private String authorizedPrefix;

	/** For JAXB. */
	@SuppressWarnings("unused")
	private UploadCredentials() {}

	public UploadCredentials(
			String id,
			String authorizedBucket,
			String authorizedPrefix,
			String secretKey,
			String sessionToken) {
		this(
				id,
				authorizedBucket,
				authorizedPrefix);
		this.secretKey = checkNotNull(secretKey);
		this.sessionToken = checkNotNull(sessionToken);
	}

	public UploadCredentials(
			String id,
			String authorizedBucket,
			String authorizedPrefix) {
		this.id = checkNotNull(id);
		this.authorizedBucket = checkNotNull(authorizedBucket);
		this.authorizedPrefix = checkNotNull(authorizedPrefix);
	}

	@XmlAttribute
	public String getAuthorizedBucket() {
		return authorizedBucket;
	}

	@XmlAttribute
	public String getAuthorizedPrefix() {
		return authorizedPrefix;
	}

	@XmlAttribute
	public String getId() {
		return id;
	}

	@Nullable
	@XmlAttribute
	public String getSessionToken() {
		return sessionToken;
	}

	@Nullable
	@XmlAttribute
	public String getSecretKey() {
		return secretKey;
	}

	@SuppressWarnings("unused")
	private void setAuthorizedBucket(String authorizedBucket) {
		this.authorizedBucket = authorizedBucket;
	}

	@SuppressWarnings("unused")
	private void setAuthorizedPrefix(String authorizedPrefix) {
		this.authorizedPrefix = authorizedPrefix;
	}

	@SuppressWarnings("unused")
	private void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	@SuppressWarnings("unused")
	private void setId(String id) {
		this.id = id;
	}

	@SuppressWarnings("unused")
	private void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

}
