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

@XmlRootElement(name="timeseries")
public class TimeSeriesList {
		public static final long serialVersionUID = 1L;
		
		@XmlElementWrapper(name = "series")
		@XmlElement(name="trace")
		private List<TimeSeries> ts;
		
		public TimeSeriesList() {
			ts = new ArrayList<TimeSeries>();
		}
		
		public TimeSeriesList(List<TimeSeries> t) {
			ts = t;
		}
		
		public TimeSeriesList(Collection<TimeSeries> t) {
			ts = new ArrayList<TimeSeries>();
			ts.addAll(t);
			Collections.sort(ts);
		}
		
		public TimeSeriesList(TimeSeries[] t){
			ts = new ArrayList<TimeSeries>(Arrays.asList(t));
			Collections.sort(ts);
		}
		
		public List<TimeSeries> getList() {
			return ts;
		}
		
		public TimeSeries[] getArray() {
			TimeSeries[] ret = new TimeSeries[ts.size()];
			
			ts.toArray(ret);
			
			return ret;
		}
}
