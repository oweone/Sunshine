package net.cliseau.AnomicFTPD;

import java.lang.reflect.Field;
import java.io.File;
import net.cliseau.runtime.javacor.CriticalEvent;
import net.cliseau.runtime.javatarget.CriticalEventFactory;
import net.cliseau.lib.policy.ChineseWallPolicy;
import de.anomic.ftpd.ftpdControl;

/**
 * Factory class for CriticalEvent objects of the AnomicFTPD instantiation.
 *
 * For each pointcut there is a static factory method creating CriticalEvent
 * objects from the respective join point.
 *
 * @see ChineseWallPolicy.Event
 */
public class ChineseWallEventFactory implements CriticalEventFactory {
	/**
	 * Factory for CriticalEvent from "fileOpCheck" pointcuts.
	 *
	 * This method assumes that a pointcut named "fileOpCheck" is defined which has
	 * two parameters. The actual values of these parameters at runtime are
	 * passed to this method to construct a critical event from it.
	 *
	 * @param control The FTP session control object on which the request shall be performed.
	 * @param file The file object that was requested.
	 * @return The constructed CriticalEvent.
	 */
	public static CriticalEvent fileOpCheck(ftpdControl control, File file) {
		String userName = "";
		try {
			Field userNameField = control.getClass().getSuperclass().getDeclaredField("user");
			userNameField.setAccessible(true);
			userName = (String)userNameField.get(control);
		} catch (Exception e) { } // should show a warning
		String[] fileInfo = file.getName().split(":");
		return new ChineseWallPolicy.Event(
			userName,
			fileInfo[0] /* COI */,
			fileInfo[1] /* company */,
			fileInfo[2] /* file name */);
	}
}
