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

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Immutable
public final class DatasetFileInfos {

	private String datasetId;
	private String datasetName;

	private Set<FileInfo> fileInfos = newTreeSet(new FileInfo.FileNameComparator());

	@SuppressWarnings("unused")
	private DatasetFileInfos() {}

	public DatasetFileInfos(
			String datasetId,
			String datasetName,
			Set<FileInfo> fileInfos) {
		this.datasetId = checkNotNull(datasetId);
		this.datasetName = checkNotNull(datasetName);
		this.fileInfos.addAll(fileInfos);
	}

	@XmlAttribute
	public String getDatasetId() {
		return datasetId;
	}

	@XmlAttribute
	public String getDatasetName() {
		return datasetName;
	}

	@XmlElementWrapper
	@XmlElement(name = "fileInfo")
	public Set<FileInfo> getFileInfos() {
		return fileInfos;
	}

	@SuppressWarnings("unused")
	private void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	@SuppressWarnings("unused")
	private void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}
}
