package aj.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import aj.misc.Stuff;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class RelayServer {

	int port;
	ServerSocket SS;
	Vector ALL;
	Vector GROUP;


	/**
	 *  Constructor for the RelayServer object 
	 *
	 *@param  s  Description of Parameter 
	 */
	public RelayServer(int s) {
		ALL = new Vector();
		GROUP = new Vector();
		port = s;
		try {
			SS = new ServerSocket(s);
		}
		catch (IOException IOE) {
			System.out.println("Server busy");
			System.exit(0);
		}
		while (true) {
			Socket S=null;
			try {
				S = SS.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (S==null) continue;
			ALL.addElement(S);
			final Socket SS=S;
			new Thread() {
				public void run() {
					BufferedReader br;
					try {
						br = new BufferedReader(new InputStreamReader(SS.getInputStream()));
						while (true) {
							String s=br.readLine();
							if (s==null) break;
							actionPerformed(SS,s);
						}
					} catch (IOException e) {
						kill(SS);
					}
				}
			}.start();
		}
	}

	
	public void send(Socket S,String s) throws IOException {
		s+="\n";
			S.getOutputStream().write(s.getBytes());
			S.getOutputStream().flush();
	}

	/**
	 *  Description of the Method 
	 *
	 *@param  ae  Description of Parameter 
	 * @throws IOException 
	 */
	public void actionPerformed(Socket S, String line) throws IOException {
		String tok[] = Stuff.getTokens(line);
		if (tok.length == 0) {
			return;
		}
		
		else if (tok[0].equalsIgnoreCase("__CREATE")) {
			//system_command
			//System.out.println("create found "+line);
			RelayGroup RG = new RelayGroup(line);
			for (int a = 0; a < GROUP.size(); a++) {
				RelayGroup rp = (RelayGroup) GROUP.elementAt(a);
				if (rp.dup(RG)) {
					return;
				}
			}
			send(S,"__CREATED " + RG.getName());
			GROUP.addElement(RG);
		}

		else if (tok[0].equalsIgnoreCase("__DESTROY") || tok[0].equalsIgnoreCase("__DELETE")) {
			//name pass
			//System.out.println("destroy found "+line);
			RelayGroup test = new RelayGroup(line);
			for (int a=0; a < GROUP.size(); a++) {
				RelayGroup rp = (RelayGroup) GROUP.elementAt(a);
				if (rp.passDup(test)) {
					send(S,"__DESTROYED " + rp.getName());
					rp.clean();
					GROUP.removeElement(rp);
				}
			}
		}

		else if (tok[0].equalsIgnoreCase("__LIST")) {
			//System.out.println("list found "+line);
			RelayGroup test = new RelayGroup(line);
			for (int a=0; a < GROUP.size(); a++) {
				RelayGroup RG = (RelayGroup) GROUP.elementAt(a);
				if (tok.length == 1 || RG.getName().equalsIgnoreCase(test.getName().toUpperCase())) {
					send(S,"__LIST " + RG.toString());
				}
			}
		}
		else if (tok[0].equalsIgnoreCase("__WHERE")) {
			for (int a=0; a < GROUP.size(); a++) {
				RelayGroup RG = (RelayGroup) GROUP.elementAt(a);
				if (RG.contains(S)) {
					send(S,"__CONNECTED " + RG.getName() + " " + RG.getLocation(S));
				}
			}
		}
		else if (tok[0].equalsIgnoreCase("__MODIFY")) {
			//name desc pass
			//System.out.println("modify found "+line);
			RelayGroup test = new RelayGroup(line);
			for (int a=0; a < GROUP.size(); a++) {
				RelayGroup RG = (RelayGroup) GROUP.elementAt(a);
				if (RG.getName().equals(test.getName()) && RG.getPass().equals(test.getPass())) {
					send(S,RG.modify(test));
				}
			}
		}
		else if (tok[0].equalsIgnoreCase("__JOIN")) {
			//System.out.println("join found "+line);
			RelayGroup test = new RelayGroup(line);
			for (int a=0; a < GROUP.size(); a++) {
				RelayGroup RG = (RelayGroup) GROUP.elementAt(a);
				if (RG.getName().equals(test.getName()) && RG.getPass().equals(test.getPass())) {
					send(S,RG.add(S));
					return;
				}
			}
			send(S,"__JOIN FAILED");
			return;
		}
		else if (tok[0].equalsIgnoreCase("__QUIT")) {
			//System.out.println("quit found "+line);
			RelayGroup test = new RelayGroup(line);
			for (int a=0; a < GROUP.size(); a++) {
				RelayGroup RG = (RelayGroup) GROUP.elementAt(a);
				if (RG.getName().equals(test.getName())) {
					send(S,RG.remove(S));
				}
			}
		}
		else if (tok[0].equalsIgnoreCase("__HELP")) {
String help="Multi Relay Server\n"+
"__create name:<name> pass:<password> max:<2...999> refill:<1/0>\n"+
"__modify name:<name> pass:<password> max:<2...999> refill:<1/0>\n"+
"__destroy name:<name> pass:<password>\n"+
"__join name:<name> pass:<password>\n"+
"__quit name:<name>\n"+
"__list		;list all groups\n"+
"__list name:<name>     ;report list by name\n"+
"__where	 	;list all groups you are in\n"+
"__number name:<name>	;reports your id number in list\n"+
"__help		;this list\n";
send(S,help);
		}
		else {
			//broadcast
			//System.out.println("broadcast found "+line);
			for (int a=0; a < GROUP.size(); a++) {
				RelayGroup v = (RelayGroup) GROUP.elementAt(a);
				if (v.contains(S)) {
					//System.out.println("Broad casting "+line+" in "+v.getName());
					v.broadcast(line, S);
				}
			}
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  NL  Description of Parameter 
	 */
	public void kill(Socket S) {
		
		if (ALL.contains(S)) ALL.removeElement(S);
		for (int a=0; a < GROUP.size(); a++) {
			RelayGroup v = (RelayGroup) GROUP.elementAt(a);
			if (v.contains(S)) v.remove(S);
		}
		try {
			if (!S.isClosed()) S.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		if (s.length != 1) {
			System.out.println("FORMAT: java RelayServer <port>");
			System.exit(0);
		}
		try {
			new RelayServer(Integer.parseInt(s[0]));
		}
		catch (NumberFormatException NFE) {
		}
	}

}

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
class RelayGroup {
	int maxsize;

	String name = null, pass = null;
	int max;
	boolean refill = false;
	boolean open = true;
	int size;
	Socket ALL[];


	/**
	 *  Constructor for the RelayGroup object 
	 *
	 *@param  s  Description of Parameter 
	 */
	public RelayGroup(String s) {
		String tok[] = aj.misc.Stuff.getTokens(s," \t");
		max=2;
		refill=true;
		pass=name="BLANK";
		for (int a=0;a<tok.length;a++) {
			if (tok[a].toUpperCase().startsWith("NAME:")) {
				name=tok[a].substring(5).trim();
			}
			if (tok[a].toUpperCase().startsWith("PASS:")) {
				pass=tok[a].substring(5).trim();
			}
			if (tok[a].toUpperCase().startsWith("MAX:")) {
				try {
					max=Integer.parseInt(tok[a].substring(4).trim());
				} catch (NumberFormatException NFE) {}
			}
			if (tok[a].toUpperCase().startsWith("REFILL:")) {
				String ttt=tok[a].substring(7).trim();
				refill= (ttt.equals("1")||ttt.equalsIgnoreCase("true"));
			}
  		}
		open = true;
		size = 0;
		ALL = new Socket[max];
	}


	/**
	 *  Gets the Location attribute of the RelayGroup object 
	 *
	 *@param  NL  Description of Parameter 
	 *@return     The Location value 
	 */
	public int getLocation(Socket NL) {
		for (int a=0; a < ALL.length; a++) {
			Socket nl = ALL[a];
			if (nl == NL) {
				return a + 1;
			}
		}
		return -1;
	}


	/**
	 *  Gets the Pass attribute of the RelayGroup object 
	 *
	 *@return    The Pass value 
	 */
	public String getPass() {
		return pass;
	}


	/**
	 *  Gets the Name attribute of the RelayGroup object 
	 *
	 *@return    The Name value 
	 */
	public String getName() {
		return name;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  r  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public String modify(RelayGroup r) {
		refill = r.refill;
		max = r.max;
		Socket n[] = new Socket[max];
		for (int a=0; a < n.length && a < ALL.length; a++) {
			n[a] = ALL[a];
		}
		ALL = n;
		open = true;
		return "__MODIFIED";
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  r  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public boolean dup(RelayGroup r) {
		return name.equals(r.getName());
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  r  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public boolean passDup(RelayGroup r) {
		return name.equals(r.getName()) && pass.equals(r.getPass());
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  NL  Description of Parameter 
	 *@return     Description of the Returned Value 
	 */
	public boolean contains(Socket NL) {
		if (NL == null) {
			return false;
		}
		for (int a=0; a < ALL.length; a++) {
			if (NL == ALL[a]) {
				return true;
			}
		}
		return false;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  NL  Description of Parameter 
	 *@return     Description of the Returned Value 
	 */
	public String remove(Socket S) {
		for (int a=0; a < ALL.length; a++) {
			if (ALL[a] == S) {
				ALL[a] = null;
				if (size == max) {
					open = refill;
				}
				size--;
				return "__REMOVED from " + getName();
			}
		}
		return "__REMOVE not in " + getName();
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  NL  Description of Parameter 
	 *@return     Description of the Returned Value 
	 */
	public String add(Socket NL) {
		for (int a=0; size < max && a < ALL.length; a++) {
			if (ALL[a] == null) {
				ALL[a] = NL;
				size++;
				if (size == max) {
					open = false;
					return "__JOINED " + getName() + " CHANEL_FULL " + getLocation(NL);
				}
				return "__JOINED " + getName() + " CHANEL_FILLING " + getLocation(NL);
			}
		}
		return "__JOIN FAILED" + getName();
	}


	/**
	 *  Description of the Method 
	 */
	public void clean() {
		ALL = null;
		name = pass = null;
		max = 0;
		open = false;
		refill = false;
	}

	public void send(Socket S,String line) throws IOException {
		line=line.trim()+"\n";
		S.getOutputStream().write(line.getBytes());
		S.getOutputStream().flush();
	}

	/**
	 *  Description of the Method 
	 *
	 *@param  line  Description of Parameter 
	 *@param  NL    Description of Parameter 
	 * @throws IOException 
	 */
	public void broadcast(String line, Socket S) throws IOException {
		for (int a=0; a < ALL.length; a++) {
			Socket s = ALL[a];
			if (s == S || s == null) {
				continue;
			}
			else {
				send(s,line);
			}
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public String toString() {
		return "GROUP> " + name + " " + pass + " (" + size + "/" + max + ") open=" + open + " refill=" + refill;
	}

}
