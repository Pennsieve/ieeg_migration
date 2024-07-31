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
package com.pennsieve.ieeg;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * Main for the Mef2EdfPlus tool
 * @author Veena Krish
 */

public class Mef2EdfMain {

	public static void main(String[] args) throws FileNotFoundException,
			IOException, Mef2EdfException {
		try {
			final Mef2EdfMainParams mainParams = new Mef2EdfMainParams();
			final JCommander jc = new JCommander(mainParams);
			Mef2Edf convert = null;
			jc.setProgramName("mef2edf");
			
			try {
				jc.parse(args);
			} catch (ParameterException e) {
				System.err.println(e.getMessage());
				jc.usage();
				System.exit(1);
			}
			if (mainParams.isHelp()) {
				System.out
						.println('\n' + "mef2edf converts mef files into an edf+ file.");
				System.out.println("See help docs: [url]" + '\n');
				jc.usage();
				return;
			}
			if (mainParams.getConfigFile() != null) {
				convert = new Mef2Edf(mainParams.getConfigFile(), mainParams.getOutputFile());
			} else if (mainParams.getFileNames().size() > 0) {
				convert = new Mef2Edf(mainParams.getFileNames(), mainParams.getOutputFile());
			} else {
				System.out.println("No files to convert.");
				jc.usage();
				System.exit(0);
			}

			if (!convert.getSameSamplingRate()) {
				System.err.println("Sampling rates in files differ");
				System.exit(1);
			} else { 
				convert.main();
				System.exit(0);
			}
		}

		catch (RuntimeException rte) {
			rte.printStackTrace();
			System.exit(2);
		} catch (Error e) {
			e.printStackTrace();
			System.exit(2);
		}
	}

}