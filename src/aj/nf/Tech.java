package aj.nf;

import java.util.*;
import java.io.*;

/**
 *
 *@author     judda 
 *@created    July 21, 2000 
 */

public class Tech implements ITThing {
	String name;
	String type;
	Vector prerequisits;
	String notes = "";
	Vector partners = new Vector();
	Vector productionCosts = new Vector();
	Vector provideList = new Vector();
	Vector consumeList = new Vector();
	double leasePrice = - 1;
	int techId;
	int mass;
	
	static int count = 1;
	
	public Tech(String n, Vector p, Vector pc, double lp, String ty, Vector pr) {
		count++;
		techId = count;
		type = ty;
		prerequisits = pr;
		name = n;
		partners = p;
		productionCosts = pc;
		leasePrice = lp;
		calcMass();
	}
	
	public Tech(String n, Vector p, Vector pc, double lp, int sid, String ty, Vector pr) {
		if(sid == - 1) {
			sid = count;
			count++;
		}
		techId = sid;
		count = Math.max(count, sid + 1);
		type = ty;
		prerequisits = pr;
		name = n;
		partners = p;
		productionCosts = pc;
		leasePrice = lp;
		calcMass();
	}
	
	public void payLease() {
		if(isPublic())return;
		if(leasePrice <= 0)return;
		double l = leasePrice / partners.size();
		for(int a = 0; a < partners.size(); a++) {
			String p = (String)partners.elementAt(a);
			Corp c = Universe.getCorpByTick(p);
			if(c == null) {
				partners.removeElement(p);
				a--;
				continue;
			}
			c.receiveLease(name, l);
		}
	}
	
	public void setLeasePrice(double lp) {
		leasePrice = lp;
		if (leasePrice==0) {
			partners=new Vector();
			partners.addElement("public");
		}
	}
	
	public String getId() {
		return"T" + techId;
	}
	
	public boolean validForType(String t) {
		return type.toUpperCase().indexOf(t.toUpperCase()) >= 0;
	}
	
	public int getMultiplyerForType(String t) {
		if(type.indexOf("r") >= 0 && t.equals("r")) {
			return 1;
		}
		if(type.indexOf("r") >= 0 && t.equals("s")) {
			return 2;
		}
		if(type.indexOf("r") >= 0 && t.equals("f")) {
			return 4;
		}
		if(type.indexOf("s") >= 0 && t.equals("s")) {
			return 1;
		}
		if(type.indexOf("s") >= 0 && t.equals("f")) {
			return 2;
		}
		if(type.indexOf("f") >= 0 && t.equals("f")) {
			return 1;
		}
		return 1;
	}
	
	public Vector getProductionCost() {
		return productionCosts;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isPublic() {
		return leasePrice==0 || isPartner("public");
	}
	
	public boolean isPartner(String tic) {
		for(int a = 0; a < partners.size(); a++) {
			if(partners.elementAt(a).toString().equalsIgnoreCase(tic)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isLeased() {
		return leasePrice > 0;
	}
	
	public double getLeasePrice() {
		return leasePrice;
	}
	
	public int getDesignMass() {
		return mass;
	}
	
	public String display() {
		return display("none");
	}
	
	public String display(String tic) {
		String s = "Tech:" + getId() + " " + name + " type " + type.toUpperCase();
		boolean pub=isPublic();
		boolean part=isPartner(tic);
		boolean holdpres=Universe.holdsPrerequisitsFor(tic,this);
		boolean res=!pub&&!part&&holdpres;
		boolean priv=!pub&&!part&&!res;
		boolean sec=isPartner("none");
		pub=pub&&!sec;
		part=part&&!sec;

		if(pub ) s += " (PUBLIC)";
		else if(part) {
			s += " (PARTNER of "+partners.size();
			if(leasePrice > 0)s += " LEASING at " + Stuff.money(leasePrice, 2);
			else s+=" held privately";
			s += ")";
		}
		else if (sec)	s+= " (SECRET)";
		else if (res) {
			if (leasePrice<0) s += " (RESEARCHABLE)";
			else s += " (RESEARCHABLE or LEASE for " + Stuff.money(leasePrice, 2)+")";
		}
		else if (leasePrice>0) s+= " (LEASE for " + Stuff.money(leasePrice, 2)+")";
		else if (priv) s+= " (PRIVATE)";
		if (!res && !pub && !part) {
			s+=" Prerequisite ";
			for (int a=0;a<prerequisits.size();a++) {
				ITThing t=Universe.getITThingByName((String)prerequisits.elementAt(a));
				if (a!=0) s+=", ";
				if (t==null) {
					s+=prerequisits.elementAt(a);
				}
				else s+=t.getName();
			}
		}
		if (pub || part || res || leasePrice>0) {
			s += "\n  " + notes;
		}
		if (pub || part || leasePrice>0) {
			s += "\n  Min Production_Cost (";
			Vector v = productionCosts;
			v=Universe.cleanXList(v);
			for(int a = 0; a < v.size(); a++) {
				String t = (String)v.elementAt(a);
				int cnt1 = 1;
				if(t.indexOf("x") > 0) {
					cnt1 = Integer.parseInt(t.substring(0, t.indexOf("x")));
					t = t.substring(t.indexOf("x") + 1);
				}
				s += cnt1 + "x" + t;
				if(a + 1 < v.size()) {
					s += ", ";
				}
			}
			s += ")";
		}
		if (pub || part || res || leasePrice>0) {
			Vector v = getProvideList();
			if (v.size()>0) s += "\n  Provides (";
			for(int a = 0; a < v.size(); a++) {
				String t = (String)v.elementAt(a);
				int cnt1 = 1;
				if(t.indexOf("x") > 0) {
					cnt1 = Integer.parseInt(t.substring(0, t.indexOf("x")));
					t = t.substring(t.indexOf("x") + 1);
				}
				if (Universe.getMarket().getMarketName(t)!=null) {
					t=Universe.getMarket().getMarketName(t);
				}
				s += cnt1 + "x" + t;
				if(a + 1 < v.size()) {
					s += ", ";
				}
			}
			if (v.size()>0) s += ")";
			v = getConsumeList();
			if (v.size()>0) s += "\n  Consumes (";
			for(int a = 0; a < v.size(); a++) {
				String t = (String)v.elementAt(a);
				int cnt1 = 1;
				if(t.indexOf("x") > 0) {
					cnt1 = Integer.parseInt(t.substring(0, t.indexOf("x")));
					t = t.substring(t.indexOf("x") + 1);
				}
				if (Universe.getMarket().getMarketName(t)!=null) {
					t=Universe.getMarket().getMarketName(t);
				}
				s += cnt1 + "x" + t;
				if(a + 1 < v.size()) {
					s += ", ";
				}
			}
			if (v.size()>0) s += ")";
		}
		return s;
	}
	
	public void addPartner(String tic) {
		if( !partners.contains(tic)) {
			//if (leasePrice>0 && partners.size()>0) {
			//	leasePrice=leasePrice/partners.size()*(partners.size()+1);
			//}
			partners.addElement(tic);
		}
	}
	public void restart() {
		if (!isPartner("none")) {
			partners=new Vector();
			leasePrice=-1;
		}
	}
	public void removePartner(String tic) {
		partners.removeElement(tic);
	}
	
	public void calcMass() {
	}
	
	public Vector getAllPrerequisits() {
		return prerequisits;
	}
	
	public String toSaveString() {
		return toGmlPair().toString();
	}
	
	public GmlPair toGmlPair() {
		Vector v = new Vector();
		GmlPair g = new GmlPair("Name", name);
		v.addElement(g);
		g = new GmlPair("TechId", techId);
		v.addElement(g);
		g = new GmlPair("Type", type);
		v.addElement(g);
		String pres="";
		for (int a=0;a<prerequisits.size();a++) {
			if (pres.length()!=0) pres+=",";
			String t=(String)prerequisits.elementAt(a);
			ITThing itt = Universe.getITThingByName(t);
			if(itt != null) t= itt.getId();
			else if( !t.equalsIgnoreCase("none")) {
				System.out.println("MyError: bad prerequisite in save in " + name + " of " + t);
			}
			pres+=t;
		}
		g = new GmlPair("Prerequisit", pres);
		v.addElement(g);
		String ss = "";
		for(int a = 0; a < partners.size(); a++) {
			ss += (String)partners.elementAt(a);
			if(a + 1 < partners.size())ss += ",";
		}
		if (ss.length()>0) {
			g = new GmlPair("Partner", ss);
			v.addElement(g);
		}
		ss = "";
		for(int a = 0; a < productionCosts.size(); a++) {
			String t = (String)productionCosts.elementAt(a);
			if(t.indexOf("x") >= 0)t = t.substring(t.indexOf("x") + 1);
			if(Universe.getMarketValue(t) == 0) {
				System.out.println("MyError: bad productioncost in " + name + " of " + t);
			}
			ss += (String)productionCosts.elementAt(a);
			if(a + 1 < productionCosts.size())ss += ",";
		}
		g = new GmlPair("ProductionCost", ss);
		v.addElement(g);
		ss="";
		for (int a=0;a<provideList.size();a++) {
			if (ss.length()!=0) ss+=",";
			ss+=(String)provideList.elementAt(a);
		}
		if (ss.length()!=0) {
			g = new GmlPair("Provides", ss);
			v.addElement(g);
		}
		ss="";
		for (int a=0;a<consumeList.size();a++) {
			if (ss.length()!=0) ss+=",";
			ss+=(String)consumeList.elementAt(a);
		}
		if (ss.length()!=0) {
			g = new GmlPair("Consumes", ss);
			v.addElement(g);
		}
		g = new GmlPair("LeasePrice", leasePrice);
		v.addElement(g);
		if(notes.length() > 0 && !notes.equalsIgnoreCase("null")) {
			g = new GmlPair("Notes", notes);
			v.addElement(g);
		}
		g = new GmlPair("Tech", v);
		return g;
	}
	
	public static Tech parse(GmlPair g) {
		if( !g.getName().equalsIgnoreCase("Tech")) {
			return null;
		}
		GmlPair n[] = g.getAllByName("Name");
		String na = "none";
		if(n.length > 0) {
			na = n[0].getString();
		}
		n = g.getAllByName("TechId");
		int rdid = - 1;
		if(n.length != 0)rdid = (int)n[0].getDouble();
		n = g.getAllByName("Type");
		String ty = n[0].getString();
		n = g.getAllByName("Prerequisit");
		Vector pr=new Vector();
		if(n.length != 0l) {
			String tt[]=Stuff.getTokens(n[0].getString(),",");
			for (int a=0;a<tt.length;a++) {
				ITThing itt = Universe.getITThingByName(tt[a]);
				if (itt!=null) pr.addElement(itt.getId());
				else pr.addElement(tt[a]);
			}
		}
		n = g.getAllByName("Partner");
		Vector p = new Vector();
		for(int a = 0; a < n.length; a++) {
			String tt[] = Stuff.getTokens(n[a].getString());
			for(int b = 0; b < tt.length; b++)p.addElement(tt[b]);
		}
		n = g.getAllByName("ProductionCost");
		Vector pc = new Vector();
		for(int a = 0; a < n.length; a++) {
			String tt[] = Stuff.getTokens(n[a].getString());
			for(int b = 0; b < tt.length; b++) {
				String lookup = tt[b];
				if(lookup.indexOf("x") >= 0)lookup = lookup.substring(lookup.indexOf("x") + 1);
				if(Universe.getMarketValue(lookup) == 0) {
					System.out.println("MyError: in " + na + " " + lookup + " unknown market item");
				}
				pc.addElement(tt[b]);
			}
		}
		n = g.getAllByName("LeasePrice");
		double lp = - 1;
		if(n.length > 0) {
			lp = n[0].getDouble();
		}
		Tech tech = new Tech(na, p, pc, lp, rdid, ty, pr);
		n = g.getAllByName("Notes");
		if(n.length > 0) {
			String notes = n[0].getString();
			tech.notes = notes;
		}
//provides list
		Vector vv=new Vector();
		n=g.getAllByName("Provides");
		for (int a=0;a<n.length;a++) {
			String tt[]=Stuff.getTokens(n[a].getString(),",");
			for (int b=0;b<tt.length;b++){
				vv.addElement(tt[b]);
			}
		}
		tech.provideList=vv;
//consumes list
		vv=new Vector();
		n=g.getAllByName("Consumes");
		for (int a=0;a<n.length;a++) {
			String tt[]=Stuff.getTokens(n[a].getString(),",");
			for (int b=0;b<tt.length;b++){
				vv.addElement(tt[b]);
			}
		}
		tech.consumeList=vv;
		return tech;
	}

	public Vector getProvideList() {
		return provideList;
	}
	public Vector getConsumeList() {
		return consumeList;
	}
}

