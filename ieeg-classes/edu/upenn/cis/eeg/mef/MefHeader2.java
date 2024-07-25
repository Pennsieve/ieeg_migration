/*
 * Portions of this file are...
#
# Copyright 2013, Mayo Foundation, Rochester MN. All rights reserved
# Written by Ben Brinkmann, Matt Stead, Dan Crepeau, and Vince Vasoli
# usage and modification of this source code is governed by the Apache 2.0 license
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#   http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
*/

package edu.upenn.cis.eeg.mef;

import java.beans.Transient;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.upenn.cis.eeg.EegFileUtils;
import edu.upenn.cis.eeg.TimeSeriesChannel;

public class MefHeader2 implements Serializable, TimeSeriesChannel {
	/************* header version & constants *******************/

	public static final int HEADER_MAJOR_VERSION = 2;
	public static final int HEADER_MINOR_VERSION = 0;
	public static final int MEF_HEADER_LENGTH = 1024;
	public static final int DATA_START_OFFSET = MEF_HEADER_LENGTH;
	public static final int UNENCRYPTED_REGION_OFFSET = 0;
	public static final int UNENCRYPTED_REGION_LENGTH = 176;
	public static final int SUBJECT_ENCRYPTION_OFFSET = 176;
	public static final int SUBJECT_ENCRYPTION_LENGTH = 160;
	public static final int SESSION_ENCRYPTION_OFFSET = 352;
	public static final int SESSION_ENCRYPTION_LENGTH = 496; // maintain
																// multiple of
																// 16
	public static final int ENCRYPTION_BLOCK_BITS = 128;
	public static final int ENCRYPTION_BLOCK_BYTES = (ENCRYPTION_BLOCK_BITS / 8);

	/******************** header fields *************************/

	// Begin Unencrypted Block
	public static final int INSTITUTION_OFFSET = 0;
	public static final int INSTITUTION_LENGTH = 64; // $(63)
	public static final int UNENCRYPTED_TEXT_FIELD_OFFSET = 64;
	public static final int UNENCRYPTED_TEXT_FIELD_LENGTH = 64; // $(63)
	public static final int ENCRYPTION_ALGORITHM_OFFSET = 128;
	public static final int ENCRYPTION_ALGORITHM_LENGTH = 32; // $(29)
	public static final int SUBJECT_ENCRYPTION_USED_OFFSET = 160;
	public static final int SUBJECT_ENCRYPTION_USED_LENGTH = 1; // ui1
	public static final int SESSION_ENCRYPTION_USED_OFFSET = 161;
	public static final int SESSION_ENCRYPTION_USED_LENGTH = 1; // ui1
	public static final int DATA_ENCRYPTION_USED_OFFSET = 162;
	public static final int DATA_ENCRYPTION_USED_LENGTH = 1; // ui1
	public static final int BYTE_ORDER_CODE_OFFSET = 163;
	public static final int BYTE_ORDER_CODE_LENGTH = 1; // ui1
	public static final int HEADER_MAJOR_VERSION_OFFSET = 164;
	public static final int HEADER_MAJOR_VERSION_LENGTH = 1; // ui1
	public static final int HEADER_MINOR_VERSION_OFFSET = 165;
	public static final int HEADER_MINOR_VERSION_LENGTH = 1; // ui1
	public static final int HEADER_LENGTH_OFFSET = 166;
	public static final int HEADER_LENGTH_LENGTH = 2; // ui2
	public static final int SESSION_UNIQUE_ID_OFFSET = 168;
	public static final int SESSION_UNIQUE_ID_LENGTH = 8; // ui1
	// End Unencrypted Block

	// Begin Subject Encrypted Block
	public static final int SUBJECT_FIRST_NAME_OFFSET = 176;
	public static final int SUBJECT_FIRST_NAME_LENGTH = 32; // $(31)
	public static final int SUBJECT_SECOND_NAME_OFFSET = 208;
	public static final int SUBJECT_SECOND_NAME_LENGTH = 32; // $(31)
	public static final int SUBJECT_THIRD_NAME_OFFSET = 240;
	public static final int SUBJECT_THIRD_NAME_LENGTH = 32; // $(31)
	public static final int SUBJECT_ID_OFFSET = 272;
	public static final int SUBJECT_ID_LENGTH = 32; // $(31)
	public static final int SESSION_PASSWORD_OFFSET = 304;
	public static final int SESSION_PASSWORD_LENGTH = ENCRYPTION_BLOCK_BYTES; // $(15)
	public static final int SUBJECT_VALIDATION_FIELD_OFFSET = 320;
	public static final int SUBJECT_VALIDATION_FIELD_LENGTH = 16;
	// End Subject Encrypted Block

	// Begin Protected Block
	public static final int PROTECTED_REGION_OFFSET = 336;
	public static final int PROTECTED_REGION_LENGTH = 16;
	// End Protected Block

	// Begin Session Encrypted Block
	public static final int SESSION_VALIDATION_FIELD_OFFSET = 352;
	public static final int SESSION_VALIDATION_FIELD_LENGTH = 16; // ui1
	public static final int NUMBER_OF_SAMPLES_OFFSET = 368;
	public static final int NUMBER_OF_SAMPLES_LENGTH = 8; // ui8
	public static final int CHANNEL_NAME_OFFSET = 376;
	public static final int CHANNEL_NAME_LENGTH = 32; // $(31)
	public static final int RECORDING_START_TIME_OFFSET = 408;
	public static final int RECORDING_START_TIME_LENGTH = 8; // ui8
	public static final int RECORDING_END_TIME_OFFSET = 416;
	public static final int RECORDING_END_TIME_LENGTH = 8; // ui8
	public static final int SAMPLING_FREQUENCY_OFFSET = 424;
	public static final int SAMPLING_FREQUENCY_LENGTH = 8; // sf8
	public static final int LOW_FREQUENCY_FILTER_SETTING_OFFSET = 432;
	public static final int LOW_FREQUENCY_FILTER_SETTING_LENGTH = 8; // sf8
	public static final int HIGH_FREQUENCY_FILTER_SETTING_OFFSET = 440;
	public static final int HIGH_FREQUENCY_FILTER_SETTING_LENGTH = 8; // sf8
	public static final int NOTCH_FILTER_FREQUENCY_OFFSET = 448;
	public static final int NOTCH_FILTER_FREQUENCY_LENGTH = 8; // sf8
	public static final int VOLTAGE_CONVERSION_FACTOR_OFFSET = 456;
	public static final int VOLTAGE_CONVERSION_FACTOR_LENGTH = 8; // sf8
	public static final int ACQUISITION_SYSTEM_OFFSET = 464;
	public static final int ACQUISITION_SYSTEM_LENGTH = 32; // $(31)
	public static final int CHANNEL_COMMENTS_OFFSET = 496;
	public static final int CHANNEL_COMMENTS_LENGTH = 128; // $(127)
	public static final int STUDY_COMMENTS_OFFSET = 624;
	public static final int STUDY_COMMENTS_LENGTH = 128; // $(127)
	public static final int PHYSICAL_CHANNEL_NUMBER_OFFSET = 752;
	public static final int PHYSICAL_CHANNEL_NUMBER_LENGTH = 4; // si4
	public static final int COMPRESSION_ALGORITHM_OFFSET = 756;
	public static final int COMPRESSION_ALGORITHM_LENGTH = 32; // $(31)
	public static final int MAXIMUM_COMPRESSED_BLOCK_SIZE_OFFSET = 788;
	public static final int MAXIMUM_COMPRESSED_BLOCK_SIZE_LENGTH = 4; // ui4
	public static final int MAXIMUM_BLOCK_LENGTH_OFFSET = 792;
	public static final int MAXIMUM_BLOCK_LENGTH_LENGTH = 8; // ui8
	public static final int BLOCK_INTERVAL_OFFSET = 800;
	public static final int BLOCK_INTERVAL_LENGTH = 8; // sf8
	public static final int MAXIMUM_DATA_VALUE_OFFSET = 808;
	public static final int MAXIMUM_DATA_VALUE_LENGTH = 4; // si4
	public static final int MINIMUM_DATA_VALUE_OFFSET = 812;
	public static final int MINIMUM_DATA_VALUE_LENGTH = 4; // si4
	public static final int INDEX_DATA_OFFSET_OFFSET = 816;
	public static final int INDEX_DATA_OFFSET_LENGTH = 8; // ui8
	public static final int NUMBER_OF_INDEX_ENTRIES_OFFSET = 824;
	public static final int NUMBER_OF_INDEX_ENTRIES_LENGTH = 8; // ui8
	public static final int BLOCK_HEADER_LENGTH_OFFSET = 832;
	public static final int BLOCK_HEADER_LENGTH_LENGTH = 2; // ui2
	public static final int UNUSED_HEADER_SPACE_OFFSET = 834;
	public static final int UNUSED_HEADER_SPACE_LENGTH = 190;
	// End Session Encrypted Block
	
	public static final int ANONYMIZED_SUBJECT_NAME_LENGTH = 63; 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String	institution;
	private String	unencryptedTextField;
	private	 String	encryptionAlgorithm;
	private int	subjectEncryptionUsed;
	private int	sessionEncryptionUsed;
	private int	dataEncryptionUsed;
	private int	byteOrderCode;
	private int	headerVersionMajor;
	private int	headerVersionMinor;
	private byte[]	sessionUniqueID = new byte[SESSION_UNIQUE_ID_LENGTH];
	private short	headerLength;
	private String	subjectFirstName;
	private String	subjectSecondName;
	private String	subjectThirdName;
	private String	subjectId;
	private String	sessionPassword;
	private String	subjectValidationField;
	private String	sessionValidationField;
	private byte[] 	protectedRegion = new byte[PROTECTED_REGION_LENGTH];
	private long	numberOfSamples;
	private String	channelName;
	private long	recordingStartTime;
	private long	recordingEndTime;
	private double	samplingFrequency;
	private double	lowFrequencyFilterSetting;
	private double	highFrequencyFilterSetting;
	private double	notchFilterFrequency;
	private double	voltageConversionFactor;
	private String	acquisitionSystem;
	private String	channelComments;
	private String	studyComments;
	private int	physicalChannelNumber;
	private String	compressionAlgorithm;
	private long	maximumCompressedBlockSize;
	private long maximumBlockLength; 
	private long	blockInterval;
	private int maximumDataValue;
	private int minimumDataValue;
	private long	indexDataOffset;
	private long numberOfIndexEntries;
	private short blockHeaderLength;
	
	private float GmtOffset;
	private long discontinuityDataOffset;
	private long numberOfDiscontinuityEntries;
	private byte[] fileUniqueID = new byte[8];
	private String anonymizedSubjectName;
	private byte[] headerCrc = new byte[4];
	///
	
	private static Random random = new Random();
	private int bytesRead = 0;
	
	private String filename;
	
	public MefHeader2() {
//		random = new Random();
		
		// set defaults, some of these can be over-ridden
		byteOrderCode = 1;              // little-endian
		headerVersionMajor = 2;
		headerVersionMinor = 1;
		headerLength = 1024;
		GmtOffset = (float)-6.0;         // CST time zone
		voltageConversionFactor = 1.0;
		lowFrequencyFilterSetting = -1.0;
		highFrequencyFilterSetting = -1.0;
		notchFilterFrequency = -1.0;
		physicalChannelNumber = -1;
		subjectEncryptionUsed = 0;
		sessionEncryptionUsed = 0;
		dataEncryptionUsed = 0;
		recordingStartTime = 0;
		encryptionAlgorithm = "128-bit AES";
		compressionAlgorithm = "Range Encoded Differences (RED)";
		blockHeaderLength = 287;
		
		// default random ID's in case none are ever specified
		generateUniqueFileID();
		generateUniqueSessionID();
	}
	
	public MefHeader2(byte[] data)
			throws IOException {
		this(ByteBuffer.wrap(data));
	
	}
	public MefHeader2(ByteBuffer bb)
				throws IOException {
		bb.order(ByteOrder.LITTLE_ENDIAN);

		bb.rewind();
		
		updateFromBB(bb);
	}
	
	void updateFromBB(ByteBuffer bb) throws IOException {
		byte[] temp = new byte[INSTITUTION_LENGTH];
		bb.get(temp, 0, INSTITUTION_LENGTH);
		bytesRead += INSTITUTION_LENGTH;
		institution = newString(temp);
		temp = new byte[UNENCRYPTED_TEXT_FIELD_LENGTH];
		bb.get(temp, 0, UNENCRYPTED_TEXT_FIELD_LENGTH);
		bytesRead += UNENCRYPTED_TEXT_FIELD_LENGTH;
		unencryptedTextField = newString(temp);
		temp = new byte[ENCRYPTION_ALGORITHM_LENGTH];
		bb.get(temp, 0, ENCRYPTION_ALGORITHM_LENGTH);
		bytesRead += ENCRYPTION_ALGORITHM_LENGTH;
		encryptionAlgorithm = newString(temp);

		subjectEncryptionUsed = bb.get();
		bytesRead++;
		sessionEncryptionUsed = bb.get();
		bytesRead++;
		if (subjectEncryptionUsed != 0 || sessionEncryptionUsed != 0)
			throw new IOException("Unsupported: encrypted data");
		dataEncryptionUsed = bb.get();
		bytesRead++;
		byteOrderCode = bb.get();
		bytesRead++;
		headerVersionMajor = bb.get();
		bytesRead++;
		headerVersionMinor = bb.get();
		bytesRead++;

		if (headerVersionMajor >= 2) {
			headerLength = bb.getShort();
			bytesRead += 2;
			
			bb.get(sessionUniqueID, 0, sessionUniqueID.length);
			bytesRead += sessionUniqueID.length;

		} else {
			bb.get(sessionUniqueID, 0, sessionUniqueID.length);
			bytesRead += sessionUniqueID.length;

			headerLength = bb.getShort();
			bytesRead += 2;
		}
		
		temp = new byte[SUBJECT_FIRST_NAME_LENGTH];
		bb.get(temp, 0, SUBJECT_FIRST_NAME_LENGTH);
		bytesRead += SUBJECT_FIRST_NAME_LENGTH;
		subjectFirstName = newString(temp);
		temp = new byte[SUBJECT_SECOND_NAME_LENGTH];
		bb.get(temp, 0, SUBJECT_SECOND_NAME_LENGTH);
		bytesRead += SUBJECT_SECOND_NAME_LENGTH;
		subjectSecondName = newString(temp);
		temp = new byte[SUBJECT_THIRD_NAME_LENGTH];
		bb.get(temp, 0, SUBJECT_THIRD_NAME_LENGTH);
		bytesRead += SUBJECT_THIRD_NAME_LENGTH;
		subjectThirdName = newString(temp);

		temp = new byte[SUBJECT_ID_LENGTH];
		bb.get(temp, 0, SUBJECT_ID_LENGTH);
		bytesRead += SUBJECT_ID_LENGTH;
		subjectId = newString(temp);
		temp = new byte[SESSION_PASSWORD_LENGTH];
		bb.get(temp, 0, SESSION_PASSWORD_LENGTH);
		bytesRead += SESSION_PASSWORD_LENGTH;
		sessionPassword = newString(temp);
		temp = new byte[SUBJECT_VALIDATION_FIELD_LENGTH];
		bb.get(temp, 0, SUBJECT_VALIDATION_FIELD_LENGTH);
		bytesRead += SUBJECT_VALIDATION_FIELD_LENGTH;
		subjectValidationField = newString(temp);
		
		if (headerVersionMajor >= 2) {
			bb.get(protectedRegion, 0, PROTECTED_REGION_LENGTH);
			bytesRead += PROTECTED_REGION_LENGTH;
			temp = new byte[SESSION_VALIDATION_FIELD_LENGTH];
			bb.get(temp, 0, SESSION_VALIDATION_FIELD_LENGTH);
			bytesRead += SESSION_VALIDATION_FIELD_LENGTH;
			sessionValidationField = newString(temp);
		} else {
			temp = new byte[SESSION_VALIDATION_FIELD_LENGTH];
			bb.get(temp, 0, SESSION_VALIDATION_FIELD_LENGTH);
			bytesRead += SESSION_VALIDATION_FIELD_LENGTH;
			sessionValidationField = newString(temp);
			bb.get(protectedRegion, 0, PROTECTED_REGION_LENGTH);
			bytesRead += PROTECTED_REGION_LENGTH;
		}

		numberOfSamples = bb.getLong();
		bytesRead += 8;
		temp = new byte[CHANNEL_NAME_LENGTH];
		bb.get(temp, 0, CHANNEL_NAME_LENGTH);
		bytesRead += CHANNEL_NAME_LENGTH;
		channelName = newString(temp);

		recordingStartTime = bb.getLong();
		recordingEndTime = bb.getLong();
		samplingFrequency = bb.getDouble();
		lowFrequencyFilterSetting = bb.getDouble();
		highFrequencyFilterSetting = bb.getDouble();
		notchFilterFrequency = bb.getDouble();
		voltageConversionFactor = bb.getDouble();

		temp = new byte[ACQUISITION_SYSTEM_LENGTH];
		bb.get(temp, 0, ACQUISITION_SYSTEM_LENGTH);
		acquisitionSystem = newString(temp);
		temp = new byte[CHANNEL_COMMENTS_LENGTH];
		bb.get(temp, 0, CHANNEL_COMMENTS_LENGTH);
		channelComments = newString(temp);
		temp = new byte[STUDY_COMMENTS_LENGTH];
		bb.get(temp, 0, STUDY_COMMENTS_LENGTH);
		studyComments = newString(temp);

		physicalChannelNumber = bb.getInt();
		temp = new byte[COMPRESSION_ALGORITHM_LENGTH];
		bb.get(temp, 0, COMPRESSION_ALGORITHM_LENGTH);
		compressionAlgorithm = newString(temp);

		maximumCompressedBlockSize = bb.getInt();
		maximumBlockLength = bb.getLong();
		blockInterval = bb.getLong();
		maximumDataValue = bb.getInt();
		minimumDataValue = bb.getInt();
		indexDataOffset = bb.getLong();
		numberOfIndexEntries = bb.getLong();
		blockHeaderLength = bb.getShort();

		GmtOffset = bb.getFloat();
		discontinuityDataOffset = bb.getLong();
		numberOfDiscontinuityEntries = bb.getLong();
		bb.get(fileUniqueID, 0, 8);
		
		temp = new byte[ANONYMIZED_SUBJECT_NAME_LENGTH];
		bb.get(temp, 0, ANONYMIZED_SUBJECT_NAME_LENGTH);
		anonymizedSubjectName = newString(temp);
		
		bb.get(headerCrc, 0, 4);
	}
	
	public String newString(byte[] array) {
		int i = 0;
		for (; i < array.length && array[i] != 0; i++) { }
		return new String(array, 0, i);
	}

	
	public MefHeader2(FileChannel fc, ByteBuffer bb) throws IOException {
		bb.order(ByteOrder.LITTLE_ENDIAN);

		int b = fc.read(bb);
		bb.rewind();

		updateFromBB(bb);
	}

//	@Override
//	public String toString() {
//		StringBuilder ret = new StringBuilder();
//		
//		ret.append("\ninstitution: " + institution);
//		ret.append("\nunencrypted text field: " + getUnencryptedTextField());
//		ret.append("\nencryption algorithm: " + getEncryptionAlgorithm());
//		ret.append("\nsessionUniqueId: " + getSessionUniqueID());
//		ret.append("\nfirst name: " + getSubjectFirstName());
//		ret.append("\nsecond name: " + getSubjectSecondName());
//		ret.append("\nthird name: " + getSubjectThirdName());
//		ret.append("\nid: " + getSubjectId());
//		ret.append("\nsession password: " + getSessionPassword());
//		ret.append("\nsubject validation field: " + getSubjectValidationField());
//		ret.append("\nsession validation field: " + getSessionValidationField());
//		ret.append("\nprotected_region: " + protectedRegion);
//		ret.append("\nchannel name: " + getChannelName());
//		ret.append("\nacquisition system: " + getAcquisitionSystem());
//		ret.append("\nchannel comments: " + getChannelComments());
//		ret.append("\nstudy comments: " + getStudyComments());
//		ret.append("\ncompression algorithm: " + getCompressionAlgorithm());
//		
//		return ret.toString();
//	}

	public String toString(){
		return "institution: " + getInstitution() + '\n' +
			   "unencrypted_text_field: " + getUnencryptedTextField() + '\n' +
			   "encryption_algorithm: " + getEncryptionAlgorithm() + '\n' +
			   "subject_encryption_used: " + getSubjectEncryptionUsed() + '\n' +
			   "session_encryption_used: " + getSessionEncryptionUsed() + '\n' +
			   "data_encryption_used: " + getDataEncryptionUsed() + '\n' +
			   "byte_order_code: " + getByteOrderCode() + '\n' +
			   "header_version_major: " + getHeaderVersionMajor() + '\n' +
			   "header_version_minor: " + getHeaderVersionMinor() + '\n' +
			   "session_unique_ID: " + EegFileUtils.fromCString(getSessionUniqueID()) + '\n' +
			   "header_length: " + getHeaderLength() + '\n' +
			   "subject_first_name: " + getSubjectFirstName() + '\n' +
			   "subject_second_name: " + getSubjectSecondName() + '\n' +
			   "seubject_third_name: " + getSubjectThirdName() + '\n' +
			   "subject_id: " + getSubjectId() + '\n' +
			   "session_password: " + getSessionPassword() + '\n' +
			   "number_of_samples: " + getNumberOfSamples() + '\n' +
			   "channel_name: " + getChannelName() + '\n' +
			   "recording_start_time: " + getRecordingStartTime() + '\n' +
			   "recording_end_time: " + getRecordingEndTime() + '\n' +
			   "sampling_frequency: " + getSamplingFrequency() + '\n' +
			   "low_frequency_filter_setting: " + getLowFrequencyFilterSetting() + '\n' +
			   "high_frequency_filter_setting: " + getHighFrequencyFilterSetting() + '\n' +
			   "notch_filter_frequency: " + getNotchFilterFrequency() + '\n' +
			   "voltage_conversion_factor: " + getVoltageConversionFactor() + '\n' +
			   "acquisition_system: " + getAcquisitionSystem() + '\n' +
			   "channel_comments: " + getChannelComments() + '\n' +
			   "physical_channel_number: " + getPhysicalChannelNumber() + '\n' +
			   "compression_algorithm: " + getCompressionAlgorithm() + '\n' +
			   "maximum_compressed_block_size: " + getMaximumCompressedBlockSize() + '\n' +
			   "maximum_block_length: " + getMaximumBlockLength() + '\n' +
			   "block_interval: " + getBlockInterval() + '\n' +
			   "maximum_data_value: " + getMaximumDataValue() + '\n' +
			   "minimum_data_value: " + getMinimumDataValue() + '\n' +
			   "index_data_offset: " + getIndexDataOffset() + '\n' +
			   "number_of_index_entries: " + getNumberOfIndexEntries();
			   
	}
	
	public void generateUniqueFileID() {
		fileUniqueID = new byte[8];
		random.nextBytes(fileUniqueID);
	}
	
	public void generateUniqueSessionID() {
		sessionUniqueID = new byte[8];
		random.nextBytes(sessionUniqueID);
	}
	
	@Transient
	@JsonIgnore
	public byte[] get8RandomBytes() {
		byte[] new_bytes = new byte[8];
		random.nextBytes(new_bytes);
		return new_bytes;
	}
    private void CalcCRCchecksum(byte[] checksum, byte[] out_buffer, long num_bytes) {
    	checksum[0] = (byte)0xFF;
    	checksum[1] = (byte)0xFF;
    	checksum[2] = (byte)0xFF;
    	checksum[3] = (byte)0xFF;
    	
    	byte[] new_checksum = new byte[4];
        for (int i=0;i<num_bytes-4;i++){
        	update_crc_32(checksum, out_buffer[i], new_checksum);
            for (int j=0;j<4;j++)
            	checksum[j] = new_checksum[j];
        } 	
    }
    
    private void update_crc_32(byte[] checksum, int c, byte[] new_checksum) {
       	int tmp = checksum[0] ^ (c & 0xFF);
    	if (tmp < 0) tmp += 256;
    	
    	new_checksum[0] = (byte)(checksum[1] ^ ((byte)(crc_tab32[tmp] & 0xFF)));
    	new_checksum[1] = (byte)(checksum[2] ^ ((byte)((crc_tab32[tmp] >> 8) & 0xFF)));
    	new_checksum[2] = (byte)(checksum[3] ^ ((byte)((crc_tab32[tmp] >> 16) & 0xFF)));
    	new_checksum[3] = (byte)((crc_tab32[tmp] >> 24) & 0xFF);
    }  
	
    private void PackString(byte[] buffer, int buffer_beginning, int max_chars, String str) {
    	if (str == null)
    		return;
    	int max_string = str.length();
    	if (str.length() > max_chars)
    		max_string = max_chars;
    	for (int i=0;i<max_string;i++)
    		buffer[buffer_beginning+i] = (byte)(str.charAt(i));
    }
    
    private void PackInt1(byte[] buffer, int buffer_beginning, int the_int) {
    	buffer[buffer_beginning] = (byte)(the_int & 0xFF);
    }
    
    private void PackInt2(byte[] buffer, int buffer_beginning, int the_int) {
    	buffer[buffer_beginning] = (byte)(the_int & 0xFF);
    	buffer[buffer_beginning+1] = (byte)((the_int >> 8) & 0xFF);
    }
    
    private void PackInt4(byte[] buffer, int buffer_beginning, int the_int) {
    	buffer[buffer_beginning] = (byte)(the_int & 0xFF);
    	buffer[buffer_beginning+1] = (byte)((the_int >> 8) & 0xFF);
    	buffer[buffer_beginning+2] = (byte)((the_int >> 16) & 0xFF);
    	buffer[buffer_beginning+3] = (byte)((the_int >> 24) & 0xFF);
    }
    
    private void PackInt8(byte[] buffer, int buffer_beginning, long the_int) {
    	buffer[buffer_beginning] = (byte)(the_int & 0xFF);
    	buffer[buffer_beginning+1] = (byte)((the_int >> 8) & 0xFF);
    	buffer[buffer_beginning+2] = (byte)((the_int >> 16) & 0xFF);
    	buffer[buffer_beginning+3] = (byte)((the_int >> 24) & 0xFF);
    	the_int = the_int >> 32;
    	buffer[buffer_beginning+4] = (byte)(the_int & 0xFF);
    	buffer[buffer_beginning+5] = (byte)((the_int >> 8) & 0xFF);
    	buffer[buffer_beginning+6] = (byte)((the_int >> 16) & 0xFF);
    	buffer[buffer_beginning+7] = (byte)((the_int >> 24) & 0xFF);
    }
    
    private void PackFloat4(byte[] buffer, int buffer_beginning, float the_float) {
    	PackInt4(buffer, buffer_beginning, Float.floatToRawIntBits(the_float));
    }

    private void PackFloat8(byte[] buffer, int buffer_beginning, double the_double) {
    	PackInt8(buffer, buffer_beginning, Double.doubleToRawLongBits(the_double));
    }
    
    private void PackByte8(byte[] buffer, int buffer_beginning, byte[] the_ID) {
    	for (int i=0;i<8;i++)
    		buffer[buffer_beginning+i] = the_ID[i];
    }
    
	public byte[] serialize() {
		// allocate and init buffer
	    byte[] out_data = new byte[1024];
	    for (int i=0;i<1024;i++)
	    	out_data[i] = 0;
	    
	    // Pack header data
	    PackString(out_data, 0, 63, institution);
	    PackString(out_data, 64, 63, unencryptedTextField);
	    PackString(out_data, 128, 31, encryptionAlgorithm);
	    PackInt1(out_data, 160, subjectEncryptionUsed);
	    PackInt1(out_data, 161, sessionEncryptionUsed);
	    PackInt1(out_data, 162, dataEncryptionUsed);
	    PackInt1(out_data, 163, byteOrderCode);
	    PackInt1(out_data, 164, headerVersionMajor);
	    PackInt1(out_data, 165, headerVersionMinor);
	    PackInt2(out_data, 166, headerLength);
	    PackByte8(out_data, 168, sessionUniqueID);
	    PackString(out_data, 176, 31, subjectFirstName);
	    PackString(out_data, 208, 31, subjectSecondName);
	    PackString(out_data, 240, 31, subjectThirdName);
	    PackString(out_data, 272, 31, subjectId);
	    PackInt8(out_data, 368, numberOfSamples);
	    PackString(out_data, 376, 31, channelName);
	    PackInt8(out_data, 408, recordingStartTime);
	    PackInt8(out_data, 416, recordingEndTime);
	    PackFloat8(out_data, 424, samplingFrequency);
	    PackFloat8(out_data, 432, lowFrequencyFilterSetting);
	    PackFloat8(out_data, 440, highFrequencyFilterSetting);
	    PackFloat8(out_data, 448, notchFilterFrequency);
	    PackFloat8(out_data, 456, voltageConversionFactor);
	    PackString(out_data, 464, 31, acquisitionSystem);
	    PackString(out_data, 496, 127, channelComments);
	    PackString(out_data, 624, 127, studyComments);
	    PackInt4(out_data, 752, physicalChannelNumber);
	    PackString(out_data, 756, 31, compressionAlgorithm);
	    PackInt4(out_data, 788, (int)maximumCompressedBlockSize);
	    PackInt8(out_data, 792, maximumBlockLength);
	    PackInt8(out_data, 800, blockInterval);
	    PackInt4(out_data, 808, maximumDataValue);
	    PackInt4(out_data, 812, minimumDataValue);
	    PackInt8(out_data, 816, indexDataOffset);
	    PackInt8(out_data, 824, numberOfIndexEntries);
	    PackInt2(out_data, 832, blockHeaderLength);
	    PackFloat4(out_data, 836, GmtOffset);
	    PackInt8(out_data, 840, discontinuityDataOffset);
	    PackInt8(out_data, 848, numberOfDiscontinuityEntries);
	    PackByte8(out_data, 948, fileUniqueID);
	    PackString(out_data, 956, 63, anonymizedSubjectName);
	    
	    // Pack checksum
        byte[] checksum = new byte[4];
        CalcCRCchecksum(checksum, out_data,1024);
        for (int i=0;i<4;i++)
        	out_data[1020+i] = checksum[i];
	    
		return out_data;
	}
	
	static final long[] crc_tab32 = {0x0, 0x09695c4ca, 0xfb4839c9, 0x6dddfd03, 0x20f3c3cf, 0xb6660705, 0xdbbbfa06, 0x4d2e3ecc, 0x41e7879e, 0xd7724354, 0xbaafbe57, 0x2c3a7a9d, 0x61144451, 0xf781809b, 0x9a5c7d98, 0xcc9b952, 0x83cf0f3c, 0x155acbf6, 0x788736f5, 0xee12f23f, 0xa33cccf3, 0x35a90839, 0x5874f53a, 0xcee131f0, 0xc22888a2, 0x54bd4c68, 0x3960b16b, 0xaff575a1, 0xe2db4b6d, 0x744e8fa7, 0x199372a4, 0x8f06b66e, 0xd1fdae25, 0x47686aef, 0x2ab597ec, 0xbc205326, 0xf10e6dea, 0x679ba920, 0xa465423, 0x9cd390e9, 0x901a29bb, 0x68fed71, 0x6b521072, 0xfdc7d4b8, 0xb0e9ea74, 0x267c2ebe, 0x4ba1d3bd, 0xdd341777, 0x5232a119, 0xc4a765d3, 0xa97a98d0, 0x3fef5c1a, 0x72c162d6, 0xe454a61c, 0x89895b1f, 0x1f1c9fd5, 0x13d52687, 0x8540e24d, 0xe89d1f4e, 0x7e08db84, 0x3326e548, 0xa5b32182, 0xc86edc81, 0x5efb184b, 0x7598ec17, 0xe30d28dd, 0x8ed0d5de, 0x18451114, 0x556b2fd8, 0xc3feeb12, 0xae231611, 0x38b6d2db, 0x347f6b89, 0xa2eaaf43, 0xcf375240, 0x59a2968a, 0x148ca846, 0x82196c8c, 0xefc4918f, 0x79515545, 0xf657e32b, 0x60c227e1, 0xd1fdae2, 0x9b8a1e28, 0xd6a420e4, 0x4031e42e, 0x2dec192d, 0xbb79dde7, 0xb7b064b5, 0x2125a07f, 0x4cf85d7c, 0xda6d99b6, 0x9743a77a, 0x1d663b0, 0x6c0b9eb3, 0xfa9e5a79, 0xa4654232, 0x32f086f8, 0x5f2d7bfb, 0xc9b8bf31, 0x849681fd, 0x12034537, 0x7fdeb834, 0xe94b7cfe, 0xe582c5ac, 0x73170166, 0x1ecafc65, 0x885f38af, 0xc5710663, 0x53e4c2a9, 0x3e393faa, 0xa8acfb60, 0x27aa4d0e, 0xb13f89c4, 0xdce274c7, 0x4a77b00d, 0x7598ec1, 0x91cc4a0b, 0xfc11b708, 0x6a8473c2, 0x664dca90, 0xf0d80e5a, 0x9d05f359, 0xb903793, 0x46be095f, 0xd02bcd95, 0xbdf63096, 0x2b63f45c, 0xeb31d82e, 0x7da41ce4, 0x1079e1e7, 0x86ec252d, 0xcbc21be1, 0x5d57df2b, 0x308a2228, 0xa61fe6e2, 0xaad65fb0, 0x3c439b7a, 0x519e6679, 0xc70ba2b3, 0x8a259c7f, 0x1cb058b5, 0x716da5b6, 0xe7f8617c, 0x68fed712, 0xfe6b13d8, 0x93b6eedb, 0x5232a11, 0x480d14dd, 0xde98d017, 0xb3452d14, 0x25d0e9de, 0x2919508c, 0xbf8c9446, 0xd2516945, 0x44c4ad8f, 0x9ea9343, 0x9f7f5789, 0xf2a2aa8a, 0x64376e40, 0x3acc760b, 0xac59b2c1, 0xc1844fc2, 0x57118b08, 0x1a3fb5c4, 0x8caa710e, 0xe1778c0d, 0x77e248c7, 0x7b2bf195, 
		0xedbe355f, 0x8063c85c, 0x16f60c96, 0x5bd8325a, 0xcd4df690, 0xa0900b93, 0x3605cf59, 0xb9037937, 0x2f96bdfd, 0x424b40fe, 0xd4de8434, 0x99f0baf8, 0xf657e32, 0x62b88331, 0xf42d47fb, 0xf8e4fea9, 0x6e713a63, 0x3acc760, 0x953903aa, 0xd8173d66, 0x4e82f9ac, 0x235f04af, 0xb5cac065, 0x9ea93439, 0x83cf0f3, 0x65e10df0, 0xf374c93a, 0xbe5af7f6, 0x28cf333c, 0x4512ce3f, 0xd3870af5, 0xdf4eb3a7, 0x49db776d, 0x24068a6e, 0xb2934ea4, 0xffbd7068, 0x6928b4a2, 0x4f549a1, 0x92608d6b, 0x1d663b05, 0x8bf3ffcf, 0xe62e02cc, 0x70bbc606, 0x3d95f8ca, 0xab003c00, 0xc6ddc103, 0x504805c9, 0x5c81bc9b, 0xca147851, 0xa7c98552, 0x315c4198, 0x7c727f54, 0xeae7bb9e, 0x873a469d, 0x11af8257, 0x4f549a1c, 0xd9c15ed6, 0xb41ca3d5, 0x2289671f, 0x6fa759d3, 0xf9329d19, 0x94ef601a, 0x27aa4d0, 0xeb31d82, 0x9826d948, 0xf5fb244b, 0x636ee081, 0x2e40de4d, 0xb8d51a87, 0xd508e784, 0x439d234e, 0xcc9b9520, 0x5a0e51ea, 0x37d3ace9, 0xa1466823, 0xec6856ef, 0x7afd9225, 0x17206f26, 0x81b5abec, 0x8d7c12be, 0x1be9d674, 0x76342b77, 0xe0a1efbd, 0xad8fd171, 0x3b1a15bb, 0x56c7e8b8, 0xc0522c72};

	private void writeString(ByteBuffer bb, String str, int length) {
		int pos = 0;
		for (int i = 0; i < institution.length() && i < length; i++) {
			bb.put((byte)institution.charAt(i));
			pos++;
		}
		while (pos < length) {
			bb.put((byte)0);
			pos++;
		}
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getUnencryptedTextField() {
		return unencryptedTextField;
	}

	public void setUnencryptedTextField(String unencryptedTextField) {
		this.unencryptedTextField = unencryptedTextField;
	}

	public String getEncryptionAlgorithm() {
		return encryptionAlgorithm;
	}

	public void setEncryptionAlgorithm(String encryptionAlgorithm) {
		this.encryptionAlgorithm = encryptionAlgorithm;
	}

	public int getSubjectEncryptionUsed() {
		return subjectEncryptionUsed;
	}

	public void setSubjectEncryptionUsed(int subjectEncryptionUsed) {
		this.subjectEncryptionUsed = subjectEncryptionUsed;
	}

	public int getSessionEncryptionUsed() {
		return sessionEncryptionUsed;
	}

	public void setSessionEncryptionUsed(int sessionEncryptionUsed) {
		this.sessionEncryptionUsed = sessionEncryptionUsed;
	}

	public int getDataEncryptionUsed() {
		return dataEncryptionUsed;
	}

	public void setDataEncryptionUsed(int dataEncryptionUsed) {
		this.dataEncryptionUsed = dataEncryptionUsed;
	}

	public int getByteOrderCode() {
		return byteOrderCode;
	}

	public void setByteOrderCode(int byteOrderCode) {
		this.byteOrderCode = byteOrderCode;
	}

	public int getHeaderVersionMajor() {
		return headerVersionMajor;
	}

	public void setHeaderVersionMajor(int headerVersionMajor) {
		this.headerVersionMajor = headerVersionMajor;
	}

	public int getHeaderVersionMinor() {
		return headerVersionMinor;
	}

	public void setHeaderVersionMinor(int headerVersionMinor) {
		this.headerVersionMinor = headerVersionMinor;
	}

	public byte[] getSessionUniqueID() {
		return sessionUniqueID;
	}

	public void setSessionUniqueID(int i, byte sessionUniqueID) {
		this.sessionUniqueID[i] = sessionUniqueID;
	}

	public void setSessionUniqueID(byte []sessionUniqueID) {
		this.sessionUniqueID = sessionUniqueID;
	}

	public short getHeaderLength() {
		return headerLength;
	}

	public void setHeaderLength(short headerLength) {
		this.headerLength = headerLength;
	}

	public String getSubjectFirstName() {
		return subjectFirstName;
	}

	public void setSubjectFirstName(String subjectFirstName) {
		this.subjectFirstName = subjectFirstName;
	}

	public String getSubjectSecondName() {
		return subjectSecondName;
	}

	public void setSubjectSecondName(String subjectSecondName) {
		this.subjectSecondName = subjectSecondName;
	}

	public String getSubjectThirdName() {
		return subjectThirdName;
	}

	public void setSubjectThirdName(String subjectThirdName) {
		this.subjectThirdName = subjectThirdName;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getSessionPassword() {
		return sessionPassword;
	}

	public void setSessionPassword(String sessionPassword) {
		this.sessionPassword = sessionPassword;
	}

	public String getSubjectValidationField() {
		return subjectValidationField;
	}

	public void setSubjectValidationField(String subjectValidationField) {
		this.subjectValidationField = subjectValidationField;
	}

	public String getSessionValidationField() {
		return sessionValidationField;
	}

	public void setSessionValidationField(String sessionValidationField) {
		this.sessionValidationField = sessionValidationField;
	}

	public long getNumberOfSamples() {
		return numberOfSamples;
	}

	public void setNumberOfSamples(long numberOfSamples) {
		this.numberOfSamples = numberOfSamples;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public long getRecordingStartTime() {
		return recordingStartTime;
	}

	public void setRecordingStartTime(long recordingStartTime) {
		this.recordingStartTime = recordingStartTime;
	}

	public long getRecordingEndTime() {
		return recordingEndTime;
	}

	public void setRecordingEndTime(long recordingEndTime) {
		this.recordingEndTime = recordingEndTime;
	}

	public double getSamplingFrequency() {
		return samplingFrequency;
	}

	public void setSamplingFrequency(double samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}

	public double getLowFrequencyFilterSetting() {
		return lowFrequencyFilterSetting;
	}

	public void setLowFrequencyFilterSetting(double lowFrequencyFilterSetting) {
		this.lowFrequencyFilterSetting = lowFrequencyFilterSetting;
	}

	public double getHighFrequencyFilterSetting() {
		return highFrequencyFilterSetting;
	}

	public void setHighFrequencyFilterSetting(double highFrequencyFilterSetting) {
		this.highFrequencyFilterSetting = highFrequencyFilterSetting;
	}

	public double getNotchFilterFrequency() {
		return notchFilterFrequency;
	}

	public void setNotchFilterFrequency(double notchFilterFrequency) {
		this.notchFilterFrequency = notchFilterFrequency;
	}

	public double getVoltageConversionFactor() {
		return voltageConversionFactor;
	}

	public void setVoltageConversionFactor(double voltageConversionFactor) {
		this.voltageConversionFactor = voltageConversionFactor;
	}

	public String getAcquisitionSystem() {
		return acquisitionSystem;
	}

	public void setAcquisitionSystem(String acquisitionSystem) {
		this.acquisitionSystem = acquisitionSystem;
	}

	public String getChannelComments() {
		return channelComments;
	}

	public void setChannelComments(String channelComments) {
		this.channelComments = channelComments;
	}

	public String getStudyComments() {
		return studyComments;
	}

	public void setStudyComments(String studyComments) {
		this.studyComments = studyComments;
	}

	public int getPhysicalChannelNumber() {
		return physicalChannelNumber;
	}

	public void setPhysicalChannelNumber(int physicalChannelNumber) {
		this.physicalChannelNumber = physicalChannelNumber;
	}

	public String getCompressionAlgorithm() {
		return compressionAlgorithm;
	}

	public void setCompressionAlgorithm(String compressionAlgorithm) {
		this.compressionAlgorithm = compressionAlgorithm;
	}

	public long getMaximumCompressedBlockSize() {
		return maximumCompressedBlockSize;
	}

	public void setMaximumCompressedBlockSize(long maximumCompressedBlockSize) {
		this.maximumCompressedBlockSize = maximumCompressedBlockSize;
	}

	public long getMaximumBlockLength() {
		return maximumBlockLength;
	}

	public void setMaximumBlockLength(long maximumBlockLength) {
		this.maximumBlockLength = maximumBlockLength;
	}

	public long getBlockInterval() {
		return blockInterval;
	}

	public void setBlockInterval(long blockInterval) {
		this.blockInterval = blockInterval;
	}

	public int getMaximumDataValue() {
		return maximumDataValue;
	}

	public void setMaximumDataValue(int maximumDataValue) {
		this.maximumDataValue = maximumDataValue;
	}

	public int getMinimumDataValue() {
		return minimumDataValue;
	}

	public void setMinimumDataValue(int minimumDataValue) {
		this.minimumDataValue = minimumDataValue;
	}

	public long getIndexDataOffset() {
		return indexDataOffset;
	}

	public void setIndexDataOffset(long indexDataOffset) {
		this.indexDataOffset = indexDataOffset;
	}

	public long getNumberOfIndexEntries() {
		return numberOfIndexEntries;
	}

	public void setNumberOfIndexEntries(long numberOfIndexEntries) {
		this.numberOfIndexEntries = numberOfIndexEntries;
	}

	public short getBlockHeaderLength() {
		return blockHeaderLength;
	}

	public void setBlockHeaderLength(short blockHeaderLength) {
		this.blockHeaderLength = blockHeaderLength;
	}

	public float getGmtOffset() {
		return GmtOffset;
	}

	public void setGmtOffset(float gmtOffset) {
		GmtOffset = gmtOffset;
	}

	public long getDiscontinuityDataOffset() {
		return discontinuityDataOffset;
	}

	public void setDiscontinuityDataOffset(long discontinuityDataOffset) {
		this.discontinuityDataOffset = discontinuityDataOffset;
	}

	public long getNumberOfDiscontinuityEntries() {
		return numberOfDiscontinuityEntries;
	}

	public void setNumberOfDiscontinuityEntries(long numberOfDiscontinuityEntries) {
		this.numberOfDiscontinuityEntries = numberOfDiscontinuityEntries;
	}

	public byte[] getFileUniqueID() {
		return fileUniqueID;
	}

	public void setFileUniqueID(byte[] fileUniqueID) {
		this.fileUniqueID = fileUniqueID;
	}

	public String getAnonymizedSubjectName() {
		return anonymizedSubjectName;
	}

	public void setAnonymizedSubjectName(String anonymizedSubjectName) {
		this.anonymizedSubjectName = anonymizedSubjectName;
	}

	public byte[] getHeaderCrc() {
		return headerCrc;
	}

	public void setHeaderCrc(byte[] headerCrc) {
		this.headerCrc = headerCrc;
	}

	@Transient
	public static long[] getCrcTab32() {
		return crc_tab32;
	}

	@Transient
	@JsonIgnore
	@Override
	public double getVoltageOffset() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Transient
	@JsonIgnore
	public int getBytesRead() {
		return bytesRead;
	}

	@Transient
	@JsonIgnore
	public long getIndexDataOffsetEnd() {
		return getIndexDataOffset() + (getNumberOfIndexEntries() * 24) - 1;
	}
	
	@Transient
	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String f) {
		filename = f;
	}
}
