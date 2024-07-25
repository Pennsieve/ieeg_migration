/*
 * Copyright 2013, Mayo Foundation, Rochester MN. All rights reserved
 * Written by Ben Brinkmann, Matt Stead, Dan Crepeau, Vince Vasoli, and
 * Mark Bower
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
package edu.upenn.cis.thirdparty;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.annotation.Nullable;

import com.google.common.io.ByteStreams;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.primitives.Ints;

import edu.upenn.cis.db.mefview.services.TimeSeriesPage;

//import EphysIO.Codec.RED.HeaderOfRED;

public class RED {
	private HeaderOfRED header;
	public final static int BLOCK_HEADER_BYTES = 287;
	public final static long CARRY_CHECK = 0x7F800000;
	public final static long BOTTOM_VALUE = 0x800000;
	public final static long BOTTOM_VALUE_M_1 = 0x7FFFFF;
	public final static long SHIFT_BITS = 23;
	public final static long EXTRA_BITS = 7;
	public final static int FILLER_BYTE = 0x55;
	long range;
	long low_bound;
	int[] diff_buffer = new int[120000];
	int[] in_buffer = new int[51200];
	long[] cnts = new long[256];
	long[] cum_cnts = new long[257];
	byte[] encrypted_bytes = new byte[16];
	int[] output_int = new int[16];

	int currentSizeOfByteArray;
	int buffer_p;
	int bytes_p;
	int in_byte;

	// boolean printAllFlag = false;
	// PrintWriter pw;

	public RED() {
		// cipher = new AES_C();
		header = new HeaderOfRED();
	}

	// private int[] key() {
	// return expandedKey;
	// }
	//
	// public void key( String k ) {
	// key = new int[8];
	// bufToIntArray( ByteBuffer.wrap(k.getBytes()), key );
	// cipher.initialize( k );
	// expandedKey = cipher.getRoundKey();
	// }

	// public void bufToIntArray( ByteBuffer buf, int[] intArray ) {
	// byte[] byteBuf = buf.array();
	// int L = byteBuf.length;
	// for ( int z=0; z<L; z++) {
	// intArray[z] = (int) (byteBuf[z]);// & 0xFF);
	// }
	// byteBuf = null;
	// }

	public int[] decode(byte[] raw_bytes, int size) {
		// header = new HeaderOfRED();

		return decode(raw_bytes, 0, size);
	}

	public int[] decode(byte[] raw_bytes, int ptr, int size) {
		// int[] diff_buffer = new int[50000];
		// in_buffer = new int[raw_bytes.length];
		// for ( int z=0; z<in_buffer.length; z++ )
		// in_buffer[z] = (int)raw_bytes[z];
		// if (size + ptr > header.compressed_bytes || size == 0)
		if (size == 0)
			size = raw_bytes.length - ptr;
		int[] out_buffer = decompress_block(raw_bytes, ptr, size, null, false);// ,header);
		// diff_buffer = null;
		// in_buffer = null;
		return out_buffer;
	}

	private int dec_normalize(int in_bytes_p) {
		// boolean beyondLimitFlag = false;
		while (range <= BOTTOM_VALUE) {
			// if ( printAllFlag ) {
			// pw.println( in_byte+" "+low_bound+" "+range );
			// }
			low_bound = (low_bound << 8) | ((in_byte << EXTRA_BITS) & 0xFF);
			// if ( in_bytes_p >= currentSizeOfByteArray ) {
			// if ( beyondLimitFlag )
			// System.out.println("RED :: dec_normalize :: in_bytes_p too big.");
			// else
			// beyondLimitFlag = true;
			// }
			in_byte = in_buffer[in_bytes_p];
			in_bytes_p++;
			low_bound |= (in_byte >> (8 - EXTRA_BITS));
			range <<= 8;
		}
		return in_bytes_p;
	}

	public static int[] decompress_block(ByteBuffer bytes, int ptr,
			int bytesSize, int[] externalKey, boolean validate_CRC,
			long cnts[], long cum_cnts[], int diff_buffer[]) {// , HeaderOfRED
																// header) {
		/*
		 * The size of diff_buffer caused more than a bit of grief. Originally,
		 * I set this size to 50k, which worked 99% of the time, but choked on
		 * that annoying 1% of runs containing a few more than 50k diffs.
		 * Because I did not know the size of diff_cnts ahead of time, I tried
		 * checking the size on each run and increasing the size of diff_buffer
		 * when needed. This slowed the code by a factor THREE! Clearly, the
		 * compiler optimizes fixed array sizes! Moving to Collections will help
		 * this considerably.
		 */
		// currentSizeOfByteArray = bytesSize;

		bytes.position(ptr);

		long checksum;
		long checksum_read = bytes.getInt() & 0xffffffffL;
		long comp_block_len = bytes.getInt() & 0xffffffffL;
		long time_value = bytes.getLong();
		long diff_cnts = bytes.getInt() & 0xffffffffL;
		long block_len = bytes.getInt() & 0xffffffffL;
		if ((int) block_len < 0) {
			throw new RuntimeException("Illegal block length size: "
					+ (int) block_len);
		}
		int[] out_buffer = new int[(int) block_len];

		int max_data_value = bytes.getInt();
		int min_data_value = bytes.getInt();
		byte discontinuity = bytes.get();

		// TODO: fix this to *copy* the header before doing a CRC on it!!!
		// if ( validate_CRC ) {
		// CRC32 crc = new CRC32();
		//
		// byte[] byteBuf = new byte[(int)comp_block_len + BLOCK_HEADER_BYTES -
		// 4];
		// for ( int i=4; i < (comp_block_len + BLOCK_HEADER_BYTES); i++ ) {
		// byteBuf[i - 4] = (byte)out_buffer[i];
		// }
		// crc.update(byteBuf);
		// checksum = (int)crc.getValue();
		// if ( checksum != checksum_read ) {
		// //header.CRC_validated( false );
		// return null;
		// }
		// }

		for (int i = 0; i < 256; i++) {
			cnts[i] = bytes.get() & 0xffL;// getUI1();
		}
/*
 * if ( header != null) { header.CRC_32().update_crc_32( checksum_read, 0 );
 * header.block_start_time( time_value ); header.compressed_bytes(
 * comp_block_len ); header.difference_count( diff_cnts ); header.sample_count(
 * block_len ); header.max_value( max_data_value ); header.min_value(
 * min_data_value ); header.discontinuity( discontinuity ); }
 */
		/*** generate statistics ***/
		cum_cnts[0] = 0;
		for (int i = 0; i < 256; ++i)
			cum_cnts[i + 1] = cnts[i] + cum_cnts[i];
		long scaled_tot_cnts = cum_cnts[256];

		/*** range decode ***/
		diff_buffer[0] = -128;
		int db_p = 1;
		++diff_cnts; // initial -128 not coded in encode (low frequency symbol)
		// int ib_p = BLOCK_HEADER_BYTES + 1; // skip initial dummy byte from
		// encode

		bytes.position(BLOCK_HEADER_BYTES + 1); // skip initial dummy byte from
												// encode
		// in_byte = in_buffer[ ib_p++ ];
		int in_byte = bytes.get() & 0xff;

		long low_bound = in_byte >> (8 - EXTRA_BITS);
		long range = 1 << EXTRA_BITS;
		// dec_initialize( range, low_bound, in_buffer );
		int ob_p = 0;
		for (long i = diff_cnts; i > 0; i--) {
			// if ( i==1L )
			// System.out.println("stop");
			// printf("in %u %u\t", range, low_bound);

			while (range <= BOTTOM_VALUE) {
				low_bound = (low_bound << 8) | ((in_byte << EXTRA_BITS) & 0xFF);

				in_byte = bytes.get() & 0xff;
				low_bound |= (in_byte >> (8 - EXTRA_BITS));
				range <<= 8;
			}
			// printf("out %u %u\n", range, low_bound);
			long range_per_cnt = range / scaled_tot_cnts;
			long tmp = low_bound / range_per_cnt;
			long cc = (tmp >= scaled_tot_cnts ? (scaled_tot_cnts - 1) : tmp);

			int symbol;
			if (cc > cum_cnts[128]) {
				for (symbol = 255; cum_cnts[symbol] > cc; symbol--)
					;
			} else {
				for (symbol = 1; cum_cnts[symbol] <= cc; symbol++)
					;
				--symbol;
			}
			tmp = range_per_cnt * cum_cnts[symbol];
			low_bound -= tmp;
			if (symbol < 255)
				range = range_per_cnt * cnts[symbol];
			else {
				range -= tmp;
				// System.out.println( db_p );
			}
			// Special case, because Java has no unsigned variables, 128 equals
			// 128.
			// There is no "cast" that will convert 128 to -128, like C's
			// casting to si1.
			// Problem is, when constructing a new number after a break, you may
			// want the
			// value +128. So, you either convert all +128 to -128 here and
			// unroll it later,
			// or determine at this time whether you want the actual value or
			// the "flag", -128.
			// I chose to "roll" it here on each occassion, and "unroll" it
			// later when needed.
			// This must be "unrolled" a few lines down.
			if (symbol == 128)
				diff_buffer[db_p++] = -128;
			else
				diff_buffer[db_p++] = symbol;
		}
		while (range <= BOTTOM_VALUE) {
			low_bound = (low_bound << 8) | ((in_byte << EXTRA_BITS) & 0xFF);

			in_byte = bytes.get() & 0xff;
			low_bound |= (in_byte >> (8 - EXTRA_BITS));
			range <<= 8;
		}
		// printf("end %u %u\n", range, low_bound);
		/*** generate output data from differences ***/
		db_p = 0;
		ob_p = 0;
		int current_val = 0;
		for (long i = block_len; i > 0; i--) {
			// if (diff_buffer[db_p] == -128 || diff_buffer[db_p] == 255 ) { //
			// assumes little endian input
			if (diff_buffer[db_p] == -128) { // assumes little endian input
				// Here is the "unrolling". A "value" of -128 must be
				// interpreted as +128.
				if (diff_buffer[++db_p] == -128)
					diff_buffer[db_p] = 128;
				current_val = diff_buffer[db_p];
				if (diff_buffer[++db_p] == -128)
					diff_buffer[db_p] = 128;
				current_val += diff_buffer[db_p] << 8;
				if (diff_buffer[++db_p] == -128)
					diff_buffer[db_p] = 128;
				current_val += diff_buffer[db_p] << 16;
				if (diff_buffer[db_p++] > 127)
					current_val += -1 << 24;
				if (current_val > max_data_value
						|| current_val < min_data_value)
					// System.out.println("decoded value out of range.");
					throw new RuntimeException("decoded value out of range.");
			} else {
				current_val += (byte) diff_buffer[db_p++];
				if (current_val > max_data_value
						|| current_val < min_data_value)
					// System.out.println
					throw new RuntimeException("decoded value out of range: "
							+ current_val + "\tRange: " + min_data_value + " "
							+ max_data_value);
			}
			out_buffer[ob_p] = current_val;
			ob_p++;
		}
		return out_buffer;
	}

	public synchronized int[] decompress_block(byte[] bytes, int ptr,
			int bytesSize, @Nullable int[] externalKey, boolean validate_CRC) {// ,
		// HeaderOfRED
		// header)
		// {
		/*
		 * The size of diff_buffer caused more than a bit of grief. Originally,
		 * I set this size to 50k, which worked 99% of the time, but choked on
		 * that annoying 1% of runs containing a few more than 50k diffs.
		 * Because I did not know the size of diff_cnts ahead of time, I tried
		 * checking the size on each run and increasing the size of diff_buffer
		 * when needed. This slowed the code by a factor THREE! Clearly, the
		 * compiler optimizes fixed array sizes! Moving to Collections will help
		 * this considerably.
		 * 
		 * Another problem I found is that a new Codec is being created for each
		 * thread, rather than building a "library" and checking out Codecs as
		 * needed. Because the codec is generated in the DirectorBuilder,
		 * establish the library there.
		 */
		currentSizeOfByteArray = bytesSize;
		if (in_buffer == null)
			in_buffer = new int[2 * bytesSize];
		if (in_buffer.length < 2 * bytesSize)
			in_buffer = new int[2 * bytesSize];

		bytes_p = ptr;
		buffer_p = 0;
		if (bytes_p + bytesSize > bytes.length) {
			throw new RuntimeException(
					"Asking for too many bytes from input buffer! "
							+ bytes.length + " available, " + bytes_p
							+ " used (vs " + ptr + "), " + bytesSize
							+ " requested");
		}
		if (bytesSize > in_buffer.length) {
			throw new RuntimeException("Overflowing work buffer!");
		}

		for (int z = 0; z < 36; z++)
			in_buffer[z] = (int) bytes[bytes_p + z];

		//Don't read unused values
		//long checksum;
		skip(4);//long checksum_read = getUI4();
		skip(4);//long comp_block_len = getUI4();
		skip(8);//long time_value = getUI8();
		long diff_cnts = getUI4();
		long block_len = getUI4();
		if ((int) block_len < 0) {
			throw new RuntimeException("Illegal block length size: "
					+ (int) block_len);
		}
		int[] out_buffer = new int[(int) block_len];

		long max_data_value = getSI3();
		long min_data_value = getSI3();
		skip(1);//int discontinuity = getSI1();

		// if ( validate_CRC ) {
		// crc CRC_32 = new crc();
		// if ( out_buffer.length > 0 ) {
		// checksum = 0xFFFFFFFF;
		// for ( int i=4; i < (comp_block_len + BLOCK_HEADER_BYTES); i++ ) {
		// checksum = CRC_32.update_crc_32( checksum, out_buffer[i] );
		// if ( checksum != checksum_read )
		// //header.CRC_validated( false );
		// return null;
		// }
		// }
		// }
		// TODO: fix this to *copy* the header before doing a CRC on it!!!
		// if ( validate_CRC ) {
		// CRC32 crc = new CRC32();
		//
		// byte[] byteBuf = new byte[(int)comp_block_len + BLOCK_HEADER_BYTES -
		// 4];
		// for ( int i=4; i < (comp_block_len + BLOCK_HEADER_BYTES); i++ ) {
		// byteBuf[i - 4] = (byte)out_buffer[i];
		// }
		// crc.update(byteBuf);
		// checksum = (int)crc.getValue();
		// if ( checksum != checksum_read ) {
		// //header.CRC_validated( false );
		// return null;
		// }
		// }

		// if ( key != null ) {
		// in_buffer = cipher.AES_decryptWithKey( in_buffer, key );
		// }

/*
 * if ( key != null ) { for ( int z=buffer_p; z<in_buffer.length; z++)
 * in_buffer[z] = (int) in_buffer[z] & 0xFF; int[] encrypted = new int[16]; for
 * ( int i=0; i<16; i++ ) encrypted[i] = in_buffer[buffer_p+i]; int[]
 * decrypted_tmp = cipher.AES_decryptWithKey( encrypted, expandedKey ); for (
 * int i=0; i<16; i++ ) in_buffer[buffer_p + i] = decrypted_tmp[i]; encrypted =
 * null; decrypted_tmp = null; }
 */
		// bytesSize -= (bytes_p - ptr);
		if (bytes_p + bytesSize > bytes.length) {
			throw new RuntimeException(
					"Asking for too many bytes from input buffer! "
							+ bytes.length + " available, " + bytes_p
							+ " used (vs " + ptr + "), " + bytesSize
							+ " requested");
		}
		if (bytesSize > in_buffer.length) {
			throw new RuntimeException("Overflowing work buffer!");
		}
		for (int z = buffer_p + 0; z < bytesSize; z++)
			in_buffer[z] = (int) bytes[bytes_p + z] & 0xFF;

		for (int i = 0; i < 256; i++) {
			cnts[i] = getUI1();
		}
/*
 * if ( header != null) { header.CRC_32().update_crc_32( checksum_read, 0 );
 * header.block_start_time( time_value ); header.compressed_bytes(
 * comp_block_len ); header.difference_count( diff_cnts ); header.sample_count(
 * block_len ); header.max_value( max_data_value ); header.min_value(
 * min_data_value ); header.discontinuity( discontinuity ); }
 */
		/*** generate statistics ***/
		cum_cnts[0] = 0;
		for (int i = 0; i < 256; ++i)
			cum_cnts[i + 1] = cnts[i] + cum_cnts[i];
		long scaled_tot_cnts = cum_cnts[256];

		/*** range decode ***/
		diff_buffer[0] = -128;
		int db_p = 1;
		++diff_cnts; // initial -128 not coded in encode (low frequency symbol)
		int ib_p = BLOCK_HEADER_BYTES + 1; // skip initial dummy byte from
											// encode
		// Convert the values of in_buffer to all "unsigned" values.
		// for ( int z=ib_p; z<in_buffer.length; z++ ) {
		// in_buffer[z] = (int) in_buffer[z] & 0xFF;
		// }
		in_byte = in_buffer[ib_p++];
		low_bound = in_byte >> (8 - EXTRA_BITS);
		range = 1 << EXTRA_BITS;
		// dec_initialize( range, low_bound, in_buffer );
		int ob_p = 0;
		for (long i = diff_cnts; i > 0; i--) {
			// if ( i==1L )
			// System.out.println("stop");
			// printf("in %u %u\t", range, low_bound);
			ib_p = dec_normalize(ib_p);
			// printf("out %u %u\n", range, low_bound);
			long range_per_cnt = range / scaled_tot_cnts;
			long tmp = low_bound / range_per_cnt;
			long cc = (tmp >= scaled_tot_cnts ? (scaled_tot_cnts - 1) : tmp);
			/*
			 * int symbol = Collections.binarySearch( cum_cnts, cc); // The
			 * polarity of the binarySearch result: // + : item found in the
			 * list : set symbol to preceding index. // - : negative of
			 * insertion index : set symbol to absolute value. symbol = symbol
			 * >= 0 ? (symbol+1) : (Math.abs(symbol)-1); symbol--;
			 */
			int symbol;
			if (cc > cum_cnts[128]) {
				for (symbol = 255; cum_cnts[symbol] > cc; symbol--)
					;
			} else {
				for (symbol = 1; cum_cnts[symbol] <= cc; symbol++)
					;
				--symbol;
			}
			tmp = range_per_cnt * cum_cnts[symbol];
			low_bound -= tmp;
			if (symbol < 255)
				range = range_per_cnt * cnts[symbol];
			else {
				range -= tmp;
				// System.out.println( db_p );
			}
			// Special case, because Java has no unsigned variables, 128 equals
			// 128.
			// There is no "cast" that will convert 128 to -128, like C's
			// casting to si1.
			// Problem is, when constructing a new number after a break, you may
			// want the
			// value +128. So, you either convert all +128 to -128 here and
			// unroll it later,
			// or determine at this time whether you want the actual value or
			// the "flag", -128.
			// I chose to "roll" it here on each occassion, and "unroll" it
			// later when needed.
			// This must be "unrolled" a few lines down.
			if (symbol == 128)
				diff_buffer[db_p++] = -128;
			else
				diff_buffer[db_p++] = symbol;
		}
		ib_p = dec_normalize(ib_p);
		// printf("end %u %u\n", range, low_bound);
		/*** generate output data from differences ***/
		db_p = 0;
		ob_p = 0;
		int current_val = 0;
		for (long i = block_len; i > 0; i--) {
			// if (diff_buffer[db_p] == -128 || diff_buffer[db_p] == 255 ) { //
			// assumes little endian input
			if (diff_buffer[db_p] == -128) { // assumes little endian input
				// Here is the "unrolling". A "value" of -128 must be
				// interpreted as +128.
				if (diff_buffer[++db_p] == -128)
					diff_buffer[db_p] = 128;
				current_val = diff_buffer[db_p];
				if (diff_buffer[++db_p] == -128)
					diff_buffer[db_p] = 128;
				current_val += diff_buffer[db_p] << 8;
				if (diff_buffer[++db_p] == -128)
					diff_buffer[db_p] = 128;
				current_val += diff_buffer[db_p] << 16;
				if (diff_buffer[db_p++] > 127)
					current_val += -1 << 24;
				if (current_val > max_data_value
						|| current_val < min_data_value)
					// System.out.println("decoded value out of range.");
					throw new RuntimeException("decoded value out of range.");
			} else {
				current_val += (byte) diff_buffer[db_p++];
				if (current_val > max_data_value
						|| current_val < min_data_value)
					// System.out.println
					throw new RuntimeException("decoded value out of range: "
							+ current_val + "\tRange: " + min_data_value + " "
							+ max_data_value);
			}
			out_buffer[ob_p] = current_val;
			ob_p++;
		}
		return out_buffer;
	}

	public void clear() {
		diff_buffer = null;
		in_buffer = null;
		cnts = null;
		cum_cnts = null;
		encrypted_bytes = null;
		output_int = null;
	}

	// Cannot use Bits.java, because these values came from unsigned C variables
	// and so may exceed the maximum allowable value in some cases.
	// That requires using the next size up for each variable, which Bits.java
	// does not do.
	private int getSI1() {
		int result = in_buffer[buffer_p];
		buffer_p++;
		return result;
	}

	private int getUI1() {
		int result = (int) in_buffer[buffer_p] & 0xFF;
		buffer_p++;
		return result;
	}

	private long getUI4() {
		long result = 0;
		for (int k = 0; k < 4; k++) {
			int shift = k * 8;
			Long shiftedValue = new Long(in_buffer[buffer_p] & 0xFF) << shift;
			result += shiftedValue;
			buffer_p++;
		}
		return result & 0xffffffffL;
	}

	private long getSI3() {
		long result = 0;
		for (int k = 0; k < 3; k++) {
			int shift = k * 8;
			int intValue = in_buffer[buffer_p] & 0xFF;
			Long shiftedValue = new Long(intValue) << shift;
			result += shiftedValue;
			buffer_p++;
		}
		if (in_buffer[buffer_p - 1] < 0) {
			result += -1 << 24;
		}
		return result;
	}

	private long getUI8() {
		BigInteger result = new BigInteger("0");
		for (int k = 0; k < 8; k++) {
			int shift = k * 8;
			int tmp = in_buffer[buffer_p] & 0xFF;
			BigInteger shiftedValue = new BigInteger(Integer.toString(tmp))
					.shiftLeft(shift);
			result = result.add(shiftedValue);
			buffer_p++;
		}
		if (result.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) >= 0)
			throw new RuntimeException("Value exceeds max long value.");
		if (result.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) <= 0)
			throw new RuntimeException("Value exceeds min long value.");
		return result.longValue();
	}
	
	private void skip(int n) {
		checkArgument(n >= 0);
		buffer_p += n;
	}

	public static class HeaderOfRED {

		/**
		 * length of RED header in bytes
		 */
		public static final int BYTES = 287;
		// crc CRC_32;
		private boolean CRC_validated;
		private long compressed_bytes;
		private long block_start_time;
		private long difference_count;
		private long sample_count;
		private long max_value;
		private long min_value;
		private int discontinuity;

		public HeaderOfRED() {
			// CRC_32 = new crc();
		}

		public HeaderOfRED(byte[] bytes, int offset) throws IOException {
			final LittleEndianDataInputStream is = new LittleEndianDataInputStream(
					new ByteArrayInputStream(bytes));
			// Skip to offset and then past the CRC entry
			final int toSkip = offset + 4;
			ByteStreams.skipFully(is, toSkip);
			compressed_bytes = is.readInt();
			block_start_time = is.readLong();
			ByteStreams.skipFully(is, 4);// skip over difference count
			sample_count = is.readInt();
			is.close();

		}

		// public void clear() {
		// CRC_32.clear();
		// CRC_32 = null;
		// }
		//
		// void CRC_32( crc c ) { CRC_32 = c; }
		// crc CRC_32() { return CRC_32; }
		void CRC_validated(boolean v) {
			CRC_validated = v;
		}

		boolean CRC_validated() {
			return CRC_validated;
		}

		void compressed_bytes(long comp_block_len) {
			compressed_bytes = comp_block_len;
		}

		public long compressed_bytes() {
			return compressed_bytes;
		}

		void block_start_time(long bst) {
			block_start_time = bst;
		}

		public long block_start_time() {
			return block_start_time;
		}

		void difference_count(long dc) {
			difference_count = dc;
		}

		long difference_count() {
			return difference_count;
		}

		void sample_count(long sc) {
			sample_count = sc;
		}

		public long sample_count() {
			return sample_count;
		}

		void max_value(long mv) {
			max_value = mv;
		}

		long max_value() {
			return max_value;
		}

		void min_value(long mv) {
			min_value = mv;
		}

		long min_value() {
			return min_value;
		}

		void discontinuity(int discontinuity2) {
			discontinuity = discontinuity2;
		}

		int discontinuity() {
			return discontinuity;
		}

		// public long update_crc_32(long checksum, int intValue) {
		// return CRC_32.update_crc_32(checksum, intValue);
		// }
	}

	public static final class PageAndBytesRead implements Serializable {
		private static final long serialVersionUID = 1L;
		public final TimeSeriesPage page;
		public final int noBytesRead;
		public final byte[] bytesRead;

		public PageAndBytesRead(
				TimeSeriesPage page, 
				int noBytesRead,
				byte[] bytesRead) {
			this.noBytesRead = checkNotNull(
					noBytesRead);
			this.page = checkNotNull(page);
			this.bytesRead = checkNotNull(bytesRead);
		}
	}

	/**
	 * Decode the page starting at {@code pos}.
	 * 
	 * @param rawData data to be decoded
	 * @param pos where the page starts
	 * @param samplingPeriodMicros the samplin period of the decoded data
	 * @param decompressData if true fill in the data, otherwise only get the
	 *            start and times
	 * 
	 * @return the decoded page
	 */
	public PageAndBytesRead decodePage(
			byte[] rawData,
			int pos,
			double samplingPeriodMicros,
			boolean decompressData) {
		try {
			HeaderOfRED header = new HeaderOfRED(rawData, pos);
			int noBytes = Ints.checkedCast(
					HeaderOfRED.BYTES + header.compressed_bytes());
			long startTime = header.block_start_time();
			long noSamples = header.sample_count();
			TimeSeriesPage page = new TimeSeriesPage();

			if (decompressData) {
				int[] decoded = decode(rawData, pos, noBytes);
				page.values = decoded;
			}
			long endTime =
					startTime + (long) (noSamples * samplingPeriodMicros);

			page.timeStart = startTime;
			page.timeEnd = endTime;
			return new PageAndBytesRead(
					page,
					noBytes,
					Arrays.copyOfRange(rawData, pos, pos + noBytes));
		} catch (IOException ioe) {
			throw propagate(ioe);
		}
	}
	
	
	/**
	 * Decode the page starting at {@code pos}.
	 * 
	 * @param rawData data to be decoded
	 * @param pos where the page starts
	 * @param samplingPeriodMicros the samplin period of the decoded data
	 * 
	 * @return the decoded page
	 */
	public PageAndBytesRead decodePage(
			byte[] rawData,
			int pos,
			double samplingPeriodMicros) {
		return decodePage(rawData, pos, samplingPeriodMicros, true);
	}
}
