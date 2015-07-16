package ftp.cliseau;

import java.io.File;

import net.cliseau.runtime.javacor.CriticalEvent;

public class FTP_CriticalEvent implements CriticalEvent{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public final String userName;

	//1 means permitted, 0 means rejected, -1 means unknown
	public int PNGAccess;

	public int JPGAccess;
	
	public int TXTAccess;
	
	public String userRole;
	
	public String fileName;
	
	public FTP_CriticalEvent(String userName,int PNGAccess,int JPGAccess,
							 int TXTAccess, String userRole, String fileName){
		this.userName = userName;
		this.PNGAccess = PNGAccess;
		this.JPGAccess = JPGAccess;
		this.TXTAccess = TXTAccess;
		this.userRole = userRole;
		this.fileName  = fileName;
	}
	
}
