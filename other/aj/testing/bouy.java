package aj.testing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import aj.io.MLtoText;

public class bouy {
	public static void main(String s[]) throws IOException {
		System.out.println("Name, url, Latitued, Longitude");
		try {
		BufferedReader br=new BufferedReader(new FileReader("bouylist.txt"));
		while (true) {
			String ss=br.readLine();
			if (ss==null) break;
			if (ss.length()==0) continue;
			readBouy(ss);
		}
		} catch (IOException IOE) {}
	}

	public static void readBouy(String s) throws IOException {
		URLConnection url=new URL(s).openConnection();
		BufferedReader br=new BufferedReader(new InputStreamReader(url.getInputStream()));
		String all="";
		while (true) {
			String ss=br.readLine();
			if (ss==null) break;
			
			if ((ss.indexOf(" N ")>=0 || ss.indexOf(" S ")>=0) && (ss.indexOf(" W ")>=0 || ss.indexOf(" E ")>=0)) {
				ss=MLtoText.cutMLaddSpaces(ss);
				if (ss.indexOf("(")>=0) ss=ss.substring(0,ss.indexOf("("));
				String name=s.substring(s.indexOf("station=")+8);
				if (ss.indexOf(" N ")>=0) ss=ss.substring(0,ss.indexOf(" N ")+3)+","+ss.substring(ss.indexOf(" N ")+3);
				if (ss.indexOf(" S ")>=0) ss=ss.substring(0,ss.indexOf(" S ")+3)+","+ss.substring(ss.indexOf(" S ")+3);
				System.out.println(name+","+s+","+ss);
				break;
			}
		}
	}
}

