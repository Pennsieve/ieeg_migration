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
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "timeseriestasks")
public class TimeSeriesTaskList {
	private List<TimeSeriesTask> taskSpecs;
	private static final Logger logger = LoggerFactory
			.getLogger(TimeSeriesTaskList.class);

	/** For JAXB */
	TimeSeriesTaskList() {
		this.taskSpecs = newArrayList();
	}

	public TimeSeriesTaskList(final List<TimeSeriesTask> taskIds) {
		this.taskSpecs = newArrayList(taskIds);
	}

	public TimeSeriesTaskList(final TimeSeriesTask[] taskIds) {
		this.taskSpecs = newArrayList(taskIds);
	}

	@XmlElement(name = "task")
	public List<TimeSeriesTask> getTasks() {
		return taskSpecs;
	}

	public void setTasks(final List<TimeSeriesTask> tasks) {
		this.taskSpecs = tasks;
	}

	public String toString() {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(TimeSeriesTaskList.class);
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
