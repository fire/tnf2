package aj.nf;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

/**
 *
 *@author     judda 
 *@created    July 21, 2000 
 */

public class Active implements NFObject {
	
	static long MINTIME = Universe.MINUTE*10;
	static long MINUTE = Universe.MINUTE;//1000 * 60;
	static long HOUR = Universe.HOUR;//MINUTE*60;
	static long DAY = Universe.DAY;//HOUR * 24;
	static long WEEK = Universe.WEEK;//DAY*7;
	static long MONTH = Universe.MONTH;//DAY * 30;
	static long ERRORTIME = Universe.HOUR;
	
	static double SALVAGEAMT = .3;//percent salvaged into spare parts
	static long REFINERATE = DAY / 2;//2ton/day facility refine
	static long PRODRATE = DAY / 2;//2ton/day facility production
	static long SALVAGERATE = DAY / 3;//3ton/day robot salvage
	static long SALVAGERATEFAC = DAY / 5;//15ton/day facility salvage
	static long REPAIRRATE = DAY / 3;//3ton/day robot repair 
	static long REPAIRRATEFAC = DAY / 15;//3ton/day facility repair
	static long CONSTRUCTRATE = DAY / 5;//20ton/day robot construction
	static long RECOVERRATE = DAY / 5;//20ton/day robot construction

	static String CARGOSEP = ",";
	static String SCANSEP = ",";
	
	static int reuse=1;
	static int count = 0;

	boolean verbose=false;
	int ActiveId;
	String corpTick;
	ActiveDesign activeDesign;
	Location loc;
	StringBuffer log = new StringBuffer();
	long finishTime = - 1;
	String finishCommand = "";
	String lines; //loaded program and subroutines
	Hashtable vars = new Hashtable();
	long currTime = 0;
	String gotoLine = "";
	String subCommand = "";
	String nick="";
	String desc="";

//TODO todo set workamt when load/unload/collect/refine/produce/salv/repar
//TOD add GROUPS

	int workamt=0;

	public Active(String cn, String rdid, Location l, long ft, String fc, String ls, Hashtable vs) {
		finishTime = ft;
		finishCommand = fc;
		lines = ls;
		vars = vs;
		ActiveId = count;
		count++;
		reuse=1;
		while (Universe.getNFObjectById("A"+reuse)!=null) {
			reuse++;
		}
		ActiveId=reuse;
		corpTick = cn;
		activeDesign = (ActiveDesign)Universe.getITThingByName(rdid);
		loc = l;
		checkVars();
	}
	
	public Active(String cn, String rdid, Location l, int sid, long ft, String fc, String ls, Hashtable vs) {
		finishTime = ft;
		finishCommand = fc;
		lines = ls;
		vars = vs;
		ActiveId = sid;
		count = Math.max(count, sid + 1);
		corpTick = cn;
		activeDesign = (ActiveDesign)Universe.getITThingByName(rdid);
		loc = l;
		checkVars();
	}
	
	public boolean hasTech(String s) {
		if(activeDesign != null)return activeDesign.hasTech(s);
		else return false;
	}
	
	public void setMessage(String s) {
		vars.put("message", s + "");
	}
	
	public int getCargoMass() {
		//String ori=(String)vars.get("_cargo");
		String all = "";
		int tot = 0;
		Vector v = Universe.getNFObjectsByLocation(getInsideLoc());
		//if (Main.DEBUG)  System.out.println("DEBUG: "+v.size()+" objects found inside "+getId()+" with inside of "+getInsideLoc());
		for(int a = 0; a < v.size(); a++) {
			NFObject nfo=(NFObject)v.elementAt(a);
			tot += nfo.getMass();
			if (a!=0) all+=CARGOSEP;
			all+= nfo.getId();
			
		}
		if (all.equals("")) all="-";
		//if (Main.DEBUG && !ori.equals(all)) System.out.println("DEBUG: cargomass error fixed "+ori+" to "+all);
		//if (Main.DEBUG) System.out.println("DEBUG: cargomass  called with carog= "+all);
		vars.put("_cargo",all);
		return tot;
	}

	public boolean contains(Active A) {
		if (this == A) return true;
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
	public int getCargoCapacity() {
		int max = (int)activeDesign.cargocapacity;
		int tot = getCargoMass();
		return max - tot;
	}
	
	public void setCorpTick(String s) {
		corpTick = s;
	}
	
	public void setLocation(Location l) {
		loc = l;
		vars.put("_location", l.toString() + "");
	}

	public void consumeFuel(double c) {
		//for ships move only
		if (!isRobot()) {
			String s = (String)vars.get("_endurance");
			try {
				double e=Stuff.parseDouble(s);
				e=Math.max(0,e-c);
				vars.put("_endurance",""+Stuff.trunc(e,2));
			} catch (NumberFormatException nfe) {
				System.out.println("MyError:  Bad endurance found "+s);
			}
		}
	}

	public void refuel(int i) {
//for ships move only
		if (isShip()) {
			double e2=getEndurance()+i/2.0;
			e2=Math.min(e2,activeDesign.getDesignEndurance());
			vars.put("_endurance",""+Stuff.trunc(e2,2));
		}
	}

	public void recharge(double e) {
//for robots move only
		if (isRobot()) {
			double e2=getEndurance()+e;
			e2=Math.min(e2,activeDesign.getDesignEndurance());
			vars.put("_endurance",""+Stuff.trunc(e2,2));
		}
	}

	public void recharge() {
//for robots move only
		if (isRobot()) {
			vars.put("_endurance", activeDesign.getDesignEndurance()+"");
		}
	}

	public double getEndurance() {
		String s = (String)vars.get("_endurance");
		try {
			return Stuff.parseDouble(s);
		} catch (NumberFormatException nfe) {
			System.out.println("MyError:  Bad endurance found "+s);
		}
		return 0;
	}

	public void sharePower(Active a) {
		double e=getEndurance();
		double ew=a.getEndurance();
		if (isRobot() && a.isRobot() && e>ew) {
			e+=ew;
			e=e/2;
			vars.put("_endurance",""+Stuff.trunc(e,2));
			a.recharge(e-ew);
		}
		else if (isShip() && a.isRobot()) {
			a.recharge();
		}
	}

	public void attackedBy(Active A){
		int weaptype=0;
		int armtype=0;
		if (A.hasTech("T35")) weaptype+=1;//weapons
		if (A.hasTech("T36")) weaptype+=2;
		if (A.hasTech("T53")) weaptype+=3;
		if (A.hasTech("T54")) weaptype+=7;
		if (A.hasTech("T55")) weaptype+=9;
		if (A.hasTech("T56")) weaptype+=12;

		if (hasTech("T16")) armtype+=1;//weather
		if (hasTech("T17")) armtype+=1;//water
		if (hasTech("T18")) armtype+=2;//ceros
		if (hasTech("T19")) armtype+=4;//vacu
		if (hasTech("T20")) armtype+=1;//lowtemp
		if (hasTech("T21")) armtype+=1;//very lowtemp
		if (hasTech("T22")) armtype+=1;//hight temp
		if (hasTech("T23")) armtype+=1;//very high temp
		if (hasTech("T24")) armtype+=3;//earthquake
		if (hasTech("T25")) armtype+=1;//rad
		if (hasTech("T26")) armtype+=1;//heavy rad
		if (hasTech("T27")) armtype+=3;//pressure fit
		if (hasTech("T28")) armtype+=5;//extra pressure fit
		if (hasTech("T29")) armtype+=1;//armor
		if (hasTech("T30")) armtype+=4;
		if (hasTech("T31")) armtype+=6;
		if (hasTech("T32")) armtype+=8;
		if (hasTech("T33")) armtype+=12;
		if (hasTech("T34")) armtype+=15;
		if (weaptype>0 && weaptype*2>=armtype) {
			applyDamage((armtype-weaptype)*1.5);
		}
	}
	public void counterAttack(Active A){
		if (!isPowered()) return;
		int weaptype=0;
		int armtype=0;
		if (hasTech("T35")) weaptype+=1;//weapons
		if (hasTech("T36")) weaptype+=2;
		if (hasTech("T53")) weaptype+=3;
		if (hasTech("T54")) weaptype+=7;
		if (hasTech("T55")) weaptype+=9;
		if (hasTech("T56")) weaptype+=12;

		if (A.hasTech("T16")) armtype+=1;//weather
		if (A.hasTech("T17")) armtype+=1;//water
		if (A.hasTech("T18")) armtype+=2;//ceros
		if (A.hasTech("T19")) armtype+=4;//vacu
		if (A.hasTech("T20")) armtype+=1;//lowtemp
		if (A.hasTech("T21")) armtype+=1;//very lowtemp
		if (A.hasTech("T22")) armtype+=1;//hight temp
		if (A.hasTech("T23")) armtype+=1;//very high temp
		if (A.hasTech("T24")) armtype+=3;//earthquake
		if (A.hasTech("T25")) armtype+=1;//rad
		if (A.hasTech("T26")) armtype+=1;//heavy rad
		if (A.hasTech("T27")) armtype+=3;//pressure fit
		if (A.hasTech("T28")) armtype+=5;//extra pressure fit
		if (A.hasTech("T29")) armtype+=1;//armors
		if (A.hasTech("T30")) armtype+=4;
		if (A.hasTech("T31")) armtype+=6;
		if (A.hasTech("T32")) armtype+=8;
		if (A.hasTech("T33")) armtype+=12;
		if (A.hasTech("T34")) armtype+=15;
		if (weaptype>0 && weaptype*2>=armtype) {
			A.applyDamage((armtype-weaptype)*1);
		}
	}
	public void applyDamage(double d) {
		if (d<0) d=1;
		int comp = 0, sens = 0, eng = 0, weap = 0, hull = 0;
		String s = (String)vars.get("_damage");
		String t[] = Stuff.getTokens(s, ".");
		if(t.length >= 5) {
			try {
				comp = Integer.parseInt(t[0]);
				sens = Integer.parseInt(t[1]);
				eng = Integer.parseInt(t[2]);
				weap = Integer.parseInt(t[3]);
				hull = Integer.parseInt(t[4]);
			}
			catch(NumberFormatException nfe) {
			}
		}
		comp+=(int)(d*(Math.random()+1));
		sens+=(int)(d*(Math.random()+1));
		eng+=(int)(d*(Math.random()+1));
		weap+=(int)(d*(Math.random()+1));
		hull+=(int)(d*(Math.random()+1));
		s = comp + "." + sens + "." + eng + "." + weap + "." + hull;
		log ("Warning: Combat, damage sustained.");
		if (Main.DEBUG) System.out.println("DEBUG: damage warning = " + getId()+" " +getLocation()+ " " + s + " landing");
		vars.put("_damage", s + "");
		int count = 0;
		if(comp > 50)count++;
		if(sens > 50)count++;
		if(eng > 50)count++;
		if(weap > 50)count++;
		if(hull > 50)count++;
		if(count > 3 || comp >= 100 || sens >= 100 || eng >= 100 || weap >= 100 || hull >= 100) {
			destroy();
		}
	}

	public void landingDamage() {
		int comp = 0, sens = 0, eng = 0, weap = 0, hull = 0;
		String s = (String)vars.get("_damage");
		String t[] = Stuff.getTokens(s, ".");
		if(t.length >= 5) {
			try {
				comp = Integer.parseInt(t[0]);
				sens = Integer.parseInt(t[1]);
				eng = Integer.parseInt(t[2]);
				weap = Integer.parseInt(t[3]);
				hull = Integer.parseInt(t[4]);
			}
			catch(NumberFormatException nfe) {
			}
		}
		comp+=(int)(5*Math.random());
		sens+=(int)(2*Math.random());
		eng+=(int)(25*Math.random());
		weap+=(int)(15*Math.random());
		hull+=(int)(20*Math.random());
		s = comp + "." + sens + "." + eng + "." + weap + "." + hull;
		log ("Warning: Landing error, damage sustained.");
		if (Main.DEBUG ) System.out.println("DEBUG: damage warning = " + getId()+" " +getLocation()+ " " + s + " landing");
		vars.put("_damage", s + "");
		int count = 0;
		if(comp > 50)count++;
		if(sens > 50)count++;
		if(eng > 50)count++;
		if(weap > 50)count++;
		if(hull > 50)count++;
		if(count > 3 || comp >= 100 || sens >= 100 || eng >= 100 || weap >= 100 || hull >= 100) {
			destroy();
		}
	}	

	public void enviromentalEffects() {
		if (Main.DEBUG)
			System.out.println("DEBUG:  Enviro check active");
		String s = (String)vars.get("_endurance");
		if (isRobot()) {
			try {
				double e=Stuff.parseDouble(s);
				e=Math.max(0,(e*Universe.MONTH-Universe.CHECKTIME)/Universe.MONTH);
				vars.put("_endurance",""+Stuff.trunc(e,2));
			} catch (NumberFormatException nfe) {
				System.out.println("MyError:  Bad endurance found "+s);
			}
		}

		if (Universe.locationHasTechnology(loc,"T84")) {
			if (Main.DEBUG)
				System.out.println("DEBUG:  HQ safe");
			return;
		}

		int comp = 0, sens = 0, eng = 0, weap = 0, hull = 0;
		s = (String)vars.get("_damage");
		String t[] = Stuff.getTokens(s, ".");
		if(t.length >= 5) {
			try {
				comp = Integer.parseInt(t[0]);
				sens = Integer.parseInt(t[1]);
				eng = Integer.parseInt(t[2]);
				weap = Integer.parseInt(t[3]);
				hull = Integer.parseInt(t[4]);
			}
			catch(NumberFormatException nfe) {
			}
		}
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
			atmo=0;tect=0;weath=0;
		}
		String type = "";
		//weather minor
		if( !hasTech("T16") && weath > 0 && !loc.isInside() && !loc.isLevel()) {
			comp += 0;
			int dam=(Math.random()<.1*weath?1:0);
			sens += dam;
			dam=(Math.random()<.1*weath?1:0);
			eng += dam;
			weap += 0;
			dam=(Math.random()<.1*weath?1:0);
			hull += dam;
			type += "w";
		}
		//liquid
		if( !hasTech("T17") && liquid) {
			int dam =(Math.random()>.5?1:0); 
			comp += dam;
			dam =(Math.random()>.5?1:0); 
			sens += dam;
			dam =(Math.random()>.5?1:0); 
			eng += dam;
			dam =(Math.random()>.5?1:0); 
			weap += dam;
			hull += 0;
			type += "l";
		}
		//cerosive atmo
		if( !hasTech("T18") && atmo > 4 && !loc.isLevel()) {
			comp += 0;
			int dam =(Math.random()<.1*(atmo-4)?1:0); 
			sens += dam;
			eng += 0;
			dam =(Math.random()<.1*(atmo-4)?1:0); 
			weap += dam;
			dam =(Math.random()<.1*(atmo-4)?1:0); 
			hull += dam;
			type += "a";
		}
		//vacume
		if( !hasTech("T19") && atmo == 0) {
			//not needed for robots or ships
		}
		//cold
		if( !hasTech("T20") && !hasTech("T21") && temp < - 80 && !isShip() ) {
			//not needed for ships
			comp += 0;
			sens += 0;
			int dam =(Math.random()<.1*((-temp+60)/20)?1:0);
			eng += dam;
			dam =(Math.random()<.1*((-temp+60)/20)?1:0);
			weap += dam;
			dam =(Math.random()<.1*((-temp+60)/20)?1:0);
			hull += dam;
			type += "c";
		}
		//very cold	
		if( !hasTech("T21") && temp < - 200 && !isShip()) {
			comp += 0;
			sens += 0;
			int dam = (Math.random()<.1*((-temp+200)/10)?1:0);
			eng += dam;
			dam = (Math.random()<.1*((-temp+200)/10)?1:0);
			weap += dam;
			dam = (Math.random()<.1*((-temp+200)/10)?1:0);
			hull += dam;
			type += "C";
		}
		//hot
		if( !hasTech("T22") && !hasTech("T23") && temp > 100 ) {
			comp += 0;
			int dam =(Math.random()<.1*((temp-90)/10)?1:0); 
			sens += dam;
			dam =(Math.random()<.1*((temp-90)/10)?1:0); 
			eng += dam;
			weap += 0;
			dam =(Math.random()<.1*((temp-90)/10)?1:0); 
			hull += dam;
			type += "h";
		}
		//very hot
		if( !hasTech("T23") && temp > 200) {
			comp += 0;
			int dam=(Math.random()<.1*((temp-180)/20)?1:0);
			sens += dam;
			dam=(Math.random()<.1*((temp-180)/20)?1:0);
			eng += dam;
			weap += 0;
			dam=(Math.random()<.1*((temp-180)/20)?1:0);
			hull += dam;
			type += "H";
		}
		//tectonics minor
		if( !hasTech("T24") && tect > 0) {
			int dam = (Math.random()<.1*tect?1:0);
			comp += dam;
			dam = (Math.random()<.1*tect?1:0);
			sens += dam;
			dam = (Math.random()<.1*tect?1:0);
			eng += dam;
			weap += 0;
			dam = (Math.random()<.1*tect?1:0);
			hull += dam;
			type += "t";
		}
		//tectonics major
		if( !hasTech("T24") && tect > 2) {
			int dam = (Math.random()<.1*(tect-2)?1:0);
			comp += dam;
			dam = (Math.random()<.1*(tect-2)?1:0);
			sens += dam;
			dam = (Math.random()<.1*(tect-2)?1:0);
			eng += dam;
			weap += 0;
			dam = (Math.random()<.1*(tect-2)?1:0);
			hull += dam;
			type += "T";
		}
		//radiation
		if( !hasTech("T25") && !hasTech("T26") && rad > 0) {
			int dam = (Math.random()<.1*rad?1:0);
			comp += dam;
			dam = (Math.random()<.1*rad?1:0);
			sens += dam;
			eng += 0;
			weap += 0;
			hull += 0;
			type += "r";
		}
		//very radiation
		if( !hasTech("T26") && rad > 3) {
			int dam=(Math.random()<.1*(rad-3)?1:0);
			comp += dam;
			dam=(Math.random()<.1*(rad-3)?1:0);
			sens += dam;
			eng += 0;
			weap += 0;
			hull += 0;
			type += "R";
		}
		//pressure
		if( !hasTech("T27") && !hasTech("T28") && pres) {
			//noeffect on robots
		}
		//extream pressure
		if( !hasTech("T28") && expres) {
			//noeffect on ships or robots
		}
		if (hasTech("T89")) {//regenerate
			comp = (int)Math.max(0,comp-Math.random()*2);
			sens = (int)Math.max(0,sens-Math.random()*2);
			eng = (int)Math.max(0,eng-Math.random()*2);
			weap = (int)Math.max(0,weap-Math.random()*2);
			hull = (int)Math.max(0,hull-Math.random()*2);
			type += "-";
		}
		s = comp + "." + sens + "." + eng + "." + weap + "." + hull;
		if (Main.DEBUG && type.length()>0) System.out.println("DEBUG: damage warning = " + getId()+" " +getLocation()+ " " + s + " " + type);
		vars.put("_damage", s + "");
		int count = 0;
		if(comp > 50)count++;
		if(sens > 50)count++;
		if(eng > 50)count++;
		if(weap > 50)count++;
		if(hull > 50)count++;
		if(count > 3 || comp >= 100 || sens >= 100 || eng >= 100 || weap >= 100 || hull >= 100) {
			destroy();
		}
	}
	
	public String getId() {
		return"A" + ActiveId;
	}
	
	public double getValue() {
		if (activeDesign==null) return -1;
		return activeDesign.getProducedValue() * 1.5*(1-getDamageRate());
	}
	
	public String getNick() {
		return nick;
	}
	public String getName() {
		return activeDesign.getName();
	}
	
	public boolean isPowered() {
		String end=(String)vars.get("_endurance");
		if (Stuff.parseDouble(end)>0) return true;
		return false;
	}
	public boolean isDestroyed() {
		return destroyed;
	}

	public boolean isShip() {
		return activeDesign.isShip();
	}
	
	public boolean isRobot() {
		return activeDesign.isRobot();
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
	
	public Location getInsideLoc() {
		return Location.getInsideActive(getId());
	}
	
	public int getRepairMass() {
		return(int)(activeDesign.getDesignMass() * getDamageRate()/4);
	}
	
	public void doRepair(int x) {
		int need = getRepairMass();
		double chang = Math.max(0, Math.min(1, 1 - 1.0 * x / need));
		String s = (String)vars.get("_damage");
		String t[] = Stuff.getTokens(s, ".");
		int comp = 0, sens = 0, eng = 0, weap = 0, hull = 0;
		if(t.length >= 5) {
			try {
				comp = (int)(Integer.parseInt(t[0]) * chang);
				sens = (int)(Integer.parseInt(t[1]) * chang);
				eng = (int)(Integer.parseInt(t[2]) * chang);
				weap = (int)(Integer.parseInt(t[3]) * chang);
				hull = (int)(Integer.parseInt(t[4]) * chang);
			}
			catch(NumberFormatException nfe) {
			}
		}
		s = comp + "." + sens + "." + eng + "." + weap + "." + hull;
		vars.put("_damage", s + "");
	}
	
	public int getMass() {
		return activeDesign.getDesignMass() + getCargoMass();
	}
	
	public void checkVars() {
		for(int a = 1; a < 6; a++) {
			if( !vars.containsKey("arg" + a)) {
				vars.put("arg" + a, "-");
			}
		}
		if( !vars.containsKey("_jr")) {
			vars.put("_jr", "0");
		}
		if( !vars.containsKey("_damage")) {
			vars.put("_damage", "0.0.0.0.0");
		}
		if( !vars.containsKey("message")) {
			vars.put("message", "-");
		}
		if( !vars.containsKey("_cargo")) {
			vars.put("_cargo", "-");
		}
		if( !vars.containsKey("_endurance")) {
			vars.put("_endurance", activeDesign.getDesignEndurance()+"");
		}
	}
	
	public void reset() {
		finishTime = - 1;
		vars.put("_jr", "0");
	}
	public int getMaxSubroutines(){
			if (hasTech("T90")) {
				return 2;
			}
			if (hasTech("T91")) {
				return 3;
			}
			if (hasTech("T92")) {
				return 20;
			}
		return 1;
	}	
	public void uploadProgram(String s,String d) {
		if(finishTime == - 1) {
			desc=d;
			int maxlines=20;
			if (hasTech("T90")) {
				maxlines=40;
			}
			if (hasTech("T91")) {
				maxlines=60;
			}
			if (hasTech("T92")) {
				maxlines=1000;
			}
			int linecount=0;
			String s2="";
			while (linecount<maxlines && s.indexOf(";")>0) {
				s2+=s.substring(0,s.indexOf(";")+1);
				s=s.substring(s.indexOf(";")+1);
				linecount++;
			}
			if (s.length()>0){
if (Main.DEBUG) System.out.println("MYDEBUG: program truncated.  Too long");
			}
			s=s2;
			//count lines
			lines = s;
			Hashtable vars2 = new Hashtable();
			Enumeration keys = vars.keys();
			while(keys.hasMoreElements()) {
				String key = (String)keys.nextElement();
				String val = (String)vars.get(key);
				if (key.startsWith("_")) vars2.put(key,val);
			}
			vars2.put("_jr", "0");
			vars=vars2;
		}
	}
	
	public void initilizeProgram() {
		Hashtable vars2= new Hashtable();
		//put all default vars in
		if(vars.containsKey("_damage")) {
			vars2.put("_damage",vars.get("_damage") );
		}
		if(vars.containsKey("_cargo")) {
			vars2.put("_cargo",vars.get("_cargo") );
		}
		if(vars.containsKey("_endurance")) {
			vars2.put("_endurance",vars.get("_endurance") );
		}
		vars=vars2;
		checkVars();
		//check vars to insert defaults
		finishTime = 0;
		//flag to set init time and begin run.
		finishCommand = "";
		//no current line running
	}
	
	public String toScanString() {
		return corpTick + "." + getId() + "." + activeDesign.getName();
	}
	
	public String displayHeader() {
		String s = "";
		s += (nick.length()>0?nick+":":"")+getId() + ":" + (activeDesign.isShip()?"SHIP":"ROBOT") + ": " + activeDesign.getName() + " at " + loc.toString();
		if(!isPowered()) {
			s+=" Out of power ";
			if(finishTime == - 1)s += " Not running.";
			else if(finishTime == 0)s += " Initialized.";
			else if(finishTime > 0)s += " Halted.";
			
		}
		else if(finishTime == - 1)s += " Not running.";
		else if(finishTime == 0)s += " Initialized.";
		else if(finishTime > 0)s += " Running.";

		double dr=getDamageRate();
		s += " End ("+vars.get("_endurance")+"/"+activeDesign.getDesignEndurance()+") Dam "+Stuff.trunc(dr*100,2)+"%";
		return s+" "+Stuff.money(getValue());
	}

	public String display() {
		String s = displayHeader();
		String desc=this.desc;
		while(desc.indexOf("_") >= 0) {
			desc=desc.substring(0, desc.indexOf("_")) + "\n"+ desc.substring(desc.indexOf("_") + 1);
		}
		
		s+="\n"+desc;
		if (verbose) {
			String lines="  "+this.lines;
			while (lines.indexOf(";_")>=0) {
				String hide=lines.substring(lines.indexOf(";_")+1);
				if (hide.indexOf(";")>=0) hide=hide.substring(0,hide.indexOf(";"));
				String rep=hide;
				if (rep.indexOf(":")>=0) rep=rep.substring(1,rep.lastIndexOf(":")+1)+"***LEASED CODE***";
				else rep="***LEASED CODE***";	
				lines=lines.substring(0,lines.indexOf(hide))+rep+lines.substring(lines.indexOf(hide)+hide.length());
			}
			while (lines.indexOf(";")>=0) {
				lines=lines.substring(0,lines.indexOf(";"))+"\n  "+lines.substring(lines.indexOf(";")+1);
			}
			String finishCommand=this.finishCommand;
			if (finishCommand.indexOf(":")>=0) {
				finishCommand=finishCommand.substring(0,finishCommand.lastIndexOf(":"));
			}
			if (finishCommand.length()>0 && lines.indexOf(" "+finishCommand)>=0) {
				lines=lines.substring(0,lines.indexOf(finishCommand))+"*"+lines.substring(lines.indexOf(finishCommand));
			}
			s += "\n" + lines;
		}
		s += "\n" + log.toString();
		return s;
	}

	public double getDamageRate() {
		double dam = 0;
		String s = (String)vars.get("_damage");
		String t[] = Stuff.getTokens(s, ".");
		if(t.length >= 5) {
			try {
				dam += Integer.parseInt(t[0]) / 100.0;
				dam += Integer.parseInt(t[1]) / 100.0;
				dam += Integer.parseInt(t[2]) / 100.0;
				dam += Integer.parseInt(t[3]) / 100.0;
				dam += Integer.parseInt(t[4]) / 100.0;
				dam = dam / 5;
			}
			catch(NumberFormatException nfe) {
			}
		}
		return dam;
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
		g = new GmlPair("ActiveDesignId", activeDesign.getId());
		v.addElement(g);
		g = new GmlPair("ActiveId", ActiveId);
		v.addElement(g);
		g = new GmlPair("FinishTime", "" + finishTime);
		v.addElement(g);
		g = new GmlPair("FinishCommand", finishCommand);
		v.addElement(g);
		if (desc.length()>0) {
			g = new GmlPair("Desc", desc);
			v.addElement(g);
		}
		if (nick.length()>0){
			 g = new GmlPair("Nick", nick);
			v.addElement(g);
		}
		while(lines.indexOf("\"") >= 0) {
			//lines must not have quotes
			lines = lines.substring(0, lines.indexOf("\"")) + lines.substring(lines.indexOf("\"") + 1);
		}
		g = new GmlPair("Lines", lines);
		v.addElement(g);
		Enumeration keys = vars.keys();
		while(keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			String val = (String)vars.get(key);
			while(val.indexOf("\"") >= 0) {
				//don't save quotes!
				val = val.substring(0, val.indexOf("\"")) + val.substring(val.indexOf("\"") + 1);
			}
			if (!key.equalsIgnoreCase("_location") && !key.equalsIgnoreCase("_time") && !key.equalsIgnoreCase("_cc") && !key.equalsIgnoreCase("_cm")  ) {
				g = new GmlPair("Var", key + ":" + val);
				v.addElement(g);
			}
		}
		g = new GmlPair("Active", v);
		return g;
	}
	//1 hour / command minimum execut time (24/day -> 100/day)
	public void terminate(String s) {
		log("Program Terminated: " + s);
		finishTime = - 1;
		finishCommand = "";
	}
	
	public void loadNextCommand() {
		if(finishTime == - 1) {
			finishCommand = "";
			return;
		}
		if(finishTime == 0) {
			finishTime=(int)(Math.random()*HOUR);
			//load first command
			finishCommand = lines;
			if(finishCommand.indexOf(";") >= 0) {
				finishCommand = finishCommand.substring(0, finishCommand.indexOf(";"));
			}
			String cp = lines.toLowerCase();
			//init all vars.
			while(cp.toUpperCase().indexOf("VAR ") >= 0) {
				String key = cp.substring(cp.toUpperCase().indexOf("VAR ") + 3).trim();
				cp = cp.substring(cp.toUpperCase().indexOf("VAR ") + 4).trim();
				if(key.indexOf(";") > 0) {
					key = key.substring(0, key.indexOf(";"));
				}
				String allkey[] = Stuff.getTokens(key, " \t,");
				for(int a = 0; a < allkey.length; a++) {
					if( !allkey[a].startsWith("$")) {
						log("In line: " + finishCommand + " WARNING: Invalid var name in declaration " + allkey[a] + " assuming $" + allkey[a]);
					}
					else {
						allkey[a] = allkey[a].substring(1);
					}
					if( !vars.containsKey(allkey[a]) && !allkey[a].startsWith("_")) {
						vars.put(allkey[a], "-");
					}
				}
			}
		}
		else if( !gotoLine.equals("")) {
			finishCommand = gotoLine;
			gotoLine = "";
		}
		else {
			//find next command
			if (finishCommand.indexOf(":")>=0)
				finishCommand=finishCommand.substring(0,finishCommand.lastIndexOf(":")+1);
			String next = lines.substring(lines.indexOf(finishCommand) + finishCommand.length()).trim();
			if(next.indexOf(";") < 0) {
				terminate("no more commands");
				return;
			}
			next = next.substring(next.indexOf(";") + 1).trim();
			if(next.indexOf(";") >= 0) {
				next = next.substring(0, next.indexOf(";")).trim();
			}
			if(next.equals("")) {
				terminate("no more commands");
				return;
			}
			finishCommand = next;
		}
		//finishCommand = next command to run
		finishTime = currTime;
		finishCommand=subRandom(finishCommand);
		String toks[] = Stuff.getTokens(finishCommand, " \t");
		if(toks.length == 0) {
			terminate("unparsable command");
			return;
		}
		checkCommand(finishCommand);
		long tc = timeCommand(finishCommand);
		if (Main.DEBUG) System.out.println("DEBUG: Timing command "+finishCommand);
		if (Main.DEBUG) System.out.println("DEBUG: execute time = "+Stuff.trunc(1.0*tc/DAY,2)+" days");
		if(finishTime >= 0)finishTime = currTime + tc;
	}
	
	public void checkCommand(String s) {
		if(Main.DEBUG)System.out.println("DEBUG: check command " + s);
		while(s.indexOf(":") >= 0)s = s.substring(s.indexOf(":") + 1);
		String t = s.toUpperCase().trim();
		s = UniVar.getValue(subVars(s) + "") + "";
		String n[] = Stuff.getTokens(s, " \"\t");
		if(n.length == 0) {
			terminate("No command found");
			return;
		}
		if(t.startsWith("IF ")) {
			if(t.lastIndexOf("IF ")!=t.indexOf("IF")) {
				terminate("Impropper use of IF, nested IFs.");
			}
			if(t.indexOf("ELSE ") > 0 && t.indexOf("ELSE ") < t.lastIndexOf("THEN ")) {
				terminate("Impropper use of IF.  Wrong placement of ELSE.");
			}
			if(t.indexOf("THEN ") < 0) {
				terminate("Impropper use of IF, no THEN.");
			}
		}
		if(t.startsWith("VAR ") || t.startsWith("LOG ") || 
			t.startsWith("SEND ") || t.startsWith("REM"))return;
		if(t.startsWith("SET ")) {
			t = t.substring(4);
			if(t.trim().startsWith("$"))t = t.substring(1);
			if(t.trim().startsWith("_")) {
				terminate("Impropper use of SET, read only variable.");
			}
		}
		if(n[0].equals("NOOP") || n[0].equals("RETURN") || n[0].equals("EXIT") || n[0].equals("VERBOSE") || n[0].equals("BRIEF")) {
			if(n.length != 1) {
				terminate("No parameters allowed in use of " + n[0] + " :" + t);
			}
		}
		if(n[0].equals("REFUEL")) {
			if(n.length != 1 && n.length!=2 && n.length!=3) {
				terminate("Zero or One parameter required in use of " + n[0] + " :" + t);
			}
		}
		if(n[0].equals("RECHARGE") || n[0].equals("SALVAGE") ){
			if(n.length != 1 && n.length!=2 ) {
				terminate("Zero or One parameter required in use of " + n[0] + " :" + t);
			}
		}
		if(n[0].equals("GOTO") || n[0].equals("WAIT") || n[0].equals("MOVE") || 
			n[0].equals("ATTACK") || n[0].equals("CONSTRUCT") || n[0].equals("GOSUB") ||
			n[0].equals("RECOVER") ) {
			if(n.length != 2) {
				terminate("Exactly one parameter required in use of " + n[0] + " :" + t);
			}
		}
		if(n[0].equals("REFINE") || n[0].equals("COLLECT")) {
			if(n.length != 3) {
				terminate("Exactly two parameter required in use of " + n[0] + " : " + t);
			}
		}
		if(n[0].equals("LOAD") ) {
			if(n.length > 4 || n.length <2) {
				terminate("Exactly one, two, or three parameters required in use of " + n[0] + " :" + t);
			}
		}
		if(n[0].equals("REPAIR") ||
			n[0].equals("UNLOAD") || 
			n[0].equals("PRODUCE") || n[0].equals("SCAN") ||
			n[0].equals("SCANLOG") ) {
			if(n.length != 3 && n.length != 2) {
				terminate("Exactly one or two parameters required in use of " + n[0] + " :" + t);
			}
		}
	}
	
	public static String validCommand(String S) {
		String t=S.trim().toUpperCase();
		while(t.indexOf(":") >= 0) t = t.substring(t.indexOf(":") + 1).trim();
		String n[] = Stuff.getTokens(t, " \"\t");
		if(n.length == 0) {
			return "INVALID COMMAND>:No command found"+S;
		}
		if(t.startsWith("IF ")) {
			if(t.lastIndexOf("IF ")!=t.indexOf("IF")) {
				return "INVALID COMMAND>:Nested if found."+S;
			}
			if(t.indexOf("ELSE ") > 0 && t.indexOf("ELSE ") < t.lastIndexOf("THEN ")) {
				return "INVALID COMMAND>:Wrong placement of ELSE."+S;
			}
			if(t.indexOf("THEN ") < 0) {
				return "INVALID COMMAND>:No THEN in IF statement."+S;
			}
			String case1 = t.substring(t.toUpperCase().indexOf(" THEN ") + 6).trim();
			String case2 = "NOOP";
			if(case1.toUpperCase().indexOf(" ELSE ") > 0) {
				case2 = case1.substring(case1.toUpperCase().indexOf(" ELSE ") + 6).trim();
				case1 = case1.substring(0, case1.toUpperCase().indexOf(" ELSE ")).trim();
			}
			if (!case1.equals(validCommand(case1))) 
				return S+"\nINVALID SUBCOMMAND"+validCommand(case1);
			if (!case2.equals(validCommand(case2))) 
				return S+"\nINVALID SUBCOMMAND"+validCommand(case2);
		}
		else if(t.startsWith("VAR")) {
			if (n.length==1) {
				return "INVALID COMMAND>:Wrong number of args."+S;
			}
		}
		else if (t.startsWith("LOG") || t.startsWith("REM")) {
		}
		else if (t.startsWith("SEND")) {
			if (n.length<2) {
				return "INVALID COMMAND>:Wrong number of args."+S;
			}
//robotid or shipid or $
		}
		else if(t.startsWith("SET")) {
			t = t.substring(3).trim();
			if(t.startsWith("$"))t = t.substring(1);
			if(t.startsWith("_")) {
				return "INVALID COMMAND>:Cannot set readonly vars."+S;
			}
		}
		else if(n[0].equals("NOOP") || n[0].equals("RETURN") || n[0].equals("EXIT") || n[0].equals("VERBOSE") || n[0].equals("BRIEF")) {
			if(n.length != 1) {
				return "INVALID COMMAND>:Wrong number of args."+S;
			}
		}
		else if(n[0].equals("REFUEL")) {
			if(n.length != 1 && n.length!=2 && n.length!=3) {
				return "INVALID COMMAND>:Wrong number of args."+S;
			}
		}
		else if(n[0].equals("RECHARGE")|| n[0].equals("SALVAGE") ) {
			if(n.length != 1 && n.length!=2) {
				return "INVALID COMMAND>:Wrong number of args."+S;
			}
		}
		else if(n[0].equals("GOTO") || n[0].equals("WAIT") || 
			n[0].equals("MOVE") || n[0].equals("ATTACK") || 
			n[0].equals("CONSTRUCT") || n[0].equals("GOSUB") ||
			n[0].equals("RECOVER")) {
			if(n.length != 2) {
				return "INVALID COMMAND>:Wrong number of args."+S;
			}
		}
		else if(n[0].equals("REFINE") || n[0].equals("COLLECT")) {
			if(n.length != 3) {
				return "INVALID COMMAND>:Wrong number of args."+S;
			}
		}
		else if(n[0].equals("LOAD") ) {
			if(n.length > 4 || n.length <2) {
				return "INVALID COMMAND>:Wrong number of args."+S;
			}
		}
		else if(n[0].equals("REPAIR") ||
			n[0].equals("UNLOAD") || 
			n[0].equals("PRODUCE") ) {

			if(n.length != 3 && n.length != 2) {
				return "INVALID COMMAND>:Wrong number of args."+S;
			}
		}
		else if (n[0].equals("SCAN") || n[0].equals("SCANLOG") ) {
			if(n.length != 3 && n.length != 2) {
				return "INVALID COMMAND>:Wrong number of args."+S;
			}
			if (n.length==3 && n[1].equalsIgnoreCase("LOG")) {
				return "INVALID COMMAND>:SCANLOG is one word."+S;
			}
			if (
			!n[1].startsWith("TEM") && !n[1].startsWith("SUR") &&
			!n[1].startsWith("RES") && !n[1].startsWith("OBJ") &&
			!n[1].startsWith("OBJ") && !n[1].startsWith("ROB") &&
			!n[1].startsWith("SHI") && !n[1].startsWith("PRE") &&
			!n[1].startsWith("STO") && !n[1].startsWith("FAC") &&
			!n[1].startsWith("SEC") && !n[1].startsWith("TAR") && 
			!n[1].startsWith("SYS") &&
			n[1].indexOf("$")<0) {
				return "INVALID COMMAND>:SCAN types must be SYS,SEC,TEM,SUR,RES,STO,ROB,SHI,FAC,OBJ,TAR, or PRE."+S;
			}
		}
		else {
			return "INVALID COMMAND>:Unknown command."+S;
		}

		return S;
	}

	public long timeCommand(String s) {
		String S = s;
		if(s.indexOf(":") >= 0) {
			s = s.substring(s.lastIndexOf(":") + 1).trim();
		}
		if (Main.DEBUG) System.out.println("DEBUG: Timing command "+s);

		if(s.toUpperCase().startsWith("GOTO ") || 
			s.toUpperCase().startsWith("GOSUB ") || 
			s.toUpperCase().startsWith("RETURN")) {
			return MINUTE*10;
		}
		if(s.toUpperCase().startsWith("LOG")) {
			return MINUTE*30;
		}
		if(s.toUpperCase().startsWith("REM") || s.toUpperCase().startsWith("NOOP") || 
			s.toUpperCase().startsWith("SET ") || s.toUpperCase().startsWith("VAR ")||
			s.toUpperCase().startsWith("VERBOSE") || s.toUpperCase().startsWith("BRIEF")) {
			return MINUTE*10;
		}
		if (s.toUpperCase().startsWith("IF ")) {
//time command
			if(s.toUpperCase().indexOf(" THEN ") < 0) {
				return ERRORTIME;
			}
			String test = s.substring("IF ".length(), s.toUpperCase().indexOf(" THEN ")).trim();
			test = UniVar.getValue(subVars(test) + "") + "";//required

			String case1 = s.substring(s.toUpperCase().indexOf(" THEN ") + 6).trim();
			if(case1.indexOf("IF ") >= 0) {
				return ERRORTIME;
			}
			String case2 = "NOOP";
			if(case1.toUpperCase().indexOf(" ELSE ") > 0) {
				case2 = case1.substring(case1.toUpperCase().indexOf(" ELSE ") + 6).trim();
				case1 = case1.substring(0, case1.toUpperCase().indexOf(" ELSE ")).trim();
			}
			if (Main.DEBUG) System.out.println("DEBUG: case1="+case1+" time case1="+timeCommand(case1));
			if (Main.DEBUG) System.out.println("DEBUG: case2="+case2+" time case2="+timeCommand(case2));
			String subCommand =null;
			if( !test.equals("0")) {
				subCommand = case1;
			}
			else {
				subCommand = case2;
			}
			if (Main.DEBUG) System.out.println("DEBUG: subCommand="+subCommand+" time subCommand="+timeCommand(subCommand));
			return MINUTE*10+timeCommand(subCommand);
		}
		if(s.toUpperCase().startsWith("EXIT")) {
			return HOUR;
		}
		if(s.toUpperCase().startsWith("WAIT ")) {
			s = UniVar.getValue(subVars(s) + "") + "";
			if(s.startsWith("\""))s = s.substring(1);
			if(s.endsWith("\""))s = s.substring(0, s.length() - 1);
			String tt = s.toUpperCase().trim();
			String n[] = Stuff.getTokens(tt, " \"\t");
			if(n.length != 2) {
				return ERRORTIME;
			}
			try {
				long del=0;
				String d[]=Stuff.getTokens(n[1],".");
				if (d.length==1) {
					del = (long)(HOUR *Math.max(0,Math.min(24,Stuff.parseDouble(d[0]))));
				}
				if (d.length==2) {
					del = (long)(HOUR *Math.max(0,Math.min(24,Stuff.parseDouble(d[1]))));
					del += (long)(DAY *Math.max(0,Math.min(31,Stuff.parseDouble(d[0]))));
				}
				if (d.length==3) {
					del = (long)(HOUR *Math.max(0,Math.min(24,Stuff.parseDouble(d[2]))));
					del += (long)(DAY *Math.max(0,Math.min(31,Stuff.parseDouble(d[1]))));
					del += (long)(MONTH *Math.max(0,Stuff.parseDouble(d[1])));
				}
				return del;
			}
			catch(NumberFormatException NFE) {
				return ERRORTIME;
			}
		}
		if(s.toUpperCase().startsWith("LOAD ")) {
			s = UniVar.getValue(subVars(s) + "") + "";
			if(s.startsWith("\""))s = s.substring(1);
			if(s.endsWith("\""))s = s.substring(0, s.length() - 1);
			String tt = s.toUpperCase().trim();
			String n[] = Stuff.getTokens(tt, " \"\t");
			if(n.length < 2 || n.length > 4) {
				if (Main.DEBUG) log ("DEBUG: error in args in LOAD");
				return ERRORTIME;
			}
			Location loadloc=loc;
			if (n.length>2) {
				loadloc = Location.parse(n[1]);
				if(loadloc == null) {
					loadloc = loc.reference(n[1]);//N,S,E,W, U,D, ESC, LAU, REE,LAN, EXIT , ID
				}
				if (loadloc!=null) {
					int moveType = loc.getMoveType(loadloc);
					if(moveType == Location.INTERPLANET || moveType == Location.ESCAPE || moveType == Location.REENTRY) {
						return ERRORTIME;
					}
//SECURITY HERE
					if (loadloc.isInside()) {
						NFObject nfoo=Universe.getNFObjectById(loadloc.getInsideWhat());
						if (nfoo instanceof Active) {
							Active aaa=(Active)nfoo;
							if (aaa.hasTech("T57") && !aaa.getCorpTick().equalsIgnoreCase(corpTick)) {
								return ERRORTIME;
							}
						}
						if (nfoo instanceof Facility) {
							Facility aaa=(Facility)nfoo;
							if (aaa.hasTech("T57") && !aaa.getCorpTick().equalsIgnoreCase(corpTick)) {
								return ERRORTIME;
							}
						}
					}
					String nn[];
					if (n.length>3) {
						nn=new String[3];
						nn[2]=n[3];
					}
					else nn=new String[2];
					nn[0]=n[0];nn[1]=n[2];
					n=nn;
				}
				else loadloc=loc;
			}
			int cc=getCargoCapacity();
			int totalMass = 1;
			NFObject nfo = Universe.getNFObjectById(n[1]);
			//find NFOID
			if(nfo == null && Market.getMarketName(n[1]) == null) {
				if (Main.DEBUG) log ("DEBUG: error material name in LOAD");
				return ERRORTIME;
			}
			if(nfo == null ) {
				n[1]=Market.getMarketName(n[1]);
				totalMass=cc;
				if (Main.DEBUG) log ("DEBUG: material load found");
				if (Main.DEBUG) log ("DEBUG: cargo capacity = "+totalMass);
				try {
					if (n.length==3) {
						totalMass = (int)(Stuff.parseDouble(n[2]));
						if (Main.DEBUG) log ("DEBUG: limit number found= "+totalMass);
					}
				}
				catch(NumberFormatException NFE) {
					if (Main.DEBUG) log ("DEBUG: error in number in LOAD");
					return ERRORTIME;
				}
				int ava=Universe.availableMaterial(loadloc, n[1]);
				if (Main.DEBUG) log ("DEBUG: avail limit number found= "+ava);
				totalMass=Math.min(totalMass,ava);
				if (Main.DEBUG) log ("DEBUG: new totalmass found = "+totalMass);
			}
			else if(nfo != null) {
				if (nfo == this) {
					if (Main.DEBUG) log ("DEBUG: error in self load in LOAD");
					return ERRORTIME;
				}
				if (nfo instanceof Facility) {
					if (Main.DEBUG) log ("DEBUG: error in load cannot load facility ");
					return ERRORTIME;
				}
				if (!nfo.getLocation().equals(loadloc)) {
					if (Main.DEBUG) log ("DEBUG: error in bad locaiton in LOAD");
					return ERRORTIME;
				}
				if (cc<nfo.getMass()) {
					if (Main.DEBUG) log ("DEBUG: error over full cargo requested in LOAD");
					return ERRORTIME;
				}
				totalMass = nfo.getMass();
			}
			if( !hasTech("T35") && !hasTech("T36") && !hasTech("T88")) {
				if (Main.DEBUG) log ("DEBUG: error bad tech in LOAD");
				return ERRORTIME;
			}
			if (totalMass<=0) {
				if (n.length>2) 
					if (Main.DEBUG) 
						log ("DEBUG: error negitiave load in LOAD ="+totalMass+" n[1]="+n[1]+" n[2]=("+n[2]+")"+" parsed="+(int)Stuff.parseDouble(n[2]));
				if (n.length==2) 
					if (Main.DEBUG) log ("DEBUG: error negitiave load in LOAD ="+totalMass+" n[1]="+n[1]);
				return ERRORTIME;
			}
			if (Main.DEBUG) log ("DEBUG: load for "+totalMass+" requested");;
			if (hasTech("T88")) 
				return HOUR+HOUR * totalMass/6;
			if (hasTech("T36")) 
				return HOUR+HOUR * totalMass/2;
			if (hasTech("T35")) 
				return HOUR+HOUR * totalMass;
		}
		if(s.toUpperCase().startsWith("UNLOAD ")) {
			//rebuild cargo var
			getCargoMass();
			s = UniVar.getValue(subVars(s) + "") + "";
			String tt = s.toUpperCase().trim();
			String n[] = Stuff.getTokens(tt, " \"\t");
			if(n.length != 2 && n.length != 3) {
				return ERRORTIME;
			}
			int totalMass = 0;
			NFObject nfo = Universe.getNFObjectById(n[1]);
			//find NFOID
			if(nfo == null && Market.getMarketName(n[1]) != null) {
				n[1]=Market.getMarketName(n[1]);
				totalMass = Universe.availableMaterial(Location.parse("INSIDE." + getId()), n[1]);
				try {
					if (n.length==3) 
						totalMass = Math.max(0,Math.min(totalMass,(int)Stuff.parseDouble(n[2])));
				}
				catch(NumberFormatException NFE) {
					return ERRORTIME;
				}
			}
			else if(nfo != null) {
				if (!nfo.getLocation().equalsIgnoreCase(Location.parse("INSIDE."+getId()))) {
					return ERRORTIME;
				}
				totalMass = nfo.getMass();
			}
			else return ERRORTIME;
			if (totalMass<=0) return ERRORTIME;
			if (hasTech("T88")) 
				return HOUR+HOUR * totalMass/6;
			if (hasTech("T36")) 
				return HOUR+HOUR * totalMass/2;
			if (hasTech("T35")) 
				return HOUR+HOUR * totalMass;
		}
		if(s.toUpperCase().startsWith("SCAN ") || s.toUpperCase().startsWith("SCANLOG ")) {
			s = UniVar.getValue(subVars(s) + "") + "";
			if(s.startsWith("\""))s = s.substring(1);
			if(s.endsWith("\""))s = s.substring(0, s.length() - 1);
			String tt = s.toUpperCase().trim();
			String n[] = Stuff.getTokens(tt, " \"\t");
			if(n.length != 2 && n.length != 3) {
				return ERRORTIME;
			}
			if(n[1].toUpperCase().startsWith("SYS")) {
				if (!loc.isOrbit()) {
					return ERRORTIME;
				}
				if (!hasTech("T95")) return ERRORTIME;
				return DAY*2;
			}
			if(n[1].toUpperCase().startsWith("SEC")) {
				//scan sector 1
				Body b = Universe.getBodyByLocation(loc);
				if(b == null || !loc.isOrbit()) {
					return ERRORTIME;
				}
				if(b != null)return DAY * (b.size / 2);
				return ERRORTIME;
			}
			return HOUR;
			//scan time=1 hour
			//sector scan time =1 day/size
		}
		if(s.toUpperCase().startsWith("MOVE ")) {
			s = UniVar.getValue(subVars(s) + "") + "";
			if(s.startsWith("\""))s = s.substring(1);
			if(s.endsWith("\""))s = s.substring(0, s.length() - 1);
			String tt = s.toUpperCase().trim();
			String n[] = Stuff.getTokens(tt, " \"\t");
			if (n.length!=2) return ERRORTIME;
			Location l = Location.parse(n[1]);
			if(l == null) {
				l = loc.reference(n[1]);//N,S,E,W, U,D, ESC, LAU, REE,LAN, EXIT
			}
			if(l == null) {
				return ERRORTIME;
			}
			if(l.equals(getInsideLoc())) {//load self error
				return ERRORTIME;
			}
			if( !Location.adjacentTo(l, this.loc)) {
				return ERRORTIME;
			}
			if( !l.valid()) {
				return ERRORTIME;
			}
			int moveType = loc.getMoveType(l);
			if(moveType == Location.LOAD || moveType == Location.UNLOAD) {
				return HOUR;
//tech mobility check or ERRORTIME is HOUR so no error check required
			}
			if(moveType == Location.SURFACE) {
				consumeFuel(.06);
				boolean atm = false;
				Body b = Universe.getBodyByLocation(l);
				if(b != null)atm = b.hasAtmosphere();
				String sur = Universe.getSurfaceByLocation(l);
				if (atm && hasTech("T6")) return DAY/4;//wings
				if (Universe.locationContainsTechnology(l,"T58") && hasTech("T1")) return DAY/3;//wheels on road
				if (Universe.locationContainsTechnology(l,"T58")) return DAY;//road
				if (Universe.locationContainsTechnology(l,"T59")) return DAY;//tunnel

				int techlevel = 0;
				if (hasTech("T8") && l.isOrbit()) return HOUR;
				if (sur.equalsIgnoreCase("P")) return DAY;
				if (sur.equalsIgnoreCase("W")) return 2*DAY;
				if (sur.equalsIgnoreCase("B")) return 2*DAY;
				if (sur.equalsIgnoreCase("M")) return 4*DAY;
				return DAY;
			}
			if(moveType == Location.SUBTER) {
				consumeFuel(.12);
				if (Universe.locationContainsTechnology(l,"T59")) return DAY;//tunnel
				return DAY * 2;
			}
			if(moveType == Location.REENTRY) {
				Body bod=Universe.getBodyByLocation(l);
				if (bod!=null) {
					if (hasTech("T6") && bod.hasAtmosphere()) 
						consumeFuel(bod.getGravity()*MONTH/MONTH/4);
					if (hasTech("T10") && bod.hasAtmosphere()) 
						consumeFuel(bod.getGravity()*MONTH/MONTH/2);
					if (hasTech("T11")) 
						consumeFuel(bod.getGravity()*MONTH/MONTH/2);
					else
						consumeFuel(bod.getGravity()*MONTH/MONTH);
				}
//TODO todo if no space mobility tech then error time
				return 6 * HOUR;
			}
			if(moveType == Location.ESCAPE) {
				Body bod=Universe.getBodyByLocation(l);
				if (bod!=null) {
						consumeFuel(bod.getGravity()*MONTH/MONTH);
				}
//TODO todo check if escape facility has takeoff ability LANDINGZONE or error time
				return DAY;
			}
			if(moveType == Location.INTERPLANET) {
				double acc = .62;
				if (hasTech("T12")) acc=.61;
				if (hasTech("T13")) acc=1.21;
				if (hasTech("T14")) acc=2.42;
				if (hasTech("T15")) acc=.4;
				double maxvel = 300000000 * .01;
				double maxt = maxvel / acc * 2;
				double accdis = .5 * acc * maxt * maxt;
				Body locp = Universe.getBodyByLocation(loc);
				Body lp = Universe.getBodyByLocation(l);
				if(locp != null || lp != null) {
					DPoint dloc = locp.getDPoint(currTime);
					DPoint dl = lp.getDPoint(currTime);
					DPoint aloc = dloc;
					DPoint bloc = dl;
					double dx = (aloc.getX() - bloc.getX());
					double dy = (aloc.getY() - bloc.getY());
					double d = Math.pow((dx * dx) + (dy * dy), .5);
					double t = Math.pow(d / 2 / acc, .5) * 2;
					//in secs halfway twice 
					t *= 1000;
					//convert to millis
					if (!hasTech("T15")) 
						consumeFuel(Math.max(t/MONTH,.04));
					else 
						consumeFuel(Math.max(t/MONTH/4,.01));
					return(long)t;
				}
				else {
					return MONTH;
				}
			}
			if(moveType == Location.INTERSOLAR) {
//TODO add WARP DRIVE???
				//if warp+
				consumeFuel(1);
				return MONTH * 12 * 40;
			}
			return ERRORTIME;
		}
		//COLLECT <eq> <eq>
		if(s.toUpperCase().startsWith("COLLECT ")) {
			s = UniVar.getValue(subVars(s) + "") + "";
			if(s.startsWith("\""))s = s.substring(1);
			if(s.endsWith("\""))s = s.substring(0, s.length() - 1);
			String tt = s.toUpperCase().trim();
			String n[] = Stuff.getTokens(tt, " \"\t");
			if(n.length != 3) {
				return ERRORTIME;
			}
			if(!hasTech("T36")) {
				return ERRORTIME;
			}
			String name = n[1];
			int den = Universe.getMaterialDensity(loc, name);
//collector
			if(den<6 &&  !Universe.locationHasTechnology(loc, "T44")) {
				return ERRORTIME;
			}
//Condensor
			if(den==6 &&  !Universe.locationHasTechnology(loc, "T52")) {
				return ERRORTIME;
			}
//Distilation
			if(den>6 &&  !Universe.locationHasTechnology(loc, "T51")) {
				return ERRORTIME;
			}
			int amt = 1;
			try {
				amt=(int)Stuff.parseDouble(n[2]);
			} catch (NumberFormatException nfe) {
				return ERRORTIME;
			}
			double rate = 2.0 / DAY;
			switch(den) {
				case 7:rate = DAY/8.0;break;
				case 6:rate = DAY/6.0;break;
				case 5:rate = DAY/3.0;break;
				case 4:rate = DAY/2;break;
				case 3:rate = DAY;break;
				case 2:rate = WEEK/4;break;
				case 1:rate = WEEK/2;break;
				case 0:return ERRORTIME;
			}
			return(long)(HOUR+amt * rate);
		}
		//REFINE <eq> <eq>
		if(s.toUpperCase().startsWith("REFINE ")) {
			s = UniVar.getValue(subVars(s) + "") + "";
			if(s.startsWith("\""))s = s.substring(1);
			if(s.endsWith("\""))s = s.substring(0, s.length() - 1);
			String tt = s.toUpperCase().trim();
			String n[] = Stuff.getTokens(tt, " \"\t");
			if(n.length != 3) {
				return ERRORTIME;
			}
			if(!hasTech("T36")) {
				return ERRORTIME;
			}
			if( !Universe.locationHasTechnology(loc, "T45")) {
				return ERRORTIME;
			}
			int amt = 1;
			try {
				if (n.length==3) {
					amt=(int)Stuff.parseDouble(n[2]);
				}
				if (amt==0) {
					return ERRORTIME;
				}
			} catch (NumberFormatException nfe) {
				return ERRORTIME;
			}

			MarketItem mi = Universe.getMarket().getItemByName(n[1]);
			if(mi == null) {
				return ERRORTIME;
			}
			String reqmat = mi.getRefineMaterial();
			if(reqmat == null) {
				return ERRORTIME;
			}
			//check for material availability
			int ava = Universe.availableMaterial(Location.parse("INSIDE." + getId()), reqmat);
			ava += Universe.availableMaterial(loc, reqmat);
			if (ava==0) return ERRORTIME;
			amt=Math.min(ava,amt);
			if (amt==0) return ERRORTIME;
			return HOUR+amt * REFINERATE;
		}
		//SALVAGE [<amt>]
		if(s.toUpperCase().startsWith("SALVAGE ")) {
			s = UniVar.getValue(subVars(s) + "") + "";
			if(s.startsWith("\""))s = s.substring(1);
			if(s.endsWith("\""))s = s.substring(0, s.length() - 1);
			String tt = s.toUpperCase().trim();
			String n[] = Stuff.getTokens(tt, " \"\t");
			if(n.length != 1 && n.length != 2) {
				return ERRORTIME;
			}
			int ava = Universe.availableMaterial(Location.parse("INSIDE." + getId()), "JUNK");
			ava += Universe.availableMaterial(loc, "JUNK");
			if (ava<1) return ERRORTIME;
			if(!hasTech("T36")) {
				return ERRORTIME;
			}
			if( !Universe.locationHasTechnology(loc, "T48") && !hasTech("T48")) {
				return ERRORTIME;//this is also covered below
			}
			else {
				if(n.length == 2) {
					try {
						ava=Math.max(0,Math.min(ava,(int)Stuff.parseDouble(n[1])));
					}
					catch(NumberFormatException nfe) {
						return ERRORTIME;
					}
				}
				if (ava==0) {
					return ERRORTIME;
				}
				if(Universe.locationHasTechnology(loc, "T48") && hasTech("T48")) {
					return HOUR+ava * SALVAGERATEFAC/2;
				}
				else if(Universe.locationHasTechnology(loc, "T48")) {
					return HOUR+ava * SALVAGERATEFAC;
				}
				else if(hasTech("T48")) {
					return HOUR+ava * SALVAGERATE;
				}
				else {
					return ERRORTIME;
				}
			}
		}
		//RECOVER <eq>
		if(s.toUpperCase().startsWith("RECOVER ")) {
			s = UniVar.getValue(subVars(s) + "") + "";
			if(s.startsWith("\""))s = s.substring(1);
			if(s.endsWith("\""))s = s.substring(0, s.length() - 1);
			String tt = s.toUpperCase().trim();
			String n[] = Stuff.getTokens(tt, " \"\t");
			if(n.length == 2) {
				NFObject nfo = Universe.getNFObjectById(n[1]);
				//find NFOID
				if(nfo == null || !(nfo instanceof Facility)) {
					return ERRORTIME;
				}
				if( !nfo.getLocation().equals(loc)) {
					return ERRORTIME;
				}
				else if(!hasTech("T36")) {//heavy work arms ??? add salvage or repair?
					return ERRORTIME;
				}
				Facility f=(Facility)nfo;
				if (!f.canRecover()) return ERRORTIME;
				return HOUR+nfo.getMass() * RECOVERRATE;
			}
			return ERRORTIME;
		}
		//CONSTRUCT <eq>
		if(s.toUpperCase().startsWith("CONSTRUCT ")) {
			s = UniVar.getValue(subVars(s) + "") + "";
			if(s.startsWith("\""))s = s.substring(1);
			if(s.endsWith("\""))s = s.substring(0, s.length() - 1);
			String tt = s.toUpperCase().trim();
			String n[] = Stuff.getTokens(tt, " \"\t");
			if(n.length == 2) {
				NFObject nfo = Universe.getNFObjectById(n[1]);
				//find NFOID
				if(nfo == null || !(nfo instanceof Prefab)) {
					return ERRORTIME;
				}
				if( !nfo.getLocation().equals(loc)) {
					return ERRORTIME;
				}
				else if(!hasTech("T36")) {
					return ERRORTIME;
				}
				if(loc.isInside() || loc.isFacility()) {
					return ERRORTIME;
				}
				Prefab pf=(Prefab)nfo;
				if (!pf.canConstructAt(loc)) {
					return ERRORTIME;
				}
				if (Universe.facilityCount(loc)>3) {
					return ERRORTIME;
				}
				return HOUR+nfo.getMass() * CONSTRUCTRATE;
			}
			return ERRORTIME;
		}
		//PRODUCE <eq> <eq>
		if(s.toUpperCase().startsWith("PRODUCE ")) {
			s = UniVar.getValue(subVars(s) + "") + "";
			if(s.startsWith("\""))s = s.substring(1);
			if(s.endsWith("\""))s = s.substring(0, s.length() - 1);
			String tt = s.toUpperCase().trim();
			String n[] = Stuff.getTokens(tt, " \"\t");
			//if prefab produce base on weight
			//else produce base on number
			if(n.length == 2) {
				ITThing itt = Universe.getITThingByName(n[1]);
				//find NFOID
				if(itt == null) {
					return ERRORTIME;
				}
				if(!hasTech("T36")) {
					return ERRORTIME;
				}
				if( !Universe.locationHasTechnology(loc, "T46")) {
					return ERRORTIME;
				}
				if(itt instanceof ActiveDesign) {
					ActiveDesign att = (ActiveDesign)itt;
					int amt2=availableMaterialList(Location.parse("INSIDE."+getId()),loc,att.getAllProductionCosts());
					if (amt2<1) return ERRORTIME;
					if (Main.DEBUG) System.out.println("DEBUG: active PRODUCE AMOUNT CHECK available ="+amt2);
					return(long)(HOUR+att.getDesignMass() * PRODRATE);
				}
				else if(itt instanceof FacilityDesign) {
					FacilityDesign att = (FacilityDesign)itt;
					int amt2=att.getDesignMass()*availableMaterialList(Location.parse("INSIDE."+getId()),loc,att.getAllProductionCosts());
					if (amt2<1) return ERRORTIME;
					if (Main.DEBUG) System.out.println("DEBUG: facility PRODUCE AMOUNT CHECK  available ="+amt2);
					return(long)(HOUR+att.getDesignMass() * PRODRATE);
				}
				else {
					return ERRORTIME;
				}
			}
			else if(n.length == 3) {
				try {	
					String mat = Market.getMarketName(n[1]);
					int amt=(int)Stuff.parseDouble(n[2]);
					MarketItem mi = Universe.getMarket().getItemByName(mat);
					if (mi==null) return ERRORTIME;
					if (mi.getId().equals("M1")) {
						if(!hasTech("T36")) {
							return ERRORTIME;
						}
						if( !Universe.locationHasTechnology(loc, "T49") && !Universe.locationHasTechnology(loc, "T50")) {
							return ERRORTIME;
						}
						int unitsize=1;
						int amt2=0;
						if( !Universe.locationHasTechnology(loc, "T49") ) {
							amt2+=availableMaterialList(Location.parse("INSIDE."+getId()),loc,"M70");
						}
						if( !Universe.locationHasTechnology(loc, "T50") ) {
							amt2+=availableMaterialList(Location.parse("INSIDE."+getId()),loc,"M68");
							amt2+=availableMaterialList(Location.parse("INSIDE."+getId()),loc,"M69");
						}
						amt=Math.min(amt2,amt);
						return(long)(HOUR+amt * PRODRATE);
					}
					else if (mi.isProduced()){
						if(!hasTech("T36")) {
							return ERRORTIME;
						}
						if( !Universe.locationHasTechnology(loc, "T46")) {
							return ERRORTIME;
						}
						int unitsize=mi.getUnitSize();
						amt=amt+(amt%unitsize!=0?unitsize-(amt%unitsize):0);
						if (Main.DEBUG) System.out.println("DEBUG: Material id "+n[1]+" is "+mat+" is "+mi.getName()+" with prod of "+mi.getProducedMaterial());
						int amt2=unitsize*availableMaterialList(Location.parse("INSIDE."+getId()),loc,mi.getProducedMaterial());
						if (Main.DEBUG) System.out.println("DEBUG: PRODUCE AMOUNT CHECK asked for "+amt+" available ="+amt2);
						amt=Math.min(amt2,amt);
						return(long)(HOUR+amt * PRODRATE);
					}
					else {
						return ERRORTIME;
					}
				} catch (NumberFormatException nfe) {
					return ERRORTIME;
				}
			}
			else {
				return ERRORTIME;
			}
		}
		//RECHARGE [<eq>]  : rechare robot by id (default self)
		if(s.toUpperCase().startsWith("RECHARGE")) {
			s = UniVar.getValue(subVars(s) + "") + "";
			if(s.startsWith("\""))s = s.substring(1);
			if(s.endsWith("\""))s = s.substring(0, s.length() - 1);
			String tt = s.toUpperCase().trim();
			String n[] = Stuff.getTokens(tt, " \"\t");
			if (isShip()) return ERRORTIME;
			if(n.length == 1) {
				if (loc.isInside()) {
					NFObject nfo=Universe.getNFObjectById(loc.getInsideWhat());
					if (nfo==null) {
						return ERRORTIME;
					}
					if (nfo==this) {
						return ERRORTIME;
					}
					if (nfo instanceof Active) {
						Active a=(Active)nfo;
						if (a.isPowered()){
							return HOUR*2;
						}
					}
					//NO ERRORTIME for bad facilities.  ERRORTIME is normal time
				}
			}
			if(n.length == 2) {
				NFObject nfo=Universe.getNFObjectById(n[1]);
				if (nfo==null) {
					return ERRORTIME;
				}
				if (nfo==this) {
					return ERRORTIME;
				}
				if (nfo instanceof Active) {
					return HOUR*2;
				}
				
			}
			return ERRORTIME;
		}
//REFUEL : fill own tank (done)
//REFUEL <amount> : fill own tank amount
//REFUEL <id>  :file other tank
//REFUEL <id> <amt> :file other tank amount
		if(s.toUpperCase().startsWith("REFUEL ")) {
			s = UniVar.getValue(subVars(s) + "") + "";
			if(s.startsWith("\""))s = s.substring(1);
			if(s.endsWith("\""))s = s.substring(0, s.length() - 1);
			String tt = s.toUpperCase().trim();
			String n[] = Stuff.getTokens(tt, " \"\t");
//no error checking required error time== execute time (error = 2hour not one hour)
			if (n.length>3) return ERRORTIME;
			return HOUR*2;
		}
		//REPAIR <eq> [<eq>]      : repair robot or facility id
		if(s.toUpperCase().startsWith("REPAIR ")) {
			s = UniVar.getValue(subVars(s) + "") + "";
			if(s.startsWith("\""))s = s.substring(1);
			if(s.endsWith("\""))s = s.substring(0, s.length() - 1);
			String tt = s.toUpperCase().trim();
			String n[] = Stuff.getTokens(tt, " \"\t");
			int amt = 0;
			if(n.length < 2) {
				return ERRORTIME;
			}
			NFObject nfo = Universe.getNFObjectById(n[1]);
			//find NFOID
			if(nfo == null) {
				return ERRORTIME;
			}
			if(nfo == this) {
				return ERRORTIME;
			}
			if(nfo instanceof StockPile || nfo instanceof Prefab) {
				return ERRORTIME;
			}
			if(!hasTech("T36")) {
				return ERRORTIME;
			}
			if (!nfo.getLocation().equalsIgnoreCase(loc)) {
				return ERRORTIME;
			}
			if(nfo instanceof Active) {
				Active aa = (Active)nfo;
				amt = aa.getRepairMass();
			}
			if(nfo instanceof Facility) {
				Facility aa = (Facility)nfo;
				amt = aa.getRepairMass();
			}
if (Main.DEBUG) {
	System.out.println("DEBUG: repair test mass needed"+amt);
}
			if(n.length == 3) {
				try {
					amt = Math.max(0, Math.min(amt,(int) Stuff.parseDouble(n[2])));
				}
				catch(NumberFormatException nfe) {
					return ERRORTIME;
				}
			}
			//check for material availability
			int ava = Universe.availableMaterial(Location.parse("INSIDE." + getId()), "SpareParts");
			ava += Universe.availableMaterial(loc, "SpareParts");
			if (Main.DEBUG) System.out.println("DEBUG: repair test parts ava "+ava);
			if(ava == 0 || amt==0) {
				return ERRORTIME;
			}
			if(ava < amt) {
				amt = ava;
			}
			if (Main.DEBUG) System.out.println("DEBUG: repair test mass used "+amt);
			if(Universe.locationHasTechnology(loc, "T47") && hasTech("T47")) return HOUR+amt * REPAIRRATEFAC/2;
			else if(Universe.locationHasTechnology(loc, "T47")) return HOUR+amt * REPAIRRATEFAC;
			else if(hasTech("T47"))return HOUR+amt * REPAIRRATE;
			else return ERRORTIME;
		}
		//ATTACK <eq>      : attack robot or facility id
		if(s.toUpperCase().startsWith("ATTACK ")) {
			s = UniVar.getValue(subVars(s) + "") + "";
			if(s.startsWith("\""))s = s.substring(1);
			if(s.endsWith("\""))s = s.substring(0, s.length() - 1);
			String tt = s.toUpperCase().trim();
			String n[] = Stuff.getTokens(tt, " \"\t");
			if(n.length != 2) {
				return ERRORTIME;
			}
			NFObject nfo = Universe.getNFObjectById(n[1]);
			if(nfo == null || nfo instanceof StockPile || nfo instanceof Prefab || nfo== this) {
				return ERRORTIME;
			}
			if (!nfo.getLocation().equals(getLocation())) {
				return ERRORTIME;
			}
			return HOUR;
		}
		//SEND <eq> <eq>   : send message to robot
		if(s.toUpperCase().startsWith("SEND ")) {
			s = UniVar.getValue(subVars(s) + "") + "";
			if(s.startsWith("\""))s = s.substring(1);
			if(s.endsWith("\""))s = s.substring(0, s.length() - 1);
			String tt = s.toUpperCase().trim();
			String n[] = Stuff.getTokens(tt, " \"\t");
			if(n.length < 2) {
				return ERRORTIME;
			}
			NFObject nfo = Universe.getNFObjectById(n[1]);
			if(nfo == null || !(nfo instanceof Active)) {
				return ERRORTIME;
			}
			return HOUR;
		}
		///default time
		return ERRORTIME;
	}
	
	public String subRandom(String s) {
		while(s.toLowerCase().indexOf("$_random") >= 0) {
			s = s.substring(0, s.toLowerCase().indexOf("$_random")) + Math.random() + s.substring(s.toLowerCase().indexOf("$_random") + "$_random".length());
		}
		return s;
	}

	public String subVars(String s) {
		vars.put("_time", myTimeFormat(currTime));
		vars.put("_location", loc + "");
		vars.put("_id", getId() + "");
			
		vars.put("_cc",getCargoCapacity()+"");
		vars.put("_cm",getCargoMass()+"");
		Enumeration keys = vars.keys();
		while(keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			String val = (String)vars.get(key);
			while(s.toLowerCase().indexOf("$" + key) >= 0) {
				s = s.substring(0, s.toLowerCase().indexOf("$" + key)) + val + s.substring(s.toLowerCase().indexOf("$" + key) + 1 + key.length());
			}
		}
		return s;
	}
	
	public void executeCommand() {
		if (lines.toUpperCase().indexOf("VERBOSE")>=0) {
			verbose=true;
		}
		if (lines.toUpperCase().indexOf("BRIEF")>=0) {
			verbose=false;
		}
		if(Main.DEBUG)System.out.println("DEBUG: "+getId()+" Executing command (" + finishCommand + ")");
		String workingCommand = finishCommand;
		if( !subCommand.equals("")) {
			workingCommand = subCommand;
			subCommand = "";
		}
		if(workingCommand.indexOf(":") >= 0) {
			workingCommand = workingCommand.substring(workingCommand.lastIndexOf(":") + 1).trim();
		}
		if(Main.DEBUG)System.out.println("DEBUG: working command " + workingCommand);
		boolean leasedcommand=false;
		if (finishCommand.startsWith("_")) {
			leasedcommand=true;
		}
		if(verbose && !leasedcommand)		
			nolog(myTimeFormat(currTime) + " " + workingCommand);
		//NOOP and VAR
		if(workingCommand.toUpperCase().startsWith("NOOP") || workingCommand.toUpperCase().startsWith("VAR ") ||
		   workingCommand.toUpperCase().startsWith("WAIT")) {
			return;
		}
		else if(workingCommand.toUpperCase().startsWith("BRIEF")) {
			verbose=false;
		}
		else if(workingCommand.toUpperCase().startsWith("VERBOSE")) {
			verbose=true;
		}
		else if(workingCommand.toUpperCase().startsWith("EXIT")) {
			terminate("EXIT found");
			return;
		}
		//SET <VAR>=<EQ>
		else if(workingCommand.toUpperCase().startsWith("SET ")) {
			String var = workingCommand.trim().substring("SET".length()).trim();
			String eq = "";
			if(var.indexOf("=") >= 0 && var.indexOf(" ") >= 0 && var.indexOf("=") < var.indexOf(" ")) {
				eq = var.substring(var.indexOf("=")).trim();
				var = var.substring(0, var.indexOf("=")).trim();
			}
			else if(var.indexOf("=") >= 0 && var.indexOf(" ") >= 0 && var.indexOf("=") > var.indexOf(" ")) {
				eq = var.substring(var.indexOf(" ")).trim();
				var = var.substring(0, var.indexOf(" ")).trim();
			}
			else if(var.indexOf("=") >= 0 && var.indexOf(" ") < 0) {
				eq = var.substring(var.indexOf("=")).trim();
				var = var.substring(0, var.indexOf("=")).trim();
			}
			else if(var.indexOf("=") <= 0 && var.indexOf(" ") > 0) {
				eq = var.substring(var.indexOf(" ")).trim();
				var = var.substring(0, var.indexOf(" ")).trim();
			}
			if(eq.startsWith("="))eq = eq.substring(1).trim();
			var = var.toLowerCase();
			if(var.startsWith("$")) {
				var = var.substring(1).trim();
			}
			if( !vars.containsKey(var)) {
				log("In line " + workingCommand + " WARNING: Undefined varabile " + var);
				return;
			}
			if(var.startsWith("_") && !Main.DEBUG) {
				log("In line " + workingCommand + " WARNING: Cannot SET readonly varabile " + var);
				return;
			}
			eq = UniVar.getValue(subVars(eq) + "") + "";
			if (eq.length()==0) eq="-";
			vars.put(var, eq);
		}
		//REM
		else if(workingCommand.toUpperCase().startsWith("REM")) {
			//DO NOTHING
		}
		//LOG
		else if(workingCommand.toUpperCase().startsWith("LOG ")) {
			String eq = workingCommand.substring("LOG".length()).trim();
			eq = UniVar.getValue(subVars(eq) + "") + "";
			log(eq);
		}
		else if(workingCommand.toUpperCase().startsWith("RETURN")) {
			String eq = workingCommand.substring("RETURN".length()).trim();
			eq = UniVar.getValue(subVars(eq) + "") + "";
			String prev = (String)vars.get("_jr");
			//if(Main.DEBUG)System.out.println("DEBUG: RETURN found");
			//if(Main.DEBUG)System.out.println("DEBUG:  old jr=" + prev);
			String next = "";
			if(prev.indexOf(";") >= 0) {
				next = prev.substring(prev.lastIndexOf(";") + 1);
				prev = prev.substring(0, prev.lastIndexOf(";"));
			}
			vars.put("_jr", prev);
			//if(Main.DEBUG)System.out.println("DEBUG:  next=" + next);
			//if(Main.DEBUG)System.out.println("DEBUG:  jr=" + prev);
			gotoLine = next;
		}
		else if(workingCommand.toUpperCase().startsWith("GOSUB ")) {
			String eq = workingCommand.substring("GOSUB".length()).trim();
			eq = UniVar.getValue(subVars(eq) + "") + "";
			while(eq.indexOf("\"") >= 0) {
				eq = eq.substring(0, eq.indexOf("\"")) + eq.substring(eq.indexOf("\"") + 1);
			}
			//if(Main.DEBUG)System.out.println("DEBUG: GOSUB found");
			String line = "";
			line = eq + ":";
			if(lines.indexOf(line) <= 0) {
				log("In line: " + workingCommand + " WARNING: Undefined line refrence " + eq);
		log("Program Terminated: " );
finishCommand="";finishTime=-1;
				return;
			}
			//if(Main.DEBUG)System.out.println("DEBUG:  label to gosub to " + line);
			gotoLine = lines.substring(0, lines.indexOf(line));
			if(gotoLine.indexOf(";") >= 0) {
				gotoLine = gotoLine.substring(0, gotoLine.lastIndexOf(";") + 1);
			}
			else {
				gotoLine = "";
			}
			gotoLine = lines.substring(gotoLine.length());
			if(gotoLine.indexOf(";") > 0) {
				gotoLine = gotoLine.substring(0, gotoLine.indexOf(";"));
			}
			//if(Main.DEBUG)System.out.println("DEBUG finishCommand=" + finishCommand);
			if(Main.DEBUG)System.out.println("DEBUG gotoLine=" + gotoLine);
			String next = lines.substring(lines.indexOf(finishCommand) + finishCommand.length()).trim();
			if(next.indexOf(";") < 0) {
				next = "";
			}
			next = next.substring(next.indexOf(";") + 1).trim();
			if(next.indexOf(";") >= 0) {
				next = next.substring(0, next.indexOf(";")).trim();
			}
			String prev = (String)vars.get("_jr");
			prev += ";" + next;
			vars.put("_jr", prev);
		}
		//GOTO
		else if(workingCommand.toUpperCase().startsWith("GOTO ")) {
			String eq = workingCommand.substring("GOTO".length()).trim();
			eq = UniVar.getValue(subVars(eq) + "") + "";
			while(eq.indexOf("\"") >= 0) {
				eq = eq.substring(0, eq.indexOf("\"")) + eq.substring(eq.indexOf("\"") + 1);
			}
			String line = "";
			line = eq + ":";
			if(lines.indexOf(line) <= 0) {
				log("In line: " + workingCommand + " WARNING: Undefined line refrence " + eq);
				log("Program Terminated: " );
				finishCommand="";
				finishTime=-1;
				return;
			}
			gotoLine = lines.substring(0, lines.indexOf(line));
			if(gotoLine.indexOf(";") >= 0) {
				gotoLine = gotoLine.substring(0, gotoLine.lastIndexOf(";") + 1);
			}
			else {
				gotoLine = "";
			}
			gotoLine = lines.substring(gotoLine.length());
			if(gotoLine.indexOf(";") > 0) {
				gotoLine = gotoLine.substring(0, gotoLine.indexOf(";"));
			}
		}
		//IF
		else if(workingCommand.toUpperCase().startsWith("IF ")) {
			if(workingCommand.toUpperCase().indexOf(" THEN ") < 0) {
				log("In line: " + workingCommand + " WARNING: IF without THEN");
				return;
			}
			String test = workingCommand.substring("IF ".length(), workingCommand.toUpperCase().indexOf(" THEN ")).trim();
			test = UniVar.getValue(subVars(test) + "") + "";
			String case1 = workingCommand.substring(workingCommand.toUpperCase().indexOf(" THEN ") + 6).trim();
			//if(Main.DEBUG)System.out.println("if FOUND");
			String case2 = "NOOP";
			if(case1.toUpperCase().indexOf(" ELSE ") > 0) {
				case2 = case1.substring(case1.toUpperCase().indexOf(" ELSE ") + 6).trim();
				case1 = case1.substring(0, case1.toUpperCase().indexOf(" ELSE ")).trim();
			}
			if (Main.DEBUG) System.out.println("DEBUG: case1="+case1);
			if (Main.DEBUG) System.out.println("DEBUG: case2="+case2);
			//if (Main.DEBUG) System.out.println("test="+test);
			test = UniVar.getValue(subVars(test) + "") + "";
			if( !test.equals("0")) {
				//if (Main.DEBUG) System.out.println("case1 chosen=");
				subCommand = case1;
			}
			else {
				//if (Main.DEBUG) System.out.println("case2 chosen");
				subCommand = case2;
			}
			if (Main.DEBUG) System.out.println("DEBUG: subCommand="+subCommand);
			executeCommand();
		}
		//SCAN
		else if(workingCommand.toUpperCase().startsWith("SCAN ") || workingCommand.toUpperCase().startsWith("SCANLOG ")) {
			boolean dolog = workingCommand.toUpperCase().startsWith("SCANLOG ");
			String eq = workingCommand.substring(workingCommand.indexOf(" ")).trim();
			eq = UniVar.getValue(subVars(eq) + "") + "";
			if(Main.DEBUG)System.out.println("SCAN FOUND");
			while(eq.indexOf("\"") >= 0) {
				eq = eq.substring(0, eq.indexOf("\"")) + eq.substring(eq.indexOf("\"") + 1);
			}
			//SCAN <eq>        : scan terrain,resorces,temp,objects,targets,facilities,location
			String n[] = Stuff.getTokens(eq, " \t");
			//SCAN TEMP
			if(n[0].toUpperCase().startsWith("TEM")) {
				Location loc = this.loc;
				if(n.length == 2)loc = Location.parse(n[1]);
				if(loc == null || !loc.valid())loc = this.loc.reference(n[1]);
				if(loc == null || !loc.valid()) {
					log("In line: " + workingCommand + " WARNING: invalid location " + n[1] + " Scan canceled");
					return;
				}
				if( !Location.adjacentTo(loc, this.loc)) {
					log("In line: " + workingCommand + " WARNING: Nonadjacent location " + eq + " Scan canceled");
					return;
				}
				if( !hasTech("T39")) {
					log("In line: " + workingCommand + " WARNING: No ThermalImage technology cannot do temp scan.");
					return;
				}
				if(dolog)log("Scan Temp: " + loc + " = " + Universe.getTempByLocation(loc));
				vars.put("arg1", "" + Universe.getTempByLocation(loc));
			}
			else if(n[0].toUpperCase().startsWith("SYS")) {
				if (!hasTech("T95")) {
					log("In line: " + workingCommand + " WARNING: No telescope technolgy availalbe.  Scan failed.");
					return;
				}
				if(!loc.isOrbit()) {
					log("In line: " + workingCommand + " WARNING: Must be in orbit to scan system.  " + loc +" is not.");
					return;
				}
				String sys=loc.toString();
				sys=sys.substring(0,sys.indexOf("."));
				log(Universe.getSystemScan(sys));
			}
			else if(n[0].toUpperCase().startsWith("SEC")) {
				int sec = 0;
				if(n.length == 2) {
					try {
						sec = (int)Stuff.parseDouble(n[1]);
					}
					catch(NumberFormatException NFE) {
						log("In line: " + workingCommand + " WARNING: Bad sector " + eq + "");
						return;
					}
				}
				if(sec < 0 || sec > 3) {
					log("In line: " + workingCommand + " WARNING: Bad sector " + eq + "");
					return;
				}
				Body b = Universe.getBodyByLocation(loc);
				if(b == null || !loc.isOrbit()) {
					log("In line: " + workingCommand + " WARNING: Cannot scan sector from " + loc);
					return;
				}
				log(b.getSectorScan(this, sec));
			}
			//SCAN SURFACE
			else if(n[0].toUpperCase().startsWith("SUR")) {
				//terrain  "M" "." "p" "v"
				Location loc = this.loc;
				if(n.length == 2)loc = Location.parse(n[1]);
				if(loc == null || !loc.valid())loc = this.loc.reference(n[1]);
				if(loc == null || !loc.valid()) {
					log("In line: " + workingCommand + " WARNING: invalid location " + n[1] + " Scan canceled");
					return;
				}
				if( !Location.adjacentTo(loc, this.loc)) {
					log("In line: " + workingCommand + " WARNING: Nonadjacent location " + eq + " Scan canceled");
					return;
				}
				if( !hasTech("T38")) {
					log("In line: " + workingCommand + " WARNING: No LowLightVideo technology cannot do surface scan.");
					return;
				}
				if(dolog)log("Scan Surface: " + loc + " = " + Universe.getSurfaceByLocation(loc));
				vars.put("arg1", Universe.getSurfaceByLocation(loc));
			}
			//SCAN RESOURCES
			else if(n[0].toUpperCase().startsWith("RES")) {
				//4.goldore,3.ironore
				Location loc = this.loc;
				if(n.length == 2)loc = Location.parse(n[1]);
				if(loc == null || !loc.valid())loc = this.loc.reference(n[1]);
				if(loc == null || !loc.valid()) {
					log("In line: " + workingCommand + " WARNING: invalid location " + n[1] + " Scan canceled");
					return;
				}
				if( !Location.adjacentTo(loc, this.loc)) {
					log("In line: " + workingCommand + " WARNING: Nonadjacent location " + eq + " Scan canceled");
					return;
				}
				if( !hasTech("T40")) {
					log("In line: " + workingCommand + " WARNING: No Spectroscope technology cannot do resource scan.");
					return;
				}
				String res = Universe.getResourcesByLocation(loc);
				if(dolog)log("Scan Resources: " + loc + " = " + res);
				vars.put("arg1", res);
			}
			//SCAN STOCKPILES
			else if(n[0].toUpperCase().startsWith("STO")) {
				//XFG.SP1,XXX.SP123,BPS.SP352
				Location loc = this.loc;
				if(n.length == 2)loc = Location.parse(n[1]);
				if(loc == null || !loc.valid())loc = this.loc.reference(n[1]);
				if(loc == null || !loc.valid()) {
					log("In line: " + workingCommand + " WARNING: invalid location " + n[1] + " Scan canceled");
					return;
				}
				if( !Location.adjacentTo(loc, this.loc)) {
					log("In line: " + workingCommand + " WARNING: Nonadjacent location " + eq + " Scan canceled");
					return;
				}
				String ans = "";
				Vector v = Universe.getNFObjectsByLocation(loc);
				for(int a = 0; a < v.size(); a++) {
					if(v.elementAt(a)instanceof StockPile) {
						if(ans.length() > 0) {
							ans += SCANSEP;
						}
						ans += ((NFObject)v.elementAt(a)).toScanString();
					}
				}
				if( !hasTech("T37")) {
					log("In line: " + workingCommand + " WARNING: No Radar technology cannot do scan.");
					return;
				}
				if(ans.length() == 0)ans = "-";
				if(dolog)log("Scan Stockpiles: " + loc + " = " + ans);
				vars.put("arg1", ans);
			}
			//SCAN ROBOTS
			else if(n[0].toUpperCase().startsWith("ROB")) {
				//XFG.A32,BPS.A72
				if( !hasTech("T37")) {
					log("In line: " + workingCommand + " WARNING: No Radar technology cannot do scan.");
					return;
				}
				Location loc = this.loc;
				if(n.length == 2)loc = Location.parse(n[1]);
				if(loc == null || !loc.valid())loc = this.loc.reference(n[1]);
				if(loc == null || !loc.valid()) {
					log("In line: " + workingCommand + " WARNING: invalid location " + n[1] + " Scan canceled");
					return;
				}
				if( !Location.adjacentTo(loc, this.loc)) {
					log("In line: " + workingCommand + " WARNING: Nonadjacent location " + eq + " Scan canceled");
					return;
				}
				String ans = "";
				Vector v = Universe.getNFObjectsByLocation(loc);
				for(int a = 0; a < v.size(); a++) {
					if(v.elementAt(a)instanceof Active) {
						Active A = (Active)v.elementAt(a);
						if (A.hasTech("T94")) continue;//cloak
						if(A.isRobot() && A != this) {
							if(ans.length() > 0)ans += SCANSEP;
							ans += ((NFObject)v.elementAt(a)).toScanString();
						}
					}
				}
				if(ans.length() == 0)ans = "-";
				if(dolog)log("Scan Robots: " + loc + " = " + ans);
				vars.put("arg1", ans);
			}
			//SCAN SHIPS
			else if(n[0].toUpperCase().startsWith("SHI")) {
				//XFG.A73,BPS.A213
				Location loc = this.loc;
				if(n.length == 2)loc = Location.parse(n[1]);
				if(loc == null || !loc.valid())loc = this.loc.reference(n[1]);
				if(loc == null || !loc.valid()) {
					log("In line: " + workingCommand + " WARNING: invalid location " + n[1] + " Scan canceled");
					return;
				}
				if( !Location.adjacentTo(loc, this.loc)) {
					log("In line: " + workingCommand + " WARNING: Nonadjacent location " + eq + " Scan canceled");
					return;
				}
				String ans = "";
				Vector v = Universe.getNFObjectsByLocation(loc);
				for(int a = 0; a < v.size(); a++) {
					if(v.elementAt(a)instanceof Active && ((Active)(v.elementAt(a))).isShip() && v.elementAt(a) != this) {
						Active A=(Active)v.elementAt(a);
						if (A.hasTech("T94")) continue;//cloak
						if(ans.length() > 0)ans += SCANSEP;
						ans += ((NFObject)v.elementAt(a)).toScanString();
					}
				}
				if( !hasTech("T37")) {
					log("In line: " + workingCommand + " WARNING: No Radar technology cannot do scan.");
					return;
				}
				if(ans.length() == 0)ans = "-";
				if(dolog)log("Scan ships: " + loc + " = " + ans);
				vars.put("arg1", ans);
			}
			//SCAN PREFABS
			else if(n[0].toUpperCase().startsWith("PRE")) {
				//XFG_PF173,BPS_PF23
				Location loc = this.loc;
				if(n.length == 2)loc = Location.parse(n[1]);
				if(loc == null || !loc.valid())loc = this.loc.reference(n[1]);
				if(loc == null || !loc.valid()) {
					log("In line: " + workingCommand + " WARNING: invalid location " + n[1] + " Scan canceled");
					return;
				}
				if( !Location.adjacentTo(loc, this.loc)) {
					log("In line: " + workingCommand + " WARNING: Nonadjacent location " + eq + " Scan canceled");
					return;
				}
				String ans = "";
				Vector v = Universe.getNFObjectsByLocation(loc);
				for(int a = 0; a < v.size(); a++) {
					if(v.elementAt(a)instanceof Prefab) {
						if(ans.length() > 0)ans += SCANSEP;
						ans += ((NFObject)v.elementAt(a)).toScanString();
					}
				}
				if( !hasTech("T37")) {
					log("In line: " + workingCommand + " WARNING: No Radar technology cannot do scan.");
					return;
				}
				if(ans.length() == 0)ans = "-";
				if(dolog)log("Scan Prefabs: " + loc + " = " + ans);
				vars.put("arg1", ans);
			}
			//SCAN FACILITIES
			else if(n[0].toUpperCase().startsWith("FAC")) {
				//XFG_F23,BPS_F73,BPS_F11
				Location loc = this.loc;
				if(n.length == 2)loc = Location.parse(n[1]);
				if(loc == null || !loc.valid())loc = this.loc.reference(n[1]);
				if(loc == null || !loc.valid()) {
					log("In line: " + workingCommand + " WARNING: invalid location " + n[1] + " Scan canceled");
					return;
				}
				if( !Location.adjacentTo(loc, this.loc)) {
					log("In line: " + workingCommand + " WARNING: Nonadjacent location " + eq + " Scan canceled");
					return;
				}
				String ans = "";
				Vector v = Universe.getNFObjectsByLocation(loc);
				for(int a = 0; a < v.size(); a++) {
					if(v.elementAt(a)instanceof Facility) {
						Facility A=(Facility)v.elementAt(a);
						if (A.hasTech("T94")) continue;
						if(ans.length() > 0)ans += SCANSEP;
						ans += ((NFObject)v.elementAt(a)).toScanString();
					}
				}
				if( !hasTech("T37")) {
					log("In line: " + workingCommand + " WARNING: No Radar technology cannot do scan.");
					return;
				}
				if(ans.length() == 0)ans = "-";
				if(dolog)log("Scan Facilities: " + loc + " = " + ans);
				vars.put("arg1", ans);
			}
			//SCAN OBJECTS
			else if(n[0].toUpperCase().startsWith("OBJ")) {
				Location loc = this.loc;
				if(n.length == 2)loc = Location.parse(n[1]);
				if(loc == null || !loc.valid())loc = this.loc.reference(n[1]);
				if(loc == null || !loc.valid()) {
					log("In line: " + workingCommand + " WARNING: invalid location " + n[1] + " Scan canceled");
					return;
				}
				if( !Location.adjacentTo(loc, this.loc)) {
					log("In line: " + workingCommand + " WARNING: Nonadjacent location " + eq + " Scan canceled");
					return;
				}
				String ans = "";
				Vector v = Universe.getNFObjectsByLocation(loc);
				for(int a = 0; a < v.size(); a++) {
					if(v.elementAt(a)instanceof NFObject && v.elementAt(a) != this) {
						if (v.elementAt(a) instanceof Active) {
							Active A=(Active)v.elementAt(a);
							if (A.hasTech("T94")) continue;
						}
						if (v.elementAt(a) instanceof Facility) {
							Facility A=(Facility)v.elementAt(a);
							if (A.hasTech("T94")) continue;
						}
						if(ans.length() > 0)ans += SCANSEP;
						ans += ((NFObject)v.elementAt(a)).toScanString();
					}
				}
				if( !hasTech("T37")) {
					log("In line: " + workingCommand + " WARNING: No Radar technology cannot do scan.");
					return;
				}
				if(ans.length() == 0)ans = "-";
				if(dolog)log("Scan Objects: " + loc + " = " + ans);
				vars.put("arg1", ans);
			}
			//SCAN TARGETS
			else if(n[0].toUpperCase().startsWith("TAR")) {
				Location loc = this.loc;
				if(n.length == 2)loc = Location.parse(n[1]);
				if(loc == null || !loc.valid())loc = this.loc.reference(n[1]);
				if(loc == null || !loc.valid()) {
					log("In line: " + workingCommand + " WARNING: invalid location " + n[1] + " Scan canceled");
					return;
				}
				if( !Location.adjacentTo(loc, this.loc)) {
					log("In line: " + workingCommand + " WARNING: Nonadjacent location " + eq + " Scan canceled");
					return;
				}
				String ans = "";
				Vector v = Universe.getNFObjectsByLocation(loc);
				for(int a = 0; a < v.size(); a++) {
					if((v.elementAt(a)instanceof Facility || v.elementAt(a)instanceof Active) && v.elementAt(a) != this && !((NFObject)(v.elementAt(a))).getCorpTick().equalsIgnoreCase(corpTick)) {
						if (v.elementAt(a) instanceof Active) {
							Active A=(Active)v.elementAt(a);
							if (A.hasTech("T94")) continue;
						}
						if (v.elementAt(a) instanceof Facility) {
							Facility A=(Facility)v.elementAt(a);
							if (A.hasTech("T94")) continue;
						}
						if(ans.length() > 0)ans += SCANSEP;
						ans += ((NFObject)v.elementAt(a)).toScanString();
					}
				}
				if( !hasTech("T37")) {
					log("In line: " + workingCommand + " WARNING: No Radar technology cannot do scan.");
					return;
				}
				if(ans.length() == 0)ans = "-";
				if(dolog)log("Scan Targets: " + loc + " = " + ans);
				vars.put("arg1", ans);
			}
			else {
				log("In line: " + workingCommand + " WARNING: Bad Scan command " + n[0] + ". Use SYS,SEC,TEM,SUR,RES,STO,ROB,SHI,FAC,OBJ,TAR or PRE");
			}
		}
		//MOVE
		else if(workingCommand.toUpperCase().startsWith("MOVE ")) {
			String eq = workingCommand.substring("MOVE".length()).trim();
			eq = UniVar.getValue(subVars(eq) + "") + "";
			Location l = Location.parse(eq);
			if(l == null) {
				l = loc.reference(eq);//N,S,E,W,U,D,ESCAPE,LAUNCH,REENTRY,LAND, EXIT
			}
			if(l == null) {
				log("In line: " + workingCommand + " WARNING: no such location " + eq + " Move canceled");
				return;
			}
			if(l.equals(getInsideLoc())) {
				log("In line: " + workingCommand + " WARNING: cannot move inside self.  Move canceled");
				return;
			}
			if( !Location.adjacentTo(loc, l)) {
				log("In line: " + workingCommand + " WARNING: Nonadjacent location " + eq + " Move canceled");
				return;
			}
			if( !l.valid()) {
				log("In line: " + workingCommand + " WARNING: Target locaiton not valid " + eq + " Move canceled");
				return;
			}
			int moveType = loc.getMoveType(l);
			if(moveType == Location.LOAD || moveType == Location.UNLOAD) {
				int techlevel = 0;
				boolean atm = false;
				Body b = Universe.getBodyByLocation(l);
				if(b != null)atm = b.hasAtmosphere();
				if(hasTech("T8") && (loc.isOrbit() || l.isOrbit()))techlevel = 1;
				if(hasTech("T1") && !loc.isOrbit())techlevel = 1;
				if(hasTech("T2") && !loc.isOrbit())techlevel = 1;
				if(hasTech("T3") && !loc.isOrbit())techlevel = 1;
				if(hasTech("T4") && !loc.isOrbit())techlevel = 1;
				if(hasTech("T7") && !loc.isOrbit())techlevel = 1;
				if(techlevel < 1) {
					if(loc.isOrbit())log("In line: " + workingCommand + " WARNING: Cannot move no EVAJets technology available.  Move canceled");
					else log("In line: " + workingCommand + " WARNING: Cannot move no surface movement technology avaialble .  Move canceled");
					return;
				}
				if(loc.isInside()) {
					NFObject nfo = Universe.getNFObjectById(loc.getInsideWhat());
					NFObject nfo2 = Universe.getNFObjectById(l.getInsideWhat());
					if(nfo == null) {
						log("SYSTEM ERROR:In line: " + workingCommand + " WARNING: Bad move cannot find source inside location");
						System.out.println("SYSTEM ERROR: line: " + workingCommand + " WARNING: Bad move cannot find source inside location");
						return;
					}
					if(nfo instanceof Active) {
						Active act = (Active)nfo;
						if (act.hasTech("T57") && !nfo.getCorpTick().equalsIgnoreCase(corpTick) && !nfo2.getLocation().equalsIgnoreCase(loc)){
							log("In line: " + workingCommand + " WARNING: Exit blocked by security.");
							return;
						}
					}
					else if (nfo instanceof Facility) {
						Facility fac=(Facility) nfo;
						if (fac.hasTech("T57") && !nfo.getCorpTick().equalsIgnoreCase(corpTick) && !nfo2.getLocation().equalsIgnoreCase(loc)){
							log("In line: " + workingCommand + " WARNING: Exit blocked by security.");
							return;
						}
//security check here
					}
				}
				if(l.isInside()) {
					NFObject nfo = Universe.getNFObjectById(l.getInsideWhat());
					NFObject nfo2 = Universe.getNFObjectById(loc.getInsideWhat());
					if(nfo == null) {
						log("In line: " + workingCommand + " WARNING: Bad move cannot find target inside location");
						System.out.println("MyError line: " + workingCommand + " WARNING: Bad move cannot find source inside location");
						return;
					}
					if(nfo instanceof Active) {
						Active act = (Active)nfo;
						if (act.hasTech("T57") && !nfo.getCorpTick().equalsIgnoreCase(corpTick)){
							log("In line: " + workingCommand + " WARNING: Entry blocked by security.");
							return;
						}
						if(act == this) {
							log("In line: " + workingCommand + " WARNING: Bad move cannot move inside self");
							return;
						}
						if(act.getCargoCapacity() < getMass() && !act.contains(this)) {
							log("In line: " + workingCommand + " WARNING: No room to enter active " + eq + " Move canceled");
							return;
						}
					}
					else if (nfo instanceof Facility) {
						Facility fac=(Facility) nfo;
						if (fac.hasTech("T57") && !fac.getCorpTick().equalsIgnoreCase(corpTick)){
							log("In line: " + workingCommand + " WARNING: Entry blocked by security.");
							return;
						}
						if(fac.hasTech("T87")) {
							Random r = new Random(fac.getId().hashCode());
							int sol=(int)(Universe.getMaxSolar()*r.nextDouble()+1);//random solar system
							int plan=(int)(Universe.getMaxPlanet(sol)*r.nextDouble()+1);//random planet
							Location worm=Location.parse(sol+"."+plan);
							setLocation(worm);
							return;
						}
						if(fac.getCapacity() < getMass() && !fac.contains(this)) {
if (Main.DEBUG) System.out.println("MYDEBUG: entrance blocked.  Facility full with "+ fac.getCapacity()+" room and "+getMass()+" added");
							log("In line: " + workingCommand + " WARNING: No room to enter facility " + eq + " Move canceled");
							
							return;
						}
//security check here
					}
				}
				setLocation(l);
			}
			else if(moveType == Location.SURFACE) {
				String sur = Universe.getSurfaceByLocation(l);
				int techlevel = 0;
				boolean atm = false;
				Body b = Universe.getBodyByLocation(l);
				if(b != null)atm = b.hasAtmosphere();
				if(hasTech("T1"))techlevel = Math.max(techlevel, 1);
				if(hasTech("T2"))techlevel = Math.max(techlevel, 2);
				if(hasTech("T5") && atm)techlevel = Math.max(techlevel, 3);
				if(hasTech("T3"))techlevel = Math.max(techlevel, 3);
				if(hasTech("T4"))techlevel = Math.max(techlevel, 4);
				if(hasTech("T6") && atm)techlevel = Math.max(techlevel, 5);
				if(hasTech("T7"))techlevel = Math.max(techlevel, 5);
				if(techlevel < 1) {
					log("In line: " + workingCommand + " WARNING: Cannot move no Track technology available");
					return;
				}
				if(sur.equals("b") && techlevel < 2 && !Universe.locationContainsTechnology(l,"T58")) {
					log("In line: " + workingCommand + " WARNING: Cannot move no Track or HoverFit technology available");
					return;
				}
				if(sur.equals("B") && techlevel < 3 && !Universe.locationContainsTechnology(l,"T58")) {
					log("In line: " + workingCommand + " WARNING: Cannot move no Hexapod technology available");
					return;
				}
				if(sur.equals("m") && techlevel < 4 && !Universe.locationContainsTechnology(l,"T58")) {
					log("In line: " + workingCommand + " WARNING: Cannot move no Bipod technology available");
					return;
				}
				if(sur.equals("M") && techlevel < 5 && !Universe.locationContainsTechnology(l,"T58")) {
					if(atm)log("In line: " + workingCommand + " WARNING: Cannot move no PropulsionJets or Wings technology available");
					else log("In line: " + workingCommand + " WARNING: Cannot move no PropulsionJets technology available");
					return;
				}
				setLocation(l);
			}
			else if(moveType == Location.SUBTER) {
				if (!hasTech("T9") && !Universe.locationContainsTechnology(l,"T59")) {
					log("In line: " + workingCommand + " WARNING: Cannot move subterranin no BoringDrill technolgy available");
					return;
				}
				setLocation(l);
			}
			else if(moveType == Location.REENTRY) {
				boolean atm = false;
				Body b = Universe.getBodyByLocation(l);
				if(b != null)atm = b.hasAtmosphere();
				int techlevel = 0;
				if(hasTech("T8"))techlevel = 1;
				if(hasTech("T15"))techlevel = 1;
				if(hasTech("T10") && atm)techlevel = 2;
				else if(hasTech("T10") && !atm)techlevel = 1;
				if(hasTech("T11"))techlevel = 2;
				if(hasTech("T12"))techlevel = 2;
				if(hasTech("T13"))techlevel = 2;
				if(hasTech("T14"))techlevel = 2;
				if(techlevel < 1) {
					log("In line: " + workingCommand + " WARNING: Cannot move from orbit no Reentry technolgy available");
					return;
				}
				if(techlevel < 2) {
					if(l.isFacility()) {
						NFObject nfo = Universe.getNFObjectById(l.getFac());
						if(nfo instanceof Facility) {
							l = l.getOutsideFacilityLocation();
							setLocation(l);
							Facility ff = (Facility)nfo;
							if(!ff.hasTech("T77")){
							//no log necessary robot will be destroyed anyway
								ff.destroy();
							}
						}
					}
					destroy();
					//parachute with no atmosphere
				}
				//check surface landing if facility LANDING FAC
				NFObject faci=Universe.getNFObjectById(l.getInsideWhat());
				if(l.isFacility() && Universe.locationHasTechnology(l, "T57") && faci!=null && faci instanceof Facility && !faci.getCorpTick().equals(corpTick)) {
					log("In line: " + workingCommand + " WARNING: Cannot land.  Entry blocked by security.  Landing nearby.");
					l = l.getOutsideFacilityLocation();
				}
				if(l.isFacility() && !Universe.locationHasTechnology(l, "T77")) {
					log("In line: " + workingCommand + " WARNING: Cannot land.  Facility doesn't have LandingZone technology. Landing nearby.");
					l = l.getOutsideFacilityLocation();
				}
				if(faci!=null && faci instanceof Facility && ((Facility)faci).getCapacity() < getMass()) {
//System.out.println("MYDEBUG: reentry entrance blocked.  Facility full");
					log("In line: " + workingCommand + " WARNING: No room to enter facility " + eq + ".  Landing nearby.");
					l = l.getOutsideFacilityLocation();
				}
				setLocation(l);
				if(!l.isFacility()) {
					String su=Universe.getSurfaceByLocation(l);
					if (su.equalsIgnoreCase("W") && Math.random()>.95) landingDamage();
					if (su.equals("P") && Math.random()>.90) landingDamage();
					if (su.equals("p") && Math.random()>.85) landingDamage();
					if (su.equals("b") && Math.random()>.75) landingDamage();
					if (su.equals("B") && Math.random()>.5) landingDamage();
					if (su.equals("m") && Math.random()>.3) landingDamage();
					if (su.equals("M") && Math.random()>.1) landingDamage();
				}
			}
			else if(moveType == Location.ESCAPE) {
				if( !hasTech("T12") && !hasTech("T13") && !hasTech("T14")) {
					log("In line: " + workingCommand + " WARNING: Cannot escape.  No Rocket technolgy available");
					return;
				}
				if(loc.isFacility() && !Universe.locationHasTechnology(loc, "T77")) {
					log("In line: " + workingCommand + " WARNING: Cannot launch.  Facility doesn't have LandingZone technology.");
					return;
				}
				NFObject faci=Universe.getNFObjectById(l.getInsideWhat());
				if(loc.isFacility() && !Universe.locationHasTechnology(loc, "T57") && faci!=null && !faci.getCorpTick().equalsIgnoreCase(corpTick)) {
					log("In line: " + workingCommand + " WARNING: Cannot launch.  Exit blocked by security.");
					return;
				}
				setLocation(l);
			}
			else if(moveType == Location.INTERPLANET) {
				if( !hasTech("T12") && !hasTech("T13") && !hasTech("T14") && !hasTech("T15")) {
					log("In line: " + workingCommand + " WARNING: Cannot move intraplanetary no Warp technolgy available");
					return;
				}
				setLocation(l);
			}
			else if(moveType == Location.INTERSOLAR) {
				if( !hasTech("T87")) {
					log("In line: " + workingCommand + " WARNING: Cannot move intrasolar no Warp technolgy available");
					return;
				}
				setLocation(l);
			}
		}
		//LOAD
		else if(workingCommand.toUpperCase().startsWith("LOAD ")) {
			getMass();//clean any cargo errors
			String eq = workingCommand.substring("LOAD".length()).trim();
			eq = UniVar.getValue(subVars(eq) + "") + "";
			String n[] = Stuff.getTokens(eq, " \"\t");
			if(n.length > 3 || n.length < 1) {
				log("In line: " + workingCommand + " WARNING:  bad format for load command");
				return;
			}
			if( !hasTech("T35") && !hasTech("T36") && !hasTech("T88")) {
				log("In line: " + workingCommand + " WARNING: Cannot load no Arm technolgy available");
				return;
			}
			Location loadloc=loc;
			if (n.length>1) {
				loadloc = Location.parse(n[0]);
				if(loadloc == null) {
					loadloc = loc.reference(n[0]);//N,S,E,W, U,D, ESC, LAU, REE,LAN, EXIT
				}
				if (loadloc!=null) {
					int moveType = loc.getMoveType(loadloc);
					if(moveType == Location.INTERPLANET || moveType == Location.ESCAPE || moveType == Location.REENTRY) {
						log("In line: " + workingCommand + " WARNING: Cannot remote load between orbits or surface to orbit. "+n[0]);
						return;
					}
//SECURITY HERE
					if (loadloc.isInside()) {
						NFObject nfoo=Universe.getNFObjectById(loadloc.getInsideWhat());
						if (nfoo instanceof Active) {
							Active aaa=(Active)nfoo;
							if (aaa.hasTech("T57") && !aaa.getCorpTick().equalsIgnoreCase(corpTick)) {
								log("In line: " + workingCommand + " WARNING: Cannot remote load.  Blocked by secuirty at "+aaa.getId());
								return;
							}
						}
						if (nfoo instanceof Facility) {
							Facility aaa=(Facility)nfoo;
							if (aaa.hasTech("T57") && !aaa.getCorpTick().equalsIgnoreCase(corpTick)) {
								log("In line: " + workingCommand + " WARNING: Cannot remote load.  Blocked by secuirty at "+aaa.getId());
								return;
							}
						}
					}
					String nn[];
					if (n.length==3) {
						nn=new String[2];
						nn[1]=n[2];
					}
					else nn=new String[1];
					nn[0]=n[1];
					n=nn;
				}
				else loadloc=loc;
			}
			int cc = getCargoCapacity();
			if (cc<=0) {
				log("In line: " + workingCommand + " WARNING: unable to load "+n[0]+".  Cargo bay full.");
				return;
				
			}
			NFObject nfo = Universe.getNFObjectById(n[0]);
			if(nfo != null) {
				if( !nfo.getLocation().equals(loadloc)) {
					log("In line: " + workingCommand + " WARNING: unable to load " + n[0] + " not at location");
					return;
				}
				if( nfo == this) {
					log("In line: " + workingCommand + " WARNING: cannot load self");
					return;
				}
				if( nfo instanceof Facility) {
					log("In line: " + workingCommand + " WARNING: cannot load facilities");
					return;
				}
//TODO if stockpile then load partial stockpile
				if (nfo.getMass()>cc) {
					log("In line: " + workingCommand + " WARNING:  Insufficent cargospace to load "+n[0]);
					return;
				}
			}
			if(nfo == null) {
				//find material in stockpile
				try {
					String mat = Market.getMarketName(n[0]);
					if(mat == null) {
						log("In line: " + workingCommand + " WARNING: unknown material " + n[0]);
						return;
					}
					int num=cc;
					try {
						if(n.length == 2) 
							num =(int) Stuff.parseDouble(n[1]);
						if(num > cc) {
							log("In line: " + workingCommand + " WARNING: unable to load " + num + " " + mat + " not enough room will load " + cc);
							num = cc;
						}
					} catch (NumberFormatException nfe) {
						log("In line: " + workingCommand + " WARNING: bad number in command" + n[1] + ".");
						return;
					}
					int ava = Universe.availableMaterial(loadloc, mat);
					if(ava <= 0) {
						log("In line: " + workingCommand + " WARNING: unable to load " + mat + ".  None available");
						return;
					}
					if(ava < num ) {
						if (n.length==2) 	
							log("In line: " + workingCommand + " WARNING: unable to load " + num + " " + mat + " will load " + ava);
						num = ava;
					}
					nfo = new StockPile(corpTick, mat, num, Location.parse("INSIDE." + getId()));
					Universe.takeMaterial(corpTick, loadloc, mat, num);
					Universe.add(nfo);
				}
				catch(NumberFormatException NFE) {
					log("In line: " + workingCommand + " WARNING: bad number in load " + n[1]);
				}
			}
			Location nl = Location.parse("INSIDE." + getId());
			nfo.setLocation(nl);
			nfo.setCorpTick(corpTick);
		}
		//UNLOAD
		else if(workingCommand.toUpperCase().startsWith("UNLOAD ")) {
			getMass();//clean any cargo errors
			String eq = workingCommand.substring("UNLOAD".length()).trim();
			eq = UniVar.getValue(subVars(eq) + "") + "";
			String n[] = Stuff.getTokens(eq, " \"\t");
			NFObject nfo = Universe.getNFObjectById(n[0]);
			//find NFOID
			if(n.length != 2 && n.length != 1) {
				log("In line: " + workingCommand + " WARNING:  bad format for Unload command");
				return;
			}
			if( !hasTech("T35") && !hasTech("T36") && !hasTech("T88")) {
				log("In line: " + workingCommand + " WARNING: Cannot unload no Arm technolgy available");
				return;
			}
			if(nfo != null) {
				if( !nfo.getLocation().equals(Location.parse("INSIDE." + getId()))) {
					log("In line: " + workingCommand + " WARNING: unable to unload " + n[0] + " not carried");
					return;
				}
				nfo.setLocation(loc);
				return;
			}
			if(nfo == null) {
				//find material in stockpile
				try {
					String mat = Market.getMarketName(n[0]);
					if(mat == null) {
						log("In line: " + workingCommand + " WARNING: unknown material " + n[0]);
						return;
					}
					int num = - 1;
					try {
						if(n.length == 2) num = (int)Stuff.parseDouble(n[1]);
					} catch (NumberFormatException nfe) {
						log("In line: " + workingCommand + " WARNING: bad number in command "+n[1]);
						return;
					}
					int ava = Universe.availableMaterial(Location.parse("INSIDE." + getId()), mat);
					if(num == - 1)num = ava;
					if(ava == 0) {
						log("In line: " + workingCommand + " WARNING: cargo not carried to unload");
						return;
					}
					if(ava < num) {
						log("In line: " + workingCommand + " WARNING: unable to unload " + num + " " + mat + " will unload " + ava);
						num = ava;
					}
					nfo = new StockPile(corpTick, mat, num, loc);
					Universe.takeMaterial(corpTick, Location.parse("INSIDE." + getId()), mat, num);
					Universe.add(nfo);
				}
				catch(NumberFormatException NFE) {
					log("In line: " + workingCommand + " WARNING: bad number in unload " + n[0]);
				}
			}
		}
		//COLLECT <eq> <eq>
		else if(workingCommand.toUpperCase().startsWith("COLLECT ")) {
			String eq = workingCommand.substring("COLLECT".length()).trim();
			eq = UniVar.getValue(subVars(eq) + "") + "";
			String n[] = Stuff.getTokens(eq, " \"\t");
			if(n.length != 2) {
				log("In line: " + workingCommand + " WARNING:  bad format for Collect command");
				return;
			}
			if( !hasTech("T36")) {
				log("In line: " + workingCommand + " WARNING: Cannot collect no HeavyWorkArm technolgy available");
				return;
			}
			int matden=Universe.getMaterialDensity(loc, n[0]);
			//check for collector technology
			if(matden<6 &&  !Universe.locationHasTechnology(loc, "T44")) {
				log("In line: " + workingCommand + " WARNING: Cannot collect no Collector technolgy available");
				return;
			}
			if(matden==6 &&  !Universe.locationHasTechnology(loc, "T52")) {
//Condenser required
				log("In line: " + workingCommand + " WARNING: Cannot collect no Condenser technolgy available");
				return;
			}
			if(matden>6 &&  !Universe.locationHasTechnology(loc, "T51")) {
//Distilation required
				log("In line: " + workingCommand + " WARNING: Cannot collect no Distilation technolgy available");
				return;
			}
			if(matden> 0) {
				try {
					String mat = Market.getMarketName(n[0]);
					if(mat == null) {
						log("In line: " + workingCommand + " WARNING: unknown material " + n[0]);
						return;
					}
					int num=0;
					try {
						num = (int)Stuff.parseDouble(n[1]);
					} catch (NumberFormatException nfe) {
						log("In line: " + workingCommand + " WARNING: bad number in command "+n[1]);
						return;
					}
					NFObject nfo = new StockPile(corpTick, mat, num, loc);
					if (matden<=5) Universe.consume(mat,num,loc);//don't consume gasses and liquids
					Facility f=(Facility)Universe.getNFObjectById(nfo.getLocation().getFac());
					if (f!=null && f.getCapacity()<nfo.getMass()) {
						if (Main.DEBUG) System.out.println("DEBUG: Facility full moving stockpile outside");
						nfo.setLocation(f.getLocation());//facility full put outside
					}
					Universe.add(nfo);
				}
				catch(NumberFormatException NFE) {
					log("In line: " + workingCommand + " WARNING: bad number in Collect command " + n[1]);
				}
			}
			else {
				log("In line: " + workingCommand + " WARNING: Unable to collect " + n[0] + " at location " + loc + " no resources");
			}
		}
		//REFINE <eq> <eq>
		else if(workingCommand.toUpperCase().startsWith("REFINE ")) {
			String eq = workingCommand.substring("REFINE".length()).trim();
			eq = UniVar.getValue(subVars(eq) + "") + "";
			String n[] = Stuff.getTokens(eq, " \"\t");
			if(n.length != 2) {
				log("In line: " + workingCommand + " WARNING: bad num args in refine command");
				return;
			}
			int amt = 1;
			try {
				amt=(int)Stuff.parseDouble(n[1]);
			} catch (NumberFormatException nfe) {
				log("In line: " + workingCommand + " WARNING: bad number in command "+n[1]);
				return;
			}
			String ma = n[0];
			if( !hasTech("T36")) {
				log("In line: " + workingCommand + " WARNING: Cannot refine no HeavyWorkArm technolgy available");
				return;
			}
			if( !Universe.locationHasTechnology(loc, "T45")) {
				log("In line: " + workingCommand + " WARNING: Cannot refine no Refinery technolgy available");
				return;
			}
//check for required materials	
			MarketItem mi = Universe.getMarket().getItemByName(ma);
			if(mi == null) {
				log("In line: " + workingCommand + " WARNING: Cannot find material " + ma);
				return;
			}
			ma = mi.getName();
			String reqmat = mi.getRefineMaterial();
			if(reqmat == null) {
				log("In line: " + workingCommand + " WARNING: Cannot refine " + ma + " is native or must be produced");
				return;
			}
			//check for material availability
			int ava = Universe.availableMaterial(Location.parse("INSIDE." + getId()), reqmat);
			ava += Universe.availableMaterial(loc, reqmat);
			if(ava == 0) {
				log("In line: " + workingCommand + " WARNING: unable to refine " + ma + " no " + reqmat + " available");
				return;
			}
			if(ava < amt) {
				log("In line: " + workingCommand + " WARNING: unable to refine " + amt + " of " + ma + " not enought " + reqmat + ".  Will use " + ava + " instead");
				amt = ava;
			}
			//consume required materals
			int collected = Universe.takeMaterial(corpTick, Location.parse("INSIDE." + getId()), reqmat, amt);
			Universe.takeMaterial(corpTick, loc, reqmat, amt - collected);
			//create stockpile (outside)
			NFObject nfo = new StockPile(corpTick, ma, amt, loc);
			Universe.add(nfo);
			//Universe.mergeStockPiles();
		}
		//SALVAGE <eq>
		else if(workingCommand.toUpperCase().startsWith("SALVAGE ")) {
			String eq = workingCommand.substring("SALVAGE".length()).trim();
			eq = UniVar.getValue(subVars(eq) + "") + "";
			String n[] = Stuff.getTokens(eq, " \"\t");

			int ava = Universe.availableMaterial(Location.parse("INSIDE." + getId()), "JUNK");
			ava += Universe.availableMaterial(loc, "JUNK");

			if( !hasTech("T36")) {
				log("In line: " + workingCommand + " WARNING: Cannot salvage no HeavyWorkArm technolgy available");
				return;
			}
			if( !Universe.locationHasTechnology(loc, "T48") && !hasTech("T48")) {
				log("In line: " + workingCommand + " WARNING: Cannot salvage no Salvage technolgy available");
				return;
			}
			int amt = ava;
			if (ava==0) {
				log("  WARNING: No Junk available to Salvage.");
				return;
			}
			if(n.length == 2) {
				try {
					int reqamt = (int)Stuff.parseDouble(n[1]);
					if (reqamt>ava) {
						log("  WARNING: Not enough Junk available will Salvage "+amt+" instead");
					}
					amt = Math.max(0, Math.min(amt, reqamt));
				}
				catch(NumberFormatException nfe) {
					log("In line: " + workingCommand + " WARNING: Bad number in salvage " + n[1]);
					return;
				}
			}
			if (Main.DEBUG) System.out.println("MYDEBUG: Salvage requested for "+amt);
			
			int collected = Universe.takeMaterial(corpTick, Location.parse("INSIDE." + getId()), "JUNK", amt);
			Universe.takeMaterial(corpTick, loc, "JUNK", amt - collected);
			double sa=SALVAGEAMT+Math.random()*SALVAGEAMT;
			int spamt = (int)(amt * sa);
			int iwamt = amt-spamt;
			NFObject nfo1 = new StockPile(corpTick, "SpareParts", spamt, loc);
			NFObject nfo2 = new StockPile(corpTick, "IndustrialWaste", iwamt, loc);
			Universe.add(nfo1);
			Universe.add(nfo2);
		}
		//RECOVEr <eq>
		else if(workingCommand.toUpperCase().startsWith("RECOVER ")) {
			String eq = workingCommand.substring("RECOVER".length()).trim();
			eq = UniVar.getValue(subVars(eq) + "") + "";
			String n[] = Stuff.getTokens(eq, " \"\t");
			if(n.length != 1) {
				log("In line: " + workingCommand + " WARNING:  wrong number of args");
				return;
			}
			//get prefab
			NFObject nfo = Universe.getNFObjectById(n[0]);
			//find NFOID
			if(nfo == null || !(nfo instanceof Facility)) {
				log("In line: " + workingCommand + " WARNING: cannot find Facility " + n[0]);
				return;
			}
			//check if prefab at loc
			if( !nfo.getLocation().equals(loc)) {
				log("In line: " + workingCommand + " WARNING: cannot recover facility.  Not at location.");
				return;
			}
			if( !hasTech("T36")) {
				log("In line: " + workingCommand + " WARNING: Cannot recover no HeavyWorkArm technolgy available");
				return;
			}
			Facility f=(Facility)nfo;
			if (!f.canRecover()) {
				log("In line: " + workingCommand + " WARNING: Cannot recover facility too damaged.");
				return;
			}
			int fcount=Universe.facilityCount(loc);
			if (fcount==3 || fcount==4) {
				Body b=Universe.getBodyByLocation(loc);
				if (b!=null && fcount==3) 
					b.setSurfaceLocation(loc,'p');
				if (b!=null && fcount==4) 
					b.setSurfaceLocation(loc,'c');
			}
			//create new prefab
			Prefab p = new Prefab(corpTick, f.getFacilityDesignId(), loc);
			Universe.add(p);
			//empty facility
			Location l2=Location.parse(loc+"."+f.getId());
			Vector v = Universe.getNFObjectsByLocation(l2);
			for(int a = 0; a < v.size(); a++) {
				NFObject nfooo = (NFObject)v.elementAt(a);
				nfooo.setLocation(loc);
			}
			//remove facility
			Universe.remove(nfo);
		}
		//CONSTRUCT <eq>
		else if(workingCommand.toUpperCase().startsWith("CONSTRUCT ")) {
			String eq = workingCommand.substring("CONSTRUCT".length()).trim();
			eq = UniVar.getValue(subVars(eq) + "") + "";
			String n[] = Stuff.getTokens(eq, " \"\t");
			if(n.length != 1) {
				log("In line: " + workingCommand + " WARNING:  wrong number of args");
				return;
			}
			//get prefab
			NFObject nfo = Universe.getNFObjectById(n[0]);
			//find NFOID
			if(nfo == null || !(nfo instanceof Prefab)) {
				log("In line: " + workingCommand + " WARNING: cannot find prefab " + n[0]);
				return;
			}
			//check if prefab at loc
			if( !nfo.getLocation().equals(loc)) {
				log("In line: " + workingCommand + " WARNING: cannot construct facility.  No prefab available.");
				return;
			}
			//check location is inside
			if(loc.isInside() || loc.isFacility()) {
				log("In line: " + workingCommand + " WARNING: cannot construct facility inside");
				return;
			}
			if( !hasTech("T36")) {
				log("In line: " + workingCommand + " WARNING: Cannot construct no HeavyWorkArm technolgy available");
				return;
			}
			Prefab pf=(Prefab)nfo;
			if (!pf.canConstructAt(loc)) {
				log("In line: " + workingCommand + " WARNING: Cannot construct facility.  Technology cannot operate at locaiton.");
				return;
			}
			int fcount=Universe.facilityCount(loc);
			if (fcount>3) {
				log("In line: " + workingCommand + " WARNING: Cannot construct facility.  Locaiton at maximum capacity.");
				return;
			}
			if (fcount==2 || fcount==3) {
				Body b=Universe.getBodyByLocation(loc);
				if (b!=null && fcount==2) 
					b.setSurfaceLocation(loc,'c');
				if (b!=null && fcount==3) 
					b.setSurfaceLocation(loc,'C');
			}
			//create new facility
			Facility f = new Facility(corpTick, pf.facilityDesign.getId(), loc);
			Universe.add(f);
			//remove prefab
			Universe.remove(nfo);
		}
		else if(workingCommand.toUpperCase().startsWith("PRODUCE ")) {
			String eq = workingCommand.substring("PRODUCE".length()).trim();
			eq = UniVar.getValue(subVars(eq) + "") + "";
			String n[] = Stuff.getTokens(eq, " \"\t");
			Vector req = new Vector();

			int amt = 1;
			//check for arm
			if( !hasTech("T36")) {
				log("In line: " + workingCommand + " WARNING: Cannot produce no HeavyWorkArm technolgy available");
				return;
			}
			ITThing itt = Universe.getITThingByName(n[0]);
			MarketItem mi = Universe.getMarket().getItemByName(n[0]);
			if (itt!=null) {
				if (n.length>1) {
					log("In line: " + workingCommand + " WARNING: Bad use of design production.  Cannot produce more than one.");
					return;
				}
				//check for PRODUCTION technology
				if( !Universe.locationHasTechnology(loc, "T46")) {
					log("In line: " + workingCommand + " WARNING: Cannot produce no Production technolgy available");
					return;
				}
				if (itt instanceof ActiveDesign) {
					ActiveDesign att=(ActiveDesign)itt;
					if (availableMaterialList(Location.parse("INSIDE."+getId()),loc,att.getAllProductionCosts())<1){
						log("In line: " + workingCommand + " WARNING: Cannot produce " + n[0] + " cannot find needed materials");
						return;
					}
					//get lease price and check payable
					if (!itt.isPartner(corpTick) && !itt.isPublic()) {
						double leasePrice=itt.getLeasePrice();
						Corp c=Universe.getCorpByTick(corpTick);
						if (c!=null) {
							if (c.canAfford(leasePrice)) {
								c.makePayment(leasePrice);
								c.report+="  "+getId()+" Paying lease on "+n[0]+" of "+Stuff.money(leasePrice,2)+"\n";
								itt.payLease();
							}
							else {
								log("WARNING: cannot pay lease on "+n[0]+".");
								return;
							}
						}
					}
					consumeMaterialList(Location.parse("INSIDE."+getId()),loc,att.getAllProductionCosts(),1);
					Active AA = new Active(corpTick, att.getId(), loc, - 1, "", "", new Hashtable());
					Universe.add(AA);
				}
				else if (itt instanceof FacilityDesign) {
					FacilityDesign att=(FacilityDesign)itt;
					if (availableMaterialList(Location.parse("INSIDE."+getId()),loc,att.getAllProductionCosts())<1){
						log("In line: " + workingCommand + " WARNING: Cannot produce " + n[0] + " cannot find needed materials");
						return;
					}
					//get lease price and check payable
					if (!itt.isPartner(corpTick) && !itt.isPublic()) {
						double leasePrice=itt.getLeasePrice();
						Corp c=Universe.getCorpByTick(corpTick);
						if (c!=null) {
							if (c.canAfford(leasePrice)) {
								c.makePayment(leasePrice);
								c.report+="  "+getId()+" Paying lease on "+n[0]+" of "+Stuff.money(leasePrice,2)+"\n";
								itt.payLease();
							}
							else {
								log("WARNING: cannot pay lease on "+n[0]+".");
								return;
							}
						}
					}
					consumeMaterialList(Location.parse("INSIDE."+getId()),loc,att.getAllProductionCosts(),1);
					Prefab p = new Prefab(corpTick, att.getId(), loc);
					Universe.add(p);
				}
				else {
					log("WARNING: Unknown design.  Cannot produce "+n[0]+".");
					return;
				}
			}
			else if (mi!=null) {
if (Main.DEBUG) System.out.println("MYDEBUG: produce material found "+mi.getName());
				if (n.length!=2) {
					log("In line: " + workingCommand + " WARNING: Bad use of material production.  Must specify number to produce.");
					return;
				}
				int reqamt=0;
				try {
					reqamt=Integer.parseInt(n[1]);	
				} catch (NumberFormatException nfe) {
					log("WARNING: Bad number in Produce command "+n[1]+".");
					return;
				}
if (Main.DEBUG) System.out.println("MYDEBUG: amount requested ="+reqamt);
				if (mi.getId().equals("M1")) {
if (Main.DEBUG) System.out.println("MYDEBUG: recycle or incenerate requested");
					if( !Universe.locationHasTechnology(loc, "T49") && !Universe.locationHasTechnology(loc, "T50")) {
						log("WARNING: No recycler or inceneerator technology at location.");
						return;
					}
					int unitsize=1;
					int amt2=0;
					int industwast=availableMaterialList(Location.parse("INSIDE."+getId()),loc,"M70");
					int toxwast=availableMaterialList(Location.parse("INSIDE."+getId()),loc,"M68");
					int humanwast=availableMaterialList(Location.parse("INSIDE."+getId()),loc,"M69");
					int otherwast=toxwast+humanwast;
if (Main.DEBUG) System.out.println("MYDEBUG: recycle max ="+industwast+"  incenerate max ="+otherwast);
					//indwastamt
					if( !Universe.locationHasTechnology(loc, "T49") ) {
						amt2+=industwast;
					}
					//othwastamt
					if( !Universe.locationHasTechnology(loc, "T50") ) {
						amt2+=toxwast;
						amt2+=humanwast;
					}
					//if availableMaterialList>=amt
					if (amt2==0) {
						log("In line: " + workingCommand + " WARNING: No material available for recycle or incenerate.");
						return;
					}
					if (reqamt>amt2) {
						log("In line: " + workingCommand + " WARNING: Insufficent material.  Will produce "+amt2+" instead");
						reqamt=amt2;
					}
					//consumeMaterialList
					int collect=0;
					if( !Universe.locationHasTechnology(loc, "T49") && industwast>0) {
						int get=Math.min(reqamt,industwast);
						consumeMaterialList(Location.parse("INSIDE."+getId()),loc,"M70",get);
						collect+=get;
						if (Main.DEBUG) System.out.println("MYDEBUG: recycling  ="+get);
					}
					if( !Universe.locationHasTechnology(loc, "T50") && otherwast>0) {
						int get=Math.min(reqamt-collect,humanwast);
						consumeMaterialList(Location.parse("INSIDE."+getId()),loc,"M69",get);
						collect+=get;
						if (Main.DEBUG) System.out.println("MYDEBUG: incenerate 1  ="+get);
						get=Math.min(reqamt-collect,toxwast);
						consumeMaterialList(Location.parse("INSIDE."+getId()),loc,"M68",get);
						collect+=get;
						if (Main.DEBUG) System.out.println("MYDEBUG: incenerate 2  ="+get);
					}
					//make stokepiles
					NFObject nfo = new StockPile(corpTick, mi.getName(), reqamt, loc);
					Universe.add(nfo);
				}
				else if (mi.isProduced()) {
					if( !Universe.locationHasTechnology(loc, "T46")) {
						log("In line: " + workingCommand + " WARNING: Cannot produce no Production technolgy available");
						return;
					}
					int unitsize=mi.getUnitSize();	
					reqamt=reqamt+(reqamt%unitsize!=0?unitsize-reqamt%unitsize:0);
					int maxamount=availableMaterialList(Location.parse("INSIDE."+getId()),loc,mi.getProducedMaterial());
					if (maxamount==0) {
						log("In line: " + workingCommand + " WARNING: No materials available.");
						return;
					}
					if (reqamt>maxamount*unitsize) {
						log("In line: " + workingCommand + " WARNING: Insufficent material.  Will produce "+maxamount*unitsize+" instead");
						reqamt=maxamount*unitsize;
					}
					consumeMaterialList(Location.parse("INSIDE."+getId()),loc,mi.getProducedMaterial(),reqamt/unitsize);
					NFObject nfo = new StockPile(corpTick, mi.getName(), reqamt, loc);
					Universe.add(nfo);
				}
				else {
					log("WARNING: Materila cannot be produced.  Must be Refined or Collected. "+n[0]+".");
					return;
				}
			}
			else {
				log("WARNING: Unknown production.  Cannot produce "+n[0]+".");
				return;
			}

		}
		//REPAIR <eq> [<eq>]
		else if(workingCommand.toUpperCase().startsWith("REPAIR ")) {
			String eq = workingCommand.substring("REPAIR".length()).trim();
			eq = UniVar.getValue(subVars(eq) + "") + "";
			String n[] = Stuff.getTokens(eq, " \"\t");
			int amt = 1;
			if(n.length < 1) {
				log("In line: " + workingCommand + " WARNING: unable to repair bad number of args");
				return;
			}
			NFObject nfo = Universe.getNFObjectById(n[0]);
			//find NFOID
			if(nfo == null) {
				log("In line: " + workingCommand + " WARNING: unable to repair cannot find " + n[0]);
				return;
			}
			if(nfo == this) {
				log("In line: " + workingCommand + " WARNING: unable to repair self");
				return;
			}
			if (!nfo.getLocation().equalsIgnoreCase(loc)) {
				log("In line: " + workingCommand + " WARNING: unable to repair not at same location.");
				return;
			}
			if(nfo instanceof StockPile || nfo instanceof Prefab) {
				log("In line: " + workingCommand + " WARNING: unable to repair " + n[0] + " is stockpile not robot, ship or facility");
				return;
			}
			if(nfo instanceof Active) {
				Active aa = (Active)nfo;
				amt = aa.getRepairMass();
			}
			if(nfo instanceof Facility) {
				Facility aa = (Facility)nfo;
				amt = aa.getRepairMass();
			}
			if(n.length == 2) {
				try {
					amt = Math.max(1, Math.min(amt,(int) Stuff.parseDouble(n[1])));
				}
				catch(NumberFormatException nfe) {
					log("In line: " + workingCommand + " WARNING: Bad number in repair " + n[1]);
					return;
				}
			}
			if( !Universe.locationHasTechnology(loc, "T47") && !hasTech("T47")) {
				log("In line: " + workingCommand + " WARNING: No Repair technology.");
				return;
			}
			int ava = Universe.availableMaterial(Location.parse("INSIDE." + getId()), "SpareParts");
			ava += Universe.availableMaterial(loc, "SpareParts");
			if(ava == 0) {
				log("In line: " + workingCommand + " WARNING: unable to repair no SpareParts available");
				return;
			}
			if(ava < amt) {
				if(n.length == 2) {
					log("In line: " + workingCommand + " WARNING: unable to repair " + amt + " not enough SpareParts.  Will use " + ava + " instead");
				}
				amt = ava;
			}
			//consume required materals
			int collected = Universe.takeMaterial(corpTick, Location.parse("INSIDE." + getId()), "SpareParts", amt);
			Universe.takeMaterial(corpTick, loc, "SpareParts", amt - collected);
			if (Main.DEBUG) System.out.println("DEBUG: do repair test"+amt);
			if(nfo instanceof Active) {
				Active aa = (Active)nfo;
				aa.doRepair(amt);
				if (Main.DEBUG)  System.out.println("DEBUG: repair done test"+aa.getRepairMass());
			}
			if(nfo instanceof Facility) {
				Facility aa = (Facility)nfo;
				aa.doRepair(amt);
				if (Main.DEBUG)  System.out.println("DEBUG: repair done test"+aa.getRepairMass());
			}
		}
		//REFUEL  :fill self
		//REFUEL <amt> : fill self amount
		//REFUEL <id> : fill other
		//REFUEL <id> <amt> : fill other amt
		else if(workingCommand.toUpperCase().startsWith("REFUEL")) {
			String eq = workingCommand.substring("REFUEL".length()).trim();
			eq = UniVar.getValue(subVars(eq) + "") + "";
			String n[] = Stuff.getTokens(eq, " \"\t");
			Active targ=this;
			int amt=(int)(activeDesign.getDesignEndurance())*2;
			if (n.length>0) {
				NFObject nfo=Universe.getNFObjectById(n[0]);
				if (nfo!=null && nfo instanceof Active) {
					targ=(Active)nfo;
					if (!targ.getLocation().equalsIgnoreCase(getLocation())) {
							log("In line: " + workingCommand + " WARNING: Cannot refuel "+n[0]+" not at same location");
							return;
					}
					amt=(int)(targ.activeDesign.getDesignEndurance())*2;
					if (n.length>1) {
						try {
							amt=Math.max(0,Math.min(amt,(int) Stuff.parseDouble(n[1])));
						} catch (NumberFormatException nfe) {
							log("In line: " + workingCommand + " WARNING: Bad number in refuel");
							return;
						}
					}
				}
				else {
					try {
						amt=Math.max(0,Math.min(amt,(int)Stuff.parseDouble(n[0])));
					} catch (NumberFormatException nfe) {
						log("In line: " + workingCommand + " WARNING: Bad number in refuel");
						return;
					}
				}
			}
			if (targ.isRobot()) {
				log("In line: " + workingCommand + " WARNING: Cannot refuel robot, must Recharge");
				return;
			}
			
			amt=Math.min(amt,(int)(activeDesign.getDesignEndurance()-targ.getEndurance())*2);
			int ava = Universe.availableMaterial(Location.parse("INSIDE." + getId()), "RocketFuel");
			ava += Universe.availableMaterial(loc, "RocketFuel");
			if (ava==0) {
				log("In line: " + workingCommand + " WARNING: unable to refuel no RocketFuel found.");
			}
			else if (amt>ava) {
				log("In line: " + workingCommand + " WARNING: unable to refuel " + amt + " not enough RocketFuel.  Will use " + ava + " instead.");
			}
			amt=Math.min(ava,amt);
			//collect fuel needed
			int collected = Universe.takeMaterial(corpTick, Location.parse("INSIDE." + getId()), "RocketFuel", amt);
			Universe.takeMaterial(corpTick, loc, "RocketFuel", amt - collected);
			targ.refuel(amt);
		}
		//RECHARGE [<eq>]
		else if(workingCommand.toUpperCase().startsWith("RECHARGE")) {
			String eq = workingCommand.substring("RECHARGE".length()).trim();
			eq = UniVar.getValue(subVars(eq) + "") + "";
			String n[] = Stuff.getTokens(eq, " \"\t");
			if(n.length == 0) {
				if (isShip()) {
					log("In line: " + workingCommand + " WARNING: Cannot recharge ship, must Refuel");
					return;
				}
				if (loc.isInside()) {
					NFObject nfo=Universe.getNFObjectById(loc.getInsideWhat());
					if (nfo==null) {
						log("In line: " + workingCommand + " WARNING: Cannot find INSIDE for recharge");
						return;
					}
					if (nfo==this) {
						log("In line: " + workingCommand + " WARNING: Cannot recharge self");
						return;
					}
					if (nfo instanceof Active) {
						Active a=(Active)nfo;
						if (!a.isPowered() && a.isShip()) {
							log("In line: " + workingCommand + " WARNING: Cannot recharge.  Ship out of power.");
							return;
						}
						else if (a.isRobot()) {
							log("In line: " + workingCommand + " WARNING: Cannot recharge inside robot only ships.");
							return;
						}
						recharge();
						return;
					}
					if (nfo instanceof Facility) {
						Facility f=(Facility)nfo;
						if (f.hasTech("T64") || f.hasTech("T65") || f.hasTech("T66") || f.hasTech("T67") || f.hasTech("T68") || f.hasTech("T85") ){
							recharge();
							return;
						}
						else {
							log("In line: " + workingCommand + " WARNING: Cannot recharge.  NO power inside facility.");
							return;
						}
					}
					log("In line: " + workingCommand + " WARNING: Unknown error in recharge.");
					return;
				}
				else {
					log("In line: " + workingCommand + " WARNING: Not inside ship or facilty.  Cannot recharge");
					return;
				}
			}
			if(n.length == 1) {
				NFObject nfo=Universe.getNFObjectById(n[0]);
				if (nfo==null) {
					log("In line: " + workingCommand + " WARNING: Cannot find "+n[0]+" for recharge");
					return;
				}
				if (nfo instanceof Active) {
					if (!nfo.getLocation().equals(loc)){
						log("In line: " + workingCommand + " WARNING: Cannot recharge "+n[0]+". Not at same location.");
						return;
					}
					sharePower((Active)nfo);
				}
				
			}
		}
		//ATTACK <eq>
		else if(workingCommand.toUpperCase().startsWith("ATTACK ")) {
			String eq = workingCommand.substring("ATTACK ".length()).trim();
			eq = UniVar.getValue(subVars(eq) + "") + "";
			String n[] = Stuff.getTokens(eq, " \"\t");
			if(n.length != 1) {
				log("In line: " + workingCommand + " WARNING: Bad use of attack.  Must specify target robot, ship or facility.");
				return;
			}
			NFObject nfo = Universe.getNFObjectById(n[0]);
			if(nfo == null) {
				log("In line: " + workingCommand + " WARNING: Bad use of attack.  Cannot find target "+n[0]);
				return;
			}
			if(nfo == this) {
				log("In line: " + workingCommand + " WARNING: Bad use of attack.  Cannot attack self");
				return;
			}
			if(nfo instanceof StockPile || nfo instanceof Prefab) {
				log("In line: " + workingCommand + " WARNING: Bad use of attack.  target not robot, ship of facility. "+n[0]);
				return;
			}
			if (!nfo.getLocation().equals(getLocation())) {
				log("In line: " + workingCommand + " WARNING: Cannot attack not at same location. "+n[0]);
				return;
			}
			if (nfo instanceof Active) {
				Active ac=(Active)nfo;
				ac.attackedBy(this);
				ac.counterAttack(this);
			}
			if (nfo instanceof Facility) {
				Facility fac=(Facility)nfo;
				fac.attackedBy(this);
				fac.counterAttack(this);
			}
		}
		//SEND <eq> [<corp>] message
		else if(workingCommand.toUpperCase().startsWith("SEND ")) {
			String eq = workingCommand.substring("SEND ".length()).trim();
			eq = UniVar.getValue(subVars(eq) + "") + "";
			String n[] = Stuff.getTokens(eq, " \"\t");
			if(n.length < 2) {
				log("In line: " + workingCommand + " WARNING: Bad use of send.  Must specify robot or ship to receive and message.");
				return;
			}
			NFObject nfo = Universe.getNFObjectById(n[0]);
			if(nfo == null) {
				log("In line: " + workingCommand + " WARNING: Bad use of send.  Cannot find "+n[0]+" to send message to.");
				return;
			}
			if(!(nfo instanceof Active)) {
				log("In line: " + workingCommand + " WARNING: Bad use of send.  Message can only be sent to robot or ship.");
				return;
			}
			Active act=(Active)nfo;
			if (n[1].length()==0) n[1]="-";
			act.setMessage(n[1]);
		}
		else {
			log("In line: " + workingCommand + " WARNING: unknown command");
		}
		Universe.mergeStockPiles();
	}
	
	public void advanceClockTo(long t) {
		if(currTime <= 0) {
			currTime = t;
		}
		if(finishTime == - 1) {
			return;
		}
		//not initilized
		if(lines == null || lines.trim().equals("")) {
			finishTime = - 1;
			return;
		}
		if(finishTime == 0) {
			//init was called begin
			finishCommand = "";
			loadNextCommand();
			if(Main.DEBUG)System.out.println("init program found");
		}
		for(; currTime < t; currTime += MINTIME) {
			if(finishTime <= currTime) {
				//finish last command
				executeCommand();
				loadNextCommand();
			}
			if(finishTime == - 1) {
				return;
			}
		}
	}
	
	public void nolog(String s) {
		if(Main.DEBUG)System.out.println(s);
		while(s.indexOf("\"") >= 0) {
			s = s.substring(0, s.indexOf("\"")) + s.substring(s.indexOf("\"") + 1);
		}
		log .append(">" + s + "\n");
	}
	
	public void log(String s) {
		if(Main.DEBUG)System.out.println("log: " + s);
		log.append("  LOG:");
		while(s.indexOf("_") >= 0) {
			log.append( s.substring(0, s.indexOf("_")) + "\n");
			s = s.substring(s.indexOf("_") + 1);
		}
		while(s.indexOf("\"") >= 0) {
			s = s.substring(0, s.indexOf("\"")) + s.substring(s.indexOf("\"") + 1);
		}
		log.append(s + "\n");
	}
	
	public boolean chechTechnology(String s) {
		return activeDesign.checkTechnology(s);
	}
	
	public static Active parse(GmlPair g) {
		if( !g.getName().equalsIgnoreCase("Active")) {
			return null;
		}
		GmlPair n[] = g.getAllByName("Tick");
		String cn = n[0].getString();
		n = g.getAllByName("ActiveDesignId");
		String dn = n[0].getString();
		n = g.getAllByName("ActiveId");
		int fid = (int)(n[0].getDouble());
		n = g.getAllByName("FinishTime");
		long ft = (long)(n[0].getDouble());
		n = g.getAllByName("FinishCommand");
		String fc = n[0].getString();
		n = g.getAllByName("Loc");
		Location l = Location.parse(n[0].getString());
		n = g.getAllByName("Lines");
		String ls = n[0].getString();
		n = g.getAllByName("Var");
		Hashtable vs = new Hashtable();
		for(int a = 0; a < n.length; a++) {
			String temp = n[a].getString();
			if(temp.indexOf(":") > 0) {
				String key = temp.substring(0, temp.indexOf(":")).trim();
				String val = temp.substring(temp.indexOf(":") + 1).trim();
				if (val.length()==0) val="-";
				vs.put(key, val);
			}
			else {
				System.out.println("MyError: bad var read " + temp +" in "+g);
			}
		}
		//System.out.println("read var from file ="+vs);
		Active a=new Active(cn, dn, l, fid, ft, fc, ls, vs);
		GmlPair NN = g.getOneByName("Nick");
		if (NN!=null) a.nick=NN.getString();
		NN = g.getOneByName("Desc");
		if (NN!=null) a.desc=NN.getString();
		return a;
	}

	boolean destroyed=false;	
	public void destroy() {
		if (destroyed==true) return;
//log connection lost
		Corp C=Universe.getCorpByTick(corpTick);
		if (C!=null) {
   					C.addReport("Unit "+getId()+" signal lost.\n");
		}
		destroyed=true;
		//unload all cargo
		String c = (String)vars.get("_cargo");
		String cc[] = Stuff.getTokens(c, CARGOSEP);
		for(int a = 0; a < cc.length; a++) {
			if (cc[a].equals("-")) continue;
			NFObject nfo = Universe.getNFObjectById(cc[a]);
			if (nfo!=null)
				nfo.setLocation(loc);
			else {
				System.out.println("MyError: bad nfo in cargo during destory "+cc[a]);
			}
		}
		//create junk stockpile
		NFObject nfo = new StockPile(corpTick, "Junk", activeDesign.getDesignMass(), loc);
		Universe.add(nfo);
		//remvoe self
		Universe.remove(this);
		Universe.mergeStockPiles();
	}
	
	public void consumeMaterialList(Location l1,Location l2,String prodList,int mult) {
		if (prodList==null || prodList.length()==0) return;
		Vector v=new Vector();
		v.addElement(prodList);
		consumeMaterialList(l1,l2,v,mult);
	}

	public void consumeMaterialList(Location l1,Location l2,Vector prodList,int mult) {
		for(int a=0;a<prodList.size();a++) {
			String prodReq=(String)prodList.elementAt(a);
			int unitcost=1;
			if (prodReq.indexOf("x")>0) {
				try {
					unitcost=Integer.parseInt(prodReq.substring(0,prodReq.indexOf("x")));
				} catch (NumberFormatException nfe) {
				}
				prodReq=prodReq.substring(prodReq.indexOf("x")+1);
			}
			prodReq=Market.getMarketName(prodReq);
			if (prodReq==null) continue;
			int collected = 0;
			if (l1!=null) collected=Universe.takeMaterial(corpTick, l1, prodReq, unitcost*mult);
			if (l2!=null) Universe.takeMaterial(corpTick, l2, prodReq, unitcost*mult - collected);
		}
	}

	public int availableMaterialList(Location l1,Location l2,String prodList) {
		if (prodList==null || prodList.length()==0) return 0;
		Vector v=new Vector();
		v.addElement(prodList);
		return availableMaterialList(l1,l2,v);
	}

	public int availableMaterialList(Location l1,Location l2,Vector prodList) {
		if (prodList.size()==0) return 0;
		int max=999;
		for(int a=0;a<prodList.size();a++) {
			String prodReq=(String)prodList.elementAt(a);
			int unitcost=1;
			if (prodReq.indexOf("x")>0) {
				try {
					unitcost=Integer.parseInt(prodReq.substring(0,prodReq.indexOf("x")));
				} catch (NumberFormatException nfe) {
				}
				prodReq=prodReq.substring(prodReq.indexOf("x")+1);
			}
			prodReq=Market.getMarketName(prodReq);
			if (prodReq==null) return 0;
			int ava =0;
			if (l1!=null) ava= Universe.availableMaterial(l1, prodReq);
			if (l2!=null) ava += Universe.availableMaterial(l2, prodReq);
			max=Math.min(max,ava/unitcost);
		}
		return max;
	}

	public static String myTimeFormat(long time) {
		//dow mon dd hh:mm:ss zzz yyyy
		//    YYYY.MM.DD.HH:MM:SS
		Date D = new Date(time);
		String s = D.toString();
		String y = s.substring(s.lastIndexOf(" ")).trim();
		String m = s.substring(s.indexOf(" ")).trim();
		String d = m.substring(m.indexOf(" ")).trim();
		m = m.substring(0, m.indexOf(" "));
		String h = d.substring(d.indexOf(" ")).trim();
		d = d.substring(0, d.indexOf(" "));
		h = h.substring(0, h.indexOf(" "));
		h = h.substring(0, h.lastIndexOf(":"));
		if(m.equals("Jan")) {
			m = "01";
		}
		else if(m.equals("Feb")) {
			m = "02";
		}
		else if(m.equals("Mar")) {
			m = "03";
		}
		else if(m.equals("Apr")) {
			m = "04";
		}
		else if(m.equals("May")) {
			m = "05";
		}
		else if(m.equals("Jun")) {
			m = "06";
		}
		else if(m.equals("Jul")) {
			m = "07";
		}
		else if(m.equals("Aug")) {
			m = "08";
		}
		else if(m.equals("Sep")) {
			m = "09";
		}
		else if(m.equals("Oct")) {
			m = "10";
		}
		else if(m.equals("Nov")) {
			m = "11";
		}
		else if(m.equals("Dec")) {
			m = "12";
		}
		return y + "." + m + "." + d + "." + h;
		//dow mon dd hh:mm:ss zzz yyyy
		//    YYYY.MM.DD.HH:MM:SS
		//    return
	}
}

