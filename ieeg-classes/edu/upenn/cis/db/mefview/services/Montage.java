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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newTreeSet;

import java.util.Set;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import edu.upenn.cis.db.mefview.shared.IHasName;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public final class Montage implements IHasName {

	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String eTag;
	private String datasetId;
	private String name;
	private Set<MontagedChannel> montagedChannels = newTreeSet(new IHasName.NameComparator());

	@SuppressWarnings("unused")
	private Montage() {}

	public Montage(
			String name,
			Set<MontagedChannel> montagedChannels) {
		this.name = checkNotNull(name);
		this.montagedChannels.addAll(montagedChannels);
	}

	public Montage(
			String name,
			Set<MontagedChannel> montagedChannels,
			long id,
			String eTag,
			String datasetId) {
		this(name, montagedChannels);
		this.id = id;
		this.eTag = checkNotNull(eTag);
		this.datasetId = checkNotNull(datasetId);
	}

	@Nullable
	@XmlAttribute
	public String getDatasetId() {
		return datasetId;
	}

	@Nullable
	@XmlAttribute
	public String getETag() {
		return eTag;
	}

	@Nullable
	@XmlAttribute
	public Long getId() {
		return id;
	}

	@XmlElementWrapper
	@XmlElement(name = "montagedChannel")
	public Set<MontagedChannel> getMontagedChannels() {
		return montagedChannels;
	}

	public Set<Long> getMontagedChannnelIds() {
		return MontagedChannel.toLongIds(getMontagedChannels());
	}

	@Override
	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	@SuppressWarnings("unused")
	private void setETag(String eTag) {
		this.eTag = eTag;
	}

	@SuppressWarnings("unused")
	private void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Montage [id=" + id + ", name=" + name + ", eTag=" + eTag
				+ ", montagedChannels=" + montagedChannels + "]";
	}
}
