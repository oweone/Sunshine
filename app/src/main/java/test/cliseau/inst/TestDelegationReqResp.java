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

import net.cliseau.runtime.javacor.DelegationReqResp;

/**
 * The base class for TestDelegationRequest and TestDelegationResponse, which
 * comprises the common fields for both types of delegation messages.
 */
public class TestDelegationReqResp implements DelegationReqResp {
	/** The source of the request */
	public String sourceID;
	/** The (final) destination of the request */
	public String destID;

	/** Construct a delegation request/response object.
	 * @param sourceID The source of the request/response
	 * @param destID The (final) destination of the request/response
	 */
	protected TestDelegationReqResp(final String sourceID, final String destID) {
		this.sourceID = sourceID;
		this.destID   = destID;
	}
}
