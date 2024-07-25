/*
 * Copyright (C) 2014 Trustees of the University of Pennsylvania
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static com.google.common.collect.Sets.newTreeSet;

import java.util.Set;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public final class MontagedChAnnotation {

	private long id;
	private long parentId;
	private String type;
	private String description;
	private long startOffsetUsecs;
	private long endOffsetUsecs;
	private String color;
	private Set<Long> montagedChannelIds = newTreeSet();

	/** For JPA. */
	@SuppressWarnings("unused")
	private MontagedChAnnotation() {}

	public MontagedChAnnotation(
			long parentId,
			String type,
			String description,
			long startOffsetUsecs,
			long endOffsetUsecs,
			String color,
			Set<Long> montagedChannelIds,
			long id) {
		this(
				parentId,
				type,
				description,
				startOffsetUsecs,
				endOffsetUsecs,
				color,
				montagedChannelIds);
		this.id = id;
	}

	public MontagedChAnnotation(
			long parentId,
			String type,
			String description,
			long startOffsetUsecs,
			long endOffsetUsecs,
			String color,
			Set<Long> montagedChannelIds) {
		this.parentId = parentId;
		this.type = checkNotNull(type);
		this.description = description;
		checkArgument(startOffsetUsecs >= 0,
				"start offset must be >= 0");
		this.startOffsetUsecs = startOffsetUsecs;
		checkArgument(
				endOffsetUsecs >= startOffsetUsecs,
				"end offset must be >= start offset");
		

		checkArgument(
				startOffsetUsecs >= 0,
				"end offset must be >= 0");
		this.endOffsetUsecs = endOffsetUsecs;
		this.color = color;

		checkArgument(
				montagedChannelIds.size() > 0,
				"must annotate at least one channel");
		this.montagedChannelIds.addAll(montagedChannelIds);
	}

	@Nullable
	@XmlAttribute
	public String getColor() {
		return color;
	}

	@Nullable
	@XmlAttribute
	public String getDescription() {
		return description;
	}

	@XmlAttribute
	public long getEndOffsetUsecs() {
		return endOffsetUsecs;
	}

	@Nullable
	@XmlAttribute
	public Long getId() {
		return id;
	}

	@XmlElementWrapper
	@XmlElement(name = "montagedChanenelId")
	public Set<Long> getMontagedChannelIds() {
		return montagedChannelIds;
	}

	@XmlAttribute
	public long getParentId() {
		return parentId;
	}

	@XmlAttribute
	public Long getStartOffsetUsecs() {
		return startOffsetUsecs;
	}

	@XmlAttribute
	public String getType() {
		return type;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEndOffsetUsecs(long endOffsetUsecs) {
		this.endOffsetUsecs = endOffsetUsecs;
	}

	@SuppressWarnings("unused")
	private void setId(long id) {
		this.id = id;
	}

	@SuppressWarnings("unused")
	private void setId(Long id) {
		this.id = id;
	}

	public void setMontagedChannelIds(Set<Long> montagedChannelIds) {
		this.montagedChannelIds = newLinkedHashSet(montagedChannelIds);
	}

	@SuppressWarnings("unused")
	private void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public void setStartOffsetUsecs(long startOffsetUsecs) {
		this.startOffsetUsecs = startOffsetUsecs;
	}

	public void setStartOffsetUsecs(Long startOffsetUsecs) {
		this.startOffsetUsecs = startOffsetUsecs;
	}

	public void setType(String type) {
		this.type = type;
	}

}
