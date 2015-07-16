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

import net.cliseau.composer.config.base.InvalidConfigurationException;

/**
 * Exception thrown during the construction of a PEI.
 *
 * Objects of this class should be thrown when a certain configuration setting
 * is of a wrong syntax.
 */
public class InvalidConfigValueException extends InvalidConfigurationException {
	/**
	 * Construct a MissingConfigurationException object.
	 *
	 * @param key The key in the configuration whose value is invalid.
	 * @param value The part of the value that is invalid.
	 * @param reason The reason why the value is invalid.
	 */
	public InvalidConfigValueException(final String key, final String value, final String reason) {
		super("Configuration value '"+value+"' invalid as part of key '"+key+"' ("+reason+").");
	}
}

