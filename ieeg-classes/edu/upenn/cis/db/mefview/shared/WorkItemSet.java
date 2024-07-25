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
package edu.upenn.cis.db.mefview.shared;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class WorkItemSet extends LinkedHashSet<IWorkItem> {
	String description;
	JsonTyped json;
	WorkItemSet start = null;
	Object origin;
	Serializable request;
	Set<String> newSnapshots = new HashSet<String>();
	
	public WorkItemSet(String description, JsonTyped json, Serializable request, Object origin) {
		super();
		this.description = description;
		this.json = json;
		start = this;
		setRequest(request);
		setOrigin(origin);
	}
	
	public WorkItemSet(WorkItemSet one) {
		super();
		this.description = one.description;
		this.json = one.json;
		start = this;
		setRequest(one.request);
//		newSnapshots.addAll(one.newSnapshots);
		setOrigin(one.origin);
	}

	public WorkItemSet(Set<? extends IWorkItem> items, String description, JsonTyped json) {
		super();
		addAll(items);
		this.description = description;
		this.json = json;
		start = this;
	}
	
	public String getDescription() {
		return description;
	}
	
	public JsonTyped getJson() {
		return json;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setJson(JsonTyped json) {
		this.json = json;
	}

	public Object getOrigin() {
		return origin;
	}

	public void setOrigin(Object origin) {
		this.origin = origin;
	}

	public Serializable getRequest() {
		return request;
	}

	public void setRequest(Serializable request) {
		this.request = request;
	}

	public WorkItemSet getStart() {
		return start;
	}

	public void setStart(WorkItemSet start) {
		this.start = start;
	}

	public Set<String> getNewSnapshots() {
		return newSnapshots;
	}

	public void setNewSnapshots(Set<String> newSnapshots) {
		this.newSnapshots = newSnapshots;
	}
	
}
