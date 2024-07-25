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

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "IeegWsException")
@XmlAccessorType(XmlAccessType.NONE)
public class IeegWsExceptionBody {

	private String errorCode;
	private String message;
	private String errorId;
	private String hostname;

	// For JAXB
	@SuppressWarnings("unused")
	private IeegWsExceptionBody() {}

	public IeegWsExceptionBody(String errorCode,
			final String message, String hostname, final String errorId) {
		this.errorCode = errorCode;
		this.message = message;
		this.hostname = hostname;
		this.errorId = errorId;
	}

	@XmlElement
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@XmlElement
	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	@XmlElement
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	@XmlElement
	public String getErrorId() {
		return errorId;
	}

	public void setErrorId(final String errorId) {
		this.errorId = errorId;
	}

}
