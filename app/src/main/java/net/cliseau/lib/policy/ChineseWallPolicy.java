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
package net.cliseau.lib.policy;

import java.util.HashMap;
import java.util.Map;
import net.cliseau.runtime.javacor.CriticalEvent;
import net.cliseau.runtime.javacor.EnforcementDecision;
import net.cliseau.lib.policy.PartitionablePolicy;

/**
 * This class represents the Chinese Wall policy.
 *
 * The policy is a simple Chinese Wall policy based (for the sake of simplicity)
 * on the length of accessed files' names. In principle this is similar to the
 * policy in the example scenario of the paper "Service Automata" by Gay,
 * Mantel, and Sprick (FAST'11). The difference is that we here make the
 * definition of conflict-of-interest (COI) classes and the assignment of
 * responsible units concrete.
 */
public class ChineseWallPolicy extends PartitionablePolicy {
	/**
	 * Stores the files accessed by the users.
	 *
	 * This field maps user names (keys of the map) to the set of company datasets
	 * the user has accessed in the COI classes.
	 */
	private HashMap<String, HashMap<String, String>> accessedFiles;

	/**
	 * Number of units (servers) in the system.
	 *
	 * Responsibilities are distributed among these units.
	 * @see #getResponsibleUnit(CriticalEvent)
	 */
	private int numberOfUnits = 1;

	/**
	 * Construct a local policy object.
	 * @param identifier The identifier of the CliSeAu unit using the local policy
	 */
	public ChineseWallPolicy(final String identifier) {
		super(identifier);
		this.accessedFiles = new HashMap<String, HashMap<String, String>>();
	}

	@Override public void setConfig(final Map<String,String> config) {
		super.setConfig(config);
		numberOfUnits = Integer.parseInt(config.get("numberOfUnits"));
	}

	/**
	 * Determine the unit responsible for deciding upon on an event.
	 *
	 * This method takes the remainder of the event's hashCode() after
	 * division by the number of units in the system. This distributes the
	 * responsibilities somehow among the units.
	 *
	 * @param ev The event whose responsible unit is to be determined.
	 * @return Identifier of the responsible unit for the event.
	 */
	@Override protected String getResponsibleUnit(CriticalEvent ev) {
		// Note that the "+1" is because the server numbering starts at "1", not "0"
		return "id-" + String.valueOf((Math.abs(ev.hashCode()) % numberOfUnits) + 1);
	}

	/**
	 * Determine the next unit to contact on the route to destinationIdentifier.
	 *
	 * This method assumes that unit identifiers are of the form
	 * "&lt;id&gt;-&lt;N&gt;". Where &lt;N&gt; ranges from 1 to the total
	 * number of units in the system. The next unit towards the destination
	 * identifier is determined by incrementing the current unit's number by one,
	 * possibly wrapping around if the destination has a lower number than the
	 * current unit.
	 *
	 * @param destinationIdentifier Identifier of final destination.
	 * @return Identifier of next unit on route to destinationIdentifier.
	 */
	@Override protected String getNextUnit(String destinationIdentifier) {
		String ownId = getIdentifier();
		int destId = Integer.parseInt(ownId.substring(ownId.lastIndexOf('-')+1));
		return "id-" + String.valueOf((destId % numberOfUnits) + 1);
	}

	/**
	 * Based on the memorized state, decide upon a critical events.
	 *
	 * This method checks, whether the user who wants to perform the critical
	 * event has already accessed another file which is in conflict with the file
	 * to be accessed by the critical event. If there is one, the event is
	 * rejected, otherwise permitted. In the latter case, the local state is
	 * updated to record that the user will also have accessed the file referred
	 * in the critical event.
	 *
	 * This function also compiles a small description of the reason for the
	 * decision. This aspect serves neat demonstration purposes only.
	 *
	 * @param ev The critical event on which to decide based on local state.
	 * @return The enforcement decision for the critical event.
	 * @exception IllegalArgumentException Thrown is the given event is not a {@link ChineseWallPolicy.Event}
	 */
	@Override protected EnforcementDecision makeDecision(CriticalEvent ev)
			throws IllegalArgumentException {
		Event ChWev = (Event)ev;
		if (ChWev == null) {
			throw new IllegalArgumentException("Event for local request of wrong type");
		}

		// get the files accessed by the user who triggered the critical event
		HashMap<String,String> accessed = accessedFiles.get(ChWev.userName);
		if (accessed == null) {
			// no files accessed so far?
			accessed = new HashMap<String,String>(1);
			accessedFiles.put(ChWev.userName, accessed);
		} else {
			// a conflict arises if the user has already accessed
			// data from the same COI class as 'ChWev' but a different
			// company dataset
			String accessedCompany = accessed.get(ChWev.COI);
			if (accessedCompany != null) {
				if (ChWev.company.equals(accessedCompany)) {
					// same company's dataset accessed ...
					// no need to update "accessed"
					return new Decision(ChWev, Decision.Value.PERMIT, "no conflicts");
				} else {
					// another company's dataset accessed ...
					return new Decision(ChWev, Decision.Value.REJECT,
							"conflict with earlier accesses to files of "+ChWev.COI+" '"+accessedCompany+"'");
				}
			}
		}
		// no conflict found
		accessed.put(ChWev.COI, ChWev.company);
		return new Decision(ChWev, Decision.Value.PERMIT, "no conflicts");
	}

	/**
	 * Critical event data type for Chinese Wall policies.
	 *
	 * Since the Chinese Wall policy is concerned with users accessing entities,
	 * critical events capture the user names as well as names, conflict of
	 * interest (COI) classs and company names of accessed entities.
	 *
	 * @see ChineseWallPolicy
	 */
	public static class Event implements CriticalEvent {
		/** Name of the user accessing a file. */
		public String userName;
		/** Conflict of interest class of the accessed file. */
		public String COI;
		/** Company dataset the accessed file belongs to. */
		public String company;
		/** Name of the entity to be accessed. */
		public String entity;

		/**
		 * Construct an Event object.
		 *
		 * @param userName Name of the user accessing a file.
		 * @param COI      Conflict of interest class of the accessed file.
		 * @param company  Company dataset the accessed file belongs to.
		 * @param entity   Name of the entity to be accessed.
		 */
		public Event(final String userName, final String COI,
				final String company, final String entity) {
			this.userName = userName;
			this.COI      = COI;
			this.company  = company;
			this.entity   = entity;
		}

		/**
		 * Compute the hash code for this critical event.
		 *
		 * We override this method for partitioning security-relevant events
		 * into classes that should be decided by the same responsible unit.
		 * For this, the hashCode method only considers the COI and userName
		 * fields. The effect of this is that two Event objects with the
		 * same COI and userName have the same hashCode.
		 *
		 * Why does this approach of partitioning security-relevant events
		 * to an adequate decision-making?
		 * <ol>
		 * <li>Accesses by a single user to a single COI class must be decided
		 *   by the same responsible unit, no matter what {@link #company} or
		 *   {@link #entity} is accessed. Otherwise sound decisions about
		 *   conflicting accesses by a user to a COI class cannot be made.
		 *   </li>
		 * <li>Accesses by different users or, alternatively, by a single user
		 *   to different COI classes can be made by different responsible
		 *   units, because such accesses cannot be conflicting with each
		 *   other.</li>
		 * </ol>
		 *
		 * @see ChineseWallPolicy#getResponsibleUnit(CriticalEvent)
		 */
		@Override public int hashCode() {
			return this.COI.hashCode() ^ this.userName.hashCode();
		}
	}

	/**
	 * Class representing enforcement decisions for Chinese Wall policies.
	 *
	 * With this class, critical events can be permitted or rejected.
	 *
	 * @see ChineseWallPolicy
	 */
	public static class Decision implements EnforcementDecision {
		/** The critical event to which the decision belongs. */
		public Event ev;

		/** Type for decisions. */
		public enum Value { PERMIT, REJECT };

		/** The actual decision on the critical event. */
		public Value decision;

		/** For demonstrations/messages, a justification of the decision. */
		public String reason;

		/**
		 * Construct a Decision object.
		 *
		 * @param ev The critical event to which the decision belongs.
		 * @param decision The actual decision on the critical event (permit or reject).
		 * @param reason The reason for the decision.
		 */
		public Decision(final Event ev, final Value decision, final String reason) {
			this.ev = ev;
			this.decision = decision;
			this.reason = reason;
		}
	}
}
