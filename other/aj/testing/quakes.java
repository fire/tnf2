package aj.testing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import aj.io.MLtoText;

public class quakes {


	public static void main(String s[]) throws IOException {
//		System.out.println("Name, url, Latitued, Longitude");
		try {
			//RSS feed
			//http://earthquake.usgs.gov/recenteqsww/catalogs/eqs7day-M2.5.xml
//			String masterlist="http://quake.wr.usgs.gov/recenteqs/Quakes/quakes0.html";
//			BufferedReader br=new BufferedReader(new InputStreamReader(new URL(masterlist).openStream()));
			BufferedReader br=new BufferedReader(new FileReader("c:/quakes.txt"));
			String all="";
			System.out.println("reading");
			while (true) {
				String ss=br.readLine();
				if (ss==null) break;
				if (ss.length()==0) continue;
//				readBouy(ss);
				all+=ss.toUpperCase();
			}
			System.out.println("lat, lon, mag");
			while (all.indexOf("MAP</A>")>=0) {
				String sub=all.substring(0,all.indexOf("MAP</A>"));
				all=all.substring(all.indexOf("MAP</A>")+3);
//				System.out.println("sub="+sub);
//				//map</A> 1.6  <A HREF="/recenteqs/Quakes/ci14155392.html">2005/06/16 14:36:24 34.049N 116.990W 11.6</A>    6 km ( 4 mi) ENE of Yucaipa, CA <A HREF="/recenteqs/Maps/117-34.html">
				String lat="",lon="",link="",icon="",mag="",time="";
				if (sub.indexOf("<A HREF=\"")>=0) sub=sub.substring(sub.indexOf("<A HREF=\"")+9).trim();
				if (sub.indexOf("\">")>=0) link=sub.substring(0,sub.indexOf("\">"));
				link="http://quake.wr.usgs.gov"+link;
				if (sub.indexOf("\">")>=0) sub=sub.substring(sub.indexOf("\">")+2);

				if (sub.indexOf(":")>=0) time=sub.substring(0,sub.lastIndexOf(":")+3).trim();
				if (sub.indexOf(":")>=0) sub=sub.substring(sub.lastIndexOf(":")+3).trim();
//				if (sub.indexOf("\"")>=0) sub=sub.substring(sub.indexOf("\"")+1);
				if (sub.indexOf(" ")>=0) lat=sub.substring(0,sub.indexOf(" "));
				if (sub.indexOf(" ")>=0) sub=sub.substring(sub.indexOf(" ")).trim();
				if (sub.indexOf(" ")>=0) lon=sub.substring(0,sub.indexOf(" "));
				if (sub.indexOf(" ")>=0) sub=sub.substring(sub.indexOf(" ")).trim();
				if (sub.indexOf("<")>=0) mag=sub.substring(0,sub.indexOf("<"));
//				if (lat.indexOf("N")<0 && lat.indexOf("S")<0) continue;
//				if (lon.indexOf("E")<0 && lon.indexOf("W")<0) continue;
				try {
					Double.parseDouble(mag);
				} catch (NumberFormatException nfe){
					continue;
				}
				
				System.out.print(""+lat);
				System.out.print(","+lon);
				System.out.print(","+mag);
				System.out.print(","+link);
				System.out.print(","+time);
				System.out.println(","+icon);
				
			}
		} catch (IOException IOE) {}
	}

	public static void readBouy(String s) throws IOException {
		URLConnection url=new URL(s).openConnection();
		BufferedReader br=new BufferedReader(new InputStreamReader(url.getInputStream()));
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

