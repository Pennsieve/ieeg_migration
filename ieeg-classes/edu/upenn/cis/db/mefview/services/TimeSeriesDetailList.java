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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "timeseriesdetails")
public class TimeSeriesDetailList {
	public static final long serialVersionUID = 1L;

	@XmlElementWrapper(name = "details")
	@XmlElement(name = "detail")
	private List<TimeSeriesDetails> tsd;

	public TimeSeriesDetailList() {
		tsd = new ArrayList<TimeSeriesDetails>();
	}

	public TimeSeriesDetailList(List<TimeSeriesDetails> t) {
		tsd = t;
	}

	public TimeSeriesDetailList(Collection<TimeSeriesDetails> t) {
		tsd = new ArrayList<TimeSeriesDetails>();
		tsd.addAll(t);
		Collections.sort(tsd);
	}

	public TimeSeriesDetailList(TimeSeriesDetails[] t) {
		tsd = new ArrayList<TimeSeriesDetails>(Arrays.asList(t));
		Collections.sort(tsd);
	}

	public List<TimeSeriesDetails> getList() {
		return tsd;
	}

	public TimeSeriesDetails[] getArray() {
		TimeSeriesDetails[] ret = new TimeSeriesDetails[tsd.size()];

		tsd.toArray(ret);

		return ret;
	}

}
