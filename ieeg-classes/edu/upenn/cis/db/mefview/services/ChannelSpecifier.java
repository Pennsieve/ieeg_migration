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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;

import edu.upenn.cis.db.mefview.shared.INamedTimeSegment;
import edu.upenn.cis.db.mefview.shared.JsonTyped;

public abstract class ChannelSpecifier implements Serializable, JsonTyped {
	private static final long serialVersionUID = 1L;

	private final String id;
	private final String signature;
	
	private boolean isEvents = false;
	private INamedTimeSegment segment = null;
	
	private long offset = 0;
	
	public ChannelSpecifier(String id) {
		this.id = id;
		this.signature = null;
	}

	public ChannelSpecifier(
			String id,
			String signature) {
		this.id = checkNotNull(id);
		this.signature = checkNotNull(signature);
	}

	public String getHandle() {
		return id;
	}

	public String getId() {
		return id;
	}

	public String getString() {
		return id;
	}

	public String getKey() {
		return id;
	}
	
	public String getCheckStr() {
		return signature;
	}

	public abstract SnapshotSpecifier getSnapshot();

	@Override
	public String toString() {
		return getString();
	}

	public String getLabel() {
		return getString();
	}

	public void setId(String id) {
		
	}
	
	public String getStringValue(String key) {
		if (key.equals("id"))
			return id;
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
	
	public void setEvents(boolean event) {
		isEvents = event;
	}
	
	public boolean isEvents() {
		return isEvents;
	}

	public INamedTimeSegment getSegment() {
		return segment;
	}

	public void setSegment(INamedTimeSegment segment) {
		this.segment = segment;
	}
	
	public void setTimeOffset(long off) {
		offset = off;
	}
	
	public long getTimeOffset() {
		return offset;
	}


	public abstract ChannelSpecifier clone();
}