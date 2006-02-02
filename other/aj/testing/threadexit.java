package aj.testing;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 29, 2000 
 */
public class threadexit implements Runnable {

	/**
	 *  Main processing method for the Th object 
	 */
	public void run() {
		System.out.println("start th");
		while (true) {
		}
		//System.out.println("stop th");
	}
	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		System.out.println("start");
		new Thread(new threadexit()).start();
		System.out.println("stop");
		System.exit(0);
	}

}
