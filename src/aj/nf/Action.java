package aj.nf;
import java.util.Hashtable;
import java.util.Vector;

/**
 *@author     judda 
 *@created    July 21, 2000 
 */

public class Action {
	String corptick;
	long completetime;
	String line;
	Location location;
	
	public String getCorpTick() {
		return corptick;
	}
	
	public String getReport() {
		return"Action: " + Active.myTimeFormat(completetime) + " " + line + " at location " + location;
	}
	
	public Action(String c, long t, String l, Location lc) {
		corptick = c;
		completetime = t;
		line = l;
		location = lc;
		while(line.indexOf("\"") >= 0) {
			line = line.substring(0, line.indexOf("\"")) + line.substring(line.indexOf("\"") + 1).trim();
		}
	}
	
	public GmlPair toGmlPair() {
		Vector v = new Vector();
		v.addElement(new GmlPair("tick", corptick));
		v.addElement(new GmlPair("completetime", completetime + ""));
		v.addElement(new GmlPair("location", location.toString()));
		v.addElement(new GmlPair("line", line));
		return new GmlPair("action", v);
	}
	
	public boolean complete(long t) {
		return t > completetime;
	}
	
	public void post() {
		if(line.toUpperCase().startsWith("PRODUCE") || line.toUpperCase().startsWith("PURCHASE")) {
			String n[] = Stuff.getTokens(line, " \t");
			String designId = n[1];
			if (Main.DEBUG) System.out.println("DEBUG: POSTING ACTION");
			ITThing it = Universe.getITThingByName(designId);
			if(it == null) {
				System.out.println("MyError: bad id in action " + designId);
			}
			if(it instanceof ActiveDesign) {
				if (Main.DEBUG) System.out.println("CREATING ACTIVE");
				Active AA = new Active(corptick, designId, location, - 1, "", "", new Hashtable());
				Universe.add(AA);
				Corp c=Universe.getCorpByTick(corptick);
				if (c==null) {
					return;
				}
				c.addReport("" + (AA.isShip()?"Ship":"") + (AA.isRobot()?"Robot":"") + " " + AA.getName() + " complete at " + location + " ID=" + AA.getId() + " on " + Active.myTimeFormat(completetime) + "\n");
			}
			else if(it instanceof FacilityDesign) {
				if (Main.DEBUG) System.out.println("CREATING PREFAB");
				Prefab p = new Prefab(corptick, designId, location);
				Universe.add(p);
				Corp c=Universe.getCorpByTick(corptick);
				if (c==null) {
					return;
				}
				c.addReport("" + p.getName() + " Prefab complete at " + location + " ID=" + p.getId() + " on " + Active.myTimeFormat(completetime) + "\n");
			}
			else {
				System.out.println("MyError: UNKNOWN PRODUCTION ACTION. " + line + "\n" + this);
			}
		}
	}
	
	public static Action parse(GmlPair g) {
		GmlPair n[] = g.getAllByName("tick");
		String cn = n[0].getString();
		n = g.getAllByName("completetime");
		long ct = (long)(n[0].getDouble());
		n = g.getAllByName("location");
		Location l = Location.parse(n[0].getString());
		n = g.getAllByName("line");
		String ln = n[0].getString();
		return new Action(cn, ct, ln, l);
	}
}


