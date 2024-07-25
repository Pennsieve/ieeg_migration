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

import static com.google.common.collect.Lists.newArrayList;

import static com.google.common.collect.Sets.newTreeSet;

import java.io.StringWriter;
import java.util.List;
import java.util.SortedSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "timeseriesannotations")
@XmlAccessorType(XmlAccessType.NONE)
public class TimeSeriesAnnotationList {
	public static final long serialVersionUID = 1L;

	@XmlElementWrapper(name = "timeseries")
	@XmlElement(name = "timeseries")
	private SortedSet<TimeSeries> timeSeries = newTreeSet();

	@XmlElementWrapper(name = "annotations")
	@XmlElement(name = "annotation")
	private List<TimeSeriesAnnotation> tsa = newArrayList();

	private static final Logger logger = LoggerFactory
			.getLogger(TimeSeriesAnnotationList.class);

	// For JAXB
	@SuppressWarnings("unused")
	private TimeSeriesAnnotationList() {}

	public TimeSeriesAnnotationList(List<TimeSeriesAnnotation> t) {
		this.tsa.addAll(t);
		for (final TimeSeriesAnnotation a : this.tsa) {
			timeSeries.addAll(a.getAnnotatedSeries());
		}
	}

	public List<TimeSeriesAnnotation> getList() {
		return tsa;
	}

	public TimeSeriesAnnotation[] getArray() {
		TimeSeriesAnnotation[] ret = new TimeSeriesAnnotation[tsa.size()];
		tsa.toArray(ret);

		return ret;
	}

	@Override
	public String toString() {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(TimeSeriesAnnotationList.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter sw = new StringWriter();

			marshaller.marshal(this, sw);
			return sw.toString();
		} catch (JAXBException e) {
			logger.error("Error converting to String.", e);
		}
		return "";
	}
}
