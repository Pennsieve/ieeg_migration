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

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import edu.upenn.cis.db.mefview.shared.IHasName;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Immutable
public final class MontageInfo implements IHasName {

	private long id;
	private String eTag;
	private String datasetId;
	private String name;

	@SuppressWarnings("unused")
	private MontageInfo() {}

	public MontageInfo(
			long id,
			String eTag,
			String datasetId,
			String name) {
		this.id = id;
		this.eTag = checkNotNull(eTag);
		this.datasetId = checkNotNull(datasetId);
		this.name = checkNotNull(name);
	}

	@XmlAttribute
	public String getDatasetId() {
		return datasetId;
	}

	@XmlAttribute(name = "eTag")
	public String getETag() {
		return eTag;
	}

	@XmlAttribute
	public Long getId() {
		return id;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	@SuppressWarnings("unused")
	private void setDatasetId(String datasetId) {
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

	@SuppressWarnings("unused")
	private void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "MontageInfo [id=" + id + ", eTag=" + eTag + ", datasetId="
				+ datasetId + ", name=" + name + "]";
	}
}
