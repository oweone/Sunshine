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

import java.util.Set;
import net.cliseau.composer.PlainInetAddress;
import net.cliseau.composer.config.base.InvalidConfigurationException;

/**
 * Interface for obtaining basic unit-specific configuration information.
 *
 * This interface must be implemented for every unit configuration provider
 * (i.e., every implementation of {@link UnitConfigProvider}) such that the
 * basic unit-specific configuration information can be obtained.
 *
 * This interface should be extended by a new interface whenever a new target
 * system type is added (e.g., through a new target programming language or
 * a new architecture for the target system).
 *
 * @see net.cliseau.composer.config.target.JavaCorConfig
 * @see net.cliseau.composer.config.target.JavaConfig
 */
public interface UnitConfig {
	/**
	 * Retrieve the name of the unit.
	 *
	 * Units are identified by their name. Hence, these names must be unique.
	 *
	 * @return The name of the unit.
	 */
	public String getUnitName();

	/**
	 * Retrieve the type of the unit.
	 *
	 * Conceptually, the type of a unit is a string that identifies the
	 * format in which the target is given (e.g., a particular machine or
	 * bytecode language) as well as potentially relevant information about
	 * the environment in which the target is supposed to be run (e.g.,
	 * the hardware achitecture or the OS). Technically, the type of a unit
	 * determines which subclass of {@link net.cliseau.composer.UnitInstantiation}
	 * is used to perform the encapsulation of the unit's target.
	 *
	 * @return String defining the type of the unit.
	 * @exception InvalidConfigurationException Thrown in case of a missing or invalid configuration value.
	 */
	public String getUnitType() throws InvalidConfigurationException;

	/**
	 * Retrieve the set of provided options for the unit's type.
	 *
	 * Type options allow for further configuring the type of unit
	 * at a more fine-grained level. Conceptually, it is assumed that
	 * for each unit type there is one subclass of
	 * {@link net.cliseau.composer.UnitInstantiation}
	 * and the type options are used by this subclass to perform a
	 * rather fine-grained configuration of the instantiation.
	 *
	 * @return The set of provided options for the unit's type.
	 * @exception InvalidConfigurationException Thrown in case of a missing or invalid configuration value.
	 */
	public Set<String> getTypeOptions() throws InvalidConfigurationException;

	/**
	 * Retrieve the address of the unit, through which the unit can be contacted by remote units.
	 *
	 * The key concept behind CliSeAu is that the individual units
	 * can cooperate with each other. Hence, a unit must be able to contact
	 * other, remote units. For this, every unit must implement a socket through
	 * which the unit accepts requests as well as responses by other units.
	 * The getExternalAddress method returns the address through which remote
	 * units can connect to the unit.
	 *
	 * Note that currently every unit has only one address for being contacted
	 * by other units. This is because so far no need was seen to establish
	 * different sockets for different units.
	 *
	 * @return The address of the unit, through which the unit can be contacted by remote units.
	 * @exception InvalidConfigurationException Thrown in case of a missing or invalid configuration value.
	 */
	public PlainInetAddress getExternalAddress() throws InvalidConfigurationException;

	/**
	 * Retrieve the log level to use when building the instrumented unit.
	 *
	 * @exception InvalidConfigurationException Thrown in case of a missing or invalid configuration value.
	 */
	public org.apache.log4j.Level getInstantiationLogLevel() throws InvalidConfigurationException;
}
