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
package test.cliseau.inst;

import net.cliseau.runtime.javacor.EnforcementDecision;
import net.cliseau.runtime.javacor.CriticalEvent;
import net.cliseau.runtime.javatarget.Enforcer;
import net.cliseau.runtime.javatarget.EnforcerFactory;

/**
 * Simple test implementation of an Enforcer and an EnforcerFactory.
 *
 * Essentially, this Enforcer does not intervene into the program execution and
 * only shows some outputs to the console to indicate its activity.
 */
public class TestEnforcer implements Enforcer,EnforcerFactory {
	/**
	 * Create an enforcer for a given EnforcementDecision.
	 *
	 * This is part of the EnforcerFactory implementation.
	 * @param ed The enforcement decision, which is ignored.
	 * @return Created Enforcer object
	 */
	public static Enforcer fromDecision(final EnforcementDecision ed) {
		return new TestEnforcer();
	}

	/**
	 * Create a fallback enforcer for a given CriticalEvent.
	 *
	 * This is part of the EnforcerFactory implementation.
	 * @param ev The critical event, which is ignored.
	 * @return Created Enforcer object
	 */
	public static Enforcer fallback(final CriticalEvent ev) {
		return new TestEnforcer();
	}

	/**
	 * Code to execute before running the causing event.
	 *
	 * Here, only a message is output to the console.
	 */
	public void before() {
		System.out.println("before");
	}

	/**
	 * Determine whether the causing event has to be suppressed.
	 *
	 * In this simple example instantiation, we never suppress.
	 *
	 * @return False, since the causing event never shall be suppressed.
	 */
	public boolean suppress() {
		return false;
	}

	/**
	 * Determine and return the supposed return value of the enforcer.
	 *
	 * @param c Class (type) of the expected return value.
	 * @return Return value
	 * @todo When Enforcers are simplified, this kind of hackery will probably
	 *       no longer be necessary. */
	public Object getReturnValue(Class c) {
		if (c.equals(Integer.class)) {
			return new Integer(0);
		} else {
			return null;
		}
	}

	/**
	 * Code to execute after running the causing event.
	 *
	 * Here, only a message is output to the console.
	 */
	public void after() {
		System.out.println("after");
	}
}
