package aj.nf;

import java.util.Vector;

/**
 *
 *@author     judda 
 *@created    July 21, 2000 
 */

public class Prefab implements NFObject {
	int PrefabId;
	String corpTick;
	FacilityDesign facilityDesign;
	Location loc;
	
	static int reuse=1;
	static int count = 0;
	
	public Prefab(String cn, String fd, Location l) {
		PrefabId = count;
		count++;
		reuse=1;
		while (Universe.getNFObjectById("PF"+reuse)!=null) {
			reuse++;
		}
		PrefabId=reuse;
		corpTick = cn;
		facilityDesign = (FacilityDesign)Universe.getITThingByName(fd);
		loc = l;
	}
	
	public Prefab(String cn, String fd, Location l, int sid) {
		PrefabId = sid;
		count = Math.max(count, sid + 1);
		corpTick = cn;
		facilityDesign = (FacilityDesign)Universe.getITThingByName(fd);
		loc = l;
	}

	public boolean canConstructAt(Location l) {
		Body b=Universe.getBodyByLocation(l);
		if (facilityDesign==null) return false;
		if (facilityDesign.hasTech("T51")) {//distil
			if (!b.getSurf(l).equalsIgnoreCase("w") ) return false;
		}
		if (facilityDesign.hasTech("T52")) {//condens
			if (!l.isSurface()) return false;
		}
		if (l.isLevel() && facilityDesign.hasTech("T65")) return false;//solar on surface or orbit only
		if (facilityDesign.hasTech("T100")) {//geothermal
			if (!l.isLevel()) return false;
			if (b==null || b.tectonic==0) return false;
		}
		if (facilityDesign.hasTech("T101")) {//tidal
			if (!l.isSurface()) return false;
			if (b==null) return false; 
			if (!b.getSurf(l).equalsIgnoreCase("w") ) return false;
		}
		if (!l.isOrbit() && facilityDesign.hasTech("T102")) return false;//magentic
		return true;
	}
	
	public void setCorpTick(String s) {
		corpTick = s;
	}
	
	public void setLocation(Location l) {
		loc = l;
	}
	
	public String getName() {
		return facilityDesign.getName();
	}
	
	public String getNick() {return getId();}
	public String getId() {
		return"PF" + PrefabId;
	}
	
	public double getValue() {
		if (facilityDesign==null) return -1;
		double t = facilityDesign.getProducedValue();
		return t;
	}
	
	public boolean isMoveable() {
		return true;
	}
	
	public String getCorpTick() {
		return corpTick;
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public int getMass() {
		if (facilityDesign==null) return -1;
		return facilityDesign.getDesignMass();
	}
	
	public String toScanString() {
		return corpTick + "." + getId() + "." + facilityDesign.getName();
	}
	
	public String displayHeader() {return display();}
	public String display() {
		return getId() + ": " + facilityDesign.getName() + " at " + loc.toString()+" "+Stuff.money(getValue());
	}
	
	public String toSaveString() {
		return toGmlPair().toString();
	}
	public GmlPair toGmlPair() {
		Vector v = new Vector();
		GmlPair g = new GmlPair("Tick", corpTick);
		v.addElement(g);
		g = new GmlPair("Loc", loc.toString());
		v.addElement(g);
		g = new GmlPair("PrefabDesign", facilityDesign.getId());
		v.addElement(g);
		g = new GmlPair("PrefabId", PrefabId);
		v.addElement(g);
		g = new GmlPair("Prefab", v);
		return g;
	}
	
	public static Prefab parse(GmlPair g) {
		if( !g.getName().equalsIgnoreCase("Prefab")) {
			return null;
		}
		GmlPair n[] = g.getAllByName("Tick");
		String cn = n[0].getString();
		n = g.getAllByName("PrefabDesign");
		String dn = n[0].getString();
		n = g.getAllByName("PrefabId");
		int fid = (int)(n[0].getDouble());
		n = g.getAllByName("Loc");
		Location l = Location.parse(n[0].getString());
		if(l == null) {
			System.out.println("MyError: bad loc in Prefab parse");
			return null;
		}
		return new Prefab(cn, dn, l, fid);
	}
}

