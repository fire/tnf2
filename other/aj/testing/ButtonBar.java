package aj.testing;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;

import aj.awt.BoxLayout;
import aj.awt.IButton;
import aj.awt.SimpleWindowManager;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 29, 2000 
 */
public class ButtonBar extends Frame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Vector blist = new Vector();
	Vector exelist = new Vector();

	int width = -1, height = -1;


	/**
	 *  Constructor for the ButtonBar object 
	 *
	 *@param  s  Description of Parameter 
	 */
	public ButtonBar(String s[]) {
		super("ButtonBar");
		for (int a = 0; a < s.length; a++) {
			if (s[a].startsWith("-c")) {
				readConfigFile(s[a]);
				continue;
			}
			if (s[a].startsWith("width=")) {
				try {
					width = Integer.parseInt(s[a].substring(6).trim());
				}
				catch (Exception E) {
				}
			}
			if (s[a].startsWith("height=")) {
				try {
					height = Integer.parseInt(s[a].substring(7).trim());
				}
				catch (Exception E) {
				}
			}
			if (s[a].indexOf(",") < 0) {
				continue;
			}
			String b = s[a].substring(0, s[a].indexOf(",")).trim();
			String e = s[a].substring(s[a].indexOf(",") + 1).trim();
			if (e.equals("") || b.equals("")) {
				continue;
			}
			Component B;
			if (b.toUpperCase().indexOf(".GIF") > 0) {
				IButton BBB = new IButton(b);
				BBB.addActionListener(this);
				B = BBB;
			}
			else {
				Button BBB = new Button(b);
				BBB.addActionListener(this);
				B = BBB;
			}
			blist.addElement(B);
			exelist.addElement(e);
		}
		Panel P = new Panel(new BoxLayout(1, blist.size()));
		P.setLayout(new FlowLayout());
		//    P.setLayout(new BoxLayout(1,4));
		for (int a = 0; a < blist.size(); a++) {
			P.add((Component) blist.elementAt(a));
		}
		//      P.add("North",(Component)blist.elementAt(a));
		setLayout(new BorderLayout());
		add("Center", P);
		pack();
		if (width != -1 && height != -1) {
			setSize(new Dimension(width, height));
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public void readConfigFile(String s) {
		try {
			BufferedReader BR = new BufferedReader(new FileReader(s));
			while (BR.ready()) {
				BR.readLine();
			}
		}
		catch (Exception E) {
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  AE  Description of Parameter 
	 */
	public void actionPerformed(ActionEvent AE) {
		for (int a = 0; a < blist.size(); a++) {
			if (AE.getSource().equals(blist.elementAt(a))) {
				String tar = (String) exelist.elementAt(a);
				while (tar.indexOf("\"") >= 0) {
					tar = tar.substring(0, tar.indexOf("\"")).trim() + tar.substring(tar.indexOf("\"") + 1);
				}
				System.out.println("Exe called for " + exelist.elementAt(a));
				Runtime t = Runtime.getRuntime();
				try {
					t.exec(tar);
					//          p.waitFor();
					//          p.destroy();
				}
				catch (Exception E) {
					System.out.println("myError: Runtime Error?" + E);
				}
				break;
			}
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  s  Description of Parameter 
	 */
	public static void main(String s[]) {
		if (s.length == 0) {
			System.out.println("FORMAT: java ButtonBar <button_name or imagefile>,\"<executable>\"");
			System.exit(0);
		}
		ButtonBar BB = new ButtonBar(s);
		BB.setVisible(true);
		BB.addWindowListener(new SimpleWindowManager());
	}

}
// Program Files/Microsoft Office/Office/EXCEL.EXE
// Program Files/Microsoft Office/Office/WINWORD.EXE
