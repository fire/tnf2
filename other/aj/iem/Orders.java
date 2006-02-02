package aj.iem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Vector;

import aj.misc.Stuff;

//ADD USERID config
//ADD PROXY config

public class Orders {
	static int maxLimitBidAskAmount=5;

//	public static void main(String s[]) {
//		IEMTool.userId="AJudd3587";
//		String inputFileName="";
//		for (int a=0;a<s.length;a++) {
//			if (s[a].indexOf("?")>=0) {help();return;}
//			if (s[a].toUpperCase().startsWith("-I")) {
//				inputFileName=s[a].substring(2);
//			}
//			if (s[a].toUpperCase().startsWith("-W")) {
//				IEMTool.proxy=true;
//				IEMTool.proxyHost=s[a].substring(2);
//				//Sys.out.println("Proxy found "+proxyHost);
//				if (IEMTool.proxyHost.indexOf(":")>=0) {
//					try {
//						IEMTool.proxyPort=Integer.parseInt(IEMTool.proxyHost.substring(IEMTool.proxyHost.indexOf(":")+1));
//						IEMTool.proxyHost=IEMTool.proxyHost.substring(0,IEMTool.proxyHost.indexOf(":"));
//					} catch (NumberFormatException nfe) {
//						Sys.out.println("MyError proxy config bad number");
//						System.exit(0);
//					}
//				}
//			}
//		}
//		new Orders(inputFileName);
//	}
//	public static void help() {
//		Sys.out.println("Usage: java aj.iem.Orders -i<filename>");
//		Sys.out.println();
//		Sys.out.println("-w<host>:<port> for proxy connect");
//		Sys.out.println("<BID|ASK> <assetName|assetName:BundleName> <price|~price> <quantity|~quant> [expireHours]");
//	}
	Vector commands=new Vector();
	Vector assets=new Vector();

	static String report="Orders report "+new Date();
	
	
	public Orders() {
		report="Orders report "+new Date();
		try {
			BufferedReader br=new BufferedReader(new FileReader(IEMTool.orderscommandfile));
			while (true) {
				String s=br.readLine();
				if (s==null) break;
				s=s.trim();
				commands.addElement(s);
			}
			br=new BufferedReader(new FileReader(IEMTool.iemstatusordersfile));//"iem_statusorders.log"));
			while (true) {
				String s=br.readLine();
				if (s==null) break;
				s=s.trim();
				if (s.startsWith(";")) continue;
				if (s.startsWith("#")) continue;
				if (s.indexOf("PARTIAL")>=0) continue;
				assets.addElement(s);
			}
		} catch (IOException IOE) {
			report+="MyError: cannot open/read file\n";
		}
//send orders
		for (int a=0;a<commands.size();a++) {
			String s=(String)commands.elementAt(a);
			if (s.length()==0 || s.startsWith("#") || s.startsWith(";")) continue;
			report+="Processing> "+s+"\n";
			String args[]=Stuff.getTokens(s," ");
			if (args.length!=4 && args.length!=5) {
				report+="MyError> Wrong args in line "+s+"\n";
				continue;
			}

			boolean isBid=args[0].equalsIgnoreCase("BID");
			boolean isAsk=args[0].equalsIgnoreCase("ASK");
			String name=args[1];
			String val=args[2];
			String quant=args[3];
			String marketId=lookUpMarketId(name);
			String contractId=lookUpContractId(name);
			IEMTool.sessionId=lookUpSessionId(name);
			int hav=99999;
			if (lookUp(name)==null) {
				report+="MyError> Cannot find asset name or bundle "+name+"\n";
				continue;
			}
			try {
				hav=Integer.parseInt(lookUpNetHeld(name));
			} catch (NumberFormatException nfe) {
				report+="MyError> bad number in netheld> "+lookUpNetHeld(name)+"\n";
				continue;
			}
			int del=0;
			try {
				del=Integer.parseInt(lookUpDeltaHeld(name));
			} catch (NumberFormatException nfe) {
				report+="MyError> bad number in deltaheld> "+lookUpDeltaHeld(name)+"\n";
				continue;
			}
			
			//if using ~ quant then check threshold before any!
			try {
				if (quant.startsWith("~")) {
					int quan=Integer.parseInt(quant.substring(1));
					if (isAsk) {
						//Sys.out.println("request="+quant+" have "+lookUpDeltaHeld(name));
						quan=del-quan;
						if (quan<1){
							report+=" SKIPPING inventory beyond threshold ASK>"+quan+"\n";
							continue;
						}
					}
					if (isBid) {
						//Sys.out.println("request="+quant+" have "+lookUpNetHeld(name));
						quan=quan-hav;
						if (quan<1){
							report+=" SKIPPING inventory beyond threshold BID>"+quan+"\n";
							continue;
						}
					}
					if (quan>maxLimitBidAskAmount) {
						quan=maxLimitBidAskAmount;
					}
					quant=quan+"";
				}
			} catch (NumberFormatException nfe) {
				report+="MyError: bad number in quant "+quant+"\n";
				continue;//System.exit(0);
			}

			
			try {
				double d=Double.parseDouble(val);
				if (d>2) {
					d=d/1000.0;
					report+="WARNING: val >1 converting to decimal (orig="+val+" new="+d+")\n";
				}
				val=Stuff.trunc(d,3)+"";
			} catch (NumberFormatException nfe) {
				if (val.startsWith("~")) {
					double max=Double.parseDouble(val.substring(1));
					String curA=val=lookUpAsk(name);
					String curB=val=lookUpBid(name);

					String task=curA;
					double ask=Double.parseDouble((task.indexOf("*")>=0?task.substring(0,task.indexOf("*"))+task.substring(task.indexOf("*")+1):task))/1000.0;
					String tbid=lookUpBid(name);
					double bid=Double.parseDouble((tbid.indexOf("*")>=0?tbid.substring(0,tbid.indexOf("*"))+tbid.substring(tbid.indexOf("*")+1):tbid))/1000.0;
					//look for special market values

					if (isAsk && max<bid){
						report+="FAST BARGIN!!  Sell at market "+bid+" since willing at "+max+"\n";
						IEMTool.placeMarketOrder(IEMTool.sellAtMarket,""+bid,quant,contractId,marketId);
					}
					if (isBid && max>ask) {
						report+="FAST BARGIN!!  Buy at market "+ask+" since willing at "+max+"\n";
						IEMTool.placeMarketOrder(IEMTool.buyAtMarket,""+ask,quant,contractId,marketId);
					}

					if (isAsk) {
						val=lookUpAsk(name);
						if (val.endsWith("1001")) {
							report+=" SKIPPING No valid ask detected "+curA+"\n";
							continue;
						}
						if (val.endsWith("*")) {
							report+=" SKIPPING Current low asker with "+curA+"\n";
							continue;
						}
						try {
							double d=Double.parseDouble(val)/1000.0-.002;
							if (d<max) {
								report+=" SKIPPPING Target ASK "+max+" higher than current ask "+d+"\n";
								val=""+Stuff.trunc(max,3);
								continue;
							}
							else {
								//Sys.out.println("TARGET ASK *BELOW* current of "+curA+" new ask="+d);
								val=""+Stuff.trunc(d,3);
							}
						} catch(NumberFormatException nfe2) {
							report+="MyError: bad number in price"+val+"\n";
							continue;//System.exit(0);
						}
					}
					if (isBid) {
						val=curB;
						if (val.endsWith("*")) {
							report+=" SKIPPING Currenty high bidder with bid of "+curB+"\n";
							continue;
						}
						try {
							double d=Double.parseDouble(val)/1000.0+.002;
							if (d>max) {
								report+=" SKIPPING TARGET BID "+max+" below current bid "+d+"\n";
								val=""+Stuff.trunc(max,3);
								continue;
							}
							else {
								//Sys.out.println("TARGET BID *ABOVE* current of "+curB+" new bid="+d);
								val=""+Stuff.trunc(d,3);
							}
						} catch(NumberFormatException nfe2) {
							report+="MyError: bad number in price "+val+"\n";
							continue;//System.exit(0);
						}
					}
				}
				else {
					report+="MyError: bad number in price "+val+"\n";
					continue;//System.exit(0);
				}
			}

			if (marketId==null || contractId==null || IEMTool.sessionId==null) continue;
			int hour=1000*60*60;
			long delay=hour*24;
			if (args.length>3) {
				try {
					double d=Double.parseDouble(args[4]);
					delay=(long)(hour*(d));
				} catch (Exception e ){
					report+="MyError> Bad number if delay\n";
					continue;//System.exit(0);
				}
			}
			if (IEMTool.sessionId==null) {
				report+="MyError: cannot open connettion to IEM\n";
				writeOrdersReport();
				return;
			}
//TURN REAL ORDERS OFF
			if (isBid) IEMTool.placeLimitOrder(IEMTool.placeBid,val,quant,contractId,marketId,delay);
			else if(isAsk) IEMTool.placeLimitOrder(IEMTool.placeAsk,val,quant,contractId,marketId,delay);
			report+="*ORDER ="+IEMTool.sessionId+" "+(isBid?"bid":"ask")+" "+name+":"+marketId+":"+contractId+" for "+quant+" at "+val+" for "+(delay/hour)+" hours\n";
		}
		writeOrdersReport();
	}


	public String lookUp(String name) {
		String bund=null;
		if (name.indexOf(":")>=0) {
			bund=name.substring(0,name.indexOf(":"));
			name=name.substring(name.indexOf(":")+1);
		}
		for (int a=0;a<assets.size();a++) {
			String s=assets.elementAt(a).toString();
			if (bund==null && s.toUpperCase().indexOf(name.toUpperCase()+" ")>=0) return s;
			if (bund!=null && s.toUpperCase().equals(bund.toUpperCase()) && s.toUpperCase().indexOf(name.toUpperCase()+" ")>=0) return s;
		}
		//Sys.out.println("Cannot find asset name or bundle "+name);
		//System.exit(0);
		return null;
	}

	public String lookUpDeltaHeld(String name) {
		String s=lookUp(name);
		if (s==null) return null;
		String args[]=Stuff.getTokens(s," ");
		if (args.length<9) return null;
		return args[8];
	}
	
//held #=total held  *=net held less bundles
	public String lookUpNetHeld(String name) {
		String t=lookUpHeld(name);
		if (t==null) return null;
		if (t.endsWith("*")) return "99999";
//Sys.out.println("netheld="+t);
		return t;
	}
	
	public String lookUpHeld(String name) {
		String s=lookUp(name);
		if (s==null) return null;
		String args[]=Stuff.getTokens(s," ");
		if (args.length<8) return null;
//Sys.out.println("held="+args[7]);
		return args[7];
	}
	
	public String lookUpAsk(String name) {
		String s=lookUp(name);
		if (s==null) return null;
		String args[]=Stuff.getTokens(s," ");
		if (args.length<7) return null;
		return args[6];
	}
	
	public String lookUpBid(String name) {
		String s=lookUp(name);
		if (s==null) return null;
		String args[]=Stuff.getTokens(s," ");
		if (args.length<6) return null;
		return args[5];
	}
	
	public String lookUpMarketId(String name) {
		String s=lookUp(name);
		if (s==null) return null;
		String args[]=Stuff.getTokens(s," ");
		if (args.length<2) return null;
		return args[1];
	}
	
	public String lookUpContractId(String name) {
		String s=lookUp(name);
		if (s==null) return null;
		String args[]=Stuff.getTokens(s," ");
		if (args.length<1) return null;
		return args[0];
	}
	
	public String lookUpSessionId(String name) {
		String s=lookUp(name);
		if (s==null) return null;
		String args[]=Stuff.getTokens(s," ");
		if (args.length<3) return null;
		if (args[2].equals("null")) {
			report+="MyError> Cannot find session id.  Id is null\n";
			return null;
			//System.exit(0);
		}
		return args[2];
	}

	static synchronized public void writeOrdersReport() {
		try {
			PrintWriter pw=new PrintWriter(new FileWriter(IEMTool.ordersoutputlogfile));
			pw.println(report);
			pw.flush();
			pw.close();
		} catch (IOException ioe) {
			System.err.println("MyError :"+ioe);
		}
}

}

