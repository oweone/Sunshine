package ftp.cliseau;

import android.content.res.AssetManager;
import android.util.Log;

import com.example.android.sunshine.app.ForecastFragment;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.cliseau.runtime.javacor.CriticalEvent;
import net.cliseau.runtime.javatarget.CriticalEventFactory;
import net.cliseau.lib.policy.ChineseWallPolicy;
import de.anomic.ftpd.ftpdControl;

public class FTP_CriticalEventFactory implements CriticalEventFactory{

	//FIXME: the two arguments passed in might be wrong, consider this later!!!

	public static FTP_CriticalEvent readFile(String userName, String roleProperties, String fileName){
		String userRole = "";
		int JPGAccess = -1, PNGAccess = -1, TXTAccess = -1;

			//JPGAccess
			if (roleProperties.contains("jpg1")){
				JPGAccess = 1;
			}else if(roleProperties.contains("jpg0")){
				JPGAccess = 0;
			}
			//PNGAccess
			if (roleProperties.contains("png1")){
				PNGAccess = 1;
			}else if(roleProperties.contains("png0")){
				PNGAccess = 0;
			}
			//TXTAccess
			if (roleProperties.contains("txt1")){
				TXTAccess = 1;
			}else if(roleProperties.contains("txt0")){
				TXTAccess = 0;
			}

		//String fileName = file.getName();
		return new FTP_CriticalEvent(userName,PNGAccess,JPGAccess,TXTAccess,userRole, fileName);
	}
}
