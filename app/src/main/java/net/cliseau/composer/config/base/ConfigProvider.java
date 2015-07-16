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

import net.cliseau.composer.config.base.ArchitectureConfig;
import net.cliseau.composer.config.base.UnitConfigProvider;
import net.cliseau.composer.config.base.InvalidConfigurationException;

/**
 * Basic interface for concrete providers of encapsulation infrastructure configurations.
 *
 * A {@link ConfigProvider} must be able to return
 * <ul>
 * <li>the configuration of the overall architecture of the encapsulation infrastructure,</li>
 * <li>a configuration provider for each unit in the architecture, and</li>
 * <li>a destination directory for the encapsulated result.</li>
 * </ul>
 *
 * A note for extending the configurations to support further target languages:
 * the architecture is designed to be easily extensible. For doing to,
 * stick to the following steps:
 * <ol>
 * <li>Add a new composer.config.target.&lt;Name&gt;Config, e.g., inheriting from
 *     {@link UnitConfig}.</li>
 * <li>Put into the new class getters for
 *     all configuration elements that a <em>unit</em> for the language needs.</li>
 * <li>Extend the {@link UnitConfigProvider} class by an additional getter
 *     method get&lt;Name&gt;Config().</li>
 * <li>Extend all classes derived from {@link UnitConfigProvier} by an implementation
 *     of the method added in the previous step. As part of this, provide
 *     an implementation of the &lt;Name&gt;Config interface.</li>
 * </ol>
 * There is no need to extend/adapt the configuration provider when adding support
 * for further target languages.
 */
public interface ConfigProvider {
	/**
	 * Return an object from which the architecture configuration can be obtained.
	 *
	 * @return Configuration object for the architecture.
	 */
	public ArchitectureConfig getArchitectureConfig();

	/**
	 * Return an object from which a chosen unit's configuration can be obtained.
	 *
	 * @param unitName Name of the unit whose configuration provider shall be returned.
	 * @return Configuration object for the unit with the given name.
	 */
	public UnitConfigProvider getUnitConfigProvider(final String unitName);

	/**
	 * Retrieve the directory into which the encapsulated target shall be written.
	 *
	 * The returned directory becomes the parent directory for subdirectories
	 * into which the individual encapsulated components of the distributed
	 * target are written.
	 *
	 * @return The directory into which the encapsulated target shall be written.
	 */
	public String getDestinationDirectory() throws InvalidConfigurationException;
}
