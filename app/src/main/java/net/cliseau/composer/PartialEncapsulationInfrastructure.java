/* Copyright (c) 2011-2012 Richard Gay <gay@mais.informatik.tu-darmstadt.de>
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
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.zip.*;
import net.cliseau.composer.ExternalUnitAddresses;
import net.cliseau.composer.UnitInstantiation;
import net.cliseau.composer.PlainInetAddress;
import net.cliseau.composer.javatarget.JavaUnitInstantiation;
import net.cliseau.composer.config.base.ConfigProvider;
import net.cliseau.composer.config.base.UnitConfigProvider;
import net.cliseau.composer.config.base.UnitConfig;
import net.cliseau.composer.config.base.InvalidConfigurationException;
import org.apache.log4j.BasicConfigurator; // for debugging
import org.apache.log4j.Level; // for debugging
import org.apache.log4j.Logger; // for debugging

/**
 * Class for building encapsulation infrastructures and generating encapsulated systems.
 *
 * This class acts as a container for unit instantiations, associating unit
 * names with their instantiations. When all units of the distributed
 * system to be encapsulated have been added to a
 * PartialEncapsulationInfrastructure object, then the encapsulated distributed
 * system can be generated from the object.
 *
 * @see UnitInstantiation
 */
public class PartialEncapsulationInfrastructure {
	/**
	 * Stores a mapping from unit names to the instantiation information
	 * of the units corresponding to these unit names.
	 */
	private HashMap<String, UnitInstantiation> unitInst;

	/**
	 * Stores the external addressing information as a mapping from unit names to addresses.
	 */
	private ExternalUnitAddresses addressing;

	/** Stores the name of the directory to which the instantiations shall be written. */
	private String destinationDirectory;

	/**
	 * Create a new, empty PartialEncapsulationInfrastructure.
	 */
	public PartialEncapsulationInfrastructure() {
		unitInst = new HashMap<String, UnitInstantiation>();
		addressing = new ExternalUnitAddresses();
	}

	/**
	 * Create a new PartialEncapsulationInfrastructure based on a given configuration.
	 *
	 * This constructor creates a PartialEncapsulationInfrastructure object
	 * based on a given configuration. That is, it constructs the architecture
	 * of units as well as the units themselves from the given configuration.
	 */
	public PartialEncapsulationInfrastructure(final ConfigProvider config)
			throws InvalidConfigurationException {
		this(); // perform basic initialization

		// logging
		final Logger logger = Logger.getLogger("CreateInfra");

		setDestinationDirectory(config.getDestinationDirectory());

		// get the units to be configured
		final String[] unitNames = config.getArchitectureConfig().getUnitNames();

		// create units
		for (String name : unitNames) {
			final UnitConfigProvider unitConfig = config.getUnitConfigProvider(name);
			final UnitConfig unitBaseConfig = unitConfig.getUnitConfig();
			final String unitType = unitConfig.getUnitConfig().getUnitType();
			//TODO: maybe swap out the following case distinction to a separate method
			//      to have a clear, namable extension point for new target languages.
			if ("Java".equals(unitType)) {
				addUnit(unitBaseConfig.getUnitName(), unitBaseConfig.getExternalAddress(),
						new JavaUnitInstantiation(unitConfig));
			} else {
				//TODO: throw an exception (UnsupportedUnitTypeException)
			}
		}
	}

	/**
	 * Add a unit to the infrastructure.
	 *
	 * @param unitName The name of the unit to be added.
	 * @param extAddress The address under which the unit is reachable from external units.
	 * @param inst The instance describing the unit.
	 */
	public void addUnit(final String unitName, final PlainInetAddress extAddress,
			final UnitInstantiation inst) {
		if (unitInst.get(unitName) == null) {
			unitInst.put(unitName, inst);
			addressing.put(unitName, extAddress);
		} else {
			throw new IllegalArgumentException("Name of the unit to be added is already known!");
		}
	}

	/**
	 * Generate an encapsulated system.
	 *
	 * @param dirName Destination directory for the resulting instantiation.
	 * @exception UnitGenerationException Thrown if the generation of a unit failed.
	 * @exception IOException Thrown if the output of a generated unit to disk failed.
	 * @deprecated Use setDestinationDirectory(dir);generate(); instead
	 */
	public void generate(final String dirName)
			throws IOException,UnitGenerationException {
		setDestinationDirectory(dirName);
		generate();
	}

	/**
	 * Generate an encapsulated system.
	 *
	 * Based on the units added to this PartialEncapsulationInfrastructure, this
	 * method generates an encapsulated system. Such an encapsulated system
	 * comprises, for each unit, all the files required for running the
	 * encapsulated unit. The precise set of files depends on the target
	 * platform and encapsulation strategy (e.g., instrumentation versus a
	 * modified VM or interpreter). Common files are the target program itself
	 * and a program that starts the CliSeAu unit with the target program. A
	 * modified VM could also be contained in the set of files.
	 *
	 * @exception UnitGenerationException Thrown if the generation of a unit failed.
	 * @exception IOException Thrown if the output of a generated unit to disk failed.
	 */
	public void generate()
			throws IOException,UnitGenerationException {
		// Step 1. Set up the directory to hold the partial encapsulation structure
		final File instDir = new File(getDestinationDirectory());
		instDir.mkdir();

		// Step 2. For each unit, perform the generation of the instrumented
		//         unit into a separate directory.
		for (Map.Entry<String,UnitInstantiation> unit : unitInst.entrySet()) {
			final String unitName = unit.getKey();
			final UnitInstantiation inst = unit.getValue();
			final String unitDir = instDir.getPath() + File.separator + unitName + File.separator;
			new File(unitDir).mkdir();
			inst.generateUnit(addressing, unitDir);
		}
	}

	/**
	 * Get number of units in the infrastructure.
	 *
	 * @return number of units in infrastructure.
	 */
	public int getNumberOfUnits()
	{
		return unitInst.size();
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
	 * Set destinationDirectory.
	 *
	 * @param destinationDirectory the value to set.
	 */
	public void setDestinationDirectory(String destinationDirectory)
	{
		this.destinationDirectory = destinationDirectory;
	}
}
