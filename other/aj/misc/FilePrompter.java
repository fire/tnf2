package aj.misc;

import java.awt.FileDialog;
import java.awt.Frame;

/*
 * FilePrompter class
 * originally written by Ron Welte.
 * -last modified 1/7/98
 */

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class FilePrompter {

	//static funcitons
	/**
	 *  Description of the Method 
	 *
	 *@param  f         Description of Parameter 
	 *@param  fileType  Description of Parameter 
	 *@param  choice    Description of Parameter 
	 *@return           Description of the Returned Value 
	 */
	public static String GetFilename(Frame f, String fileType, String choice) {
		FileDialog d;
		if (choice.equalsIgnoreCase("load")) {
			d = new FileDialog(f, "Read File", FileDialog.LOAD);
		}
		else {
			d = new FileDialog(f, "Save File", FileDialog.SAVE);
		}
		d.setFile(fileType);
		d.setVisible(true);
		if (d.getFile() == null) {
			return null;
		}
		String filename = d.getDirectory() + d.getFile();
		return filename;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  fileType  Description of Parameter 
	 *@param  choice    Description of Parameter 
	 *@return           Description of the Returned Value 
	 */
	public static String GetFilename(String fileType, String choice) {
		Frame bogusFrame = new Frame();
		String filename = GetFilename(bogusFrame, fileType, choice);
		bogusFrame.dispose();
		return filename;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  fileType  Description of Parameter 
	 *@return           Description of the Returned Value 
	 */
	public static String GetFilename(String fileType) {
		Frame bogusFrame = new Frame();
		String filename = GetFilename(bogusFrame, fileType, "load");
		bogusFrame.dispose();
		return filename;
	}
}
