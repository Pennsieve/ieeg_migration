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

import java.util.Comparator;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Immutable
public final class ReferenceChannel {

	@Immutable
	public static final class TimeSeriesIdComparator
			implements Comparator<ReferenceChannel> {
		@Override
		public int compare(ReferenceChannel rc1, ReferenceChannel rc2) {
			return rc1.getChannelId().compareTo(rc2.getChannelId());
		}
	}

	private String channelId;
	private Double coefficient;

	@SuppressWarnings("unused")
	private ReferenceChannel() {}

	public ReferenceChannel(
			String channelId,
			Double coefficient) {
		this.channelId = checkNotNull(channelId);
		this.coefficient = checkNotNull(coefficient);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ReferenceChannel)) {
			return false;
		}
		ReferenceChannel other = (ReferenceChannel) obj;
		if (channelId == null) {
			if (other.channelId != null) {
				return false;
			}
		} else if (!channelId.equals(other.channelId)) {
			return false;
		}
		if (coefficient == null) {
			if (other.coefficient != null) {
				return false;
			}
		} else if (!coefficient.equals(other.coefficient)) {
			return false;
		}
		return true;
	}

	@XmlAttribute
	public String getChannelId() {
		return channelId;
	}

	@XmlAttribute
	public Double getCoefficient() {
		return coefficient;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((channelId == null) ? 0 : channelId.hashCode());
		result = prime * result
				+ ((coefficient == null) ? 0 : coefficient.hashCode());
		return result;
	}

	@SuppressWarnings("unused")
	private void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	@SuppressWarnings("unused")
	private void setCoefficient(Double coefficient) {
		this.coefficient = coefficient;
	}

	@Override
	public String toString() {
		return "ReferenceChannel [channelId=" + channelId + ", coefficient="
				+ coefficient + "]";
	}

}
