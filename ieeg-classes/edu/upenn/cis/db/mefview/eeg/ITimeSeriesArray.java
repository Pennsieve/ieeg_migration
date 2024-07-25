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

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;

import edu.upenn.cis.db.mefview.services.ChannelSpecifier;

@GwtCompatible
public interface ITimeSeriesArray {
	public int getNumChannels();
	
	/**
	 * List of channels available
	 * 
	 * @return
	 */
	@GwtIncompatible("ChannelSpecifier")
	public List<ChannelSpecifier> getChannelSpecifiers();
	
	/**
	 * The series may come in multiple segments
	 * 
	 * @param index
	 * @return
	 */
	public IMultiSegmentTimeSeries getSeries(int index);
	
	/**
	 * The series may come in multiple segments
	 * 
	 * @param index
	 * @return
	 */
	@GwtIncompatible("ChannelSpecifier")
	public IMultiSegmentTimeSeries getSeries(ChannelSpecifier channel);
}
