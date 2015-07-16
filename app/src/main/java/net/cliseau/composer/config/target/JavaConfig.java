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
package net.cliseau.composer.config.target;

import java.util.Collection;
import net.cliseau.composer.config.base.InvalidConfigurationException;
import net.cliseau.composer.config.target.JavaCorConfig;
import net.cliseau.composer.config.target.JavaTargetConfig;

/**
 * Interface for configurations of units implemented in Java.
 *
 * This interface is a convenience extension of the interfaces that
 * provide configuration for a unit's interceptor/enforcer code and,
 * respectively, for a unit's coordinator/local policy part.
 *
 * @todo Possibly all the methods directly contained in this interface
 *   should be moved to the interfaces that {@link JavaConfig} extends.
 */
public interface JavaConfig extends JavaCorConfig,AspectJConfig {
	/**
	 * Retrieve the JAR file containing the fixed classes of a CliSeAu unit.
	 */
	public String getCliSeAuJavaRuntime() throws InvalidConfigurationException;

	/**
	 * Retrieve the paths for all dependencies (coordinator and policy).
	 */
	public Collection<String> getJavaDependencyPaths() throws InvalidConfigurationException;

	/**
	 * Retrieve paths to JAR files in which instantiation implementation is stored.
	 *
	 * This includes, for instance, the implementation of local policies and data
	 * types (delegation requests/responses, critical events, enforcement
	 * decisions). In addition, this collection must include all third party
	 * libraries used by the instantiation.
	 */
	public Collection<String> getJavaInstantiationJARs() throws InvalidConfigurationException;
}
