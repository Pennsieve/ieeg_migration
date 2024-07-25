/*
 * Copyright 2013 Trustees of the University of Pennsylvania
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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Sam Donnelly
 */
@XmlRootElement
public class TimeSeriesIds {
	private List<String> timeSeriesIds = newArrayList();

	public TimeSeriesIds() {

	}

	public TimeSeriesIds(List<String> timeSeriesIds) {
		this.timeSeriesIds = timeSeriesIds;
	}

	@XmlElementWrapper
	@XmlElement(name = "timeSeriesId")
	public List<String> getTimeSeriesIds() {
		return timeSeriesIds;
	}

	public void setTimeSeriesIds(List<String> timeSeriesIds) {
		this.timeSeriesIds = timeSeriesIds;
	}

	@Override
	public String toString() {
		return "TimeSeriesIds [timeSeriesIds=" + timeSeriesIds + "]";
	}

}
