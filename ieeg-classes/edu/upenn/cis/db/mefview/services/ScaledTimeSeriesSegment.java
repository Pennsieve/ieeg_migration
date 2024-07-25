/*******************************************************************************
 * Copyright 2010 Trustees of the University of Pennsylvania
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package edu.upenn.cis.db.mefview.services;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import edu.upenn.cis.db.mefview.eeg.ITimeSeries;

@XmlRootElement(name="TimeSeries")
public class ScaledTimeSeriesSegment extends TimeSeriesSegment {
	private ArrayList<Double> series;

	@XmlElement(name="scale")
	public double getScale() {
		return 1.0;
	}

	public String toString() {
		return getStartTime() + "@" + getPeriod() + ": " + series.toString();
	}
	
	public List<Double> getSeriesValues() {
		return series;
	}

	public void setSeriesValues(ArrayList<Double> series) {
		this.series = series;
	}
	
	public ScaledTimeSeriesSegment() {
		super();
		series = new ArrayList<Double>();
	}

	public ScaledTimeSeriesSegment(long startTime, double period, double scale,
			int[] gapsStart, int[] gapsEnd) {
		super(startTime, period, gapsStart, gapsEnd);
		series = new ArrayList<Double>();
	}

	/**
	 * Unscaled constructor
	 * @param time2
	 */
	public ScaledTimeSeriesSegment(UnscaledTimeSeriesSegment time2) {
		setStartTime(time2.getStartTime());
		setPeriod(time2.getPeriod());
		series = new ArrayList<Double>();
		series.ensureCapacity(time2.getSeriesLength());
		for (Number val : time2.getSeries()) {
			Double value = val.doubleValue() * time2.getScale();
			
			series.add(value);
		}
	}

	public ScaledTimeSeriesSegment(ITimeSeries time2) {
		setStartTime(time2.getStartTime());
		setPeriod(time2.getPeriod());
		series = new ArrayList<Double>();
		series.ensureCapacity(time2.getSeriesLength());
		for (Number val : time2.getSeries()) {
			Double value = val.doubleValue();
			
			series.add(value);
		}
	}
	
	public Double[] toArray() {
		Double[] arr = new Double[series.size()];
		series.toArray(arr);
		return arr;
	}

	@Override
	public int getAt(int i) {
		return series.get(i).intValue();
	}

	@Override
	public int[] getSeries() {
		int[] ret = new int[series.size()];
		for (int i = 0; i < series.size(); i++)
			ret[i] = series.get(i).intValue();
		return ret;
	}

	@Override
	public int getSeriesLength() {
		return series.size();
	}
}
