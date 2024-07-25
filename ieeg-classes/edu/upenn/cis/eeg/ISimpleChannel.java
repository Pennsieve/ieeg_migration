package edu.upenn.cis.eeg;

import com.google.common.base.Function;

public interface ISimpleChannel {

	public static final Function<ISimpleChannel, String> GET_CHANNEL_NAME = new Function<ISimpleChannel, String>() {

		@Override
		public String apply(ISimpleChannel input) {
			return input.getChannelName();
		}

	};

	public String getChannelName();

	public double getSamplingFrequency();

	public double getVoltageConversionFactor();

	public double getVoltageOffset();

}
