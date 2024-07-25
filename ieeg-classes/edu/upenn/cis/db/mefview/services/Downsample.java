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

import java.math.RoundingMode;
import java.util.ArrayList;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.math.DoubleMath;

import edu.upenn.cis.db.mefview.eeg.IntArrayWrapper;


public class Downsample implements PostProcessor {
	private final static Logger logger = LoggerFactory
			.getLogger(Downsample.class);
	private static final int[] nul = new int[0];
	private final int scaleFactor;
	private final double baseFreq;
	
	public Downsample(double baseFreq, int sampleFactor) {
		this.baseFreq = baseFreq;
		scaleFactor = sampleFactor;
	}
	
	@Override
	public boolean needsWorkBuffer(int sampleCount) { return false; }
	
	@Override
	public boolean isFiltered() { return false; }
	
	@Override
	public double getSampleRate() { return baseFreq / scaleFactor; }

	@Override
	public void createWorkBuffer(int sampleCount) {
	}

	@Override
	public void setWorkBuffer(int[] buffer) {
	}

	@Override
	public ArrayList<TimeSeriesData> process(double startTime, double endTime, double trueFreq,
			long startTimeOffset, double voltageScale, TimeSeriesPage[] pageList, ChannelSpecifier path,
			boolean minValIsNull, double padBefore, double padAfter) {
		return Downsample.downsample(startTime, endTime, trueFreq, 
				startTimeOffset, scaleFactor, voltageScale, pageList,
				path, false);
	}

	/**
		 * Do a linear downsample of the data, by the specified factor with no interpolation.  Does not do
		 * any pre-filtering.
		 * 
		 * @param startTime Start time for returned samples
		 * @param endTime End time for returned samples
		 * @param trueFreq Native frequency of the data
		 * @param startTimeOffset The offset to the MEF start time (otherwise startTime is ref'd to 0)
		 * @param factor Downsampling scale factor -- return every <i>factor</i> elements
		 * @param scale The scale factor of the data (from the MEF header, copied into the <code>TimeSeriesData</code> struct)
		 * @param pageList List of pages from the MEF file
		 * @param path Specifier for the file -- only used for logging
		 * @param minValIsNull Treat Integer.MIN_VALUE as a null (gap); otherwise it's treated as a value
		 * @return
		 */
		public static ArrayList<TimeSeriesData> downsample(double startTime, double endTime, 
				double trueFreq, long startTimeOffset, int factor, double scale, 
				TimeSeriesPage[] pageList,
				@Nullable ChannelSpecifier path, boolean minValIsNull) {
			final String m = "downsample(...)";
	
			ArrayList<TimeSeriesData> results = new ArrayList<TimeSeriesData>();
			double actualPeriod = 1.E6 / trueFreq;
	
			double outputTime = startTime;
			double offset = startTime;
	//		long trueStartTime = pageList[0].timeStart;
	
			int currentPage = 0;
			int pos = 0;
	
			double samplingPeriod = actualPeriod * factor;
			int size = (int)Math.ceil((endTime - startTime) / samplingPeriod);//(int) ((endTime - startTime + samplingPeriod - 1) / samplingPeriod);// + 1;
	
			if (logger.isDebugEnabled()) {
				logger.debug("{}: Downsampling from {} to {}", new Object[] { m,
						actualPeriod, samplingPeriod });
			}
			
			if (size < 0) {
				size = 0;
			}
	
			int count = 0;
	
			int iterationCount = (int)Math.ceil((endTime - startTime) / samplingPeriod);
	
			logger.trace(m + ": Trace should return " + iterationCount
					+ " samples and has " + size + " elements");
			TimeSeriesData span = new TimeSeriesData(startTimeOffset, actualPeriod,
					scale, size);
			results.add(span);
			span.setPeriod(samplingPeriod);
	//		while (outputTime < trueStartTime && outputTime < endTime) {
	//			span.addSample(null);
	//			outputTime += samplingPeriod;
	//			iterationCount--;
	//		}
	
			int siz = pageList.length;
			
			while (siz > 0 && pageList[siz-1] == null)
				siz--;
	
			int[][] collection = new int[siz][];
			for (int i = 0; i < siz; i++) {
				if (pageList[i] != null && pageList[i].values != null)
					collection[i] = pageList[i].values;
				else
					collection[i] = nul;
			}
			IntArrayWrapper iw = new IntArrayWrapper(collection);
			
			pos = 0;
			
			int nullsAdded = 0;
			int valuesAdded = 0;
	
			// Index within the output
			int outIndex = 0;
			// Index within the input array
			int inIndex = 0;
			long prevPageEndUutc = -1;
			// Iterate page-by-page so we can track the gaps
			for (currentPage = 0; currentPage < siz; currentPage++) {
				inIndex = iw.getStart(currentPage);
				
				
				if (prevPageEndUutc != -1 && pageList[currentPage].timeStart - prevPageEndUutc >= 0.5*actualPeriod) {
					// Find the length of the gap to within half a period.
					final long pageStartUutc = pageList[currentPage].timeStart;
					if (logger.isTraceEnabled()) {
						logger.trace("{}: Found gap of {} periods between page {} starting at {} and previous page ending at {}", 
								m, 
								(pageStartUutc - prevPageEndUutc)/actualPeriod,
								currentPage, 
								pageStartUutc, 
								prevPageEndUutc);
						logger.trace("{}: A gap of {} periods between page {} start {} and offset {}",
								m,
								(pageStartUutc - offset)/actualPeriod,
								currentPage,
								pageStartUutc, 
								offset);
					}
					final double gapLowerEndpoint = Math.max(prevPageEndUutc, offset);
					final int gapLength = DoubleMath.roundToInt((pageStartUutc - gapLowerEndpoint)/actualPeriod, RoundingMode.HALF_UP);
					int g = 0;
					while (g < gapLength && offset < pageStartUutc && 
							offset < endTime) {
						span.addSample(null);
						nullsAdded++;
						outIndex++;
						g++;
						offset = startTime + samplingPeriod * outIndex;
					}
					logger.trace("{}: Added {} nulls in front of page {}", m, g, currentPage);
					
				}
				pos = 0;
				
				// Scan through the rest of this page.
				// Skip through the page until we are at a point where (offset - timeOfEntry) < samplingPeriod
				while (offset > pageList[currentPage].timeStart + pos * actualPeriod && 
						pos < pageList[currentPage].values.length && offset < endTime) {
					pos++;
					inIndex++;
				}
				
				if (pos > 0) {
					//Move back one sample if it is closer to the offset to maintain half a period error.
					final double microsToPos = pageList[currentPage].timeStart + pos * actualPeriod - offset;
					final double microsToPrevPos = offset - pageList[currentPage].timeStart + (pos - 1) * actualPeriod;
					if (microsToPrevPos < microsToPos) {
						pos--;
						inIndex--;
					}
				}
				
				// Output until end or (offset - timeOfEntry) > samplingPeriod
				while (pos < pageList[currentPage].values.length && 
						offset <= pageList[currentPage].timeEnd && 
						inIndex < iw.length() && offset < endTime) {
					
					Integer val = iw.get(inIndex);
					if (val == Integer.MIN_VALUE && minValIsNull)
						val = null;
					span.addSample(val);
					outIndex++;
	//				inIndex++;
					valuesAdded++;
					offset = startTime + samplingPeriod * outIndex;
					// Skip through the page until we are at a point where (offset - timeOfEntry) < samplingPeriod
				if (factor == 1) {
					if (pos < pageList[currentPage].values.length
							&& offset < endTime) {
						pos++;
						inIndex++;
					}
				} else {
					while (offset > pageList[currentPage].timeStart + pos
							* actualPeriod
							&&
							pos < pageList[currentPage].values.length
							&& offset < endTime) {
						pos++;
						inIndex++;
					}
				}
	//				pos++;
				}
				prevPageEndUutc = pageList[currentPage].timeEnd;
			}
			
			// While we still haven't completed things, pad further
			while (offset < endTime) {
				span.addSample(null);
				nullsAdded++;
				outIndex++;
				offset = startTime + samplingPeriod * outIndex;
			}
	
			logger.trace(m + ": Output time: " + (long) outputTime + " and offset: "
					+ (long) offset + ", versus " + endTime);
			logger.trace(m + ": Downsampling resulted in " + valuesAdded
					+ " samples, and " + nullsAdded + " gap entries, for total of "
					+ (valuesAdded + nullsAdded));
	
			logger.trace(m + ": " + Objects.firstNonNull(path, "N/A") + " request is complete with " + results.size()
					+ " spans and " + siz + " pages at " + actualPeriod
					+ " with scale " + scale + ". " + count + " periods and "
					+ valuesAdded + " samples.  Expected "
					+ (int)Math.ceil((endTime - startTime) / samplingPeriod));
	
			return results;
		}

	@Override
	public int getWorkBufferSize() {
		return 0;
	}

}