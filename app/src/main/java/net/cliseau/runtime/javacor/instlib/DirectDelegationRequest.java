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

import net.cliseau.runtime.javacor.CriticalEvent;
import net.cliseau.runtime.javacor.instlib.DirectDelegationReqResp;

/**
 * Basic data type for direct delegation requests.
 *
 * This data type for delegation requests is supposed to be usable in many
 * scenarios and enforcement strategies. The idea is that requests are directly
 * sent to a deciding Service Automaton where a decision can be made. That is,
 * this type of delegation requests might not be applicable where
 * <ul>
 * <li>partial decisions are made, such that auxiliary information about the
 *   partial decisions has to be present in the delegation requests;</li>
 * <li>routing information should be part of the delegation types, such as in
 *   the case of source routing.</li>
 * </ul>
 *
 * @see DirectDelegationResponse
 */
public class DirectDelegationRequest extends DirectDelegationReqResp {
	/** The critical event to be decided upon */
	private final CriticalEvent ev;

	/** Construct a delegation request object.
	 * @param ev The critical event to be decided upon
	 * @param sourceID The source of the request/response
	 * @param destID The (final) destination of the request/response
	 */
	public DirectDelegationRequest(final CriticalEvent ev, final String sourceID, final String destID) {
		super(sourceID, destID);
		this.ev = ev;
	}
	
	/**
	 * Get ev.
	 *
	 * @return ev as CriticalEvent.
	 */
	public CriticalEvent getEvent()
	{
		return ev;
	}
}
