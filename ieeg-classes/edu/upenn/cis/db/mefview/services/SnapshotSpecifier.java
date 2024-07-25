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

import java.util.List;

public abstract class SnapshotSpecifier {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	GeneralMetadata parent;
	
//	private static HashMap<String, VALUE_TYPE> keys;
//	static {
//		keys = new HashMap<String, VALUE_TYPE>();
//		keys.put("id", VALUE_TYPE.STRING);
//	}

	public abstract String getRevID();

	public String getLabel() {
		return getRevID();
	}
	
	public String getId() {
		return getRevID();
	}
	
	public void setId(String id) {}
	
	public String getStringValue(String key) {
		if (key.equals("id"))
			return getRevID();
		return null;
	}
	
	public Double getDoubleValue(String key) {
		return null;
	}
	
	public Integer getIntegerValue(String key) {
		return null;
	}
	
	public List<String> getStringSetValue(String key) {
		return null;
	}
}