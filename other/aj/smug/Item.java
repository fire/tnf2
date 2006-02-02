package aj.smug;

import java.util.Vector;

import aj.misc.GmlPair;

public class Item {
	static String EMPTY="EMPTY";
	static String types[] ={"equipment","weapon","gun","armor","engine","shield","hull","manuver","weaponsystem","computer","sensor"};

	String name,type;
	double cost;
	String bonusskill=EMPTY;
	int bonusamount=0;
	String available="";

	public Item (String n,String t,double c,String bs,int ba,String av) {
		name=n;type=t;cost=c;
		bonusskill=bs;bonusamount=ba;
		available=av;
	}
	public boolean stealable() {
		for (int a=0;a<3;a++)
			if (type.equalsIgnoreCase(types[a])) return true;
		return false;
	}
	public static Item parse(GmlPair g) {
		String name=EMPTY,type=EMPTY;
		double cost=-1;
		String bonusskill=EMPTY;
		int bonusamount=0;

		GmlPair n=g.getOneByName("name");
		if (n!=null) name=n.getString();
		n=g.getOneByName("type");
		if (n!=null) type=n.getString();
		n=g.getOneByName("cost");
		if (n!=null) cost=n.getDouble();
		n=g.getOneByName("bonusskill");
		if (n!=null) bonusskill=n.getString();
		n=g.getOneByName("bonusamount");
		if (n!=null) bonusamount=(int)(n.getDouble());
		String available="";
		n=g.getOneByName("available");
		if (n!=null) available=n.getString();
		return new Item(name,type,cost,bonusskill,bonusamount,available);
	}
	public GmlPair toGml() {
		Vector v=new Vector();
		GmlPair g=new GmlPair("name",name);
		v.addElement(g);
		g=new GmlPair("type",type);
		v.addElement(g);
		g=new GmlPair("cost",cost+"");
		v.addElement(g);
		g=new GmlPair("available",available+"");
		v.addElement(g);
		if (!bonusskill.equalsIgnoreCase(EMPTY)) {
			g=new GmlPair("bonusskill",bonusskill);
			v.addElement(g);
			g=new GmlPair("bonusamount",bonusamount+"");
			v.addElement(g);
		}
		g=new GmlPair("Item",v);
		return g;
	}

	public static void main(String s[]) {
			Item p=new Item("Hal","computer1",50,"EMPTY",0,"C");
			System.out.println(p.toGml());
			p=new Item("knife","weapon",50,"melecombat",5,"T");
			System.out.println(p.toGml());
	}

}
