package edu.upenn.cis.eeg;

import static com.google.common.base.Preconditions.checkNotNull;

public class SimpleChannel implements ISimpleChannel {

	private static final class RecordingParameters {
		private final double samplingFrequency;
		private final double voltageConversionFactor;
		private final double voltageOffset;

		public RecordingParameters(double samplingFrequency,
				double voltageConversionFactor, double voltageOffset) {
			this.samplingFrequency = samplingFrequency;
			this.voltageConversionFactor = voltageConversionFactor;
			this.voltageOffset = voltageOffset;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			long temp;
			temp = Double.doubleToLongBits(samplingFrequency);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(voltageConversionFactor);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(voltageOffset);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof RecordingParameters)) {
				return false;
			}
			RecordingParameters other = (RecordingParameters) obj;
			if (Double.doubleToLongBits(samplingFrequency) != Double
					.doubleToLongBits(other.samplingFrequency)) {
				return false;
			}
			if (Double.doubleToLongBits(voltageConversionFactor) != Double
					.doubleToLongBits(other.voltageConversionFactor)) {
				return false;
			}
			if (Double.doubleToLongBits(voltageOffset) != Double
					.doubleToLongBits(other.voltageOffset)) {
				return false;
			}
			return true;
		}

	}

	private final String channelName;
	private final RecordingParameters parameters;

	public SimpleChannel(String name, double sf, double cf, double os) {
		this.channelName = checkNotNull(name);
		this.parameters = new RecordingParameters(sf, cf, os);
	}

	@Override
	public String getChannelName() {
		return channelName;
	}

	@Override
	public double getSamplingFrequency() {
		return parameters.samplingFrequency;
	}

	@Override
	public double getVoltageConversionFactor() {
		return parameters.voltageConversionFactor;
	}

	@Override
	public double getVoltageOffset() {
		return parameters.voltageOffset;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((channelName == null) ? 0 : channelName.hashCode());
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SimpleChannel)) {
			return false;
		}
		SimpleChannel other = (SimpleChannel) obj;
		if (channelName == null) {
			if (other.channelName != null) {
				return false;
			}
		} else if (!channelName.equals(other.channelName)) {
			return false;
		}
		if (parameters == null) {
			if (other.parameters != null) {
				return false;
			}
		} else if (!parameters.equals(other.parameters)) {
			return false;
		}
		return true;
	}
	
	public boolean conflictsWith(SimpleChannel other) {
		return channelName.equals(other.channelName) && !parameters.equals(other.parameters);
	}

	@Override
	public String toString() {
		return channelName + ", " + parameters.samplingFrequency + ", "
				+ parameters.voltageConversionFactor + ", "
				+ parameters.voltageOffset;
	}

}
