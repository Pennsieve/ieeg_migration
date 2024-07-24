/*
 * Copyright 2015 Trustees of the University of Pennsylvania
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
package org.ieeg.cli.mef2edf;


import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

/**
 * Command Line parameters for the mef2edf tool
 * @author Veena Krish
 *
 */
public class Mef2EdfMainParams {
	
	
	@Parameter(
			names = {
					"--config-file", 
					"-cf" },
					description = "<file with list of mef's to convert>")
	private String configFile;
	
	@Parameter(
			names = {
					"--output-file",
					"-o" },
					description = "<base name of output edf file to return>", 
					required = false)
	private String outputFile;
		
	@Parameter(description = "<mef files to convert to edf+>")
	private List<String> files = new ArrayList<String>();
	
	
	@Parameter(
			names = {
					"-h",
					"--help" },
			description = "print help message",
			help = true)
	private boolean help = false;

	public boolean isHelp() {
		return help;
	}
	
	public String getConfigFile() {
		return configFile;
	}
	
	public List<String> getFileNames() {
		return files;
	}
	
	public String getOutputFile() {
		return outputFile;
	}
	

	
}