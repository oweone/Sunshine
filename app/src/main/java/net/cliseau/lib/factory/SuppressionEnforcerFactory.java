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
package net.cliseau.lib.factory;

import net.cliseau.lib.datatypes.BinaryDecision;
import net.cliseau.lib.enforcer.PermittingEnforcer;
import net.cliseau.lib.enforcer.SuppressingEnforcer;
import net.cliseau.runtime.javacor.EnforcementDecision;
import net.cliseau.runtime.javacor.CriticalEvent;
import net.cliseau.runtime.javatarget.Enforcer;
import net.cliseau.runtime.javatarget.EnforcerFactory;

/**
 * Factory class for constructing
 * {@link net.cliseau.lib.enforcer.SuppressingEnforcer}s
 * from {@link net.cliseau.lib.datatypes.BinaryDecision}s.
 */
public class SuppressionEnforcerFactory implements EnforcerFactory {
	/**
	 * Create a suitable Enforcer for a given EnforcementDecision.
	 *
	 * @param ed The EnforcementDecision from which the Enforcer is constructed.
	 * @return The constructed Enforcer.
	 */
	public static Enforcer fromDecision(final EnforcementDecision ed) {
		BinaryDecision ted = (BinaryDecision) ed;
		if (ted != null) {
			switch (ted) {
				case PERMIT: return new PermittingEnforcer();
				case REJECT: return new SuppressingEnforcer();
			}
		}
		return new SuppressingEnforcer();// be conservative
	}

	/**
	 * Create a fallback Enforcer for a given CriticalEvent.
	 *
	 * Here we always conservatively suppress the program event in case of an
	 * error. In principle, however, we could use a more optimistic approach.
	 *
	 * @param ev The CriticalEvent from which the Enforcer is constructed.
	 * @return The constructed Enforcer.
	 */
	public static Enforcer fallback(final CriticalEvent ev) {
		return new SuppressingEnforcer();
	}
}
