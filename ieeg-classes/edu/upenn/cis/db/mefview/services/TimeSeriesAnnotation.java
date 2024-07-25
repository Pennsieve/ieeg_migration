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
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Sets.newTreeSet;

import java.io.StringWriter;
import java.util.SortedSet;

import javax.annotation.Nullable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class TimeSeriesAnnotation {
	private SortedSet<TimeSeries> annotated = newTreeSet();
	private String type;
	private String annotator;

	private String description;
	private Long startTimeUutc;
	private Long endTimeUutc;
	private String revId;
	private String layer;
	private String color;

	private static final Logger logger = LoggerFactory
			.getLogger(TimeSeriesAnnotation.class);

	/**
	 * @deprecated Replaced by
	 *             {@link #TimeSeriesAnnotation(SortedSet, String, double, double, String, String)}
	 *             .
	 * @param annotated
	 * @param annotator
	 * @param startTimeUutcU
	 * @param endTimeUutc
	 * @param type
	 */
	@Deprecated
	public TimeSeriesAnnotation(
			final TimeSeries annotated,
			final String annotator,
			final double startTimeUutc,
			final double endTimeUutc,
			final String type) {
		this.annotated.add(checkNotNull(annotated));
		this.type = checkNotNull(type);
		this.annotator = checkNotNull(annotator);
		this.startTimeUutc = (long) startTimeUutc;
		this.endTimeUutc = (long) endTimeUutc;
		this.color = null;
	}

	/**
	 * @deprecated Replaced by
	 *             {@link #TimeSeriesAnnotation(SortedSet, String, Long, Long, String, String)}
	 * 
	 * @param annotated
	 * @param annotator
	 * @param startTimeUutc
	 * @param endTimeUutc
	 * @param type
	 */
	@Deprecated
	public TimeSeriesAnnotation(
			final TimeSeries annotated,
			final String annotator,
			final Long startTimeUutc,
			final Long endTimeUutc,
			final String type) {
		this.annotated.add(checkNotNull(annotated));
		this.type = checkNotNull(type);
		this.annotator = checkNotNull(annotator);
		this.startTimeUutc = checkNotNull(startTimeUutc);
		this.endTimeUutc = checkNotNull(endTimeUutc);
		this.color = null;
	}

	@Deprecated
	public TimeSeriesAnnotation(
			final SortedSet<TimeSeries> annotated,
			final String annotator,
			final Long startTimeUutc,
			final Long endTimeUutc,
			final String type,
			@Nullable String description,
			@Nullable String layer) {
		this.annotated = checkNotNull(annotated);
		this.type = checkNotNull(type);
		this.annotator = checkNotNull(annotator);
		this.startTimeUutc = checkNotNull(startTimeUutc);
		this.endTimeUutc = checkNotNull(endTimeUutc);
		this.description = description;
		this.layer = layer;
		this.color = null;
	}
	
	public TimeSeriesAnnotation(
			final SortedSet<TimeSeries> annotated,
			final String annotator,
			final Long startTimeUutc,
			final Long endTimeUutc,
			final String type,
			@Nullable String description,
			@Nullable String layer,
			@Nullable String color) {
		this.annotated = checkNotNull(annotated);
		this.type = checkNotNull(type);
		this.annotator = checkNotNull(annotator);
		this.startTimeUutc = checkNotNull(startTimeUutc);
		this.endTimeUutc = checkNotNull(endTimeUutc);
		this.description = description;
		this.layer = layer;
		this.color = color;
	}

	@Deprecated
	public TimeSeriesAnnotation(
			final SortedSet<TimeSeries> annotated,
			final String annotator,
			final double startTimeUutc,
			final double endTimeUutc,
			final String type,
			@Nullable String description,
			@Nullable String layer) {
		this.annotated = checkNotNull(annotated);
		this.type = checkNotNull(type);
		this.annotator = checkNotNull(annotator);
		this.startTimeUutc = (long) startTimeUutc;
		this.endTimeUutc = (long) endTimeUutc;
		this.description = description;
		this.layer = layer;
	}
	
	public TimeSeriesAnnotation(
			final SortedSet<TimeSeries> annotated,
			final String annotator,
			final double startTimeUutc,
			final double endTimeUutc,
			final String type,
			@Nullable String description,
			@Nullable String layer,
			@Nullable String color) {
		this.annotated = checkNotNull(annotated);
		this.type = checkNotNull(type);
		this.annotator = checkNotNull(annotator);
		this.startTimeUutc = (long) startTimeUutc;
		this.endTimeUutc = (long) endTimeUutc;
		this.description = description;
		this.layer = layer;
		this.color = color;
	}

	@Deprecated
	public TimeSeriesAnnotation(final String annotator,
			final Long startTimeUutc,
			final Long endTimeUutc,
			final String type,
			@Nullable String description,
			@Nullable String layer) {
		this.annotated = checkNotNull(annotated);
		this.type = checkNotNull(type);
		this.annotator = checkNotNull(annotator);
		this.startTimeUutc = checkNotNull(startTimeUutc);
		this.endTimeUutc = checkNotNull(endTimeUutc);
		this.description = description;
		this.layer = layer;
	}

	public TimeSeriesAnnotation(final String annotator,
			final Long startTimeUutc,
			final Long endTimeUutc,
			final String type,
			@Nullable String description,
			@Nullable String layer,
			@Nullable String color) {
		this.annotated = checkNotNull(annotated);
		this.type = checkNotNull(type);
		this.annotator = checkNotNull(annotator);
		this.startTimeUutc = checkNotNull(startTimeUutc);
		this.endTimeUutc = checkNotNull(endTimeUutc);
		this.description = description;
		this.layer = layer;
		this.color = color;
	}

	@Deprecated
	public TimeSeriesAnnotation(final String annotator,
			final Long startTimeUutc,
			final Long endTimeUutc,
			final String type) {
		this(annotator, startTimeUutc, endTimeUutc, type, null, null);
	}

	public TimeSeriesAnnotation(final String annotator,
			final Long startTimeUutc,
			final Long endTimeUutc,
			final String type,
			final String color) {
		this(annotator, startTimeUutc, endTimeUutc, type, null, null, color);
	}

	@Deprecated
	public TimeSeriesAnnotation(final String annotator,
			final double startTimeUutc,
			final double endTimeUutc,
			final String type,
			@Nullable String description,
			@Nullable String layer) {
		this.annotated = checkNotNull(annotated);
		this.type = checkNotNull(type);
		this.annotator = checkNotNull(annotator);
		this.startTimeUutc = (long) startTimeUutc;
		this.endTimeUutc = (long) endTimeUutc;
		this.description = description;
		this.layer = layer;
	}
	
	public TimeSeriesAnnotation(final String annotator,
			final double startTimeUutc,
			final double endTimeUutc,
			final String type,
			@Nullable String description,
			@Nullable String layer,
			@Nullable String color) {
		this.annotated = checkNotNull(annotated);
		this.type = checkNotNull(type);
		this.annotator = checkNotNull(annotator);
		this.startTimeUutc = (long) startTimeUutc;
		this.endTimeUutc = (long) endTimeUutc;
		this.description = description;
		this.layer = layer;
		this.color = color;
	}

	@Deprecated
	public TimeSeriesAnnotation(final String annotator,
			final double startTimeUutc,
			final double endTimeUutc,
			final String type) {
		this(annotator, startTimeUutc, endTimeUutc, type, null, null);
	}

	public TimeSeriesAnnotation(final String annotator,
			final double startTimeUutc,
			final double endTimeUutc,
			final String type,
			final String color) {
		this(annotator, startTimeUutc, endTimeUutc, type, null, null, color);
	}

	// For JAXB
	@SuppressWarnings("unused")
	private TimeSeriesAnnotation() {}

	/**
	 * @return the annotated
	 */
	@XmlElementWrapper(name = "timeseriesRevIds")
	@XmlElement(name = "timeseriesRevId")
	@XmlIDREF
	public SortedSet<TimeSeries> getAnnotatedSeries() {
		return annotated;
	}

	/**
	 * @deprecated Replaced by {@link #getAnnotatedSeries()}
	 * @return the annotated
	 */
	@Deprecated
	public TimeSeries getAnnotated() {
		checkState(
				annotated.size() == 1,
				"This annotation annotates more than one time series. Use getAnnotatedSeries() instead.");
		return getOnlyElement(annotated);
	}

	/**
	 * @return the type
	 */
	@XmlElement
	public String getType() {
		return type;
	}

	/**
	 * @return the annotator
	 */
	@XmlElement
	public String getAnnotator() {
		return annotator;
	}

	/**
	 * @return the description
	 */
	@Nullable
	@XmlElement
	public String getDescription() {
		return description;
	}
	
	@Nullable
	@XmlElement
	public String getColor() {
		return color;
	}

	/**
	 * @return the layer
	 */
	@Nullable
	@XmlElement
	public String getLayer() {
		return layer;
	}

	/**
	 * @return the startTimeUutc
	 */
	@XmlElement
	public Long getStartTimeUutc() {
		return startTimeUutc;
	}

	/**
	 * @return the endTimeUutc
	 */
	@XmlElement
	public Long getEndTimeUutc() {
		return endTimeUutc;
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
	 * Replaces annotated series with {@code annotated}.
	 * 
	 * @deprecated Replaced by {@link #setAnnotated(SortedSet)
	 * @param annotated the annotated to set
	 */
	@Deprecated
	public void setAnnotated(TimeSeries annotated) {
		checkNotNull(annotated);
		this.annotated.clear();
		this.annotated.add(annotated);
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = checkNotNull(type);
	}

	/**
	 * @param annotator the annotator to set
	 */
	public void setAnnotator(String annotator) {
		this.annotator = checkNotNull(annotator);
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(@Nullable String description) {
		this.description = description;
	}

	/**
	 * @param layer the layer to set
	 */
	public void setLayer(@Nullable String layer) {
		this.layer = layer;
	}
	
	public void setColor(@Nullable String color) {
		this.color = color;
	}

	/**
	 * @param startTimeUutc the startTimeUutc to set
	 */
	public void setStartTimeUutc(Long startTimeUutc) {
		this.startTimeUutc = checkNotNull(startTimeUutc);
	}

	/**
	 * @param endTimeUutc the endTimeUutc to set
	 */
	public void setEndTimeUutc(Long endTimeUutc) {
		this.endTimeUutc = checkNotNull(endTimeUutc);
	}

	/**
	 * @param revId the revId to set
	 */
	public void setRevId(@Nullable String revId) {
		this.revId = revId;
	}

	/**
	 * @param annotated the annotated to set
	 */
	public void setAnnotated(SortedSet<TimeSeries> annotated) {
		this.annotated = checkNotNull(annotated);
	}

	public void addAnnotated(TimeSeries annotated) {
		this.annotated.add(checkNotNull(annotated));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((annotated == null) ? 0 : annotated.hashCode());
		result = prime * result
				+ ((annotator == null) ? 0 : annotator.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((endTimeUutc == null) ? 0 : endTimeUutc.hashCode());
		result = prime * result + ((revId == null) ? 0 : revId.hashCode());
		result = prime * result
				+ ((startTimeUutc == null) ? 0 : startTimeUutc.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (!(obj instanceof TimeSeriesAnnotation)) {
			return false;
		}
		TimeSeriesAnnotation other = (TimeSeriesAnnotation) obj;
		if (annotated == null) {
			if (other.annotated != null) {
				return false;
			}
		} else if (!annotated.equals(other.annotated)) {
			return false;
		}
		if (annotator == null) {
			if (other.annotator != null) {
				return false;
			}
		} else if (!annotator.equals(other.annotator)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (endTimeUutc == null) {
			if (other.endTimeUutc != null) {
				return false;
			}
		} else if (!endTimeUutc.equals(other.endTimeUutc)) {
			return false;
		}
		if (revId == null) {
			if (other.revId != null) {
				return false;
			}
		} else if (!revId.equals(other.revId)) {
			return false;
		}
		if (startTimeUutc == null) {
			if (other.startTimeUutc != null) {
				return false;
			}
		} else if (!startTimeUutc.equals(other.startTimeUutc)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(TimeSeriesAnnotation.class);
			Marshaller marshaller = context.createMarshaller();
			StringWriter sw = new StringWriter();

			marshaller.marshal(this, sw);
			return sw.toString();
		} catch (JAXBException e) {
			logger.error("Error converting to String.", e);
		}
		return "";
	}

}
