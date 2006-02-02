package aj.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MLtoText {
	public static void main(String s[])  {
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		String all="";
		try {
			while (true) {
				String t=br.readLine();
				if (t==null) break;
				t=t.trim();
				if (t.equals("")) continue;
				all+=" "+t+"\n";
			}
		} catch (IOException ioe) {}

		System.out.println(""+cutML(all));
	}

	public static String cutMLaddSpaces(String all) {
		return cutML(all," ");
	}
	public static String cutML(String all) {
		return cutML(all,"");
	}
	/*
	 * Cuts all ML <!--  --> and <any/> and replaces with space
	 */
	private static String cutML(String all,String space) {
		if (all==null) return null;
		String res="";
		int last=0;
		int lastbegin=all.indexOf("<!--",last);
		int lastend=all.indexOf("-->",lastbegin);
		int count=0;
		String sub="";
		while (lastbegin>=0 && lastend>=0) {
				sub+=all.substring(last,lastbegin)+space;
				count++;
				if (count>100) {
					count=0;
					res+=sub;
					sub="";
				}
				last=lastend+3;
				lastbegin=all.indexOf("<!--",last);
				lastend=all.indexOf("-->",lastbegin);
		}
		res+=sub+all.substring(last)+space;
		all=res;
		res="";
		last=0;
		lastbegin=all.indexOf("<",last);
		lastend=all.indexOf(">",lastbegin);
		count=0;
		sub="";
		while (lastbegin>=0 && lastend>=0) {
				sub+=all.substring(last,lastbegin)+space;
				count++;
				if (count>100) {
					count=0;res+=sub;sub="";
				}
				last=lastend+1;
				lastbegin=all.indexOf("<",last);
				lastend=all.indexOf(">",lastbegin);
		}
		res+=sub+all.substring(last)+space;
		return res;
	}
	
	
}
