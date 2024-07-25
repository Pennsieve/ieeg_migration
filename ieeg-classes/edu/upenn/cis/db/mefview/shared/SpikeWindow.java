package edu.upenn.cis.db.mefview.shared;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Lists;

import edu.upenn.cis.db.mefview.eeg.ITimeSeries;

@GwtCompatible(serializable=true)
public class SpikeWindow implements Serializable, ITimeSeries {
	// TODO: make this a special case of ITimeSeries??
	
	String optionalName = null;
	Integer currentCluster = null;
	double startTime;
	double period;
	double duration;
	
	double baseline;
	double multiplier;
	String units;
	String modality;
	
	int[] samples;
	
	public SpikeWindow(String nameOrNull, int inx, int windowSize) {
		this.optionalName = nameOrNull;
		this.currentCluster = inx;
	}
	
	public void setSamples(int[] samples, long startTime, double period, double duration) {
		this.samples = samples;
		this.startTime = startTime;
		this.period = period;
		this.duration = duration;
	}
	
	public int[] getSamples() {
		return samples;
	}
	
	public int getWindowSize() {
		return samples.length;
	}
	
	public Integer getClusterId() {
		return currentCluster;
	}
	
	public void setClusterId(Integer id) {
		this.currentCluster = id;
	}
	
	public String getOptionalName() {
		return optionalName;
	}
	
	public void setOptionalName(String name) {
		optionalName = name;
	}

	public long getStartTime() {
		return (long)startTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public double getPeriod() {
		return period;
	}

	public void setPeriod(double period) {
		this.period = period;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}


	private int[] gapsStart;
	private int[] gapsEnd;

	public int[] getGapsStart() {
		return gapsStart;
	}

	public void setGapsStart(int[] gapStart) {
		this.gapsStart = gapStart;
	}

	public int[] getGapsEnd() {
		return gapsEnd;
	}

	public void setGapsEnd(int[] gapEnd) {
		this.gapsEnd = gapEnd;
	}

	public double getScale() {
		return multiplier;
	}

	public void setScale(double scale) {
		this.multiplier = scale;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public String toString() {
		return getStartTime() + "@" + getPeriod() + " x" + getScale() + ": " + Arrays.toString(samples);
	}

	@Override
	public List<Integer> getGapStart() {
		return Lists.newArrayList();
	}

	@Override
	public List<Integer> getGapEnd() {
		return Lists.newArrayList();
	}

	@Override
	public int[] getSeries() {
		return samples;
	}

	@Override
	public boolean isInGap(int i) {
		return false;
	}

	@Override
	public Integer getValue(int i) {
		return samples[i];
	}

	@Override
	public int getAt(int i) {
		return samples[i];
	}

	@Override
	public int getSeriesLength() {
		return samples.length;
	}
}
