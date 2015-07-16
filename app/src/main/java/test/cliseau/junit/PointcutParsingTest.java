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
package test.cliseau.junit;

import org.junit.*;
import static org.junit.Assert.*;
import java.io.StringReader;
import net.cliseau.composer.javatarget.PointcutParser.PointcutParser;
import net.cliseau.composer.javatarget.PointcutParser.ParseException;
import net.cliseau.composer.javatarget.PointcutParser.Pointcut;
import net.cliseau.composer.javatarget.PointcutParser.PointcutSpec;
import org.apache.log4j.BasicConfigurator; // for debugging
import org.apache.log4j.Logger; // for debugging

/**
 * This class tests the pointcut parsing capabilities of PointcutParser.
 *
 * The code structure has partially been inspired by
 *   http://stackoverflow.com/questions/1083056/external-data-file-for-unit-tests
 * An external file would be more interesting, but to avoid the hastle of
 * search path issues, I refused to do it that way.
 *
 * @see PointcutParser
 * @todo Should some negative tests be added?
 */
public class PointcutParsingTest {
	/** A Logger to get some information about the parse result. */
	private Logger logger;

	/**
	 * Method running multiple tests for valid pointcut strings.
	 *
	 * A collection of pointcut declarations, mainly collected from the AspectJ
	 * documentation and slightly adapted to run through @em ajc, is parsed here.
	 * Currently, this method parses only valid strings, expecting parse success.
	 * Negative (invalid) strings are not tested here.
	 *
	 * @exception ParseException Thrown if parsing fails */
	@Test
	public void testParser()
			throws ParseException {
		BasicConfigurator.configure();
		logger = Logger.getLogger(this.getClass());

		// from: http://www.eclipse.org/aspectj/doc/released/progguide/language-advice.html
		assertValidPointcut("pointcut setter(Point p1, int newval): target(p1) && args(newval) && (call(void setX(int)) || call(void setY(int)));");

		// from: http://www.eclipse.org/aspectj/doc/released/progguide/semantics-advice.html
		// (modified for return type)
		assertValidPointcut("pointcut retval(int i > int): call(int StringBuffer.lastIndexOf(String, int)) && args(i);");

		// from: http://eclipse.org/aspectj/doc/released/progguide/semantics-pointcuts.html
		// (some with modifications since AspectJ even could not work with them)
		assertValidPointcut("pointcut publicIntCall(int i): call(public * *(int)) && args(i);");
		assertValidPointcut("pointcut publicCallone(long i): call(public void Object.wait(long)) && args(i);");
		assertValidPointcut("pointcut publicCalltwo(): call(public void Object.wait(long));");
		assertValidPointcut("pointcut publicCallthree(): call(public * *(..)) && args(Object);");
		assertValidPointcut("pointcut writing(): call(void println(String)) && !within(Point);");
		assertValidPointcut("pointcut throwsMathlike(): call(* *(..) throws *..*Math*);");
		assertValidPointcut("pointcut doesNotThrowMathlike(): call(* *(..) throws !*..*Math*);");
	}

	/**
	 * Try to parse a pointcut string, failing with an exception in case of an error.
	 * @param pointcut A pointcut expression to test the parsing
	 * @exception ParseException Thrown if the parsing fails
	 */
	private void assertValidPointcut(String pointcut)
			throws ParseException {
		PointcutParser parser = new PointcutParser(new StringReader(pointcut));
		PointcutSpec pspec = parser.PointcutSpecification();
		for (Pointcut pc : pspec.getPointcuts()) {
			logger.debug("Identifier: " + pc.getName());
			for (Pointcut.ParamData pd : pc.getParameters()) {
				logger.debug("  param<" + pd.Type + ">: " + pd.Name);
			}
			logger.debug("  returns " + pc.getReturnType());
		}
	}

	/** Just a main function to invoke the test from command line */
	public static void main(String args[]) {
		org.junit.runner.JUnitCore.main("test.cliseau.junit.PointcutParsingTest");
	}
}
