package aj.nf;

import java.util.*;
import java.io.*;
/**
 *
 *@author     judda 
 *@created    July 21, 2000 
 */
public class GenPlanet {
	String name, fileName;
	int size, numsect;
	double geoDiv = .25, thrmDiv = .25;
	boolean liquids = false, gasses = false;
	String resourceTypes = "";
	static double ROUNDIT=.5;
	
	public void giveBodyRandomRecources(Body b) {
		Vector v = Universe.getMarket().getRandomRecources();
		b.solids = v;
		b.liquids=new Vector();
		b.gasses=new Vector();
		for(int a = 0; a < v.size(); a++) {
			MarketItem mi = Universe.getMarket().getItemByName((String)v.elementAt(a));
			if(mi!=null && b.hasAtmosphere()) {
				if (mi.liquidTemp<b.maxtemp && mi.gasTemp>b.mintemp) {
					b.liquids.addElement(v.elementAt(a));
				}
				if (mi.gasTemp<b.maxtemp ) {
					b.gasses.addElement(v.elementAt(a));
				}
			}
		}
	}
	
	public GenPlanet() {}
	public void loadUniverse() {
		Universe u = new Universe();
		u.load();
	}

	public void generateAll(String s[]) {
		Vector v = (Vector)Universe.allBodies.clone();
		for(int a = 0; a < v.size(); a++) {
			Body B = (Body)v.elementAt(a);
			if(B.solid == false) continue;
			boolean found = false;
			for(int b = 0; b < s.length; b++) {
				if(B.getName().equalsIgnoreCase(s[b])) {
					found = true;
				}
			}
			if (!found && s.length!=0) {
				System.out.println("Dont do "+B.getName());
				continue;
			}
			if(s.length==0 && !found && B.getName().equalsIgnoreCase("Earth")) {
				System.out.println("Dont do Earth!");
				continue;
			}
			if(found || (s.length == 0)) {
				System.out.println("Generating planet " + B.getName());
				giveBodyRandomRecources(B);
				name = B.getName();
				size = B.size;
				numsect = B.numsect;
				fileName = B.fileName;
				liquids = B.liquids.size() > 0;
				gasses = B.gasses.size() > 0;
				makeResourceTypes(Universe.getMarket(), B);
				String result = gen(B);
				makeFile(result, fileName);
			}
		}
		Universe.saveBodies();
	}
	
	//public String getGeo(int row, int col, double div) {
		//if(Math.random() > div) {
			//return(Math.random() > div?"P":"p");
		//}
		//else {
			//if(Math.random() > div) {
				//return(Math.random() > div?"b":"B");
			//}
			//else {
				//return(Math.random() > div?"m":"M");
			//}
		//}
		////c,v,w (not generated)
	//}
	
	public String getThrm(int row, int col, double div) {
		//DIV NOT USED
		double thrm = 1.0 * row / (size * 2 - 1);
		if(thrm > .5) {
			thrm = 1 - thrm;
		}
		thrm *= 2;
		return"" + Math.max(1, Math.min((int)(thrm * 8 + 1 + Math.random() * 4 - 2), 9));
	}
	
	public String getRec(int row, int col) {
		double div=Universe.RECCHANCE;
		double mot=Universe.MOTHERCHANCE;
		if (size<3) div*=2;
		if(Math.random() > div || name.indexOf("Earth")>=0) {
			return".";
		}
		else {
			int len = resourceTypes.length();
			int choose = (int)(Math.random() * len);
			if(len == 0) {
				return".";
			}
			else {
				String r = resourceTypes.substring(choose, choose + 1);
				if(Math.random() > mot) {
					r = r.toLowerCase();
				}
				else {
					r = r.toUpperCase();
				}
				return r;
			}
		}
	}
	
	public void makeFile(String s, String fileName) {
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(new File(Main.DIRBODIES+fileName)));
			//while(s.indexOf("\n") >= 0) {
				//pw.println(s.substring(0, s.indexOf("\n")));
				//s = s.substring(s.indexOf("\n") + 1);
			//}
			pw.println(s);
			pw.close();
		}
		catch(IOException IOE) {
			System.out.println("MyError: unknown file writing error" + IOE);
		}
	}
	
	public void makeResourceTypes(Market m, Body B) {
		resourceTypes = "";
		Vector v = B.solids;
		for(int a = 0; a < v.size(); a++) {
			String s = (String)v.elementAt(a);
			MarketItem mi = m.getItemByName(s);
			if(mi != null) {
				for(int b = 0; b < mi.occuranceRate * 20; b++)resourceTypes += mi.getType();
			}
			else {
				System.out.println("Unknown element " + s + " please fix in " + B.getName());
			}
		}
		B.resourceTypes = resourceTypes;
	}

//if Body has tectonics then MORE MOUNTAINS else more Broken
	public String [][] genGeo (Body B) {
		String nn[][]=new String[size*2][size*2*4];
		for (int a=0;a<nn.length;a++) {
			for (int b=0;b<nn[0].length;b++) {
				nn[a][b]="P";
			}
		}
		//place mountins
		int nummount=(int)(Math.random()*size*size/6+1);
		for (int a=0;a<nummount;a++) {
			nn[(int)(Math.random()*size*2)][(int)(Math.random()*size*2*4)]="M";
		}
		nummount=(int)(Math.random()*size*size/2+2);
		for (int a=0;a<nummount;a++) {
			nn[(int)(Math.random()*size*2)][(int)(Math.random()*size*2*4)]="m";
		}
		int numbrok=(int)(Math.random()*size*size/6+1);
		for (int a=0;a<numbrok;a++) {
			nn[(int)(Math.random()*size*2)][(int)(Math.random()*size*2*4)]="B";
		}
		numbrok=(int)(Math.random()*size*size/2+2);
		for (int a=0;a<numbrok;a++) {
			nn[(int)(Math.random()*size*2)][(int)(Math.random()*size*2*4)]="b";
		}
		//System.out.println("Preview");
		//for (int a=0;a<nn.length;a++) {
			//for (int b=0;b<nn[0].length;b++) {
				//System.out.print(nn[a][b]);
			//}
			//System.out.println("");
		//}
		//System.out.println("");
		nn=softenIt(nn);
		nn=softenIt(nn);
		nn=softenIt(nn);
		nn=softenIt(nn);

		boolean liquids=B.liquids.size()>0;
//HERE HERE place liqiuds on surface
		if (liquids) {
			for (int a=0;a<nn.length;a++) {
				for (int b=0;b<nn[a].length;b++) {
					if (nn[a][b].equals("P")) {
						nn[a][b]="W";
					}
				}
			}
			for (int a=0;a<nn.length;a++) {
				for (int b=0;b<nn[a].length;b++) {
					if ( nn[a][b].equalsIgnoreCase("P") && (
						(a>0 && nn[a-1][b].equalsIgnoreCase("W")) ||
						(a<nn.length-1 && nn[a+1][b].equalsIgnoreCase("W")) ||
						(b>0 && nn[a][b-1].equalsIgnoreCase("W")) ||
						(b<nn[a].length-1 && nn[a][b+1].equalsIgnoreCase("W")) ) ) {
						nn[a][b]="w";
						a=-1;b=-1;break;
					}
				}
			}
		}
//TODO todo.  Add liquid to P with correct temp.
//   for all P at temp - change to W
//     for all p adjacent 4 connect to W become w
		return nn;
	}


	public String[][] softenIt(String nn[][]) {
		String fix[][]=new String[nn.length][nn[0].length];
		for (int a=0;a<nn.length;a++) {
			for (int b=0;b<nn[0].length;b++) {
				int mc=0,bc=0;
//-1 0
				if (a>0 && nn[a-1][b].equals("B")) bc+=10;
				if (a>0 && nn[a-1][b].equals("M")) mc+=10;
				if (a>0 && nn[a-1][b].equals("b")) bc+=1;
				if (a>0 && nn[a-1][b].equals("m")) mc+=1;
//-1 -1
				if (Math.random()>ROUNDIT) {
					if (a>0 && b>0 && nn[a-1][b-1].equals("B")) bc+=10;
					if (a>0 && b>0 && nn[a-1][b-1].equals("M")) mc+=10;
					if (a>0 && b>0 && nn[a-1][b-1].equals("b")) bc+=1;
					if (a>0 && b>0 && nn[a-1][b-1].equals("m")) mc+=1;
				}
//0 -1
				if (b>0 && nn[a][b-1].equals("B")) bc+=10;
				if (b>0 && nn[a][b-1].equals("M")) mc+=10;
				if (b>0 && nn[a][b-1].equals("b")) bc+=1;
				if (b>0 && nn[a][b-1].equals("m")) mc+=1;
//-1 +1
				if (Math.random()>ROUNDIT) {
					if (a>0 && b<nn[0].length-1 && nn[a-1][b+1].equals("B")) bc+=10;
					if (a>0 && b<nn[0].length-1 && nn[a-1][b+1].equals("M")) mc+=10;
					if (a>0 && b<nn[0].length-1 && nn[a-1][b+1].equals("b")) bc+=1;
					if (a>0 && b<nn[0].length-1 && nn[a-1][b+1].equals("m")) mc+=1;
				}
	//+1 0
				if (a<nn.length-1 && nn[a+1][b].equals("B")) bc+=10;
				if (a<nn.length-1 && nn[a+1][b].equals("M")) mc+=10;
				if (a<nn.length-1 && nn[a+1][b].equals("b")) bc+=1;
				if (a<nn.length-1 && nn[a+1][b].equals("m")) mc+=1;
//+1 +1
				if (Math.random()>ROUNDIT) {
					if (a<nn.length-1 && b<nn[a+1].length-1 && nn[a+1][b+1].equals("B")) bc+=10;
					if (a<nn.length-1 && b<nn[a+1].length-1 && nn[a+1][b+1].equals("M")) mc+=10;
					if (a<nn.length-1 && b<nn[a+1].length-1 && nn[a+1][b+1].equals("b")) bc+=1;
					if (a<nn.length-1 && b<nn[a+1].length-1 && nn[a+1][b+1].equals("m")) mc+=1;
				}
//0 +1
				if (b<nn[a].length-1 && nn[a][b+1].equals("B")) bc+=10;
				if (b<nn[a].length-1 && nn[a][b+1].equals("M")) mc+=10;
				if (b<nn[a].length-1 && nn[a][b+1].equals("b")) bc+=1;
				if (b<nn[a].length-1 && nn[a][b+1].equals("m")) mc+=1;
//+1 -1
				if (Math.random()>ROUNDIT) {
					if (a<nn.length-1 && b>0 && nn[a+1][b-1].equals("B")) bc+=10;
					if (a<nn.length-1 && b>0 && nn[a+1][b-1].equals("M")) mc+=10;
					if (a<nn.length-1 && b>0 && nn[a+1][b-1].equals("b")) bc+=1;
					if (a<nn.length-1 && b>0 && nn[a+1][b-1].equals("m")) mc+=1;
				}

				String p=nn[a][b];
				if (nn[a][b].equals("P")) {
					if (bc >= 10 && bc>mc)  {
						if (Math.random()>.15)	p="b";
						else p="B";
					}
					else if (mc >= 10 && mc>bc) {
						if (Math.random()>.15)	p="m";
						else p="M";
					}
					else if (bc>mc) p="b";
					else if (bc<mc) p="m";
					else if (bc>0 || mc>0) p="p";
					if (p.equals("b") && Math.random()>.50) {
						if (Math.random()>.3) p="p";
						else p="B";
					}
					if (p.equals("m") && Math.random()>.50) {
						if (Math.random()>.3) p="p";
						else p="M";
					}
				}
				fix[a][b]=p;
			}
		}
		return fix;
	}


	public String gen(Body B) {
		String s = "GoePhysical\n";
		String ss = "";
		String nn[][]=genGeo(B);
		for(int a = 0; a < nn.length ; a++) {
			String tt="";
			for(int b = 0; b<nn[a].length; b++) {
				tt += nn[a][b];
			}
			ss += tt + "\n";
		}
		s += ss + "END\n\nThermal\n";
		
		ss = "";
		for(int a = 0; a < size * 2; a++) {
			String tt = "";
			for(int b = 0; b < size * 2 * 4; b++) {
				tt += getThrm(a, b, thrmDiv);
			}
			ss += tt + "\n";
		}
		s += ss + "END\n\nResource\n";

		ss = "";
		for(int a = 0; a < size * 2; a++) {
			String tt = "";
			for(int b = 0; b < size * 2 * 4; b++) {
				tt += getRec(a, b);
			}
			ss += tt + "\n";
		}
		s += ss + "END\n";
		System.out.println("done");
		return s;
	}
	
	public static void main(String s[]) {
		GenPlanet g = new GenPlanet();
		g.loadUniverse();
		g.generateAll(s);
	}
}
