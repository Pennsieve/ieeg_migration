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

import static com.google.common.collect.Lists.newArrayList;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.primitives.Ints;

@XmlRootElement(name = "TimeSeries")
public class UnscaledTimeSeriesSegment extends TimeSeriesSegment {
	private double scale;
	private int[] series;

	@XmlElement(name = "scale")
	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public String toString() {
		return getStartTime() + "@" + getPeriod() + " x" + scale + ": "
				+ Arrays.toString(series);
	}

	@XmlElementWrapper(name = "series")
	@XmlElement(name = "value")
	/**
	 * Returns an array representing the samples in the series.  Gaps are
	 * represented as 0s and one must separately test isInGap or make use
	 * of the getGapsStart() and getGapsEnd() functions to determine gap
	 * ranges
	 */
	public int[] getSeries() {
		return series;
	}

	/**
	 * Returns the number of samples in the series
	 * 
	 * @return
	 */
	public int getSeriesLength() {
		return series.length;
	}

	public void setSeries(int[] series) {
		this.series = series;
	}

	public UnscaledTimeSeriesSegment() {
		super();
		series = new int[0];
	}

	public UnscaledTimeSeriesSegment(long startTime, double period,
			double scale, int[] values, int[] gapStart, int[] gapEnd) {
		super(startTime, period, gapStart, gapEnd);
		this.scale = scale;

		series = values;
	}

	/**
	 * This constructor interprets {@code Integer.MIN_VALUE} entries in
	 * {@code values} as gap entries. It sets {@code startTime} and
	 * {@code period} to -1.
	 * 
	 * @param scale
	 * @param values
	 */
	public UnscaledTimeSeriesSegment(double scale, int[] values) { 
		super(-1, -1, null, null);
		this.scale = scale;
		series = values;
		List<Integer> gapsStartList = newArrayList();
		List<Integer> gapsEndList = newArrayList();
		boolean inGap = false;
		for (int idx = 0; idx < series.length; idx++) {
			final int value = series[idx];
			if (value == Integer.MIN_VALUE) {
				if (!inGap) {
					inGap = true;
					gapsStartList.add(Integer.valueOf(idx));
				}
			} else {
				if (inGap) {
					inGap = false;
					gapsEndList.add(Integer.valueOf(idx));
				}
			}
		}
		//If the series ends in a gap we'd like to close it.
		if (inGap) {
			gapsEndList.add(Integer.valueOf(series.length));
		}
		gapsStart = Ints.toArray(gapsStartList);
		gapsEnd = Ints.toArray(gapsEndList);
	}

	/**
	 * Returns a vector representing the data values in the series, where
	 * *nulls* represent gaps.
	 * 
	 * @return
	 */
	public Integer[] toArray() {
		Integer[] arr = new Integer[series.length];

		for (int i = 0; i < series.length; i++) {
			if (!isInGap(i))
				arr[i] = series[i];
			else
				arr[i] = null;
		}

		return arr;
	}


	@Override
	public int getAt(int i) {
		return series[i];
	}
}
