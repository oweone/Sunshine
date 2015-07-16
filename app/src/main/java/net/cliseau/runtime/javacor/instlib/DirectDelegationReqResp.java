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

import net.cliseau.runtime.javacor.DelegationReqResp;

/**
 * A base class for DirectDelegationRequest and DirectDelegationResponse.
 *
 * This class conceptually captures header information for delegations,
 * namely source and destination identifiers of the sending Service Automaton
 * and, respectively, the intended receiving Service Automaton. That is,
 * this type is intended to be used for situations where delegation requests
 * and responses are transmitted directly (hence the name) to the deciding or,
 * respectively, enforcing Service Automaton.
 */
public abstract class DirectDelegationReqResp implements DelegationReqResp {
	/** The source of the request */
	private final String sourceID;
	/** The (final) destination of the request */
	private final String destID;

	/** Construct a delegation request/response object.
	 * @param sourceID The source of the request/response
	 * @param destID The (final) destination of the request/response
	 */
	protected DirectDelegationReqResp(final String sourceID, final String destID) {
		this.sourceID = sourceID;
		this.destID   = destID;
	}
	
	/**
	 * Get sourceID.
	 *
	 * @return sourceID as String.
	 */
	public String getSourceID()
	{
		return sourceID;
	}
	
	/**
	 * Get destID.
	 *
	 * @return destID as String.
	 */
	public String getDestID()
	{
		return destID;
	}
}
