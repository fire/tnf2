package aj.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Ceaser {

	public static String common[] ={
"a","i",
"it","do","is","or","me","am","to","of","we","us","if","at",
"who","you","him","her","run","did","not","but","and","was","has","for","from","the","hit","try","out","can",
"then","when"
};
	public static void main(String SS[]) {
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		String all="";
		while (true) {
			try {
				String s=br.readLine();
				if (s==null) break;
				else all+="\t"+s;
			} catch (IOException ioe ){ 
				break;
			}
		}
		all=all.trim();
		System.out.println ("All="+all);
		String num="";
		if (all.indexOf(" ")>0) num=all.substring(0,all.indexOf(" ")).trim();
		boolean isnum=true;
		try {
			Integer.parseInt(num);
		} catch (NumberFormatException nfe) {
			isnum=false;
		}
		if (all.indexOf("  ")>0 || isnum) {//numbers convert
			System.out.println ("Number based");
			String all2="";
			while (all.length()>0) {
				String t=all;
				if (all.indexOf(" ")>0) {
					t=all.substring(0,all.indexOf(" "));
					all=all.substring(all.indexOf(" ")+1);
				}
				else all="";
				try {
					all2+=(char)('A'+Integer.parseInt(t)-1);
				} catch (NumberFormatException nfe) {
					all2+=t;
				}
				if (all.startsWith(" ")) all2+=" ";
				all=all.trim();
			}
			all=all2.trim();
			System.out.println ("Number Code="+all);
		}
		if (test(all)) {
			System.out.println ("Raw (A=A) >"+all);
		}
		doCeaser(all);
	}

	public static void doCeaser(String all) {
		for (int a=0;a<26;a++) {
			String s=shift(all,a);
			if (test(s)) {
				System.out.println("Ceaser Shift (A="+shift("A",a)+") >"+shift(all,a));
			}
		}
	}

	public static boolean test(String s) {
		for (int a=0;a<common.length;a++) {
			if (s.toUpperCase().indexOf((" "+common[a]+" ").toUpperCase())>0) {
				return true;
			}
		}
		return false;
	}

	public Ceaser() {
	}

//lok for common letters
//binary xor key
//non uniform letter transform (a=C, b=F ..)
//look for incidence of conincidence _ repated patterns

//test (check 2-4 letter each word for dictionary match)

	public static String shift(String s,int shift) {
		String r="";	
		for (int a=0;a<s.length();a++) {
			char c=s.charAt(a);
			boolean cap=false;
			if (c>='A' && c<='Z') {
				c=(char)(c-'A');
				cap=true;
			}
			else if (c>='a' && c<='z') {
				c=(char)(c-'a');
			}
			else {
				r+=c;
				continue;
			}
			c=(char)((c-1+shift)%26);
			if (cap) c=(char)(c+'A');
			else c=(char)(c+'a');
			r+=c;
		}
		return r;
	}
}
