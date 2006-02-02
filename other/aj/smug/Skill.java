package aj.smug;

import java.util.Vector;

import aj.misc.GmlPair;

public class Skill {
	String name,stat;

	public Skill (String n,String st){
		name=n;stat=st;
	}
	public static Skill parse(GmlPair g) {
		String name="EMPTY";
		GmlPair n=g.getOneByName("name");
		if (n!=null) name=n.getString();
		String stat="EMPTY";
		n=g.getOneByName("stat");
		if (n!=null) stat=n.getString();
		return new Skill(name,stat);
	}
	public GmlPair toGml() {
		Vector v=new Vector();
		GmlPair g=new GmlPair("name",name);
		v.addElement(g);
		g=new GmlPair("stat",stat);
		v.addElement(g);
		g=new GmlPair("Skill",v);
		return g;
	}

	public static void main(String s[]) {
			Skill p=new Skill("pilot","dex");
			System.out.println(p.toGml());
	}

}
