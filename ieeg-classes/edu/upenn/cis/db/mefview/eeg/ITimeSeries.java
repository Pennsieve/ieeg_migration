/*
 * Copyright 2015 Trustees of the University of Pennsylvania
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
package edu.upenn.cis.db.mefview.eeg;

import java.util.List;

public interface ITimeSeries {
	public List<Integer> getGapStart();

	public List<Integer> getGapEnd();

	/**
	 * Scale factor
	 * @return
	 */
	public double getScale();

	public double getPeriod();
	public long getStartTime();

	/**
	 * Returns the data as an int vector
	 * @return
	 */
	public int[] getSeries();
	
	/**
	 * Returns true if index position i is in a gap
	 * @param i
	 * @return
	 */
	public boolean isInGap(int i);

	/**
	 * Returns sample value, null if no sample
	 * 
	 * @param i
	 * @return
	 */
	public Integer getValue(int i);
	
	public int getAt(int i);
	
	public int getSeriesLength();
}
