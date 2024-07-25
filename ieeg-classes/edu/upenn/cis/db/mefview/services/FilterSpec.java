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

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Specifier for a filter
 * 
 * @author zives
 * 
 */
@XmlRootElement(name = "filterSpec")
public class FilterSpec {
	public static final int NO_FILTER = 0;
	public static final int DECIMATE_FILTER = (1 << 0);
	public static final int LOWPASS_FILTER = (1 << 1);
	public static final int HIGHPASS_FILTER = (1 << 2);
	public static final int BANDPASS_FILTER = (1 << 3);
	public static final int BANDSTOP_FILTER = (1 << 12);

	public static final int DEFAULT_FILTER_POLES = 3;//32;
	public static final int MAX_FILTER_POLES = 8;//128;
	public static final double DEFAULT_LOW_FC = 0.0;

	// For use ONLY as a hash map key to a particular frequency
	private double target = 0;

	int filterType;
	int numPoles;
	double bandpassLowCutoff;
	double bandpassHighCutoff;
	double bandstopLowCutoff;
	double bandstopHighCutoff;
	String filterName;

	private static final Logger logger = LoggerFactory
			.getLogger(FilterSpec.class);

	public FilterSpec() {
		filterType = 0;//DECIMATE_FILTER;
		numPoles = 3;
		bandpassLowCutoff = -1;
		bandpassHighCutoff = -1;
		bandstopLowCutoff = -1;
		bandstopHighCutoff = -1;
	}

	public FilterSpec(String name, int filterType, int numPoles, double bandpassLowCutoff,
			double bandpassHighCutoff, double bandstopLowCutoff,
			double bandstopHighCutoff) {
		this.filterName = name;
		this.filterType = filterType;
		this.numPoles = numPoles;
		this.bandpassLowCutoff = bandpassLowCutoff;
		this.bandpassHighCutoff = bandpassHighCutoff;
		this.bandstopLowCutoff = bandstopLowCutoff;
		this.bandstopHighCutoff = bandstopHighCutoff;
	}

	public FilterSpec setAt(double t) {
		FilterSpec ret = new FilterSpec(getFilterName(), getFilterType(), getNumPoles(),
				getBandpassLowCutoff(),
				getBandpassHighCutoff(), getBandstopLowCutoff(),
				getBandstopHighCutoff());

		ret.target = t;

		return ret;
	}

	public void addFilterType(int typ) {
		filterType = filterType | typ;
	}

	public void setFilterType(int typ) {
		filterType = typ;
	}

	public void removeFilterType(int typ) {
		filterType = filterType & (~typ);
	}

	public boolean includesFilterType(int typ) {
		return (filterType & typ) != 0;
	}

	@XmlElement(name = "filterType")
	public int getFilterType() {
		return filterType;
	}

	@XmlElement(name = "poles")
	public int getNumPoles() {
		return numPoles;
	}

	public void setNumPoles(int numPoles) {
		this.numPoles = numPoles;
	}

	@XmlElement(name = "bandpassLow")
	public double getBandpassLowCutoff() {
		return bandpassLowCutoff;
	}

	public void setBandpassLowCutoff(double bandpassLowCutoff) {
		this.bandpassLowCutoff = bandpassLowCutoff;
	}

	@XmlElement(name = "bandpassHigh")
	public double getBandpassHighCutoff() {
		return bandpassHighCutoff;
	}

	public void setBandpassHighCutoff(double bandpassHighCutoff) {
		this.bandpassHighCutoff = bandpassHighCutoff;
	}

	@XmlElement(name = "bandstopLow")
	public double getBandstopLowCutoff() {
		return bandstopLowCutoff;
	}

	public void setBandstopLowCutoff(double bandstopLowCutoff) {
		this.bandstopLowCutoff = bandstopLowCutoff;
	}

	@XmlElement(name = "bandstopHigh")
	public double getBandstopHighCutoff() {
		return bandstopHighCutoff;
	}

	public void setBandstopHighCutoff(double bandstopHighCutoff) {
		this.bandstopHighCutoff = bandstopHighCutoff;
	}

	
	public String getFilterName() {
		if (filterName == null)
			return "";
		else
			return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public int hashCode() {
		return getFilterType() ^ Double.valueOf(bandpassLowCutoff).hashCode() ^
				Double.valueOf(bandpassHighCutoff).hashCode()
				^ Double.valueOf(bandstopLowCutoff).hashCode()
				^ Double.valueOf(bandstopHighCutoff).hashCode() ^
				getFilterName().hashCode()
				^ getNumPoles();
	}

	@Override
	public boolean equals(Object two) {
		if (!(two instanceof FilterSpec))
			return false;
		else {
			FilterSpec second = (FilterSpec) two;

			if (getFilterType() == second.getFilterType()
					&& getFilterType() == FilterSpec.DECIMATE_FILTER)
				return true;

			return (getFilterType() == second.getFilterType()
					&&
					(int) (1000 * bandpassLowCutoff) == (int) (1000 * second
							.getBandpassLowCutoff())
					&&
					(int) (1000 * bandpassHighCutoff) == (int) (1000 * second
							.getBandpassHighCutoff())
					&&
					(((getFilterType() & FilterSpec.BANDSTOP_FILTER) == 0) || (int) (1000 * bandstopLowCutoff) == (int) (1000 * second
							.getBandstopLowCutoff()))
					&&
					(((getFilterType() & FilterSpec.BANDSTOP_FILTER) == 0) || (int) (1000 * bandstopHighCutoff) == (int) (1000 * second
							.getBandstopHighCutoff())) &&
					numPoles == second.getNumPoles() && target == second.target &&
					getFilterName().equals(second.getFilterName()));
		}
	}


	@Override
	public String toString() {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(FilterSpec.class);
			Marshaller marshaller = context.createMarshaller();
			StringWriter sw = new StringWriter();

			marshaller.marshal(this, sw);
			return sw.toString();
		} catch (JAXBException e) {
			logger.error("Error converting to String.", e);
		}
		return "";
	}

}
