/* Copyright (c) 2013-2014 Richard Gay <gay@mais.informatik.tu-darmstadt.de>
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

import net.cliseau.lib.enforcer.SuppressingEnforcer;

/**
 * This class implements a very simple enforcer that replaces method calls
 * (to methods with a return value) by a given substitute return value.
 */
public class ValueReplacingEnforcer<ReturnType> extends SuppressingEnforcer {
	/** The value with which the return value shall be replaced. */
	private final ReturnType replacementValue;

	/**
	 * Construct a new ValueReplacingEnforcer object
	 *
	 * @param replacementValue The value with which the return value shall be replaced.
	 */
	public ValueReplacingEnforcer(final ReturnType replacementValue) {
		this.replacementValue = replacementValue;
	}

	/**
	 * This method returns the given replacement value.
	 *
	 * Note in particular that the replacement value does not depend on the
	 * given Class c.
	 *
	 * @return The given replacement value.
	 */
	@Override public Object getReturnValue(Class c) {
		return replacementValue;
	}
}
