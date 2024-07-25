/*
 * Copyright 2012 Trustees of the University of Pennsylvania
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

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A wrapper for a collection of {@code JobInformation}s.
 * 
 * @author John Frommeyer
 * 
 */
@XmlRootElement(name = "jobinfos")
public class JobInformationList {

	private List<JobInformation> jobInfos;

	// For JAXB
	JobInformationList() {
		jobInfos = newArrayList();
	}

	public JobInformationList(List<JobInformation> jobInfos) {
		this.jobInfos = newArrayList(jobInfos);
	}

	public JobInformationList(JobInformation[] jobInfos) {
		this.jobInfos = newArrayList(jobInfos);
	}

	@XmlElement(name = "jobinfo")
	public List<JobInformation> getJobInfos() {
		return jobInfos;
	}

	public JobInformation[] getArray() {
		return jobInfos.toArray(new JobInformation[jobInfos.size()]);
	}
}
