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

import java.io.IOException;
import java.io.Serializable;
import net.cliseau.composer.ExternalUnitAddresses;
import net.cliseau.composer.UnitGenerationException;
import net.cliseau.composer.PlainInetAddress;
import net.cliseau.composer.config.base.UnitConfig;

/**
 * Base interface for all unit instantiations.
 *
 * This interface provides the method that must be implemented by every unit
 * implementation (for some target language) for generating the instantiated unit.
 *
 * @see PartialEncapsulationInfrastructure
 */
public interface UnitInstantiation {
	/**
	 * Generate all required files for the encapsulated unit.
	 *
	 * @param addresses The external addresses of all units, as the instantiation
	 *                  must know how to contact the other units in the system.
	 * @param dirName The directory into which the files for the unit are to be written.
	 * @exception UnitGenerationException Shall be thrown by implementations if the generation of the unit failed.
	 * @exception IOException Shall be thrown by implementations if the output of the generated unit to disk failed.
	 */
	// Idea is to write the information about the unit into the specified directory
	public abstract void generateUnit(final ExternalUnitAddresses addresses, final String dirName)
		throws IOException,UnitGenerationException;
}
