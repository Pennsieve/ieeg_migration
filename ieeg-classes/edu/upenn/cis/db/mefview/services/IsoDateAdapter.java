/*
 * Copyright 2014 Trustees of the University of Pennsylvania
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author John Frommeyer
 *
 */
public class IsoDateAdapter extends XmlAdapter<String, Date> {

	final private static String formatString = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	@Override
	public Date unmarshal(String v) throws Exception {
		if (v == null) {
			return null;
		}
		final DateFormat df = getDateFormat();
		final Date date = df.parse(v);
		return date;
	}

	@Override
	public String marshal(Date v) throws Exception {
		if (v == null) {
			return null;
		}
		final DateFormat df = getDateFormat();
		final String string = df.format(v);
		return string;
	}

	private DateFormat getDateFormat() {
		final TimeZone tz = TimeZone.getTimeZone("UTC");
		final DateFormat df = new SimpleDateFormat(formatString);
		df.setTimeZone(tz);
		return df;
	}
}
