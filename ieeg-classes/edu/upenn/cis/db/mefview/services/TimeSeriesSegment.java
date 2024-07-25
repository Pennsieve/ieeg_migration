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

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;

import edu.upenn.cis.db.mefview.eeg.ITimeSeries;

public abstract class TimeSeriesSegment implements ITimeSeries {
	@XmlElement(name="startTime")
	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	@XmlElement(name="period")
	public double getPeriod() {
		return period;
	}

	public void setPeriod(double period) {
		this.period = period;
	}

	public abstract double getScale();

	private long startTime;
	private double period;
	protected int[] gapsStart;
	protected int[] gapsEnd;
	
	public String toString() {
		return startTime + "@" + period;
	}
	
	public TimeSeriesSegment() {
		gapsStart = new int[0];
		gapsEnd = new int[0];
	}
	
	public TimeSeriesSegment(
			long startTime, 
			double period, 
			@Nullable int[] gapStart, 
			@Nullable int[] gapEnd) {
		this.startTime = startTime;
		this.period = period;

		gapsStart = gapStart;
		gapsEnd = gapEnd;
	}
	
	@XmlElement(name = "gapStart")
	public int[] getGapsStart() {
		return gapsStart;
	}

	public void setGapsStart(int[] gapsStart) {
		this.gapsStart = gapsStart;
	}

	@XmlElement(name = "gapEnd")
	public int[] getGapsEnd() {
		return gapsEnd;
	}

	public void setGapsEnd(int[] gapsEnd) {
		this.gapsEnd = gapsEnd;
	}

	/**
	 * Returns true if the particular index is within a gap
	 * 
	 * @param i
	 * @return
	 */
	public boolean isInGap(int i) {

		// the jth gap is
		// [gapsStart[j], gapsEnd[j])
		for (int j = 0; j < gapsStart.length; j++) {
			if (gapsStart[j] <= i && i < gapsEnd[j]) {
				return true;
			}
		}
		return false;

	}

	@Override
	public List<Integer> getGapStart() {
		List<Integer> ret = new ArrayList<Integer>(gapsStart.length);
		for (int i = 0; i < gapsStart.length; i++)
			ret.add(gapsStart[i]);
		return ret;
	}

	@Override
	public List<Integer> getGapEnd() {
		List<Integer> ret = new ArrayList<Integer>(gapsEnd.length);
		for (int i = 0; i < gapsEnd.length; i++)
			ret.add(gapsEnd[i]);
		return ret;
	}

	@Override
	public Integer getValue(int i) {
		if (!isInGap(i))
			return getAt(i);
		else
			return null;
	}

};

