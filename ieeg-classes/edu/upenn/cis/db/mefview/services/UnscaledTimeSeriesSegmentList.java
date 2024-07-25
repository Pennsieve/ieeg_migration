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
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "unscaledtimeseriessegments")
public class UnscaledTimeSeriesSegmentList {
	public static final long serialVersionUID = 1L;

	private List<UnscaledTimeSeriesSegment> utss;

	public UnscaledTimeSeriesSegmentList() {
		utss = new ArrayList<UnscaledTimeSeriesSegment>();
	}

	public UnscaledTimeSeriesSegmentList(List<UnscaledTimeSeriesSegment> t) {
		utss = t;
	}

	public UnscaledTimeSeriesSegmentList(Collection<UnscaledTimeSeriesSegment> t) {
		utss = new ArrayList<UnscaledTimeSeriesSegment>();
		utss.addAll(t);
	}

	public UnscaledTimeSeriesSegmentList(UnscaledTimeSeriesSegment[] t) {
		utss = new ArrayList<UnscaledTimeSeriesSegment>(Arrays.asList(t));
	}

	@XmlElement(name = "segment")
	public List<UnscaledTimeSeriesSegment> getList() {
		return utss;
	}

	public UnscaledTimeSeriesSegment[] getArray() {
		UnscaledTimeSeriesSegment[] ret = new UnscaledTimeSeriesSegment[utss
				.size()];

		utss.toArray(ret);

		return ret;
	}
}
