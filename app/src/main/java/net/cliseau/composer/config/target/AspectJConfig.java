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

import net.cliseau.composer.config.target.JavaTargetConfig;
import net.cliseau.composer.config.base.InvalidConfigurationException;

/**
 * Interface for configurations of AspectJ for instrumentation.
 *
 * This interface unites methods for querying configuration options
 * for instrumenting a Java target program using AspectJ.
 */
public interface AspectJConfig extends JavaTargetConfig {
	/**
	 * Retrieve the path to the AspectJ instrumentation backend's executable.
	 *
	 * The path to the AspectJ executable (ajc) on the
	 * machine on which the instrumentation is done.
	 */
	public String getAspectJExecutable() throws InvalidConfigurationException;

	/**
	 * Retrieve classpath element to the AspectJ libraries for instrumenting
	 * the target.
	 */
	public String getAspectJRuntimeClasspath() throws InvalidConfigurationException;

	/**
	 * Retrieve path to the file where the pointcut specification is stored.
	 */
	public String getInstrumentationPointcutSpecFile() throws InvalidConfigurationException;

	/**
	 * Retrieve the path to the "jar" program of Java.
	 */
	public String getJarExecutable() throws InvalidConfigurationException;

	/**
	 * Retrieve the file name of the aspect template (without "st" extension).
	 *
	 * @return Name of the aspect template file.
	 */
	public String getAspectJAspectTemplate() throws InvalidConfigurationException;

	/**
	 * Retrieve the file name of the advice template (without "st" extension).
	 *
	 * @return Name of the advice template file.
	 */
	public String getAspectJAdviceTemplate() throws InvalidConfigurationException;
}
