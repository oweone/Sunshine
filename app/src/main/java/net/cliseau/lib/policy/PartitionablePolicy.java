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
package net.cliseau.lib.policy;

import net.cliseau.runtime.javacor.CriticalEvent;
import net.cliseau.runtime.javacor.DelegationLocPolReturn;
import net.cliseau.runtime.javacor.DelegationReqResp;
import net.cliseau.runtime.javacor.EnforcementDecision;
import net.cliseau.runtime.javacor.LocalPolicy;
import net.cliseau.runtime.javacor.LocalPolicyResponse;
import net.cliseau.runtime.javacor.instlib.DirectDelegationRequest;
import net.cliseau.runtime.javacor.instlib.DirectDelegationResponse;

/**
 * This class is a superclass for policies whose deciding can be
 * partitioned based a partitioning of the events.
 *
 * Conceptually, a {@link PartitionablePolicy} is a specialized
 * {@link LocalPolicy} that allows one to assign to each
 * security-relevant event one unit. We call this unit the
 * &quot;responsible&quot; unit, given that it is its
 * responsibility to make a decision for the event.
 *
 * When a unit obtains an event to be decided, then
 * <ul>
 * <li>if the unit itself is responsible for the event
 *   (determined by comparing the {@link #identifier} with the
 *   result of {@link #getResponsibleUnit(CriticalEvent)}), then
 *   the unit itself makes a decision (using
 *   {@link #makeDecision(CriticalEvent)});</li>
 * <li>if the unit is not responsible for the event,
 *   then it delegates the decision-making for the event
 *   to the responsible unit.</li>
 * </ul>
 *
 * The {@link PartitionablePolicy} is supposed to be used in cases
 * when
 * <ul>
 * <li>the security-relevant events can be partitioned according
 *   to some criterion that can be computed independently of
 *   the event occurrences at other agents</li>
 * <li>all units use the same local policy implementation or,
 *   at least, the same event partitioning</li>
 * </ul>
 */
public abstract class PartitionablePolicy extends LocalPolicy {
	/**
	 * Construct a local policy object.
	 * @param identifier The identifier of the unit using the local policy
	 */
	public PartitionablePolicy(final String identifier) {
		super(identifier);
	}

	/**
	 * Determine the unit that is responsible for deciding upon an event.
	 *
	 * @param ev The event whose responsible unit is to be determined.
	 * @return Identifier of the responsible unit for the event.
	 */
	protected abstract String getResponsibleUnit(CriticalEvent ev)
		throws IllegalArgumentException;

	/**
	 * Based on the memorized state, decide upon a critical events.
	 *
	 * @param ev The critical event on which to decide based on local state.
	 * @return The enforcement decision for the critical event.
	 */
	protected abstract EnforcementDecision makeDecision(CriticalEvent ev)
		throws IllegalArgumentException;

	/**
	 * Determine the next unit to contact on the route to destinationIdentifier.
	 *
	 * @param destinationIdentifier Identifier of final destination.
	 * @return Identifier of next unit on route to destinationIdentifier.
	 */
	protected abstract String getNextUnit(String destinationIdentifier);

	/**
	 * Respond to a local request (that is, to a CriticalEvent).
	 *
	 * First, it is found out whether the local unit is responsible for deciding
	 * on the critical event. If so, then a local decision is made and an
	 * {@link EnforcementDecision} is returned. Otherwise, a {@link
	 * DirectDelegationRequest} is created and returned to be sent to the
	 * responsible unit by the coordinator.
	 *
	 * @param ev The event to decide upon.
	 * @return The response to the Coordinator.
	 * @exception IllegalArgumentException Thrown if parameter "ev" is of the
	 *   wrong type.
	 */
	@Override
	public final LocalPolicyResponse localRequest(CriticalEvent ev)
			throws IllegalArgumentException {
		String responsible = getResponsibleUnit(ev);
		if (getIdentifier().equals(responsible)) {
			// local policy is responsible for deciding
			return makeDecision(ev);
		} else {
			// must forward to remote CliSeAu unit
			DirectDelegationRequest dr = new DirectDelegationRequest(ev, getIdentifier(), responsible);
			return new DelegationLocPolReturn(getNextUnit(responsible), dr);
		}
	}

	/**
	 * Respond to a remote request.
	 *
	 * This method distinguishes between incoming requests
	 * (DirectDelegationRequest) and reponses (DirectDelegationResponse).
	 * Requests, if addressed to the local unit, are responded with a locally
	 * made {@link EnforcementDecision} and are otherwise forwarded to their next
	 * destination. Responses, if not addressed to the local unit, are forwarded
	 * to their next destination and otherwise the {@link EnforcementDecision}
	 * contained in them is returned (for the local Enforcer).
	 *
	 * @param dr The incoming delegation request or response.
	 * @return The response to the Coordinator.
	 * @exception IllegalArgumentException Thrown if parameter "dr" is of the
	 *   wrong type (not one of DirectDelegationRequest and DirectDelegationResponse).
	 * @todo Currently, the implementation has an inconsistency/unclarity:
	 *   decisions for delegation requests are sent back directly to the
	 *   original unit; maybe {@link #getNextUnit(String)} should be used
	 *   to be consistent with the idea of always using the routes
	 *   dictated by {@link #getNextUnit(String)}
	 */
	@Override
	public final LocalPolicyResponse remoteRequest(DelegationReqResp dr) {
		if (dr instanceof DirectDelegationRequest) {
			DirectDelegationRequest req = (DirectDelegationRequest)dr;
			if (getIdentifier().equals(req.getDestID())) {
				// handle request locally and return DirectDelegationResponse
				EnforcementDecision ed = makeDecision(req.getEvent());
				if (getIdentifier().equals(req.getSourceID())) {
					// the local unit originated the request --> deliver locally
					return ed;
				} else {
					// the request originated remotely --> send response back
					return new DelegationLocPolReturn(req.getSourceID(),
							new DirectDelegationResponse(ed, getIdentifier(), req.getSourceID()));
				}
			} else {
				// forward request
				return new DelegationLocPolReturn(getNextUnit(req.getDestID()), req);
			}
		} else if (dr instanceof DirectDelegationResponse) {
			DirectDelegationResponse resp = (DirectDelegationResponse)dr;
			if (getIdentifier().equals(resp.getDestID())) {
				// response is for local unit
				// and the response already contains the enforcement decision to return
				return resp.getDecision();
			} else {
				// forward response
				return new DelegationLocPolReturn(getNextUnit(resp.getDestID()), resp);
			}
		} else {
			throw new IllegalArgumentException("Event for remote request of wrong type");
		}
	}
}
