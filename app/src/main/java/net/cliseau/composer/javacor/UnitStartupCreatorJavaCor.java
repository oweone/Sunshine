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
package net.cliseau.composer.javacor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import net.cliseau.composer.ExternalUnitAddresses;
import net.cliseau.composer.UnitGenerationException;
import net.cliseau.composer.config.base.InvalidConfigurationException;
import net.cliseau.composer.config.target.JavaCorConfig;
import org.apache.commons.lang.StringUtils;
import org.antlr.stringtemplate.*;
import org.antlr.stringtemplate.language.*;
import net.cliseau.composer.PlainInetAddress;

/**
 * Exception during unit generation caused by a missing third party tool.
 *
 * When a unit is generate, some auxiliary tools are invoked. An example
 * is the Java compiler, which is used for compiling the startup JAR file.
 * Objects of this class should be thrown during unit generation, when a
 * required auxiliary tool could not be found.
 */
class MissingToolException extends UnitGenerationException {
	/**
	 * Construct a MissingToolException object.
	 *
	 * @param message The message explaining this exception (the missing tool).
	 */
	public MissingToolException(final String message) { super(message); }
}

/**
 * Class for generating unit startup Java programs.
 *
 * The unit startup program has two purposes:
 * <ol>
 * <li>start the components of the CliSeAu unit (here the Coordinator with its LocalPolicy)</li>
 * <li>start the target program (here instrumented to already contain the
 * Interceptor and Enforcer components)</li>
 * </ol>
 *
 * This class implements the Callable interface for possible future
 * parallelization of the instantiation.
 *
 * @todo A clear separation between the target language independent and the
 *			dependent part yet has to be made.
 */
public class UnitStartupCreatorJavaCor implements Callable<Void> {
	/** String for separating class paths in manifest files */
	private static final String manifestClassPathSeparator = " "; // not ":"!

	/**
	 * Name of the file in the resulting JAR file into which the
	 * policy configuration shall be written (in Properties format).
	 *
	 * @todo This field is public for being used by AspectWeaverFSI, but
	 *       this is not a very elegant solution. This should finally
	 *       be resolved differently.
	 */
	public static final String policyParamsFileName = "policy.cfg";

	/** The directory into which the files for the unit are to be written. */
	private final String destinationDirectory;

	/** The configuration for the Java coordinator. */
	private final JavaCorConfig config;

	/** Addresses of all the coordinators to listen for messages from other CliSeAu units. */
	private final ExternalUnitAddresses externalAddresses;
	/** Name of the JAR file containing the target program. */
	private final String targetJAR;
	/** Collection of starter program dependencies (relative to the starter program path). */
	private final Collection<String> starterDependencies;

	/**
	 * Construct UnitStartupCreatorJavaCor object.
	 *
	 * @param destinationDirectory The directory into which the files for the unit are to be written.
	 * @param config The configuration of the Java coordinator.
	 * @param externalAddresses Addresses of all the coordinators to listen for messages from other CliSeAu units.
	 * @param targetJAR Name of the JAR file containing the target program.
	 * @param starterDependencies Collection of starter program dependencies (relative to the starter program path);
	 *                            the dependency files must already exist in "destinationDirectory"!
	 */
	public UnitStartupCreatorJavaCor(final String destinationDirectory,
			final JavaCorConfig config,
			final ExternalUnitAddresses externalAddresses,
			final String targetJAR,
			final Collection<String> starterDependencies)
			throws InvalidConfigurationException {
		this.destinationDirectory       = destinationDirectory;
		this.config                     = config;
		this.externalAddresses          = externalAddresses;
		this.targetJAR                  = targetJAR;
		this.starterDependencies        = starterDependencies;
	}

	/**
	 * Create the unit startup program.
	 *
	 * @return Void Nothing.
	 * @exception IOException Thrown when creating startup program on disk failed
	 * @exception InvalidConfigurationException Thrown when invalid configuration is determined during the execution
	 * @todo Do a proper handling of exceptions with respect to files that are created in the process.
	 */
	public Void call() throws IOException,MissingToolException,InvalidConfigurationException {
		// create instantiated code
		String startupCode = instantiateStartupTemplate();
		// write instantiated code into a file
		final String startupBase = getDestinationDirectory() + File.separator + getStartupName();
		final File startupFile = new File(startupBase + ".java");
		PrintWriter startupWriter = new PrintWriter(startupFile);
		startupWriter.print(startupCode);
		startupWriter.close();

		// compile the system unit code
		Collection<String> startupClassFiles = compile(startupFile, getAbsoluteStarterDependencies());
		startupFile.delete();

		// create the policy parameters file
		createPolicyConfigurationFile(startupClassFiles);

		// generate a JAR file for executing the starter.
		createJAR(startupBase + ".jar", startupClassFiles, getStarterDependencies());

		return null;
	}

	/**
	 * Create the policy parameters file.
	 *
	 * This creates the policy parameters file and adds it to the list of
	 * all files in archiveFileNames.
	 *
	 * @exception InvalidConfigurationException Thrown when some required configuration could not be obtained.
	 * @exception FileNotFoundException Thrown when the destination configuration file could not be created.
	 * @exception IOException Thrown when writing the configuration file fails.
	 * @todo This method is public only to be called by AspectWeaverFSI. This
	 *       is not very elegant and should be resolved differently.
	 */
	public void createPolicyConfigurationFile(Collection<String> archiveFileNames)
			throws InvalidConfigurationException,FileNotFoundException,IOException {
		Properties policyParams = new Properties();
		policyParams.putAll(config.getLocalPolicyParameters());
		final File policyParamFile = new File(getDestinationDirectory() + File.separator + policyParamsFileName);
		policyParams.store(new FileOutputStream(policyParamFile), null);
		archiveFileNames.add(policyParamFile.getAbsolutePath());
	}

	/**
	 * Create a Java program for starting the CliSeAu unit.
	 *
	 * This method creates the Java program that starts the Coordinator of the
	 * unit and runs the target program. Here, only the source code is created
	 * and returned. The compilcation of this program is supposed to be performed
	 * by the caller.
	 *
	 * @return Java code string of the startup program.
	 *
	 * @todo The code is a bit fragile if, e.g., unit identifiers contain
	 *			characters like double quotes. This should be made more robust.
	 */
	private String instantiateStartupTemplate() throws InvalidConfigurationException {
		StringTemplateGroup group =  new StringTemplateGroup("SAgroup", AngleBracketTemplateLexer.class);
		StringTemplate unit = group.getInstanceOf(config.getStartupTemplateFile());

		unit.setAttribute("LocalPolicyClass",		  config.getLocalPolicyName());
		unit.setAttribute("Identifier",				  config.getUnitName());
		unit.setAttribute("JavaVM",					  config.getTargetSystemJavaVM());
		unit.setAttribute("JARfile",					  getTargetJAR());
		unit.setAttribute("LogLevel",					  config.getJavaRuntimeLogLevel().toString());
		unit.setAttribute("PolicyConfigRes",        policyParamsFileName);

		// Create a string of Java code which constructs a 'CoordinatorAddressing' object
		// NOTE: The code below relies on the assumption that neither unit
		//			identifiers nor hostnames contain characters that are not allowed
		//			in a Java String an unencoded form (e.g., a double quote).
		StringBuffer sb = new StringBuffer("final CoordinatorAddressing result = new CoordinatorAddressing();\n");
		sb.append(String.format("result.setPrivateAddress(%s);\n",
				config.getInternalCoordinatorAddress().toCreationCode()));
		sb.append(String.format("result.setLocalEnforcerAddress(%s);\n",
				config.getInternalEnforcerAddress().toCreationCode()));
		for (Map.Entry<String,PlainInetAddress> entry : getExternalAddresses().entrySet()) {
			sb.append(String.format("result.setAddress(\"%s\", %s);\n",
					entry.getKey(), entry.getValue().toCreationCode()));
		}
		sb.append("return result;\n");
		unit.setAttribute("AddressingSetupCode",	  sb.toString());

		return unit.toString();
	}

	/**
	 * Compiles the unit startup file from Java source to Java bytecode.
	 *
	 * This compiles the startup file. During this process, multiple class files
	 * may be generated even though only a single input file to compile is
	 * specified. The reason for this is that classes other than the main class
	 * may be defined in the single file and are written to distinct class
	 * files. All created files are collected and returned by the method.
	 *
	 * @param startupFile The file to be compiled.
	 * @param startupDependencies Names of classpath entries to use for compilation.
	 * @return List of names of created (class) files during compilation.
	 * @exception UnitGenerationException Thrown when finding a Java compiler failed.
	 */
	private LinkedList<String> compile(final File startupFile,
			final Collection<String> startupDependencies)
				throws MissingToolException,InvalidConfigurationException {
		// code inspired by the examples at
		//   http://docs.oracle.com/javase/6/docs/api/javax/tools/JavaCompiler.html
		final LinkedList<String> createdFiles = new LinkedList<String>();

		// set the file manager, which records the written files
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			throw new MissingToolException("Could not find system Java compiler.");
		}
		StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null, null, null);
		JavaFileManager fileManager = new ForwardingJavaFileManager<StandardJavaFileManager>(stdFileManager) {
			/**
			 * Collect the list of all output (class) files.
			 *
			 * Besides its side-effect on the createdFiles list of the containing
			 * method, this method is functionally equivalent to its superclass
			 * version.
			 */
			public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location,
						String className, JavaFileObject.Kind kind, FileObject sibling)
					throws IOException {
				JavaFileObject fileForOutput = super.getJavaFileForOutput(location, className, kind, sibling);
				createdFiles.addLast(fileForOutput.getName());
				return fileForOutput;
			}
		};

		// set the files to compile
		Iterable<? extends JavaFileObject> compilationUnits =
			stdFileManager.getJavaFileObjectsFromFiles(Arrays.asList(startupFile));

		// do the actual compilation
		ArrayList<String> compileParams = new ArrayList<String>(2);

		boolean verbose = org.apache.log4j.Level.DEBUG.isGreaterOrEqual(config.getInstantiationLogLevel());
		if (verbose) compileParams.add("-verbose");

		compileParams.addAll(Arrays.asList("-classpath",
					StringUtils.join(startupDependencies, File.pathSeparator)));
		if (!compiler.getTask(null, fileManager, null, compileParams,
				null, compilationUnits).call()) {
			// could not compile all files without error
			//TODO: throw an exception ... see where to get the required information from
		}
		return createdFiles;
	}

	/**
	 * Create a JAR file for startup of the unit.
	 *
	 * The created JAR file contains all the specified archive files and contains a
	 * manifest which in particular determines the class path for the JAR file.
	 * All files in startupArchiveFileNames are deleted during the execution of
	 * this method.
	 *
	 * @param fileName Name of the file to write the result to.
	 * @param startupArchiveFileNames Names of files to include in the JAR file.
	 * @param startupDependencies Names of classpath entries to include in the JAR file classpath.
	 * @exception IOException Thrown if file operations fail, such as creating the JAR file or reading from the input file(s).
	 */
	private void createJAR(final String fileName,
			final Collection<String> startupArchiveFileNames,
			final Collection<String> startupDependencies)
			throws IOException,InvalidConfigurationException {
		// Code inspired by:
		//   http://www.java2s.com/Code/Java/File-Input-Output/CreateJarfile.htm
		//   http://www.massapi.com/class/java/util/jar/Manifest.java.html

		// construct manifest with appropriate "Class-path" property
		Manifest starterManifest = new Manifest();
		Attributes starterAttributes = starterManifest.getMainAttributes();
		// Remark for those who read this code to learn something:
		// If one forgets to set the MANIFEST_VERSION attribute, then
		// silently *nothing* (except for a line break) will be written
		// to the JAR file manifest!
		starterAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
		starterAttributes.put(Attributes.Name.MAIN_CLASS, getStartupName());
		starterAttributes.put(Attributes.Name.CLASS_PATH,
				StringUtils.join(startupDependencies, manifestClassPathSeparator));

		// create output JAR file
		FileOutputStream fos = new FileOutputStream(fileName);
		JarOutputStream jos = new JarOutputStream(fos, starterManifest);

		// add the entries for the starter archive's files
		for (String archFileName : startupArchiveFileNames) {
			File startupArchiveFile = new File(archFileName);
			JarEntry startupEntry = new JarEntry(startupArchiveFile.getName());
			startupEntry.setTime(startupArchiveFile.lastModified());
			jos.putNextEntry(startupEntry);

			// copy the content of the starter archive's file
			// TODO: if we used Apache Commons IO 2.1, then the following
			//       code block could be simplified as:
			//       FileUtils.copyFile(startupArchiveFile, jos);
			FileInputStream fis = new FileInputStream(startupArchiveFile);
			byte buffer[] = new byte[1024 /*bytes*/];
			while (true) {
				int nRead = fis.read(buffer, 0, buffer.length);
				if (nRead <= 0) break;
				jos.write(buffer, 0, nRead);
			}
			fis.close();
			// end of FileUtils.copyFile() substitution code
			jos.closeEntry();

			startupArchiveFile.delete(); // cleanup the disk a bit
		}

		jos.close();
		fos.close();
	}

	/**
	 * Get targetJAR.
	 *
	 * @return targetJAR as String.
	 */
	public String getTargetJAR()
	{
		 return targetJAR;
	}

	/**
	 * Get externalAddresses.
	 *
	 * @return externalAddresses as ExternalUnitAddresses.
	 */
	public ExternalUnitAddresses getExternalAddresses()
	{
		return externalAddresses;
	}

	/**
	 * Get startup template name.
	 *
	 * The template must ensure that it defines a main class named
	 * after the template file name (excluding file name extension).
	 * If this is the case, then the result of getStartupName() can
	 * be used as
	 * <ul>
	 * <li>the main class name for the startup program,</li>
	 * <li>the name (without ".java" extension) of the instantiated template,</li>
	 * <li>the name (without ".jar" extension) of the compiled startup program.</li>
	 * </ul>
	 *
	 * @return startup template file name (without path)
	 */
	public String getStartupName() throws InvalidConfigurationException
	{
		return new File(config.getStartupTemplateFile()).getName();
	}

	/**
	 * Get destinationDirectory.
	 *
	 * @return destinationDirectory as String.
	 */
	public String getDestinationDirectory()
	{
		return destinationDirectory;
	}

	/**
	 * Get starterDependencies.
	 *
	 * @return starterDependencies as Collection&lt;String&gt;.
	 */
	public Collection<String> getStarterDependencies()
	{
		return starterDependencies;
	}

	/**
	 * Get absolute version of starterDependencies, based on destinationDirectory
	 *
	 * @return Absolute version of starterDependencies as Collection&lt;String&gt;.
	 */
	public Collection<String> getAbsoluteStarterDependencies()
	{
		ArrayList<String> deps = new ArrayList<String>(starterDependencies.size());
		for (String dep : starterDependencies) {
			deps.add(destinationDirectory + File.separator + dep);
		}
		return deps;
	}
}
