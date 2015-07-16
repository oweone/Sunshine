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

import test.cliseau.inst.TestDelegationReqResp;
import test.cliseau.inst.TestEnforcementDecision;

/**
 * This is the data type for delegation responses in this test. Analogously to
 * delegation requests, which embed the critical event directly, every
 * delegation response embeds an enforcement decision.
 */
public class TestDelegationResponse extends TestDelegationReqResp {
	/** The enforcement decision delivered by the delegation response */
	public TestEnforcementDecision ed;

	/**
	 * Construct a delegation response object.
	 * @param ed The enforcement decision delivered by the delegation response
	 * @param sourceID The source of the request/response
	 * @param destID The (final) destination of the request/response
	 */
	public TestDelegationResponse(final TestEnforcementDecision ed, final String sourceID, final String destID) {
		super(sourceID, destID);
		this.ed = ed;
	}
}
