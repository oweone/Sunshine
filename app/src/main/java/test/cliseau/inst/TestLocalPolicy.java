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
import net.cliseau.runtime.javacor.DelegationLocPolReturn;
import net.cliseau.runtime.javacor.DelegationReqResp;
import net.cliseau.runtime.javacor.LocalPolicy;
import net.cliseau.runtime.javacor.LocalPolicyResponse;
import test.cliseau.inst.TestCriticalEvent;
import test.cliseau.inst.TestDelegationRequest;
import test.cliseau.inst.TestDelegationResponse;
import test.cliseau.inst.TestEnforcementDecision;

/**
 * This is a very basic local policy class.
 *
 * These policies assume that there are only two CliSeAu units in the distributed system
 * and the for any locally occurring critical event, the respective other CliSeAu
 * unit is responsible to decide. Sophisticated decisions are not
 * made by these policies. Just a rather constant string is returned as a
 * decision.
 */
public class TestLocalPolicy extends LocalPolicy {
	/** The identifier of the other CliSeAu unit (assuming there are just 2) */
	private final String otherIdentifier;

	/**
	 * Construct a local policy object.
	 *
	 * The following assumes that there are exactly 2 CliSeAu units and their
	 * identifiers are of the form "prefix1" and "prefix2" for the same prefix.
	 *
	 * @param identifier The identifier of the CliSeAu unit using the local policy.
	 */
	public TestLocalPolicy(final String identifier) {
		super(identifier);

		this.otherIdentifier = identifier.substring(0, identifier.length()-1) +
			(identifier.charAt(identifier.length()-1)=='1' ? "2" : "1");
	}

	/**
	 * Respond to a local request (that is, to a CriticalEvent).
	 *
	 * If the local unit is not responsible for deciding, then a
	 * TestDelegationRequest is created for the CriticalEvent and returned to the
	 * Coordinator to forward it to the corresponding CliSeAu unit.
	 * Otherwise (which by construction does not happen), a local decision is
	 * made.
	 *
	 * @param ev The event to decide upon.
	 * @return The response to the Coordinator.
	 * @exception IllegalArgumentException Thrown if parameter "ev" is of the wrong type (not TestCriticalEvent).
	 */
	@Override
	public LocalPolicyResponse localRequest(CriticalEvent ev)
			throws IllegalArgumentException {
		TestCriticalEvent tev = (TestCriticalEvent)ev;
		if (tev == null) {
			throw new IllegalArgumentException("Event for local request of wrong type");
		}
		if (getIdentifier().equals(otherIdentifier)) { //Note: this by construction does not happen
			// local policy is responsible for deciding
			return new TestEnforcementDecision(tev.testName, "ok");
		} else {
			// must forward to remote unit
			TestDelegationRequest dr = new TestDelegationRequest(tev, getIdentifier(), otherIdentifier);
			return new DelegationLocPolReturn(otherIdentifier, dr);
		}
	}

	/**
	 * Respond to a remote request.
	 *
	 * This method distinguishes between incoming requests
	 * (TestDelegationRequest) and reponses (TestDelegationResponse).
	 * Requests, if addressed to the local unit, are responded with a simple
	 * EnforcementDecision and are otherwise forwarded to their final
	 * destination. Responses, if not addressed to the local unit, are forwarded
	 * to their final destination and otherwise the EnforcementDecision contained
	 * in them is returned (for the local Enforcer).
	 *
	 * @param dr The incoming delegation request or response.
	 * @return The response to the Coordinator.
	 * @exception IllegalArgumentException Thrown if parameter "dr" is of the wrong type (not one of TestDelegationRequest and TestDelegationResponse).
	 */
	@Override
	public LocalPolicyResponse remoteRequest(DelegationReqResp dr)
			throws IllegalArgumentException {
		if (dr instanceof TestDelegationRequest) {
			TestDelegationRequest req = (TestDelegationRequest)dr;
			if (getIdentifier().equals(req.destID)) {
				// handle request locally and return TestDelegationResponse
				TestEnforcementDecision ed = new TestEnforcementDecision(req.ev.testName, "ok (by "+getIdentifier()+")");
				if (getIdentifier().equals(req.sourceID)) {
					// the local unit originated the request --> deliver locally
					return ed;
				} else {
					// the request originated remotely --> send response back
					return new DelegationLocPolReturn(req.sourceID,
							new TestDelegationResponse(ed, getIdentifier(), req.sourceID));
				}
			} else {
				// forward request
				return new DelegationLocPolReturn(req.destID, req);
			}
		} else if (dr instanceof TestDelegationResponse) {
			TestDelegationResponse resp = (TestDelegationResponse)dr;
			if (getIdentifier().equals(resp.destID)) {
				// response is for local unit
				// and the response already contains the enforcement decision to return
				return resp.ed;
			} else {
				// forward response
				return new DelegationLocPolReturn(resp.destID, resp);
			}
		} else {
			throw new IllegalArgumentException("Event for remote request of wrong type");
		}
	}
}
