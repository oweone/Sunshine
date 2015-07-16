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

import net.cliseau.runtime.javacor.CriticalEvent;
import net.cliseau.runtime.javatarget.CriticalEventFactory;

/**
 * A very basic implementation of a CriticalEvent. This critical event contains
 * only a name that identifies the event - in simple practical settings this
 * could reflect, e.g., the name of a method that is about to be called by the
 * monitored target.
 *
 * For simplicity, this class combines a CriticalEvent and a
 * CriticalEventFactory. The only factory method is {@link #stringOp(String)
 * stringOp}.
 */
public class TestCriticalEvent implements CriticalEvent,CriticalEventFactory {
	/** Just something to identify the event. */
	public String testName;

	/**
	 * Initialize the instance's name.
	 * @param name Just some name to identify the event
	 */
	private TestCriticalEvent(final String name) {
		this.testName = name;
	}

	/**
	 * Factory method for a CriticalEvent corresponding to a String operation.
	 *
	 * We simply create a TestCriticalEvent and pass the value of the name
	 * parameters to the constructor of the class.
	 *
	 * @param name The name/identifier of the CriticalEvent to construct.
	 * @return Generated CriticalEvent object.
	 */
	public static CriticalEvent stringOp(final String name) {
		return new TestCriticalEvent(name);
	}
}
