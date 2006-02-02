package aj.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class HTMLPrinter2 {

	static String nott[] = {"HR", "BR", "P", "FRAME", "IMG", "BASE", "META", "LI", "DD", "PARAM", "AREA","META"};


	//specal case
	public static boolean check(String s) {
		//if (true) return false;
		if (s.startsWith("/")) {
			s=s.substring(1);//return true;
		}
		else if (s.startsWith("-")) {
			return true;
		}
		else if (s.startsWith("!")) {
			return true;
		}
		int a;
		for (a = 0; a < nott.length; a++) {
			if (s.toUpperCase().equals(nott[a])) {
				return true;
			}
		}
		return false;
	}

	public static void parse(String all) {
		Vector v=new Vector();
		all=Stuff.superTrim(all);
		while (all.length()>0) {
			//body
			if (all.indexOf("<")==0 && all.indexOf(">")>0) {
				String fulltoke = all.substring(1, all.indexOf(">")).trim();
				//COMMENT
				if (fulltoke.startsWith("!--") && all.indexOf("-->") > 0) {
//no super trim
					fulltoke = all.substring(1, all.indexOf("-->") + 3);
					all = all.substring(all.indexOf("-->") + 3);
					//cut remark toke
					v.addElement("COMMENT:<"+fulltoke);
					continue;
				}
				else if (fulltoke.indexOf("<") < fulltoke.indexOf(">")) {
					System.out.println("MyError: nested < <>> at " + all.substring(60));
					all = all.substring(all.indexOf(">") + 1);
					v.addElement("JUNK2:<"+fulltoke);
					continue;
				}
				else {
					all = all.substring(all.indexOf(">") + 1);
				}
				String toke = fulltoke.trim().toUpperCase();
				if (toke.indexOf(" ") > 0) {
					toke = toke.substring(0, toke.indexOf(" ")).toUpperCase().trim();
				}
				fulltoke = fulltoke.substring(0, fulltoke.toUpperCase().indexOf(toke.toUpperCase())) + toke + fulltoke.substring(fulltoke.toUpperCase().indexOf(toke.toUpperCase()) + toke.length());
				if (check(toke.toUpperCase()) || toke.startsWith("!")) {
				//else if (toke.startsWith("!")) {
//no super trim
					v.addElement("COMMENT:<"+fulltoke+">");
					continue;
				}
				else if (toke.toUpperCase().startsWith("/")) {
					v.addElement("CLOSE:"+"<"+toke+">");
					continue;
				}
				else {
					v.addElement("OPEN:<"+fulltoke+">");
					continue;
				}
			}
			else {
				String junk=all;
				if (all.indexOf("<")>=0) {
					junk=all.substring(0,all.indexOf("<"));
				}
				all=all.substring(junk.length());
				junk=junk.trim();
				if (junk.length()==0) continue;
				v.addElement("JUNK1:"+junk);
				continue;
			}
		}
		String ind="";
		Vector open=new Vector();
		//for (int a=0;a<v.size();a++) {
			//System.out.println(v.elementAt(a));
		//}
		for (int a=0;a<v.size();a++) {
			String t=(String)v.elementAt(a);
			String tt=t.substring(t.indexOf(":")+1);
			if (t.startsWith("OPEN")) {
				System.out.println(ind+tt);
				ind+="  ";
				open.addElement(t);
			}
			else if (t.startsWith("CLOSE")) {
				int closeto=0;
				String match=t.substring("CLOSE:</".length()).toUpperCase();
				for (int b=open.size()-1;b>=0;b--) {
					String test=(String)open.elementAt(b);
					test=test.substring("OPEN:<".length()).toUpperCase();
					if (test.startsWith(match)){
						closeto=b;
						break;
					}
				}
if (closeto==0) System.out.println("CLOSE TO NOT FOUND "+closeto);
else System.out.println("FOUND!! "+closeto);
				while (open.size()>closeto) {
					if (ind.length()>=2)
						ind=ind.substring(2);
					open.removeElementAt(closeto);
				}
				System.out.println(ind+tt);
			}
			else {
				System.out.println(ind+tt);
			}
		}
	}
	



	public static void main(String s[]) {
		if (s.length!=0) {
			System.out.println("Usage: java aj.misc.HTMLPrinter2 <infile >outfile");
			System.exit(0);
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String all = "";
		try {
			while (true) {
				String S = br.readLine();
				if (S == null) {
					break;
				}
				all += S + "\n";
			}
		}
		catch (IOException E) {
		}

		all = Stuff.superTrim(all);
		HTMLPrinter2.parse(all);

	}

}
