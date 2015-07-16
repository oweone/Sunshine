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

/** A very basic implementation of an EnforcementDecision */
public class TestEnforcementDecision implements EnforcementDecision {
	/** Just something to identify the trigger event */
	public String testName;
	/** Some string representing the decision */
	public String testDec;

	/**
	 * Initialize the instance's event name and decision info.
	 * @param name The name identifying the trigger event
	 * @param dec  The decision description
	 */
	public TestEnforcementDecision(final String name, final String dec) {
		this.testName = name;
		this.testDec  = dec;
	}
}
