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
package net.cliseau.runtime.javacor.instlib;

import net.cliseau.runtime.javacor.EnforcementDecision;
import net.cliseau.runtime.javacor.instlib.DirectDelegationReqResp;

/**
 * Basic data type for direct delegation responses.
 *
 * This data type for delegation responses is supposed to be usable in many
 * scenarios and enforcement strategies. The idea is that responses are directly
 * sent to the Service Automaton that shall enforce the decision. That is,
 * this type of delegation responses might not be applicable where
 * <ul>
 * <li>partial decisions are made, such that auxiliary information about the
 *   partial decisions has to be present in the delegation responses;</li>
 * <li>routing information should be part of the delegation types, such as in
 *   the case of source routing.</li>
 * </ul>
 *
 * @see DirectDelegationRequest
 */
public class DirectDelegationResponse extends DirectDelegationReqResp {
	/** The enforcement decision delivered by the delegation response */
	private final EnforcementDecision ed;

	/**
	 * Construct a delegation response object.
	 * @param ed The enforcement decision delivered by the delegation response
	 * @param sourceID The source of the request/response
	 * @param destID The (final) destination of the request/response
	 */
	public DirectDelegationResponse(final EnforcementDecision ed,
			final String sourceID, final String destID) {
		super(sourceID, destID);
		this.ed = ed;
	}
	
	/**
	 * Get ed.
	 *
	 * @return ed as EnforcementDecision.
	 */
	public EnforcementDecision getDecision()
	{
		return ed;
	}
}
