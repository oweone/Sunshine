/* Copyright (c) 2011-2014 Richard Gay <gay@mais.informatik.tu-darmstadt.de>
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
package net.cliseau.composer.javatarget;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import net.cliseau.composer.ExternalUnitAddresses;
import net.cliseau.composer.UnitGenerationException;
import net.cliseau.composer.PlainInetAddress;
import net.cliseau.composer.UnitInstantiation;
import net.cliseau.composer.javacor.UnitStartupCreatorJavaCor;
import net.cliseau.composer.javatarget.AspectWeaver;
import net.cliseau.composer.javatarget.AspectWeaverFSI;
import net.cliseau.composer.config.base.UnitConfigProvider;
import net.cliseau.composer.config.base.InvalidConfigurationException;
import net.cliseau.composer.config.target.JavaConfig;

/**
 * Exception during the instrumentation of the target program.
 *
 * Objects of this class should be thrown if the instrumentation of the target
 * program (the program's JAR file) fails. Here, this is if AspectJ fails to
 * perform the instrumentation for some reason. Possible causes are, for
 * instance,
 * <ul>
 * <li>the instrumentation tool (AspectJ compiler) could not be found,</li>
 * <li>definitions of classes referenced in the CliSeAu aspect cannot
 * be found.</li>
 * </ul>
 */
class InstrumentationException extends UnitGenerationException {
	/**
	 * Construct an InstrumentationException object.
	 *
	 * @param message The message explaining this exception (e.g., an error message of AspectJ indicating the problem). */
	public InstrumentationException(final String message) { super(message); }
}

/**
 * Unit instantiations for Java bytecode targets with Java coordinators.
 *
 * This unit effectively takes a target program in the form of Java bytecode and
 * an instantiation of the CliSeAu units' parameters (local policy, enforcer,
 * etc.) to generate an encapsulated program.
 *
 * The set of type options currently supported by this unit type:
 * <ul>
 * <li>"FSI" (Full Service automaton Inlining) inlines the whole CliSeAu unit,
 *     leaving out the coordinator component. In consequence, the unit can only make
 *     local decisions, does not accept remote requests, and does not need communication
 *     via sockets.</li>
 * <li>"measureTime" configures the inlined part of the CliSeAu unit to measure
 *     the taken duration for the mediation of the respective intercepted operation.</li>
 * </ul>
 *
 * @see #generateUnit(ExternalUnitAddresses, String)
 * @see net.cliseau.composer.UnitInstantiation
 */
public class JavaUnitInstantiation implements UnitInstantiation {
	/**
	 * Names of all dependencies for which paths must be provided.
	 *
	 * This list depends on all the libraries that are used by the fixed
	 * components of a CliSeAu unit. That is, whenever this part
	 * is changed in terms of making use of further libraries, the
	 * requiredDependencies array should be extended.
	 *
	 * @todo Maybe this field should also be used to specify information
	 *       about the required versions.
	 */
	public static final String[] requiredDependencies = { "log4j" };

	/** Subdirectory for dependency libraries */
	private static final String dependencySubdir = "libs";

	/** Configuration object that determines what is generated and how. */
	private final JavaConfig config;

	/**
	 * Construct a JavaUnitInstantiation object from a given configuration.
	 *
	 * @param config The configuration to use for the ininitalization.
	 * @exception InvalidConfigurationException Thrown in case of problems with the configuration.
	 */
	public JavaUnitInstantiation(final UnitConfigProvider config)
			throws InvalidConfigurationException {
		this(config.getJavaConfig());
	}

	/**
	 * Construct a JavaUnitInstantiationObject from a given configuration.
	 *
	 * @param config The configuration to use for the ininitalization.
	 * @exception InvalidConfigurationException Thrown in case of problems with the configuration.
	 */
	public JavaUnitInstantiation(final JavaConfig config)
			throws InvalidConfigurationException {
		this.config = config;
	}

	/**
	 * Make a list of files relative to a given directory.
	 *
	 * This method modifies a given an array of files
	 * ({/path1/to1/file1, /path2/to2/file2, ...}) to
	 * {destDir/file1, destDir/file2, ...}. That is, all the files' names are
	 * considered to be in "destDir" afterwards.
	 *
	 * @param paths Collection of files to be modified.
	 * @param destDir Directory in which all the files shall be.
	 * @return Modified collection of files
	 * @see #getInlinedDependencies(String)
	 * @see #getStartupDependencies(String)
	 */
	private static Collection<String> relativizePaths(Collection<String> paths, final String destDir) {
		ArrayList<String> result = new ArrayList<String>(paths.size());
		for (final String path : paths) {
			result.add(destDir + File.separator + (new File(path).getName()));
		}
		return result;
	}

	/**
	 * Return list of dependencies inlined into the target.
	 *
	 * This method returns a list of JAR files that the instrumented target
	 * additionally depends on. In addition to the AspectJ runtime this comprises
	 * all packages (not in the standard Java API) that are used by the advice
	 * which are inlined into the target. That is, whenever the template for
	 * advice is modified to make use of additional external packages, this
	 * method has to be adapted accordingly.
	 *
	 * @param destDir Alternate directory for files (use null for actual directory).
	 * @return Collection of paths to JAR files of the inlined dependencies.
	 * @see net.cliseau.composer.config.target.AspectJConfig#getAspectJAdviceTemplate()
	 * @todo We actually don't need to add all "InstantiationJARs" as dependencies
	 *       but only those relevant for interceptor and enforcer; this particularly
	 *       excludes the local policy and related data types ("DRs").
	 */
	private Collection<String> getInlinedDependencies(String destDir)
			throws InvalidConfigurationException {
		Collection<String> deps = new ArrayList<String>();
		deps.addAll(Arrays.asList(new String[] {
			// the AspectJ runtime is always (?) needed at runtime
			config.getAspectJRuntimeClasspath(),
			// aspect requires, e.g., EnforcementDecision and CriticalEventFactory
			config.getCliSeAuJavaRuntime()
		}));
		// aspect requires, e.g., the CriticalEventFactory instantiation and its
		// dependencies
		deps.addAll(config.getJavaInstantiationJARs());
		if (destDir != null) {
			deps = relativizePaths(deps, destDir);
		}
		return deps;
	}

	/**
	 * Return list of dependencies for unit startup.
	 *
	 * This method returns a list of JAR files that the unit startup program
	 * depends on. That is, whenever the template for the startup program is
	 * modified to make use of additional external packages, this method has to
	 * be adapted accordingly.
	 *
	 * @param destDir Alternate directory for files (use null for actual directory).
	 * @return Collection of paths to JAR files of the unit startup dependencies.
	 * @see JavaConfig#getJavaDependencyPaths()
	 * @todo Currently, both getInlinedDependencies and getStartupDependencies
	 *       refer to getInstantiationJARs(), even though not all of these JARs
	 *       might actually be used by both. Maybe introducing a proper split
	 *       betwen those JARs used for the INT+ENF parts and those for the
	 *       COR+POL part should be made.
	 */
	private Collection<String> getStartupDependencies(String destDir)
			throws InvalidConfigurationException {
		Collection<String> deps = new ArrayList<String>();
		deps.addAll(Arrays.asList(new String[] {
			// CliSeAu units' classes (e.g., Coordinator) are obviously required
			config.getCliSeAuJavaRuntime()
		}));
		// the startup unit creates an object of the instantiation's LocalPolicy
		// subclass and might therefore require some of its dependencies
		deps.addAll(config.getJavaInstantiationJARs());
		deps.addAll(config.getJavaDependencyPaths());
		if (destDir != null) {
			deps = relativizePaths(deps, destDir);
		}
		return deps;
	}

	/**
	 * Copy a list of files to a given directory.
	 *
	 * This method is used internally to copy the dependencies to the directory
	 * of the encapsulated target.
	 *
	 * @param fileNames Array of files to copy.
	 * @param destDir Destination directory.
	 */
	private static void copyFiles(Collection<String> fileNames, File destDir)
			throws IOException {
		for (final String f : fileNames) {
			FileUtils.copyFileToDirectory(new File(f), destDir);
		}
	}

	/**
	 * Generate everything that is required for the encapsulated unit.
	 *
	 * The main steps performed to obtain the encapsulated unit are
	 * <ol>
	 * <li>Generating an AspectJ aspect representing parts of the CliSeAu unit
	 *     and using AspectJ to weave this aspect into the target program,</li>
	 * <li>Creating the program to start the CliSeAu unit and the
	 *     instrumented target program.</li>
	 * </ol>
	 *
	 * @intnote Parameter documentation can be found in parent class
	 * @exception UnitGenerationException Thrown when generating the unit failed.
	 * @see net.cliseau.composer.UnitInstantiation#generateUnit(ExternalUnitAddresses, String)
	 * @todo For all steps, check error codes!
	 * @todo Ensure constructors with fewer parameters to be used!
	 */
	public void generateUnit(final ExternalUnitAddresses addresses, final String dirName)
			throws IOException,UnitGenerationException {
		// Step 1. Copy runtime dependency libraries
		File depDestDir = new File(dirName + File.separator + dependencySubdir);
		depDestDir.mkdir();
		copyFiles(getInlinedDependencies(null), depDestDir);//TODO: move this to AspectWeaver
		copyFiles(getStartupDependencies(null), depDestDir);//TODO: move this to UnitStartupCreatorJavaCor

		// Step 2. Weave part of the CliSeAu unit into the target program
		final Set<String> options = config.getTypeOptions();
		if (!options.contains("FSI")) {
			// normal CliSeAu cross-lining (in-line enforcer and interceptor only)
			AspectWeaver weaver = new AspectWeaver(//TODO: shorten the parameter list using "config"
					dirName, config,
					config.getInternalCoordinatorAddress(), config.getInternalEnforcerAddress(),
					getInlinedDependencies(dependencySubdir),
					options.contains("measureTime"),
					org.apache.log4j.Level.INFO.isGreaterOrEqual(config.getInstantiationLogLevel()));
			weaver.call();
		} else {
			// "full sequential inlining" (FSI)
			AspectWeaverFSI weaver = new AspectWeaverFSI(//TODO: shorten the parameter list using "config"
					dirName, config, config,
					config.getInternalCoordinatorAddress(), config.getInternalEnforcerAddress(),
					getInlinedDependencies(dependencySubdir),
					ComposerConfig.aspectFSITemplate, ComposerConfig.adviceFSITemplate,
					options.contains("measureTime"),
					org.apache.log4j.Level.INFO.isGreaterOrEqual(config.getInstantiationLogLevel()));
			weaver.call();
		}

		// Step 3. Construct the unit's starter program
		if (!options.contains("FSI")) {
			// only create a starter program if not doing
			// "full sequential inlining" (FSI)
			UnitStartupCreatorJavaCor startupCreator = new UnitStartupCreatorJavaCor(dirName,
					config, addresses,
					(new File(config.getJavaProgramJAR())).getName(),
					getStartupDependencies(dependencySubdir));
			startupCreator.call();
		}
	}

	/**
	 * Configuration options for the composer.
	 *
	 * This class encapsulates configuration for the composer which are
	 * independent of the concrete instance. In particular, this determines where
	 * the composer finds its own files and dependencies (libraries and
	 * executables).
	 *
	 * @todo Do not hard-code the template files here!
	 */
	private static class ComposerConfig {
		/** Path in which the string templates are stored. */
		public static final String templatesPath = "templates/";

		/** File name of the FSI aspect template (without "st" extension). */
		public static final String aspectFSITemplate  = templatesPath + "JavaAspect-FSI";
		/** File name of the FSI advice template (without "st" extension). */
		public static final String adviceFSITemplate  = templatesPath + "JavaAdvice-FSI";
	}
}
