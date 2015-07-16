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
package net.cliseau.composer.javatarget.PointcutParser;

import java.util.LinkedList;
import net.cliseau.composer.javatarget.PointcutParser.Pointcut;

/**
 * Data type for pointcut specifications.
 *
 * A pointcut specification is a collection of pointcut declarations and import
 * declarations. The latter are represented by the string identifying the
 * imported class(s).
 *
 * @see Pointcut
 * @see PointcutParser
 */
public class PointcutSpec {
	/** List of pointcut declarations */
	private LinkedList<Pointcut> pointcuts;
	/** List of imported classes */
	private LinkedList<String> imports;

	/** Construct a new PointcutSpec object with empty pointcut and import declaration lists. */
	public PointcutSpec() {
		pointcuts = new LinkedList<Pointcut>();
		imports   = new LinkedList<String>();
	}

	/** Add a pointcut declaration to the existing list */
	public void addPointcut(Pointcut pointcut) { pointcuts.addLast(pointcut); }
	/** Return the list of pointcut declarations */
	public LinkedList<Pointcut> getPointcuts() { return pointcuts; }

	/** Add an imported class */
	public void addImport(final String imp) { imports.addLast(imp); }
	/** Get the list of import declarations */
	public LinkedList<String> getImports() { return imports; }
}
