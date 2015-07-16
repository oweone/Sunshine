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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import net.cliseau.runtime.javacor.CoordinatorAddressing;

/**
 * This class tests CoordinatorAddressing functionality.
 */
public class AddressingTest {
	/** An address to use (only to have an existing object, not for real data exchange. */
	private InetAddress addr;
	/** An arbitrarily chosen port number for an InetSocketAddress. */
	private static int port = 1234;
	/** An addressing object to be tested. */
	private CoordinatorAddressing ca;

	/**
	 * Set up test data structures.
	 *
	 * Before the tests, the setUp function is supposed to be executed. It
	 * initializes the private fields of the class by determining the InetAddress
	 * of the local host and by allocating an CoordinatorAddressing object which is
	 * tested by the tests.
	 */
	@Before
	public void setUp() {
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {}
		if (addr == null) {
			try {
				addr = InetAddress.getByName("localhost");
			} catch (UnknownHostException e) {}
		}
		ca = new CoordinatorAddressing();
	}

	/**
	 * This test checks whether setting the address of an identifier and
	 * retrieving it again works properly.
	 */
	@Test
	public void testRetrievePass() {
		SocketAddress isa1 = new InetSocketAddress(addr, port);
		ca.setAddress("foo", isa1);
		SocketAddress isa2 = ca.getAddress("foo");
		assertTrue(isa1.equals(isa2));
	}

	/**
	 * This test checks whether querying the address of a non-existing identifier
	 * fails as expected with an exception.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRetrieveFail() {
		ca.setAddress("foo", new InetSocketAddress(addr, port));
		ca.getAddress("bar"); // of course, "bar" cannot be found ...
	}

	/** Just a main function to invoke the test from command line. */
	public static void main(String args[]) {
		org.junit.runner.JUnitCore.main("test.cliseau.junit.AddressingTest");
	}
}
