package aj.nf;

import java.io.*;
import java.util.Vector;

/**
 *
 *@author     judda 
 *@created    July 21, 2000 
 */

public class Auction {
	Vector allItems = new Vector();
	Location auctionHouse = Location.parse("01.03.s0.00.00.F1");

	public void restart() {
		allItems = new Vector();
	}
	
	public String getFullReport() {
		String h = "\n--------Auction Report All---------\n";
		String s = "";
		for(int a = 0; a < allItems.size(); a++) {
			s += ((AuctionItem)allItems.elementAt(a)).toString() + "\n";
		}
		if(s.length() > 0)s = h + s + "\n";
		return s;
	}
	
	public void newOwner(String newowner, String oldowner) {
		for(int a = 0; a < allItems.size(); a++) {
			AuctionItem ai = (AuctionItem)allItems.elementAt(a);
			if(ai.origOwner.equalsIgnoreCase(oldowner)) {
				ai.origOwner = newowner;
			}
		}
	}
	
	public void load() {
		try {
			GmlPair g = GmlPair.parse(new File(Main.DIRDATA+Main.auctionFileName));
			GmlPair n[] = g.getAllByName("AuctionItem");
			for(int a = 0; a < n.length; a++) {
				AuctionItem mi = AuctionItem.parse(n[a]);
				if(mi != null) {
					allItems.addElement(mi);
				}
			}
		}
		catch(IOException IOE) {
			System.out.println("MyError: Auction load error");
			System.exit(0);
		}
	}
	
	public boolean bid(String id, double amt, Corp c) {
		for(int a = 0; a < allItems.size(); a++) {
			AuctionItem ai = (AuctionItem)allItems.elementAt(a);
			if(ai.getId().equalsIgnoreCase(id)) {
				return ai.newHighBidder(c, amt);
			}
		}
		c.addReport("NO SUCH AUCTION " + id + " in bid\n");
		return false;
	}
	
	public boolean consign(String id, String cn, double reserve, int close) {
		NFObject n = Universe.getNFObjectById(id);
		if(n == null) {
			return false;
		}
		Corp c = Universe.getCorpByTick(n.getCorpTick());
		if( !n.getCorpTick().equalsIgnoreCase(cn)) {
			return false;
		}
		if(n.isMoveable() && !n.getLocation().equalsIgnoreCase(c.getHome())) {
			return false;
		}
		if(reserve < 0) {
			return false;
		}
		if(close <= 0) {
			return false;
		}
		n.setCorpTick("AUCTION");
		if (!n.getCorpTick().equals("AUCTION")) return false;
		if(n.isMoveable())n.setLocation(auctionHouse);
		if (n instanceof Active) {//stop any running program
			Active ac=(Active)n;
			ac.reset();
		}
		AuctionItem ai = new AuctionItem(n.getId(), reserve, cn, close);
		allItems.addElement(ai);
		return true;
	}
	
	public void advance() {
		for(int a = 0; a < allItems.size(); a++) {
			AuctionItem ai = (AuctionItem)allItems.elementAt(a);
			ai.advance();
			if(ai.getClosing() <= 1) {
				ai.closeAuctionItem();
				allItems.removeElement(ai);
				a--;
			}
		}
	}
	
	public void save() {
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(Main.DIRDATA+Main.auctionFileName));
			
			//pw.println("all [");
			//for(int a = 0; a < allItems.size(); a++) {
				//pw.println(((AuctionItem)allItems.elementAt(a)).toSaveString());
			//}
			//pw.println("]");

			Vector v=new Vector();
			for(int a = 0; a < allItems.size(); a++) {
				v.addElement(((AuctionItem)allItems.elementAt(a)).toGmlPair());
			}
			GmlPair g=new GmlPair("all",v);
			pw.println(g.prettyPrint());
			pw.flush();
			pw.close();
		}
		catch(IOException IOE) {
			System.out.println("MyError: Auction Save error");
			System.exit(0);
		}
	}
	
	public String toString() {
		String s = "Auction Standing " + allItems.size() + " items listed\n";
		for(int a = 0; a < allItems.size(); a++) {
			s += ((AuctionItem)allItems.elementAt(a)).toString() + "\n";
		}
		return s;
	}
	
	public static void main(String s[]) {
		Auction m = new Auction();
		m.load();
		System.out.println(m);
		m.save();
	}
}

