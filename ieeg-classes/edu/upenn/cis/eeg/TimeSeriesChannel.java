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
package edu.upenn.cis.eeg;

public interface TimeSeriesChannel extends ISimpleChannel{
	public String getInstitution();

	public short getHeaderLength();

	public String getSubjectFirstName();

	public String getSubjectSecondName();

	public String getSubjectThirdName();

	public String getSubjectId();

	public long getNumberOfSamples();

//	public String getChannelName();

	public long getRecordingStartTime();

	public long getRecordingEndTime();

//	public double getSamplingFrequency();

	public double getLowFrequencyFilterSetting();

	public double getHighFrequencyFilterSetting();

	public double getNotchFilterFrequency();

//	public double getVoltageConversionFactor();

	public String getAcquisitionSystem();

	public String getChannelComments();

	public String getStudyComments() ;

	public int getPhysicalChannelNumber();

	public long getMaximumBlockLength();

	public long getBlockInterval();

//	public long getIndexDataOffset();

//	public long getNumberOfIndexEntries();

	public short getBlockHeaderLength();
	
	public int getMaximumDataValue();
	
	public int getMinimumDataValue();

//	public long getBytesRead();

}
