package ftp.cliseau;

import net.cliseau.lib.enforcer.PermittingEnforcer;

public class FTP_Permitter extends PermittingEnforcer{
	
	public void before(){
		System.out.println("-------------------Permitted-------------------");
	}

}
