package aj.fm;

import java.util.Vector;

import aj.misc.GmlPair;

public class Spell2 {
	String name,gest;
	String discription;

	public String getName() {return name;
	}
	public String getCleanName() {
		String t=name;
		while (t.indexOf("_")>=0) {
			t=t.substring(0,t.indexOf("_"))+" "+t.substring(t.indexOf("_")+1);
		}
		return t;
	}
	public String getRemainingGest(String curr) {
		for (int a=1;a<gest.length()+1;a++) {
			String t=curr+gest.substring(gest.length()-a);
			if (getStepsFrom(t)==0) {
				return gest.substring(gest.length()-a);
			}
		}
		return gest;
	}

	public Spell2(String n,String  g) {
		name=n;gest=g;
	}
	public static Spell2 parse(GmlPair g) {
		String name=g.getOneByName("name").getString();
		String gest=g.getOneByName("gesture").getString();
		Spell2 sp=new Spell2(name,gest);
		GmlPair di=g.getOneByName("discription");
		if (di!=null) sp.setDiscription(di.getString());
		return sp;
	}
	public void setDiscription(String s) {
		discription=s;
	}
	public String getDiscription() {
		return discription;
	}


	public int getStepsFrom(String curr) {
		for (int a=0;a<gest.length();a++) {
			String t=curr+gest.substring(gest.length()-a);
			if (t.toUpperCase().endsWith(gest.toUpperCase())) {
				if (oneHand() || lowerCheck(t,gest)) return a;
			}
		}
		return gest.length();
	}

	public boolean stepsToCast(String curr,int b) {
		if (b>gest.length()) return false;
		String t=curr+gest.substring(gest.length()-b);
		if (t.toUpperCase().endsWith(gest.toUpperCase())) {
			if (oneHand() || lowerCheck(t,gest)) return true;
		}
		return false;
	}
	


	public boolean oneHand(){ 
		return gest.equals(gest.toUpperCase());
	}

	public boolean lowerCheck(String hist,String gest) {
		for (int a=0;a<gest.length();a++) {
			char g=gest.charAt(gest.length()-1-a);
			if ((""+g).toUpperCase().equals(""+g)) continue;
			char h=hist.charAt(hist.length()-1-a);
			if (g!=h) return false;
		}
		return true;
	}

//caps check?
	public Vector headStart(String curr) {
		Vector res=new Vector();
		for (int a=0;a<Tool2.allSpells.size();a++) {
			Spell2 sp=(Spell2)Tool2.allSpells.elementAt(a);
			if (sp==this && a==0) continue;
			if (sp.getStepsFrom(curr+gest)<sp.gest.length() && sp.getStepsFrom(curr+gest)!=0 && !res.contains(sp)) {
				res.addElement(sp);
			}
		}
		return res;
	}

//caps check?
	public Vector choice(String curr) {
		Vector res=new Vector();
		for (int a=0;a<Tool2.allSpells.size();a++) {
			Spell2 sp=(Spell2)Tool2.allSpells.elementAt(a);
			if (sp.gest.equals("p")) continue;//skip surrender as choice
			if (sp.getStepsFrom(curr+gest)==0 && !res.contains(sp)) {
			//if (t.lastIndexOf(sp.gest)>curr.length() && t.endsWith(sp.gest) && !res.contains(sp)) {
				res.addElement(sp);
			}
		}
		return res;
	}

	public Vector includes(String curr,String rem) {
		Vector res=new Vector();
		String t=curr+rem;t=t.toUpperCase();
		for (int a=0;a<Tool2.allSpells.size();a++) {
			Spell2 sp=(Spell2)Tool2.allSpells.elementAt(a);
			if (sp==this || sp.gest.toUpperCase().equals("P")) continue;//skip surrender as inlcude
			//if (gest.toUpperCase().indexOf(sp.gest.toUpperCase())>=0 && gest.toUpperCase().indexOf(sp.gest.toUpperCase())<gest.length()-1 && !res.contains(sp)) {
				//res.addElement(sp);
			//}
			if (t.lastIndexOf(sp.gest.toUpperCase())+sp.gest.length()>Math.max(curr.length(),0) && !t.endsWith(sp.gest.toUpperCase()) && !res.contains(sp)) {
				res.addElement(sp);
			}
		}
		return res;
	}
	public String toString() {
		return name+" "+gest;
	}
	public String getGest() {return gest;}
	public String summaryString(String curr) {
		String next=getRemainingGest(curr).substring(0,1);
		String res=next+" "+getStepsFrom(curr)+","+getStepsFrom("")+":"+toString()+" i="+includes(curr,getRemainingGest(curr)).size()+" hs=";
		Vector vv=headStart(curr);
		for (int a=1;a<10;a++) {
			int total=0;
			for (int b=0;b<vv.size();b++) {
				Spell2 t=(Spell2)vv.elementAt(b);
				if (t.getStepsFrom(curr+gest)==a) {total++;vv.removeElement(t);}
			}
			if (vv.size()>0 && total!=0) res+=(a!=1?",":"")+total;
		}
		return res+" rem="+getRemainingGest(curr);
	}
}


/*
Standard Duel Status for Peltor (game 3723, turn 7):

 Dayron (Cmt) HP: 13 State: thinking
  
  Left:  BSFWPPC
  Right: BDSFFFC

 Peltor (Flandar) HP: 14 State: thinking (orders)
  Sick(5) 

  Left:  BPSF>CF
  Right: B>>>CCW

------
Game #  Turn #

Dayron (Cmt)     LH:BSFWPPC   
                 RH:BDSFFFC  

Peltor (Flandar) LH:BPSF>CF  
                 RH:B>>>CCW  

---
mage <name list>     mage <name list>
  option RH            option RH *save if mymage
  option LH            option LH *save if mymage

----
spell - <#steps>name gesture
---
gesture color at choices
P - black
SP - P red for can choose (SP or P)
SPD - P blue for free (P)


myspells - sort alphebetically
theyspells - sort by speed

alphebetically sort by gest = branches visible
fastest sort by number of turns = next incomming/outgoing
sort by relation = 
  relations = list of all next spells that are started 3,2 and 1 steps

RH - spell (name) (turns) (gest) (relations)
LH - spell (name) (turns) (gest) (relations)


Desiese - 7 - DFPSWc - cWWS 

color (gest) for free spells
relatiosn show (spell info) for all next spells
---
send
auto turn number
auto check for PP
auto CHOOSE
auto TARGET
auto PARALIZE
auto DIRECT?


--------------------------------
|GM TU                         |
|                              |
|mage rh spell_N_T_R           |
|     lh                       |
|                              |
|                              |
--------------------------------

spell
  name
  steps to complete
  gesture
  head start relations
  include relations



CurrentGest [   ]

spells [name] [steps] [gesture] [headstart] [include]
*/


