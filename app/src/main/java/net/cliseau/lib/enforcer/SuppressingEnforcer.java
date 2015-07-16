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
package net.cliseau.lib.enforcer;

import net.cliseau.runtime.javatarget.Enforcer;

/**
 * This class implements a very simple enforcer that suppresses the
 * security-relevant event.
 *
 * Caution: If the suppressed event was a call to a method with a return value,
 * this return value is replaced by null. This may lead to unexpected/unintended
 * behavior, if the target program does not expect null at the respective point
 * in the execution.
 */
public class SuppressingEnforcer implements Enforcer {
	/** This method does nothing before permitting the event. */
	@Override public void before() { }
	/** This method always returns false, indicating that the event should not be suppressed. */
	@Override public boolean suppress() { return true; }
	/** This method does nothing after permitting the event. */
	@Override public void after() { }

	/**
	 * This method returns a null object always.
	 *
	 * @return Always null.
	 */
	@Override public Object getReturnValue(Class c) {
		return null;
	}
}
