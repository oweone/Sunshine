package ftp.cliseau;

import net.cliseau.lib.enforcer.SuppressingEnforcer;

/**
 * Created by Beene on 7/8/15.
 */
public class FTP_Unknown extends SuppressingEnforcer {
    public void before(){
        System.out.println("-------------------Suppressed-------------------!");
    }
}
