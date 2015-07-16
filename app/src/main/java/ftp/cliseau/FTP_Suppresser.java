package ftp.cliseau;

import net.cliseau.lib.enforcer.SuppressingEnforcer;

public class FTP_Suppresser extends SuppressingEnforcer{

	public void before(){
		System.out.println("-------------------Suppressed-------------------!");
	}
}
