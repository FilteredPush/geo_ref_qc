/** Runner.java
 * 
 * Copyright 2022 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.filteredpush.qc.georeference;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.api.result.NumericalValue;
import org.datakurator.ffdq.model.ResultState;

/**
 * Selfstanding execution of geo_ref_qc functionality.  Can run TG2 Space related tests on flat DarwinCore.
 * 
 * @author mole
 *
 */
public class Runner {

	private static final Log logger = LogFactory.getLog(Runner.class);

	/**
	 * Execute Runner from the command line.
	 * 
	 * @param args -e runTests with -f {filename} -h
	 */
	public static void main(String[] args) {
		Options options = new Options();
		Option opte = new Option("e", "execute", true, "Action to execute (runTests)");
		options.addOption(opte);
		options.addOption("f", null, true, "Input file containing darwin core data to test.");
		options.addOption("l","limit",true,"Limit processing to the specified number of rows");
		options.addOption("h","help",false,"Show help.");

		try { 
			// Get option values
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption("h")) { 
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "java -jar geo_ref_qc-{version}-{gitcommit}-executable.jar", options);
			} 
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
