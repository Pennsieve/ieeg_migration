/*
 * (The MIT license)
 * 
 * Copyright (c) 2012 Wolfgang Halbeisen (halbeisen.wolfgang@gmail.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
 * and associated documentation files (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies 
 * or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE 
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.upenn.cis.eeg.edf;

import static edu.upenn.cis.eeg.edf.EDFConstants.DISCONTINUITY_LIMIT;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;

import edu.upenn.cis.eeg.edf.EDFHeader.Builder;

class EDFWriterException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EDFWriterException(String msg) {
		super(msg);
	}
}

// TODO: Deal with channels that have different start times??

/**
 * This class is capable of writing EDF+ data structures.
 * 
 * EDF+ can deal with non-continuous data but has a constant number of samples
 * per block stored. Therefore, non-continuities can only be included on a
 * 'per-block' interval. This EDF+ writer will truncate incomplete blocks prior
 * to a discontinuity. This will cause some data-loss at those interfaces, but
 * assures that all stored data are real recorded values.
 * 
 * TODO: You should be able to set the block size as an argument to the
 * constructor. Setting the block size to 1 sample will prevent trimming but
 * will greatly inflate the file size due to the annotation channel on each
 * block.
 * 
 */
public class EDFWriter
{

	private String out_file_name;
	private int file_closed;
	private FileOutputStream out_file;
	private EDFHeader out_header;
	private long last_timestamp;
	private Integer[] signalCh;
	// private Integer[] annotationCh;
	private int nrWrittenRecords;
	private short[][] extra;

	public EDFWriter(String filename, EDFHeader header) {

		out_file_name = filename;
		out_header = header;

		// Check for Annotation Channel. (Enable for EDF+ files)
		/*
		 * annotationCh = header.findAnnotationChannels(); if (annotationCh ==
		 * null){ throw new
		 * EDFWriterException("EDF Header must contain Annotation Channel."); }
		 */

		// Check for Signal Channels
		signalCh = header.findSignalChannels();
		if (signalCh == null) {
			throw new EDFWriterException(
					"EDF Header must contain at least one Signal Channel.");
		}

		// Open channel output file, and write header to it
		try {
			out_file = new FileOutputStream(out_file_name);
		} catch (FileNotFoundException e) {
			throw new EDFWriterException("Can't create output EDF file: "
					+ out_file_name);
		}

		// this writes a header to the beginning of the file.
		// The header will be properly written upon closing the file.
		try {
			out_file.write(getEDFHeader().serialize().array());
		} catch (IOException e) {
			throw new EDFWriterException("Can't write to output EDF file: "
					+ out_file_name);
		}

		file_closed = 0;

	}

	public int getNrRecords() {
		return this.nrWrittenRecords;
	}
	
	public String getOutputFilename() {
		return out_file_name;
	}

	public void close() {
		if (file_closed == 1)
			throw new EDFWriterException(
					"Can't close a EDFWriter object that has already been closed!");

		file_closed = 1;

		try {

			out_file.close();
		} catch (IOException e) {
			throw new EDFWriterException("Can't close output EDF file: "
					+ out_file_name);
		}

		// Rewrite header, with completely filled in data
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(out_file_name, "rw");
		} catch (FileNotFoundException e) {
			throw new EDFWriterException("Can't open output mef file: "
					+ out_file_name);
		}
		try {
			raf.seek(0);

			// create new updated header
			Builder newHeader = new EDFHeader.Builder(getEDFHeader());
			newHeader.setNumberOfRecords(nrWrittenRecords);
			// Date newStartTime = new Date(timestamp/1000);
			// newHeader.setStartTime(newStartTime);

			raf.write(newHeader.build().serialize().array());
		} catch (IOException e) {
			try {
				raf.close();
			} catch (IOException e2) {
				throw new EDFWriterException(
						"Can't write to or close output EDF file: "
								+ out_file_name);
			}
			throw new EDFWriterException("Can't write to output EDF file: "
					+ out_file_name);
		}
		try {
			raf.close();
		} catch (IOException e) {
			throw new EDFWriterException("Can't close output EDF file: "
					+ out_file_name);
		}

	}

	/**
	 * Appends data to the EDF-File. The timestamp is the time offset in usec
	 * from the beginning of the file.
	 * 
	 * @param s
	 * @param timestamp
	 * @return the number of records that it's written to a file
	 * @throws IOException
	 */
	public int appendDataBlock(short[][] s, long timestamp) throws IOException,
			NullPointerException
	{

		// Check file open
		if (file_closed == 1)
			throw new EDFWriterException(
					"Can't write data to a EDFWriter object that has already been closed!");

		// Check that number of Channels is correct
		if (s.length != signalCh.length)
			throw new EDFWriterException(
					"Number of channels must match the number of signal channels in the header");

		// Check continuity

		double lastTsEnd = last_timestamp + getEDFHeader().durationOfRecords
				* 1e6;
		double adjustedTimestampMicros = timestamp;
		if (extra != null) {
			final double tsOffsetFromExtrasMicros = extra[0].length
					* ((getEDFHeader().durationOfRecords * 1e6) / getEDFHeader().numberOfSamples[0]);
			adjustedTimestampMicros = timestamp - tsOffsetFromExtrasMicros;
		}
		double timestampDiffMicros = (lastTsEnd - adjustedTimestampMicros);

		if (last_timestamp != 0 && timestampDiffMicros > DISCONTINUITY_LIMIT) {
			System.out.println("Current Position in Record: " + lastTsEnd);
			throw new EDFWriterException(
					"Trying to write data with a timestamp < than current position in record.");
		}

		short[][] newS;
		// Append extra if necessary
		if (extra != null) {
			// append extra
			newS = new short[s.length][s[0].length + extra[0].length];

			for (int ch = 0; ch < s.length; ch++) {
				System.arraycopy(extra[ch], 0, newS[ch], 0, extra[ch].length);
			}
			for (int ch = 0; ch < s.length; ch++) {
				System.arraycopy(s[ch], 0, newS[ch], extra[0].length - 1,
						s[0].length);
			}
			System.out.println("inserted " + extra[0].length
					+ " samples left over from last append");
		} else {
			newS = s;

		}

		int numSamples = 0;
		for (Integer singleCh : getEDFHeader().numberOfSamples) {
			numSamples += singleCh;
		}

		Integer[] samplesPerBlock = getEDFHeader().numberOfSamples;
		int nrBlocks = (newS[0].length / samplesPerBlock[0]);

		int extraSamples = newS[0].length - (nrBlocks * samplesPerBlock[0]);
		int nr = nrBlocks * samplesPerBlock[0];

		// Store extra values from newS if necessary:
		if (nrBlocks * samplesPerBlock[0] == newS[0].length) {
			extra = null;
		}
		else {
			// create extra
			extra = new short[newS.length][extraSamples];
			// copy the extra values from newS into extra

			for (int ch = 0; ch < newS.length; ch++) {
				System.arraycopy(newS[ch], nr, extra[ch], 0, extraSamples);
			}

		}
		if (nrBlocks > 0) {
			// Create timestamp array
			long[] tsArray = new long[nrBlocks];
			tsArray[0] = (long) adjustedTimestampMicros;
			for (int i = 1; i < nrBlocks; i++) {
				tsArray[i] = (long) (tsArray[i - 1] + getEDFHeader().durationOfRecords * 1e6);
			}
			ByteBuffer byteBuffer = ByteBuffer.allocate(2 * numSamples
					* nrBlocks);
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
			ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
			FileChannel f2 = out_file.getChannel();

			// Write Signal Data to Buffer
			for (int iBlock = 0; iBlock < nrBlocks; iBlock++) {
				int curChannel = 0;
				for (int iCh = 0; iCh < getEDFHeader().numberOfChannels; iCh++) {
					int offset = iBlock * samplesPerBlock[iCh];
					/*
					 * if (Arrays.asList(annotationCh).contains(iCh)) { if (iCh
					 * == annotationCh[0]) { // Write Timestamp short[] timeAnn
					 * = createTimeAnn(tsArray[iBlock],samplesPerBlock[iCh]);
					 * shortBuffer.put(timeAnn);
					 * 
					 * } else{ // Fill with null short[] emptyAnnotation = new
					 * short[samplesPerBlock[iCh]];
					 * shortBuffer.put(emptyAnnotation);
					 * 
					 * } } else{
					 */

					for (int iSample = 0; iSample < samplesPerBlock[iCh]; iSample++) {
						shortBuffer.put(newS[curChannel][offset + iSample]);
					}
					curChannel++;
					// }
				}
			}
			nrWrittenRecords += nrBlocks;
			System.out.println("Wrote " + nrBlocks + " EDF data records. Total data records written so far: " + nrWrittenRecords);
			f2.write(byteBuffer);
			last_timestamp = tsArray[tsArray.length - 1];
		}
		return nrWrittenRecords;

	}

	/**
	 * Creates a short arrays that can be written to the EDF+ file as the
	 * timestamp for the record.
	 * 
	 * @param time (in microseconds)
	 * @param nrSamples (length of annotation array)
	 * @return
	 * @throws IOException
	 */
	private static short[] createTimeAnn(long time, int nrSamples)
			throws IOException {

		// Create Timestamp String in seconds
		String test = "+" + String.format("%.6f", time / 1e6);
		// Put into ByteBuffer
		ByteBuffer out_bytes = ByteBuffer.allocate(nrSamples * 2);
		out_bytes.put(test.getBytes("UTF-8"));

		// Add terminator token
		byte[] token = { 0x14, 0x14 };
		out_bytes.put(token);

		// Turn into short-array which can be written to the file.
		short[] out_shorts = new short[nrSamples];
		out_bytes.rewind();
		out_bytes.order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(out_shorts);

		return out_shorts;

	}

	/**
	 * @return the out_header
	 */
	public EDFHeader getEDFHeader() {
		return out_header;
	}
}
