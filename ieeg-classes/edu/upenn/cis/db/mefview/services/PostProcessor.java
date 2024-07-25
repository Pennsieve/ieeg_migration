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

import java.util.ArrayList;

import javax.annotation.Nullable;


public interface PostProcessor {
	public boolean isFiltered();
	public double getSampleRate();
	
	public boolean needsWorkBuffer(int sampleCount);
	
	public void createWorkBuffer(int sampleCount);
	
	public void setWorkBuffer(int[] buffer);
	
	public int getWorkBufferSize();
	
	/**
	 * 
	 * @param startTime requested start time in uUTC
	 * @param endTime requested end time in uUTC
	 * @param trueFreq frequency of data in Hz
	 * @param startTimeOffset microseconds since start of recording
	 * @param voltageScale scale factor
	 * @param pageList list of data pages 
	 * @param path 
	 * @param minValIsNull 
	 * @param padBefore
	 * @param padAfter
	 * @return
	 */
	public ArrayList<TimeSeriesData> process(double startTime, double endTime, double trueFreq, 
			long startTimeOffset, double voltageScale, TimeSeriesPage[] pageList,
			@Nullable ChannelSpecifier path, boolean minValIsNull, 
			double padBefore, double padAfter);
}