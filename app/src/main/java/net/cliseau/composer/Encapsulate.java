/* Copyright (c) 2011-2013 Richard Gay <gay@mais.informatik.tu-darmstadt.de>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.cliseau.composer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.Vector;
import org.apache.log4j.BasicConfigurator; // for debugging
import org.apache.log4j.Level; // for debugging
import org.apache.log4j.Logger; // for debugging
import net.cliseau.composer.UnitGenerationException;
import net.cliseau.composer.PartialEncapsulationInfrastructure;

import net.cliseau.composer.config.base.ConfigProvider;
import net.cliseau.composer.config.format.PropertiesConfig;

/**
 * Encapsulation program for distributed target applications.
 *
 * This program uses the "Service Automata Composer" to encapsulate
 * a given target application with a given security policy. The target
 * and policy are given through a configuration file.
 *
 * @todo Implement a graphical user interface for creating/editing the configurations.
 */
public class Encapsulate {
	/** Path to resource file containing default configuration values. */
	private static final String defaultConfigFile  = "/config/defaults.cfg";
	/** Path to resource file containing composer configuration values. */
	private static final String composerConfigFile = "/config/composer.cfg";

	/**
	 * Main method.
	 *
	 * @param args Command line arguments.
	 */
	public static void main(String args[])
			throws IOException,UnitGenerationException {
		if (args.length < 1) {
			System.err.println("Usage: java -jar encaps.jar <config> [<config> ...]");
			System.err.println();
			System.err.println("This encapsulates a target application of possibly multiple agents");
			System.err.println("with Service Automata. The configuration of the target and the");
			System.err.println("security requirement must be specified in the <config> files.");
			System.err.println("Configuration files are read in the order specified and configuration");
			System.err.println("values in later configuration files take precedence over earlier ones.");
			System.err.println();
			System.err.println("All relative path names mentioned in a <config> file are considered");
			System.err.println("to be relative to the directory of the *first* given <config> file.");
			System.exit(1);
		}
		BasicConfigurator.configure(); // log4j
		final Logger logger = Logger.getLogger("Encapsulate");

		// collect input streams for all given <config> files
		Vector<InputStream> configInputs = new Vector<InputStream>();
		configInputs.add(Encapsulate.class.getResourceAsStream(defaultConfigFile));
		for (String cfg : args) {
			configInputs.add(new FileInputStream(cfg));
		}

		// Absolute path to directory of first config
		File pathToConfig = (new File(args[0]).getAbsoluteFile()).getParentFile();
		// Absolute path to base directory of implementation
		// (TODO: this hard-codes that this JAR file is one level below the base directory,
		//        as it is via "build/")
		File pathToJAR    =
			new File(Encapsulate.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsoluteFile()
				.getParentFile().getParentFile(); // strip file name + one directory level

		//NOTE/future:
		//  If at some point we allow several configuration formats (e.g., XML), then
		//  we could put a switch here to see which concrete ConfigProvider object is
		//  to be created.

		// Configure a partial encapsulation infrastructure from a properties file
		ConfigProvider config = new PropertiesConfig(
				pathToConfig, new SequenceInputStream(configInputs.elements()),
				pathToJAR,    Encapsulate.class.getResourceAsStream(composerConfigFile));
		PartialEncapsulationInfrastructure pe = new PartialEncapsulationInfrastructure(config);

		logger.info("Generating encapsulated system consisting of "
				+ pe.getNumberOfUnits() + " units in " + pe.getDestinationDirectory());
		pe.generate();
		logger.info("done.");
	}
}
