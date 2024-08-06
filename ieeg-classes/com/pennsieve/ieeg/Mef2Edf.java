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
package com.pennsieve.ieeg;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Range;
import com.google.common.math.DoubleMath;
import com.google.common.math.LongMath;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Shorts;




import edu.upenn.cis.Strings;
import edu.upenn.cis.db.mefview.services.TimeSeriesPage;
import edu.upenn.cis.eeg.EegFileUtils;
import edu.upenn.cis.eeg.edf.EDFHeader;
import edu.upenn.cis.eeg.edf.EDFHeader.Builder;
import edu.upenn.cis.eeg.edf.EDFWriter;
import edu.upenn.cis.eeg.mef.MEFStreamer;
import edu.upenn.cis.eeg.mef.MefHeader2;

/**
 * Converts mef files into edf files. The mef2edf tool reads in mef files from
 * the command line and converts them into edf files. It checks for
 * discontinuities between blocks of data and writes an edf file for each
 * continuous recordings. All input mef files must have the same sampling rate.
 * See documentation: htps://code.google.com/p/braintrust/wiki/mef2edf
 * 
 * @author Veena Krish
 * 
 */
public class Mef2Edf {

	private String[] fileNames;
	private int maxBlockBatch = 100; // number of blocks to convert at a time
	private String outFileParent;
	private String outFile;
	private String outDir;

	private long numMefBlocks;
	private long recordingStartTimeUutc;
	private long recordingStartTimeEdfUutc;
	private MefHeader2[] mefHeaders;
	private Builder edfBuilder;
	private EDFHeader edfHeader;
	private EDFWriter writer;
	private MEFStreamer[] mefStreamers;

	private boolean sameSamplingRate;
	private boolean timestampsDiffer;
	private long lastTimeEnd = 0;
	private double fs; // sampling rate
	private Logger logger = LoggerFactory.getLogger(getClass());

	public Mef2Edf(String configFile, String outputFile) throws IOException {

		File cf = new File(configFile);
		Scanner config = new Scanner(cf);
		List<String> fileList = new LinkedList<String>();

		while (config.hasNext()) {
			fileList.add(config.nextLine());
		}
		config.close();
		Collections.sort(fileList, Strings.getNaturalComparator());

		this.fileNames = fileList.toArray(new String[fileList.size()]);

		// put outFile in new directory
		String outFileName = FilenameUtils.getBaseName(outputFile);
		String currentDir = System.getProperty("user.dir");
		outDir = currentDir + "/" + outFileName + "_EDF";
		new File(outDir).mkdir();
		String outFilePath = outDir + "/" + outFileName + ".edf";

		this.outFile = outFilePath;

		fs = samplingRateTest();

	}

	public Mef2Edf(List<String> filenames, String outputFile)
			throws IOException {

		Collections.sort(filenames, Strings.getNaturalComparator());
		this.fileNames = filenames.toArray(new String[filenames.size()]);

		for (int i = 0; i < fileNames.length; i++) {
			System.out.println("Converting: " + fileNames[i]);
		}

		// put outFile in new directory
		String outFileName = FilenameUtils.getBaseName(outputFile);
		String currentDir = System.getProperty("user.dir");
		outDir = currentDir + "/" + outFileName + "_EDF";
		new File(outDir).mkdir();
		String outFilePath = outDir + "/" + outFileName + ".edf";

		this.outFile = outFilePath;

		fs = samplingRateTest();
	}

	public void main() throws IOException, Mef2EdfException {

		// Create edf header from mef headers
		getMEFHeaders();
		createEDFHeader(mefHeaders);
		edfHeader = edfBuilder.build();

		// Open writer, outputFile, and streamers
		int pos = outFile.lastIndexOf('.');
		outFileParent = outFile.substring(0, pos);
		outFile = outFileParent + "_0.edf";

		writer = new EDFWriter(outFile, edfHeader);
		mefStreamers = new MEFStreamer[fileNames.length];
		for (int ch = 0; ch < fileNames.length; ch++) {
			mefStreamers[ch] = new MEFStreamer(new FileInputStream(
					fileNames[ch]), true);
		}
		lastTimeEnd = recordingStartTimeUutc;

		// 3. Loop to convert data at NUM_BLOCKS at a time
		int block = 0;

		while (block < numMefBlocks) {
			int numBlocksToConvert = maxBlockBatch;
			if (block + numBlocksToConvert > numMefBlocks) {
				numBlocksToConvert = (int) (numMefBlocks % numBlocksToConvert);
			}
			getMEFData(block, numBlocksToConvert);
			block += numBlocksToConvert;

			System.out.println("Converted " + block + " mef blocks out of "
					+ numMefBlocks);
		}

		// Close streamers and writer
		for (int ch = 0; ch < fileNames.length; ch++) {
			mefStreamers[ch].close();
		}
		writer.close();

		System.out.println("Conversion finished.");
	}

	/**
	 * Creates a MEFHeader for each file to be converted from the first 1024
	 * bytes (standard mef header size).
	 * 
	 * @throws IOException
	 */
	public void getMEFHeaders() throws IOException {
		mefHeaders = new MefHeader2[fileNames.length];
		byte[] mefBytes = new byte[1024]; // header is first 1024 bytes
		recordingStartTimeUutc = -1;
		numMefBlocks = -1;
		for (int i = 0; i < fileNames.length; i++) {
			RandomAccessFile mefFile = new RandomAccessFile(fileNames[i], "r");
			mefFile.readFully(mefBytes);
			mefHeaders[i] = new MefHeader2(mefBytes);
			mefFile.close();
			if (i == 0) {
				recordingStartTimeUutc = mefHeaders[i].getRecordingStartTime();
				numMefBlocks = mefHeaders[i].getNumberOfIndexEntries();
			} else {
				if (recordingStartTimeUutc != mefHeaders[i]
						.getRecordingStartTime()) {
					throw new Mef2EdfException(
							"All files to be converted must have the same recording start time. File ["
									+ fileNames[i]
									+ "] has a start time of "
									+ mefHeaders[i].getRecordingStartTime()
									+ " uUTC which does not match the start time of "
									+ recordingStartTimeUutc
									+ " uUTC found in the files checked so far.");
				}
				if (numMefBlocks != mefHeaders[i]
						.getNumberOfIndexEntries()) {
					throw new Mef2EdfException(
							"All files to be converted must have the same number of blocks. File ["
									+ fileNames[i]
									+ "] "
									+ mefHeaders[i].getNumberOfIndexEntries()
									+ " blocks which does not match the number of blocks "
									+ numMefBlocks
									+ " found in the files checked so far.");
				}
			}
		}
	}

	/**
	 * Creates an EDFHeader provided an array of MEFHeaders. First creates a
	 * EDFBuilder to populate a new EDFHeader, then sets all information and
	 * adds appropriate channel info by iterating through the String[] fileNames
	 * initially provided. Also adds an annotation channel. Calls
	 * edfBuilder.build() to create the final header.
	 * 
	 * @param mefHeader
	 * @throws IOException
	 */
	public void createEDFHeader(MefHeader2[] mefHeaders) throws IOException {
		Date startDate = uutcToEDFStartDate(recordingStartTimeUutc);
		recordingStartTimeEdfUutc = startDate.getTime() * 1000;
		edfBuilder = new EDFHeader.Builder(startDate);

		edfBuilder.setIdCode("0       ");
		edfBuilder.setFormatVersion("");
		edfBuilder.setPatientInfo("X", true, null, "X");
		edfBuilder.setRecordingInfo(
				mefHeaders[0].getInstitution().replace(",", ""), "X", "X");
		double dur = calculateBlockDuration();
		edfBuilder.setDurationOfRecords(dur);
		logger.debug("duration of recs: {}", dur);
		// The writer will set the correct number of records
		edfBuilder.setNumberOfRecords(-1);
		for (int ch = 0; ch < fileNames.length; ch++) {
			// Voltage conversion factor may be negative
			final double extremeValueMicroVolts1 = mefHeaders[ch]
					.getMaximumDataValue()
					* mefHeaders[ch].getVoltageConversionFactor();
			final double extremeValueMicroVolts2 = mefHeaders[ch]
					.getMinimumDataValue()
					* mefHeaders[ch]
							.getVoltageConversionFactor();
			final double minPhysicalValueMicroVolts = Doubles.min
					(extremeValueMicroVolts1, extremeValueMicroVolts2);
			final double maxPhysicalValueMicroVolts = Doubles.max
					(extremeValueMicroVolts1, extremeValueMicroVolts2);
			edfBuilder
					.addChannel(
							FilenameUtils.getBaseName(fileNames[ch]),
							"Unknown",
							"uV",
							minPhysicalValueMicroVolts,
							maxPhysicalValueMicroVolts,
							Integer.valueOf(Short.MIN_VALUE), // standard -32767
							Integer.valueOf(Short.MAX_VALUE),
							"HP:"
									+ mefHeaders[ch]
											.getLowFrequencyFilterSetting()
									+ "Hz "
									+ "LP:"
									+ mefHeaders[ch]
											.getHighFrequencyFilterSetting()
									+ "Hz" + "N:"
									+ mefHeaders[ch].getNotchFilterFrequency()
									+ "Hz",
							DoubleMath.roundToInt(dur * fs, RoundingMode.FLOOR));
		}
		edfHeader = edfBuilder.build();
	}

	@VisibleForTesting
	Date uutcToEDFStartDate(long startTimeUutc) {
		// Round up start time to nearest second since EDF does not do
		// sub-second start times. May need to throw away some values
		long startTimeUnix = LongMath.divide(
				startTimeUutc,
				1_000_000,
				RoundingMode.CEILING);
		Date startDate = new Date(startTimeUnix * 1000);
		return startDate;
	}

	/**
	 * Retrieves data from mef file and calls convertMEFData. Handles
	 * discontinuities between batches of blocks by calling createWriter() if
	 * necessary
	 * 
	 * @param startBlock
	 * @param numBlocksAtOnce
	 * @throws Exception
	 */
	public void getMEFData(int startBlock, int numBlocksAtOnce)
			throws IOException, Mef2EdfException {
		@SuppressWarnings("unchecked")
		List<TimeSeriesPage> pages[] = new ArrayList[fileNames.length];
		for (int ch = 0; ch < fileNames.length; ch++) {
			pages[ch] = mefStreamers[ch].getNextBlocks(numBlocksAtOnce);
		}
		if (pages[0].get(0).timeStart - lastTimeEnd >= (1000000 / fs)) {
			final Date edfStartDate = uutcToEDFStartDate(
					pages[0].get(0).timeStart);
			createNewWriter(edfStartDate);
		}
		lastTimeEnd = convertMEFData(pages);

	}

	/**
	 * Writes mef data to an EDFWriter. Checks for discontinuities between
	 * blocks and calls createWriter() if necessary
	 * 
	 * @param pages, an array of List<TimeSeriesPage> returned from mefStreamers
	 *            for each channel
	 * @return The last timestamp of the bunch of blocks that it's written
	 * @throws Exception
	 */
	public long convertMEFData(final List<TimeSeriesPage>[] pages)
			throws IOException, Mef2EdfException {

		int numPages = pages[0].size();

		long previousEndUutc = -1;
		int lowIndex = 0;
		List<Range<Integer>> continuousRanges = newArrayList();
		// First make sure beginnings and ends of blocks line up. Also find gaps
		long startBlockTimeUutc, endBlockTimeUutc, otherStartUutc, otherEndUutc;
		for (int bl = 0; bl < numPages; bl++) {
			startBlockTimeUutc = pages[0].get(bl).timeStart;
			endBlockTimeUutc = pages[0].get(bl).timeEnd;
			logger.debug("startBlockTime: {} and endBlockTime: {} ",
					startBlockTimeUutc, endBlockTimeUutc);
			for (int ch = 1; ch < fileNames.length; ch++) {
				if (numPages != pages[ch].size()) {
					throw new Mef2EdfException(
							"Error in mef files: " + fileNames[ch] + " differs in the number of MEF blocks");
				}
				otherStartUutc = pages[ch].get(bl).timeStart;
				otherEndUutc = pages[ch].get(bl).timeEnd;
				if (otherStartUutc != startBlockTimeUutc
						|| otherEndUutc != endBlockTimeUutc) {
					timestampsDiffer = true;
					throw new Mef2EdfException(
							"Error in mef file: " + fileNames[ch] + " differs in its timestamps from other channels");
				}
			}
			if (previousEndUutc != -1
					&& startBlockTimeUutc - previousEndUutc >= 1e6 / fs) {
				// A gap between page bl - 1 and page bl.
				continuousRanges.add(Range.closedOpen(lowIndex, bl));
				lowIndex = bl;
			}
			previousEndUutc = endBlockTimeUutc;
		}
		continuousRanges.add(Range.closedOpen(lowIndex, numPages));

		short[][] digVals = new short[fileNames.length][];
		for (Range<Integer> continuousRange : continuousRanges) {
			@SuppressWarnings("unchecked")
			List<Integer> allDigVals = new ArrayList<>();
			final long fileStartUutc = writer.getEDFHeader().EDFDate2uUTC();
			long firstSampleUutc = -1;
			for (int ch = 0; ch < fileNames.length; ch++) {
				allDigVals.clear();
				final long minMefValue = mefHeaders[ch].getMinimumDataValue();
				final long maxMefValue = mefHeaders[ch].getMaximumDataValue();

				for (TimeSeriesPage blocksToWrite : pages[ch].subList(
						continuousRange.lowerEndpoint(),
						continuousRange.upperEndpoint())) {
					for (int i = 0; i < blocksToWrite.values.length; i++) {
						final double sampleUutc = blocksToWrite.timeStart + i
								* (1e6 / fs);
						if (sampleUutc >= fileStartUutc) {
							// We may have to throw away data if MEF block
							// starts in the sub-second before the EDF file
							// does. Really could be limited to the first time a
							// writer is used
							if (firstSampleUutc == -1) {
								firstSampleUutc = (long) sampleUutc;
							}
							double scaledValue = (blocksToWrite.values[i] - minMefValue)
									/ (1.0 * (maxMefValue - minMefValue));
							scaledValue = scaledValue
									* (Short.MAX_VALUE - Short.MIN_VALUE)
									+ Short.MIN_VALUE;
							
							//Addition of debugging statements
							System.out.println("Original Value: " + blocksToWrite.values[i]);
							System.out.println("Scaled Value: " + scaledValue);
							System.out.println("minMefValue: " + minMefValue);
							System.out.println("maxMefValue: " + maxMefValue);
							
							// added this to clmap the value to be within the short range
							if (scaledValue > Short.MAX_VALUE) {
								scaledValue = Short.MAX_VALUE;
							} else if (scaledValue < Short.MIN_VALUE) {
								scaledValue = Short.MIN_VALUE;
							}
							
							// Ensure the value is within the range of short before casting 
							//if (scaledValue < Short.MIN_VALUE || scaledValue > Short.MAX_VALUE) {
							//	System.out.println("Value out of range: " + scaledValue);
							//	throw new IllegalArgumentException("Value " + scaledValue + " out of range for short");
							//}
							
							// end of what was added
							final Integer digitalValue = Integer
									.valueOf(Shorts.checkedCast(Math
											.round(scaledValue)));
							allDigVals.add(digitalValue);
						}
					}
				}
				digVals[ch] = Shorts.toArray(allDigVals);
			}
			writer.appendDataBlock(digVals,
					firstSampleUutc
							- fileStartUutc);
			// Only create a new writer if this is not the last range
			if (continuousRange.upperEndpoint().intValue() != numPages) {
				final Date edfStartDate = uutcToEDFStartDate(
						pages[0].get(continuousRange.upperEndpoint()).timeStart);
				createNewWriter(edfStartDate);
			}
		}
		return pages[0].get(numPages - 1).timeEnd;

	}

	/**
	 * Closes current EDFWriter and opens a new one for discontinuous edf files
	 */
	public void createNewWriter(Date startTime) {

		writer.close();
		if (writer.getNrRecords() == 0) {
			File emptyFile = new File(writer.getOutputFilename());
			if (emptyFile.exists()) {
				System.out.println("Deleting file with no data: ["
						+ writer.getOutputFilename()
						+ "]. Will reuse filename if necessary.");
				emptyFile.delete();
			}
		}

		// create a new output file to replace out_file:
		int childNo = 0;
		String child = "_" + childNo + ".edf";
		File newFile = new File(outFileParent + child);
		while (newFile.exists()) {
			childNo++;
			child = "_" + childNo + ".edf";
			newFile = new File(outFileParent + child);
		}
		outFile = newFile.getAbsolutePath();
		EDFHeader.Builder builder = new Builder(edfHeader);
		builder.setStartTime(startTime);
		edfHeader = builder.build();
		final DateFormat startTimeFormat = SimpleDateFormat
				.getDateTimeInstance();
		startTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		System.out.println("Starting new EDF file: " + newFile.getName()
				+ " starting at: " + startTimeFormat.format(startTime));
		writer = new EDFWriter(outFile, edfHeader);

	}

	/**
	 * Tests the MEFs used in the conversion to make sure the sampling rates are
	 * homogeneous, sets the boolean sameSamplingRate to the result
	 * 
	 * @throws IOException
	 */
	private double samplingRateTest() throws IOException {
		double[] rates = new double[fileNames.length];
		byte[] rate = null;
		RandomAccessFile searchSample = null;
		for (int i = 0; i < fileNames.length; i++) {
			rate = new byte[8];
			searchSample = new RandomAccessFile(fileNames[i], "r");
			searchSample.seek(424);
			searchSample.read(rate);
			rates[i] = Double.longBitsToDouble(EegFileUtils
					.getLongFromBytes(rate));
			searchSample.close();
		}

		this.sameSamplingRate = true;

		for (int i = 0; i < fileNames.length; i++) {
			if (rates[0] != rates[i]) {
				this.sameSamplingRate = false;
				throw new Mef2EdfException("Sampling Rates among files differ");
			}
		}

		return rates[0];
	}

	public double calculateBlockDuration() throws IOException {

		if (fs == 0) {
			fs = samplingRateTest(); // make sure fs is set for tests...
		}
		final int noChannels = fileNames.length;
		final double maxDuration = 30720 / (fs * noChannels);
		double bestDuration = 0;
		double minError = Double.MAX_VALUE;
		int bestN = 0;

		// Only bother with durations < 1 if sample rate is not an integer or if
		// 1 second duration exceeds max data record bytes
		if (!DoubleMath.isMathematicalInteger(fs) || fs * noChannels > 30720) {
			// durations < 1. Seven decimal digits + one decimal point to fit in
			// EDF header
			for (int scale = 1; scale < 6; scale++) {
				for (int unscaledValue = 1; unscaledValue <= 9_999_999
						&& unscaledValue <= maxDuration * Math.pow(10, scale); unscaledValue++) {
					// duration = unscaledValue * 10^(-scale)
					final BigDecimal duration = new BigDecimal(
							new BigInteger(
									Integer.toString(unscaledValue)),
							scale);

					final double d = duration.doubleValue();
					double nWithError = d * fs;
					int n = DoubleMath.roundToInt(nWithError,
							RoundingMode.FLOOR);
					double error = nWithError - n;
					double relError = error / nWithError;
					if (relError < minError) {
						minError = relError;
						bestDuration = d;
						bestN = n;
					}
				}
			}

		}
		// integer durations. 8 decimal digits to fit in EDF header
		for (int d = 1; d <= maxDuration && d <= 99_999_999; d++) {
			double nWithError = d * fs;
			int n = DoubleMath.roundToInt(nWithError, RoundingMode.FLOOR);
			double error = nWithError - n;
			double relError = error / nWithError;
			if (relError < minError) {
				minError = relError;
				bestDuration = d;
				bestN = n;
			}

		}
		System.out
				.println(
				"Calculated EDF data record duration of "
						+ bestDuration
						+ " seconds for "
						+ noChannels
						+ " channel"
						+ (noChannels > 1 ? "s" : "")
						+ " at "
						+ fs
						+ " Hz. Continuous MEF segments shorter than this will be lost.");
		return bestDuration;
	}

	/**
	 * returns the sameSamplingRate boolean
	 * 
	 * @return true if the sampling rates of all the MEFs used in the conversion
	 *         match, false otherwise
	 */
	public boolean getSameSamplingRate() {
		return sameSamplingRate;
	}

	/**
	 * returns whether timestamps differ...
	 * 
	 * @return true if the start and end timestamps for some mef block aren't
	 *         the same for all channels at that block.
	 */
	public boolean getTimestampsDiffer() {
		return timestampsDiffer;
	}

	@VisibleForTesting
	String getOutFile() {
		return outFile;
	}

	@VisibleForTesting
	String getOutDir() {
		return outDir;
	}

}

class Mef2EdfException extends RuntimeException {

	private static final long serialVersionUID = 42L;

	public Mef2EdfException(String msg) {
		super(msg);

	}
}
