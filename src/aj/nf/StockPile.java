package aj.nf;

import java.util.Vector;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    July 21, 2000 
 */

public class StockPile implements NFObject {
	String corpTick;
	String material;
	int amount;
	Location loc;
	int stockid;
	
	static int count = 1;
	static int reuse=1;
	
	public StockPile(String cn, String m, int a, Location l) {
		stockid = count;
		count++;
		//reuse=1;
		while (Universe.getNFObjectById("SP"+reuse)!=null) {
			reuse++;
		}
		stockid=reuse;
		corpTick = cn;
		material = m;
		amount = a;
		loc = l;
	}
	
	public StockPile(String cn, String m, int a, Location l, int sid) {
		stockid = sid;
		count = Math.max(count, sid + 1);
		corpTick = cn;
		material = m;
		amount = a;
		loc = l;
	}
	
	public void setCorpTick(String s) {
		corpTick = s;
	}
	
	public void setLocation(Location l) {
		loc = l;
	}
	
	public String getId() {
		return"SP" + stockid;
	}
	public String getNick() {return getId();}
	
	public double getValue() {
		return Universe.getMarketValue(material) * amount;
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
	
	public int getAmount() {
		return amount;
	}
	
	public int getMass() {
		return amount;
	}
	
	public String getMaterial() {
		return material;
	}
	
	public StockPile steal(String cn, int amt) {
		amount -= amt;
		return new StockPile(cn, material, amt, loc);
	}
	
	public StockPile take(int amt) {
		amount -= amt;
		return new StockPile(corpTick, material, amt, loc);
	}
	
	public void give(int amt) {
		amount += amt;
	}
	
	public boolean merge(StockPile s) {
		if (s==this) return false;
		if (s.corpTick.equals("AUCTION")) return false;//don't merge auction items
		if(s.corpTick.equalsIgnoreCase(corpTick) && s.material.equalsIgnoreCase(material) && s.loc.equals(loc)) {
			amount += s.amount;
			s.amount=0;
			return true;
		}
		return false;
	}
	
	public String toScanString() {
		return corpTick + "." + getId() + "." + amount + "." + material;
	}
	
	public String displayHeader() {return display();}
	public String display() {
		return getId() + ": Stockpile of " + amount + " " + material + " at " + loc.toString()+" "+Stuff.money(getValue());
	}
	
	public String toSaveString() {
		return toGmlPair().toString();
	}
	public GmlPair toGmlPair() {
		Vector v = new Vector();
		GmlPair g = new GmlPair("Tick", corpTick);
		v.addElement(g);
		g = new GmlPair("Material", material);
		v.addElement(g);
		g = new GmlPair("Amount", amount);
		v.addElement(g);
		g = new GmlPair("Loc", loc.toString());
		v.addElement(g);
		g = new GmlPair("StockId", stockid);
		v.addElement(g);
		g = new GmlPair("Stockpile", v);
		return g;
	}
	
	public static StockPile parse(GmlPair g) {
		if( !g.getName().equalsIgnoreCase("Stockpile")) {
			return null;
		}
		GmlPair n[] = g.getAllByName("Tick");
		String cn = n[0].getString();
		n = g.getAllByName("Material");
		String m = n[0].getString();
		n = g.getAllByName("Amount");
		int a = (int)(n[0].getDouble());
		n = g.getAllByName("StockId");
		int sid = (int)(n[0].getDouble());
		n = g.getAllByName("Loc");
		Location l = Location.parse(n[0].getString());
		return new StockPile(cn, m, a, l, sid);
	}
}

