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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.GwtCompatible;

import edu.upenn.cis.db.mefview.eeg.ITimeSeries;

@GwtCompatible(serializable=true)
public class TimeSeriesData implements ITimeSeries, Serializable {
	private double scale;
	private double period;
	private long startTime;
	private int[] series;
	private List<Integer> gapStart;
	private List<Integer> gapEnd;
	private String revId;
	private String channelName;
	private int pos = 0;
	private boolean minMax = false;
	
	private final static Logger logger = LoggerFactory
	.getLogger(TimeSeriesData.class);

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public List<Integer> getGapStart() {
		return gapStart;
	}

	public void setGapStart(List<Integer> gapStart) {
		this.gapStart = gapStart;
	}

	public List<Integer> getGapEnd() {
		return gapEnd;
	}

	public void setGapEnd(ArrayList<Integer> gapEnd) {
		this.gapEnd = gapEnd;
	}
	
	public boolean isMinMax() {
		return minMax;
	}
	
	public void setMinMax(boolean isMinMax) {
		this.minMax = isMinMax;
	}

	public String getRevId() {
		return revId;
	}

	public void setRevId(String revId) {
		this.revId = revId;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public double getPeriod() {
		return period;
	}

	public void setPeriod(double period) {
		this.period = period;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public String toString() {
		return getStartTime() + "-" + getSeriesLength() + "@" + getPeriod() + " x" + scale + ": " + getSeriesLength() + " entries, with gaps " + gapStart.toString() + "/" + gapEnd.toString();
	}
	
	public String getStringValues() {
		StringBuilder buf = new StringBuilder("[");
		
		boolean first = true;
		for (int i = 0; i < getSeriesLength(); i++) {
				if (first)
					first = false;
				else
					buf.append(',');
				buf.append(series[i]);
			}
		
		buf.append("]");
		return buf.toString();
	}
	
	public int[] getSeries() {
		return series;
	}
	
	public boolean isInGap(int i) {
		int j = 0;
		
		int gaps = gapStart.size();
		
		while (j < gaps && i >= gapEnd.get(j))
			j++;
		
		return (j < gaps && i >= gapStart.get(j) && i < gapEnd.get(j));
	}

	public Integer getValue(int i) {
		if (isInGap(i))
			return null;
		else
			return series[i];
	}
	
	public int getAt(int i) {
		return series[i];
	}
	
	public int getSeriesLength() {
		return pos;
	}

	public void setSeries(int[] series) {
		this.series = series;
	}
	
	public void setSeries(List<Integer> series) {
		this.series = new int[series.size()];
		
		int count = 0;
		for (Integer i : series)
			this.series[count++] = i;
	}
	
	public void addSample(@Nullable Number sample) {
		if (sample != null)
			series[pos++] = sample.intValue();
		else {
			int gapCount = gapStart.size();
			if (gapCount > 0 && gapEnd.get(gapCount - 1) == pos) {
				gapEnd.set(gapCount - 1, pos + 1); 
			} else {
				gapStart.add(pos);
				gapEnd.add(pos + 1);
			}
			series[pos++] = 0;
		}
	}
	
	public void addSample(int sample) {
		series[pos++]= sample;
	}
	
	public TimeSeriesData() {
		super();
		series = new int[0];
	}
	
	static final int pad = 16; 

	public TimeSeriesData(long startTime, double period, double scale, int size) {
		this.scale = scale;
		this.startTime = startTime;
		this.period = period;
		series = new int[size + pad];
		gapStart = new ArrayList<Integer>();
		gapEnd = new ArrayList<Integer>();
	}
	
	public TimeSeriesData(long startTime, double period, double scale, int[] s) {
		this.scale = scale;
		this.startTime = startTime;
		this.period = period;
		series = s;
		pos = s.length;
		gapStart = new ArrayList<Integer>();
		gapEnd = new ArrayList<Integer>();
	}

	public Integer[] toArray() {
		Integer[] arr = new Integer[getSeriesLength()];
		for (int i = 0; i < getSeriesLength(); i++) {
			arr[i] = getValue(i);
		}
		return arr;
	}
	
	public UnscaledTimeSeriesSegment getSegment() {
		int values[] = series;
		
		int[] gStart = new int[gapStart.size()];
		int[] gEnd = new int[gapEnd.size()];
		
		for (int i = 0; i < gapStart.size(); i++)
			gStart[i] = gapStart.get(i);
		for (int i = 0; i < gapEnd.size(); i++)
			gEnd[i] = gapEnd.get(i);
		
		logger.trace("Gaps at " + gapStart.toString() + " / " + gapEnd.toString() + " from " + getSeriesLength());

		UnscaledTimeSeriesSegment s2;
		if (getSeriesLength() != values.length) {
			values = new int[getSeriesLength()];
			for (int i = 0; i < getSeriesLength(); i++)
				values[i] = series[i];
		}
		s2 = new UnscaledTimeSeriesSegment(startTime, period, scale, values, gStart, gEnd);
		
		return s2;
	}
	
}
