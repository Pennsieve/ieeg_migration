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

import static com.google.common.collect.Lists.newArrayList;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "traceinfo")
public class TimeSeriesDetails implements Serializable,
		Comparable<TimeSeriesDetails> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TimeSeriesDetails() {}

	/**
	 * Constructor for time series details
	 * 
	 * @param startTime Start date/time (uUTC)
	 * @param duration Duration (usec)
	 * @param sampleRate Sampling rate (Hz)
	 * @param voltageConversion Voltage conversion factor
	 * @param minSampleValue Minimum integer sample value
	 * @param maxSampleValue Maximum integer sample value
	 * @param acquisitionSystem
	 * @param channelName
	 * @param label
	 * @param revId
	 */
	public TimeSeriesDetails(
			long startTime,
			long endTime,
			double duration,
			double sampleRate,
			double voltageConversion,
			int minSampleValue,
			int maxSampleValue,
			String acquisitionSystem,
			String channelName,
			String label,
			String revId,
			String dataCheck,
			long numberOfSamples) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.duration = duration;
		this.sampleRate = sampleRate;
		this.voltageConversion = voltageConversion;
		this.label = label;
		this.revId = revId;
		this.minSampleValue = minSampleValue;
		this.maxSampleValue = maxSampleValue;
		this.acquisitionSystem = acquisitionSystem;
		this.channelName = channelName;
		this.dataCheck = dataCheck;
		this.numberOfSamples = numberOfSamples;
	}

	public TimeSeriesDetails(String str) {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(TimeSeriesDetails.class);

			Unmarshaller unmarshaller = context.createUnmarshaller();

			StringReader sr = new StringReader(str);

			unmarshaller.unmarshal(sr);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(TimeSeriesDetails.class);
			Marshaller marshaller = context.createMarshaller();
			StringWriter sw = new StringWriter();

			marshaller.marshal(this, sw); // save to StringWriter, you can then
											// call sw.toString() to get
											// java.lang.String
			return sw.toString();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private double duration;
	private double sampleRate;
	private long startTime;
	private long endTime;
	private double voltageConversion;
	private String label;
	private String revId;
	private int minSampleValue;
	private int maxSampleValue;
	private String acquisitionSystem;
	private String channelName;
	private String dataCheck;
	private long numberOfSamples;

	@XmlElement
	public long getNumberOfSamples() {
		return numberOfSamples;
	}

	public void setNumberOfSamples(long numberOfSamples) {
		this.numberOfSamples = numberOfSamples;
	}

	public int compareTo(TimeSeriesDetails o) {
		return label.compareTo(o.label);
	}

	/**
	 * Return the duration of the recording in usec
	 * 
	 * @return
	 */
	@XmlElement(name = "duration")
	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	/**
	 * Return the recording sample rate in Hz
	 * 
	 * @return
	 */
	@XmlElement(name = "sampleRate")
	public double getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(double sampleRate) {
		this.sampleRate = sampleRate;
	}

	/**
	 * Return the recording start time in uUTC
	 * 
	 * @return
	 */
	@XmlElement(name = "startTime")
	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	/**
	 * Return the recording voltage conversion factor
	 * 
	 * @return
	 */
	@XmlElement(name = "voltageConversionFactor")
	public double getVoltageConversion() {
		return voltageConversion;
	}

	public void setVoltageConversion(double voltageConversion) {
		this.voltageConversion = voltageConversion;
	}

	/**
	 * Internal use only
	 * 
	 * @return
	 */
	@XmlElement(name = "channelLabel")
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@XmlElement(name = "revisionId")
	public String getRevId() {
		return revId;
	}

	public void setRevId(String revId) {
		this.revId = revId;
	}

	/**
	 * Return the minimum value sampled in the recording
	 * 
	 * @return
	 */
	@XmlElement(name = "minSample")
	public int getMinSampleValue() {
		return minSampleValue;
	}

	public void setMinSampleValue(int minSampleValue) {
		this.minSampleValue = minSampleValue;
	}

	/**
	 * Return the maximum value sampled in the recording
	 * 
	 * @return
	 */
	@XmlElement(name = "maxSample")
	public int getMaxSampleValue() {
		return maxSampleValue;
	}

	public void setMaxSampleValue(int maxSampleValue) {
		this.maxSampleValue = maxSampleValue;
	}

	/**
	 * Sensor acquisition system
	 * 
	 * @return
	 */
	@XmlElement(name = "acquisition")
	public String getAcquisitionSystem() {
		return acquisitionSystem;
	}

	public void setAcquisitionSystem(String acquisitionSystem) {
		this.acquisitionSystem = acquisitionSystem;
	}

	/**
	 * Internal use only
	 * 
	 * @return
	 */
	@XmlElement(name = "name")
	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	/**
	 * Returns a time series specifier
	 * 
	 * @return
	 */
	public TimeSeries getTimeSeries() {
		final TimeSeries series = new TimeSeries(getRevId(), getLabel());
		series.setRevId(getRevId());
		return series;
	}

	public String getDataCheck() {
		return dataCheck;
	}

	public void setDataCheck(String dataCheck) {
		this.dataCheck = dataCheck;
	}

	public static List<TimeSeriesIdAndDCheck> toTimeSeriesIdAndDCheck(
			Iterable<? extends TimeSeriesDetails> tsDetails) {
		List<TimeSeriesIdAndDCheck> timeSeriesIdAndDCheck = newArrayList();
		for (TimeSeriesDetails tsDetail : tsDetails) {
			timeSeriesIdAndDCheck.add(
					new TimeSeriesIdAndDCheck(
							tsDetail.getRevId(),
							tsDetail.getDataCheck()));
		}
		return timeSeriesIdAndDCheck;
	}

}
