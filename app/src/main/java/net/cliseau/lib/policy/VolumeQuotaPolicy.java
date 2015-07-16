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
package net.cliseau.lib.policy;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import net.cliseau.runtime.javacor.CriticalEvent;
import net.cliseau.runtime.javacor.EnforcementDecision;
import net.cliseau.lib.policy.PartitionablePolicy;

/**
 * This class represents a volume quota policy.
 *
 * According to the volume quota policy, all entities belonging to a quota
 * class must at all times not exceed the given {@link #quotaLimit} of
 * the quota class.
 *
 * The volume quota policy decides about events that in some form
 * consume or release the use of a quota-constrained resource.
 * Consumption events have a positive
 * {@link VolumeQuotaPolicy.Event#quotaUseDelta},
 * release events have a negative one.
 */
public class VolumeQuotaPolicy extends PartitionablePolicy {
	/**
	 * Stores the current quota use for each quota class.
	 */
	private HashMap<String, Float> quotaUse;

	/**
	 * Number of units (servers) in the system.
	 *
	 * Responsibilities are distributed among these units. The
	 * default value can be overridden by the policy's
	 * &quot;numberOfUnits&quot; setting.
	 *
	 * @see #getResponsibleUnit(CriticalEvent)
	 */
	private int numberOfUnits = 1;

	/**
	 * The maximal number of quota units that a single user may consume
	 * at a time.
	 *
	 * The default value can be overridden by the policy's
	 * &quot;quotaLimit&quot; setting.
	 */
	private Float quotaLimit = new Float(10.0);

	/**
	 * Construct a local policy object.
	 * @param identifier The identifier of the unit using the local policy
	 */
	public VolumeQuotaPolicy(final String identifier) {
		super(identifier);
		this.quotaUse = new HashMap<String, Float>();
	}

	@Override public void setConfig(final Map<String,String> config) {
		super.setConfig(config);
		numberOfUnits = Integer.parseInt(config.get("numberOfUnits"));
		quotaLimit    = Float.parseFloat(config.get("quotaLimit"));
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
	 * Determine the next unit to contact on the route to {@code destinationIdentifier}.
	 *
	 * This method always chooses the destination unit as the next unit.
	 *
	 * @param destinationIdentifier Identifier of final destination.
	 * @return Identifier of next unit on route to {@code destinationIdentifier}.
	 */
	@Override protected String getNextUnit(String destinationIdentifier) {
		return destinationIdentifier;
	}

	/**
	 * Based on the memorized state, decide upon a security-relevant event.
	 *
	 * The {@link VolumeQuotaPolicy} keeps track of the consumed quota for each
	 * quota class in a map. If permitting a consumption event would result
	 * in an exceeded quota for the class, then the event is rejected; otherwise
	 * it is permitted.
	 *
	 * @param ev The critical event on which to decide based on local state.
	 * @return The enforcement decision for the critical event.
	 * @exception IllegalArgumentException Thrown is the given event is not a
	 *   {@link VolumeQuotaPolicy.Event}.
	 */
	@Override protected EnforcementDecision makeDecision(CriticalEvent ev)
			throws IllegalArgumentException {
		Event FQev = (Event)ev;
		if (FQev == null) {
			throw new IllegalArgumentException("Event for local request of wrong type");
		}

		// get the files accessed by the user who triggered the critical event
		Float currentUse = quotaUse.get(FQev.quotaClass);
		if (currentUse == null) {
			// no use recorded currently? initialize
			currentUse = new Float(0.0);
		}
		// update the use to the value that would be reached when performing the event
		currentUse += FQev.quotaUseDelta;

		// now check whether letting the event occur would violate the policy
		if (currentUse > quotaLimit) {
			// quota exceeded --> reject the event
			return new Decision(FQev, Decision.Value.REJECT,
					"quota would be exceeded; release resources first or request less");
		} else {
			// quota OK --> permit and record the event's quota use
			quotaUse.put(FQev.quotaClass, currentUse);
			return new Decision(FQev, Decision.Value.PERMIT,
					"quota respected");
		}
	}

	/**
	 * Critical event data type for Volume Quota policies.
	 *
	 * Events for {@link VolumeQuotaPolicy}s all affect a
	 * particular {@link #quotaClass} and represent a particular
	 * change ({@link #quotaUseDelta}) of the quota-constrained
	 * resource. Additional consumptions of the resource have
	 * a positive {@link #quotaUseDelta}; releases of the
	 * resource have a negative {@link #quotaUseDelta}.
	 *
	 * @see FrequencyQuotaPolicy.Event
	 */
	public static class Event implements CriticalEvent {
		/**
		 * Name of the class in which the resource use is subject to quota.
		 *
		 * An example for such a class could be events caused by
		 * a particular user (the class could then be represented
		 * by the user's name).
		 */
		public String quotaClass;
		/** Quota usage delta (increase or decrease). */
		public Float quotaUseDelta;

		/**
		 * Construct an Event object.
		 *
		 * @param quotaClass    Name of the class in which the resource use is subject to quota.
		 * @param quotaUseDelta Quota usage delta (increase or decrease).
		 */
		public Event(final String quotaClass, final Float quotaUseDelta) {
			this.quotaClass    = quotaClass;
			this.quotaUseDelta = quotaUseDelta;
		}

		/**
		 * Compute the hash code for this critical event.
		 *
		 * We override this method for partitioning critical events into
		 * classes that should be decided by the same responsible unit.
		 * For this, the {@link #hashCode()} method only considers the
		 * {@link #quotaClass} field.
		 * The effect of this is that two Event objects with the
		 * same {@link #quotaClass} have the same {@link #hashCode()}.
		 *
		 * Why does this approach of partitioning security-relevant events
		 * to an adequate decision-making?
		 * <ol>
		 * <li>Quota consumptions and releases within a single
		 *   {@link #quotaClass} must be decided by the same responsible
		 *   unit, no matter what {@link #quotaUseDelta} is given.
		 *   Otherwise sound and transparent decisions about quota
		 *   consumptions cannot be made.</li>
		 * <li>Consumptions or releases to different {@link #quotaClass}es
		 *   can be decided by different responsible units, because such
		 *   events do not interfere with each other.</li>
		 * </ol>
		 */
		@Override public int hashCode() {
			return this.quotaClass.hashCode();
		}
	}

	/**
	 * Class representing enforcement decisions for Volume Quota policies.
	 *
	 * With this class, critical events can be permitted or rejected.
	 *
	 * @see ChineseWallPolicy.Decision
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
		 * Construct a Decision.
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
