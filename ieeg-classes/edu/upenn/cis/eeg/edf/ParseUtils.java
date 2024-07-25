/*
 * (The MIT license)
 * 
 * Copyright (c) 2012 MIPT (mr.santak@gmail.com)
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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.common.io.ByteStreams;

public abstract class ParseUtils
{
	public static ArrayList<String> readBulkASCIIFromStream(InputStream is, int size, int length) throws IOException
	{
        ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < length; i++)
		{
		  result.add(readASCIIFromStream(is, size));
		}
		return result;
	}

	public static ArrayList<Double> readBulkDoubleFromStream(InputStream is, int size, int length) throws IOException
	{
	  ArrayList<Double> result = new ArrayList<Double>();
		for (int i = 0; i < length; i++)
			result.add(Double.parseDouble(readASCIIFromStream(is, size).trim()));
		return result;
	}

	public static ArrayList<Integer> readBulkIntFromStream(InputStream is, int size, int length) throws IOException
	{
	  ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < length; i++)
			result.add(Integer.parseInt(readASCIIFromStream(is, size).trim()));
		return result;
	}

	public static String readASCIIFromStream(InputStream is, int size) throws IOException
	{
		byte[] data = new byte[size];
		try {
			ByteStreams.readFully(is, data);
		} catch (EOFException eofE) {
			throw new EDFParserException("Unexpected end of file");
		}
		return new String(data, EDFConstants.CHARSET);
	}

	public static <T> T[] removeElement(T[] array, int i)
	{
		if (i < 0)
			return array;
		if (i == 0)
			return Arrays.copyOfRange(array, 1, array.length);
		T[] result = Arrays.copyOfRange(array, 0, array.length - 1);
		for (int j = i + 1; j < array.length; j++)
			result[j - 1] = array[j];
		return result;
	}
}
