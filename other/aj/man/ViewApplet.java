package aj.man;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import aj.awt.SimpleWindowManager;

public class ViewApplet extends Applet implements ActionListener {

	View V;
	Vector allMoves=new Vector();

	public static void main(String s[]) {
		ViewApplet va=new ViewApplet();
		va.mainSetup(s);
	}

	public void mainSetup(String s[]) {
		for (int a=0;a<s.length;a++) {
			allMoves.addElement(s[a]);
		}
		Frame f=new Frame();
		f.addWindowListener(new SimpleWindowManager());
		V=new View();
		f.add("Center",V);
		Panel p=new Panel(new FlowLayout());
		for (int a=0;a<allMoves.size();a++){
			Button B=new Button(allMoves.elementAt(a).toString());
			B.addActionListener(this);
			p.add(B);
		}
		f.add("South",p);
		f.setSize(new Dimension(400,250));
		f.setVisible(true);
		new Thread(V).start();
	}

	public void init() {
		String s = getParameter("delay");
		if (s!=null) {
			System.out.println("SLEEPTIME/DELAY set to "+s);
			try {
				View.SLEEPTIME=Integer.parseInt(s);
			} catch (NumberFormatException nfe) {}
		}
		s = getParameter("step");
		if (s!=null) {
			System.out.println("STEPCOUNT/STEP set to "+s);
			try {
				View.STEPCOUNT=Integer.parseInt(s);
			} catch (NumberFormatException nfe) {}
		}
		for (int a=0;a<10;a++) {
			s = getParameter("move"+a);
			if (s!=null) allMoves.addElement(s);
		}

		V=new View();
		add("Center",V);
		setSize(new Dimension(400,250));
		setVisible(true);
		Panel p=new Panel(new FlowLayout());
		for (int a=0;a<allMoves.size();a++){
			Button B=new Button(allMoves.elementAt(a).toString());
			B.addActionListener(this);
			p.add(B);
		}
		add("South",p);
		new Thread(V).start();
	}

	public void actionPerformed(ActionEvent ae) {
		View.DEBUG=false;
		Object o=ae.getSource();
		if (o instanceof Button) {
			Button b=(Button)o;
			String s=b.getLabel();
			String ss[]=aj.misc.Stuff.getTokens(s);
			newParams(ss);
		}
	}

	public void newParams(String s[]) {
		if (s.length>=2) {
			try {
				View.pos=new int[s.length];
				for (int a=0;a<s.length;a++) {
					View.pos[a]=Integer.parseInt(s[a]);
				}
			} catch (NumberFormatException e) {
				System.out.println("setup bad read");
				View.pos=new int[2];
				View.pos[0]=1;View.pos[1]=2;
			}
		}
		V.mp=null;
	}
}
