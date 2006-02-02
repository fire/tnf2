package aj.smug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Vector;

import aj.misc.Bnf;
import aj.misc.GmlPair;
import aj.misc.Stuff;
import aj.net.Send;

public class Player {
	static String BNFFILE="text.gml";
	static Bnf bnf=null;

	static int REPORTMAXCOL=60;
	static String CARGOSEP="`";//ie 12`Gold
	static String REPORTFILENAME="Report.txt";
	static String ORDERSFILE="Orders.txt";

	static int MAXTAX=500,MAXFINE=500;
	String name,pass,email;
	double money;
	double tax;
	Vector cargo,//cargo =#`name
		itemnames,//itemname only
		skills;//skill`#lv (not listed=0)
	String loc;
	int vp;
//skills (fight, xenospeak, bribe,pilot,shipweap,pickpocket,
//pref (fight,steal,cheat,rob,party,risk,polite
//stat (str,con,dex,int,wis  mec,tec
//damage (eng5%,hull5%,shiel10%)
//fame infamey (trade,pilot,smuggler,pirate)
//trade ++ (each trade, 10 for each trade>100K, 100 each >1M, -1 sell under 5, +1 sell >5)
//pilot ++ (each battle)
//pirate ++ (each battle started)
//smuggler ++ (each illegal sell)
//demerits (times cought, opponent killed)
//bounty (once demerits >10 bounty = pirate+smuggler*1.5^(demerits-10)
//FUEL
//if ship destroyed all ==0
	
	String log="";

	Vector orders=new Vector();

	public Player(String n,String p,String e,double m,double t, String l,Vector c,Vector i,int v,Vector sk) {
		if (bnf==null) {
			try {
				bnf=new Bnf(GmlPair.parse(new File(BNFFILE)));
			} catch (IOException ioe) {
				System.out.println("MyError: BAD BNF FILE");
				System.exit(0);
			}
		}
		skills=sk;
		vp=v;
		name=n;
		pass=p;
		email=e;
		money=m;
		loc=l;
		cargo=c;
		itemnames=i;
		tax=t;
	}

	public static Player parse(GmlPair g) {
		String name="EMPTY",pass="EMPTY",email="EMPTY";
		double money=0,tax=0;
		int vp=0;
		String loc="EMPTY";
		GmlPair n=g.getOneByName("name");
		if (n!=null) name=n.getString();
		n=g.getOneByName("pass");
		if (n!=null) pass=n.getString();
		n=g.getOneByName("email");
		if (n!=null) email=n.getString();
		n=g.getOneByName("loc");
		if (n!=null) loc=n.getString();
		n=g.getOneByName("money");
		if (n!=null) money=n.getDouble();
		n=g.getOneByName("tax");
		if (n!=null) tax=n.getDouble();
		n=g.getOneByName("vp");
		if (n!=null) vp=(int)n.getDouble();
		GmlPair gg[]=g.getAllByName("cargo");
		Vector cargo=new Vector();
		for (int a=0;a<gg.length;a++) {
			cargo.addElement(gg[a].getString());
		}
		Vector itemnames=new Vector();
		gg=g.getAllByName("itemname");
		for (int a=0;a<gg.length;a++) {
			itemnames.addElement(gg[a].getString());
		}
		gg=g.getAllByName("skill");
		Vector skills=new Vector();
		for (int a=0;a<gg.length;a++) {
			skills.addElement(gg[a].getString());
		}
		return new Player(name,pass,email,money,tax,loc,cargo,itemnames,vp,skills);
	}

	public GmlPair toGml() {
		Vector v=new Vector();
		GmlPair g=new GmlPair("name",name);
		v.addElement(g);
		g=new GmlPair("pass",pass);
		v.addElement(g);
		g=new GmlPair("email",email);
		v.addElement(g);
		g=new GmlPair("loc",loc);
		v.addElement(g);
		g=new GmlPair("money",money+"");
		v.addElement(g);
		g=new GmlPair("tax",tax+"");
		v.addElement(g);
		g=new GmlPair("vp",vp+"");
		v.addElement(g);
		for (int a=0;a<cargo.size();a++) {
			g=new GmlPair("cargo",(String)cargo.elementAt(a));
			v.addElement(g);
		}
		for (int a=0;a<itemnames.size();a++) {
			g=new GmlPair("itemname",(String)itemnames.elementAt(a));
			v.addElement(g);
		}
		for (int a=0;a<skills.size();a++) {
			g=new GmlPair("skill",(String)skills.elementAt(a));
			v.addElement(g);
		}
		g=new GmlPair("Player",v);
		return g;
	}


	public void readOrders() {
		try {
			File f=new File(name+ORDERSFILE);
			if (f.exists()) {
				orders=new Vector();
				BufferedReader br=new BufferedReader(new FileReader(name+ORDERSFILE));
				while (true) {
					String s=br.readLine();
					if (s==null) break;
					orders.addElement(s.trim());
				}
				br.close();
			}
		} catch (IOException ioe) {
			System.out.println("MyError: cannot read orders");
		}
	}
	public void saveOrders(Vector v) {
		try {
			PrintWriter pw=new PrintWriter(new FileWriter(name+ORDERSFILE));
			for (int a=0;a<v.size();a++) {
				String s=(String)v.elementAt(a);
				pw.println(s.trim());
			}
			pw.flush();
			pw.close();
		} catch (IOException ioe) {
			System.out.println("MyError: cannot save orders");
		}
	}

	public void saveReport() {
//write header
String header="Smuggler report for "+name+"\n";
	header+="Password: "+pass+"\n";
	header+="Email: "+email+"\n";
	header+="Location: "+loc+"\n";
	header+="Money: "+Stuff.money(money,0)+"\n";
	header+="Taxes: "+Stuff.money(tax,0)+"\n";
	header+="Victor Points: "+vp+"\n";
	header+="Cargo: ";
	if (cargo.size()>0) header+=cargo.elementAt(0);
	for (int a=1;a<cargo.size();a++) header+=", "+cargo.elementAt(a);
	header+="\nItems: ";
	if (itemnames.size()>0) header+=itemnames.elementAt(0);
	for (int a=1;a<itemnames.size();a++) header+=", "+itemnames.elementAt(a);
	header+="\nSkills: ";
	if (skills.size()>0) header+=skills.elementAt(0);
	for (int a=1;a<skills.size();a++) header+=", "+skills.elementAt(a);

	header+="\n";
	log=header+log;
		try {
			PrintWriter pw=new PrintWriter(new FileWriter(name+REPORTFILENAME));
			while (log.length()>0) {
				if (log.indexOf("\n")>=0) {
					String w=log.substring(0,log.indexOf("\n"));
					log=log.substring(log.indexOf("\n")+1);
					while (w.length()>REPORTMAXCOL) {
						String ww=w.substring(0,60);
						w=w.substring(60);
						if (w.indexOf(" ")>=0) {
							ww+=w.substring(0,w.indexOf(" ")).trim();
							w=w.substring(w.indexOf(" ")+1).trim();
						}
						pw.println(ww);
					}
					pw.println(w);
				}
				else {
					pw.println(log);
					log="";
				}
			}
			pw.flush();
			pw.close();
		} catch (IOException IOE) {
			System.out.println("MyError: cannot save report");
		}
	}

	public void mailReport() {
		try {
			BufferedReader br=new BufferedReader(new FileReader(name+REPORTFILENAME));
			String s=br.readLine();
			while (s!=null) {
				log+=s+"\n";
				s=br.readLine();
			}
			br.close();
			log="SUBJECT: REPORT for player "+name+"\n"+log;
			log="TO: "+email+"\n"+log;
			log="FROM: "+SmugMain.gameEmail+"\n"+log;
			Send send=new Send();
			send.read(new BufferedReader(new StringReader(log)));
			send.deliver();
		} catch (IOException IOE) {
			System.out.println("MyError: cannot mail report");
		}
	}


	public boolean doneOrders() {return orders.size()==0;}

	public void execOrder(SmugMain m) {
		if (orders.size()==0) return;
		String c=(String)orders.elementAt(0);
		c=c.trim();
		orders.removeElementAt(0);
		report("");
		if (c.startsWith("@") || c.startsWith("#") || c.startsWith(";")) {
			report("NOTE: "+c);
			return;
		}
		report("EXECUTING ORDER: "+c);
		if (c.toUpperCase().startsWith("BUY")) {
/*
startBuy();//you leav ship to buy stuff
interruptCheck();//attacked,robbed,steal,gamble,etc
doBuy();//buy/sell
checkbuyevent();//rippoff/swindle
interruptCheck();//attacked,robbed,steal,gamble,etc
wrapup();//vp
*/
			doBuy(m,c);
		}
		else if (c.toUpperCase().startsWith("SELL")) {
			doSell(m,c);
		}
		else if (c.toUpperCase().startsWith("LAUNCH")) {
			Planet p= m.getPlanetAtLoc(loc);
			if (!loc.toUpperCase().endsWith("S")) {report("BAD ORDERS NOT ON PLANET SURFACE: "+c);return;}
			launch(p);
			spaceEventCheck(m);
		}
		else if (c.toUpperCase().startsWith("LAND")) {
			Planet p= m.getPlanetAtLoc(loc);
			if (!loc.toUpperCase().endsWith("O") || p==null) {report("BAD ORDERS NOT IN ORBIT: "+c);return;}
			land(p);
			planetEventCheck(m);
		}
		else if (c.toUpperCase().startsWith("MOVE")) {
			Planet p= m.getPlanetAtLoc(loc);
			String t[]=Stuff.getTokens(c," \t");
			if (loc.toUpperCase().endsWith("S")) {report("BAD ORDERS NOT IN ORBIT: "+c);return;}
			if (t.length==2) {
				Planet pp=m.getPlanetByName(t[1]);
				Planet ppp=m.getPlanetAtLoc(t[1]);
				if (pp==null && ppp!=null) pp=ppp;
				if (pp!=null && p!=null) {
					String r=getText("<leaveplanet> <arriveplanet>");
					r=replace(r,"$fromplanetname",p.name);
					r=replace(r,"$toplanetname",pp.name);
					report(r);
					loc=pp.loc+"xO";
				}
				else if (p==null && pp!=null) {
					String r=getText("<leaveplanet> <arrivedeepspace>");
					r=replace(r,"$fromplanetname",p.name);
					report(r);
					loc=t[1];//pp.loc+"xO";
				}
				else if (p!=null && validLoc(t[1])) {
					String r=getText("<leavedeepspace> <arriveplanet>");
					r=replace(r,"$toplanetname",pp.name);
					report(r);
					loc=pp.loc+"xO";
				}
				else if (p==null && pp==null) {
					String r=getText("<leavedeepspace> <arrivedeepspace>");
					report(r);
					loc=t[1];
				}
				spaceEventCheck(m);
			}
			else {report("BAD ORDERS INVALID MOVE TYPE: "+c);return;}
			
		}
	}
	
	public boolean validLoc(String s) {
		String t[]=Stuff.getTokens(s,"x");
		if (t.length!=2) return false;
		try {
			int x=Integer.parseInt(t[0]);
			x=Integer.parseInt(t[1]);
			return true;
		} catch (NumberFormatException NFE){return false;}
	}

	public void doSell(SmugMain m,String c) {
//must have "SELL PRD AMNT"
		c=c.substring(4);//cut off BUY
		c=Stuff.superTrim(c);
		if (c.indexOf(" ")<0) {
			report("BAD ORDER MISSING ARGS: "+c);
			return;
		}
		String count=c.substring(c.lastIndexOf(" "),c.length()).trim();
		String prodName=c.substring(0,c.lastIndexOf(" "));
		
		Planet p= m.getPlanetAtLoc(loc);
//must be on surface
		if (!loc.toUpperCase().endsWith("S")) {
			report("BAD ORDERS NOT ON SURFACE: "+c);
			return;
		}
//PRD must be valid
		Product prd=m.getProductByName(prodName);
		if (prd==null) {
			report("BAD ORDER NOSUCH PRODUCT: "+prodName);
			return;
		}
//NUM must be valid
		int num=0;
		try {
			num=Integer.parseInt(count);
		} catch (NumberFormatException NFE) {
			report("BAD ORDERS NON NUMBER: "+count);
			return;
		}
//must have num in cargo
		if (!cargoCheck(prd.name,num)) {
			report("BAD ORDERS DO NOT HAVE ENOUGH TO SELL: "+c);
			return;
		}
		double mod=p.getBaseMod(prd.base)+Math.random()-.5;
		double totalcost=num*prd.cost*Math.pow(2,(5-mod)/5);
//can trade on planet
		String r=getText("<sellcargo>");
		r=replace(r,"$planetname",p.name);
		r=replace(r,"$productname",prd.name);
		r=replace(r,"$num",num+"");
		r=replace(r,"$eachcost",""+Stuff.money(totalcost/num,0));
		r=replace(r,"$totalcost",""+Stuff.money(totalcost,0));
		report(r);
		removeCargo(num,prd.name);
		money+=totalcost;
		planetEventCheck(m);
	}

	public void doBuy(SmugMain m,String c) {
//must have "BUY PRD AMNT"
		c=c.substring(3);//cut off BUY
		c=Stuff.superTrim(c);
		if (c.indexOf(" ")<0) {
			report("BAD ORDER MISSING ARGS: "+c);
			return;
		}
		String count=c.substring(c.lastIndexOf(" "),c.length()).trim();
		String prodName=c.substring(0,c.lastIndexOf(" "));
		
		Planet p= m.getPlanetAtLoc(loc);
//must be on surface
		if (!loc.toUpperCase().endsWith("S")) {
			report("BAD ORDERS NOT ON SURFACE: "+c);
			return;
		}
//PRD must be valid
		Product prd=m.getProductByName(prodName);
		if (prd==null) {
			report("BAD ORDER NOSUCH PRODUCT: "+prodName);
			return;
		}
//NUM must be valid
		int num=0;
		try {
			num=Integer.parseInt(count);
		} catch (NumberFormatException NFE) {
			report("BAD ORDERS NON NUMBER: "+count);
			return;
		}
//must have funds
		double mod=p.getBaseMod(prd.base)+Math.random()*2-1;
		double totalcost=num*prd.cost*mod/5;
		if (totalcost>money) {
			report("BAD ORDER NOT ENOUGH FUNDS: "+Stuff.money(totalcost,0)+" greater than "+Stuff.money(money,0)+" "+c);
			return;
		}
//have room in cargobay
//can trade on planet
		String r=getText("<purchasecargo>");
		r=replace(r,"$planetname",p.name);
		r=replace(r,"$productname",prd.name);
		r=replace(r,"$num",num+"");
		r=replace(r,"$eachcost",""+Stuff.money(totalcost/num,0));
		r=replace(r,"$totalcost",""+Stuff.money(totalcost,0));
		report(r);
		addCargo(num,prd.name);
		money-=totalcost;
		planetEventCheck(m);
	}

	public boolean skillCheck(String s) {
		return Math.random()>.5;
	}
	public String getRobbedItem(SmugMain m) {
		Vector v=new Vector();
		for (int a=0;a<itemnames.size();a++) {
			String s=(String)itemnames.elementAt(a);
			Item i=m.getItemByName(s);
			if (i.stealable()) v.addElement(s);		
		}
		if (v.size()==0) return null;
		else return (String)v.elementAt((int)(Math.random()*v.size()));
	}
	

	public void planetEventCheck(SmugMain m) {
		//check risk level
		if (Math.random()>0) {
			report("");
			//choose event type
			String r="";
//robbed requires have items
//fight gun requires guns on planet and have gun
//fight weap requires weap on planet and have weap
int taxtype=0,finetype=1,stealtype=2,robbedtype=3,fighttype=4;
			int choose=(int)(Math.random()*5);
			double cost;
			switch (choose) {
				case 0: 
					r=getText("<PlanetEventtax>");
					cost=Math.random()*MAXTAX;
					r=replace(r,"$amount",Stuff.money(cost,0));
					//check for reduce and vps
					tax=tax+cost;
					r=replace(r,"$totaltax",Stuff.money(tax,0));
					report(r);
					break;
				case 1: 
					r=getText("<PlanetEventfine>");
					cost=Math.min(money,Math.random()*MAXFINE);
					r=replace(r,"$amount",Stuff.money(cost,0));
					//check for reduce and vps
					money-=cost;
					report(r);
					break;
				case 2: 
			//skill check for fail
					Item i=m.getRandomItem();
					if (!skillCheck("steal")) {
						r=getText("<PlanetEventstealfail>");
					}
					else {
						r=getText("<PlanetEventsteal>");
						if (itemnames.contains(i.name)) {
							r+="  "+getText("<dontneedit>");
						}
						else {
							itemnames.addElement(i.name);
						}
					}
					r=replace(r,"$ITEM",i.name);
					report(r);
					break;
				case 3: 
					String si=getRobbedItem(m);
	//check for no items
	//check for robber failed and VP
					r=getText("<PlanetEventrobbed>");
					if (si==null) r=getText("<PlanetEventrobbednone>");
					else if (skillCheck("robbed")) {
						r=getText("<PlanetEventrobbedfail>");	
					}
					else {
						itemnames.removeElement(si);
					}
					r=replace(r,"$item",si);
					report(r);
					break;
				case 4: 
					//check battletype effectivness
					report("battles not done");
					//hand fight, weap fight, gun fight
					//check bounty
					break;
			}

		}
	}


	boolean checkIllegalCargo(SmugMain m){
		Vector v=m.getIllegalCargo();
		for (int a=0;a<v.size();a++) {
			Product p=(Product)v.elementAt(a);
			if (cargoCheck(p.name,1)) return true;
		}
		return false;
	}
	boolean checkSafeIllegalBase(SmugMain m){
//get all illegal cargo
//get all bases for illegal cargo carried
		String ilc="";//illegal base carried
 		Vector v=m.getIllegalCargo();
		for (int a=0;a<v.size();a++) {
			Product p=(Product)v.elementAt(a);
			if (cargoCheck(p.name,1)) ilc+=" "+p.base;
		}
//get all legalcargo
//get all bases for legal cargo
 		v=m.getLegalCargo();
		for (int a=0;a<v.size();a++) {
			Product p=(Product)v.elementAt(a);
			if (cargoCheck(p.name,1) ) {
				while (ilc.indexOf(p.base)>=0) {
					ilc=ilc.substring(0,ilc.indexOf(p.base))+" "+
ilc.substring(ilc.indexOf(p.base)+p.base.length());
				}
			}
		}	
		if (ilc.trim().length()>0) return false;
		else return false;
	}
 	int seiseCargoCount(SmugMain m){
		int count=0;
		Vector v=m.getIllegalCargo();
		for (int a=0;a<v.size();a++) {
			Product p=(Product)v.elementAt(a);
			count+=cargoCount(p.name);
		}
		return count;
	}
	double seiseCargoValue(SmugMain m){
		double value=0;
		Vector v=m.getIllegalCargo();
		for (int a=0;a<v.size();a++) {
			Product p=(Product)v.elementAt(a);
			int count=cargoCount(p.name);
			value+=count*p.cost;
		}
		return value;
	}
	void seiseCargo(SmugMain m){
		Vector v=m.getIllegalCargo();
		for (int a=0;a<v.size();a++) {
			Product p=(Product)v.elementAt(a);
			int count=cargoCount(p.name);
			removeCargo(count,p.name);
		}
	}

	public void spaceEventCheck(SmugMain m){
//overall risk
//personal risk
//check if risk occured
		if (Math.random()>0) {
			report("");
			String r="";
			int attack=0,custom=1,damage=2;
			int choose=(int)(Math.random()*3);
			switch (choose) {
				case 0:
					//check shippowerratings
					r="*"+getText("<spacebattlewin>");
					r+="\n*"+getText("<spoilsreceived>");
						//check for bounty
					r+="\n*"+getText("<spacebattlelose>");
					r+="\n*"+getText("<spoilstaken>");
					r+="\n*"+getText("<spacebattledamage>");
					r+="\n*"+getText("<spacebattledestroyedescape>");
					r+="\n*"+getText("<spacebattledestroyeddie>");
					r=replace(r,"$item","new");
					break;
				case 1:
					boolean haveit=checkIllegalCargo(m);
					boolean hidingit=checkSafeIllegalBase(m);
					boolean skill=skillCheck("smuggle");
					boolean skill2=skillCheck("smuggle");
					boolean avoid=haveit && (skill || (skill2 && hidingit));
					//check illegal cargo
					//check %illegal %bas
					//check skill (avoid)
					if (!haveit) 
						r=getText("<spaceeventcustomspass>");
					else if (avoid){
						r=getText("<spaceeventcustomsmiss>");
					}
					else  {
						r=getText("<spaceeventcustomsfail>");
						boolean law=skillCheck("law");
						boolean chr=skillCheck("chr");
						int num=seiseCargoCount(m);
						double fine=Math.min(seiseCargoValue(m),money);
						seiseCargo(m);
						r+="  "+getText("<customsseisure>");
						if (!chr) {
							r+="  "+getText("<customsseisurefine>");
							money-=fine;
						}
						else if (!law) {
							r+="  "+getText("<customsseisurefineavoid>");
							fine=fine*Math.random();
							money-=fine;
						}
						else  {
							r+="  "+getText("<customsseisurenofine>");
						}
						r=replace(r,"$fine",Stuff.money(fine,0));
						r=replace(r,"$numcargo",num+"");
					}
					break;
				case 2:
					//check skill
					r="*"+getText("<spaceeventdamage>");
					r=replace(r,"$item","new");
					break;	
				}
			report(r);	
		}
	}

	public boolean cargoCheck(String na,int num){
		boolean found=false;
		for (int a=0;a<cargo.size();a++) {
			String s=(String)cargo.elementAt(a);
			String t[]=Stuff.getTokens(s,CARGOSEP);
			if (t.length<2) {
				System.out.println("MyError: Bad cargo format, cargo dropped:<"+s+">");
				cargo.removeElementAt(a);a--;
			}
			else if (t[1].equalsIgnoreCase(na)) {
				return Integer.parseInt(t[0])>=num;
			}
		}
		return false;
	}
	public int cargoCount(String na) {
		for (int a=0;a<cargo.size();a++) {
			String s=(String)cargo.elementAt(a);
			String t[]=Stuff.getTokens(s,CARGOSEP);
			if (t.length<2) {
				System.out.println("MyError: Bad cargo format, cargo dropped:<"+s+">");
				cargo.removeElementAt(a);a--;
			}
			else if (t[1].equalsIgnoreCase(na)) {
				return Integer.parseInt(t[0]);
			}
		}
		return 0;
	}

	public void addCargo(int n,String na) {
		boolean found=false;
		for (int a=0;a<cargo.size();a++) {
			String s=(String)cargo.elementAt(a);
			String t[]=Stuff.getTokens(s,CARGOSEP);
			if (t.length<2) {
				System.out.println("MyError: Bad cargo format, cargo dropped:<"+s+">");
				cargo.removeElementAt(a);a--;
			}
			else if (t[1].equalsIgnoreCase(na)) {
				n=n+Integer.parseInt(t[0]);
				cargo.removeElementAt(a);
				cargo.addElement(n+CARGOSEP+na);
				found=true;
				break;
			}
		}
		if (!found) {cargo.addElement(n+CARGOSEP+na);}
	}
	public void removeCargo(int n,String na) {
		boolean found=false;
		for (int a=0;a<cargo.size();a++) {
			String s=(String)cargo.elementAt(a);
			String t[]=Stuff.getTokens(s,CARGOSEP);
			if (t[1].equals(na)) {
				cargo.removeElementAt(a);
				n=Integer.parseInt(t[0])-n;
				if (n>0)
					cargo.addElement(n+CARGOSEP+na);
				found=true;
				break;
			}
		}
		if (!found) {cargo.addElement(n+CARGOSEP+na);}
	}


	public void launch(Planet p) {
		String r=getText("<launch>");
		r=replace(r,"$planetname",p.name);
		report(r);
		loc=p.loc+"xO";
	}
	public void land(Planet p) {
		String r=getText("<landing>");
		r=replace(r,"$planetname",p.name);
		report(r);
		loc=p.loc+"xS";
	}
	public void moveToDeepSpace(Planet p,String l) {
		String t[]=Stuff.getTokens(l,"x");
		int x=0,y=0;
		try {
			x=Integer.parseInt(t[0]);
			y=Integer.parseInt(t[1]);
		} catch (NumberFormatException NFE){}
		String r=getText("<moveToDeepSpace>");
		r=replace(r,"$fromplanetname",p.name);
		report(r);
		loc=l;
	}
	public void moveFromDeepSpace(String l,Planet pp) {
		String t[]=Stuff.getTokens(l,"x");
		int x=0,y=0;
		try {
			x=Integer.parseInt(t[0]);
			y=Integer.parseInt(t[1]);
		} catch (NumberFormatException NFE){}
		String r=getText("<moveFromDeepSpace>");
		r=replace(r,"$toplanetname",pp.name);
		report(r);
		loc=pp.loc+"xO";
	}

	public void report(String s) {
		log+=s+"\n";
	}

	public static String getText(String s) {
		if (bnf==null) {System.out.println("MyError: BNF not defined");return "";}
		if (s.indexOf("<")<0 || s.indexOf(">")<s.indexOf(">")) {
			System.out.println("MyError: Bad Bnf request");
			return "";
		}
		return bnf.process(s);
	}

//case insensitive search and replace
	public String replace(String orig,String find,String next){
		if (next.length()==0) {
			System.out.println("missing replacement for "+find+" in "+orig);
		}
		while (orig.toUpperCase().indexOf(find.toUpperCase())>=0) {
			orig=orig.substring(0,orig.toUpperCase().indexOf(find.toUpperCase()))+next+orig.substring(orig.toUpperCase().indexOf(find.toUpperCase())+find.length());
		}
		return orig;
	}

	public static void main(String s[]) {
		Player p=new Player("test","test","test@nowhere.com",500,500,"5x5",new Vector(),new Vector(),0,new Vector());	
		System.out.println(Player.getText("<purchasecargo>"));
		System.out.println(Player.getText("<A>"));

		String r=getText("<purchasecargo>");
		r=p.replace(r,"$planetname","Earth");
		r=p.replace(r,"$productname","Raw Compounds");
		r=p.replace(r,"$num","5");
		r=p.replace(r,"$eachcost","12.0");
		r=p.replace(r,"$totalcost","60.0");
		System.out.println(r);
	}

}
