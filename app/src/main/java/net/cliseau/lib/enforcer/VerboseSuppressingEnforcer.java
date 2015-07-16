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
package net.cliseau.lib.enforcer;

import net.cliseau.lib.enforcer.SuppressingEnforcer;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

/**
 * This class implements a very simple enforcer that suppresses the
 * security-relevant event and produces a console output before.
 *
 * This enforcer is intended to be used for debugging or
 * demonstration purposes. It requires "jansi" to be installed
 * and to be provided to the configuration file of the instantiation.
 * Example:
 *    &lt;unitname&gt;.inline-classpath = ...:std-enforcers:jansi
 */
public class VerboseSuppressingEnforcer extends SuppressingEnforcer {
	/** Informational message describing the operation of the enforcer */
	private final String msg;

	/**
	 * Construct a VerboseSuppressingEnforcer object.
	 *
	 * @param msg An information message to be shown by this enforcer.
	 */
	public VerboseSuppressingEnforcer(final String msg) {
		super();
		this.msg = msg;
	}

	/**
	 * This method shows some colored log output before permitting the event.
	 */
	@Override public void before() {
		AnsiConsole.out.println(Ansi.ansi()
				.a(Ansi.Attribute.INTENSITY_BOLD)
				.a("Enforcer: ")
				.fg(Ansi.Color.RED)
				.a(msg).reset());
	}
}

