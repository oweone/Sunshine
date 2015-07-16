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
package net.cliseau.composer.config.base;

import java.io.File;
import java.io.IOException;

/**
 * Static helper functions for use by concrete configuration providers.
 *
 * The helper functions currently address the following topics:
 * <ul>
 * <li>validating configuration values</li>
 * </ul>
 */
public class ConfigUtils {
	/**
	 * Check whether a file name is valid (with respect to a given path)
	 *
	 * @param path The base path for relative file names (may be null).
	 * @param file The file name whose validity is to be checked.
	 * @return true if file is valid (with respect to path).
	 * @see <a href="http://www.rgagnon.com/javadetails/java-check-if-a-filename-is-valid.html">Real Gagnon's webpage</a>
	 */
	public static boolean isFilenameValid(String path, String file) {
		File f = new File(path, file);
		try {
			f.getCanonicalPath();
			return true;
		}
		catch (IOException e) {
			return false;
		}
	}
}
