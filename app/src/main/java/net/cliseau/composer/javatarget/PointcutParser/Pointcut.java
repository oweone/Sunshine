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
package net.cliseau.composer.javatarget.PointcutParser;

import java.util.LinkedList;

/**
 * Data structure representing pointcut information.
 *
 * Each Object of this class holds all relevant information of a single pointcut
 * declaration in a structured form. Objects are constructed from String
 * pointcut expressions by the PointcutParser and collected in PointcutSpec
 * structures. Based on the information stored in objects of this class, advice
 * can be generated automatically (using, e.g., the pointcut name and the
 * parameter list), as done by the AspectWeaver.
 *
 * @see PointcutSpec
 * @see PointcutParser
 * @see net.cliseau.composer.javatarget.AspectWeaver
 */
public class Pointcut {
	/**
	 * Class for storing information about a single pointcut's parameter.
	 *
	 * Every pointcut has a (possibly empty) list of parameters. Each such
	 * parameters has a type and name, just as for a Java method. We use
	 * ParamData objects to store a pair of parameter type and name.
	 */
	public class ParamData {
		/** Parameter type */
		public String Type;
		/** Parameter name */
		public String Name;

		/**
		 * Construct a new parameter object.
		 * @param t Parameter type
		 * @param n Parameter name
		 */
		public ParamData(final String t, final String n) {
			Type = t;
			Name = n;
		}
	}

	/** The pointcut name */
	private String name;
	/** A list of formal pointcut parameters */
	private LinkedList<ParamData> parameters;
	/** The return type of the pointcut shadows */
	private String returnType;
	/** The pointcut expression (describing which shadows the pointcut matches) */
	private String pointcutExpression;

	/**
	 * Default return type as String.
	 *
	 * Whenever a pointcut declaration does not explicitly specify a return type
	 * for its join points, then the type specified by this variable is
	 * implicitly assumed. */
	public static final String noReturnType = "void";

	/** Construct a new Pointcut object */
	public Pointcut() {
		parameters = new LinkedList<ParamData>();
		returnType = noReturnType;
	}

	/**
	 * Add a parameter to the list of the pointcut's parameters.
	 *
	 * @param paramType Java type of the parameter (as String, not Class)
	 * @param paramName Name of the parameter
	 */
	public void addParameter(String paramType, String paramName) {
		parameters.addLast(new ParamData(paramType, paramName));
	}

	/**
	 * Get the list of parameters.
	 *
	 * @return Linked list of the pointcut's parameters.
	 */
	public LinkedList<ParamData> getParameters() { return parameters; }

	/**
	 * Return a comma-separated list of the formal parameter names.
	 *
	 * @return Comma-separated list of pointcut parameters
	 */
	public String getParametersList() {
		StringBuffer sb = new StringBuffer();
		for (ParamData pd : parameters) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(pd.Name);
		}
		return sb.toString();
	}

	/**
	 * Return a comma-separated list of the formal parameter names and their types.
	 *
	 * @return Comma-separated list of typed pointcut parameters
	 */
	public String getTypedParametersList() {
		StringBuffer sb = new StringBuffer();
		for (ParamData pd : parameters) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(pd.Type + " " + pd.Name);
		}
		return sb.toString();
	}

	/**
	 * Create a pointcut declaration from the data structure.
	 *
	 * This method transforms a Pointcut object into a pointcut string. The
	 * result is intended to be understandable by AspectJ. That is, this function
	 * does not necessarily output the pointcut in the same way that it was read
	 * by the PointcutParser - extensions to the language (e.g., for a return
	 * type) are not reflected here.
	 *
	 * @return String representation of the pointcut
	 */
	public String toString() {
		//Note: pointcut modifiers are not included
		StringBuffer sb = new StringBuffer();
		sb.append("pointcut " + name + "(");
		sb.append(getTypedParametersList());
		sb.append(") : " + pointcutExpression + ";\n");
		return sb.toString();
	}

	/**
	 * Get pointcutExpression.
	 *
	 * @return pointcutExpression as String.
	 */
	public String getPointcutExpression()
	{
	    return pointcutExpression;
	}

	/**
	 * Set pointcutExpression.
	 *
	 * @param pointcutExpression the value to set.
	 */
	public void setPointcutExpression(String pointcutExpression)
	{
	    this.pointcutExpression = pointcutExpression;
	}

	/**
	 * Get returnType.
	 *
	 * @return returnType as String.
	 */
	public String getReturnType()
	{
	    return returnType;
	}

	/**
	 * Set returnType.
	 *
	 * @param returnType the value to set.
	 */
	public void setReturnType(String returnType)
	{
	    this.returnType = returnType;
	}

	/**
	 * Get name.
	 *
	 * @return name as String.
	 */
	public String getName()
	{
	    return name;
	}

	/**
	 * Set name.
	 *
	 * @param name the value to set.
	 */
	public void setName(String name)
	{
	    this.name = name;
	}
}
