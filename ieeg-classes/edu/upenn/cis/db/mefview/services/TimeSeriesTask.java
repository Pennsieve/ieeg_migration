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

import java.io.Serializable;
import java.io.StringReader;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="timeseriestask")
public class TimeSeriesTask implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	
	private String[] channels;
	private long startTime;
	private long endTime;
	
	public enum STATUS {UNSCHEDULED, RUNNING, KILLED, COMPLETE};
	
	private long startedRunning = 0;
	
	private STATUS status;
	
	private String assignedWorker = null;
	
	public TimeSeriesTask() {
		name = "";
		channels = new String[0];
		startTime = 0;
		endTime = 0;
		status = STATUS.UNSCHEDULED;
	}
	
	public TimeSeriesTask(final String name, final String[] ch, final long start, final long end) {
		this.name = name;
		channels = ch;
		startTime = start;
		endTime = end;
		status = STATUS.UNSCHEDULED;
	}
	
	
	public TimeSeriesTask(final String name, final String ch, final long start, final long end) {
		this.name = name;
		channels = new String[1];
		channels[0] = ch;
		startTime = start;
		endTime = end;
		status = STATUS.UNSCHEDULED;
	}
	
	public TimeSeriesTask(final String name, final String[] ch, final long start, final long end, final STATUS status) {
		this.name = name;
		channels = ch;
		startTime = start;
		endTime = end;
		this.status = status;
	}
	
	public TimeSeriesTask(String str){
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(TimeSeriesTask.class);
			
			Unmarshaller unmarshaller = context.createUnmarshaller();
			
			StringReader sr = new StringReader(str);
			
			unmarshaller.unmarshal(sr);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isEmpty() {
		return name.isEmpty();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getChannels() {
		return channels;
	}
	public void setChannels(String[] channels) {
		this.channels = channels;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public STATUS getStatus() {
		return status;
	}

	public void setStatus(STATUS status) {
		this.status = status;
	}

	public void setRunning() {
		setStatus(STATUS.RUNNING);
		startedRunning = new Date().getTime();
	}

	public long getStartedRunning() {
		return startedRunning;
	}

	public void setStartedRunning(long startedRunning, final String worker) {
		this.startedRunning = startedRunning;
		this.assignedWorker = worker;
	}

	public String getAssignedWorker() {
		return assignedWorker;
	}

}
