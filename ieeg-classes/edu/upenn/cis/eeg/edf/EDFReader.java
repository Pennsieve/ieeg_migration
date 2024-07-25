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

import static edu.upenn.cis.eeg.edf.EDFConstants.DATA_FORMAT_VERSION_SIZE;
import static edu.upenn.cis.eeg.edf.EDFConstants.DIGITAL_MAX_SIZE;
import static edu.upenn.cis.eeg.edf.EDFConstants.DIGITAL_MIN_SIZE;
import static edu.upenn.cis.eeg.edf.EDFConstants.DURATION_DATA_RECORDS_SIZE;
import static edu.upenn.cis.eeg.edf.EDFConstants.HEADER_SIZE;
import static edu.upenn.cis.eeg.edf.EDFConstants.HEADER_SIZE_PER_CHANNEL;
import static edu.upenn.cis.eeg.edf.EDFConstants.HEADER_SIZE_RECORDING_INFO;
import static edu.upenn.cis.eeg.edf.EDFConstants.IDENTIFICATION_CODE_SIZE;
import static edu.upenn.cis.eeg.edf.EDFConstants.LABEL_OF_CHANNEL_SIZE;
import static edu.upenn.cis.eeg.edf.EDFConstants.LOCAL_REOCRDING_IDENTIFICATION_SIZE;
import static edu.upenn.cis.eeg.edf.EDFConstants.LOCAL_SUBJECT_IDENTIFICATION_SIZE;
import static edu.upenn.cis.eeg.edf.EDFConstants.NUMBER_OF_CHANELS_SIZE;
import static edu.upenn.cis.eeg.edf.EDFConstants.NUMBER_OF_DATA_RECORDS_SIZE;
import static edu.upenn.cis.eeg.edf.EDFConstants.NUMBER_OF_SAMPLES_SIZE;
import static edu.upenn.cis.eeg.edf.EDFConstants.PHYSICAL_DIMENSION_OF_CHANNEL_SIZE;
import static edu.upenn.cis.eeg.edf.EDFConstants.PHYSICAL_MAX_IN_UNITS_SIZE;
import static edu.upenn.cis.eeg.edf.EDFConstants.PHYSICAL_MIN_IN_UNITS_SIZE;
import static edu.upenn.cis.eeg.edf.EDFConstants.PREFILTERING_SIZE;
import static edu.upenn.cis.eeg.edf.EDFConstants.START_DATE_SIZE;
import static edu.upenn.cis.eeg.edf.EDFConstants.START_TIME_SIZE;
import static edu.upenn.cis.eeg.edf.EDFConstants.TRANSDUCER_TYPE_SIZE;
import static edu.upenn.cis.eeg.edf.ParseUtils.readASCIIFromStream;
import static edu.upenn.cis.eeg.edf.ParseUtils.readBulkASCIIFromStream;
import static edu.upenn.cis.eeg.edf.ParseUtils.readBulkDoubleFromStream;
import static edu.upenn.cis.eeg.edf.ParseUtils.readBulkIntFromStream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import edu.upenn.cis.eeg.edf.EDFHeader.Builder;


public class EDFReader {

  private String filename;
  private EDFHeader header;


  private int dataOffset;
  private int blockSize;
  private InputStream is;
  private RandomAccessFile infile;
  boolean isClosed;

  /**
   * Creates a EDFReader object.
   * @param filename
   * @throws FileNotFoundException 
   * @throws EDFParserException 
   */
  public EDFReader(String filename) throws FileNotFoundException, EDFParserException {
    this.filename = filename;

    is = new FileInputStream(filename);
    infile = new RandomAccessFile(filename, "r");
    header = readHeader();

  }


  /**
   * Parse the InputStream which should be at the start of an EDF-File. The
   * method returns an object containing the complete header of the EDF-File
   * 
   * @param is
   *            the InputStream to the EDF-File
   * @return the parsed result
   * @throws EDFParserException
   */
  public EDFHeader readHeader() throws EDFParserException
  {
    if(isClosed){
      throw new EDFParserException("Cannot read from EDF-File, because it is already closed.");
    }
    try
    {
//      EDFParserResult result = new EDFParserResult();
//      result.header = header;

      String idCode = readASCIIFromStream(is, IDENTIFICATION_CODE_SIZE);
      if (!idCode.trim().equals("0")) {
        throw new EDFParserException();
      }
      
      //Get Global information
      String subjectID = readASCIIFromStream(is, LOCAL_SUBJECT_IDENTIFICATION_SIZE);
      String recordingID = readASCIIFromStream(is, LOCAL_REOCRDING_IDENTIFICATION_SIZE);
      String startDate = readASCIIFromStream(is, START_DATE_SIZE);
      String startTime = readASCIIFromStream(is, START_TIME_SIZE);
      
      Date recordingStartDate;
      try {
        final SimpleDateFormat recordingStartDateFormat = new SimpleDateFormat("dd.MM.yy HH.mm.ss");
        recordingStartDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		recordingStartDate = recordingStartDateFormat.parse(startDate + " " + startTime);
      } catch (ParseException e) {
        throw new EDFParserException(e);
      }
      
      Builder header = new EDFHeader.Builder(recordingStartDate);
      header.setSubjectID(subjectID);
      header.setRecordingID(recordingID);
      
      
      int tmp = Integer.parseInt(readASCIIFromStream(is, HEADER_SIZE).trim()); //Automatically re-created during build()
      header.setFormatVersion(readASCIIFromStream(is, DATA_FORMAT_VERSION_SIZE));
      header.setNumberOfRecords(Integer.parseInt(readASCIIFromStream(is, NUMBER_OF_DATA_RECORDS_SIZE).trim()));
      header.setDurationOfRecords(Double.parseDouble(readASCIIFromStream(is, DURATION_DATA_RECORDS_SIZE).trim()));
 
      int numberOfChannels = Integer.parseInt(readASCIIFromStream(is, NUMBER_OF_CHANELS_SIZE).trim());
      ArrayList<String> channelLabels = readBulkASCIIFromStream(is, LABEL_OF_CHANNEL_SIZE, numberOfChannels);
      ArrayList<String> transducerTypes = readBulkASCIIFromStream(is, TRANSDUCER_TYPE_SIZE, numberOfChannels);
      ArrayList<String> dimensions = readBulkASCIIFromStream(is, PHYSICAL_DIMENSION_OF_CHANNEL_SIZE, numberOfChannels);
      ArrayList<Double> minInUnits = readBulkDoubleFromStream(is, PHYSICAL_MIN_IN_UNITS_SIZE, numberOfChannels);
      ArrayList<Double> maxInUnits = readBulkDoubleFromStream(is, PHYSICAL_MAX_IN_UNITS_SIZE, numberOfChannels);
      ArrayList<Integer> digitalMin = readBulkIntFromStream(is, DIGITAL_MIN_SIZE, numberOfChannels);
      ArrayList<Integer> digitalMax = readBulkIntFromStream(is, DIGITAL_MAX_SIZE, numberOfChannels);
      ArrayList<String> prefilterings = readBulkASCIIFromStream(is, PREFILTERING_SIZE, numberOfChannels);
      ArrayList<Integer> numberOfSamples = readBulkIntFromStream(is, NUMBER_OF_SAMPLES_SIZE, numberOfChannels);
      
      for(int i=0; i<numberOfChannels; i++){
        if (channelLabels.get(i) == "EDF Annotations"){
          header.addAnnotationChannel(numberOfSamples.get(i));
        } else{
        header.addChannel(channelLabels.get(i), transducerTypes.get(i), dimensions.get(i), 
            minInUnits.get(i), maxInUnits.get(i), digitalMin.get(i), digitalMax.get(i),
            prefilterings.get(i), numberOfSamples.get(i));
        }
      }
    
      // Set DataOffset
      dataOffset = HEADER_SIZE_RECORDING_INFO + HEADER_SIZE_PER_CHANNEL * numberOfChannels;
      
      // Set BlockSize
      blockSize = 0;
      for (int nos : numberOfSamples)
      {
        blockSize += nos;
      }    
      
      return header.build();
    } catch (IOException e)
    {
      throw new EDFParserException(e);
    }
  }

/**
  * Reads n number of EDF blocks from a file and returns a EDFSignal object
  * 
  * @param startBlock
  * @param numBlocks
  * @return
  * @throws EDFParserException
  */
  public EDFSignal readSignal(int startBlock, int numBlocks) throws EDFParserException
  {
    if(isClosed){
      throw new EDFParserException("Cannot read from EDF-File, because it is already closed.");
    }

    try
    {

      EDFSignal signal = new EDFSignal();

      signal.unitsInDigit = new Double[header.numberOfChannels];
      for (int i = 0; i < signal.unitsInDigit.length; i++)
        signal.unitsInDigit[i] = (header.maxInUnits[i] - header.minInUnits[i])
        / (header.digitalMax[i] - header.digitalMin[i]);

      signal.digitalValues = new short[header.numberOfChannels][];
      //        signal.valuesInUnits = new double[header.numberOfChannels][];
      for (int i = 0; i < header.numberOfChannels; i++)
      {
        signal.digitalValues[i] = new short[numBlocks * header.numberOfSamples[i]];
        //            signal.valuesInUnits[i] = new double[header.numberOfRecords * header.numberOfSamples[i]];
      }

      int blockOffset = dataOffset + startBlock * blockSize; 
      infile.seek(blockOffset);
      
      
      for ( int i = 0; i < numBlocks; i++)
      {
        for ( int j = 0; j < header.numberOfChannels; j++)
          for ( int k = 0; k < header.numberOfSamples[j]; k++)

          {
            int s = header.numberOfSamples[j] * i + k;
            signal.digitalValues[j][s] = infile.readShort();
            //                    signal.valuesInUnits[j][s] = signal.digitalValues[j][s] * signal.unitsInDigit[j];
          }
      }
      
      

      return signal;
    } catch (IOException e)
    {
      throw new EDFParserException(e);
    }
  }

  public void close() throws IOException
  {
    is.close();
    infile.close();
    isClosed = true;

  }
  
  /**
   * ParseAnnotation finds the annotation channel in the EDFSignal object and parses
   * the annotations from the channel. It then removes the Annotation channel from the 
   * EDFSignal object.
   * 
   * @param header
   * @param signal
   * @return
   */
  private static List<EDFAnnotation> parseAnnotation(EDFHeader header, EDFSignal signal, int index)
  {

      if (!header.formatVersion.startsWith("EDF+"))
          return null;

      // Check if the object has an annotation channel.
      Integer[] annotationIndex = header.findAnnotationChannels();
      if (annotationIndex == null)
        return null;
      
      
      short[] s = signal.digitalValues[annotationIndex[index]];
      byte[] b = new byte[s.length * 2];
      for (int i = 0; i < s.length * 2; i += 2)
      {
          b[i] = (byte) (s[i / 2] % 256);
          b[i + 1] = (byte) (s[i / 2] / 256 % 256);
      }

//      removeAnnotationSignal(header, signal, annotationIndex);

      return parseAnnotations(b);

  }
  
  private static List<EDFAnnotation> parseAnnotations(byte[] b)
  {
      List<EDFAnnotation> annotations = new ArrayList<EDFAnnotation>();
      int onSetIndex = 0;
      int durationIndex = -1;
      int annotationIndex = -2;
      int endIndex = -3;
      for (int i = 0; i < b.length - 1; i++)
      {
          if (b[i] == 21)
          {
              durationIndex = i;
              continue;
          }
          if (b[i] == 20 && onSetIndex > annotationIndex)
          {
              annotationIndex = i;
              continue;
          }
          if (b[i] == 20 && b[i + 1] == 0)
          {
              endIndex = i;
              continue;
          }
          if (b[i] != 0 && onSetIndex < endIndex)
          {

              String onSet = null;
              String duration = null;
              if (durationIndex > onSetIndex)
              {
                  onSet = new String(b, onSetIndex, durationIndex - onSetIndex);
                  duration = new String(b, durationIndex, annotationIndex - durationIndex);
              } else
              {
                  onSet = new String(b, onSetIndex, annotationIndex - onSetIndex);
                  duration = "";
              }
              String annotation = new String(b, annotationIndex, endIndex - annotationIndex);
              annotations.add(new EDFAnnotation(onSet, duration, annotation.split("[\u0014]")));
              onSetIndex = i;
          }
      }
      return annotations;
  }

//  private static void removeAnnotationSignal(EDFHeader header, EDFSignal signal, int annotationIndex)
//  {
//      header.numberOfChannels--;
//      removeElement(header.channelLabels, annotationIndex);
//      removeElement(header.transducerTypes, annotationIndex);
//      removeElement(header.dimensions, annotationIndex);
//      removeElement(header.minInUnits, annotationIndex);
//      removeElement(header.maxInUnits, annotationIndex);
//      removeElement(header.digitalMin, annotationIndex);
//      removeElement(header.digitalMax, annotationIndex);
//      removeElement(header.prefilterings, annotationIndex);
//      removeElement(header.numberOfSamples, annotationIndex);
//      removeElement(header.reserveds, annotationIndex);
//
//      removeElement(signal.digitalValues, annotationIndex);
//      removeElement(signal.unitsInDigit, annotationIndex);
////    removeElement(signal.valuesInUnits, annotationIndex);
//  }
  
  
  public EDFHeader getHeader() {
    return header;
  }

}
