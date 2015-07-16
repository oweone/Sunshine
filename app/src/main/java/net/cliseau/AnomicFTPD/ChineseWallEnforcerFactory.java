package net.cliseau.AnomicFTPD;

import net.cliseau.runtime.javacor.EnforcementDecision;
import net.cliseau.runtime.javacor.CriticalEvent;
import net.cliseau.runtime.javatarget.Enforcer;
import net.cliseau.runtime.javatarget.EnforcerFactory;
import net.cliseau.lib.enforcer.VerbosePermittingEnforcer;
import net.cliseau.lib.enforcer.VerboseValueReplacingEnforcer;
import net.cliseau.lib.policy.ChineseWallPolicy;

/**
 * Factory class for AnomicFTPD Enforcer objects.
 *
 * Based on given EnforcementDecision objects, this factory creates enforcer
 * objects which are supposed to implement the given decision. Since there
 * currently are only two kinds of enforcement decisions - permit and reject -
 * there are also two Enforcer classes whose objects can be created by the
 * factory. Rejected events are suppressed here (instead of, e.g., terminating
 * the program that tries to perform them).
 *
 * @see ChineseWallPolicy.Decision
 */
public class ChineseWallEnforcerFactory implements EnforcerFactory {
	/**
	 * Create a suitable Enforcer for a given EnforcementDecision.
	 *
	 * @param ed The EnforcementDecision from which the Enforcer is constructed.
	 * @return The constructed Enforcer.
	 */
	public static Enforcer fromDecision(final EnforcementDecision ed) {
		ChineseWallPolicy.Decision ted = (ChineseWallPolicy.Decision) ed;
		if (ted != null) {
			switch (ted.decision) {
				case PERMIT:
					return new VerbosePermittingEnforcer(
							"Access to '" + ted.ev.entity + "' permitted (" + ted.reason + ").");
				case REJECT:
					return new VerboseValueReplacingEnforcer<Boolean>(false,
							"Access to '" + ted.ev.entity + "' prevented (" + ted.reason + ").");
			}
		}
		return new VerboseValueReplacingEnforcer<Boolean>(false, "Unexpected enforcement decision. Defaulting to prevention.");
	}

	/**
	 * Create a fallback Enforcer for a given CriticalEvent.
	 *
	 * Here we always conservatively suppress the program event in case of an
	 * error. In principle, however, we could use a more optimistic approach.
	 *
	 * @param ev The CriticalEvent from which the Enforcer is constructed.
	 * @return The constructed Enforcer.
	 */
	public static Enforcer fallback(final CriticalEvent ev) {
		return new VerboseValueReplacingEnforcer<Boolean>(false, "Fallback conservative suppression of critical program event, for no enforcement decision could be made.");
	}
}
