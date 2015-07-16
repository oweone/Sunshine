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
package net.cliseau.AnomicFTPD;

import java.io.File;
import net.cliseau.runtime.javacor.CriticalEvent;
import net.cliseau.AnomicFTPD.ChineseWallEventFactory;
import de.anomic.ftpd.ftpdControl;

/**
 * Factory class for CriticalEvent objects of the AnomicFTPD instantiation.
 *
 * For each pointcut there is a static factory method creating CriticalEvent
 * objects from the respective join point.
 *
 * @see AnomicCriticalEventFactory
 */
public class ChineseWallEventFactory2 extends ChineseWallEventFactory {
	/**
	 * For tests with multiple pointcuts.
	 *
	 * @param control The FTP session control object on which the request shall be performed.
	 * @param file The file object that was requested.
	 * @return The constructed CriticalEvent.
	 */
	public static CriticalEvent fileOpCheck2(ftpdControl control, File file) {
		return ChineseWallEventFactory.fileOpCheck(control, file);
	}
}
