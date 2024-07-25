/*
 * Copyright (C) 2011 Trustees of the University of Pennsylvania
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

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;

import com.google.common.base.Function;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class DataSnapshotAndTool {

	public static final Function<DataSnapshotAndTool, String> getToolLabel =
			new Function<DataSnapshotAndTool, String>() {

				@Override
				public String apply(final DataSnapshotAndTool input) {
					return input.getToolLabel();
				}
			};

	private String dsRevId;

	private String toolLabel;

	private String dsLabel;

	/** For JAXB */
	@SuppressWarnings("unused")
	private DataSnapshotAndTool() {}

	public DataSnapshotAndTool(final String dsRevId, final String dsLabel,
			final String toolLabel) {
		this.dsRevId = checkNotNull(dsRevId);
		this.dsLabel = dsLabel;
		this.toolLabel = checkNotNull(toolLabel);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dsLabel == null) ? 0 : dsLabel.hashCode());
		result = prime * result + ((dsRevId == null) ? 0 : dsRevId.hashCode());
		result = prime * result
				+ ((toolLabel == null) ? 0 : toolLabel.hashCode());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DataSnapshotAndTool)) {
			return false;
		}
		DataSnapshotAndTool other = (DataSnapshotAndTool) obj;
		if (dsLabel == null) {
			if (other.dsLabel != null) {
				return false;
			}
		} else if (!dsLabel.equals(other.dsLabel)) {
			return false;
		}
		if (dsRevId == null) {
			if (other.dsRevId != null) {
				return false;
			}
		} else if (!dsRevId.equals(other.dsRevId)) {
			return false;
		}
		if (toolLabel == null) {
			if (other.toolLabel != null) {
				return false;
			}
		} else if (!toolLabel.equals(other.toolLabel)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the dsRevId
	 */
	@XmlElement
	public String getDsRevId() {
		return dsRevId;
	}

	/**
	 * @return the toolName
	 */
	@XmlElement
	public String getToolLabel() {
		return toolLabel;
	}

	@XmlElement
	public String getDsLabel() {
		return dsLabel;
	}
}
