// serverSwitch.java
// -------------------------------------------
// (C) by Michael Peter Christen; mc@anomic.de
// first published on http://www.anomic.de
// Frankfurt, Germany, 2004
// last major change: 04.02.2004
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//
// Using this software in any meaning (reading, learning, copying, compiling,
// running) means that you agree that the Author(s) is (are) not responsible
// for cost, loss of data or any harm that may be caused directly or indirectly
// by usage of this softare or this documentation. The usage of this software
// is on your own risk. The installation and usage (starting/running) of this
// software may allow other people or application to access your computer and
// any attached devices and is highly dependent on the configuration of the
// software which must be done by the user of the software; the author(s) is
// (are) also not responsible for proper configuration and usage of the
// software, even if provoked by documentation provided together with
// the software.
//
// Any changes to this file according to the GPL as documented in the file
// gpl.txt aside this file in the shipment you received can be done to the
// lines that follows this copyright notice here, but changes must not be
// done inside the copyright notive above. A re-distribution must contain
// the intact and unchanged copyright notice.
// Contributions and changes to the program code must be marked as such.

/*
 this is an interface for possible switchboard implementations
 Its purpose is to provide a mechanism which cgi pages can use
 to influence the behavior of a concurrntly running application
 */

package de.anomic.ftpd;

import java.util.Enumeration;
import java.util.Hashtable;

public interface serverSwitch {

	// the switchboard can be used to set and read properties
	public void setConfig(String key, String value);

	public String getConfig(String key, String dflt);

	public int getConfigInt(String key, int dflt);

	public Enumeration configKeys();

	// the switchboard also shall maintain a job list
	// jobs can be queued by submitting a job object
	// to work off a queue job, use deQueue, which is meant to
	// work off exactly only one job, not all
	public int queueSize();

	public void enQueue(Object job);

	public void deQueue();

	// ask the switchboard to perform an action
	// the result is a properties structure with the result of the action
	// The actionName selects an action
	// the actionInput is an input for the selected action
	public Hashtable action(String actionName, Hashtable actionInput);

}
