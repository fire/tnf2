package aj.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    July 21, 2000 
 */
public class StuffIt {

	/**
	 *  Description of the Field 
	 */
	public static String slist[] = {
			"http://clubs.lycos.com/live/Directory/PhotoAlbum.asp?ACT=VOTE&CG=llfae5c88va138400188mohh08&AID=61792&PID=415159&F=1&query=&SI=13&TOK=2000081616408v467mobd19niet38b1q7f76k8"
			};


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		//additinal MyUrl cookies required!
		Vector v = new Vector();
		v.addElement("User-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 4.0; TUCOWS)");
		v.addElement("Cookie: TESTPHOTOVOTE=true; PHOTOCASTVOTE=true;");
		// PHOTOVOTEALREADY=415160; VIPPRIMARY=8%2F16%2F00+4%3A43%3A12+PM");
		v.addElement("Host: clubs.lycos.com");

		MyURL.setAdditional(v);
		if (s.length == 0) {
			System.out.println("FORMAT: java aj.net.StuffIt [<procs> [<millis> [readsome|readall|readnone scrape [<URL>]]]]");
			System.exit(0);
		}
		int procs = Integer.parseInt(s[0]);
		int delay = 0;
		if (s.length > 1) {
			delay = Integer.parseInt(s[1]);
		}
		String name = null;
		int a;
		for (a = 2; a < s.length; a++) {
			if (s[a].toUpperCase().indexOf("READSOME")>=0) {
				Stuffer.doread = true;
			}
			if (s[a].toUpperCase().indexOf("READALL")>=0) {
				Stuffer.doread = true;
				Stuffer.readall = true;
			}
			if (s[a].toUpperCase().indexOf("MAXSCRAPEREUSE:")>=0) {
				try {
					s[a]=s[a].substring(s[a].toUpperCase().indexOf("MAXSCRAPEREUSE:"));
					maxScrapeReuse=Integer.parseInt(s[a].substring(15).trim());
System.out.println("max scrape rate = "+maxScrapeReuse);
				} catch (NumberFormatException NFE) {
					System.out.println("MyError: Bad number in maxScrapeReuse, using 1");
				}
			}
			if (s[a].toUpperCase().indexOf("SCRAPE")>=0) {
				Stuffer.doscrape = true;
			}
			if (s[a].toUpperCase().indexOf("HTTP://") == 0) {
				name = s[a];
			}
		}
		int b;
		for (a = 0; a < procs; a++) {
			if (name == null) {
				for (b = 0; b < slist.length; b++) {
					new Thread(new Stuffer(slist[b], delay)).start();
				}
			}
			else {
				new Thread(new Stuffer(name, delay)).start();
			}
		}
	}


	/*
	 * changes "hello%32there" to "hello there"
	 */
	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static String removeWebEncoding(String s) {
		if (s == null) {
			return null;
		}
		while (s.indexOf("%") >= 0) {
			String enc = s.substring(s.indexOf("%") + 1);
			if (enc.length() > 2) {
				enc = enc.substring(0, 2);
			}
			int e = Integer.parseInt(enc, 16);
			s = s.substring(0, s.indexOf("%")) + (char) (e) + s.substring(s.indexOf("%") + 3);
		}
		return s;
	}


	/*
	 * Simple replace
	 */
	/**
	 *  Description of the Method 
	 *
	 *@param  s     Description of Parameter 
	 *@param  find  Description of Parameter 
	 *@param  rep   Description of Parameter 
	 *@return       Description of the Returned Value 
	 */
	public static String replace(String s, String find, String rep) {
		if (s == null || find == null || rep == null) {
			return s;
		}
		while (s.indexOf(find) >= 0) {
			s = s.substring(0, s.indexOf(find)) + rep + s.substring(s.indexOf(find) + find.length());
		}
		return s;
	}


	/*
	 * Url scraping in URL start at end of BEGIN and end at begining of END
	 */
	/**
	 *  Description of the Method 
	 *
	 *@param  url    Description of Parameter 
	 *@param  begin  Description of Parameter 
	 *@param  end    Description of Parameter 
	 *@return        Description of the Returned Value 
	 */

static String scrapeResult=null;
static int scrapeCount=0;
static int maxScrapeReuse=1;
	public synchronized static String scrape(String url, String begin, String end) {
		if (scrapeResult!=null && scrapeCount<maxScrapeReuse) {
			scrapeCount++;
			//System.out.println("OLD");
			return scrapeResult;
		}
		//System.out.println("New");
		String result = "";
		String all = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(MyURL.getInputStream(url)));
			while (true) {
				String t = br.readLine();
				if (t == null) {
					break;
				}
				all += t;
				if (all.indexOf(begin) >= 0 && all.indexOf(end) > all.indexOf(begin)) {
					br.close();
					break;
				}
			}
		}
		catch (IOException IOE) {
			System.out.println("MyError: Unable to scrape.  Cannot find web page");
		}
		if (all.indexOf(begin) >= 0) {
			result = all.substring(all.indexOf(begin) + begin.length());
		}
		if (result.indexOf(end) > 0) {
			scrapeResult=result.substring(0, result.indexOf(end));
			scrapeCount=0;
			return result.substring(0, result.indexOf(end));
		}
		scrapeResult=null;
		return null;
	}

}

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    July 21, 2000 
 */
class Stuffer implements Runnable {


	String urlname;
	int delay;
	static boolean doread = false, readall = false;
	static boolean doscrape = false;
	static String scrapeUrl = "http://clubs.lycos.com/live/Directory/PhotoAlbum.asp?CG=llfae5c88va138400188mohh08&AID=61792&F=1";
	static String beginscrape = "location = \"../Directory/PhotoVoteCheck.asp?R=%2E%2E%2F";
	static String endscrape = "\";";
	static String find = "\" + pic_id + \"";
	static String rep = 415160 + "";//rep= 415159+"";
	static String prefix = "http://clubs.lycos.com/live/";
	static String postfix = "";

	/**
	 *  Main processing method for the Stuffer object 
	 */


	//number of stuffs until rescraping
	/**
	 *  Constructor for the Stuffer object 
	 *
	 *@param  urlname  Description of Parameter 
	 *@param  delay    Description of Parameter 
	 */
	public Stuffer(String urlname, int delay) {
//		System.out.println("Stuffer running doread=" + doread + " readall=" + readall + " doscrape=" + doscrape + " urlname=" + urlname + " delay=" + delay);
		this.urlname = urlname;
		this.delay = delay;

	}


	/**
	 *  Main processing method for the Stuffer object 
	 */
	public void run() {
		while (true) {
			try {
				System.out.print(".");
				if (doscrape) {
					urlname = StuffIt.scrape(scrapeUrl, beginscrape, endscrape);
					urlname = StuffIt.removeWebEncoding(urlname);
					urlname = StuffIt.replace(urlname, find, rep);
					urlname = prefix + urlname + postfix;
				}
				BufferedReader br = new BufferedReader(new InputStreamReader(MyURL.getInputStream(urlname)));
			
				if (delay > 0) {
					try {
						Thread.sleep((int) (delay * Math.random()));
					}
					catch (Exception E) {
					}
				}
				if (br != null) {
					String s = br.readLine();
					if (doread) {
						if (readall) {
							for (int a = 0; a < 100 && s != null; a++) {
								s = br.readLine();
							}
						}
						else {
							while (s != null) {
								s = br.readLine();
							}
						}
					}
					br.close();
				}
				else {
					System.out.print("\bX");
				}
			}
			catch (IOException IOE) {
			}
		}
	}

}

//LEW GUTMAN - DARPA WORK - NETWORK SECURITY - KNOWS RELATED WORK AT DARPA
//BACK to REY - better light in view of what is alread going on.
/*
 * location = "../Directory/PhotoVoteCheck.asp?R=%2E%2E%2FDirectory%2FPhotoAlbum%2Easp%3FACT%3DVOTE%26CG%3Dllfae5c88va138400188mohh08%26AID%3D61792%26PID%3D" + pic_id + "%26F%3D1%26query%3D%26SI%3D1%26TOK%3D20000816171233ut7km7cprukb40t18bnkd7is";
 * location = "../Directory/PhotoVoteCheck.asp?R=%2E%2E%2FDirectory%2FPhotoAlbum%2Easp%3FACT%3DVOTE%26CG%3Dllfae5c88va138400188mohh08%26AID%3D61792%26PID%3D" + pic_id + "%26F%3D1%26query%3D%26SI%3D1%26TOK%3D2000081617133i21ll5nfk2eavp0ac6fqsgtf8";
 * location = "../Directory/PhotoVoteCheck.asp?R=%2E%2E%2FDirectory%2FPhotoAlbum%2Easp%3FACT%3DVOTE%26CG%3Dllfae5c88va138400188mohh08%26AID%3D61792%26PID%3D" + pic_id + "%26F%3D1%26query%3D%26SI%3D1%26TOK%3D20000816171233ut7km7cprukb40t18bnkd7is";
 * http://clubs.lycos.com/live/Directory/PhotoAlbum.asp?ACT=VOTE&CG=llfae5c88va138400188mohh08&AID=61792&PID=415159&F=1&query=&SI=1&TOK=200008161715ufihda6nbd32rr5md0jm2m3b18
 * http://clubs.lycos.com/live/Directory/PhotoAlbum.asp?ACT=VOTE&CG=llfae5c88va138400188mohh08&AID=61792&PID=415159&F=1&query=&SI=1&TOK=
 * http://clubs.lycos.com/live/Directory/PhotoAlbum.asp?ACT=VOTE&CG=llfae5c88va138400188mohh08&AID=61792&PID=415160&F=1&query=&SI=1&TOK=20000816171233ut7km7cprukb40t18bnkd7is
 * url=scrape(url,"location = \"../"," + pic_id + \",
 * String line=scrapeLine(sourceUrl,beginpattern,endpattern);
 * String line=scrapeLine("http://clubs.lycos.com/live/Directory/PhotoAlbum.asp?CG=llfae5c88va138400188mohh08&AID=61792&F=1"
 * ,"location = \"../",
 * ,"\";");
 * line=removeWebEncoding(line);
 * line=replace(line,pattern,pattern)
 * line=replace(line,"\" + pic_id + \"",415159);
 * String newUrl="http://clubs.lycos.com/live/"+line;
 */
