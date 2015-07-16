/* Copyright (c) 2014 Richard Gay <gay@mais.informatik.tu-darmstadt.de>
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
package net.cliseau.composer.config.format;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import net.cliseau.composer.UnitGenerationException;
import net.cliseau.composer.PlainInetAddress;
import net.cliseau.composer.config.base.ConfigProvider;
import net.cliseau.composer.config.base.ConfigUtils;
import net.cliseau.composer.config.base.InvalidConfigurationException;
import net.cliseau.composer.config.base.InvalidConfigValueException;
import net.cliseau.composer.config.base.MissingConfigurationException;
import net.cliseau.composer.config.base.ArchitectureConfig;
import net.cliseau.composer.config.base.UnitConfig;
import net.cliseau.composer.config.base.UnitConfigProvider;
import net.cliseau.composer.config.target.JavaConfig;
import net.cliseau.composer.javatarget.JavaUnitInstantiation;

/**
 * Configuration provider for configurations stored in Java's
 * {@code Properties} files.
 *
 * This class allows one to read configurations of
 * {@link net.cliseau.composer.PartialEncapsulationInfrastructure}s
 * from files that obey Java's {@code Properties} files. That is,
 * the configuration items are stored as key/value pairs. This class uses
 * hierarchical keys for a better visible structure of the configuration
 * files.
 *
 * @see <href a="http://docs.oracle.com/javase/6/docs/api/java/util/Properties.html">java.util.Properties</a>
 */
public class PropertiesConfig implements ConfigProvider,ArchitectureConfig {
	/** Prefix for global properties */
	private static final String propConfigGlobalPrefix = "cfg";
	/** Default Properties file unit config prefix */
	private static final String propConfigUnitPrefix = "unit";
	/** Property name separation symbol */
	private static final char propSepSymbol = '.';

	//TODO: do not hardcode the following fields' content!
	/** Path in which the string templates are stored. */
	public static final String templatesPath = "templates/";

	/** Default log level to take when no configuration is available. */
	public static final org.apache.log4j.Level defaultLogLevel = org.apache.log4j.Level.WARN;

	/** Configuration properties for the particular instance. */
	private Properties cfgInst;
	/** Configuration properties for the composer. */
	private Properties cfgComp;

	/**
	 * Create an instance of PropertiesConfig class.
	 *
	 * @param instBaseDirectory Directory to consider for all relative path names in 'inInst'.
	 * @param inInst Input stream providing instance configuration in Properties format.
	 * @param toolBaseDirectory Directory to consider for all relative path names in 'inComposer'.
	 * @param inComposer Input stream providing composer (compile-time) configuration in Properties format.
	 * @exception IOException thrown on failure of "Properties.load()"
	 */
	public PropertiesConfig(
			final File instBaseDirectory,
			final InputStream inInst,
			final File toolBaseDirectory,
			final InputStream inComposer)
			throws IOException {
		// load the configurations
		cfgInst = new Properties();
		cfgInst.load(inInst);
		cfgInst.put("basedir", instBaseDirectory);
		cfgComp = new Properties();
		cfgComp.load(inComposer);
		cfgComp.put("basedir", toolBaseDirectory);
	}

	/**
	 * {@inheritDoc}
	 *
	 * The {@link PropertiesConfig} class implements the
	 * {@link ArchitectureConfig} interface itself and therefore
	 * returns itself.
	 */
	public ArchitectureConfig getArchitectureConfig() { return this; }

	/**
	 * Return an object from which a chosen unit's configuration can be obtained.
	 *
	 * @param unitName Name of the unit whose configuration provider shall be returned.
	 * @return Configuration object for the unit with the given name.
	 */
	public UnitConfigProvider getUnitConfigProvider(final String unitName) {
		return new UnitPropertiesConfig(unitName);
	}

	/**
	 * {@inheritDoc}
	 *
	 * The destination directory is stored in the &quot;destdir&quot;
	 * configuration setting.
	 */
	public String getDestinationDirectory() throws MissingConfigurationException {
		return propGetAbsolute(cfgInst, propConfigGlobalPrefix + propSepSymbol + "destdir");
	}

	//------------- ARCHITECTURE CONFIG ------------------------------------------
	/**
	 * Return an array of all units' names.
	 *
	 * @return Array of all units' names.
	 * @exception InvalidConfigValueException Thrown if a unit has an invalid name.
	 * @exception MissingConfigurationException Thrown if a mandatory configuration setting is missing.
	 */
	public String[] getUnitNames() throws InvalidConfigValueException,MissingConfigurationException {
		// The unit names are obtained from the instance configuration, from the
		// "cfg.units" key, which is a comma-separated string.
		final String key = propConfigGlobalPrefix + propSepSymbol + "units";
		final String nameList = cfgInst.getProperty(key);
		if (nameList == null) {
			throw new MissingConfigurationException(key);
		}
		final String[] result = nameList.split(",\\s*");
		// check whether the units' names are valid
		final String destDir = getDestinationDirectory();
		for (String unitName : result) {
			if (!ConfigUtils.isFilenameValid(destDir, unitName)) {
				throw new InvalidConfigValueException(key, unitName,
						"unit name should be a valid directory name");
			}
		}
		return result;
	}

	//------------- UNIT CONFIG --------------------------------------------------
	/**
	 * Configuration provider for configurations of individual units
	 * stored in Java's {@code Properties} files.
	 */
	private class UnitPropertiesConfig implements UnitConfigProvider,JavaConfig {
		/** The name/identifier of the unit. */
		final private String unitName;

		/**
		 * Create a new {@link UnitPropertiesConfig} object.
		 *
		 * @param unitName The name/identifier of the unit.
		 */
		public UnitPropertiesConfig(final String unitName) {
			this.unitName = unitName;
		}

		//------------- UnitConfigProvider ---------------------------------------
		/**
		 * {@inheritDoc}
		 *
		 * The {@link UnitPropertiesConfig} implements the {@link UnitConfig}
		 * interface itself and returns itself here.
		 */
		public UnitConfig getUnitConfig() { return this; }

		/**
		 * {@inheritDoc}
		 *
		 * The {@link UnitPropertiesConfig} implements the {@link JavaConfig}
		 * interface itself and returns itself here.
		 */
		public JavaConfig getJavaConfig() { return this; }

		//------------- UnitConfig -----------------------------------------------
		/**
		 * {@inheritDoc}
		 *
		 * The name of the unit is provided to the object upon construction.
		 */
		public String getUnitName() {
			return unitName;
		}

		/**
		 * {@inheritDoc}
		 *
		 * The configuration is obtained from the unit's &quot;type&quot; configuration
		 * key. More precisely, the part in front of the &quot;:&quot; is taken.
		 *
		 * @see #getTypeOptions()
		 */
		public String getUnitType() throws MissingConfigurationException {
			// The unit type is configured by the "<unitname>.type" key, which is
			// of the format "<type> ':' <type-options>"
			final String[] unitType = propGetMandatoryUnitConfig(cfgInst, "type").split(":");
			return unitType[0]; // forget everything after ":"
		}

		/**
		 * {@inheritDoc}
		 *
		 * The configuration is obtained from the unit's &quot;type&quot; configuration
		 * key. More precisely, the part behind the &quot;:&quot; is taken and split
		 * into its comma-separated parts.
		 *
		 * @see #getUnitType()
		 */
		public Set<String> getTypeOptions() throws MissingConfigurationException {
			final String[] unitType = propGetMandatoryUnitConfig(cfgInst, "type").split(":");
			final HashSet<String> options;
			if (unitType.length == 1) {
				options = new HashSet<String>();
			} else {
				options = new HashSet<String>(Arrays.asList(unitType[1].split(",\\s*")));
			}
			return options;
		}

		/**
		 * {@inheritDoc}
		 *
		 * The configuration is obtained from the unit's &quot;ext-host&quot; (hostname
		 * or IP address) and &quot;ext-port&quot; (destination port of the connection)
		 * configuration keys.
		 *
		 * @throws MissingConfigurationException Thrown if either the internal external host or port is not configured.
		 */
		public PlainInetAddress getExternalAddress() throws MissingConfigurationException {
			return new PlainInetAddress(propGetMandatoryUnitConfig(cfgInst, "ext-host"),
					new Integer(propGetMandatoryUnitConfig(cfgInst, "ext-port")));
		}

		/**
		 * {@inheritDoc}
		 *
		 * This takes the default log level if no configuration value is found.
		 * Otherwise, it takes the value of the unit's &quot;loglevel&quot;
		 * configuration key.
		 *
		 * @see PropertiesConfig#defaultLogLevel
		 */
		public org.apache.log4j.Level getInstantiationLogLevel() {
			final String level = propGetUnitConfig(cfgInst, "loglevel").trim();
			if (level.isEmpty())
				return defaultLogLevel;// default value
			else
				return org.apache.log4j.Level.toLevel(level);
		}

		//------------- JavaCorConfig --------------------------------------------

		/**
		 * {@inheritDoc}
		 *
		 * The configuration is obtained from the unit's &quot;cor-host&quot; (hostname
		 * or IP address) and &quot;cor-port&quot; (destination port of the connection)
		 * configuration keys.
		 *
		 * @throws MissingConfigurationException Thrown if either the internal coordinator's host or its port is not configured.
		 */
		public PlainInetAddress getInternalCoordinatorAddress() throws MissingConfigurationException {
			return new PlainInetAddress(propGetMandatoryUnitConfig(cfgInst, "cor-host"),
					new Integer(propGetMandatoryUnitConfig(cfgInst, "cor-port")));
		}

		/**
		 * {@inheritDoc}
		 *
		 * The configuration is obtained from the unit's &quot;enf-host&quot; (hostname
		 * or IP address) and &quot;enf-port&quot; (destination port of the connection)
		 * configuration keys.
		 *
		 * @throws MissingConfigurationException Thrown if either the enforcer's host or its port is not configured.
		 */
		public PlainInetAddress getInternalEnforcerAddress() throws MissingConfigurationException {
			return new PlainInetAddress(propGetMandatoryUnitConfig(cfgInst, "enf-host"),
					new Integer(propGetMandatoryUnitConfig(cfgInst, "enf-port")));
		}

		/**
		 * {@inheritDoc}
		 *
		 * The configuration is obtained from the unit's &quot;target-javavm&quot;
		 * configuration key.
		 */
		public String getTargetSystemJavaVM() {
			return propGetUnitConfig(cfgInst, "target-javavm");
		}

		/**
		 * {@inheritDoc}
		 *
		 * @todo do not hard-code the returned value; make it configurable.
		 */
		public String getStartupTemplateFile() {
			return templatesPath + "JavaStarter";
		}

		/**
		 * {@inheritDoc}
		 *
		 * The configuration is obtained from the unit's &quot;policy&quot;
		 * configuration key.
		 */
		public String getLocalPolicyName() throws MissingConfigurationException {
			return propGetMandatoryUnitConfig(cfgInst, "policy");
		}

		/**
		 * {@inheritDoc}
		 *
		 * This collects all the &quot;sub-keys&quot; of the unit's configuration
		 * that are of the format {@code <unitname>.policy.<key>}.
		 */
		public Map<String,String> getLocalPolicyParameters() {
			Map<String,String> result = new HashMap<String,String>();

			// iterate through `cfgInst' (and `cfgComp'?) to match properties of
			// the form "<unitname>.policy.<key>" or "unit.poliicy.<key>"
			String k_unit = unitName + propSepSymbol + "policy" + propSepSymbol;
			String k_allunits = propConfigUnitPrefix + propSepSymbol + "policy" + propSepSymbol;
			for (Map.Entry<Object,Object> cfgElem : cfgInst.entrySet()) {
				String key = (String)cfgElem.getKey();
				if (key != null) { // we actually have a string key here
					String policy_key = null;
					if (key.startsWith(k_unit)) {
						policy_key = key.substring(k_unit.length());
					} else if (key.startsWith(k_allunits)) {
						policy_key = key.substring(k_allunits.length());
					} else
						continue; // irrelevant key

					result.put(policy_key, (String)cfgElem.getValue());
				}
			}
			return result;
		}

		//------------- JavaTargetConfig -----------------------------------------

		/**
		 * {@inheritDoc}
		 *
		 * The configuration is obtained from the unit's &quot;target&quot;
		 * configuration key.
		 */
		public String getJavaProgramJAR() throws MissingConfigurationException {
			return makeAbsolute(cfgInst, propGetMandatoryUnitConfig(cfgInst, "target"));
		}

		/**
		 * {@inheritDoc}
		 *
		 * The configuration is obtained from the unit's &quot;enforcer-factory&quot;
		 * configuration key.
		 */
		public String getEnforcerFactoryName() throws MissingConfigurationException {
			return propGetMandatoryUnitConfig(cfgInst, "enforcer-factory");
		}

		/**
		 * {@inheritDoc}
		 *
		 * The configuration is obtained from the unit's &quot;event-factory&quot;
		 * configuration key.
		 */
		public String getCriticalEventFactoryName() throws MissingConfigurationException {
			return propGetMandatoryUnitConfig(cfgInst, "event-factory");
		}


		//------------- JavaAspectJ Config ---------------------------------------
		/**
		 * {@inheritDoc}
		 *
		 * The configuration is obtained from the &quot;aspectj.compiler&quot;
		 * configuration key. That is, it is also the same for all units.
		 */
		public String getAspectJExecutable() throws MissingConfigurationException {
			return propGetAbsolute(cfgComp, "aspectj" + propSepSymbol + "compiler");
		}

		/**
		 * {@inheritDoc}
		 *
		 * The configuration is obtained from the &quot;aspectj.runtime&quot;
		 * configuration key. That is, it is also the same for all units.
		 */
		public String getAspectJRuntimeClasspath() throws MissingConfigurationException {
			return propGetAbsolute(cfgComp, "aspectj" + propSepSymbol + "runtime");
		}

		/**
		 * {@inheritDoc}
		 *
		 * The configuration is obtained from the &quot;java.jar&quot;
		 * configuration key. That is, it is also the same for all units.
		 */
		public String getJarExecutable() throws MissingConfigurationException {
			return propGetAbsolute(cfgComp, "java"    + propSepSymbol + "jar");
		}

		/**
		 * {@inheritDoc}
		 *
		 * The configuration is obtained from the unit's &quot;pointcuts&quot;
		 * configuration key.
		 */
		public String getInstrumentationPointcutSpecFile() throws MissingConfigurationException {
			return makeAbsolute(cfgInst, propGetMandatoryUnitConfig(cfgInst, "pointcuts"));
		}

		/**
		 * {@inheritDoc}
		 *
		 * @todo do not hard-code the returned value; make it configurable.
		 */
		public String getAspectJAspectTemplate() {
			return templatesPath + "JavaAspect";
		}

		/**
		 * {@inheritDoc}
		 *
		 * @todo do not hard-code the returned value; make it configurable.
		 */
		public String getAspectJAdviceTemplate() {
			return templatesPath + "JavaAdvice";
		}

		//------------- JavaConfig -----------------------------------------------

		/**
		 * {@inheritDoc}
		 *
		 * The configuration is obtained from the &quot;cliseau.runtime&quot;
		 * configuration key. That is, it is also the same for all units.
		 */
		public String getCliSeAuJavaRuntime() throws MissingConfigurationException {
			return propGetAbsolute(cfgComp, "cliseau" + propSepSymbol + "runtime");
		}

		/**
		 * {@inheritDoc}
		 *
		 * The configuration is obtained from the &quot;policy-classpath&quot;
		 * configuration key and extended by the internal dependencies of the
		 * coordinator/local policy implementation.
		 */
		public Collection<String> getJavaDependencyPaths() throws MissingConfigurationException {
			// get policy class path,
			// add mandatory dependencies (through Coordinator implementation)
			String policyClasspath = propGetUnitConfig(cfgInst, "policy-classpath");
			List<String> requiredDeps = new ArrayList<String>(Arrays.asList(JavaUnitInstantiation.requiredDependencies));
			if (!policyClasspath.isEmpty()) {
				requiredDeps.add(0, policyClasspath); // prepend
			}
			return getAbsoluteDeps(StringUtils.join(requiredDeps, System.getProperty("path.separator")));
		}

		/**
		 * {@inheritDoc}
		 *
		 * The configuration is obtained from the unit's &quot;inline-classpath&quot;
		 * configuration key.
		 */
		public Collection<String> getJavaInstantiationJARs() throws MissingConfigurationException {
			return getAbsoluteDeps(propGetMandatoryUnitConfig(cfgInst, "inline-classpath"));
		}

		/**
		 * Retrieve log level including and above which all runtime enforcement messages are displayed.
		 *
		 * Currently, this implementation returns the same log level for the
		 * runtime behavior as for the instantiation time. If more flexibility
		 * should be needed in the future, the configuration could be enriched
		 * to provide different configuration elements for instantiation-time
		 * and run-time logging.
		 *
		 * @see #getInstantiationLogLevel()
		 */
		public org.apache.log4j.Level getJavaRuntimeLogLevel() {
			return getInstantiationLogLevel();
		}

		//------------- INTERNAL METHODS -----------------------------------------
		/**
		 * Convert a list of possibly abbreviated dependencies into absolute ones.
		 *
		 * Abbreviated dependencies of name X must be given in the configuration cfgComp
		 * with key "libs.X" and are expanded by the respective value.
		 *
		 * @param deps Single string holding the concatenated dependencies.
		 * @return List of absolute dependencies.
		 */
		private List<String> getAbsoluteDeps(final String deps)
				throws MissingConfigurationException {
			if (deps.trim().isEmpty()) { // no dependencies --> empty list
				return new ArrayList<String>();
			}

			// split dependencies
			String[] depsList = deps.split(System.getProperty("path.separator"));

			// update library references based on cfgComp
			for (int i=0; i<depsList.length; ++i) {
				if (!depsList[i].matches(".*[.]jar")) { // does not end with ".jar"? --> lib
					depsList[i] = propGetAbsolute(cfgComp, "libs." + depsList[i]);
				} else {
					depsList[i] = makeAbsolute(cfgInst, depsList[i]);
				}
			}
			return Arrays.asList(depsList);
		}

		/**
		 * Fetch a configuration item from a properties file.
		 *
		 * This method checks whether the key "identifier.key" is
		 * specified in the properties file. If so, the corresponding
		 * value is returned. If not, then "PREFIX.key" is fetched,
		 * where the prefix is the default unit prefix (configured by
		 * the static "propConfigUnitPrefix" string).
		 *
		 * @param props Properties object holding the full configuration.
		 * @param key Key name of the configuration property to fetch.
		 * @return Value stored for the key, if existing, or null otherwise.
		 */
		private String propGetUnitConfig(final Properties props, final String key) {
			String result = props.getProperty(unitName + propSepSymbol + key);
			if (result == null) {
				result = props.getProperty(propConfigUnitPrefix + propSepSymbol + key);
			}
			return result;
		}

		/**
		 * Fetch a mandatory configuration item from a properties file.
		 *
		 * This is the same as propGetUnitConfig, but it throws an exception
		 * if the configuration element is not found in the configuration.
		 *
		 * @param props Properties object holding the full configuration.
		 * @param key Key name of the configuration property to fetch.
		 * @return Value stored for the key, if existing, or null otherwise.
		 * @exception MissingConfigurationException Thrown if the configuration is not found.
		 * @todo ensure that this method is used at the appropriate places
		 *       instead of {@link #propGetUnitConfig(Properties, String)}!
		 */
		private String propGetMandatoryUnitConfig(final Properties props, final String key)
				throws MissingConfigurationException {
			final String result = propGetUnitConfig(props, key);
			if (result == null) {
				throw new MissingConfigurationException(key);
			} else {
				return result;
			}
		}
	}

	//------------- INTERNAL METHODS ---------------------------------------------

	/**
	 * Fetch a configuration path from a properties file and return its absolute form.
	 *
	 * @param props Properties object holding the full configuration.
	 * @param key Key name of the configuration property to fetch.
	 */
	private static String propGetAbsolute(final Properties props, final String key)
			throws MissingConfigurationException {
		final String path = props.getProperty(key);
		if (path == null) {
			throw new MissingConfigurationException(key);
		}
		return makeAbsolute(props, path);
	}

	/**
	 * Make a path absolute with respect to a particular configuration.
	 *
	 * @param props Properties object holding the full configuration.
	 * @param path Possibly relative path to be made absolute.
	 */
	private static String makeAbsolute(final Properties props, final String path)
			throws MissingConfigurationException {
		File f = new File(path);
		if (f.isAbsolute()) // absolute paths are simply returned
			return f.getPath();
		else { // relative paths must be made absolute using on a chosen base directory
			// note that the "basedir" property is set in the constructor
			// of the class
			return (new File((File)props.get("basedir"), f.getPath())).getPath();
		}
	}
}
