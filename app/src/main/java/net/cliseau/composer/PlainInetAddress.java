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
package net.cliseau.composer;

/**
 * Class for storing an Internet address without any lookup functionality.
 *
 */
public class PlainInetAddress {
	/** Stores the host name */
	private String host;
	/** Stores the port number */
	private int port;

	/**
	 * Create a PlainInetAddress object
	 *
	 * @param host The host name to initialize this address with.
	 * @param port The port number to initialize this address with.
	 */
	public PlainInetAddress(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * Return InetSocketAddress creation code.
	 *
	 * This method returns a Java code string for creating an InetSocketAddress
	 * object holding the same (host,port) pair as the object this method is
	 * called on.
	 *
	 * @return Java code expression for creating an InetSocketAddress with the
	 *         same address.
	 */
	public String toCreationCode() {
		return String.format("new java.net.InetSocketAddress(\"%s\", %d)", host, port);
	}

	/**
	 * Return ServerSocket creation code.
	 *
	 * This method returns a Java code string for creating a ServerSocket object
	 * which shall listen at the port given by this object and at an interface
	 * suitable for the given host name.
	 *
	 * @return Java code expression for creating a ServerSocket object.
	 */
	public String toServerCreationCode() {
		return String.format("new java.net.ServerSocket(%d, 0, java.net.InetAddress.getByName(\"%s\"))",
				port, host);
	}

	/**
	 * Get host.
	 *
	 * @return host as String.
	 */
	public String getHost()
	{
	    return host;
	}

	/**
	 * Set host.
	 *
	 * @param host the value to set.
	 */
	public void setHost(String host)
	{
		this.host = host;
	}

	/**
	 * Get port.
	 *
	 * @return port as int.
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 * Set port.
	 *
	 * @param port the value to set.
	 */
	public void setPort(int port)
	{
		this.port = port;
	}
}

