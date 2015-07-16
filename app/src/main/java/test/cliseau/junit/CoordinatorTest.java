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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import net.cliseau.runtime.javacor.Coordinator;
import net.cliseau.runtime.javacor.CoordinatorAddressing;
import net.cliseau.runtime.javacor.CriticalEvent;
import net.cliseau.runtime.javatarget.CoordinatorInterface;
import org.apache.log4j.BasicConfigurator; // for debugging
import test.cliseau.inst.TestCriticalEvent;
import test.cliseau.inst.TestEnforcementDecision;
import test.cliseau.inst.TestDelegationRequest;
import test.cliseau.inst.TestDelegationResponse;
import test.cliseau.inst.TestLocalPolicy;

/**
 * This class tests Coordinator functionality.
 *
 * It does not really comprise only unit test but also more complicated ones.
 */
public class CoordinatorTest {
	private final static int N = 2; //!< Number of coordinators in the test
	/** List of identifiers for the constructed coordinators */
	private final static String[] IDs      = {"Coordinator 1", "Coordinator 2"};
	/** Port numbers of the coordinators for listening to remote messages */
	private final static int[] RemotePorts = {           5001, 5002 };
	/** Port numbers of the coordinators for listening to local messages (supposedly interceptors) */
	private final static int[] LocalPorts  = {           5011, 5012 };
	/** Port numbers of the enforcers for listening to messages from their coordinators */
	private final static int[] EnfPorts    = {           5021, 5022 };
	/** Socket timeout (in milliseconds) */
	private final static int TIMEOUT = 500;
	/** Host for local communication */
	private final static String localHost = "localhost";

	/**
	 * Test simple communication/coordination between two coordinators.
	 *
	 * This test spawns off two coordinators and sends a faked critical event to
	 * the first one. That is, no real interceptor for the first coordinator is
	 * involved. The critical event indicates that the second coordinator must
	 * decide upon it. The expectation of this test is that the second
	 * coordinator obtains a delegation request for the critical event and sends
	 * back a simple enforcement decision. The first coordinator passes on the
	 * enforcement decision to the local enforcer, which is here simply
	 * represented by a listening socket. Apart from testing unexpected
	 * exceptions, also the content of the resulting enforcement decision is
	 * tested.
	 */
	@Test
	public void testCommunicationBetween2()
			throws IOException,InterruptedException,ClassNotFoundException {
		BasicConfigurator.configure();

		InetAddress localHostAddr = InetAddress.getByName(localHost);

		// create the coordinators and auxiliary objects
		CoordinatorAddressing[]  addressing;    addressing    = new CoordinatorAddressing[N];
		ServerSocket[]           remote_socket; remote_socket = new ServerSocket[N];
		ServerSocket[]           local_socket;  local_socket  = new ServerSocket[N];
		TestLocalPolicy[]        policy;        policy        = new TestLocalPolicy[N];
		Coordinator[]            coor;          coor          = new Coordinator[N];
		Thread[]                 coor_thread;   coor_thread   = new Thread[N];
		for (int i=0; i<N; ++i) {
			// create addressing information
			addressing[i] = new CoordinatorAddressing();
			for (int j=0; j<N; ++j) {
				addressing[i].setAddress(IDs[j], new InetSocketAddress(localHostAddr, RemotePorts[j]));
			}
			addressing[i].setLocalEnforcerAddress(new InetSocketAddress(localHostAddr, EnfPorts[i]));

			// create the sockets (local and remote) for both coordinators
			remote_socket[i] = new ServerSocket(RemotePorts[i], 0, localHostAddr);
			remote_socket[i].setSoTimeout(TIMEOUT);
			local_socket[i]  = new ServerSocket(LocalPorts[i],  0, localHostAddr);
			local_socket[i].setSoTimeout(TIMEOUT);

			// create local policy objects
			policy[i] = new TestLocalPolicy(IDs[i]);

			// create threads for the coordinators
			coor[i] = new Coordinator(IDs[i], local_socket[i], remote_socket[i], addressing[i], policy[i], org.apache.log4j.Level.INFO);
			coor_thread[i] = new Thread(coor[i]);
		}

		// initialize the interface to be used by the simulated interceptor and enforcer
		ServerSocket enf_sock = new ServerSocket(EnfPorts[0], 0, localHostAddr);
		enf_sock.setSoTimeout(1000); // 1 second should suffice
		CoordinatorInterface.init(new InetSocketAddress(localHostAddr, LocalPorts[0]), enf_sock);

		// spawn off threads for the two coordinators
		for (int i=0; i<N; ++i) {
			coor_thread[i].start();
		}

		// send a critical event to the first coordinator and see what happens
		CriticalEvent ev = TestCriticalEvent.stringOp("foo");
		CoordinatorInterface.send(ev);
		// receive the resulting decision (this is the enforcer part)
		TestEnforcementDecision ed = (TestEnforcementDecision) CoordinatorInterface.receive();

		// check the obtained result
		assertTrue(ed != null);
		assertTrue(ed.testName.equals("foo"));

		// terminate the coordinators
		for (int i=0; i<N; ++i) {
			coor[i].stop();
		}
		for (int i=0; i<N; ++i) {
			coor_thread[i].join();
		}
	}

	/** Just a main function to invoke the test from command line */
	public static void main(String args[]) {
		org.junit.runner.JUnitCore.main("test.cliseau.junit.CoordinatorTest");
	}
}
