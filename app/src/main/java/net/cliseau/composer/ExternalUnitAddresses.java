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
package net.cliseau.composer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.cliseau.composer.PlainInetAddress;

/**
 * Class for storing external addresses of CliSeAu units.
 *
 * Objects of this class are used during the generation of encapsulated units:
 * each UnitInstantiation only stores its own addresses but not those of the
 * other units. Hence, when generating an encapsulated system from a
 * PartialEncapsulationInfrastructure object, each UnitInstantiation object is
 * given an ExternalUnitAddresses object. This allows the UnitInstantiation to
 * create, in the case of a target with a Java coordinator, a
 * CoordinatorAddressing object.
 *
 * @see net.cliseau.composer.UnitInstantiation
 * @see net.cliseau.composer.PartialEncapsulationInfrastructure
 * @see net.cliseau.runtime.javacor.CoordinatorAddressing
 */
public class ExternalUnitAddresses {
	/**
	 * Mapping from CliSeAu unit identifiers to their external addresses.
	 */
	private HashMap<String, PlainInetAddress> unitAddr;

	/**
	 * Create a new ExternalUnitAddresses object with an empty set of addresses.
	 */
	public ExternalUnitAddresses() {
		unitAddr = new HashMap<String, PlainInetAddress>();
	}

	/**
	 * Set the external address of the CliSeAu unit with a given identifier.
	 *
	 * @param identifier Identifier whose address to add or set.
	 * @param addr The (new) external address of the CliSeAu unit.
	 */
	public void put(final String identifier, final PlainInetAddress addr) {
		unitAddr.put(identifier, addr);
	}

	/**
	 * Return the set of entries in the address list.
	 *
	 * @return List of stored (identifier, address) pairs.
	 * @todo The complex return type might indicate that his approach for
	 *       operating on the unitAddr field is not the best; particularly, it
	 *       should be ensured that this method does not permit the caller to
	 *       modify the internal data structure afterwards.
	 */
	public Set<Map.Entry<String,PlainInetAddress>> entrySet() {
		return unitAddr.entrySet();
	}
}
