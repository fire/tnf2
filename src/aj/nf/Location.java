package aj.nf;


/**
 *
 *@author     judda 
 *@created    July 21, 2000 
 */
public class Location {

	private String loc = "";
	private String type = null;
	//INSIDE or INROUTE
	private int solar = -1;
	private int planet = -1;
	private String moon = "";
	private int sector = -1;//0-3 planet  
	private int row = -1;// 0 - size*2-1
	private int col = -1;// 0 - size*2-1
	private int level = -1;//0 - size-1
	private String fac = "";
	private String minor="";
	
	public static int SURFACE = 0, SUBTER = 1, ESCAPE=2, INTERPLANET = 3, INTERSOLAR = 4, LOAD=5, REENTRY=6, UNLOAD=7;


	public Location(String all, String t, int so, int p, String m, int se, int r, int c, int l, String f) {
		if (t != null) {
			type = t.toUpperCase().trim();
		}
		if (all != null) {
			loc = all.toUpperCase().trim();
		}
		solar = so;
		planet = p;
		moon = m;
		try {
			int mm=Integer.parseInt(moon);
			if (mm!=0) {
				moon=(mm>9?"":"0")+mm;
			}
			else {
				moon="";
			}
		} catch (NumberFormatException nfe) {
		}
		sector = se;
		row = r;
		col = c;
		level = l;
		if (level<0) level=0;
		if (f != null) {
			fac = f.toUpperCase().trim();
		}
		rebuild();
	}

	public Location(Location l) {
		loc = l.loc;
		type = l.type;
		solar = l.solar;
		planet = l.planet;
		moon = l.moon;
		sector = l.sector;
		row = l.row;
		col = l.col;
		level = l.level;
		fac = l.fac;
	}

	public static Location getInsideActive(String id) {
		id=id.trim().toUpperCase();
		return new Location("INSIDE."+id, "INSIDE", - 1, - 1, "", - 1, - 1, - 1, - 1, id);
	}

	public Location reference(String s) {
		s=s.toUpperCase().trim();
		NFObject nfo=Universe.getNFObjectById(s);
		if (nfo!=null) {
			if (nfo instanceof Facility) {
				//if (nfo.getLocation().equals(this)) {
					return Location.parse(nfo.getLocation()+"."+nfo.getId());
				//}
			}
			if (nfo instanceof Active) {
				//if (nfo.getLocation().equals(this)) {
					return Location.parse("INSIDE."+nfo.getId());
				//}
			}
			return null;	
		}
		int size=0;
		Body b=Universe.getBodyByLocation(this);
		if (b!=null) size=b.size;
		if (isSurface() && (s.equals("N") || s.equals("NORTH")) ) {
			if (row>0) return new Location(null,type,solar,planet,moon,sector,row-1,col,level,null);
		}
		if (isSurface() && (s.equals("S") || s.equals("SOUTH")) ){
			if (row<size*2-1) return new Location(null,type,solar,planet,moon,sector,row+1,col,level,null);
		}
		if (isSurface() && (s.equals("W") || s.equals("WEST")) ){
			int rc=col+sector*size*2-1;
			if (rc<0) rc=rc+size*2*4;
			return new Location(null,type,solar,planet,moon,rc/(size*2),row,rc%(size*2),level,null);
		}
		if (isSurface() && (s.equals("E") || s.equals("EAST")) ){
			int rc=col+sector*size*2+1;
			if (rc>size*2*4-1) rc=rc-size*2*4;
			return new Location(null,type,solar,planet,moon,rc/(size*2),row,rc%(size*2),level,null);
		}
		if (isFacility() && level==0 && (s.equals("U") || s.equals("UP") || s.equals("LAUNCH") || s.equals("ESCAPE")) ){
			return new Location(null,type,solar,planet,moon,-1,-1,-1,-1,null);
		}
		if (isSurface() && (s.equals("U") || s.equals("UP") || s.equals("LAUNCH") || s.equals("ESCAPE")) ){
			return new Location(null,type,solar,planet,moon,-1,-1,-1,-1,"");
		}
		if (isLevel() && (s.equals("U") || s.equals("UP")) ){
			return new Location(null,type,solar,planet,moon,sector,row,col,level-1,null);
		}
		if (isLevel() && level<9 && (s.equals("D") || s.equals("DOWN")) ){
			return new Location(null,type,solar,planet,moon,sector,row,col,level+1,null);
		}
		if (isSurface() && (s.equals("D") || s.equals("DOWN")) ){
			return new Location(null,type,solar,planet,moon,sector,row,col,1,null);
		}
		if (isOrbit() && (s.equals("D") || s.equals("DOWN") || s.equals("LAND") || s.equals("REENTRY") ) ){
			return new Location(null,type,solar,planet,moon,(int)(Math.random()*4),(int)(Math.random()*size*2),(int)(Math.random()*size*2),0,null);
		}
		if (isInside() && (s.equals("EXIT") || s.equals("LEAVE") )){
			nfo= Universe.getNFObjectById(fac);
			if (nfo==null) return null;
			return nfo.getLocation();
		}
		return null;
	}

	public Location getBodyLocation() {
		if (getPlanet()==-1) return null;
		if (moon.length() > 0) {
			return Location.parse(solar + "." + planet + "." + moon);
		}
		if (planet >= 0) {
			return Location.parse(solar + "." + planet);
		}
		return null;
	}


	public Location getOutsideFacilityLocation() {
		if (isFacility()) {
			return reference("EXIT");//Location.parse(loc.substring(0,loc.indexOf(".F")));
		}
		return this;
	}
	public Location getSurfaceLocation() {
		if (sector == -1) {
			return null;
		}
		String loc = this.loc;
		if (loc.indexOf(".L") > 0) {
			loc =loc.substring(0, loc.indexOf(".L"));
		}
		if (loc.indexOf(".F") > 0) {
			loc = loc.substring(0, loc.indexOf(".F"));
		}
		return Location.parse(loc);
	}

	public boolean isSolar() {return planet==-1;}
	public boolean isPlanet() {return planet!=-1 && moon.length()==0 && sector==-1 && fac.length()==0;}
	public boolean isMoon() {return planet!=-1 && moon.length()>0 && sector==-1 && fac.length()==0;}
	public boolean isOrbit() {return planet!=-1 && sector==-1 && fac.length()==0;}
	public boolean isSurface() {return sector!=-1 && row!=-1 && col!=-1 && level==0 && fac.length()==0;}
	public boolean isLevel() {return level!=0 && fac.length()==0;}
	public boolean isFacility() {return fac!=null && fac.length()>0 && fac.substring(0,1).equalsIgnoreCase("F");}
	public boolean isInside() {
		return fac!=null && fac.length()>0;
	}

	public boolean valid() {
//if facility at location okay
		if (isFacility()) {
			NFObject facNFO=Universe.getNFObjectById(fac);
			if (facNFO==null) {
				fac="";//fix it if I can
				return false;
			}
			if (facNFO instanceof Facility) {
				Facility fff=(Facility)facNFO;
				if (!loc.equalsIgnoreCase(fff.getLocation()+"."+fff.getId())) return false;
				return true;
			}
			return false;
		}
		if (isInside() && !isFacility()) {
			NFObject nfo=Universe.getNFObjectById(getInsideWhat());
			if (nfo==null) {
				return false;
			}
			return true;
		}
		Body b=Universe.getBodyByLocation(this);
		if (b==null) return false;
//check Body boundaries    
			if (row>=0 && b.getMaxRow()<row) return false;
 			if (col>=0 && b.getMaxCol()<col) return false;
//check depth boundary     
			if (level>0 && b.size<level) return false;
		return true;
	}

	public String getInsideWhat() {
		if (!isInside()) return null;
		return fac;
	}


	public int getSolar() {return solar;}
	public int getPlanet() {return planet;}
	public String getMoon() {return moon;}
	public int getSector() {return sector;}
	public int getRow() {return row;}
	public int getCol() {return col;}
	public int getLevel() {return level;}
	public String getFac() {if (fac.length()>0) return fac;else return null;}

	public void rebuild() {
		loc = "";
		if (type != null) {
			loc += type + ".";
		}
		if (type!=null && type.equalsIgnoreCase("INSIDE")) {loc="INSIDE."+fac;return;}
		if (solar >= 0) {
			loc += (solar<10?"0":"")+solar;
		}
		if (planet >= 0) {
			loc += "." + (planet<10?"0":"")+planet;
		}
		if (moon.length() >0) {
			loc += "."+moon;
		}
		if (sector >= 0) {
			loc += ".S" + sector;
		}
		//else {loc+=".00";}
		if (row >= 0) {
			loc += "." +(row<10?"0":"")+ row;
		}
		if (col >= 0) {
			loc += "." + (col<10?"0":"")+ col;
		}
		if (level >0) {
			loc += ".L" + (level<10?"0":"")+ level;
		}
		if (fac.length()>0) {
			loc += "." + fac;
		}
	}


	public String toString() {
		return loc;
	}


	public boolean equalsIgnoreCase(Location l) {
		if (loc==null || l==null) return false;
		return loc.equalsIgnoreCase(l.loc);
	}
	public boolean equals(Location l) {
		if (loc==null || l==null) return false;
		return loc.equalsIgnoreCase(l.loc);
	}




	public boolean contains(Location l) {
		return (l.loc.startsWith(loc) );
	}


	public static boolean adjacentTo(Location one, Location two) {
		if (two==null || one==null) return false;
//add invalid location check
		Body onep=Universe.getBodyByLocation(one);
		Body twop=Universe.getBodyByLocation(two);

		//if (one.isInroute() || two.isInroute()) return false;

		if (one.isInside()&&!one.isFacility()) {
			NFObject nfo=Universe.getNFObjectById(one.getInsideWhat());
			if (nfo==null) return false;
			if (nfo.getLocation().equals(two)) return true;
		}
		if (two.isInside() && !two.isFacility()) {
			NFObject nfo=Universe.getNFObjectById(two.getInsideWhat());
			if (nfo==null) return false;
			if (nfo.getLocation().equals(one)) return true;
		}

		//any Orbit to any Orbit in same Solar
		if (one.isOrbit() && two.isOrbit() && two.getSolar()==one.getSolar()) return true;

		//any Surface on same Planet & moon to Orbit()
		//from Orbit to any Surface on same Planet & Moon
		if (one.getSolar()==two.getSolar() && one.getPlanet()==two.getPlanet() && one.getMoon().equalsIgnoreCase(two.getMoon()) && one.isOrbit() && two.isSurface()) return true;
		if (one.getSolar()==two.getSolar() && one.getPlanet()==two.getPlanet() && one.getMoon().equalsIgnoreCase(two.getMoon()) && two.isOrbit() && one.isSurface()) return true;


		//from sufrace to level or level to level if same solar, planet, moon, sector
		//ADD FOR LEVEL MAX CHECK && one.getLevel()<onep.getMaxLevel() && two.getLevel()<twop.getMaxLevel()
		if (one.getSolar()==two.getSolar() && one.getPlanet()==two.getPlanet() && one.getMoon().equalsIgnoreCase(two.getMoon()) && one.getSector()==two.getSector() &&one.getRow()==two.getRow() && one.getCol()==two.getCol() &&((one.isLevel()|| one.isSurface()) && (two.isLevel() || two.isSurface() ))&& (one.getLevel()==two.getLevel()+1 || one.getLevel()==two.getLevel()-1) && one.getLevel()>=0 && two.getLevel()>=0) return true;

		//from Level() or Surface to fac if fac in same location && not in facility
		if (one.getSolar()==two.getSolar() && one.getPlanet()==two.getPlanet() && one.getMoon().equalsIgnoreCase(two.getMoon()) && one.getSector()==two.getSector() &&one.getRow()==two.getRow() && one.getCol()==two.getCol() &&one.getLevel()==two.getLevel() &&((one.isFacility() && !two.isFacility()) || (!one.isFacility() && two.isFacility()))) return true;

		//from one Surface() to next if (Row()+/-1==Row() || Col()+/-1 && Row()+/-1 =! Col()+/-1) && same Sector()
		if (onep!=null && twop!=null && one.getFac()==null && two.getFac()==null &&one.getCol()<onep.getMaxCol() && two.getCol()<twop.getMaxCol() && one.getRow()<onep.getMaxRow() &&one.getSolar()==two.getSolar() && one.getPlanet()==two.getPlanet() && one.getMoon().equalsIgnoreCase(two.getMoon()) && one.getSector()==two.getSector() && one.getRow()==two.getRow() && one.getRow()>=0 &&(one.getCol()==two.getCol()+1 || one.getCol()==two.getCol()-1) && one.getCol()>=0 && two.getCol()>=0 ) return true;
		if (onep!=null && twop!=null && one.getFac()==null && two.getFac()==null &&one.getRow()<onep.getMaxRow() && two.getRow()<twop.getMaxRow() && one.getCol()<onep.getMaxCol() &&one.getSolar()==two.getSolar() && one.getPlanet()==two.getPlanet() && one.getMoon().equalsIgnoreCase(two.getMoon()) && one.getSector()==two.getSector() && one.getCol()==two.getCol() && one.getCol()>=0 &&(one.getRow()==two.getRow()+1 || one.getRow()==two.getRow()-1) && one.getRow()>=0 && two.getRow()>=0 ) return true;

		//from fac to Orbit() if fac on Surface() of same Planet() ||from Orbit() to fac if fac on same Planet() && on Surface()
		if (one.getSolar()==two.getSolar() && one.getPlanet()==two.getPlanet() && one.getMoon().equalsIgnoreCase(two.getMoon()) && ((one.isOrbit() && two.isFacility() && two.getLevel()==0)||(two.isOrbit() && one.isFacility() && one.getLevel()==0) )) return true;
		if (one.loc.equalsIgnoreCase(two.loc)) return true;

		//from one Surface() to next if Sector() +/-1 && Col() =0 and Col() two =max
		if (onep!=null && twop!=null && one.getFac()==null && two.getFac()==null &&one.getSolar()==two.getSolar() && one.getPlanet()==two.getPlanet() && one.getMoon().equalsIgnoreCase(two.getMoon()) && one.getSector()!=two.getSector() &&one.getRow()==two.getRow() && one.getRow()>=0 && one.getLevel()==two.getLevel() &&
			((one.getCol()==0 && one.getSector()==0 && two.getCol()==twop.getMaxCol()-1 && two.getSector()==3 )||
			(one.getCol()==0 && one.getSector()==1 && two.getCol()==twop.getMaxCol()-1 && two.getSector()==0 )||
			(one.getCol()==0 && one.getSector()==2 && two.getCol()==twop.getMaxCol()-1 && two.getSector()==1 )||
			(one.getCol()==0 && one.getSector()==3 && two.getCol()==twop.getMaxCol()-1 && two.getSector()==2 )||

			(one.getCol()==onep.getMaxCol()-1 && one.getSector()==0 && two.getCol()==0 && two.getSector()==1 )||
			(one.getCol()==onep.getMaxCol()-1 && one.getSector()==1 && two.getCol()==0 && two.getSector()==2 )||
			(one.getCol()==onep.getMaxCol()-1 && one.getSector()==2 && two.getCol()==0 && two.getSector()==3 )||
			(one.getCol()==onep.getMaxCol()-1 && one.getSector()==3 && two.getCol()==0 && two.getSector()==0 ))
			) return true;


		return false;

	}

	public String getMoveTypeS(Location dest) {
		int t=getMoveType(dest);
		if (t==-1) return "BAD";
		if (t==UNLOAD) return "UNLOAD";
		if (t==LOAD) return "LOAD";
		if (t==INTERSOLAR) return "INTERSOLAR";
		if (t==INTERPLANET) return "INTERPLANET";
		if (t==ESCAPE) return "ESCAPE";
		if (t==REENTRY) return "REENTRY";
		if (t==SURFACE) return "SURFACE";
		if (t==SUBTER) return "SUBTER";
		return "UNKNOWN";
	}


	public Location nested() {
		if (!isInside()) return this;
		else {
			NFObject nfo=Universe.getNFObjectById(fac);
			if (nfo==null) return this;
			else return nfo.getLocation();
		}
	}

	public int getMoveType(Location dest) {
		if (!adjacentTo(this,dest)) return -1;

		Location l=this;

		//if (getSolar()!=dest.getSolar() && !isInside() && !dest.isInside()) {return INTERSOLAR;}


		if (isOrbit() && dest.isOrbit() ) {return INTERPLANET;}

		if (isSurface() && dest.isOrbit() ){return ESCAPE;}
		if (isOrbit() && dest.isSurface() ){return REENTRY;}

		if (isFacility() && nested().isSurface()&& dest.isOrbit() ){return ESCAPE;}
		if (isOrbit() && dest.isFacility() && dest.nested().isSurface() ){return REENTRY;}

		if (isSurface() && dest.isSurface()) {return SURFACE;}

		if (isLevel() || dest.isLevel()) {return SUBTER;}

		if (isInside() &&  !dest.isInside()) {return UNLOAD;}
		if (!isInside() && dest.isInside()) {return LOAD;}
		if (isInside() && dest.isInside()) {return LOAD;}
		return SURFACE;
	}


	public static Location parse(String s) {
		if (s == null) {
			return null;
		}
		while (s.indexOf("\"") >= 0) {
			s = s.substring(0, s.indexOf("\"")) + s.substring(s.indexOf("\"") + 1);
		}
		s = s.toUpperCase().trim();
		String loc = s;
		String type = null;
		int solar,planet,sector,row,col,level;
		String fac=null;
		String moon="";
		solar = planet = sector = row = col = level = -1;
		fac = null;
		if (s.startsWith("INSIDE.")) {
			type = "INSIDE";
			fac = s.substring(s.indexOf(".") + 1);
			//fix from nick name to real name
			NFObject nnnn=Universe.getNFObjectById(fac);
			if (nnnn!=null && !nnnn.getId().equals(fac)) fac=nnnn.getId();
			return new Location(loc, type, solar, planet, moon, sector, row, col, level, fac);
		}
		if (s.toUpperCase().indexOf(".F") > 0) {
			fac = s.substring(s.toUpperCase().indexOf(".F") + 1).trim();
			s = s.substring(0, s.toUpperCase().indexOf(".F")).trim();
		}
		try {
			if (s.toUpperCase().indexOf(".L") > 0) {
				level = Integer.parseInt(s.substring(s.toUpperCase().indexOf(".L") + 2).trim());
				s = s.substring(0, s.toUpperCase().indexOf(".L")).trim();
			}
			String bod = s;
			String sur = "";
			if (s.toUpperCase().indexOf(".S") > 0) {
				bod = bod.substring(0, bod.toUpperCase().indexOf(".S"));
				sur = s.substring(s.toUpperCase().indexOf(".S") + 2).trim();
			}
			String bn[] = Stuff.getTokens(bod, " .\t");
			if (bn.length > 0) {
				solar = Integer.parseInt(bn[0]);
				if (solar==0) solar=-1;
			}
			if (bn.length > 1) {
				planet = Integer.parseInt(bn[1]);
				if (planet==0) planet=-1;
			}
			if (bn.length > 2) {
				moon = bn[2].trim();;
			}
			String sn[] = Stuff.getTokens(sur, " .\t");
			if (sn.length > 0) {
				sector = Integer.parseInt(sn[0]);
				if (sector<0 || sector>3) return null;
			}
			if (sn.length > 1) {
				row = Integer.parseInt(sn[1]);
				if (level==-1) level=0;
			}
			if (sn.length > 2) {
				col = Integer.parseInt(sn[2]);
				if (level==-1) level=0;
			}
			return new Location(loc, type, solar, planet, moon, sector, row, col, level, fac);
		}
		catch (NumberFormatException NFE) {
			return null;
		}
	}


	public static void main(String s[]) {
		Universe u=new Universe();
		u.load();
		Location L1,L2;

System.out.println("good moves");//good cases
		L1=Location.parse("01.01");
		L2=Location.parse("01.02.00");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L1.getMoveTypeS(L2));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L2.getMoveTypeS(L1));

		L1=Location.parse("01.03");
		L2=Location.parse("01.03.01");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L1.getMoveTypeS(L2));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L2.getMoveTypeS(L1));

		L1=Location.parse("01.03");
		L2=Location.parse("01.07.02");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L1.getMoveTypeS(L2));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L2.getMoveTypeS(L1));

		L1=Location.parse("01.03");
		L2=Location.parse("01.03.s1.03.05");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L1.getMoveTypeS(L2));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L2.getMoveTypeS(L1));

		L1=Location.parse("01.03.s1.03.05");
		L2=Location.parse("01.03.s1.03.05.L1");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.03.s1.03.05.L1");
		L2=Location.parse("01.03.s1.03.05.L2");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.03.s1.03.05.L5");
		L2=Location.parse("01.03.s1.03.05.L4");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.03.s1.03.03");
		L2=Location.parse("01.03.s1.04.03");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.03.s1.00.01");
		L2=Location.parse("01.03.s1.01.01");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.03.s1.00.05");
		L2=Location.parse("01.03.s1.01.05");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.03.s1.01.00");
		L2=Location.parse("01.03.s1.01.01");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.03.s1.05.00");
		L2=Location.parse("01.03.s1.05.01");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.03.s0.00.00");
		L2=Location.parse("01.03.s3.00.63");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.03.s0.00.63");
		L2=Location.parse("01.03.s1.00.00");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.03.s1.00.63");
		L2=Location.parse("01.03.s2.00.00");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.03.s3.00.63");
		L2=Location.parse("01.03.s0.00.00");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

System.out.println("BAD ONLY BELOW");
//bad cases
		L1=Location.parse("01.03.s1.03.03");
		L2=Location.parse("01.03.s1.03.04");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.00");
		L2=Location.parse("01.02.00");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.01");
		L2=Location.parse("02.01");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.01.s1.03.04");
		L2=Location.parse("02.01.01");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.01.s1.03.04");
		L2=Location.parse("01.01.01");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.01.s1.03.04");
		L2=Location.parse("01.01.01");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.01");
		L2=Location.parse("01.01.s1.02.01.L4");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.01.s2.02.02");
		L2=Location.parse("01.01.s1.02.01");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.01.s2.02.02");
		L2=Location.parse("01.01.s2.02.04");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.01.s2.04.02");
		L2=Location.parse("01.01.s2.02.02");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));


		L1=Location.parse("01.03.s0.12.00");
		L2=Location.parse("01.03.s3.00.00");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.03.s3.14.00");
		L2=Location.parse("01.03.s3.13.00");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));


		L1=Location.parse("01.03.s0.00.12");
		L2=Location.parse("01.03.s3.00.00");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.03.s1.00.-1");
		L2=Location.parse("01.03.s1.01.-1");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.03.s1.-1.01");
		L2=Location.parse("01.03.s1.0.01");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));



		L1=Location.parse("01.03.s1.05.05");
		L2=Location.parse("01.03.s1.05.05");

		System.out.println("reference NORTH "+L1+" is "+L1.reference("NORTH"));
		System.out.println("reference SOUTH "+L1+" is "+L1.reference("SOUTH"));
		System.out.println("reference WEST "+L1+" is "+L1.reference("WEST"));
		System.out.println("reference EAST "+L1+" is "+L1.reference("EAST"));
		System.out.println("reference UP "+L1+" is "+L1.reference("UP"));
		System.out.println("reference DOWN "+L1+" is "+L1.reference("DOWN"));
		System.out.println("reference EXIT "+L1+" is "+L1.reference("EXIT"));
		L1=Location.parse("01.03");
		System.out.println("reference DOWN "+L1+" is "+L1.reference("DOWN"));
		L1=Location.parse("01.03.S2.02.03.L5");
		System.out.println("reference UP "+L1+" is "+L1.reference("UP"));
		L1=Location.parse("01.03.S2.00.00");
		System.out.println("reference WEST "+L1+" is "+L1.reference("WEST"));
		L1=Location.parse("01.03.S2.00.63");
		System.out.println("reference EAST "+L1+" is "+L1.reference("EAST"));
		L1=Location.parse("01.03.S0.00.00");
		System.out.println("reference WEST "+L1+" is "+L1.reference("WEST"));
		L1=Location.parse("01.03.S3.00.63");
		System.out.println("reference EAST "+L1+" is "+L1.reference("EAST"));

System.out.println("facility and robot tests");

		L1=Location.parse("01.03.01.s1.03.05.F14");
		L2=Location.parse("01.03.01.s1.03.05.F14");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L1.getMoveTypeS(L2));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L2.getMoveTypeS(L1));

		L1=Location.parse("01.03");
		L2=Location.parse("01.03.s1.03.05.F14");
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));

		L1=Location.parse("01.03.s1.03.05.L5");
		L2=Location.parse("01.03.s1.03.05.L5.F13");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.01");
		L2=Location.parse("01.03.s1.02.01.L5.F7");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.03.s1.03.05.F2");
		L2=Location.parse("01.03.s1.03.05.F3");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.01.s2.02.03.F2");
		L2=Location.parse("01.01.s2.02.02.F3");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("INSIDE.A4");
		L2=Location.parse("01.03.S1.03.05.F2");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("INSIDE.A1");
		L2=Location.parse("01.02");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		L1=Location.parse("01.04.A1");
		L2=Location.parse("01.04");
		System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
		System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));

		for (int mm=0;mm<s.length;mm++) {
			L1=Location.parse(s[mm]);
			System.out.println("s["+mm+"] ="+s[mm]+" = "+L1);
			for (int mmm=mm;mmm<s.length;mmm++) {
				L2=Location.parse(s[mmm]);
				System.out.println("s["+mmm+"] ="+s[mmm]+" = "+L2);
				System.out.println(L1+" is "+(adjacentTo(L1,L2)?"adjacentTo":"NOT adjacentTo")+" to "+L2+" type="+L2.getMoveTypeS(L1));
				System.out.println(L2+" is "+(adjacentTo(L2,L1)?"adjacentTo":"NOT adjacentTo")+" to "+L1+" type="+L1.getMoveTypeS(L2));
			}
		}
	}

}
