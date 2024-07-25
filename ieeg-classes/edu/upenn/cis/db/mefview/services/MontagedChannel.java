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

import edu.upenn.cis.db.mefview.shared.IHasName;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public final class MontagedChannel implements IHasName {

	public static Set<Long> toLongIds(Set<MontagedChannel> montagedChannels) {
		Set<Long> longIds = newLinkedHashSet();
		for (MontagedChannel montagedCh : montagedChannels) {
			longIds.add(montagedCh.getId());
		}
		return longIds;
	}

	private Long id;
	private String name;
	private String channelId;
	private Set<ReferenceChannel> referenceChannels = newTreeSet(new ReferenceChannel.TimeSeriesIdComparator());

	@SuppressWarnings("unused")
	private MontagedChannel() {}

	public MontagedChannel(
			String name,
			String timeSeriesId,
			Set<ReferenceChannel> referenceChannels) {
		this.name = checkNotNull(name);
		this.channelId = checkNotNull(timeSeriesId);
		this.referenceChannels.addAll(referenceChannels);
	}

	public MontagedChannel(
			String name,
			String timeSeriesId,
			Set<ReferenceChannel> referenceChannels,
			long id) {
		this.name = checkNotNull(name);
		this.channelId = checkNotNull(timeSeriesId);
		this.referenceChannels.addAll(referenceChannels);
		this.id = id;
	}

	@XmlAttribute
	public String getChannelId() {
		return channelId;
	}

	@Nullable
	@XmlAttribute(name = "id")
	public Long getId() {
		return id;
	}

	@Override
	@XmlAttribute
	public String getName() {
		return name;
	}

	@XmlElementWrapper
	@XmlElement(name = "referenceChannel")
	public Set<ReferenceChannel> getReferenceChannels() {
		return referenceChannels;
	}

	@SuppressWarnings("unused")
	private void setChannelId(String timeSeriesId) {
		this.channelId = timeSeriesId;
	}

	@SuppressWarnings("unused")
	private void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = checkNotNull(name);
	}

	@Override
	public String toString() {
		return "MontagedChannel [id=" + id + ", name="
				+ name + ", channelId=" + channelId + ", referenceChannels="
				+ referenceChannels + "]";
	}

}
