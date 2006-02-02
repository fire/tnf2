package aj.misc;


/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class Sleeper {

	String command;


	/**
	 *  Constructor for the Sleeper object 
	 *
	 *@param  s  Description of Parameter 
	 */
	public Sleeper(String s[]) {
		if (s.length != 2) {
			System.out.println("FORMAT: java Sleeper <commandfile> <delay_secs>");
			System.exit(0);
		}
		command = s[0];
		int delay = 0;
		try {
			delay = 1000 * Integer.parseInt(s[1]);
		}
		catch (NumberFormatException NFE) {
			System.out.println("FORMAT: java Sleeper <commandfile> <delay_secs>");
			System.exit(0);
		}
		long t = System.currentTimeMillis();
		while (true) {
			while (t + delay > System.currentTimeMillis()) {
				try {
					Thread.sleep(delay - (System.currentTimeMillis() - t));
				}
				catch (InterruptedException IE) {
				}
			}
			t = System.currentTimeMillis();
			process();
		}
	}


	/**
	 *  Description of the Method 
	 */
	public void process() {
		String tar = command;
		while (tar.indexOf("\"") >= 0) {
			tar = tar.substring(0, tar.indexOf("\"")).trim() + tar.substring(tar.indexOf("\"") + 1);
		}
		System.out.println("Exe called for " + tar);
		Runtime t = Runtime.getRuntime();
		try {
			Process p = t.exec(Stuff.getTokens(tar, " \t"));
			//      BufferedReader br=new BufferedReader(new InputStreamReader (p.getInputStream()));
			//      while (br.readLine()!=null);
		}
		catch (Exception E) {
			System.out.println("myError: Runtime Error?" + E);
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		new Sleeper(s);
	}
}
