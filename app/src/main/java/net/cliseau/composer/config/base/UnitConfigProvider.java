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
package net.cliseau.composer.config.base;

import net.cliseau.composer.config.base.UnitConfig;
import net.cliseau.composer.config.target.JavaConfig;

/**
 * Basic interface for concrete providers of unit configurations.
 *
 * A {@link UnitConfigProvider} must be able to return basic unit
 * configuration as well as unit configuration for the specific
 * unit types (e.g., {@link JavaConfig} for a unit that addresses
 * a Java target program).
 *
 * This class should contain one individual getter for every type
 * of target.
 *
 * @see net.cliseau.composer.PartialEncapsulationInfrastructure
 * @see net.cliseau.composer.config.format.PropertiesConfig
 */
public interface UnitConfigProvider {
	/**
	 * Return a configuration object that captures the basic
	 * configuration of the unit.
	 *
	 * @return Configuration object that captures the basic configuration of the unit.
	 */
	public UnitConfig getUnitConfig();

	// language-specific configurations follow

	/**
	 * Retrieve the configuration for a Java unit.
	 *
	 * @return Configuration object for a Java unit.
	 */
	public JavaConfig getJavaConfig();
}
