package ftp.cliseau;


import net.cliseau.runtime.javacor.EnforcementDecision;
import net.cliseau.runtime.javacor.CriticalEvent;
import net.cliseau.runtime.javatarget.Enforcer;
import net.cliseau.runtime.javatarget.EnforcerFactory;
import net.cliseau.lib.enforcer.PermittingEnforcer;
import net.cliseau.lib.enforcer.SuppressingEnforcer;
import net.cliseau.lib.enforcer.VerboseValueReplacingEnforcer;

//import net.cliseau.lib.policy.ChineseWallPolicy;



public class FTP_EnforcerFactory implements EnforcerFactory{
	// this is created by Gongbin
	public static Enforcer fromDecision(final EnforcementDecision ed) {
		FTP_EnforcementDecision FTPDecision = (FTP_EnforcementDecision) ed;
		if (FTPDecision != null) {
			switch (FTPDecision.decision) {
				case PERMIT:
					return new FTP_Permitter();
				case REJECT:
					return new FTP_Suppresser();
				case UNKNOWN:
					return new FTP_Unknown();
			}
		}
		return new FTP_Suppresser();
	}
	
	public static Enforcer fallback(final CriticalEvent ev) {
		return new FTP_Suppresser();
	}
	


}
