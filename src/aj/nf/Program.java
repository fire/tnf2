package aj.nf;

import java.util.*;
import java.io.*;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    July 21, 2000 
 */

public class Program implements ITThing {
	String name;
	Vector partners = new Vector();
	double leasePrice = - 1;
	int programId;
	String lines;
	String description;

	
	static int reuse=1;
	static int count = 1;
	
	public Program(String n, Vector p, double lp, String l, String d) {
		lines = l;
		description = d;
		programId = count;
		while (Universe.getITThingByName("PR"+reuse)!=null) {
			reuse++;
		}
		programId=reuse;
		reuse++;
		count++;
		name = n;
		partners = p;
		leasePrice = lp;
	}
	
	public Program(String n, Vector p, double lp, int sid, String l, String d) {
		lines = l;
		description = d;
		programId = sid;
		count = Math.max(count, sid + 1);
		name = n;
		partners = p;
		leasePrice = lp;
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
	
	public String getLeaseLines() {
		String fix="_";
		String lines=this.lines;
		while (lines.indexOf(";")>=0) {
			fix+=lines.substring(0,lines.indexOf(";"))+";_";
			lines=lines.substring(lines.indexOf(";")+1);
		}
		if (fix.endsWith("_")) fix=fix.substring(0,fix.length()-2);
		return fix;
	}
	public String getLines() {
		return lines;
	}
	public String getDesc() {return description;}
	
	public String getId() {
		return"PR" + programId;
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
	
	public String display() {
		return null;
		//programs are always private
	}
	
	public int getLOC(String l) {
		int t=0;
		while (l.indexOf(";")>=0) {
			l=l.substring(l.indexOf(";")+1);
			t++;
		}
		return t;
	}
	public String display(String tic) {
		//no public
		boolean part=isPartner(tic);
		String s = "PROG:" + getId() + " " + name;
		if(isPublic()) {
			s+=" (PUBLIC)\n";
			s += "  DESC:" + description + ";";
			s += "  LOC:"+getLOC(lines)+";";
			s += "" + lines;
			while(s.indexOf(";") >= 0) {
				s = s.substring(0, s.indexOf(";")) + "\n  " + s.substring(s.indexOf(";") + 1);
			}
		}
		else if(part) {
			s += " (" + (partners.size() > 1?partners.size() + " ":"") + "PARTNER" + (partners.size() > 1?"S":"");
			if(leasePrice > 0)s += " Leasing at " + Stuff.money(leasePrice, 2);
			else s += " private";
			s += ")\n";
			s += "  DESC:" + description + ";";
			s += "  LOC:"+getLOC(lines)+";";
			s += "" + lines;
			while(s.indexOf(";") >= 0) {
				s = s.substring(0, s.indexOf(";")) + "\n  " + s.substring(s.indexOf(";") + 1);
			}
		}
		else if(leasePrice > 0) {
			s += " (LEASE " + Stuff.money(leasePrice, 2) + ")\n  DESC:" + description+"\n  LOC:"+getLOC(lines);
		}
		else {
			s+=" (SECRET)";
		}
		return s;
	}
	
	public void addPartner(String tic) {
		if( !partners.contains(tic)) {
			partners.addElement(tic);
		}
	}
	
	public void removePartner(String tic) {
		partners.removeElement(tic);
		if (partners.size()==0) {
			Universe.remove(this);
		}
	}

	public String toSaveString() {
		return toGmlPair().toString();
	}
	
	public GmlPair toGmlPair() {
		Vector v = new Vector();
		GmlPair g = new GmlPair("name", name);
		v.addElement(g);
		g = new GmlPair("programId", programId);
		v.addElement(g);
		for(int a = 0; a < partners.size(); a++) {
			g = new GmlPair("partner", (String)partners.elementAt(a));
			v.addElement(g);
		}
		while(lines.indexOf("\"") >= 0) {
			//lines must not have quotes
			lines = lines.substring(0, lines.indexOf("\"")) + lines.substring(lines.indexOf("\"") + 1);
		}
		g = new GmlPair("lines", lines);
		v.addElement(g);
		g = new GmlPair("description", description);
		v.addElement(g);
		g = new GmlPair("leasePrice", leasePrice);
		v.addElement(g);
		g = new GmlPair("program", v);
		return g;
	}
	
	public static Program parse(GmlPair g) {
		if( !g.getName().equals("program")) {
			return null;
		}
		GmlPair n[] = g.getAllByName("name");
		String na = n[0].getString();
		n = g.getAllByName("programId");
		int pid = (int)n[0].getDouble();
		n = g.getAllByName("partner");
		Vector p = new Vector();
		for(int a = 0; a < n.length; a++) {
			p.addElement(n[a].getString());
		}
		n = g.getAllByName("lines");
		String l = n[0].getString();
		n = g.getAllByName("description");
		String d = n[0].getString();
		n = g.getAllByName("leasePrice");
		double lp = n[0].getDouble();
		return new Program(na, p, lp, pid, l, d);
	}
}
