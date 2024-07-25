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

import com.google.common.base.Optional;

public class UserAndPassword {
	private static ThreadLocal<Optional<String>> username = new ThreadLocal<Optional<String>>();
	private static ThreadLocal<Optional<String>> hashedPasswd = new ThreadLocal<Optional<String>>();

	static {
		username.set(Optional.<String> absent());
		hashedPasswd.set(Optional.<String> absent());
	}

	public static Optional<String> getUsername() {
		return username.get();
	}

	public static void set(
			String username,
			String hashedPassword) {
		UserAndPassword.username.set(Optional.of(username));
		UserAndPassword.hashedPasswd.set(Optional.of(hashedPassword));
	}

	public static void clear() {
		username.set(Optional.<String> absent());
		hashedPasswd.set(Optional.<String> absent());
	}

	public static Optional<String> getHashedPassword() {
		return hashedPasswd.get();
	}

	private UserAndPassword() {}
}
