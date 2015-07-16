package ftp.cliseau;

//import net.cliseau.lib.policy.ChineseWallPolicy.Event;
//import net.cliseau.lib.policy.ChineseWallPolicy.Decision.Value;
import net.cliseau.runtime.javacor.EnforcementDecision;

public class FTP_EnforcementDecision implements EnforcementDecision{
	
	/** The critical event to which the decision belongs. */
	//public Event ev; this is changed 

	/** Type for decisions. */
	public enum Value { PERMIT, REJECT, UNKNOWN };

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
	public FTP_EnforcementDecision(final Value decision, final String reason) {
		//this.ev = ev;
		this.decision = decision;
		this.reason = reason;
	}
}