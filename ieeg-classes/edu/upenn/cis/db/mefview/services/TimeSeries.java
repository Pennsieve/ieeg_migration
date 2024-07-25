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
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class TimeSeries implements Serializable, Comparable<TimeSeries> {

	public static Function<TimeSeries, String> getLabel = new Function<TimeSeries, String>() {

		@Override
		public String apply(TimeSeries input) {
			return input.getLabel();
		}
	};

	public static Function<TimeSeries, String> getRevIdFunc = new Function<TimeSeries, String>() {

		@Override
		public String apply(TimeSeries input) {
			return input.getRevId();
		}
	};

	private static final long serialVersionUID = 1L;
	private String label;
	private String revId;

	private static final Logger logger = LoggerFactory
			.getLogger(TimeSeries.class);

	public TimeSeries(final String revId, final String label) {
		this.revId = checkNotNull(revId);
		this.label = checkNotNull(label);
	}

	@SuppressWarnings("unused")
	private TimeSeries() {}

	/**
	 * @return the label
	 */
	@XmlElement
	public String getLabel() {
		return label;
	}

	/**
	 * @return the revId
	 */
	@XmlID
	public String getRevId() {
		return revId;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = checkNotNull(label);
	}

	/**
	 * @param revId the revId to set
	 */
	public void setRevId(String revId) {
		this.revId = checkNotNull(revId);
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
	public int compareTo(TimeSeries o) {
		final int labelCmp = label.compareTo(o.label);
		return (labelCmp == 0) ? revId.compareTo(o.revId) : labelCmp;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((revId == null) ? 0 : revId.hashCode());
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
		if (!(obj instanceof TimeSeries)) {
			return false;
		}
		TimeSeries other = (TimeSeries) obj;
		if (revId == null) {
			if (other.revId != null) {
				return false;
			}
		} else if (!revId.equals(other.revId)) {
			return false;
		}
		return true;
	}
}
