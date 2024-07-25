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
package edu.upenn.cis.eeg;

import java.io.IOException;
import java.io.RandomAccessFile;

public class EegFileUtils {

	public static String fromCString(byte[] str) {
		int i = 0;
		for (; i < str.length; i++)
			if (str[i] == 0)
				break;
	
		return new String(str, 0, i);
	}

	/**
	 * Little-endian int from byte sequence
	 */
	public static int getIntFromBytes(byte[] intBytes) {
		return (intBytes[0] & 0xff) | (intBytes[1] & 0xff) << 8 |
				(intBytes[2] & 0xff) << 16 | (intBytes[3] & 0xff) << 24;
	}

	/**
	 * Little-endian long from byte sequence
	 */
	public static long getLongFromBytes(byte[] longBytes) {
		long upper = (((long) longBytes[4] & 0xff)
				| ((long) longBytes[5] & 0xff) << 8 |
				((long) longBytes[6] & 0xff) << 16 | ((long) longBytes[7] & 0xff) << 24) & 0xffffffffL;
	
		upper = upper << 32;
	
		long lower = ((longBytes[0] & 0xff) | (longBytes[1] & 0xff) << 8 |
				(longBytes[2] & 0xff) << 16 | (longBytes[3] & 0xff) << 24) & 0xffffffffL;
	
		upper = upper | lower;
	
		return upper;
	}

	/**
	 * Little-endian short from byte sequence
	 */
	public static short getShortFromBytes(byte[] shortBytes) {
		return (short) ((shortBytes[0] & 0xff) | (shortBytes[1] & 0xff) << 8);
	}

	public static String getString(byte[] array) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < array.length && array[i] != 0; i++)
			buf.append((char) array[i]);
		return buf.toString();
	}

	public static void printCString(String str, byte[] array) {
		System.out.print(str + ": ");
		for (int i = 0; i < array.length && array[i] != 0; i++)
			System.out.print((char) array[i]);
		System.out.println();
	}

	// public static class MEFHeader {
	
	public static int read(RandomAccessFile r, byte[] arr) throws IOException {
		r.read(arr);
		return arr.length;
	}

}
