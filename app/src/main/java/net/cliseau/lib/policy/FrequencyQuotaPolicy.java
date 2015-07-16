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
 * This class represents a frequency quota policy.
 *
 * According to the frequency quota policy, the number of
 * security-relevant events belonging to a quota class must
 * not exceed a given frequency (determined by the
 * {@link #quotaAmount} and {@link #quotaInterval} parameters).
 *
 * The frequency quota policy decides about events that are
 * attempted at a particular point in time and belong to a
 * particular quota class.
 */
public class FrequencyQuotaPolicy extends PartitionablePolicy {
	/**
	 * Stores the relevant recent times of event occurrences for each quota class.
	 *
	 * This field maps quota classes (key of the map) to sets of times of events.
	 */
	private HashMap<String, TreeSet<Long>> eventTimes;

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
	 * The interval in which at most {@link #quotaAmount}
	 * security-relevant events may occur per quota class.
	 *
	 * The value is given in milliseconds. The default value
	 * can be overridden by the policy's &quot;quotaInterval&quot;
	 * setting.
	 */
	private long quotaInterval = 1000; // 1000ms (= 1s)
	/**
	 * The maximal number of security-relevant events
	 * per quota class that may occur per {@link #quotaInterval}.
	 *
	 * The default value can be overridden by the policy's
	 * &quot;quotaAmount&quot; setting.
	 */
	private long quotaAmount = 1;

	/**
	 * Construct a local policy object.
	 * @param identifier The identifier of the unit using the local policy
	 */
	public FrequencyQuotaPolicy(final String identifier) {
		super(identifier);
		this.eventTimes = new HashMap<String, TreeSet<Long>>();
	}

	@Override public void setConfig(final Map<String,String> config) {
		super.setConfig(config);
		numberOfUnits = Integer.parseInt(config.get("numberOfUnits"));
		quotaInterval = Long.parseLong(config.get("quotaInterval"));
		quotaAmount   = Long.parseLong(config.get("quotaAmount"));
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
	 * The {@link FrequencyQuotaPolicy} keeps track of the most recent
	 * times of security-relevant events for the individual quota classes.
	 * If permitting a security-relevant event would exceed the permitted
	 * maximal event frequency with (i.e., {@link #quotaAmount} divided by
	 * {@link #quotaInterval}), then the event is rejected; otherwise it
	 * is permitted.
	 *
	 * @param ev The critical event on which to decide based on local state.
	 * @return The enforcement decision for the critical event.
	 * @exception IllegalArgumentException Thrown is the given event is not a
	 *   {@link FrequencyQuotaPolicy.Event}
	 */
	@Override protected EnforcementDecision makeDecision(CriticalEvent ev)
			throws IllegalArgumentException {
		Event FQev = (Event)ev;
		if (FQev == null) {
			throw new IllegalArgumentException("Event for local request of wrong type");
		}

		// get the files accessed by the user who triggered the critical event
		TreeSet<Long> classTimes = eventTimes.get(FQev.quotaClass);
		if (classTimes == null) {
			// no events recorded currently?
			classTimes = new TreeSet<Long>();
		} else {
			// remove too old entries
			classTimes = new TreeSet<Long>(classTimes.tailSet(System.currentTimeMillis()-quotaInterval));
		}
		eventTimes.put(FQev.quotaClass, classTimes);

		// now check whether letting the event occur would violate the policy
		if (classTimes.size() >= quotaAmount) {
			// quota exceeded --> reject the event
			return new Decision(FQev, Decision.Value.REJECT,
					"quota exceeded currently, try again later");
		} else {
			// quota OK --> permit and record the event
			classTimes.add(FQev.timeOfOccurrence);
			return new Decision(FQev, Decision.Value.PERMIT,
					"quota respected");
		}
	}

	/**
	 * Critical event data type for Frequency Quota policies.
	 *
	 * Events for {@link FrequencyQuotaPolicy}s all account to
	 * particular {@link #quotaClass} and occur at a particular
	 * {@link #timeOfOccurrence}.
	 *
	 * @see FrequencyQuotaPolicy
	 */
	public static class Event implements CriticalEvent {
		/**
		 * Name of the class in which the frequencies of events are subject to quota.
		 *
		 * An example for such a class could be events caused by
		 * a particular user (the class could then be represented
		 * by the user's name).
		 */
		public String quotaClass;
		/** Time of the intended event's occurrence. */
		public long timeOfOccurrence;

		/**
		 * Construct an Event object.
		 *
		 * @param quotaClass       Name of the group in which the frequencies of events are subject to quota.
		 * @param timeOfOccurrence Time of the intended event's occurrence.
		 */
		public Event(final String quotaClass, final long timeOfOccurrence) {
			this.quotaClass   = quotaClass;
			this.timeOfOccurrence = timeOfOccurrence;
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
		 * <li>Security-relevant events for a single
		 *   {@link #quotaClass} must be decided by the same responsible
		 *   unit, no matter what {@link #timeOfOccurrence} is given.
		 *   Otherwise sound and transparent decisions about quota
		 *   consumptions cannot be made.</li>
		 * <li>Security-relevant events in different {@link #quotaClass}es
		 *   can be decided by different responsible units, because such
		 *   events do not interfere with each other.</li>
		 * </ol>
		 */
		@Override public int hashCode() {
			return this.quotaClass.hashCode();
		}
	}

	/**
	 * Class representing enforcement decisions for Frequency Quota policies.
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
