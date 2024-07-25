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

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "timeseriesrequests")
public class TimeSeriesRequestList {
	public static final long serialVersionUID = 1L;

	private RevisionIdList revIds;
	private List<FilterSpec> filters;

	private static final Logger logger = LoggerFactory
			.getLogger(TimeSeriesRequestList.class);

	// For JAXB
	@SuppressWarnings("unused")
	private TimeSeriesRequestList() {
		this.revIds = new RevisionIdList();
		this.filters = newArrayList();
	}

	public TimeSeriesRequestList(final List<String> revIds,
			final List<FilterSpec> filters) {
		if (revIds.size() != filters.size()) {
			throw new IllegalArgumentException(
					"The number of revision IDs must be the same as the number of FilterSpecs");
		}
		this.revIds = new RevisionIdList(revIds);
		this.filters = newArrayList(filters);
	}
	
	public TimeSeriesRequestList(final List<String> revIds) {
		this.revIds = new RevisionIdList(revIds);
		this.filters = newArrayList();
	}

	public TimeSeriesRequestList(final String[] revIds,
			final FilterSpec[] filters) {
		this(Arrays.asList(revIds), Arrays.asList(filters));
	}

	public TimeSeriesRequestList(final String[] revIds) {
		this(Arrays.asList(revIds));
	}

	@XmlElement
	public RevisionIdList getRevisionIds() {
		return revIds;
	}

	public void setRevisionIds(final RevisionIdList revisionIdList) {
		this.revIds = revisionIdList;
	}

	@XmlElement(name = "filterspecs")
	public List<FilterSpec> getFilterSpecs() {
		return filters;
	}

	public void setFilterSpecs(final List<FilterSpec> filterSpecs) {
		this.filters = filterSpecs;
	}

	public String toString() {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(TimeSeriesRequestList.class);
			Marshaller marshaller = context.createMarshaller();
			StringWriter sw = new StringWriter();

			marshaller.marshal(this, sw); // save to StringWriter, you can then
											// call sw.toString() to get
											// java.lang.String
			return sw.toString();
		} catch (JAXBException e) {
			logger.error("Error creating String representation", e);
		}
		return "";
	}

}
