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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

//import edu.upenn.cis.braintrust.BtUtil;
import edu.upenn.cis.db.mefview.services.TimeSeriesPage;
import edu.upenn.cis.eeg.TimeSeriesChannel;
import edu.upenn.cis.thirdparty.RED;

public class simple_MEFReader {
  protected RandomAccessFile[] infile;
  private FileChannel[] fc;
  public TimeSeriesChannel header;
  String[] filenames;
  protected ByteBuffer bb;
  
  long[] timeIndex = null; 
  long[] offsetIndex = null; 
  long[] sampleIdxIndex = null; 
  int[] blockLengthsIndex = null; 
  byte[] blockFlagsIndex = null;
  
  protected long pos;
  
  static RED redDecoder = new RED();
  
//  final static Logger logger = LoggerFactory.getLogger(simple_MEFReader.class);
//  final static Logger timeLogger = LoggerFactory.getLogger("time." + simple_MEFReader.class.getName());
      
  ByteBuffer buf = null;
  
  /**
   * Constructor
   * @param filename
   * @param channels
   * @throws IOException
   */
  public simple_MEFReader(String[] filename) throws IOException {
      final String M = "Simple_MEFRead(...)";
      this.filenames = filename;
      this.infile = new RandomAccessFile[filename.length];
      this.fc = new FileChannel[filename.length];

//      logger.debug("{}: Creating TimeSeriesFileReader for {}", M, filename);
      
      init();
      //System.out.println(getCompressionScheme());
  }
  
  /**
   * Init method for simple_MEFReader class
   * @throws IOException
   */
  public void init() throws IOException {
    
    
    int i = 0;
    try {
        for (String file: filenames) {
            infile[i] = new RandomAccessFile(file, "r");
            setFileChannel(i, infile[i++].getChannel());
        }
    } catch (FileNotFoundException f) {
        File d = new File(".");
//        logger.error("Cannot find " + filenames[i] + " in current dir: " + d.getCanonicalPath());
        System.err.println("Cannot find " + filenames[i] + " in current dir: " + d.getCanonicalPath());
        f.printStackTrace();
        throw f;
    }
    bb = ByteBuffer.allocate(1024);
    pos = readHeader();
    
    if (!getCompressionScheme().equals("Range Encoded Differences (RED)"))
      throw new IOException("Unsupported compression format: must be RED");
    if (!isLittleEndian())
      throw new IOException("Unsupported endian-ness: must be little-endian");
  }
  
  /**
   * Read the index structures into memory
   * 
   * @throws IOException
   */
   public synchronized void buildIndex() throws IOException {
      
      int count = (int)getNumIndexEntries();
      
      ByteBuffer bbI = ByteBuffer.allocate(count * 3 * (Long.SIZE / 8));
      bbI.order(ByteOrder.LITTLE_ENDIAN);
      
//      logger.debug("There are " + count + " entries in the index...");
      timeIndex = new long[count]; 
      offsetIndex = new long[count]; 
      sampleIdxIndex = new long[count]; 
//    indexLength = count; 
      
      getFileChannel().read(bbI, getMEFHeader().getIndexDataOffset());
      bbI.rewind();
      
      // Read the index data
      for (int i = 0; i < count; i++) {
          
          timeIndex[i] = bbI.getLong();
          offsetIndex[i] = bbI.getLong();
          //Need to read past the sample number index entry
          sampleIdxIndex[i] = bbI.getLong();
      }
      
      
  }
  
  public long[] getTimeIndex() throws IOException{
      if (timeIndex == null)
          buildIndex();
      return timeIndex;
  }
  
  
  public int[] getBlockLengthsIndex() throws IOException {  // WATCH It was supposed to be uint32
		if (timeIndex == null)
			buildIndex();  // For offsetIndex
		if (blockLengthsIndex == null) {
			int count = (int)getNumIndexEntries();
			ByteBuffer bbRED = ByteBuffer.allocate(31);  // Will hold the 1st 31 bytes of every RED block small header
														 // (covers all except block stats & var-length compressed data)
			bbRED.order(ByteOrder.LITTLE_ENDIAN);
			
			blockLengthsIndex = new int[count];
			blockFlagsIndex = new byte[count];
		    int bytesSkip = 4 + 4 + 8 + 4;  // # of bytes inserted at start of each RED block's header before getting to block length 4-byte word
			// Read from block headers
			for (int b = 0; b < count; b++) {
				getFileChannel().read(bbRED,offsetIndex[b]);
				bbRED.rewind();
				blockLengthsIndex[b] = bbRED.getInt(bytesSkip);  // Reads 4 bytes at absolute pos from beginning. WATCH It was supposed to be uint32
	//			bbRED.position(bytesSkip);  // The above doesn't move position
	//			bbRED.getInt(); bbRED.getShort(); // Skip 6 more bytes, here 4+2 but representing 3+3 ...
	//	        blockFlagsIndex[b] = bbRED.get();  // Read 1 byte. WATCH It was supposed to be uint8
				blockFlagsIndex[b] = bbRED.get(bytesSkip+10);
		          // (int)(blockFlags[b] & 0x01) gives the discontinuity bit
			}
		}
		return blockLengthsIndex;
	}
 
  
  
  /** 
   * Returns header of first MEF file of simple_MEFReader object.
   * @return
   * @throws IOException
   */
  public long readHeader() throws IOException {
    
    header = new MefHeader2(fc[0], bb);
    //System.out.println(header.getSamplingFrequency());
    
    //System.out.println(getMEFHeader().getInstitution());
    //System.out.println(getMEFHeader().getNumberOfIndexEntries());
    //System.out.println(getMEFHeader().getSubjectId());
    
    return getMEFHeader().getBytesRead();
}
  
  
  
  private MefHeader2 getMEFHeader() {
    return (MefHeader2)header;
  }
  
  private String getCompressionScheme() {
    return (getMEFHeader().getCompressionAlgorithm());
  }

  private boolean isLittleEndian() {
    return getMEFHeader().getByteOrderCode() == 1;
  }
  
  private void setFileChannel(int inx, FileChannel fc) {
    this.fc[inx] = fc;
  }
  
  public long getNumIndexEntries() {
    return getMEFHeader().getNumberOfIndexEntries();
  }
  
/**
 * Returns file
 * @return
 */
  public FileChannel getFileChannel() {
    return fc[0];
}
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  private synchronized long retrieveTimeEntryFromIndex(int inx) throws IOException {
		
		@SuppressWarnings("unused")
		long size = getFileChannel().size();
		
		buf.rewind();
		int bytes = getFileChannel().read(buf, getMEFHeader().getIndexDataOffset() + 3 * inx * (Long.SIZE / 8));
		while (bytes < Long.SIZE / 8) {
			int b = getFileChannel().read(buf);
			if (b == -1)
				break;
			bytes += b;
		}
		buf.rewind();
		return buf.getLong();
	}
  private long getTimeIndex(int inx) throws IOException {
		//if (lazy)
		//	return retrieveTimeEntryFromIndex(inx);
		
		if (timeIndex == null)
			buildIndex();
		return timeIndex[inx];
	}
  private synchronized long retrieveOffsetEntryFromIndex(int inx) throws IOException {
		buf.rewind();
		int bytes = getFileChannel().read(buf, getMEFHeader().getIndexDataOffset() + 3 * inx * (Long.SIZE / 8) + (Long.SIZE / 8));
		while (bytes < Long.SIZE / 8) {
			int b = getFileChannel().read(buf);
			if (b == -1)
				break;
			bytes += b;
		}
		buf.rewind();
		return buf.getLong();
	}
  private long getOffsetIndex(int inx) throws IOException {
		//if (lazy)
		//	return retrieveOffsetEntryFromIndex(inx);
		
		if (offsetIndex == null)
			buildIndex();

		return offsetIndex[inx];
	}
  private int getOffsetIndexLength() throws IOException {
		return (int)getNumIndexEntries();
	}
  
  	private byte[] mainBuf = new byte[10240];
	private ByteBuffer bb2 = ByteBuffer.wrap(mainBuf);
	private final static Logger logger = LoggerFactory.getLogger(simple_MEFReader.class);
	private final static Logger timeLogger = LoggerFactory.getLogger("time." + simple_MEFReader.class.getName());
	
	
	public synchronized //ArrayList<MEFReader.MEFPage> 
	TimeSeriesPage[] readPages(long pageId, int numPages, boolean doDecode)
	throws IOException {
		
		final String M = "readPages(long, int, boolean)";
		
		if (pageId >= getOffsetIndexLength())
			return new TimeSeriesPage[0];//>();
		
		long startFileOffset = getOffsetIndex((int)pageId);

		// Period in usec
		double period = 1000000. / header.getSamplingFrequency();
		
		int amount;
		if (pageId + numPages >= getNumIndexEntries()) {
			amount = (int)(getMEFHeader().getIndexDataOffset() - getOffsetIndex((int)pageId));
			numPages = (int)(getNumIndexEntries() - pageId);
		} else {
			amount = (int)(getOffsetIndex((int)pageId+numPages) - getOffsetIndex((int)pageId));
		}

		if (amount > mainBuf.length) {
			logger.trace(
					"{}: resizing from bb2 from {} to {}", 
					new Object[] {M, 
					mainBuf.length,
					amount});
			mainBuf = new byte[amount];
			bb2 = ByteBuffer.wrap(mainBuf);
		}
		bb2.order(ByteOrder.LITTLE_ENDIAN);

		bb2.rewind();
		
		long bytes;
		
		long readIn = System.nanoTime();
		
		try {
			bytes = getFileChannel().read(bb2, startFileOffset);
			
			// If there is a file I/O error, try re-reading once more
		} catch (IOException e) {
			infile[0] = new RandomAccessFile(filenames[0], "r");
			setFileChannel(0, infile[0].getChannel());
			bytes = getFileChannel().read(bb2, startFileOffset);
		}
		
		//timeLogger.trace("{}: read {} seconds", M, BtUtil.diffNowThenSeconds(readIn));
		
		if (bytes < amount)
			throw new RuntimeException("Unable to read full amount " + amount + "(" + bytes +")");
		
		bb2.rewind();

		TimeSeriesPage[] results = new TimeSeriesPage[numPages];//ArrayList<MEFReader.MEFPage>(numPages); 
		for (int i = 0; i < numPages; i++) {
			TimeSeriesPage p = new TimeSeriesPage();
//			results.add(p);
			results[i] = p;
	
			p.timeStart = getTimeIndex((int)pageId + i);
			
			long startInx = getOffsetIndex((int)(pageId + i)) - getOffsetIndex((int)pageId);  
			int len;
			if (pageId + i == getNumIndexEntries() - 1) {
				len = (int)(getMEFHeader().getIndexDataOffset() - getOffsetIndex((int)pageId + i));
			} else {
				len = (int)(getOffsetIndex((int)pageId+1+i) - getOffsetIndex((int)pageId+i));
			}

			try {
				if (doDecode) {
					p.values = redDecoder.decode(mainBuf, (int)startInx, (int)(len));//buffer.length);
				} else {
					p.values = new int[(int)(len - startInx)];
					for (int x = (int)startInx; x < (int)len; x++)
						p.values[x] = mainBuf[x];
				}
			} catch (RuntimeException rt) {
				logger.error("Error in reading " + filenames[0] + " page " + pageId + " (time " + getTimeIndex((int)pageId) +")");
				logger.error("Seek to " + startFileOffset + ", read " + amount + ", decode " + (len - startInx));////(pos - startFileOffset));
				rt.printStackTrace();
				throw rt;
			}
	
			// Use # samples to determine end time
			p.timeEnd = (long)(p.timeStart + period * p.values.length);
		}
		return results;
	}

  
}
