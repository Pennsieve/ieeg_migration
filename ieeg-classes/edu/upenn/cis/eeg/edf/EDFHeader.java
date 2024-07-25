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
package edu.upenn.cis.eeg.edf;

import static com.google.common.base.Preconditions.checkArgument;
import static edu.upenn.cis.eeg.edf.EDFConstants.*;
import static edu.upenn.cis.eeg.edf.ParseUtils.readASCIIFromStream;
import static edu.upenn.cis.eeg.edf.ParseUtils.readBulkASCIIFromStream;
import static edu.upenn.cis.eeg.edf.ParseUtils.readBulkDoubleFromStream;
import static edu.upenn.cis.eeg.edf.ParseUtils.readBulkIntFromStream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.google.common.base.Preconditions;

/**
 * This class represents the complete header of an EDF-File.
 */
public final class EDFHeader
{
	public final String idCode;
	public final String subjectID;
	public final String recordingID;
	public final String startDate;
	public final String startTime;
	public final int bytesInHeader;
	public final String formatVersion;
	public final int numberOfRecords;
	public final double durationOfRecords;
	public final int numberOfChannels;
	public final String[] channelLabels;
	public final String[] transducerTypes;
	public final String[] dimensions;
	public final Double[] minInUnits;
	public final Double[] maxInUnits;
	public final Integer[] digitalMin;
	public final Integer[] digitalMax;
	public final String[] prefilterings;
	public final Integer[] numberOfSamples;
	public final byte[][] reserveds;
	public final boolean hasAnnotationChannel;

	/**
	 * To populate the EDFHeader
	 * 
	 */
	public static class Builder {

		// Optional
		private String idCode = "0       ";
		private String subjectID = null;
		private String recordingID = null;
		private String startDate;
		private String startTime;
		private int bytesInHeader = 0;
		private String formatVersion = "EDF+C";
		private int numberOfRecords = 0;
		private double durationOfRecords = 0;
		private int numberOfChannels = 0;
		private List<String> channelLabels;
		private List<String> transducerTypes;
		private List<String> dimensions;
		private List<Double> minInUnits;
		private List<Double> maxInUnits;
		private List<Integer> digitalMin;
		private List<Integer> digitalMax;
		private List<String> prefilterings;
		private List<Integer> numberOfSamples;
		private boolean hasAnnotationChannel = false;

		private String patientCode = "X";
		private String patientGender = "X";
		private String patientBirthdate = "X";
		private String patientName = "X";
		private String recordingHospital = "X";
		private String recordingTechnician = "X";
		private String recordingEquipment = "X";
		private String recordingStartDate = "X";

		public Builder(Date startOfRecording) {
			Preconditions.checkNotNull(startOfRecording);

			init();
			setStartTime(startOfRecording);
		}

		public Builder(EDFHeader header)
		{
			init();

			idCode = header.idCode;
			subjectID = header.subjectID;
			recordingID = header.recordingID;
			startDate = header.startDate;
			startTime = header.startTime;
			formatVersion = header.formatVersion;
			numberOfRecords = header.numberOfRecords;
			numberOfChannels = header.numberOfChannels;
			durationOfRecords = header.durationOfRecords;
			channelLabels = new ArrayList<String>(
					Arrays.asList(header.channelLabels));
			transducerTypes = new ArrayList<String>(
					Arrays.asList(header.transducerTypes));
			dimensions = new ArrayList<String>(Arrays.asList(header.dimensions));
			minInUnits = new ArrayList<Double>(Arrays.asList(header.minInUnits));
			maxInUnits = new ArrayList<Double>(Arrays.asList(header.maxInUnits));
			digitalMin = new ArrayList<Integer>(
					Arrays.asList(header.digitalMin));
			digitalMax = new ArrayList<Integer>(
					Arrays.asList(header.digitalMax));
			prefilterings = new ArrayList<String>(
					Arrays.asList(header.prefilterings));
			numberOfSamples = new ArrayList<Integer>(
					Arrays.asList(header.numberOfSamples));
		}

		public void editChannelName(int position, String newName) {
			this.channelLabels.set(position, newName);

		}

		public void setSubjectID(String subjectID) {
			this.subjectID = subjectID;
		}

		public void setRecordingID(String recordingID) {
			this.recordingID = recordingID;
		}

		public void setNumberOfRecords(int val) { // **changed protected to
													// public by Veena Krish on
													// July 1***
			numberOfRecords = val;
		}

		// -- --

		public void setFormatVersion(String formatVersion) {
			this.formatVersion = formatVersion;
		}

		public void setDurationOfRecords(double durationOfRecords) {
			this.durationOfRecords = durationOfRecords;
		}

		public void setIdCode(String idCode) {
			this.idCode = idCode;
		}

		public void addChannel(String label, String transducer,
				String dimension,
				Double minValue, Double maxValue, Integer digitalMinValue,
				Integer digitalMaxValue, String prefiltering,
				Integer samplesPerBlock) {
			channelLabels.add(label);
			transducerTypes.add(transducer);
			dimensions.add(dimension);
			minInUnits.add(minValue);
			maxInUnits.add(maxValue);
			digitalMin.add(digitalMinValue);
			digitalMax.add(digitalMaxValue);
			prefilterings.add(prefiltering);
			numberOfSamples.add(samplesPerBlock);

			numberOfChannels++;

			if (label.equals("EDF Annotations")) {
				hasAnnotationChannel = true;
			}

		}

		public void addAnnotationChannel(int l) {
			String test = "EDF Annotations";
			channelLabels.add(test);
			transducerTypes.add(" ");
			dimensions.add(" ");
			minInUnits.add((double) 0);
			maxInUnits.add((double) 1);
			digitalMin.add(-32768);
			digitalMax.add(32767);
			prefilterings.add(" ");
			numberOfSamples.add(l);

			numberOfChannels++;
		}

		public void setPatientInfo(String pCode, boolean isMale,
				Date pBirthDate, String pName) {
			patientCode = nonSpaceString(pCode);
			patientGender = isMale ? "M" : "F";
			if (pBirthDate == null)
				patientBirthdate = "X";
			else
				patientBirthdate = new SimpleDateFormat("dd-MMM-yyyy").format(
						pBirthDate).toUpperCase();
			patientName = nonSpaceString(pName);
		}

		public void setRecordingInfo(String recHospital, String recTechnician,
				String recEquipment) {
			recordingHospital = nonSpaceString(recHospital);
			recordingTechnician = nonSpaceString(recTechnician);
			recordingEquipment = nonSpaceString(recEquipment);
		}

		public void setContinuous(boolean isContinuous) {
			formatVersion = isContinuous ? "EDF+C" : "EDF+D";
		}

		/*
		 * Added by Veena Krish on 7/1/14....
		 */
		public int getNumberOfRecords() {
			return this.numberOfRecords;
		}

		public void setStartTime(Date startOfRecording) {
			final SimpleDateFormat recordingStartDateFormat = new SimpleDateFormat(
					"dd-MMM-yyyy");
			recordingStartDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			recordingStartDate = recordingStartDateFormat.format(
					startOfRecording).toUpperCase();

			final SimpleDateFormat startDateFormat = new SimpleDateFormat(
					"dd.MM.yy");
			startDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			startDate = startDateFormat.format(startOfRecording);

			final SimpleDateFormat startTimeFormat = new SimpleDateFormat(
					"HH.mm.ss");
			startTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			startTime = startTimeFormat.format(startOfRecording);
		}

		public EDFHeader build() {

			if (subjectID == null) {
				subjectID = buildPatientString();
			}

			if (recordingID == null) {
				recordingID = buildRecordingString();
			}

			bytesInHeader = 256 + (channelLabels.size() * 256);

			return new EDFHeader(this);

		}

		private void init() {
			channelLabels = new ArrayList<String>();
			transducerTypes = new ArrayList<String>();
			dimensions = new ArrayList<String>();
			minInUnits = new ArrayList<Double>();
			maxInUnits = new ArrayList<Double>();
			digitalMin = new ArrayList<Integer>();
			digitalMax = new ArrayList<Integer>();
			prefilterings = new ArrayList<String>();
			numberOfSamples = new ArrayList<Integer>();
		}

		private String buildPatientString()
		{
			return new StringBuilder().append(patientCode).append(" ")
					.append(patientGender).append(" ")
					.append(patientBirthdate).append(" ").append(patientName)
					.toString();
		}

		private String buildRecordingString()
		{
			return new StringBuilder().append("Startdate").append(" ")
					.append(recordingStartDate).append(" ")
					.append(recordingHospital).append(" ")
					.append(recordingTechnician).append(" ")
					.append(recordingEquipment).toString();
		}

		private String nonSpaceString(String val)
		{
			return val.replaceAll(" ", "_");
		}

	}

	/**
	 * Constructs an EDFHeader object from a EDFHeader.Builder object.
	 * 
	 * @param builder
	 */
	private EDFHeader(Builder builder) {
		idCode = builder.idCode;
		subjectID = builder.subjectID;
		recordingID = builder.recordingID;
		startDate = builder.startDate;
		startTime = builder.startTime;
		bytesInHeader = builder.bytesInHeader;
		formatVersion = builder.formatVersion;
		numberOfRecords = builder.numberOfRecords;
		numberOfChannels = builder.numberOfChannels;
		durationOfRecords = builder.durationOfRecords;
		hasAnnotationChannel = builder.hasAnnotationChannel;

		channelLabels = (String[]) builder.channelLabels.toArray(new String[0]);
		transducerTypes = (String[]) builder.transducerTypes
				.toArray(new String[0]);
		dimensions = (String[]) builder.dimensions.toArray(new String[0]);
		minInUnits = (Double[]) builder.minInUnits.toArray(new Double[0]);
		maxInUnits = (Double[]) builder.maxInUnits.toArray(new Double[0]);
		digitalMin = (Integer[]) builder.digitalMin.toArray(new Integer[0]);
		digitalMax = (Integer[]) builder.digitalMax.toArray(new Integer[0]);
		prefilterings = (String[]) builder.prefilterings.toArray(new String[0]);
		numberOfSamples = (Integer[]) builder.numberOfSamples
				.toArray(new Integer[0]);
		reserveds = new byte[numberOfChannels][RESERVED_SIZE];
		for (byte[] chArr : reserveds)
			Arrays.fill(chArr, (byte) 0x20);

	}

	/**
	 * Returns the indexes of the annotation channels if the object contains
	 * them. Otherwise it returns null.
	 * 
	 * @return
	 */
	public Integer[] findAnnotationChannels() {
		List<Integer> annotationIndexList = new ArrayList<Integer>();
		for (int i = 0; i < numberOfChannels; i++)
		{
			if ("EDF Annotations".equals(channelLabels[i].trim()))
			{
				annotationIndexList.add(i);
			}
		}
		if (annotationIndexList.isEmpty())
			return null;

		return (Integer[]) annotationIndexList.toArray(new Integer[0]);
	}

	public ByteBuffer serialize() {
		// allocate and init buffer

		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("#.######", dfs);

		ByteBuffer bb = ByteBuffer.allocate(bytesInHeader);
		putIntoBuffer(bb, IDENTIFICATION_CODE_SIZE, idCode);
		putIntoBuffer(bb, LOCAL_SUBJECT_IDENTIFICATION_SIZE, subjectID);
		putIntoBuffer(bb, LOCAL_REOCRDING_IDENTIFICATION_SIZE, recordingID);
		putIntoBuffer(bb, START_DATE_SIZE, startDate);
		putIntoBuffer(bb, START_TIME_SIZE, startTime);
		putIntoBuffer(bb, HEADER_SIZE, bytesInHeader);
		putIntoBuffer(bb, DATA_FORMAT_VERSION_SIZE, formatVersion);
		putIntoBuffer(bb, NUMBER_OF_DATA_RECORDS_SIZE, numberOfRecords);
		putIntoBuffer(bb, DURATION_DATA_RECORDS_SIZE, durationOfRecords, df);
		putIntoBuffer(bb, NUMBER_OF_CHANELS_SIZE, numberOfChannels);

		putIntoBuffer(bb, LABEL_OF_CHANNEL_SIZE, channelLabels);
		putIntoBuffer(bb, TRANSDUCER_TYPE_SIZE, transducerTypes);
		putIntoBuffer(bb, PHYSICAL_DIMENSION_OF_CHANNEL_SIZE, dimensions);
		putIntoBuffer(bb, PHYSICAL_MIN_IN_UNITS_SIZE, minInUnits, df);
		putIntoBuffer(bb, PHYSICAL_MAX_IN_UNITS_SIZE, maxInUnits, df);
		putIntoBuffer(bb, DIGITAL_MIN_SIZE, digitalMin);
		putIntoBuffer(bb, DIGITAL_MAX_SIZE, digitalMax);
		putIntoBuffer(bb, PREFILTERING_SIZE, prefilterings);
		putIntoBuffer(bb, NUMBER_OF_SAMPLES_SIZE, numberOfSamples);
		putIntoBuffer(bb, reserveds);

		return bb;

	}

	private static void putIntoBuffer(ByteBuffer bb, int lengthPerValue,
			Double[] values, DecimalFormat df)
	{
		for (Double value : values)
		{
			putIntoBuffer(bb, lengthPerValue, value, df);
		}
	}

	private static void putIntoBuffer(ByteBuffer bb, int length, Double value, DecimalFormat df)
  {
    if (Math.floor(value) == value) {
      putIntoBuffer(bb, length, value.intValue());
    } else {
    	String valueStr = df.format(value);
    	if (valueStr.length() > length) {
    		int pointIdx = valueStr.indexOf('.');
    		if (pointIdx < 0) {
    			throw new IllegalArgumentException("Value [" + valueStr + "] will not fit into field of length " + length);
    		}
    		int noCharsAllowedAfterDecimalPoint = length - (pointIdx + 1); //length chars - (chars before point + one for point)
    		String newPattern = "#";
    		for (int i = 0; i < noCharsAllowedAfterDecimalPoint; i++) {
    			if (i == 0) {
    				newPattern += ".#";
    			} else {
    				newPattern += "#";
    			}
    		}
    		final DecimalFormat newDf = new DecimalFormat(newPattern, df.getDecimalFormatSymbols());
    		valueStr = newDf.format(value);
    	}
      putIntoBuffer(bb, length, valueStr);
    }
  }

	private static void putIntoBuffer(ByteBuffer bb, int lengthPerValue,
			Integer[] values)
	{
		for (Integer value : values)
		{
			putIntoBuffer(bb, lengthPerValue, value);
		}
	}

	private static void putIntoBuffer(ByteBuffer bb, int length, int value)
	{
		putIntoBuffer(bb, length, String.valueOf(value));
	}

	private static void putIntoBuffer(ByteBuffer bb, int lengthPerValue,
			String[] values)
	{
		for (String value : values)
		{
			putIntoBuffer(bb, lengthPerValue, value);
		}
	}

	private static void putIntoBuffer(ByteBuffer bb, int length, String value)
	{

		ByteBuffer valueBuffer = ByteBuffer.allocate(length);
		// System.out.println("lenght allocated: " + length + "for value: " +
		// value);
		valueBuffer.put(value.getBytes(EDFConstants.CHARSET));
		while (valueBuffer.position() < length)
			valueBuffer.put((byte) 0x20);

		valueBuffer.rewind();
		bb.put(valueBuffer);
		// System.out.println("valueBuffer: " + valueBuffer.capacity());
	}

	private static void putIntoBuffer(ByteBuffer bb, byte[][] values)
	{
		for (byte[] val : values)
		{
			bb.put(val);
		}
	}

	public Integer[] findSignalChannels() {
		List<Integer> signalIndexList = new ArrayList<Integer>();
		for (int i = 0; i < numberOfChannels; i++)
		{
			if (!"EDF Annotations".equals(channelLabels[i].trim()))
			{
				signalIndexList.add(i);
			}
		}
		if (signalIndexList.isEmpty())
			return null;

		return (Integer[]) signalIndexList.toArray(new Integer[0]);
	}

	public String getIdCode()
	{
		return idCode;
	}

	public String getSubjectID()
	{
		return subjectID;
	}

	public String getRecordingID()
	{
		return recordingID;
	}

	public String getStartDate()
	{
		return startDate;
	}

	public String getStartTime()
	{
		return startTime;
	}

	public int getBytesInHeader()
	{
		return bytesInHeader;
	}

	public String getFormatVersion()
	{
		return formatVersion;
	}

	public int getNumberOfRecords()
	{
		return numberOfRecords;
	}

	public double getDurationOfRecords()
	{
		return durationOfRecords;
	}

	public int getNumberOfChannels()
	{
		return numberOfChannels;
	}

	public String[] getChannelLabels()
	{
		return channelLabels;
	}

	public String[] getTransducerTypes()
	{
		return transducerTypes;
	}

	public String[] getDimensions()
	{
		return dimensions;
	}

	public Double[] getMinInUnits()
	{
		return minInUnits;
	}

	public Double[] getMaxInUnits()
	{
		return maxInUnits;
	}

	public Integer[] getDigitalMin()
	{
		return digitalMin;
	}

	public Integer[] getDigitalMax()
	{
		return digitalMax;
	}

	public String[] getPrefilterings()
	{
		return prefilterings;
	}

	public Integer[] getNumberOfSamples()
	{
		return numberOfSamples;
	}

	public byte[][] getReserveds()
	{
		return reserveds;
	}

	public String toString() {
		return this.subjectID + " : " + this.numberOfChannels + " - "
				+ this.startDate + " : " + this.startTime;

	}

	public static EDFHeader createFromStream(InputStream is) throws IOException
	{

		// InputStream is = stream.getInputStream();

		try
		{

			String idCode = readASCIIFromStream(is, IDENTIFICATION_CODE_SIZE);
			if (!idCode.trim().equals("0")) {
				throw new EDFParserException();
			}

			// Get Global information
			String subjectID = readASCIIFromStream(is,
					LOCAL_SUBJECT_IDENTIFICATION_SIZE);
			String recordingID = readASCIIFromStream(is,
					LOCAL_REOCRDING_IDENTIFICATION_SIZE);
			String startDate = readASCIIFromStream(is, START_DATE_SIZE);
			String startTime = readASCIIFromStream(is, START_TIME_SIZE);

			// For cases where EDF is not formatted to spec.
			startDate = startDate.replaceAll("[\\D]", ".");
			startTime = startTime.replaceAll("[\\D]", ".");

			Date recordingStartDate;
			try {
				recordingStartDate = new SimpleDateFormat("dd.MM.yy HH.mm.ss")
						.parse(startDate + " " + startTime);
			} catch (ParseException e) {
				throw new EDFParserException(e);
			}

			Builder header = new EDFHeader.Builder(recordingStartDate);
			header.setSubjectID(subjectID);
			header.setRecordingID(recordingID);

			int tmp = Integer.parseInt(readASCIIFromStream(is, HEADER_SIZE)
					.trim()); // Automatically re-created during build()
			header.setFormatVersion(readASCIIFromStream(is,
					DATA_FORMAT_VERSION_SIZE));
			header.setNumberOfRecords(Integer.parseInt(readASCIIFromStream(is,
					NUMBER_OF_DATA_RECORDS_SIZE).trim()));
			header.setDurationOfRecords(Double.parseDouble(readASCIIFromStream(
					is, DURATION_DATA_RECORDS_SIZE).trim()));

			int numberOfChannels = Integer.parseInt(readASCIIFromStream(is,
					NUMBER_OF_CHANELS_SIZE).trim());
			ArrayList<String> channelLabels = readBulkASCIIFromStream(is,
					LABEL_OF_CHANNEL_SIZE, numberOfChannels);
			ArrayList<String> transducerTypes = readBulkASCIIFromStream(is,
					TRANSDUCER_TYPE_SIZE, numberOfChannels);
			ArrayList<String> dimensions = readBulkASCIIFromStream(is,
					PHYSICAL_DIMENSION_OF_CHANNEL_SIZE, numberOfChannels);
			ArrayList<Double> minInUnits = readBulkDoubleFromStream(is,
					PHYSICAL_MIN_IN_UNITS_SIZE, numberOfChannels);
			ArrayList<Double> maxInUnits = readBulkDoubleFromStream(is,
					PHYSICAL_MAX_IN_UNITS_SIZE, numberOfChannels);
			ArrayList<Integer> digitalMin = readBulkIntFromStream(is,
					DIGITAL_MIN_SIZE, numberOfChannels);
			ArrayList<Integer> digitalMax = readBulkIntFromStream(is,
					DIGITAL_MAX_SIZE, numberOfChannels);
			ArrayList<String> prefilterings = readBulkASCIIFromStream(is,
					PREFILTERING_SIZE, numberOfChannels);
			ArrayList<Integer> numberOfSamples = readBulkIntFromStream(is,
					NUMBER_OF_SAMPLES_SIZE, numberOfChannels);

			for (int i = 0; i < numberOfChannels; i++) {
				if (channelLabels.get(i) == "EDF Annotations") {
					header.addAnnotationChannel(numberOfSamples.get(i));
				} else {
					header.addChannel(channelLabels.get(i).trim(),
							transducerTypes.get(i).trim(), dimensions.get(i)
									.trim(),
							minInUnits.get(i), maxInUnits.get(i), digitalMin
									.get(i), digitalMax.get(i),
							prefilterings.get(i), numberOfSamples.get(i));
				}
			}

			// Set cursor to beginning of data

			int skipSize = RESERVED_SIZE * numberOfChannels;
			is.skip(skipSize);

			return header.build();
		} catch (IOException e)
		{
			throw e;
			// throw new EDFParserException(e);
		}
	}

	/*
	 * Return Start-date of EDF file in MicroUTC. (usec from jan 1, 1970)
	 */
	public long EDFDate2uUTC() {
		try {
			SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy.kk.mm.ss Z");

			Date startD = df.parse(startDate + "." + startTime + " -0000");
			long retTime = startD.getTime() * 1000;
			return retTime;
		} catch (ParseException e) {
			throw new IllegalStateException("Cannot convert EDF start to uUTC",
					e);
		}
	}

}
