
package aj.fm;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import aj.misc.Stuff;
import aj.net.Send;

class Mage implements ListSelectionListener,ActionListener {
	JButton limitedUpdate=new JButton("Update");
	JTextField currRH=new JTextField(10),currLH=new JTextField(10);
	JList choiceR,choiceL;
	JTextArea castingDetails=new JTextArea(12,33),spellDetails=new JTextArea(12,33);
	JTextArea orders=new JTextArea(8,15);
	JButton send=new JButton("Send");

	String owner,name,rh,lh,status;
	int hp;
	boolean activeMage=false;

	public void setActiveIfName(String s) {
		if (s.equals(name)) {
			activeMage=true;
		}
		else {
			activeMage=false;
		}
		send.setEnabled(activeMage);
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==limitedUpdate) {
			rh=currRH.getText();
			lh=currLH.getText();
			choiceR.setListData(Tool2.getOptionsStringFor(rh));
			choiceL.setListData(Tool2.getOptionsStringFor(lh));
		}
		else if (ae.getSource()==send) {
			StringReader SR = new StringReader(orders.getText());
			BufferedReader BR = new BufferedReader(SR);
			Send S = new Send();
			S.read(BR);
			try {
				S.deliver();
				System.out.println("Email delievered");
			}
			catch (IOException IOE7) {
				System.out.println("Email connect Error! " + IOE7);
			}
		}
		else {
		}
	}


	public Mage(String ll) {
		//System.out.println("Mage found>"+ll);
		//System.out.println("Mage parsing");
		String all[]=Stuff.getTokens(ll,"\t\n:,() ");
		//System.out.println("ll="+all.length+" tokens>"+ll);
		if (all.length>0) name=all[0];
		//System.out.println("name="+name);
		if (all.length>1) owner=all[1];
		//System.out.println("owner="+owner);
		try {
			if (all.length>2) hp=Integer.parseInt(all[3]);
		} catch (NumberFormatException nfe) {
			System.out.println("MyError: number error in HP"+nfe);
		}
		String left="Left: ";
		String right="Right: ";
		if (ll.indexOf(left)>=0) {
			lh=ll.substring(ll.indexOf(left)+left.length()).trim();
			if (lh.indexOf("\n")>=0) lh=lh.substring(0,lh.indexOf("\n")).trim();
		}
		if (ll.indexOf(right)>=0) {
			rh=ll.substring(ll.indexOf(right)+right.length()).trim();
			if (rh.indexOf("\n")>=0) rh=rh.substring(0,rh.indexOf("\n")).trim();
		}
		if (ll.indexOf("\n")>=0) {
			status=ll.substring(ll.indexOf("\n")).trim();
			if (status.indexOf("\n")>=0) {
				status=status.substring(0,status.indexOf("\n")).trim();
			}
			if (status.length()>0 && ll.indexOf("Left: ")>=0) {
				status=ll.substring(ll.indexOf(status),ll.indexOf("Left:"));
				status=Stuff.superTrim(status);
			}
		}
	}

	public Mage(String name,String rh,String lh,String status,int hp) {
		this.name=name;
		this.rh=rh;
		this.lh=lh;
		this.status=status;
		this.hp=hp;
	}

	public void valueChanged(ListSelectionEvent e) {
		JList jl=(JList)e.getSource();
		boolean right=false;
		if (jl==choiceR) right=true;
		String curr=(right?currRH.getText():currLH.getText());

		String val=(String)jl.getSelectedValue();
		if (val==null) return;
		Spell2 sp=Tool2.getSpellBySummary(val);
		
		castingDetails.setText(Tool2.getCastingDetails(curr,sp));
		spellDetails.setText(Tool2.getSpellDiscription(sp));
		
		if (activeMage) addOrders(right,sp);
	}

	public String getOrder(boolean right) {
		String o=orders.getText();
		String ref=(right?"R":"L")+"H ";
		String h=null,ch=null;
		while (o.indexOf("TARGET "+ref)>=0) {
			ch=o.substring(o.indexOf("TARGET "+ref));
			if (ch.indexOf("\n")>=0) ch=ch.substring(0,ch.indexOf("\n")).trim();
			o=o.substring(0,o.indexOf(ch))+o.substring(o.indexOf(ch)+ch.length()).trim();
		}
		while (o.indexOf("CHOICE "+ref)>=0) {
			ch=o.substring(o.indexOf("CHOICE "+ref));
			if (ch.indexOf("\n")>=0) ch=ch.substring(0,ch.indexOf("\n")).trim();
			o=o.substring(0,o.indexOf(ch))+o.substring(o.indexOf(ch)+ch.length()).trim();
		}
		while (o.indexOf(";;")>=0) {
			o=o.substring(0,o.indexOf(";;"))+o.substring(o.indexOf(";;")+";;".length()).trim();
		}
		while (o.indexOf(ref)>=0) {
			h=o.substring(o.indexOf(ref));
			if (h.indexOf("\n")>=0) h=h.substring(0,h.indexOf("\n")).trim();
			o=o.substring(0,o.indexOf(h))+o.substring(o.indexOf(h)+h.length()).trim();
		}
		return h;
	}
	public void replaceOrder(boolean right,String next,String choose,String comment) {
		String o=orders.getText();
		String old=getOrder(right);
		String ref=(right?"R":"L")+"H ";

		String h=null,ch=null;
//
		while (o.indexOf("TARGET "+ref)>=0) {
			ch=o.substring(o.indexOf("TARGET "+ref));
			if (ch.indexOf("\n")>=0) ch=ch.substring(0,ch.indexOf("\n")+1).trim();
			o=o.substring(0,o.indexOf(ch))+o.substring(o.indexOf(ch)+ch.length()).trim();
		}
		while (o.indexOf("CHOICE "+ref)>=0) {
			ch=o.substring(o.indexOf("CHOICE "+ref));
			if (ch.indexOf("\n")>=0) ch=ch.substring(0,ch.indexOf("\n")+1).trim();
			o=o.substring(0,o.indexOf(ch))+o.substring(o.indexOf(ch)+ch.length()).trim();
		}
		while (o.indexOf(";;")>=0) {
			o=o.substring(0,o.indexOf(";;"))+o.substring(o.indexOf(";;")+";;".length()).trim();
		}
//		while (o.indexOf(ref)>=0) {
//			h=o.substring(o.indexOf(ref));
//			if (h.indexOf("\n")>=0) h=h.substring(0,h.indexOf("\n")).trim();
//			o=o.substring(0,o.indexOf(h))+o.substring(o.indexOf(h)+h.length()).trim();
//		}
		
		//if (h==null) {
			//o+=""+ref+next;
		//}
		String rep=ref+next+";"+(comment!=null?comment:"");
		if (choose!=null) {
			rep+="\nCHOICE "+ref+choose;
			rep+="\n;TARGET "+ref;
		}
		o=o.substring(0,o.indexOf(old))+rep+o.substring(o.indexOf(old)+old.length());
		orders.setText(o);

		if (!next.toUpperCase().equals(next) && comment!=null) {
			replaceOrder(!right,next,null,null);
		}
		if (old.indexOf(";")>=0) old=old.substring(0,old.indexOf(";"));
		if (comment!=null && old!=null && !old.toUpperCase().equals(old) && old.indexOf(next)<0) {
			replaceOrder(!right,"-","Undecided",null);
		}
	}

	public void addOrders(boolean right,Spell2 sp) {
		String curr=(right?currRH.getText():currLH.getText());
		String next=sp.getRemainingGest(curr).substring(0,1);
		String choiceName=null;
		String comment="";
//if include 
//  if include now
		if (sp.getStepsFrom(curr)!=1 && sp.includes(curr,sp.getRemainingGest(curr)).size()>0) {
			Vector v=sp.includes(curr,sp.getRemainingGest(curr));
			for (int a=0;a<v.size();a++) {
				Spell2 sp2=(Spell2)v.elementAt(a);
				if (sp2.getStepsFrom(curr)==1) {
					choiceName=sp2.getCleanName();
					if (comment.indexOf("NOW")<0) comment+="NOW ";
					comment+=" "+sp2.getCleanName()+" ";
					if (comment.indexOf("LATER")<0) comment+=" LATER "+sp.getCleanName()+";";
				}
				else if (sp2.getStepsFrom(curr)>1) {
					choiceName=null;
					if (comment.indexOf("FIRST")<0) comment+="FIRST ";
					comment+=sp2.getCleanName()+" ";
					if (comment.indexOf("LATER")<0) comment+=" LATER "+sp.getCleanName()+";";
				}
			}
		}
//if choice	
		else if (sp.getStepsFrom(curr)==1) {
			if (sp.choice(curr).size()>1) {
				Vector v=sp.choice(curr);
				for (int a=0;a<v.size();a++) {
					Spell2 sp2=(Spell2)v.elementAt(a);
					if (sp2==sp) continue;
					choiceName=sp.getCleanName();
					if (comment.indexOf("MULTI")<0) comment+="MULTICHOICE ";
					comment+=sp.getCleanName()+";";
				}
			}
		}
		else {
			choiceName=null;
		}
		replaceOrder(right,next,choiceName,sp.name);
		String o=orders.getText();
		if (o.indexOf("RH P")>=0 && o.indexOf("LH P")>=0) {
			replaceOrder(right,next.toUpperCase(),"WARNING *p*",sp.name);
			replaceOrder(!right,next.toUpperCase(),"WARNING *p*",sp.name);
		}
	}
/*
	public void addOrders(boolean right,Spell2 sp) {
		String o=orders.getText();
		if (o.indexOf("\nEND")>=0) {
			o=o.substring(0,o.indexOf("\nEND"));
		}
		if (o.indexOf("\n\n")>=0) {
			o=o.substring(0,o.indexOf("\n\n"))+o.substring(o.indexOf("\n\n")+1);
		}
		String h="",ch="";
		
		String next=sp.gest.substring(0,1);
		if (right) {
			while (o.indexOf("CHOICE RH ")>=0) {
				ch=o.substring(o.indexOf("CHOICE RH "));
				if (ch.indexOf("\n")>=0) ch=ch.substring(0,ch.indexOf("\n")).trim();
				o=o.substring(0,o.indexOf(ch))+o.substring(o.indexOf(ch)+ch.length()).trim();
			}
			while (o.indexOf("RH ")>=0) {
				h=o.substring(o.indexOf("RH "));
				if (h.indexOf("\n")>=0) h=h.substring(0,h.indexOf("\n")).trim();
				o=o.substring(0,o.indexOf(h))+o.substring(o.indexOf(h)+h.length()).trim();
			}
			String curr=currRH.getText();
			for (int a=0;a<sp.gest.length()-1;a++) {
				String t=curr+sp.gest.substring(sp.gest.length()-a);
				if (sp.getStepsFrom(t)==0) {
					next=sp.gest.substring(sp.gest.length()-a).substring(0,1);
				}
			}
			o+="\nRH "+next;
			orders.setText(o+"\nEND\n");
			if (sp.getStepsFrom(curr)==1) {
				if (sp.oneHand() || o.indexOf(sp.getCleanName())<0) 
					o+="\nCHOICE RH "+sp.getCleanName()+"";
			}
			if (o.indexOf("RH P")>=0 && o.indexOf("LH P")>=0) o+=" WARNING *p* ";
			orders.setText(o+"\nEND\n");
			if (!next.toUpperCase().equals(next) && o.indexOf((!right?"R":"L")+"H "+next)<0) addOrders(!right,sp);
		}
		else {
			while (o.indexOf("CHOICE LH ")>=0) {
				ch=o.substring(o.indexOf("CHOICE LH "));
				if (ch.indexOf("\n")>=0) ch=ch.substring(0,ch.indexOf("\n")).trim();
				o=o.substring(0,o.indexOf(ch))+o.substring(o.indexOf(ch)+ch.length()).trim();
			}
			while (o.indexOf("LH ")>=0) {
				h=o.substring(o.indexOf("LH "));
				if (h.indexOf("\n")>=0) h=h.substring(0,h.indexOf("\n")).trim();
				o=o.substring(0,o.indexOf(h))+o.substring(o.indexOf(h)+h.length()).trim();
			}
			String curr=currLH.getText();
			for (int a=0;a<sp.gest.length()-1;a++) {
				String t=curr+sp.gest.substring(sp.gest.length()-a);
				if (sp.getStepsFrom(t)==0) next=sp.gest.substring(sp.gest.length()-a).substring(0,1);
			}
			o+="\nLH "+next;
			orders.setText(o+"\nEND\n");
			if (sp.getStepsFrom(curr)==1) {
				if (sp.oneHand() || o.indexOf(sp.getCleanName())<0) 
					o+="\nCHOICE LH "+sp.getCleanName()+"";
			}
			if (o.indexOf("RH P")>=0 && o.indexOf("LH P")>=0) o+=" WARNING *p*";
			orders.setText(o+"\nEND\n");
			if (!next.toUpperCase().equals(next) && o.indexOf((!right?"R":"L")+"H "+next)<0) addOrders(!right,sp);
		}
	}
*/

	public Container getGUI(){
		String header="TO:fm@gamerz.net\nFROM:flandar@yahoo.com\nSUBJECT:FM ORDERS\n\nUSER "+owner+" PASSWD\nMAGE "+name+"\nTURN "+Tool2.turn+"\n\nRH -\n\nLH -\n\nEND\n";
		for (int a=0;a<rh.length() && a<lh.length();a++) {
			if (rh.charAt(a)==lh.charAt(a) && rh.charAt(a)=='C') {
				rh=rh.substring(0,a)+"c"+rh.substring(a+1);
				lh=lh.substring(0,a)+"c"+lh.substring(a+1);
			}
			if (rh.charAt(a)==lh.charAt(a) && rh.charAt(a)=='W') {
				rh=rh.substring(0,a)+"w"+rh.substring(a+1);
				lh=lh.substring(0,a)+"w"+lh.substring(a+1);
			}
		}
		currRH.setText(rh);
		currLH.setText(lh);
		//choiceR=new JList(((Spell2)Tool2.getOptionsFor(rh)).summaryString());
		choiceR=new JList(Tool2.getOptionsStringFor(rh));
		choiceR.addListSelectionListener(this);
		//choiceL=new JList(((Spell2)Tool2.getOptionsFor(lh)).summaryString());
		choiceL=new JList(Tool2.getOptionsStringFor(lh));
		choiceL.addListSelectionListener(this);
		JPanel jp=new JPanel(new BorderLayout());
			JPanel h=new JPanel(new BorderLayout());
				JPanel m=new JPanel(new FlowLayout());
				m.add(new JLabel("RH"));	
				m.add(currRH);
				m.add(new JScrollPane(choiceR));
			h.add("North",m);
				m=new JPanel(new FlowLayout());
				m.add(new JLabel("LH"));	
				m.add(currLH);
				m.add(new JScrollPane(choiceL));
			h.add("Center",m);
				limitedUpdate.addActionListener(this);
			h.add("South",limitedUpdate);
		jp.add("West",h);
		if (activeMage){
			h=new JPanel(new BorderLayout());
			h.add("Center",new JScrollPane(orders));
			send.addActionListener(this);
 			h.add("South",new JScrollPane(send));
			orders.setText(header);
			jp.add("East",h);
		}
		JPanel mm=new JPanel(new BorderLayout());
		mm.add("North",jp);
		jp=new JPanel(new BorderLayout());
			h=new JPanel(new BorderLayout());
			h.add("North",new JLabel("Casting"));
			h.add("Center",new JScrollPane(castingDetails));
			castingDetails.setLineWrap(false);
		jp.add("West",h);
			h=new JPanel(new BorderLayout());
			h.add("North",new JLabel("Details"));
			h.add("Center",new JScrollPane(spellDetails));
			spellDetails.setLineWrap(true);
			spellDetails.setWrapStyleWord(true);
		jp.add("East",h);
		mm.add("Center",jp);

		return mm;
	}
	public String getTitle() {
		return hp+":"+name+" "+status;
	}

}

