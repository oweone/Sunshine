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

import net.cliseau.composer.config.base.UnitConfig;
import net.cliseau.composer.config.base.InvalidConfigurationException;

/**
 * Basic interface for configurations of units with Java target programs.
 *
 * This interface unites methods for querying basic configuration
 * options that affect the target program and the immediately related
 * components of units (i.e., the event factory and the enforcer
 * factory).
 */
public interface JavaTargetConfig extends UnitConfig {
	/**
	 * Retrieve the path to the program JAR file on the current machine.
	 */
	public String getJavaProgramJAR() throws InvalidConfigurationException;

	/**
	 * Retrieve fully qualified name of the factory class used by the instantiation for creating Enforcer objects.
	 */
	public String getEnforcerFactoryName() throws InvalidConfigurationException;

	/**
	 * Retrieve fully qualified name of the factory class used by the instantiation for creating CriticalEvent objects.
	 */
	public String getCriticalEventFactoryName() throws InvalidConfigurationException;
}
