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

import java.io.Serializable;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ImageSpecifier implements Serializable, Comparable<ImageSpecifier> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory
			.getLogger(TimeSeries.class);

	private int type;
	private String fileKey;
	private String revId;

	public ImageSpecifier(@Nullable final String fileKey, final int type, @Nullable final String revId) {
		this.type = checkNotNull(type);
		this.fileKey = fileKey;
		this.revId = revId;
	}

	@SuppressWarnings("unused")
	private ImageSpecifier() {}

	/**
	 * @return the label
	 */
	@XmlElement
	public int getType() {
		return type;
	}

	/**
	 * @return the revId
	 */
	@Nullable
	@XmlElement
	public String getRevId() {
		return revId;
	}
	/**
	 * @return the file key
	 */
	@XmlElement
	public String getFileKey() {
		return fileKey;
	}


	/**
	 * @param label the label to set
	 */
	public void setType(int type ) {
		this.type = type;
	}

	/**
	 * @param fileKey the file's key
	 */
	public void setFileKey(@Nullable String fileKey) {
		this.fileKey = fileKey;
	}


	/**
	 * @param revId the revId to set
	 */
	public void setRevId(@Nullable String revId) {
		this.revId = revId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileKey == null) ? 0 : fileKey.hashCode());
		result = prime * result + ((revId == null) ? 0 : revId.hashCode());
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ImageSpecifier)) {
			return false;
		}
		ImageSpecifier other = (ImageSpecifier) obj;
		if (fileKey == null) {
			if (other.fileKey != null) {
				return false;
			}
		} else if (!fileKey.equals(other.fileKey)) {
			return false;
		}
		if (revId == null) {
			if (other.revId != null) {
				return false;
			}
		} else if (!revId.equals(other.revId)) {
			return false;
		}
		return type == other.type;
	}

	@Override
	public String toString() {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(TimeSeries.class);
			Marshaller marshaller = context.createMarshaller();
			StringWriter sw = new StringWriter();

			marshaller.marshal(this, sw);
			return sw.toString();
		} catch (JAXBException e) {
			logger.error("Error converting to String.", e);
		}
		return "";
	}

	@Override
	public int compareTo(ImageSpecifier o) {
		return fileKey.compareTo(o.fileKey);
	}

}
