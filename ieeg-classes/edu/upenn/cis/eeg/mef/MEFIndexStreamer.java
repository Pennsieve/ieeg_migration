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

package edu.upenn.cis.eeg.mef;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import com.google.common.io.ByteStreams;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.primitives.Ints;

import edu.upenn.cis.db.mefview.services.TimeSeriesPage;
import edu.upenn.cis.thirdparty.RED;
import edu.upenn.cis.thirdparty.RED.PageAndBytesRead;

/**
 * For reading forward through a mef file.
 * 
 * Like {@link MEFStreamer} but can be used if there are corrupt blocks and a
 * good index.
 * 
 * @author John Frommeyer
 */
public final class MEFIndexStreamer implements Closeable {

	private int indexEntriesRead = 0;
	private long nextBlockOffset = -1;
	private final MefHeader2 mefHeader;
	private final RED red = new RED();
	private final RandomAccessFile mefDataStream;
	private final LittleEndianDataInputStream mefIndexStream;
	private final boolean decompressData;
	private final long indexOffset;

	/**
	 * Streams through mef data. Useful if you don't need random access into the
	 * file.
	 * 
	 * {@code mefInputStream}'s pointer will be moved. It is imperative that
	 * clients do not move the pointer while use a {@code MEFStreamer}.
	 * 
	 * @param mefInputStream the mef file stream
	 * @param decompressData if true then decompress the data, otherwise the
	 *            {@code TimeSeriesPage}s will have no data, only start and end
	 *            times
	 * 
	 * @throws IOException if thrown will reading the mef file
	 */
	public MEFIndexStreamer(
			File mefFile,
			boolean decompressData) throws IOException {
		mefDataStream = new RandomAccessFile(mefFile, "r");
		byte[] headerBytes = new byte[1024];
		mefDataStream.readFully(headerBytes);
		mefHeader = new MefHeader2(headerBytes);
		indexOffset = this.mefHeader.getIndexDataOffset();
		mefIndexStream = new LittleEndianDataInputStream(new FileInputStream(
				mefFile));
		ByteStreams.skipFully(mefIndexStream, indexOffset);
		nextBlockOffset = getNextBlockOffset();

		this.decompressData = decompressData;
	}

	/**
	 * Closes the client supplied {@code RandomAccessFile}.
	 * 
	 * @throws IOException from calling close.
	 */
	@Override
	public void close() throws IOException {
		mefDataStream.close();
		mefIndexStream.close();
	}

	public MefHeader2 getMEFHeader() {
		return mefHeader;
	}

	/**
	 * Get the next {@code noBlocks} mef blocks or less from the file. Returns
	 * less than {@code noBlocks} if there are less left. Returns an empty list
	 * if there are no more blocks.
	 * 
	 * @param noBlocks the number of blocks you want to read
	 * 
	 * @return the decompressed blocks that were read
	 * 
	 * @throws IOException if thrown when reading the file
	 */
	public List<TimeSeriesPage> getNextBlocks(int noBlocks)
			throws IOException {
		List<TimeSeriesPage> pages = newArrayList();
		final double samplingPeriodMicros = getSamplingPeriodMicros();
		for (int i = 0; i < noBlocks; i++) {

			if (!moreBlocks()) {
				return pages;
			}
			long blockOffset = nextBlockOffset;
			nextBlockOffset = getNextBlockOffset();
			indexEntriesRead++;
			int lengthOfBlockBytes = Ints.checkedCast(nextBlockOffset
					- blockOffset);

			mefDataStream.seek(blockOffset);
			byte[] blockBytes = new byte[lengthOfBlockBytes];
			mefDataStream.readFully(blockBytes);

			PageAndBytesRead pageAndBytesRead =
					red.decodePage(
							blockBytes,
							0,
							samplingPeriodMicros,
							decompressData);
			pages.add(pageAndBytesRead.page);

		}
		return pages;
	}

	private long getNextBlockOffset() throws IOException {
		checkState(moreBlocks());
		long blockOffset;
		if (indexEntriesRead == mefHeader.getNumberOfIndexEntries() - 1) {
			blockOffset = indexOffset;
		} else {
			// Skip block start uutc
			ByteStreams.skipFully(mefIndexStream, 8);
			// read offset
			blockOffset = mefIndexStream.readLong();
			// skip index of first sample in block
			ByteStreams.skipFully(mefIndexStream, 8);
		}
		
		return blockOffset;
	}

	/**
	 * Get the total number of blocks in the mef file.
	 * 
	 * @return the total number of blocks in the mef file
	 */
	public long getNumberOfBlocks() {
		return mefHeader.getNumberOfIndexEntries();
	}

	public double getSamplingPeriodMicros() {
		return 1E6 / mefHeader.getSamplingFrequency();
	}

	public long getRecordingStartTimeUutc() {
		return mefHeader.getRecordingStartTime();
	}

	/**
	 * Tells us if there are any blocks left.
	 * 
	 * @return true if there are blocks left, false otherwise
	 */
	public boolean moreBlocks() {
		if (indexEntriesRead < mefHeader.getNumberOfIndexEntries()) {
			return true;
		}
		return false;
	}

}
