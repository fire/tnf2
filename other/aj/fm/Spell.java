package aj.fm;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import aj.misc.XMLReader;

public class Spell {

	private String gesture;

	private String name;

	private String priority;

	private String description;

	private Vector cancel;

	private Vector diffuse;

	private Vector delay;

	private String notes;

	private String limited_game;

	private String defaultTarget;
	
	static {
		parseAllSpells();
	}

	public Spell(String gesture, String name, String priority, String description, Vector diffuse, Vector cancel, Vector delay, String notes, String limited_game, String defaulttarget2) {
		this.gesture=gesture;
		this.name=name;
		this.priority=priority;
		this.description=description;
		this.diffuse=diffuse;
		this.cancel=cancel;
		this.delay=delay;
		this.notes=notes;
		this.limited_game=limited_game;
		this.defaultTarget=defaulttarget2;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		parseAllSpells();
		
		Spell s=(Spell)allSpells.elementAt(15);
		System.out.println("s="+s.toString());
		System.out.println(s.getStepsLeft("V"));
		System.out.println(s.getStepsLeft("D"));
		System.out.println(s.getStepsLeft("DF"));
		System.out.println(s.getStepsLeft("DP"));
	}


	private static Vector allSpells = null;

	private static Document allSpellsDoc;
	
	
	private static void parseAllSpells() {
		allSpellsDoc=XMLReader.readDocumentFromStream(Spell.class.getResourceAsStream("spells.xml"));
		allSpells = new Vector();
		Node n=XMLReader.getChildNodeNameByName("Spell_List",allSpellsDoc);
		Vector spells=XMLReader.getAllChildNodeByName("Spell",n);
//		System.out.println("found "+spells.size()+" spell childers");
		for (int a=0;a<spells.size();a++) {
			Spell s=parseSpell((Node)spells.elementAt(a));
			if (s!=null) allSpells.addElement(s);
		}
	}


	private static Spell parseSpell(Node n) {
		String name=XMLReader.getChildValueByNodeName("name",n);
		String gesture=XMLReader.getChildValueByNodeName("gesture",n);
		String priority=XMLReader.getChildValueByNodeName("priority",n);
		String description=XMLReader.getChildValueByNodeName("description",n);
		Vector diffuse=XMLReader.getAllChildValueByNodeName("diffues",n);
		Vector cancel=XMLReader.getAllChildValueByNodeName("cancel",n);
		Vector delay=XMLReader.getAllChildValueByNodeName("delay",n);
		String notes=XMLReader.getChildValueByNodeName("notes",n);
		String limited_game=XMLReader.getChildValueByNodeName("limited_game",n);
		String defaulttarget=XMLReader.getChildValueByNodeName("target",n);
		if (defaulttarget.equals(Wizard.TARGET_SELF)) defaulttarget=Wizard.TARGET_SELF;
		if (defaulttarget.equals(Wizard.TARGET_NO_ONE)) defaulttarget=Wizard.TARGET_NO_ONE;
		return new Spell(gesture,name,priority,description,diffuse,cancel,delay,notes,limited_game,defaulttarget);
	}
	
	public String toString() {
		return "Spell "+name+" g="+gesture+" pri="+priority;
	}


	public static Vector getAllSpellsByNearestToPattern(String rpat) {
		Vector res=new Vector();
		if (allSpells==null) parseAllSpells();
		for (int count=0;count<9;count++) {
			for (int a=0;a<allSpells.size();a++) {
				Spell s=(Spell)allSpells.elementAt(a);
				if (s.getStepsLeft(rpat)==count && !res.contains(s)) {
					res.addElement(s);
				}
			}
		}
		return res;
	}
	
	public static Vector getAllSpells() {
		return (Vector)allSpells.clone();
	}

	public String getName() {
		return name;
	}


	public String getGesture() {
		return gesture;
	}

	public int getStepsLeft(String rpat) {
		for (int a=gesture.length()-1;a>0;a--) {
			if ((rpat+gesture.substring(a)).endsWith(gesture)) return gesture.length()-a;
		}
		return gesture.length();
	}


	public String getNextStep(String leftPattern) {
		for (int a=gesture.length()-1;a>0;a--) {
			if ((leftPattern+gesture.substring(a)).endsWith(gesture)) return gesture.substring(a,a+1);
		}
		return gesture.substring(0,1);
	}


	public String getDefaultTarget() {
		return defaultTarget;
	}

}
