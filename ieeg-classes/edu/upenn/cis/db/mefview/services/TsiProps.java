/*
 * Copyright 2013 Trustees of the University of Pennsylvania
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import com.google.common.base.Optional;

/**
 * Properties for {@link TimeSeriesInterface}
 * 
 * @author John Frommeyer
 */
public final class TsiProps {

	private Optional<String> mongoDbHostname;
	private Optional<Integer> mongoDbPort;
	private Optional<String> mongoDbUsername;
	private String mongoDbPassword;

	public static final String MONGODB_HOSTNAME = "mongodb.hostname";
	public static final String MONGODB_USERNAME = "mongodb.username";
	public static final String MONGODB_PASSWORD = "mongodb.password";
	public static final String MONGODB_PORT = "mongodb.port";
	
	public static final String MONGODB_PASSWORD_DFLT_VALUE = "";

	public TsiProps(File tsiPropsFile) {
		checkNotNull(tsiPropsFile);
		try {
			final FileReader propReader = new FileReader(tsiPropsFile);
			final Properties props = new Properties();
			props.load(propReader);
			mongoDbHostname =
					Optional.fromNullable(props.getProperty(MONGODB_HOSTNAME));

			// If hostname present, then not empty
			checkArgument(
					!mongoDbHostname.isPresent()
							|| !"".equals(mongoDbHostname.get()),
					"specified emtpy mongodb hostname; provide a value or remove the "
							+ MONGODB_HOSTNAME + " line");

			mongoDbUsername = Optional.fromNullable(props
					.getProperty(MONGODB_USERNAME));

			// If username is present, then not empty
			checkArgument(
					!mongoDbUsername.isPresent()
							|| !"".equals(mongoDbUsername.get()),
					"specified emtpy mongodb username; provide a value or remove the "
							+ MONGODB_USERNAME
							+ " line");

			// If username is present, then so is hostname.
			checkArgument(
					!mongoDbUsername.isPresent() || mongoDbHostname.isPresent(),
					"specified a mongodb username but no hostname");

			mongoDbPassword =
					props.getProperty(MONGODB_PASSWORD, MONGODB_PASSWORD_DFLT_VALUE);

			final String portStr = props.getProperty(MONGODB_PORT);
			if (portStr == null) {
				mongoDbPort = Optional.absent();
			} else if ("".equals(portStr)) {
				throw new IllegalArgumentException(
						"specified empty mongodb port; provide a value or remove the "
								+ MONGODB_PORT + " line");
			} else {
				// If port is present, then so is hostname.
				checkArgument(mongoDbHostname.isPresent(),
						"specified a port but no hostname");
				mongoDbPort = Optional.of(Integer.valueOf(portStr).intValue());
			}
		} catch (Exception e) {
			throw propagate(e);
		}
	}

	public Optional<String> getMongoDbHostname() {
		return mongoDbHostname;
	}

	public String getMongoDbPassword() {
		return mongoDbPassword;
	}

	public Optional<Integer> getMongoDbPort() {
		return mongoDbPort;
	}

	public Optional<String> getMongoDbUsername() {
		return mongoDbUsername;
	}

}
