package aj.nf;

import java.util.Vector;
/**
 *
 *@author     judda 
 *@created    July 21, 2000 
 */
public class Facility implements NFObject {
	int facilityId;
	String corpTick;
	FacilityDesign facilityDesign;
	Location loc;
	int damage = 0;
	double contractPrice=0;
	double newContractPrice=0;
	
	static int reuse=1;
	static int count = 0;
	String nick="";

	boolean busy=false;
	boolean disabled=false;
	boolean active=false;
	boolean wasActive=false;
	
	public String getFacilityDesignId() {
		if (facilityDesign==null) return null;
		return facilityDesign.getId();
	}

	public String getResearchReport() {
		return ""+getId()+":"+facilityDesign.getName()+" Contract available for "+Stuff.money(newContractPrice,2);
	}

	public void setContractPrice(double cp) {
		newContractPrice=Math.max(0,cp);
	}

	public double getContractPrice() {
		return contractPrice;
	}

	public void payContract() {
		if(contractPrice <= 0) return;
		Corp c = Universe.getCorpByTick(corpTick);
		if(c != null) {
			c.receiveContract(getId(), contractPrice);
		}
	}

	public void setActive(boolean b) {//make producables available
		wasActive=active;active=b;
		if (active==true) {
//do provide material
			Vector give=(Vector)facilityDesign.getProvideList().clone();
			give=Universe.cleanXList(give);
			for (int a=0;a<give.size();a++) {
				String what=(String)give.elementAt(a);
				int amt=1;
				if (what.indexOf("x")>0) {
					try {
						amt=Integer.parseInt(what.substring(0,what.indexOf("x")));
						what=what.substring(what.indexOf("x")+1);
					} catch (NumberFormatException nfe) {
						System.out.println("MyError: Bad number in set active, material produce");
					}
				}
				//create M producables
				if (what.startsWith("M")) {
					String na = Market.getMarketName(what);
					if(na != null) {
						//create stockpiple
						NFObject nfo = new StockPile(getCorpTick(), na, amt, getInsideLoc());
						Universe.add(nfo);
						if (getCapacity()<nfo.getMass()) {
							nfo.setLocation(getLocation());//facility full put outside
						}
						//marke provides is consumed
						consume(na,amt);
					}
				}
			}
		}
	}
	public boolean wasActive() {//active last turn
		return wasActive;
	}
	public boolean isActive() {
		return active;
	}
	public void setDisabled(boolean b) {//
		disabled=b;
	}
	public boolean isDisabled() {
		return disabled;
	}
	public void setBusy(boolean b) {
		busy=b;
	}
	public boolean isBusy() {
		return busy;
	}
	
	public boolean hasAbility(String s) {
		if(facilityDesign != null) return facilityDesign.hasTech(s);
		else return false;
	}
	public boolean hasTech(String s) {
		if (!isActive() || isDisabled()) return false;
		if(facilityDesign != null) return facilityDesign.hasTech(s);
		else return false;
	}
	
	public Facility(String cn, String fd, Location l) {
		facilityId = count;
		count++;
		reuse=1;
		while (Universe.getNFObjectById("F"+reuse)!=null) {
			reuse++;
		}
		facilityId=reuse;
		corpTick = cn;
		facilityDesign = (FacilityDesign)Universe.getITThingByName(fd);
		loc = l;
	}
	
	public Facility(String cn, String fd, Location l, int sid) {
		facilityId = sid;
		count = Math.max(count, sid + 1);
		corpTick = cn;
		facilityDesign = (FacilityDesign)Universe.getITThingByName(fd);
		loc = l;
	}

	boolean destroyed=false;	
	public void destroy() {
		if (hasAbility("T84")) return;
		if (destroyed) return;
//log connection lost
		Corp C=Universe.getCorpByTick(corpTick);
		if (C!=null) {
   			C.addReport("Facility "+getId()+" signal lost.\n");
		}
		destroyed=true;
		//unload all cargo
		Location l2=Location.parse(loc+"."+getId());
		Vector v = Universe.getNFObjectsByLocation(l2);
		for(int a = 0; a < v.size(); a++) {
			NFObject nfo = (NFObject)v.elementAt(a);
			nfo.setLocation(loc);
		}
		//create junk stockpile
		NFObject nfo = new StockPile(corpTick, "Junk", getMass(), loc);
		Universe.add(nfo);
		//destroy self
		Universe.remove(this);
		int fcount=Universe.facilityCount(loc);
		Body b=Universe.getBodyByLocation(loc);
		if (b!=null && fcount==3) {
			b.setSurfaceLocation(loc,'c');
		}
		else if (b!=null && fcount==2) {
			b.setSurfaceLocation(loc,'p');
		}
	}

	public boolean contains(Active A) {
		Vector v = Universe.getNFObjectsByLocation(getInsideLoc());
		for(int a = 0; a < v.size(); a++) {
			NFObject nfo=(NFObject)v.elementAt(a);
			if (nfo == A) return true;
			if (nfo instanceof Active) {
				Active aa=(Active)nfo;
				if (aa.contains(A)) return true;
			}
		}
		return false;
	}
	
	public int getCapacity() {
		if (hasAbility("T84")) {//not at HQ
			return 100000;
		}
		int total=0;
		Location l2=Location.parse(loc+"."+getId());
		Vector v = Universe.getNFObjectsByLocation(l2);
		for(int a = 0; a < v.size(); a++) {
			NFObject nfo = (NFObject)v.elementAt(a);
//System.out.println("Facility "+getId()+" contains "+nfo.getId()+" of "+nfo.getMass()+" mass");
			total+=nfo.getMass();
		}
		int max=200;
		if (hasAbility("T93")) {//WhereHousing
			max=1000;
		}
		return max-total;
	}
	
	public void setCorpTick(String s) {
		if (hasAbility("T84")) return;//cannot auction or give corporate HQ
		corpTick = s;
	}
	
	public void setLocation(Location l) {
		loc = l;
	}
	
	public String getNick() {
		return nick;
	}
	public String getId() {
		return"F" + facilityId;
	}
	
	public double getValue() {
		if(facilityDesign != null)
			return facilityDesign.getProducedValue()*(1-damage/100);
		return - 1;
	}
	
	//public boolean isPowered() {
		//return true;
	//}

	public boolean isDestroyed() {
		return destroyed;
	}

	public boolean isMoveable() {
		return false;
	}
	
	public String getCorpTick() {
		return corpTick;
	}
	
	public Location getLocation() {
		return loc;
	}
	public Location getInsideLoc() {
		return Location.parse(loc+"."+getId());
	}
	
	public int getMass() {
		return facilityDesign.getDesignMass();
	}
	
	public int getRepairMass() {
		int tot = facilityDesign.getDesignMass();
		return (int)(tot * damage/100.0/4);
	}
	
	public void doRepair(int x) {
		int need = getRepairMass();
		double chang = Math.max(0, Math.min(1, 1 - 1.0 * x / need));
		damage = Math.max((int)(damage * chang),0);
	}
	public void attackedBy(Active A){
		int weaptype=0;
		int armtype=0;
//robot weapons
		if (A.hasTech("T35")) weaptype+=1;
		if (A.hasTech("T36")) weaptype+=2;
		if (A.hasTech("T53")) weaptype+=3;
		if (A.hasTech("T54")) weaptype+=7;
		if (A.hasTech("T55")) weaptype+=9;
		if (A.hasTech("T56")) weaptype+=12;
//facility armor
		if (hasAbility("T16")) armtype+=1;//weather
		if (hasAbility("T17")) armtype+=1;//water
		if (hasAbility("T18")) armtype+=2;//ceros
		if (hasAbility("T19")) armtype+=4;//vacu
		if (hasAbility("T20")) armtype+=1;//lowtemp
		if (hasAbility("T21")) armtype+=1;//very lowtemp
		if (hasAbility("T22")) armtype+=1;//hight temp
		if (hasAbility("T23")) armtype+=1;//very high temp
		if (hasAbility("T24")) armtype+=3;//earthquake
		if (hasAbility("T25")) armtype+=1;//rad
		if (hasAbility("T26")) armtype+=1;//heavy rad
		if (hasAbility("T27")) armtype+=3;//pressure fit
		if (hasAbility("T28")) armtype+=5;//extra pressure fit
		if (hasAbility("T96")) armtype+=30;//extra pressure fit
		if (weaptype>0 && weaptype*2>=armtype) {
			applyDamage((armtype-weaptype)*1.5);
		}
	}
	public void counterAttack(Active A){
		int weaptype=0;
		int armtype=0;
		if (A.hasTech("T29")) armtype+=1;//robot armor
		if (A.hasTech("T30")) armtype+=4;
		if (A.hasTech("T31")) armtype+=6;
		if (A.hasTech("T32")) armtype+=8;
		if (A.hasTech("T33")) armtype+=12;
		if (A.hasTech("T34")) armtype+=15;

		if (hasAbility("T75")) weaptype+=15;//facility weapons
		if (hasAbility("T76")) weaptype+=25;
		if (weaptype>0 && weaptype*2>=armtype) {
			A.applyDamage((armtype-weaptype)*1);
		}
	}
	public void applyDamage(double d) {
		if (hasAbility("T84")) return;
		if (d<0) d=1;
		damage += d*(Math.random()+1);
	}
	public boolean canRecover() {
//change to 10?
		return damage<25;
	}
	
	public void enviromentalEffects() {
		if (hasAbility("T84")) return;//no damage at HQ
		if (Main.DEBUG)
			System.out.println("DEBUG:  Enviro check fac");
		boolean liquid = Universe.getSurfaceByLocation(loc).equalsIgnoreCase("W");
		boolean pres = loc.isLevel();
		boolean expres = (loc.isLevel() && loc.getLevel() > 2);
		double temp = Universe.getTempByLocation(loc);
		double tect = 0, atmo = 0, weath = 0, rad = 0;
		Body b = Universe.getBodyByLocation(loc);
		if(b != null) {
			tect = b.tectonic;
			atmo = b.atmos;
			weath = b.weather;
			rad = b.radation;
		}
		if (loc.isOrbit()) {
			tect=0;atmo=0;weath=0;
		}
		String type="";
		//weather minor (1 damage 10% for each weather point)
		if( !hasAbility("T16") && weath > 0) {
			damage += (Math.random()<.1*weath?1:0);
			type+="w";
		}
		//liquid
		if( !hasAbility("T17") && liquid) {
			damage += (Math.random()>.5?1:0);
			type+="l";
		}
		//cerosive atmo (1 damage 10% for each atmo >4
		if( !hasAbility("T18") && atmo > 4 && !loc.isLevel()) {
			damage += (Math.random()<.1*(atmo-4)?1:0);
			type+="a";
		}
		//vacume (10% chance 1 damage)
		if( !hasAbility("T19") && atmo == 0) {
			damage += (Math.random()<.1?1:0);
			//not needed for robots or ships
			type+="v";
		}
		//cold  1 point damage 10% per 20 degrees
		if( !hasAbility("T20") && !hasAbility("T21") && temp < - 80 ) {
			damage += (Math.random()<.1*((-temp+60)/20)?1:0);
			type+="c";
		}
		//very cold 1 point damage 10% pre 10 degrees
		if( !hasAbility("T21") && temp < - 200) {
			damage += (Math.random()<.1*((-temp+200)/10)?1:0);
			type+="C";
		}
		//hot
		if( !hasAbility("T22") && !hasAbility("T23") && temp > 100 ) {
			damage += (Math.random()<.1*((temp-90)/10)?1:0);
			type+="h";
		}
		//very hot
		if( !hasAbility("T23") && temp > 200) {
			damage += (Math.random()<.1*((temp-180)/20)?1:0);
			type+="H";
		}
		//tectonics minor
		if( !hasAbility("T24") && tect > 0) {
			damage += (Math.random()<.1*tect?1:0);
			type+="t";
		}
		//tectonics major
		if( !hasAbility("T24") && tect > 2) {
			damage += (Math.random()<.1*(tect-2)?1:0);
			type+="T";
		}
		//radiation
		if( !hasAbility("T25") && !hasAbility("T26") && rad > 0) {
			damage += (Math.random()<.1*rad?1:0);
			type+="r";
		}
		//very radiation
		if( !hasAbility("T26") && rad > 3) {
			damage += (Math.random()<.1*(rad-3)?1:0);
			type+="R";
		}
		//pressure
		if( !hasAbility("T27") && !hasAbility("T28") && pres) {
			damage += 1;
			type+="p";
		}
		//extream pressure
		if( !hasAbility("T28") && expres) {
			damage += 2;
			type+="P";
		}
		if (hasTech("T89")) {//regenerate
			damage=(int)Math.max(0,damage-Math.random()*2);
			type+="-";
		}
		if (Main.DEBUG && type.length()>0) System.out.println("DEBUG: damage warning = "+getId()+" "+getLocation()+" "+damage+" "+type);
		if(damage >= 100) destroy();
		if (damage < 0) damage=0;
	}
	
	public String toScanString() {
		return corpTick + "." + getId() + "." + facilityDesign.getName();
	}
	
	public String displayHeader() {return display();}
	public String display() {
		String s=(nick.length()>0?nick+":":"")+getId() + ": " + facilityDesign.getName() + " at " + loc.toString() + " " + damage + "% damaged";
		if (isDisabled()) {
			s+=" DISABLED";
		}
		else if (!isActive()) {
			s+=" INACTIVE";
		}
		else {
			s+=" Active";
		}
		return s+" "+Stuff.money(getValue());
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
		g = new GmlPair("FacilityDesign", facilityDesign.getId());
		v.addElement(g);
		g = new GmlPair("FacilityId", facilityId);
		v.addElement(g);
		g = new GmlPair("ContractPrice", newContractPrice);
		v.addElement(g);
		g = new GmlPair("Disabled", (disabled?1:0));
		v.addElement(g);
		g = new GmlPair("Active", (active?1:0));
		v.addElement(g);
		g = new GmlPair("Dam", damage);
		v.addElement(g);
		if (nick.length()>0){
			 g = new GmlPair("Nick", nick);
			v.addElement(g);
		}
		g = new GmlPair("Facility", v);
		return g;
	}
	
	public static Facility parse(GmlPair g) {
		if( !g.getName().equalsIgnoreCase("Facility")) {
			return null;
		}
		GmlPair n[] = g.getAllByName("Tick");
		String cn = n[0].getString();
		n = g.getAllByName("FacilityDesign");
		String dn = n[0].getString();
		n = g.getAllByName("FacilityId");
		int fid = (int)(n[0].getDouble());
		n = g.getAllByName("Loc");
		Location l = Location.parse(n[0].getString());
		if(l == null) {
			System.out.println("MyError: bad loc in facility parse");
			return null;
		}
		Facility f = new Facility(cn, dn, l, fid);
		n = g.getAllByName("Dam");
		if(n.length > 0) f.damage = Math.max(0,(int)(n[0].getDouble()));
		GmlPair NN = g.getOneByName("Nick");
		if (NN!=null) f.nick=NN.getString();
		NN = g.getOneByName("ContractPrice");
		if (NN!=null) f.newContractPrice=f.contractPrice=NN.getDouble();
		NN = g.getOneByName("Disabled");
		if (NN!=null) f.setDisabled(NN.getDouble()==1);
		NN = g.getOneByName("Active");
		if (NN!=null) f.active=(NN.getDouble()==1);
//power on time
		return f;
	}

	public Vector getConsumeList() {
		return (Vector)facilityDesign.getConsumeList().clone();
	}
	public int getProvidesAmt(String s) {
		String what=null;
		Vector give=(Vector)facilityDesign.getProvideList().clone();
		give=Universe.cleanXList(give);
		for (int a=0;a<give.size();a++) {
			String t=(String)give.elementAt(a);
			if (t.toUpperCase().indexOf(s.toUpperCase())>=0) {
				what=t;break;
			}
		}
		if (what==null) return 0;
		int amt=1;
		if (what.indexOf("x")>0) {
			try {
				amt=Integer.parseInt(what.substring(0,what.indexOf("x")));
				what=what.substring(what.indexOf("x")+1);
			} catch (NumberFormatException nfe) {
				System.out.println("MyError: bad number in provides list"+what);
			}
		}
		String what2=null;
		for (int a=0;a<consumed.size();a++) {
			String t=(String)consumed.elementAt(a);
			if (t.toUpperCase().indexOf(s.toUpperCase())>=0) {
				what2=t;break;
			}
		}
		if (what2==null) return amt;
		int amt2=1;
		if (what2.indexOf("x")>0) {
			try {
				amt2=Integer.parseInt(what2.substring(0,what2.indexOf("x")));
				what2=what2.substring(what2.indexOf("x")+1);
			} catch (NumberFormatException nfe) {
				System.out.println("MyError: bad number in provides list"+what2);
			}
		}
		return amt-amt2;
	}

	Vector consumed=new Vector();
	public void consume(String s, int amt) {
		consumed.addElement(amt+"x"+s);
		consumed=Universe.cleanXList(consumed);
	}
}

