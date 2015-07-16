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
package net.cliseau.composer.config.target;

import java.util.Map;
import net.cliseau.composer.PlainInetAddress;
import net.cliseau.composer.config.base.UnitConfig;
import net.cliseau.composer.config.base.InvalidConfigurationException;

/**
 * Interface for configurations of units whose coordinator is implemented in Java.
 *
 * This interface focuses on the configuration elements relevant for
 * the coordinator and the local policy. The interface neglects all
 * configuration aspects related to intercepting events and enforcing
 * decisions.
 */
public interface JavaCorConfig extends UnitConfig {
	/**
	 * Retrieve fully qualified name of the class to be used for the local policy of the unit.
	 *
	 * @return The fully qualified name of the class to be used for the local policy of the unit.
	 */
	public String getLocalPolicyName() throws InvalidConfigurationException;

	/**
	 * Retrieve the parameters to be passed to the local policy of the unit.
	 *
	 * @return A map object mapping configuration keys to configuration values.
	 */
	public Map<String,String> getLocalPolicyParameters() throws InvalidConfigurationException;

	/**
	 * Retrieve the internal address with which the interceptor can contact the coordinator.
	 *
	 * @return Internal address of the coordinator
	 */
	public PlainInetAddress getInternalCoordinatorAddress() throws InvalidConfigurationException;

	/**
	 * Retrieve the internal address with which the coordinator can contact the enforcer.
	 *
	 * @return Internal address of the enforcer
	 */
	public PlainInetAddress getInternalEnforcerAddress() throws InvalidConfigurationException;

	/**
	 * Retrieve the path to the Java VM executable on the target system of the unit.
	 *
	 * @return Path to the Java VM executable on the target system of the unit.
	 */
	public String getTargetSystemJavaVM() throws InvalidConfigurationException;

	/**
	 * File name of the unit startup template (without "st" extension).
	 *
	 * @return Name of the unit startup template.
	 */
	public String getStartupTemplateFile() throws InvalidConfigurationException;

	/**
	 * Retrieve log level including and above which all runtime enforcement messages are displayed.
	 *
	 * @return Log level including and above which all runtime enforcement messages are displayed.
	 */
	public org.apache.log4j.Level getJavaRuntimeLogLevel() throws InvalidConfigurationException;
}
