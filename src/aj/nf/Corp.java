package aj.nf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Vector;

/**
 *
 *@author     judda 
 *@created    July 21, 2000 
 */
public class Corp {

	static double MAXTAXPEN=.03;
	static double MINTAXPEN=.01;
	static double TAXPENELTY=200000;//amount taxes due with no penelty
	static double TAXTOP=1000000;
	static double MININTERESTRATE=.02;
	static double MAXINTERESTRATE=.04;
	static double RESEARCHCOST=50000;
	static int MAXTRANS=20;
	static int MINTECHCOST=3;//mininum required research turns for tech
	static int VARTECHCOST=3;//min+var = max

	static double FUNCSRATE=1.1,CARGORATE=1.1,ENDURRATE=1.1,SIZERATE=1.1;//  cost=cost*funrate^numberfuns

	Location home;
	String name = "";
	String tick = "";
	String email = "";
	String password = "";
	String owner = "";
	String corpReport = "NONE";
	String itMarketReport = "NONE";
	double cash = 0;
	double tax = 0;
	double loan = 0;
	double startval=-1;	
	Vector projects=new Vector();
	boolean verbose=true;
	boolean active=false;
	double maxContract=200000;//max research contract pay
	double loanLimit=0;
	double maxMegawatts=0;
	double maxFood=0;
	double maxOxygen=0;
	double maxWater=0;
	double maxHuman=0;
	double setMegawatts=-1;
	double setFood=-1;
	double setOxygen=-1;
	double setWater=-1;
	double setHuman=-1;


	public double getMaxFee(String s) {
		if (s.equalsIgnoreCase("MegaWatts")) return maxMegawatts;
		if (s.equalsIgnoreCase("Oxygen")) return maxOxygen;
		if (s.equalsIgnoreCase("Water")) return maxWater;
		if (s.equalsIgnoreCase("Food")) return maxFood;
		if (s.equalsIgnoreCase("HumanLabor")) return maxHuman;
		return 0;
	}
	public double getSetFee(String  s) {
		if (s.equalsIgnoreCase("MegaWatts")) return setMegawatts;
		if (s.equalsIgnoreCase("Oxygen")) return setOxygen;
		if (s.equalsIgnoreCase("Water")) return setWater;
		if (s.equalsIgnoreCase("Food")) return setFood;
		if (s.equalsIgnoreCase("HumanLabor")) return setHuman;
		return -1;
	}
	public double getAvailableLoan() {
		double max= Math.max(0,(getAssetValue()-tax)/2-loan);
		if (loanLimit<=0) return max;
		if (max>loanLimit) return loanLimit;
		return max;
	}

	public double getCanAfford() {
		if (getAvailableLoan()>0) return cash+getAvailableLoan();
		else return cash;
	}

	public boolean canAfford(double x) {
		if (x<getCanAfford()) return true;
		return false;
	}

	public void makePayment(double x) {
		if (cash>=x) cash=cash-x;
		else if (cash<x) {
			x=x-cash;
			cash=0;
			loan+=x;
		}
	}

	public void payTax() {//tax paied at beginning of turn
		double total=0;
		int rc=0,fc=0;
		Vector v = Universe.getNFObjectsByTick(tick);
		for (int a=0;a<v.size();a++) {
			NFObject nfo=(NFObject)v.elementAt(a);
			if (nfo.getLocation().toString().startsWith("01.03.S")) {
				if (nfo instanceof Facility && !(nfo.getLocation()+"."+nfo.getId()).equalsIgnoreCase(home.toString()) ) {
					fc++;
					total+=50000*fc;
					System.out.println("DEBUG: TAX for "+nfo.getId());
				}
				if (nfo instanceof Active && !nfo.getLocation().equalsIgnoreCase(home)) {
					rc++;
					total+=10000*rc;
					if (Main.DEBUG) System.out.println("DEBUG: TAX for "+nfo.getId());
				}
			}
		}
		if (total>0) {
			String s= "Taxes accesed at "+Stuff.money(total,2)+" on ";
			if (fc>0) 
				s+=fc+" facilit"+(fc==1?"y":"ies");
			if (rc>0 && fc>0) 
				s+=" and ";
			if (rc>0) 
				s+=rc+" activ"+(rc==1?"e":"ies");
			addReport(s+"\n");
			tax+=total;
		}
	}

	public void payInterest() {
		double cost=loan*getInterestRate();
		if (cost>0) {
			addReport("Interest charges accrued of "+Stuff.money(cost,2)+"\n");
			loan+=cost;
		}
		cost=tax*getTaxPenelty();
		if (cost>0) {
			addReport("Tax penlty charges accrued of "+Stuff.money(cost,2)+"\n");
			tax+=cost;
		}
	}

	public double getTaxPenelty() {
		if (tax<TAXPENELTY) return 0;
		return Math.min(MAXTAXPEN,Math.max(MINTAXPEN,(tax-TAXPENELTY)/TAXTOP*MAXTAXPEN));
	}
	public double getInterestRate() {
		if (loan==0) return 0;
		double max=(getAssetValue()-tax)/2;
		if (max==0) max=1;
		if (loan>max) return MAXINTERESTRATE;
		return Math.max(MININTERESTRATE,loan/max*MAXINTERESTRATE);
	}
	
	public boolean isSysop() {
		if (owner.toLowerCase().indexOf("sysop access only") >= 0) {
			return true;
		}
		return false;
	}

	public String getRankReport() {
		if (isSysop() || tick.equals("XXX")) return null;
		double prof=getNetValue()-startval;
		double totval=getNetValue();
		int r=0,s=0,f=0,p=0,t=0;
		Vector v = Universe.getNFObjectsByTick(tick);
		for (int a=0;a<v.size();a++) {
			NFObject n=(NFObject)v.elementAt(a);
			if (n instanceof Active ){
				if (((Active)n).isShip()) s++;
				else r++;
			}
			if (n instanceof Facility ) {
				f++;
			}
			if (n instanceof Prefab ) {
				p++;
			}
			if (n instanceof StockPile ) {
				t=t+n.getMass();	
			}
		}
		String m=Stuff.money(cash,2)+"                    ";
		m=m.substring(0,15);
		String as=Stuff.money(getAssetValue(),2)+"                    ";
		as=as.substring(0,15);
		String tv=Stuff.money(totval,2)+"                    ";
		tv=tv.substring(0,15);
		String pr=Stuff.money(prof,2)+"                    ";
		pr=pr.substring(0,15);

		return tick+"  "+m+" "+as+" "+tv+" "+pr+" "+(r<10?" ":"")+r+" "+(s<10?" ":"")+s+" "+(f<10?" ":"")+f+" "+(p<10?" ":"")+p+" "+(t<1000?" ":"")+(t<100?" ":"")+(t<10?" ":"")+t+" "+(active?"A":"N");
	}

	Vector orders = new Vector();

	String report = "";

	Vector defs = new Vector();

	String additionalReport = "";
	long lastlogin=0;
	String lastloginDate="";
	long signedup=0;
	String signedupDate="";

	public void login() {
		lastlogin=System.currentTimeMillis();
		lastloginDate=new Date().toString();
		save();
	}

	public Corp(String na, String tx, Location hq, String em, String pa, 
			String o, double c, double l, double t, 
			String itm,Vector proj,String cm) {
		name = na;
		tick = tx;
		home = hq;
		email = em;
		password = pa;
		owner = o;
		cash = c;
		loan = l;
		tax = t;
		itMarketReport = itm;
		corpReport = cm;
		projects=proj;
	}


	//create new corp
	public Corp(String na, String tx, Location hq, String em, String pa, String o) {
		lastlogin=System.currentTimeMillis();
		lastloginDate=new Date().toString();
		signedup=lastlogin;
		signedupDate=lastloginDate;
		name = na;
		tick = tx;
		home = hq;
		email = em;
		password = pa;
		owner = o;
		cash = 2500000;
		loan = tax = 0;
		itMarketReport = "research,lease,public,partner";
		corpReport="all";
	}


	public String getTick() {
		return tick;
	}


	public String getName() {
		return name;
	}	


	public String getPassword() {
		return password;
	}


	public Location getHome() {
		return home;
	}

	public double lastValue=0;
	public long lastTime=-1;

	public double getAssetValue() {
		if (lastTime>=Universe.time) {
			return lastValue;
		}
		lastTime=Universe.time;
		Vector v = Universe.getNFObjectsByTick(tick);
		double total = 0;
		for (int a = 0; a < v.size(); a++) {
			NFObject n = (NFObject) v.elementAt(a);
			total += n.getValue();
		}
		lastValue=total;
		return total;
	}


	public void saveOrders(String[] ords) {
		String ordersFile=Main.DIRORDERS+"Orders_" + tick + ".txt";
		if (ords.length==0) return;
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(ordersFile));
			for (int a = 0; a < ords.length; a++) {
				pw.println(ords[a]);
			}
			pw.flush();
			pw.close();
		}
		catch (IOException IOE) {
			System.out.println("MyError: cannot save orders");
			System.exit(0);
		}
	}


	public void expireOrders() {
		String ordersFile=Main.DIRORDERS+"Orders_"+tick+".txt";
		File f = new File(ordersFile);//erase current orders
		f.delete();
	}

	String delayOrders="";
	public void delayOrders() {
		if (delayOrders.length()>0) {
			try {
				String ordersFile=Main.DIRORDERS+"Orders_"+tick+".txt";
				File f=new File(ordersFile);
				PrintWriter pw = new PrintWriter(new FileWriter(ordersFile));
				pw.println(delayOrders);
				pw.flush();
				pw.close();
			}
			catch (IOException IOE) {
				System.out.println("MyError: cannot load orders");
			}
		}
	}

	public void loadOrders() {
		active=false;
		if(startval<0) {
			startval=getNetValue();
			//System.out.println(tick+" start val set to "+startval);
		}
		try {
			String ordersFile=Main.DIRORDERS+"Orders_"+tick+".txt";
			File f=new File(ordersFile);
			if (!f.exists()) return;
			BufferedReader br = new BufferedReader(new FileReader(ordersFile));
			while (true) {
				String s = br.readLine();
				if (s == null) {
					break;
				}
				s = s.trim();
				if (s.startsWith(";") || s.equals("")) {
					continue;
				}
				orders.addElement(s);
			}
			br.close();
			active=true;
		}
		catch (IOException IOE) {
			System.out.println("MyError: cannot load orders");
		}
	}


	public void doBuy(String toks[]) {
		if (toks.length != 3) {
			report += "ERROR: INCOMPLETE COMMAND BUY <marketitem> <amount>\n";
			return;
		}
		toks[1] = Market.getMarketName(toks[1]);
		if (toks[1] == null) {
			report += "ERROR: NO SUCH MARKET ITEM\n";
			return;
		}
		try {
			int amt = Integer.parseInt(toks[2]);
			if (amt <= 0) {
				throw new NumberFormatException();
			}
			if (amt > MAXTRANS) {
				report += "ERROR: BUY amount too large.  Reduced to max transaction "+MAXTRANS+ " instead.\n";
				orders.addElement("buy "+toks[1]+" "+(amt-MAXTRANS));
//HERE
				amt=MAXTRANS;
			}
			if (!Universe.getMarket().available(toks[1], amt)) {
				amt = Universe.getMarket().available(toks[1]);
				report += "ERROR: BAD insufficent supply. Buying " + amt + " instead.\n";
			}
			double costeach = Universe.market.getValue(toks[1]);
			double cost=costeach*amt;
			if (cost<0) {
				report += "Warning: CANNOT BUY "+amt+" Negative cost.";
				return;
			}
			if (!canAfford(costeach)) {
				report += "ERROR: CANNOT BUY INSUFFICENT FUNDS\n";
				return;
			}
			if (!canAfford(cost)) {
				int oldamt=amt;
				amt=(int)(getCanAfford()/costeach);
				if (amt==0) {
					report += "ERROR: CANNOT BUY INSUFFICENT FUNDS\n";
					return;
				}
				report += "ERROR: Cannot afford "+oldamt+" will buy "+amt+" instead.\n";
			}
			Universe.market.buy(toks[1], amt);
			makePayment(cost);
			double taxcost=cost*Universe.materialTax;
			tax+=taxcost;
			StockPile s = new StockPile(tick, toks[1], amt, home);
			Universe.add(s);
			Universe.mergeStockPiles();
			report += "  COST =" + Stuff.money(cost, 2) + "\n";
			report += "  Earth material market purchase tax "+Stuff.money(taxcost,2)+" on "+amt+" of "+toks[1]+"\n";
			//create stockpile at corp head quarters
		}
		catch (NumberFormatException NFE) {
			report += "ERROR: BAD number in command BUY <marketitem> <amount>\n";
		}
	}


	public void doSell(String toks[]) {
		if (toks.length != 3 && toks.length!=2) {
			report += "ERROR: INCOMPLETE COMMAND SELL <marketitem> <amount>\n";
			return;
		}
		NFObject nfo=Universe.getNFObjectById(toks[1]);
		if (nfo!=null) {
			if (!nfo.getLocation().equals(home)) {
				report += "ERROR: CANNOT SELL STOCKPILE.  NOT AT HQ\n";
				return;
			}
			if (nfo instanceof StockPile) {
				StockPile sp=(StockPile)nfo;
				int amt=nfo.getMass();
				if (toks.length==3) {
					try {
						int newamt=Math.max(0,Integer.parseInt(toks[2]));
						if (newamt>amt) {
							report += "ERROR: Amount requested greater than available.  Selling "+amt+" instead\n";
						}
						if (newamt>MAXTRANS) {
							orders.addElement("sell  "+toks[1]+" "+(newamt-MAXTRANS));
							amt=Math.min(amt,MAXTRANS);
							report += "ERROR: Amount requested greater than MAXTRANS.  Selling "+MAXTRANS+" instead\n";
						}
						amt=Math.min(amt,newamt);
					} catch (NumberFormatException nfe) {
						report += "ERROR: Bad number in sell."+toks[2]+"\n";
						return;
					}
				}
				else if (amt>MAXTRANS) {
					orders.addElement("sell  "+toks[1]+" "+(amt-MAXTRANS));
					amt=Math.min(amt,MAXTRANS);
					report += "ERROR: Amount available greater than MAXTRANS.  Selling "+MAXTRANS+" instead\n";
				}
				StockPile s = sp.take(amt);
				Universe.mergeStockPiles();
				double profit = Universe.getMarket().sell(s);
				cash += profit;
				report += "  EARN =" + Stuff.money(profit, 2) + "\n";
				return;
			}
			report += "ERROR: can only sell stockpiles\n";
			return;
			
		}
		toks[1] = Market.getMarketName(toks[1]);
		if (toks[1] == null) {
			report += "ERROR: NO SUCH MARKET ITEM\n";
			return;
		}
		try {
			int amt = 0;
			StockPile sp = new StockPile(tick, toks[1], 0, home);
			sp = Universe.getStockPile(sp);
			if (sp == null) {
				report += "ERROR: CANNOT SELL STOCKPILE NOT FOUND\n";
				return;
			}
			amt = sp.getAmount();
			if (amt == 0) {
				report += "ERROR: INSUFFICENT STOCKPILE.  NONE TO SELL\n";
				return;
			}
			if (toks.length==3) {
				int newamt=Integer.parseInt(toks[2]);
				if (newamt>amt) {
					report += "Warning: INSUFFICENT STOCKPILE.  SELLING " + amt + " INSTEAD\n";
				}
				amt=Math.min(amt,newamt);
			}
			if (amt > sp.getAmount()) {
				amt = Math.min(amt, sp.getAmount());
				report += "Warning: INSUFFICENT STOCKPILE.  SELLING " + amt + " INSTEAD\n";
			}
			if (amt > MAXTRANS) {
				orders.addElement("sell  "+toks[1]+" "+(amt-MAXTRANS));
				amt = Math.min(amt, MAXTRANS);
				report += "Warning: Cannot sell more than MAXTRANS.  SELLING " + MAXTRANS + " INSTEAD\n";
			}
			StockPile s = sp.take(amt);
			double profit = Universe.getMarket().sell(s);
			cash += profit;
			Universe.mergeStockPiles();
			report += "  EARN =" + Stuff.money(profit, 2) + "\n";
		}
		catch (NumberFormatException NFE) {
			report += "ERROR: BAD number in command SELL <marketitem> <amount>\n";
		}
	}


	public void doConsign(String toks[]) {
		if (toks.length != 4) {
			report += "ERROR: INCOMPLETE COMMAND CONSIGN <id> <reserve> <closing>\n";
			return;
		}
		try {
			String id = toks[1];
			double res = Stuff.parseDouble(toks[2]);
			int cls = Integer.parseInt(toks[3]);
			if (Universe.getAuction().consign(id, tick, res, cls+2)) {
				report += "  CONSIGNED "+id+" with reserve "+res+" and closing in "+cls+" turns.\n";
			}
			else {
				report += "ERROR: FAILED CONSIGN\n";
			}
		}
		catch (NumberFormatException NFE) {
			report += "ERROR: BAD number in command CONSIGN <id> <reserve> <closing>\n";
		}
	}


	public void doBid(String toks[]) {
		if (toks.length != 3) {
			report += "ERROR: INCOMPLETE COMMAND BID <id> <amount>\n";
			return;
		}
		try {
			String id = toks[1];
			double amt = Stuff.parseDouble(toks[2]);
			if (!canAfford(amt)) {
				report += "ERROR: INSUFFICENT FUNDS FOR BID\n";
				return;
			}
			Universe.getAuction().bid(id, amt, this);
		}
		catch (NumberFormatException NFE) {
			report += "ERROR: BAD number in command BID <id> <amount>\n";
		}

	}

	public String validOrder(String c) {
		if (c==null) {
			return "INVALID ORDER>"+c;
		}
		if (c.trim().length()==0 || c.startsWith("#") || c.startsWith(";") ){
			return c;
		}
		String toks[] = Stuff.getTokens(c, " \t");
		if (toks.length == 0) 
			return "INVALID ORDER>"+c;
		toks[0] = toks[0].toUpperCase();
		if (toks[0].equals("NOOP") && toks.length==1) {
			return c;
		}
		else if (toks[0].equals("DELAY")) {
			if (toks.length<3) {
				return "INVALID ORDER>Improper use of DELAY <turns> <command . . .>: "+c;
			}
			return c;
		}
		else if (toks[0].equals("RESET")) {
			if (toks.length != 2) {
				return "INVALID ORDER>NO ID in reset: "+c;
			}
			NFObject n = Universe.getNFObjectById(toks[1]);
			if (n == null) {
				return "INVALID ORDER>BAD ID in reset: "+c;
			}
			if (!n.getCorpTick().equals(tick)) {
				return "INVALID ORDER>CANNOT reset ID not owned: "+c;
			}
			if (!(n instanceof Active)) {
				return "INVALID ORDER>RESET only applies to ROBOTS or SHIPS: "+c;
			}
		}
		else if (toks[0].equals("INIT")) {
			if (toks.length != 2) {
				return "INVALID ORDER>NO ID in init: "+c;
			}
			NFObject n = Universe.getNFObjectById(toks[1]);
			if (n == null) {
				return "INVALID ORDER>BAD ID in init: "+c;
			}
			if (!n.getCorpTick().equals(tick)) {
				return "INVALID ORDER>CANNOT INIT ID not owned : "+c;
			}
			if (!(n instanceof Active)) {
				return "INVALID ORDER>INIT only applies to ROBOTS and SHIPS: "+c;
			}
		}
		else if (toks[0].equals("UPLOAD")) {
			if (toks.length < 3) {
				return "INVALID ORDER>UPLOAD requires ID and PROGRAM_NAME_LIST: "+c;
			}
			NFObject n = Universe.getNFObjectById(toks[1]);
			if (n == null) {
				return "INVALID ORDER>BAD ID in upload: "+c;
			}
			if (!n.getCorpTick().equals(tick)) {
				return "INVALID ORDER>CANNOT upload ID not owned: "+c;
			}
			for (int b=2;b<toks.length;b++) {
				boolean found=false;
				for (int a = 0; a < defs.size(); a++) {
					GmlPair g = (GmlPair) defs.elementAt(a);
					if (g.getName().equalsIgnoreCase(toks[b])) {
						GmlPair gg[] = g.getAllByName("lines");
						if (gg.length != 0) {
							found=true;
							break;
						}
					}
				}
				if (!found) {
					ITThing itt=Universe.getITThingByName(toks[b]);
					if (itt instanceof Program ) {
						found=true;
						Program p=(Program)itt;	
                                                if (!p.isPartner(tick) && !p.isPublic() && !p.isLeased()) {
							return "INVALID ORDER>CANNOT lease private Programs: "+c;
                                                }
					}
				}
				if (!found) {
					return "INVALID ORDER>CANNOT find programs "+toks[b]+": "+c;
				}
			}
			if (!(n instanceof Active)) {
				return "INVALID ORDER>UPLOAD only applies to ROBOTS and SHIPS:"+c;
			}
		}
		else if (toks[0].equals("PROGRAM")) {
			String name = Stuff.superTrim(c.substring(7));
			while (name.indexOf("\"")>=0) {
				name=name.substring(0,name.indexOf("\""))+name.substring(name.indexOf("\"")+1);
			}
			String desc = "";
			if (name.indexOf(" ") > 0) {
				desc = name.substring(name.indexOf(" ") + 1).trim();
				name = name.substring(0, name.indexOf(" ")).trim();
			}
			int count = 1;
			String line = "\"" + name + "\" [ type program desc \"" + desc + "\" lines \"";
			line += "\" ]";
			try {
				GmlPair g = GmlPair.parse(line);
				defs.addElement(g);
			}
			catch (IOException IOE) {
				return "INVALID ORDER>BAD program name or description:"+c;
			}
		}
		else if (toks[0].equals("NICK")) {
			if (toks.length!=3) {
				return "INVALID ORDER>Nick requires ID and nick name:"+c;
			}
			String oldname=toks[1];
			String newname=toks[2];
			NFObject i=Universe.getNFObjectById(oldname);
			NFObject ii=Universe.getNFObjectById(newname);
			if (ii!=null) {
				return "INVALID ORDER>Nickname already in use:"+c;
			}
			if (i==null) {
				return "INVALID ORDER>Cannot find ship, robot or facility to nickname:"+c;
			}
			else if (!(i instanceof Facility) && !(i instanceof Active)) {
				return "INVALID ORDER>Cannot nickname stockpile or prefab:"+c;
			}
		}
		else if (toks[0].equals("RENAME")) {
			if (toks.length!=3) {
				return "INVALID ORDER>Rename requires old and new names:"+c;
			}
			String oldname=toks[1];
			String newname=toks[2];
			ITThing i=Universe.getITThingByName(oldname);
			for (int a=0;a<projects.size();a++) {
				Project p=(Project)projects.elementAt(a);
				if (p.getName().equalsIgnoreCase(newname)) {
					return "INVALID ORDER>New name not unique:"+c;
				}
			}
			ITThing ii=Universe.getITThingByName(newname);
			if (ii!=null) {
				return "INVALID ORDER>New name not unique:"+c;
			}
			if (i!=null && i.isPartner(tick)) {
				if (!(i instanceof ActiveDesign) && !(i instanceof FacilityDesign)) {
					return "INVALID ORDER>You can oly rename Facilty and Active designs:"+c;
				}
			}
			else {
				return "INVALID ORDER>You do not hold partner access to "+oldname+":"+c;
			}
		}
		else if (toks[0].equals("DESIGN")) {
			String def="";
			if (toks[1].equalsIgnoreCase("FACILITY")) {
				if (toks.length<4) {
					return "INVALID ORDER>Facility design requires name and techlist:"+c;
				}
				def="design [type facility name \""+toks[2]+"\" ";
				for (int a=3;a<toks.length;a++) {
					Tech t=Universe.getTechByName(toks[a]);
					if (t==null){
						return "INVALID ORDER>Cannot find tech "+toks[a]+" in Facility design:"+c;
					}
					if (!t.validForType("f")) {
						return "INVALID ORDER>"+t.getName()+" not allowed for facilities:"+c;
					}
					def+=" func \""+toks[a]+"\" ";
				}
				def+=" ]";
				
			}
			else if (toks[1].equalsIgnoreCase("SHIP") || toks[1].equalsIgnoreCase("ROBOT")) {
				if (toks.length<6) {
					return "INVALID ORDER>Active design requires name cargo, capacity, endurance and techlist:"+c;
				}
				def="design [type "+toks[1].toLowerCase()+" name \""+toks[2]+"\" cargocap \""+toks[3]+"\" endur \""+toks[4]+"\" ";
				try {
					Integer.parseInt(toks[4]);
					if (toks[3].toUpperCase().startsWith("X")) {
						Integer.parseInt(toks[3].substring(1));
					}
					else if (toks[3].toUpperCase().endsWith("X")) {
						Integer.parseInt(toks[3].substring(0,toks[3].length()-1));
					}
					else {
						Integer.parseInt(toks[3]);
					}
	
				} catch (NumberFormatException nfe) {
					return "INVALID ORDER>Bad number in design:"+c;
				}
				for (int a=5;a<toks.length;a++) {
					Tech t=Universe.getTechByName(toks[a]);
					if (t==null){
						return "INVALID ORDER>Cannot find tech "+toks[a]+" in Active design:"+c;
					}
					if (!t.validForType(toks[1].toLowerCase().charAt(0)+"")) {
						return "INVALID ORDER>"+t.getName()+" not allowed for "+toks[1].toUpperCase()+":"+c;
					}
					def+=" func \""+toks[a]+"\" ";
				}
				def+=" ]";
				
			}
			else {
				return "INVALID ORDER>Bad use of design:"+c;
			}
			try {
				GmlPair g = GmlPair.parse(def);
				defs.addElement(g);
			}
			catch (IOException IOE) {
				return "INVALID ORDER> Design error in GML:"+def+" "+c;
			}
		}
		//RELEASE <ID>  : no longer partner.  if no partner and design then erase design
		else if (toks[0].equals("RELEASE")) {
			if (toks.length != 2) {
				return "INVALID ORDER>Bad usage of RELEASE.  Need Id:"+c;
			}
			ITThing i=Universe.getITThingByName(toks[1]);
			if (i==null) {
				return "INVALID ORDER>No such tech, design or program:"+c;
			}
			if (!i.isPartner(tick)){
				return "INVALID ORDER>You are not partner on tech, design or program:"+c;
			}
		}
		//PATENT <ID>  : save and make partner
		else if (toks[0].equals("PATENT")) {
			if (toks.length!=2) {
				return "INVALID ORDER>wrong number of args:"+c;
			}
			//check IT list less than 5
			int num=Universe.countProg(tick);
			if (num==5) {
				return "INVALID ORDER>Sorry only 5 programs can be pattened. Try releasing one:"+c;
			}
			if (Universe.getITThingByName(toks[1])!=null) {
				return "INVALID ORDER>Name not unique:"+c;
			}
			//check for defined program name
			boolean found=false;
			for (int a = 0; a < defs.size(); a++) {
				GmlPair g = (GmlPair) defs.elementAt(a);
				if (g.getName().equalsIgnoreCase(toks[1])) {
					found=true;
					String name="",lines="",desc="";
					GmlPair ggg=g.getOneByName("type");
					if (ggg==null || !ggg.getString().equalsIgnoreCase("program")) {
						return "INVALID ORDER>Improper program in patent:"+c;
					}
				}

			}
			if (!found) {
				return "INVALID ORDER>Cannot find program in patent:"+c;
			}
		}
		//PAY <corp> <amount> [<desc>]
		else if (toks[0].equals("PAY")) {
			if (toks.length <3) {
				return "INVALID ORDER>Bad usage of pay.  Need ticker and amount:"+c;
			}
			Corp C=Universe.getCorpByTick(toks[1]);
			if (C == null && !toks[1].equalsIgnoreCase("ALL") && !toks[1].equalsIgnoreCase("TAX") && !toks[1].equalsIgnoreCase("LOAN")) {
				return "INVALID ORDER>Bad Corp in pay:"+c;
			}
			double amt=0;
			try {
				amt=Double.parseDouble(toks[2]);
			} catch (NumberFormatException nfe) {
				return "INVALID ORDER>Bad number in pay:"+c;
			}
			if (amt<0) {
				return "INVALID ORDER>Cannot pay negitave funds:"+c;
			}
		}
		//GIVE <ID> <TICK>
		else if (toks[0].equals("GIVE")) {
			if (toks.length != 3) {
				return "INVALID ORDER>Bad usage of GIVE.  Need ID and corp ticker:"+c;
			}
			NFObject nfo = Universe.getNFObjectById(toks[1]);
			if (nfo == null) {
				return "INVALID ORDER>Bad id in GIVE:"+c;
			}
			if (!nfo.getCorpTick().equals(tick)) {
				return "INVALID ORDER>Not Owner of object:"+c;
			}
			Corp C=Universe.getCorpByTick(toks[2]);
			if (C == null) {
				return "INVALID ORDER>Bad Corp in GIVE:"+c;
			}
		}
		//PARTNER <ID> <TICK>
		else if (toks[0].equals("PARTNER")) {
			if (toks.length != 3) {
				return "INVALID ORDER>Bad usage of PARTNER.  Need ID and corp ticker:"+c;
			}
			ITThing t = Universe.getITThingByName(toks[1]);
			if (t == null) {
				return "INVALID ORDER>Bad id in PARTER:"+c;
			}
			if (!t.isPartner(tick)) {
				return "INVALID ORDER>Not PARTER:"+c;
			}
			Corp C=Universe.getCorpByTick(toks[2]);
			if (C == null) {
				return "INVALID ORDER>Bad Corp in PARTER:"+c;
			}
		}
		//CANCEL <TECHID> 
		else if (toks[0].equals("CANCEL")) {
			if (toks.length != 2) {
				return "INVALID ORDER>Bad usage of CANCEL. Need ID:"+c;
			}
			boolean found=false;
			for (int a=0;a<projects.size();a++) {
				Project p=(Project)projects.elementAt(a);
				if (p.getId().equalsIgnoreCase(toks[1])) {
					found=true;
					break;
				}
			}
			if (!found) {
				return "INVALID ORDER>Cannot find project to cancel:"+c;
			}
		}
		//CONTRACT <id> <PRICE>
		else if (toks[0].equals("CONTRACT")) {
			if (toks.length != 3) {
				return "INVALID ORDER>Bad usage of CONTRACT. Need ID and PRICE:"+c;
			}
			NFObject t = Universe.getNFObjectById(toks[1]);
			if (t == null) {
				return "INVALID ORDER>Bad ID in CONTRACT:"+c;
			}
			if (!(t instanceof Facility)) {
				return "INVALID ORDER>CONTRACT must be facility:"+c;
			}
			if (!((Facility)t).hasAbility("T69")) {
				return "INVALID ORDER>Facility must have research Technology:"+c;
			}
			double price=-1;
			try {
				price=Math.max(0,Stuff.parseDouble(toks[2]));	
			} catch (NumberFormatException nfe) {
				return "INVALID ORDER>Bad PRICE in CONTRACT:"+c;
			}	
			if (!t.getCorpTick().equalsIgnoreCase(getTick())) {
				return "INVALID ORDER>Cannot CONTRACT not owner:"+c;
			}
		}
		//LEASE <ID> <PRICE>
		else if (toks[0].equals("LEASE")) {
			if (toks.length != 3) {
				return "INVALID ORDER>Bad usage of LEASE. Need ID and PRICE:"+c;
			}
			ITThing t = Universe.getITThingByName(toks[1]);
			if (t == null) {
				return "INVALID ORDER>Bad ID in LEASE:"+c;
			}
			double price=-1;
			try {
				price=Math.max(0,Stuff.parseDouble(toks[2]));	
			} catch (NumberFormatException nfe) {
				return "INVALID ORDER>Bad PRICE in LEASE:"+c;
			}	
			if (!t.isPartner(tick)) {
				return "INVALID ORDER>Cannot LEASE not partner:"+c;
			}
			if (t.getLeasePrice()<price && t.getLeasePrice()>=0) {
				return "INVALID ORDER>Cannot raise LEASE price:"+c;
			}
		}
		else if (toks[0].equals("RESEARCH")) {
			if (toks.length!=3) {
				return "INVALID ORDER>wrong number of args:"+c;
			}
			boolean found =false;
			for (int a=0;a<projects.size();a++) {
				Project p=(Project)projects.elementAt(a);
				if (p.getId().equalsIgnoreCase(toks[2])) {
					if (p.isReady()) {
						found=true;
					}
					else {
						return "INVALID ORDER>Project busy cannot research until next turn:"+c;
					}
				}
			}
			NFObject fnfo=Universe.getNFObjectById(toks[1]);
			if (fnfo==null && !toks[1].equalsIgnoreCase("ANY")) {
				return "INVALID ORDER>Cannot find research facility "+toks[1]+":"+c;
			}
			if (!(fnfo instanceof Facility)  && !toks[1].equalsIgnoreCase("ANY")){
				return "INVALID ORDER>Cannot find research at "+toks[1]+" not facility:"+c;
			}
			if (fnfo!=null ){
				Facility f=(Facility)fnfo;
				if (!f.hasTech("T69")) {
					return "INVALID ORDER>Facility does not have research ability or is not active:"+c;
				}
			}
			ITThing t=Universe.getITThingByName(toks[2]);
			if (!found && t!=null) {
				found=true;
				if (!(t instanceof Tech)) {
					return "INVALID ORDER>Cannot research active designes:"+c;
				}
				if (t.isPartner(tick)) {
					return "INVALID ORDER>Already paratner:"+c;
				}
				Tech T=(Tech)t;
				
				boolean holdsPrerequisits=Universe.holdsPrerequisitsFor(tick,T);
				if (!holdsPrerequisits) {
					return "INVALID ORDER>Cannot access prerequisits Technology. "+c;
				}
			}
			for (int a=0;a<defs.size();a++) {
				GmlPair g=(GmlPair)defs.elementAt(a);
				if (g.getName().equalsIgnoreCase("design")) {
					found=true;
					GmlPair n=g.getOneByName("name");
					String na=null;
					if (n!=null)  na=n.getString();
					if (!na.equalsIgnoreCase(toks[2])) {
						continue;
					}
					String type=null;
					n=g.getOneByName("type");
					if (n!=null) type=n.getString();
					Vector part=new Vector();
					part.addElement(tick);
					Vector funcs=new Vector();
					GmlPair tt[]=g.getAllByName("func");
					for (int b=0;b<tt.length;b++) {
						String funs=tt[b].getString().toUpperCase();
						Tech tech=Universe.getTechByName(funs);
						if (tech==null) {
							return "INVALID ORDER>Unknown tech in design "+funs+":"+c;
						}
						if (funcs.contains(tech.getName())) {
							return "INVALID ORDER>Duplicat tech "+funs+":"+c;
						}
						funcs.addElement(tech.getName());
						if (!tech.isPartner(tick) && !tech.isPublic() && !tech.isLeased()) {
							return "INVALID ORDER>Cannot access tech "+funs+":"+c;
						}
					}
					funcs.addElement("basic"+type);

					if (type.equalsIgnoreCase("robot") || type.equalsIgnoreCase("ship")) {
						double endur=-1;
						double cargocap=-1;

						n=g.getOneByName("endur");
						if (n!=null) endur=(int)n.getDouble();

						ActiveDesign ad=new ActiveDesign(type.charAt(0)+"",na,part,funcs,new Vector(),-1,endur,cargocap);
						int mass=ad.getDesignMass();

						n=g.getOneByName("cargocap");
						if (n!=null) {
							if (n.getString().toUpperCase().startsWith("X")) {
								try {
									cargocap=mass*Integer.parseInt(n.getString().substring(1));
								} catch (NumberFormatException nfe) {
									return "INVALID ORDER>Bad number in cargo multiplier"+n.getString()+":"+c;
								}
							}
							else if (n.getString().toUpperCase().endsWith("X")) {
								try {
									cargocap=mass*Integer.parseInt(n.getString().substring(0,n.getString().length()-2));
								} catch (NumberFormatException nfe) {
									return "INVALID ORDER>Bad number in cargo multiplier"+n.getString()+":"+c;
								}
							}
							else {
								cargocap=(int)n.getDouble();
							}
							ad.setCargoCap((int)cargocap);
						}
						if (cargocap<0 || endur<0){
							return "INVALID ORDER>Cannot use negitive endurance or cargo:"+c;
						}
						double cost=1;
						int basefun=5;
						if (type.equalsIgnoreCase("SHIP")) basefun=4;
						cost=cost*Math.pow(FUNCSRATE,funcs.size()-basefun);
						int basemass=30;
						if (type.equalsIgnoreCase("SHIP")) basemass=50;
						int basestep=10;
						if (type.equalsIgnoreCase("SHIP")) basestep=25;
						int sizmult=(mass-basemass)/basestep;
						cost=cost*Math.pow(SIZERATE,sizmult);
						int cargotype=(int)(cargocap/mass);//robot rate  (1xmass=1, 2xmass=2)
						if (type.equalsIgnoreCase("ship")) cargotype=(int)(cargocap/mass/2);//ship rate 2xmass=1 4xmass=2
						cost=cost*Math.pow(CARGORATE,cargotype);
						boolean robot=type.toUpperCase().startsWith("R");
						int basendur=(robot?9:5);
						int endurtype=(int)(Math.max(0,endur-basendur));
						cost=cost*Math.pow(ENDURRATE,endurtype);
						int costtime=(int)(cost/RESEARCHCOST/2);
						Project p=new Project(costtime,0,ad);
						addProject(p);
						return c+"\n #estimated cost multipiler="+Stuff.money(cost,2)+" base material ="+
ad.getProducedValue();
					}
					else if (type.equalsIgnoreCase("facility")) {
						FacilityDesign fd=new FacilityDesign(na,part,funcs,new Vector(),-1);
						double cost=1;
						int basefun=3;
						cost=cost*Math.pow(FUNCSRATE,funcs.size()-basefun);
						int costtime=(int)(cost/RESEARCHCOST/2);
						Project p=new Project(costtime,0,fd);
						addProject(p);
						return c+"\n #estimated cost multipiler="+Stuff.money(cost,2)+" base material ="+
fd.getProducedValue();
					}
					else {
						return "INVALID ORDER>Unknown type in RESEARACH use ROBOT, SHIP or FACILITY:"+c;
					}
				}
			}
			if (!found) {
				return "INVALID ORDER>No such Tech, Design or Project in research:"+c;
			}
		}
		else if (toks[0].equals("SETMAIL")) {
			if (toks.length!=2) {
				return "INVALID ORDER>Wrong number of args:"+c;
			}
			if (toks[1].indexOf("@")<=0 || 
			(toks[1].indexOf("@") <= 0 && toks[1].lastIndexOf(".") > toks[1].indexOf("@"))) {
				return "INVALID ORDER>Invalid Email address:"+c;
			}
		}
		else if (toks[0].equals("SETPASSWORD")) {
			if (toks.length!=2) {
				return "INVALID ORDER>Wrong number of args:"+c;
			}
			if (toks.length<2 || toks[1].length()<6) {
				return "INVALID ORDER>Invalid password:"+c;
			}
		}
		else if (toks[0].equalsIgnoreCase("DISABLE") || toks[0].equalsIgnoreCase("ENABLE")) {
			if (toks.length!=2) {return "INVALID ORDER>Wrong number of args:"+c;}
			NFObject nfo=Universe.getNFObjectById(toks[1]);
			if (nfo==null) {
				return "INVALID ORDER>Cannot find Facility in "+toks[0]+":"+c;
			}
			if (!(nfo instanceof Facility) ) {
				return "INVALID ORDER>Not a Facility.  Can only "+toks[0]+" Facilities :"+c;
			}
			if (!nfo.getCorpTick().equalsIgnoreCase(tick)) {
				return "INVALID ORDER>You do not own this facility. Cannot "+toks[0]+":"+c;
			}
		}
		else if (toks[0].equalsIgnoreCase("MAXCONTRACT")) {
			if (toks.length!=2) {return "INVALID ORDER>Wrong number of args:"+c;}
			try { Integer.parseInt(toks[1]);} catch (NumberFormatException nfe) {return "INVALID ORDER>Bad number:"+c;}
		}
		else if (toks[0].equalsIgnoreCase("MAXLOAN")) {
			if (toks.length!=2) {return "INVALID ORDER>Wrong number of args:"+c;}
			try { Integer.parseInt(toks[1]);} catch (NumberFormatException nfe) {return "INVALID ORDER>Bad number:"+c;}
		}
		else if (toks[0].equalsIgnoreCase("SETFEE")) {
			if (toks.length!=3) {return "INVALID ORDER>Wrong number of args:"+c;}
			if (!toks[1].equalsIgnoreCase("MEGAWATTS") && !toks[1].equalsIgnoreCase("FOOD") &&
				!toks[1].equalsIgnoreCase("OXYGEN") && !toks[1].equalsIgnoreCase("HYDRATION") &&
				!toks[1].equalsIgnoreCase("HUMANLABOR") ) {
				return "INVALID ORDER>Bad type if SETFEE.  Must be MEGAWATTS,FOOD,OXYGEN,HYDRATION,HUMANLABOR:"+c;
			}
			try { Integer.parseInt(toks[2]);} catch (NumberFormatException nfe) {return "INVALID ORDER>Bad number:"+c;}
		}
		else if (toks[0].equalsIgnoreCase("MAXFEE")) {
			if (toks.length!=3) {return "INVALID ORDER>Wrong number of args:"+c;}
			if (!toks[1].equalsIgnoreCase("MEGAWATTS") && !toks[1].equalsIgnoreCase("FOOD") &&
				!toks[1].equalsIgnoreCase("OXYGEN") && !toks[1].equalsIgnoreCase("HYDRATION") &&
				!toks[1].equalsIgnoreCase("HUMANLABOR") ) {
				return "INVALID ORDER>Bad type if MAXFEE.  Must be MEGAWATTS,FOOD,OXYGEN,HYDRATION,HUMANLABOR:"+c;
			}
			try { Integer.parseInt(toks[2]);} catch (NumberFormatException nfe) {return "INVALID ORDER>Bad number:"+c;}
		}
		else if (toks[0].equalsIgnoreCase("SETLOG")) {
			if (toks.length!=2) {
				return "INVALID ORDER>Wrong number of args:"+c;
			}
			if (!toks[1].toUpperCase().startsWith("VERBOSE") && !toks[1].toUpperCase().startsWith("BRIEF") ) {
				return "INVALID ORDER>Setlog use VERBOSE or BRIEF:"+c;
			}
		}
		else if (toks[0].equalsIgnoreCase("SETCORP")) {
			if (toks.length!=2) {
				return "INVALID ORDER>Wrong number of args:"+c;
			}
			if (!toks[1].toUpperCase().startsWith("NONE") && 
			!toks[1].toUpperCase().startsWith("ALL") && !toks[1].toUpperCase().startsWith("ACTIVE") && 
			!toks[1].toUpperCase().startsWith("OWN") ) {
				return "INVALID ORDER>SETCORP use NONE, ALL, ACTIVE or OWN:"+c;
			}
		}
		else if (toks[0].equalsIgnoreCase("SETTECH")) {
			if (toks.length!=2) {
				return "INVALID ORDER>Wrong number of args:"+c;
			}
			String subs[]=Stuff.getTokens(toks[1]," ,");
			for (int cc=0;cc<subs.length;cc++) {
				if (!subs[cc].equalsIgnoreCase("ALL") &&
				    !subs[cc].equalsIgnoreCase("NONE") &&
				    !subs[cc].equalsIgnoreCase("PUBLIC") &&
				    !subs[cc].equalsIgnoreCase("PARTNER") &&
				    !subs[cc].equalsIgnoreCase("PRIVATE") &&
				    !subs[cc].equalsIgnoreCase("LEASE") &&
				    !subs[cc].equalsIgnoreCase("RESEARCH")) {
					return "INVALID ORDER>Must by ALL, NONE, PUBLIC, PARTNER, PRIVATE, LEASE or RESEARCH:"+c;
				}
			}
			
		}
		else if (toks[0].equals("BUY")) {
			if (toks.length != 3) {
				return "INVALID ORDER>Wrong number of args:"+c;
			}
			toks[1] = Market.getMarketName(toks[1]);
			if (toks[1] == null) {
				return "INVALID ORDER>No such market item:"+c;
			}
			try {
				int amt = Integer.parseInt(toks[2]);
			}
			catch (NumberFormatException NFE) {
				return "INVALID ORDER>Bad number:"+c;
			}
		}
		else if (toks[0].equals("SELL")) {
			if (toks.length != 3 && toks.length!=2) {
				return "INVALID ORDER>Wrong number of args:"+c;
			}
			NFObject nfo=Universe.getNFObjectById(toks[1]);
			if (nfo!=null) {
				if (!nfo.getLocation().equals(home)) {
					return "INVALID ORDER>Cannot sell stockpile not at HQ:"+c;
				}
				if (nfo instanceof StockPile) {
					StockPile sp=(StockPile)nfo;
					int amt=nfo.getMass();
					if (toks.length==3) {
						try {
							int newamt=Math.max(0,Integer.parseInt(toks[2]));
						} catch (NumberFormatException nfe) {
							return "INVALID ORDER>Bad number:"+c;
						}
					}
				}
				else {
					return "INVALID ORDER>Sell only applies to stockpiles:"+c;
				}
				
			}
			else {
				toks[1] = Market.getMarketName(toks[1]);
				if (toks[1] == null) {
					return "INVALID ORDER>No such market item:"+c;
				}
				try {
					int amt = 0;
					StockPile sp = new StockPile(tick, toks[1], 0, home);
					sp = Universe.getStockPile(sp);
					if (sp == null) {
						return "INVALID ORDER>Cannot find material at HQ:"+c;
					}
					amt = sp.getAmount();
					if (amt == 0) {
						return "INVALID ORDER>No material available to sell at HQ:"+c;
					}
					if (toks.length==3) amt=Math.max(amt,Integer.parseInt(toks[2]));
				}
				catch (NumberFormatException NFE) {
					return "INVALID ORDER>Bad number:"+c;
				}
			}
		}
		else if (toks[0].equals("CONSIGN")) {
			if (toks.length != 4) {
				return "INVALID ORDER>Wrong number of args:"+c;
			}
			try {
				String id = toks[1];
				double res = Stuff.parseDouble(toks[2]);
				int cls = Integer.parseInt(toks[3]);
				NFObject nfo=Universe.getNFObjectById(id);
				if (nfo==null) {
					return "INVALID ORDER>Wrong number of args:"+c;
				}
				if (!nfo.getCorpTick().equalsIgnoreCase(tick)){
					return "INVALID ORDER>Not owner:"+c;
				}
				if (!nfo.getLocation().equalsIgnoreCase(home)){
					return "INVALID ORDER>Not at HQ:"+c;
				}
			}
			catch (NumberFormatException NFE) {
				return "INVALID ORDER>Bad number:"+c;
			}
		}
		else if (toks[0].equals("BID")) {
			if (toks.length != 3) {
				return "INVALID ORDER>Wrong number of args:"+c;
			}
			try {
				String id = toks[1];
				double amt = Stuff.parseDouble(toks[2]);
				NFObject nfo=Universe.getNFObjectById(id);
				if (nfo==null) {
					return "INVALID ORDER>Wrong number of args:"+c;
				}
				if (!nfo.getCorpTick().equalsIgnoreCase("AUCTION")){
					return "INVALID ORDER>Not for auction:"+c;
				}
			}
			catch (NumberFormatException NFE) {
				return "INVALID ORDER>Bad number:"+c;
			}
		}
		else if (toks[0].equals("DEFINE")) {
			return "INVALID ORDER>"+c;
		}
		else if (toks[0].equals("PRODUCE") || toks[0].equals("PURCHASE")) {
			if (toks.length != 2 && toks.length != 3) {
				return "INVALID ORDER>Wrong number of args:"+c;
			}
			else if (Market.getMarketName(toks[1])!=null) {
				doBuy(toks);
			}
			else if (toks.length == 2) {
				Location l = home;
				ITThing itt = Universe.getITThingByName(toks[1]);
				if (itt == null) {
					return "INVALID ORDER>No such design:"+c;
				}
				double cost = 0;
				if (!itt.isPartner(tick) && !itt.isPublic()) {
					cost += itt.getLeasePrice();
				}
				if (cost < 0 ) {
					return "INVALID ORDER>Private design cannot use:"+c;
				}
			}
			else if (toks.length == 3) {
				return "INVALID ORDER>Facility Production no yet implemented:"+c;
			}
		}
		else if (toks[0].equals("QUIT") && toks.length==1) {
		}
		else {
			return "INVALID ORDER>Unknown Command:"+c;
		}
		return c;
	}


	public boolean executeOrder() {
		if (orders.size() == 0) {
			return false;
		}
		String c = ((String) orders.elementAt(0)).trim();
		orders.removeElementAt(0);
		if (c.startsWith("#") || c.startsWith(";")){
			report += "COMMENT> " + c + "\n";
			return true;
		}
		String toks[] = Stuff.getTokens(c, " \t");
		if (toks.length == 0) {
			return true;
		}
		toks[0] = toks[0].toUpperCase();
		report += "ORDER> " + c + "\n";
		if (toks[0].equals("NOOP")) {
			return true;
		}
		else if (toks[0].equals("DELAY")) {
			c=c.trim();
			int delay=1;
			try {
				delay=Integer.parseInt(toks[1]);	
			} catch (NumberFormatException nfe) {
				report += "ERROR: Bad number in DELAY!\n";
			}
			c=c.substring("DELAY ".length()).trim();
			c=c.substring(c.indexOf(" ")).trim();
			if (delay>1) {
				c="DELAY "+(delay-1)+" "+c;
			}
			delayOrders+=c+"\n";
			return true;
		}
		else if (toks[0].equals("RESET")) {
			if (toks.length != 2) {
				report += "ERROR: NO ID in reset!\n";
				return true;
			}
			NFObject n = Universe.getNFObjectById(toks[1]);
			if (n == null) {
				report += "ERROR: BAD ID in reset!\n";
				return true;
			}
			if (!n.getCorpTick().equals(tick)) {
				report += "ERROR: CANNOT reset ID not owned!\n";
				return true;
			}
			if (n instanceof Active) {
				((Active) n).reset();
			}
			else {
				report += "ERROR: RESET only applies to ROBOTS and SHIPS!\n";
				return true;
			}
			report += "  "+n.getId()+" reset\n";
		}
		else if (toks[0].equals("INIT")) {
			if (toks.length != 2) {
				report += "ERROR: NO ID in initilize!\n";
				return true;
			}
			NFObject n = Universe.getNFObjectById(toks[1]);
			if (n == null) {
				report += "ERROR: BAD ID in initilize!\n";
				return true;
			}
			if (!n.getCorpTick().equals(tick)) {
				report += "ERROR: CANNOT INIT ID not owned!\n";
				return true;
			}
			if (n instanceof Active) {
				((Active) n).initilizeProgram();
			}
			else {
				report += "ERROR: INIT only applies to ROBOTS and SHIPS!\n";
				return true;
			}
			report += "  "+n.getId()+" initilized\n";
		}
		else if (toks[0].equals("UPLOAD")) {
			if (toks.length < 3) {
				report += "ERROR: UPLOAD requires ID and PROGRAM_NAME!\n";
				return true;
			}
			NFObject n = Universe.getNFObjectById(toks[1]);
			if (n == null) {
				report += "ERROR: BAD ID in upload!\n";
				return true;
			}
			if (!n.getCorpTick().equals(tick)) {
				report += "ERROR: CANNOT upload ID not owned!\n";
				return true;
			}
			String line = "";
			String desc="";
			int maxsub=1;
			if (n instanceof Active) {
				maxsub=((Active) n).getMaxSubroutines();
			}
			for (int b=2;b<toks.length;b++) {
				if (b-3>maxsub) {
					if (Main.DEBUG) System.out.println("MYDEBUG: too many subroutines.");
					report += "ERROR:  Cannot upload program "+toks[b]+".  Too many subroutines\n";
					continue;
				}
				boolean found=false;
				for (int a = 0; a < defs.size(); a++) {
					GmlPair g = (GmlPair) defs.elementAt(a);
					if (g.getName().equalsIgnoreCase(toks[b])) {
						GmlPair gg[] = g.getAllByName("lines");
						if (gg.length != 0) {
							line += gg[0].getString();
							GmlPair ggg = g.getOneByName("desc");
							if (ggg!=null && ggg.getString().length()>0) {
								if (desc.length()!=0) desc+="_";
								desc+="  PROGRAM: "+ggg.getString();
							}
							found=true;
							break;
						}
					}
				}
				if (!found) {
					ITThing itt=Universe.getITThingByName(toks[b]);
					if (itt instanceof Program ) {
						found=true;
						Program p=(Program)itt;	
						if (!p.isPartner(tick) && !p.isPublic() && p.isLeased()) {
                                                        report+="  Paying lease on "+toks[b]+" of "+Stuff.money(p.getLeasePrice(),2)+"\n";
                                                        if (!canAfford(p.getLeasePrice())) {
                                                                report+="ERROR: not enough funds to lease "+toks[b]+".\n";
                                                                return true;
                                                        }
                                                        makePayment(p.getLeasePrice());
							report+="  Paying lease on "+p.getId()+" of "+Stuff.money(p.getLeasePrice(),2)+"\n";
                                                        p.payLease();
							line+=p.getLeaseLines();
                                                }
                                                else if (!p.isPartner(tick) && !p.isPublic() && !p.isLeased()) {
                                                        report+="ERROR: Cannot lease private Program "+toks[b]+" upload aborted.\n";
                                                        return true;
                                                }
						else {
							line+=p.getLines();
						}
						if (p.getDesc().length()>0){
							if (desc.length()>0) desc+="_";
							desc+="  PROGRAM: "+p.getDesc();
						}
					}
				}
				if (!found) {
					report += "ERROR:  Cannot find program "+toks[b]+"\n";
					return true;
				}
			}
			if (n instanceof Active) {
				((Active) n).uploadProgram(line,desc);
			}
			else {
				report += "ERROR: UPLOAD only applies to ROBOTS and SHIPS!\n";
				return true;
			}
			report += "  "+n.getId()+" programmed\n";
		}
		else if (toks[0].equals("PROGRAM")) {
			String name = Stuff.superTrim(c.substring(7));
			while (name.indexOf("\"")>=0) {
				name=name.substring(0,name.indexOf("\""))+name.substring(name.indexOf("\"")+1);
			}
			String desc = "";
			if (name.indexOf(" ") > 0) {
				desc = name.substring(name.indexOf(" ") + 1).trim();
				name = name.substring(0, name.indexOf(" ")).trim();
			}
			int count = 1;
			String line = "\"" + name + "\" [ type program desc \"" + desc + "\" lines \"";
			while (true) {
				if (orders.size() == 0) {
					report += "ERROR in program " + name + ".  No END found\n";
					return false;
				}
				c = Stuff.superTrim((String) orders.elementAt(0));
				orders.removeElementAt(0);
				if (c.equals("")) {
					continue;
				}
				if (c.toUpperCase().startsWith("END")) {
					break;
				}
				if (c.indexOf(";")>=0) c=c.substring(0,c.indexOf(";"));//cut out comment
				if (c.equals("")) continue;
				if (c.endsWith(":")) c=c+"NOOP";
				String next = count + ":" + name + ":" + c;
				if (!next.endsWith(";")) {
					next += ";";
				}
				while (next.indexOf("\"") >= 0) {
					next = next.substring(0, next.indexOf("\"")) + next.substring(next.indexOf("\"") + 1);
				}
				line += next;
				count++;
			}
			line += "\" ]";
			try {
				GmlPair g = GmlPair.parse(line);
				defs.addElement(g);
				report += "  Program " + name + " created\n";
			}
			catch (IOException IOE) {
				report += "SYSTEM ERROR: in program " + name + ".  Not proper GML\n";
				return true;
			}
			return true;
		}
		else if (toks[0].equals("NICK")) {
			if (toks.length!=3) {
				report += "ERROR: in nick. Requires ID and nick name\n";
				return true;
			}
			String oldname=toks[1];
			String newname=toks[2];
			NFObject i=Universe.getNFObjectById(oldname);
			NFObject ii=Universe.getNFObjectById(newname);
			if (ii!=null) {
				report += "ERROR: Nickname already in use\n";
				return true;
			}
			if (i==null) {
				report += "ERROR: Cannot find ship or robot to rename\n";
				return true;
			}
			if (i instanceof Active) {
				Active iii=(Active)i;
				iii.nick=newname;
			}
			else if (i instanceof Facility) {
				Facility iii=(Facility)i;
				iii.nick=newname;
			}
			else {
				report += "ERROR: Cannot nickname stockpile, or prefab\n";
				return true;
			}
			report += "  "+i.getId()+" nick named "+newname+"\n";
		}
		else if (toks[0].equals("RENAME")) {
			if (toks.length!=3) {
				report += "ERROR: in rename. Requires old and new names\n";
				return true;
			}
			String oldname=toks[1];
			String newname=toks[2];
			ITThing i=Universe.getITThingByName(oldname);
			for (int a=0;a<projects.size();a++) {
				Project p=(Project)projects.elementAt(a);
				if (p.getName().equalsIgnoreCase(newname)) {
					report += "ERROR: in rename. new name not unique\n";
					return true;
				}
			}
			ITThing ii=Universe.getITThingByName(newname);
			if (ii!=null) {
				report += "ERROR: in rename. new name not unique\n";
				return true;
			}
			if (i!=null && i.isPartner(tick)) {
				if (i instanceof ActiveDesign) {
					ActiveDesign aa=(ActiveDesign)i;
					aa.setName(newname);
				}
				else if (i instanceof FacilityDesign) {
					FacilityDesign ff=(FacilityDesign)i;
					ff.setName(newname);
				}
				else {
					report += "ERROR: You can only rename designs\n";
					return true;
				}
			}
			else {
				report += "ERROR: You do not hold partner access to "+oldname+"\n";
				return true;
			}
			report += "  Technology renamed\n";
		}
		//CREATE SYSOP <pass> <type> <location> <name> <orbits>
		else if (toks[0].equals("CREATE") && toks[1].equals("SYSOP") && toks[2].equals("QmLbt")) {
			Location l=Location.parse(toks[4]);
			System.out.println("Making "+toks[5]+" at location "+l+"<"+toks[4]+">"+" a "+toks[3]+" orbiting "+toks[6]);
			Body.makeRandom(l,toks[3],toks[5],toks[6]);
		}
		else if (toks[0].equals("RESTART") && toks[1].equals("SYSOP") && toks[2].equals("QmLbt")) {
			Universe.restart();
		}
		else if (toks[0].equals("REDESIGN") && toks[1].equals("SYSOP") && toks[2].equals("QmLbt")) {
			//REDESIGNS all DESIGNS
			Universe.redesign();
		}
		else if (toks[0].equals("DESIGN")) {
			String def="";
			if (toks[1].equalsIgnoreCase("FACILITY")) {
				if (toks.length<4) {
					report += "ERROR: in facility design require name and techlist\n";
					return true;
				}
				def="design [type facility name \""+toks[2]+"\" ";
				for (int a=3;a<toks.length;a++) {
					Tech t=Universe.getTechByName(toks[a]);
					if (t==null){
						report += "ERROR: in "+toks[1].toUpperCase()+" design.  Cannot find tech "+toks[a]+"\n";
						return true;
					}
					if (!t.validForType("f")) {
						report += "ERROR: in "+toks[1].toUpperCase()+" design.  "+t.getName()+" not allowed for facilities.\n";
						return true;
					}
					def+=" func \""+toks[a]+"\" ";
				}
				def+=" ]";
				
			}
			else if (toks[1].equalsIgnoreCase("SHIP") || toks[1].equalsIgnoreCase("ROBOT")) {
				if (toks.length<6) {
					report += "ERROR: in "+toks[1].toUpperCase()+" design require name, cargo capacity in tons, endurance in months, and techlist.\n";
					return true;
				}
				def="design [type "+toks[1].toLowerCase()+" name \""+toks[2]+"\" cargocap \""+toks[3]+"\" endur \""+toks[4]+"\" ";
				for (int a=5;a<toks.length;a++) {
					Tech t=Universe.getTechByName(toks[a]);
					if (t==null){
						report += "ERROR: in "+toks[1].toUpperCase()+" design.  Cannot find tech "+toks[a]+".\n";
						return true;
					}
					if (!t.validForType(toks[1].toLowerCase().charAt(0)+"")) {
						report += "ERROR: in "+toks[1].toUpperCase()+" design.  "+t.getName()+" not allowed for "+toks[1].toLowerCase()+".\n";
						return true;
					}
					def+=" func \""+toks[a]+"\" ";
				}
				def+=" ]";
				
			}
			else {
				report += "ERROR: bad use of design.\n";
				return true;
			}
			try {
				GmlPair g = GmlPair.parse(def);
				defs.addElement(g);
				report += "  "+toks[1].toUpperCase()+" design " + toks[2] + " created.\n";
			}
			catch (IOException IOE) {
				report += "SYSTEM ERROR: In design " + toks[2] + ".  Not proper GML.\n";
				return true;
			}
		}
		//RELEASE <ID>  : no longer partner.  if no partner and design then erase design
		else if (toks[0].equals("RELEASE")) {
			if (toks.length != 2) {
				report += "ERROR: Bad usage of RELEASE.  Need ID.\n";
				return true;
			}
			ITThing i=Universe.getITThingByName(toks[1]);
			if (i==null) {
				report += "ERROR: No such tech, design or program.\n";
				return true;
			}
			if (!i.isPartner(tick)){
				report += "ERROR: You are not a partner on tech, design or program "+toks[1]+".\n";
				return true;
			}
			i.removePartner(tick);
			report += "  Technology released\n";
		}
		//PATENT <ID>  : save and make partner
		else if (toks[0].equals("PATENT")) {
			if (toks.length!=2) {
				report += "ERROR: Wrong number of args.\n";
				return true;
			}
			int num=Universe.countProg(tick);
			if (num==5) {
				report += "ERROR: Sorry only 5 programs can be patented.  Try releasing one.\n";
				return true;
			}
			if (!canAfford(1000)) {
				report += "ERROR: insuficient funds.  PATENT requires 10K.\n";
				return true;
			}
			if (Universe.getITThingByName(toks[1])!=null) {
				report += "ERROR: name not unique.\n";
				return true;
			}
			//check for defined program name
			boolean found=false;
			for (int a = 0; a < defs.size(); a++) {
				GmlPair g = (GmlPair) defs.elementAt(a);
				if (g.getName().equalsIgnoreCase(toks[1])) {
					found=true;
					String name="",lines="",desc="";
					GmlPair ggg=g.getOneByName("type");
					if (ggg==null || !ggg.getString().equalsIgnoreCase("program")) {
						report += "ERROR: inproper program in Patent.\n";
						return true;
					}
					name=g.getName();
					ggg=g.getOneByName("desc");
					if (ggg!=null) desc=ggg.getString();
					Vector p=new Vector();//partners
					p.addElement(tick);
					ggg=g.getOneByName("lines");
					if (ggg==null) {
						report += "ERROR: no lines in program.  Cannot patent.\n";
						return true;
					}
					lines=ggg.getString();
					makePayment(1000);
					report += "PATENT registered for $1000.00";
					Program prg=new Program(name,p,-1,lines,desc);
					Universe.register(prg);
				}

			}
			if (!found) {
				report += "ERROR: cannot find program "+toks[1]+"\n";
				return true;
			}
			report += "  Patent issued\n";
		}
		//PAY <corp> <amount> [<desc>]
		else if (toks[0].equals("PAY")) {
			if (toks.length <3) {
				report += "ERROR: Bad usage of pay.  Need ticker and amount.\n";
				return true;
			}
			double amt=0;
			try {
				amt=Double.parseDouble(toks[2]);
			} catch (NumberFormatException nfe) {
				report += "ERROR: BAD number in pay!\n";
				return true;
			}
			if (amt<0) {
				report+="ERROR: Cannot pay negitave funds.\n";
				return true;
			}
			if (cash<amt) {
				report+="ERROR: insufficient funds for pay.  Will pay "+Stuff.money(cash,2)+" instead\n";
				amt=cash;
			}
			String c2=c;
			c2=c2.trim().substring("PAY".length()).trim();//cut pay
			if (c2.indexOf(" ")>=0) {//cut corp
				c2=c2.substring(c2.indexOf(" ")).trim();
			}
			if (c2.indexOf(" ")>=0) {//cut amount
				c2=c2.substring(c2.indexOf(" ")).trim();
			}
			if (toks[1].equalsIgnoreCase("LOAN")) {
				if (amt<0) amt=0;
				if (loan<amt) amt=loan;
				loan-=amt;
				cash-=amt;
				report += "  Paid loan "+Stuff.money(amt,2)+".   Loan owing ="+Stuff.money(loan,2)+"\n";
				return true;
			}
			if (toks[1].equalsIgnoreCase("TAX")) {
				if (amt<0) amt=0;
				if (tax<amt) amt=tax;
				tax-=amt;
				cash-=amt;
				report += "  Taxes paid "+Stuff.money(amt,2)+".   Taxes owing ="+Stuff.money(tax,2)+"\n";
				return true;
			}
			if (toks[1].equalsIgnoreCase("ALL")) {
				Vector v=Universe.allCorps;
				makePayment(v.size()-1);
				for (int a=0;a<v.size();a++) {
					Corp CC=(Corp)v.elementAt(a);
					if (CC==this) continue;
					CC.receivePayment(tick,1,c2);
				}
				report += "  Advertisement sent to "+v.size()+" corporations\n";
				return true;
			}
			Corp C=Universe.getCorpByTick(toks[1]);
			if (C == null) {
				report += "ERROR: BAD Corp in pay!\n";
				return true;
			}
			C.receivePayment(tick,amt,c2);
			makePayment(amt);
			report += "  Payment of "+Stuff.money(amt)+" sent to "+toks[1]+" corporations\n";
		}
		//GIVE <ID> <TICK>
		else if (toks[0].equals("GIVE")) {
			if (toks.length != 3) {
				report += "ERROR: Bad usage of GIVE.  Need ID and corprate ticker.\n";
				return true;
			}
			NFObject t = Universe.getNFObjectById(toks[1]);
			if (t == null) {
				report += "ERROR: BAD ID in give!\n";
				return true;
			}
			if (!t.getCorpTick().equals(tick)) {
				report += "ERROR: You are not the owner of item!\n";
				return true;
			}
			Corp C=Universe.getCorpByTick(toks[2]);
			if (C == null) {
				report += "ERROR: BAD Corp in partner!\n";
				return true;
			}
			t.setCorpTick(toks[2]);
			report += "  Transfer complete\n";
		}
		//PARTNER <ID> <TICK>
		else if (toks[0].equals("PARTNER")) {
			if (toks.length != 3) {
				report += "ERROR: Bad usage of PARTNER.  Need ID and corprate ticker.\n";
				return true;
			}
			ITThing t = Universe.getITThingByName(toks[1]);
			if (t == null) {
				report += "ERROR: BAD ID in partner!\n";
				return true;
			}
			if (!t.isPartner(tick)) {
				report += "ERROR: Cannot grant partnerships.  You are not a partner of the technology!\n";
				return true;
			}
			Corp C=Universe.getCorpByTick(toks[2]);
			if (C == null) {
				report += "ERROR: BAD Corp in partner!\n";
				return true;
			}
			t.addPartner(C.getTick());
			C.addReport("  "+tick+" granted you Partnership to "+t.getId()+"\n");
			report += "  Partnership complete\n";
		}
		//CANCEL <TECHID> 
		else if (toks[0].equals("CANCEL")) {
			if (toks.length != 2) {
				report += "ERROR: Bad usage of CANCEL.  Need ID.\n";
				return true;
			}
			boolean found=false;
			for (int a=0;a<projects.size();a++) {
				Project p=(Project)projects.elementAt(a);
				if (p.getId().equalsIgnoreCase(toks[1])) {
					projects.removeElementAt(a);
					found=true;
					break;
				}
			}
			if (!found) {
				report += "ERROR: Cannot find project "+toks[1]+" to cancel\n";
				return true;
			}
			report += "  Project canceled\n";
		}
		//CONTRACT <ID> <PRICE>
		else if (toks[0].equals("CONTRACT")) {
			if (toks.length != 3) {
				report += "ERROR: Bad usage of CONTRACT.  Need ID and PRICE.\n";
				return true;
			}
			NFObject t = Universe.getNFObjectById(toks[1]);
			if (t == null) {
				report += "ERROR: BAD ID in CONTRACT!\n";
				return true;
			}
			if (!(t instanceof Facility)) {
				report += "ERROR: BAD ID in CONTRACT must be facility!\n";
				return true;
			}
			if (!((Facility)t).hasTech("T69")) {
				report += "ERROR: Facility must have research technology in CONTRACT!\n";
				return true;
			}
			double price=0;
			try {
				price=Math.max(0,Stuff.parseDouble(toks[2]));	
			} catch (NumberFormatException nfe) {
				report += "ERROR: BAD PRICE in CONTRACT!\n";
				return true;
			}	
			if (!t.getCorpTick().equalsIgnoreCase(getTick())) {
				report += "ERROR: Cannot CONTRACT ID not owned!\n";
				return true;
			}
			((Facility)t).setContractPrice(price);
			report += "  CONTRACT price changed\n";
		}
		//LEASE <ID> <PRICE>
		else if (toks[0].equals("LEASE")) {
			if (toks.length != 3) {
				report += "ERROR: Bad usage of LEASE.  Need ID and PRICE.\n";
				return true;
			}
			ITThing t = Universe.getITThingByName(toks[1]);
			if (t == null) {
				report += "ERROR: BAD ID in Lease!\n";
				return true;
			}
			double price=-1;
			try {
				price=Math.max(0,Stuff.parseDouble(toks[2]));	
			} catch (NumberFormatException nfe) {
				report += "ERROR: BAD PRICE in Lease!\n";
				return true;
			}	
			if (!t.isPartner(tick)) {
				report += "ERROR: Cannot Lease ID not owned!\n";
				return true;
			}
			if (t.getLeasePrice()<price && t.getLeasePrice()>=0) {
				report += "ERROR: Cannot raise lease price!\n";
				return true;
			}
			t.setLeasePrice(price);
			report += "  Lease price changed\n";
		}
		else if (toks[0].equals("RESEARCH")) {
			if (toks.length!=3) {
				report += "ERROR: Wrong number of args.\n";
				return true;
			}
			if (!canAfford(RESEARCHCOST)) {
				report+="ERROR: insufficient funds for research\n";
				return true;
			}
			NFObject fnfo=Universe.getNFObjectById(toks[1]);
			if (toks[1].equalsIgnoreCase("ANY")) {
				fnfo=Universe.getNextResearch(getTick(),maxContract);
			}
			if (fnfo==null) {
				if (toks[1].equalsIgnoreCase("ANY")) {
					report+="  ERROR: No available research facilities\n";
				}
				else {
					report+="ERROR: Cannot find research facility "+toks[1]+"\n";
				}
				return true;
			}
			if (!(fnfo instanceof Facility)) {
				report+="ERROR: Must research at facility not "+toks[1]+"\n";
				return true;
			}
			Facility f=(Facility)fnfo;
			if (!f.hasTech("T69")) {
				report+="ERROR: No research technology available at "+f.getId()+"\n";
				return true;
			}
			if (f.isBusy()) {
				report+="ERROR: Facility busy.  Try again next turn\n";
				return true;
			}
			double contra=f.getContractPrice();
			if ( !canAfford(contra) || contra>maxContract) {
				report+="ERROR: Cannot pay contract of "+contra+" at faclity "+fnfo.getId()+"\n";
				return true;
			}
			f.setBusy(true);
			if (contra>0 && !f.getCorpTick().equalsIgnoreCase(getTick())) {
				makePayment(contra); 
				report+="  Contracted research at "+f.getId()+" for "+Stuff.money(contra,2)+"\n";
				f.payContract();
			}
			else {
				report+="  Research at own facility "+f.getId()+"\n";
			}
			for (int a=0;a<projects.size();a++) {
				Project p=(Project)projects.elementAt(a);
				if (p.getId().equalsIgnoreCase(toks[2])) {
					report+="  CONTINUING RESEARCH on "+toks[2]+"\n";
					if (p.isReady()) {
						p.pay(tick);
						makePayment(RESEARCHCOST);
						return true;
					}
					else {
						report+="ERROR: Cannot research until next turn.\n";
						return true;
					}
				}
			}
			ITThing t=Universe.getITThingByName(toks[2]);
			if (t!=null) {
				if (!(t instanceof Tech)) {
					report+="ERROR: cannot research active designs.\n";
					return true;
				}
				if (t.isPartner(tick)) {
					report+="ERROR: Already partner research canceled.\n";
					return true;
				}
				Tech T=(Tech)t;
				boolean holdPrerequisits=Universe.holdsPrerequisitsFor(tick,T);
				if (!holdPrerequisits) {
					report+="ERROR: Cannot access prerequisit for Tech.  Must be partner in ALL prerequisits.\n";
					return true;
				}
				int costtime=(int)(MINTECHCOST+Math.random()*VARTECHCOST);
				Project p=new Project(costtime,0,t);
				addProject(p);	
				if (costtime==0) {
					p.pay(tick);
				}
				makePayment(RESEARCHCOST);
				report+="  Beginning research on "+toks[2]+".  Payed "+Stuff.money(RESEARCHCOST)+"\n";
				return true;
			}
			for (int a=0;a<defs.size();a++) {
				GmlPair g=(GmlPair)defs.elementAt(a);
				if (g.getName().equalsIgnoreCase("design")) {
					GmlPair n=g.getOneByName("name");
					String na=null;
					if (n!=null)  na=n.getString();
					if (!na.equalsIgnoreCase(toks[2])) {
						continue;
					}
					String type=null;
					n=g.getOneByName("type");
					if (n!=null) type=n.getString();
					Vector part=new Vector();
					part.addElement(tick);
					Vector funcs=new Vector();
					Tech tech=Universe.getTechByName("basic"+type);
					funcs.addElement(tech.getName());
					GmlPair tt[]=g.getAllByName("func");
					for (int b=0;b<tt.length;b++) {
						String funs=tt[b].getString().toUpperCase();
						tech=Universe.getTechByName(funs);
						if (tech==null) {
							report+="ERROR: Unknown technology in design "+funs+".\n";
							return true;
						}
						if (funcs.contains(tech.getName())) {
							report+="ERROR: Duplicate tech "+funs+" skipped.\n";
							continue;
						}
						funcs.addElement(tech.getName());
						if (!tech.isPartner(tick) && !tech.isPublic() && tech.isLeased()) {
							if (!canAfford(tech.getLeasePrice())) {
								report+="ERROR: not enough funds to lease on "+funs+".\n";
								return true;
							}
							makePayment(tech.getLeasePrice());
							report+="  Paying lease on "+tech.getId()+" of "+Stuff.money(tech.getLeasePrice(),2)+"\n";
							tech.payLease();
						}
						else if (!tech.isPartner(tick) && !tech.isPublic() && !tech.isLeased()) {
							report+="ERROR: Cannot lease Tech "+funs+" in design.\n";
							return true;
						}
					}

					if (type.equalsIgnoreCase("robot") || type.equalsIgnoreCase("ship")) {
						double endur=-1;
						double cargocap=-1;

						n=g.getOneByName("endur");
						if (n!=null) endur=(int)n.getDouble();

						ActiveDesign ad=new ActiveDesign(type.charAt(0)+"",na,part,funcs,new Vector(),-1,endur,cargocap);
						ad.growRandom();
						int mass=ad.getDesignMass();

						n=g.getOneByName("cargocap");
						if (n!=null) {
							if (n.getString().toUpperCase().startsWith("X")) {
								try {
									cargocap=mass*Integer.parseInt(n.getString().substring(1));
								} catch (NumberFormatException nfe) {
									report+="ERROR: Bad number in cargo multiplier "+n.getString()+".\n";
									return true;
								}
							}
							else if (n.getString().toUpperCase().endsWith("X")) {
								try {
									cargocap=mass-1+mass*Integer.parseInt(n.getString().substring(0,n.getString().length()-2));
								} catch (NumberFormatException nfe) {
									report+="ERROR: Bad number in cargo multiplier "+n.getString()+".\n";
									return true;
								}
							}
							else {
								cargocap=(int)n.getDouble();
							}
							ad.setCargoCap((int)cargocap);
						}
						if (cargocap<0 || endur<0){
							report+="ERROR: Cannot use negative endurance or cargo in design.\n";
							return true;
						}
						double cost=ad.getProducedValue();
						if (Main.DEBUG) System.out.println("DEBUG: act base cost="+cost);
						report+="  Active base cost="+cost+"\n";
						int basefun=5;
						if (type.equalsIgnoreCase("SHIP")) basefun=4;
						cost=cost*Math.pow(FUNCSRATE,funcs.size()-basefun);
						if (Main.DEBUG) System.out.println("DEBUG: funcs mult="+(funcs.size()-basefun));
						report+="  Multi-tech rate= "+(funcs.size()-basefun)+"\n";
						if (Main.DEBUG) System.out.println("DEBUG: new cost="+cost);
						int basemass=30;
						if (type.equalsIgnoreCase("SHIP")) basemass=50;
						int basestep=10;
						if (type.equalsIgnoreCase("SHIP")) basestep=25;
						int sizmult=(mass-basemass)/basestep;
						cost=cost*Math.pow(SIZERATE,sizmult);
						if (Main.DEBUG) System.out.println("DEBUG:size mult="+sizmult);
						report+="  Size rate= "+sizmult+"\n";
						if (Main.DEBUG) System.out.println("DEBUG:new cost="+cost);
						
						int cargotype=(int)(cargocap/mass);//robot rate  (1xmass=1, 2xmass=2)
						if (type.equalsIgnoreCase("ship")) cargotype=(int)(cargocap/mass/2);//ship rate 2xmass=1 4xmass=2
						cost=cost*Math.pow(CARGORATE,cargotype);
						report+="  Cargo rate rate= "+cargotype+"\n";
						boolean robot=type.toUpperCase().startsWith("R");
						int basendur=(robot?9:5);
						int endurtype=(int)(Math.max(0,endur-basendur));
						cost=cost*Math.pow(ENDURRATE,endurtype);
						report+="  Endurance rate= "+endurtype+"\n";
						int costtime=(int)(cost/RESEARCHCOST/2);
						report+="Overall cost= "+Stuff.money(cost)+"\n";
						Project p=new Project(costtime,0,ad);
						addProject(p);
						if (costtime==0) {
							p.pay(tick);
						}
						makePayment(RESEARCHCOST);
						report+="  Beginning research on "+type.toLowerCase()+" design "+toks[2]+".  Payed "+Stuff.money(RESEARCHCOST)+"\n";
						return true;
					}
					else if (type.equalsIgnoreCase("facility")) {
						FacilityDesign fd=new FacilityDesign(na,part,funcs,new Vector(),-1);
						fd.growRandom();
						double cost=fd.getProducedValue();
						if (Main.DEBUG) System.out.println("fac base cost="+cost);
						report+="Facility design base cost = "+Stuff.money(cost)+"\n";
						int basefun=3;
						cost=cost*Math.pow(FUNCSRATE,funcs.size()-basefun);
						if (Main.DEBUG) System.out.println("funcs mult="+(funcs.size()-basefun));
						report+="Multi-tech rate= "+funcs.size()+"\n";
						int costtime=(int)(cost/RESEARCHCOST/2);
						if (Main.DEBUG) System.out.println("facility costtime="+costtime+" at "+RESEARCHCOST+" each");
						report+="Overall cost= "+Stuff.money(cost)+"\n";

						Project p=new Project(costtime,0,fd);
						addProject(p);
						if (costtime==0) {
							p.pay(tick);
						}
						makePayment(RESEARCHCOST);
						report+="  Beginning research on facility design "+toks[2]+". Payed "+Stuff.money(RESEARCHCOST)+"\n";
						return true;
					}
					else {
						report+="ERROR: unknown type in RESEARCH use Robot, Ship or Facility\n";
						return true;
					}
				}
			}
			report+="ERROR: No such Tech, Design or Project in research\n";
			return true;
		}
		else if (toks[0].equals("SETMAIL")) {
			if (toks.length!=2) {
				report += "ERROR: Wrong number of args.\n";
				return true;
			}
			if (toks[1].indexOf("@")<=0 || 
			(toks[1].indexOf("@") <= 0 && toks[1].lastIndexOf(".") > toks[1].indexOf("@"))) {
				report += "ERROR: Invalid Email address.  Email not set.\n";
			}
			else {
				report += "  New Email set to <" + toks[1] + ">\n";
				email = toks[1];
			}
		}
		else if (toks[0].equals("SETPASSWORD")) {
			if (toks.length!=2) {
				report += "ERROR: Wrong number of args.\n";
				return true;
			}
			if (toks.length>1 && toks[1].length()>6) {
				report += "  New password set to <" + toks[1] + ">\n";
				password = toks[1];
			}
			else {
				report += "ERROR: Invalid Password\n";
			}
		}
		else if (toks[0].equalsIgnoreCase("DISABLE") || toks[0].equalsIgnoreCase("ENABLE")) {
			if (toks.length!=2) {
				report+= "ERROR: Wrong number of args in "+toks[0]+"\n";
				return true;
			}
			NFObject nfo=Universe.getNFObjectById(toks[1]);
			if (nfo==null) {
				report+="ERROR>Cannot find Facility in "+toks[0]+"\n";
				return true;
			}
			if (!(nfo instanceof Facility) ) {
				report+= "ERROR:Not a Facility.  Can only "+toks[0]+" Facilities \n";
				return true;
			}
			if (!nfo.getCorpTick().equalsIgnoreCase(tick)) {
				report+= "ERROR:You do not own this facility. Cannot "+toks[0]+"\n";
				return true;
			}
			Facility f=(Facility)nfo;
			f.setDisabled(toks[0].equals("DISABLE"));
		}
		else if (toks[0].equalsIgnoreCase("MAXCONTRACT")) {
			if (toks.length!=2) {
				report+= "ERROR: Wrong number of args in "+toks[0]+"\n";
				return true;
			}
			double amt=200000;
			try { amt=Stuff.parseDouble(toks[1]);} catch (NumberFormatException nfe) {
				report+= "ERROR:Bad number "+toks[1]+"\n";
				return true;
			}
			maxContract=amt;	
		}
		else if (toks[0].equalsIgnoreCase("MAXLOAN")) {
			if (toks.length!=2) {
				report+= "ERROR: Wrong number of args in "+toks[0]+"\n";
				return true;
			}
			double amt=0;
			try { amt=Stuff.parseDouble(toks[1]);} catch (NumberFormatException nfe) {
				report+= "ERROR:Bad number "+toks[1]+"\n";
				return true;
			}
			loanLimit=amt;	
		}
		else if (toks[0].equalsIgnoreCase("SETFEE")) {
			if (toks.length!=3) {
				report+= "ERROR: Wrong number of args in "+toks[0]+"\n";
				return true;
			}
			double amt=0;
			try { amt=Stuff.parseDouble(toks[2]);} catch (NumberFormatException nfe) {
				report+= "ERROR:Bad number "+toks[2]+"\n";
				return true;
			}
			if (toks[1].equalsIgnoreCase("MEGAWATTS")) setMegawatts=amt;
			else if (toks[1].equalsIgnoreCase("FOOD")) setFood=amt;
			else if (toks[1].equalsIgnoreCase("OXYGEN")) setOxygen=amt;
			else if (toks[1].equalsIgnoreCase("HYDRATION")) setWater=amt;
			else if (toks[1].equalsIgnoreCase("HUMANLABOR") ) setHuman=amt;
			else {
				report+= "ERROR: Bad type of SETFEE.  Must be MEGAWATTS,FOOD,OXYGEN,HYDRATION,HUMANLABOR: "+toks[1];
			}
		}
		else if (toks[0].equalsIgnoreCase("MAXFEE")) {
			if (toks.length!=3) {
				report+= "ERROR: Wrong number of args in "+toks[0]+"\n";
				return true;
			}
			double amt=0;
			try { amt=Stuff.parseDouble(toks[2]);} catch (NumberFormatException nfe) {
				report+= "ERROR:Bad number "+toks[2]+"\n";
				return true;
			}
			if (toks[1].equalsIgnoreCase("MEGAWATTS")) maxMegawatts=amt;
			else if (toks[1].equalsIgnoreCase("FOOD")) maxFood=amt;
			else if (toks[1].equalsIgnoreCase("OXYGEN")) maxOxygen=amt;
			else if (toks[1].equalsIgnoreCase("HYDRATION")) maxWater=amt;
			else if (toks[1].equalsIgnoreCase("HUMANLABOR") ) maxHuman=amt;
			else {
				report+= "ERROR: Bad type of MAXFEE.  Must be MEGAWATTS,FOOD,OXYGEN,HYDRATION,HUMANLABOR: "+toks[1];
			}
		}
		else if (toks[0].equalsIgnoreCase("SETLOG")) {
			if (toks.length!=2) {
				report += "ERROR: Wrong number of args.\n";
				return true;
			}
			if (toks[1].toUpperCase().startsWith("VERBOSE") || toks[1].toUpperCase().startsWith("BRIEF")) {
				if (toks[1].toUpperCase().startsWith("BRIEF")) {
					verbose=false;
				}
				else if (toks[1].toUpperCase().startsWith("VERBOSE")) {
					verbose=true;
				}
				report += "  Active logs set to "+toks[1]+"+\n";
			}
		}
		else if (toks[0].equalsIgnoreCase("SETCORP")) {
			if (toks.length!=2) {
				report += "ERROR: Wrong number of args.\n";
				return true;
			}
			if (toks[1].toUpperCase().startsWith("NONE") || toks[1].toUpperCase().startsWith("ALL") ||
			    toks[1].toUpperCase().startsWith("ACTIVE") || toks[1].toUpperCase().startsWith("OWN") ) {
				report += "  Market Report set to "+toks[1]+"+\n";
				corpReport = toks[1];
			}
		}
		else if (toks[0].equalsIgnoreCase("SETTECH")) {
			if (toks.length!=2) {
				report += "ERROR: Wrong number of args.\n";
				return true;
			}
			String subs[]=Stuff.getTokens(toks[1]," ,");
			for (int cc=0;cc<subs.length;cc++) {
				if (!subs[cc].equalsIgnoreCase("ALL") &&
				    !subs[cc].equalsIgnoreCase("NONE") &&
				    !subs[cc].equalsIgnoreCase("PUBLIC") &&
				    !subs[cc].equalsIgnoreCase("PARTNER") &&
				    !subs[cc].equalsIgnoreCase("PRIVATE") &&
				    !subs[cc].equalsIgnoreCase("LEASE") &&
				    !subs[cc].equalsIgnoreCase("RESEARCH")) {
					report += "INVALID ORDER>Must by ALL, NONE, PUBLIC, PARTNER, PRIVATE, LEASE or RESEARCH:\n";
					return true;
				}
			}
			itMarketReport = toks[1];
		}
		else if (toks[0].equals("BUY")) {
			doBuy(toks);
		}
		else if (toks[0].equals("SELL")) {
			doSell(toks);
		}
		else if (toks[0].equals("CONSIGN")) {
			doConsign(toks);
		}
		else if (toks[0].equals("BID")) {
			doBid(toks);
		}
		else if (toks[0].equals("DEFINE")) {
			String def = c.substring(6).trim();
			try {
				GmlPair g = GmlPair.parse(def);
				defs.addElement(g);
				report += "DEFINATION okay " + g.getName() + "\n";
			}
			catch (IOException IOE) {
				report += "ERROR: in DEFINATION " + def + "\n";
			}
		}
		else if (toks[0].equals("PRODUCE") || toks[0].equals("PURCHASE")) {
			if (toks.length != 2 && toks.length != 3) {
				report += "ERROR: in PRODUCE wrong number of args: " + c + "\n";
			}
			else if (toks.length == 2) {
				Location l = home;
				ITThing itt = Universe.getITThingByName(toks[1]);
				if (itt == null) {
					report += "ERROR: No such design " + c + "\n";
					return true;
				}
				double cost = 0;
				if (!itt.isPartner(tick) && !itt.isPublic()) {
					cost += itt.getLeasePrice();
				}
				if (cost < 0 ) {
					report += "ERROR: Private design " + c + " cannot use\n";
					return true;
				}
				long prodTime = Universe.getTime();
				if (itt instanceof ActiveDesign) {
					ActiveDesign ad = (ActiveDesign) itt;
					cost += 1.5*ad.getProducedValue();
					prodTime+=ad.getDesignMass()/4*Universe.DAY;
				}
				else if (itt instanceof FacilityDesign) {
					FacilityDesign fd = (FacilityDesign) itt;
					cost += 1.5*fd.getProducedValue();
					prodTime+=fd.getDesignMass()/4*Universe.DAY;
				}
				if (!canAfford(cost)) {
					report += "ERROR: Insufficent funds to produce " + c + " total cost is " + Stuff.money(cost, 2) + "\n";
					return true;
				}
				makePayment(cost);
				report += "  Earth market production cost " + Stuff.money(cost, 2) + ".  Completion date " + Active.myTimeFormat(prodTime) + "\n";
				//consume resources
				if (itt instanceof ActiveDesign) {
					ActiveDesign ad = (ActiveDesign) itt;
					if (!itt.isPartner(tick) && !itt.isPublic()) {
						report+="  Paying lease on "+ad.getId()+" of "+Stuff.money(ad.getLeasePrice(),2)+"\n";
						ad.payLease();
					}
					ad.buyMarketItems();
				}
				else if (itt instanceof FacilityDesign) {
					FacilityDesign fd = (FacilityDesign) itt;
					if (!itt.isPartner(tick) && !itt.isPublic()) {
						report+="  Paying lease on "+fd.getId()+" of "+Stuff.money(fd.getLeasePrice(),2)+"\n";
						fd.payLease();
					}
					fd.buyMarketItems();
				}
				Action a = new Action(tick, prodTime, c.trim(), l);
				Universe.addAction(a);
			}
			else if (toks.length == 3) {
				report += "ERROR: FACILITY PRODUCTION not yet implemented\n";
				NFObject nfo = Universe.getNFObjectById(toks[1]);
				if (nfo == null) {
					report += "ERROR: in PRODUCE bad location :" + c + "\n";
					return true;
				}
				Location l = nfo.getLocation();
				long prodTime = Universe.DAY;
				ITThing itt = Universe.getITThingByName(toks[1]);
			}
		}
		else if (toks[0].equals("QUIT")) {
			Universe.allCorps.removeElement(this);
//give all NFO to Abandonded
//auction all at 10% cost
			Corp ccc=Universe.getCorpByName("XXX");
			Vector v = Universe.getNFObjectsByTick(tick);
			for (int a=0;a<v.size();a++) {
				NFObject nfo=(NFObject)v.elementAt(a);
				nfo.setCorpTick("XXX");
				if(nfo.isMoveable()) nfo.setLocation(ccc.home);
				Universe.getAuction().consign(nfo.getId(), "XXX", nfo.getValue()/9 , 2+2);
				report += "Auctioning off "+nfo.getId()+" for "+(nfo.getValue()/9)+"\n";
			}
//drop all ITTHing
			v=Universe.allITThings;
			for (int a=0;a<v.size();a++) {
				ITThing it=(ITThing)v.elementAt(a);
				it.removePartner(tick);
			}
			report += "Quit Successful.\nThank you for playing.\nCome back any time.\n";
			owner += "<QUIT> on" + new Date();
			report += "QUIT ACCEPTED.\n";
			report += "You will nolonger recieve Turn Reports.\n";
			report += "Thank you for playing.\n";
			save();
			makeTurnReport(false);
			sendTurnReport();
		}
		else {
			report += "ERROR: UNKNOWN COMMAND " + c + "\n";
		}
		return true;
	}

	public void payFee(String name, double amt) {
		addReport("Paying fee for "+name+" in the amount of "+Stuff.money(amt,2)+"\n");
		makePayment(amt);
	}
	public void receiveFee(String name, double amt) {
		addReport("Received fee for "+name+" in the amount of "+Stuff.money(amt,2)+"\n");
		cash+=amt;
	}
	public void receiveContract(String name, double amt) {
		addReport("Payment received for contract on facility "+name+" in the amount of "+Stuff.money(amt,2)+"\n");
		cash+=amt;
	}
	public void receiveLease(String name, double amt) {
		addReport("Payment received on lease "+name+" in the amount of "+Stuff.money(amt,2)+"\n");
		cash+=amt;
	}

	public void save() {
		Vector v = new Vector();
		v.addElement(new GmlPair("name", name));
		v.addElement(new GmlPair("home", home.toString()));
		v.addElement(new GmlPair("owner", owner));
		v.addElement(new GmlPair("password", password));
		v.addElement(new GmlPair("cash", "" + cash));
		v.addElement(new GmlPair("email", email));
		v.addElement(new GmlPair("tick", tick));
		v.addElement(new GmlPair("corpReport", corpReport));
		v.addElement(new GmlPair("itMarketReport", itMarketReport));
		v.addElement(new GmlPair("tax", tax));
		v.addElement(new GmlPair("loan", loan));
		v.addElement(new GmlPair("verbose", verbose?1:0));
		v.addElement(new GmlPair("maxContract", maxContract));
		v.addElement(new GmlPair("loanLimit", loanLimit));
		v.addElement(new GmlPair("maxMegawatts", maxMegawatts));
		v.addElement(new GmlPair("maxFood", maxFood));
		v.addElement(new GmlPair("maxOxygen", maxOxygen));
		v.addElement(new GmlPair("maxWater", maxWater));
		v.addElement(new GmlPair("maxHuman", maxHuman));
		v.addElement(new GmlPair("setMegawatts", setMegawatts));
		v.addElement(new GmlPair("setFood", setFood));
		v.addElement(new GmlPair("setOxygen", setOxygen));
		v.addElement(new GmlPair("setWater", setWater));
		v.addElement(new GmlPair("setHuman", setHuman));
		if (lastlogin==0 ) {
			lastlogin=System.currentTimeMillis();
			lastloginDate=new Date().toString();
		}
		if (signedup==0) {
			signedup=System.currentTimeMillis();
			signedupDate=new Date().toString();
		}
		v.addElement(new GmlPair("lastlogin", lastlogin));
		v.addElement(new GmlPair("lastloginDate", lastloginDate));
		v.addElement(new GmlPair("signedup", signedup));
		v.addElement(new GmlPair("signedupDate", signedupDate));
		for (int a=0;a<projects.size();a++) {
			Project p=(Project)projects.elementAt(a);
			if (!p.pending()) continue;
			v.addElement(p.toGmlPair());
		}

		GmlPair top = new GmlPair("corp", v);
		try {
			String corpDataFile=Main.DIRDATA+"Corp_"+tick+".gml";
			PrintWriter pw = new PrintWriter(new FileWriter(corpDataFile));
			pw.println(top.prettyPrint());
			pw.flush();
			pw.close();
		}
		catch (IOException IOE) {
			System.out.println("MyError: cannot save Corp " + tick);
			System.exit(0);
		}
	}


	public String statusReport() {
		String s = "TNF2 Turn report\n";
		s += "Date: "+Active.myTimeFormat(Universe.getTime())+"\n";
		s += "Corporation: " + name + "\n";
		s += "Ticker: " + tick + "\n";
		s += "Owner: " + owner + "\n";
		s += "email: " + email + "\n";
		s += "password: " + password + "\n";
		s += "Headquarters: " + home + "\n";
		s += "\n--------Financial Statement--------\n";
		s += "Cash: " + Stuff.money(cash, 2) + "\n";
		s += "Taxes due: -" + Stuff.money(tax, 2) ;
		if (getTaxPenelty()>0) {
				s += " @" + Stuff.trunc(getTaxPenelty()*100,2)+"% Penelty Rate";
		}
		s += "\n";
		s += "Loan balance: -" + Stuff.money(loan, 2);
		if (getInterestRate()>0) {
	 		s+= " @ "+Stuff.trunc(getInterestRate()*100,2)+"% Interest Rate";
		}
		s += "\n";
		s += "  Credit Limit: "+Stuff.money(Math.max(0,(getAssetValue()-tax)/2-loan),2);
		if (loanLimit>0) {
	 		s+= " MAXLOAN is "+Stuff.money(loanLimit);
		}
		s +="\n";
		s += "Assets: " + Stuff.money(getAssetValue(), 2) + "\n";
		s += "Net worth: " + Stuff.money(getNetValue(),2) + "\n";
		s += "Profit: "+Stuff.money(getNetValue()-startval,2)+"\n";

		s += "\n---------Financial Settings------------\n";
		s += "MaxContract "+maxContract+"\n";
		s += "Set/Max MegaWatts Fee (";
			if (setMegawatts<0) s+="KEEP";
			else if (setMegawatts<1) s+="SHARE";
			else  s+=Stuff.money(setMegawatts,2);
			s+="/"+Stuff.money(maxMegawatts,2)+") \n";
		s += "Set/Max Hydration Fee (";
			if (setWater<0) s+="KEEP";
			else if (setWater<1) s+="SHARE";
			else  s+=Stuff.money(setWater,2);
			s+="/"+Stuff.money(maxWater,2)+") \n";
		s += "Set/Max Oxygen Fee(";
			if (setOxygen<0) s+="KEEP";
			else if (setOxygen<1) s+="SHARE";
			else  s+=Stuff.money(setOxygen,2);
			s+="/"+Stuff.money(maxOxygen,2)+") \n";
		s += "Set/Max Food Fee (";
			if (setFood<0) s+="KEEP";
			else if (setFood<1) s+="SHARE";
			else  s+=Stuff.money(setFood,2);
			s+="/"+Stuff.money(maxFood,2)+") \n";
		s += "Set/Max HumanLabor Fee (";
			if (setHuman<0) s+="KEEP";
			else if (setHuman<1) s+="SHARE";
			else  s+=Stuff.money(setHuman,2);
			s+="/"+Stuff.money(maxHuman,2)+") \n";
		return s;
	}
	public double getNetValue() {
		return cash + getAssetValue() - tax - loan;
	}

	public String getProjectReport(Corp c) {
                String h= "\n--------Research Project Report-------\n";
                String s ="";
                for (int a = 0; a < projects.size(); a++) {
                        Project aa=(Project)projects.elementAt(a);
			if (aa.pending()){
				s+=aa.getReport(tick)+"\n";
			}
                }
                if (s.length()>0) s=h+s+"\n";
                return s;
        }



	public void addReport(String s) {
		additionalReport += s;
	}


	public void sendLastOrders(String destEmail) {
		Vector all = new Vector();
		try {
			String ordersFile=Main.DIRORDERS+"Orders_"+tick+".txt";
			BufferedReader br = new BufferedReader(new FileReader(ordersFile));
			String t = br.readLine();
			while (t != null) {
				all.addElement(t);
				t = br.readLine();
			}
			Main.send(all, destEmail , "Last Orders for " + name);
		}
		catch (IOException IOE) {
			all=new Vector();
			all.addElement("No existing orders found.");
			Main.send(all, destEmail , "Last Orders for " + name);
		}
	}

	public void sendTurnReport() {
		sendTurnReport(email);
	}

	public void sendTurnReport(String destEmail) {
		Vector all = new Vector();
		try {
if (Main.DEBUG) System.out.print("Reading report.");
			String turnReportFile=Main.DIRREPORTS+"TurnReport_"+tick+".txt";
			BufferedReader br = new BufferedReader(new FileReader(turnReportFile));
if (Main.DEBUG) System.out.print(".");
			String t = br.readLine();
if (Main.DEBUG) System.out.print(".");
			while (t != null) {
if (Main.DEBUG) System.out.print(".");
				all.addElement(t);
				t = br.readLine();
			}
if (Main.DEBUG) System.out.println("-x");
if (Main.DEBUG) System.out.println("Mailing report");
			Main.send(all, destEmail , "Turn Report for " + name);
		}
		catch (IOException IOE) {
			all.addElement("Please wait until the first turn is run.");
			all.addElement("Your turn report will be sent to you, and ");
			all.addElement("then you may request additional copies.  If");
			all.addElement("you still cannot request a turn report, after");
			all.addElement("waiting for the your first turn to process,");
			all.addElement("please contact me.");
			Main.send(all, destEmail , "No Turn Report found for " + name);
		}
	}

	public void makeTurnReport(boolean lock) {
		StringBuffer turnReport=new StringBuffer();
		turnReport.append(Universe.getNotice()+"\n");
		turnReport.append("\n-------------Status Report------------\n");
		turnReport.append(statusReport());
		Main.logTiming("making status report done "+name);

		if (!corpReport.equalsIgnoreCase("NONE")) {
			turnReport.append(Universe.getCorpRanking(this, corpReport,lock));
		}
		turnReport.append("\n--------Corporate Order Result--------\n");
		Main.logTiming("making rank report done "+name);
		turnReport.append(report);
		turnReport.append("\n--------Additional Report------\n"+additionalReport);
		Main.logTiming("adding order results done "+name);
		turnReport.append(getProjectReport(this));
		Main.logTiming("adding project report done "+name);
		turnReport.append(Universe.getResearchReport(this));
		Main.logTiming("adding reserach report done "+name);
		turnReport.append(Universe.getAuctionReport(this));
		String auctreport= Universe.getActionReport(this);
		turnReport.append(auctreport);
		Main.logTiming("adding auction report done "+name);
		String nforeport=Universe.getNFObjectReport(this);
		turnReport.append(nforeport); 
		Main.logTiming("adding nfo report done "+name);
		turnReport.append(Universe.getMarketReport());
		Main.logTiming("adding market report done "+name);

		if (itMarketReport.toUpperCase().indexOf("NONE")<0) {
			turnReport.append(Universe.getITMarketReport(this, itMarketReport,nforeport+auctreport));
			Main.logTiming("adding it report done "+name);
		}
		Main.logTiming("Building turn report done "+name);
		try {
			String pp = turnReport.toString();
			String turnReportFile=Main.DIRREPORTS+"TurnReport_"+tick+".txt";
			PrintWriter pw = new PrintWriter(new FileWriter(turnReportFile));
			pw.println(pp);
			pw.flush();
			pw.close();
		}
		catch (IOException IOE) {
			System.out.println("MyError: cannot make turn report for Corp " + tick);
			System.exit(0);
		}
		Main.logTiming("SAVE turn report done");

	}


	public static Corp parse(GmlPair g) {
		String name = null;
		String tick = null;
		Location home = null;
		String email = null;
		String password = null;
		String owner = null;
		double cash = 0;
		double loan = 0;
		double tax = 0;
		String corpReport = "all";
		String itMarketReport = "all";
		GmlPair n = g.getOneByName("name");
		if (n!=null) {
			name = n.getString();
		}
		n = g.getOneByName("tick");
		if (n!=null) {
			tick = n.getString();
		}
		n = g.getOneByName("home");
		if (n!=null) {
			home = Location.parse(n.getString());
		}
		n = g.getOneByName("email");
		if (n!=null) {
			email = n.getString();
		}
		n = g.getOneByName("password");
		if (n!=null) {
			password = n.getString();
		}
		n = g.getOneByName("owner");
		if (n!=null) {
			owner = n.getString();
		}
		n = g.getOneByName("cash");
		if (n!=null) {
			cash = n.getDouble();
		}
		n = g.getOneByName("corpReport");
		if (n!=null) {
			corpReport = n.getString();
		}
		n = g.getOneByName("itMarketReport");
		if (n!=null) {
			itMarketReport = n.getString();
		}
		n = g.getOneByName("tax");
		if (n!=null) {
			tax = n.getDouble();
		}
		n = g.getOneByName("loan");
		if (n!=null) {
			loan = n.getDouble();
		}
		Vector projs=new Vector();
		GmlPair nn[] = g.getAllByName("project");
		for (int a=0;a<nn.length;a++) {
			Project p=Project.parse(nn[a]);
			if (p!=null) projs.addElement(p);
		}
//System.out.println(name+" "+tick+" "+home+" "+email+" "+password+ " "+owner);
		Corp ccc= new Corp(name, tick, home, email, password, owner, cash, loan, tax, itMarketReport,projs,corpReport);
		n = g.getOneByName("lastlogin");
		if (n!=null) {
			ccc.lastlogin = (long)n.getDouble();
		}
		n = g.getOneByName("lastloginDate");
		if (n!=null) {
			ccc.lastloginDate = n.getString();
		}
		n = g.getOneByName("signedup");
		if (n!=null) {
			ccc.signedup=(long)n.getDouble();
		}
		n = g.getOneByName("signedupDate");
		if (n!=null) {
			ccc.signedupDate=n.getString();
		}
		n = g.getOneByName("verbose");
		if (n!=null) {
			ccc.verbose=n.getDouble()==1;
		}
		n = g.getOneByName("maxContract");
		if (n!=null) {
			ccc.maxContract=n.getDouble();
		}
		n = g.getOneByName("loanLimit");
		if (n!=null) {
			ccc.loanLimit=n.getDouble();
		}
		n = g.getOneByName("maxMegawatts");
		if (n!=null) {
			ccc.maxMegawatts=n.getDouble();
		}
		n = g.getOneByName("maxFood");
		if (n!=null) {
			ccc.maxFood=n.getDouble();
		}
		n = g.getOneByName("maxOxygen");
		if (n!=null) {
			ccc.maxOxygen=n.getDouble();
		}
		n = g.getOneByName("maxWater");
		if (n!=null) {
			ccc.maxWater=n.getDouble();
		}
		n = g.getOneByName("maxHuman");
		if (n!=null) {
			ccc.maxHuman=n.getDouble();
		}
		n = g.getOneByName("setMegawatts");
		if (n!=null) {
			ccc.setMegawatts=n.getDouble();
		}
		n = g.getOneByName("setFood");
		if (n!=null) {
			ccc.setFood=n.getDouble();
		}
		n = g.getOneByName("setOxygen");
		if (n!=null) {
			ccc.setOxygen=n.getDouble();
		}
		n = g.getOneByName("setWater");
		if (n!=null) {
			ccc.setWater=n.getDouble();
		}
		n = g.getOneByName("setHuman");
		if (n!=null) {
			ccc.setHuman=n.getDouble();
		}
		return ccc;
	}


	public static Corp load(String fileName) {
		try {
			GmlPair g = GmlPair.parse(new File(Main.DIRDATA+fileName));
			Corp c = Corp.parse(g);
			return c;
		}
		catch (IOException IOE) {
			System.out.println("MyError: cannot load corp " + fileName + IOE);
			System.exit(0);
		}
		return null;
	}

	public void receivePayment(String tick,double amt,String desc){
		addReport("Payment received from "+tick+" "+Stuff.money(amt,2)+" "+desc+" \n");
		cash+=amt;
	}

	public void addProject(Project p) {
		for (int a=0;a<projects.size();a++) {
			Project pp=(Project)projects.elementAt(a);
			if (pp.itt.getName().equalsIgnoreCase(p.itt.getName()) || pp.itt.getId().equalsIgnoreCase(p.itt.getId())) {
				report+="Project already exists "+pp.itt.getName()+" "+p.itt.getId()+".  Uses RESEARCH "+pp.getId()+"\n";
				if (pp.isReady()) {
					pp.pay(tick);
					return;
				}
				else {
					report+="ERROR: cannot research until next turn.  Returning money.\n";
					cash+=RESEARCHCOST;
					return;
				}
			}
		}
		projects.addElement(p);
	}

	public double maxResourcePay(String s) {
		if (s.equalsIgnoreCase("MegaWatts")) return maxMegawatts;
		else if (s.equalsIgnoreCase("Food")) return maxFood;
		else if (s.equalsIgnoreCase("Oxygen")) return maxOxygen;
		else if (s.equalsIgnoreCase("Hydration")) return maxWater;
		else if (s.equalsIgnoreCase("Human")) return maxHuman;
		return 0;
	}
}
