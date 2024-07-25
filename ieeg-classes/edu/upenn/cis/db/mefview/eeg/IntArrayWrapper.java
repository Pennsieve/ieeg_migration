/*
 * Copyright 2012 Trustees of the University of Pennsylvania
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
package edu.upenn.cis.db.mefview.eeg;

import java.util.Arrays;

/**
 * Abstraction over a collection of arrays -- which gives
 * the illusion of a single uniform array
 * 
 * @author zives
 *
 */
public class IntArrayWrapper {
	int[][] _arrays;
	int[] _pos;
	int _last;
	
	public IntArrayWrapper(int arrays) {
		_arrays = new int[arrays][];
		_last = -1;
		_pos = new int[arrays];
		_pos[0] = 0;
	}
	
	public IntArrayWrapper(int[][] arrays) {
		_arrays = arrays;
		_pos = new int[arrays.length];
		_pos[0] = 0;
		for (_last = 0; _last < arrays.length - 1; _last++)
			_pos[_last + 1] = _pos[_last] + _arrays[_last].length;
	}
	
	public void addArray(int[] ar) {
		if (_last < _arrays.length) {
			if (_last > -1)
				_pos[_last + 1] = _pos[_last] + _arrays[_last].length;
			_arrays[++_last] = ar;
		} else
			throw new RuntimeException("Out of bounds!");
	}
	
//	private Integer getPos(int inx) {
//		int pos = 0;
//		int i = 0;
//		int j = 0;
//		
//		while (pos < inx) {
//			j++;
//			pos++;
//			if (j >= _arrays[i].length) {
//				j = 0;
//				i++;
//			}
//			if (i >= _arrays.length)
//				return null;
//		}
//		return _arrays[i][j];
//	}
	
	public int getStart(int page) {
		return _pos[page];
	}
	
	public Integer get(int inx) {
		int pos = Arrays.binarySearch(_pos, inx);
		
		if (pos < 0) {
			pos = -pos - 2;
		}
		if (pos < 0)
			pos = 0;
		
		if (pos <= _last) {
			Integer ret;
			try {
				ret = _arrays[pos][inx - _pos[pos]];
			} catch (ArrayIndexOutOfBoundsException ae) {
				throw ae;
			}
			
//			Integer ret2 = getPos(inx);
			
//			if ((ret == null && ret2 != null) || (ret != null && ret2 == null) || ret.intValue() != ret2.intValue())
//				throw new RuntimeException("Mismatch! " + ret + " vs " + ret2);
			return ret;
		} else
			throw new RuntimeException("Out of bounds");
	}
	
	public void put(int inx, int value) {
		int pos = Arrays.binarySearch(_pos, inx);
		
		if (pos < 0) {
			pos = -pos - 2;
		}
		if (pos < 0)
			pos = 0;
		
		if (pos <= _last)
			_arrays[pos][inx - _pos[pos]] = value;
		else
			throw new RuntimeException("Out of bounds");
	}
	
	public int length() {
		return _pos[_last] + _arrays[_last].length;
	}
	
	public String toString() {
		StringBuilder buf = new StringBuilder("[");
		
		boolean first = true;
		for (int i = 0; i < _arrays.length; i++)
			for (int j = 0; j < _arrays[i].length; j++) {
				if (first)
					first = false;
				else
					buf.append(',');
				buf.append(_arrays[i][j]);
			}
		
		buf.append("]");
		return buf.toString();
	}
}
