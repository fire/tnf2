package aj.proof;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 29, 2000 
 */
public class Proof extends Applet implements ActionListener {
	KnowBase KB = new KnowBase();

	String menu = 
			"----------------------MENU-------------------------\n" + 
			"HELP, FILE <FileName>, SHOWRULES, SHOWRESULTS, SHOWSUBS, CLEAR\n" + 
			"---------------------------------------------------\n" + 
			"Predicates and Termianls are case sensitive.\n" + 
			"Predicates begin with ?{ie ?man(Tom)}. \n" + 
			"Termials begin with capital {ie Tom}.\n" + 
			"Qualifies require scoping [ ] {ie [ forall x : ?man(x) ]}\n" + 
			"Operations and case insensitive\n" + 
			"ASSERT QUERY FORALL EXISTS \n" + 
			"AND(&,&&) OR(|,||) IMP(=>,==>,-->) IFF(<=>,<->) NOT(~,!) \n" + 
			"---------------------sample------------------------\n" + 
			"ASSERT [ FORALL x : ?Alive(x)=>~?Dead(x)].\n" + 
			"ASSERT ~[ EXISTS x : ?Man(x)<=>?Woman(x)].\n" + 
			"ASSERT ?Man(Plato).\n" + 
			"QUERY ?Alive(Plato) || ~?Woman(Plato)?\n";

	String appletmenu = 
			"----------------------MENU-------------------------\n" + 
			"HELP, FILE <FileName>, SHOWRULES, SHOWRESULTS, SHOWSUBS, CLEAR\n" + 
			"---------------------------------------------------\n" + 
			"Predicates and Termianls are case sensitive.\n" + 
			"Predicates begin with ?{ie ?man(Tom)}. \n" + 
			"Termials begin with capital {ie Tom}.\n" + 
			"Qualifies require scoping [ ] {ie [ forall x : ?man(x) ]}\n" + 
			"Operations and case insensitive\n" + 
			"ASSERT QUERY FORALL EXISTS \n" + 
			"AND(&,&&) OR(|,||) IMP(=>,==>,-->) IFF(<=>,<->) NOT(~,!) \n" + 
			"---------------------sample------------------------\n" + 
			"ASSERT [ FORALL x : ?Alive(x)=>~?Dead(x)].\n" + 
			"ASSERT ~[ EXISTS x : ?Man(x)<=>?Woman(x)].\n" + 
			"ASSERT ?Man(Plato).\n" + 
			"QUERY ?Alive(Plato) || ~?Woman(Plato)?\n";

	boolean applet = false;

	static TextField input = new TextField(20);
	static TextArea rules = new TextArea(10, 20);
	static TextArea results = new TextArea(10, 20);


	/**
	 *  Constructor for the Proof object 
	 */
	public Proof() {
		applet = true;
		Panel mid = new Panel(new BorderLayout());
		mid.add("North", rules);
		mid.add("South", results);
		setLayout(new BorderLayout());
		add("North", mid);
		add("South", input);
		input.addActionListener(this);
		input.setFont(new Font("Courier", Font.PLAIN, 12));

		rules.setEditable(false);
		rules.setBackground(Color.lightGray);
		rules.setFont(new Font("Courier", Font.PLAIN, 12));
		results.setEditable(false);
		results.setBackground(Color.gray);
		results.setBackground(Color.lightGray);
		results.setFont(new Font("Courier", Font.PLAIN, 12));
	}


	/**
	 *  Constructor for the Proof object 
	 *
	 *@param  argv  Description of Parameter 
	 */
	public Proof(String argv[]) {
		if (argv.length == 1) {
			readfile(argv[0]);
		}
		try {
			readkeyboard();
		}
		catch (IOException foo) {
			System.err.println("MyError: UNKNOW IO ERROR");
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  F  Description of Parameter 
	 */
	public void readfile(String F) {
		String S;
		char ch;
		try {
			BufferedReader dstr = new BufferedReader(new FileReader(F));
			while (true) {
				S = dstr.readLine();
				if (S == null) {
					break;
				}
				System.out.println(command(S));
			}
		}
		catch (FileNotFoundException foo) {
			System.out.println("FILE NOT FOUND");
		}
		catch (IOException foo) {
			System.out.println("UNKNOW IO ERROR");
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@exception  IOException  Description of Exception 
	 */
	public void readkeyboard() throws IOException {
		String S;
		BufferedReader dstr = new BufferedReader(new InputStreamReader(System.in));
		System.out.println(menu);
		while (true) {
			System.out.print("PROOF>");
			S = dstr.readLine();
			if (S == null) {
				break;
			}
			else if (S.toUpperCase().startsWith("SHOWRULES")) {
				String r = KB.getRules();
				System.out.println(r);
			}
			else if (S.toUpperCase().startsWith("SHOWRESULTS")) {
				String r = KB.getResults();
				System.out.println(r);
			}
			else if (S.toUpperCase().startsWith("SHOWSUBS")) {
				String r = KB.getSubs();
				System.out.println(r);
			}
			else if (S.toUpperCase().startsWith("HELP")) {
				System.out.println(menu);
			}
			else if (S.toUpperCase().startsWith("FILE")) {
				readfile(S.substring(4).trim());
				System.out.println("File done");
			}
			else {
				System.out.println(command(S));
			}
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  ae  Description of Parameter 
	 */
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == input) {
			String r = input.getText();
			input.setText("");
			r = r + "\n" + command(r);
			rules.setText(KB.getRules());
			if (r.startsWith("True")) {
				r += "\n" + KB.getResults() + "\n" + KB.getSubs();
			}
			results.setText(r);
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  S  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public String command(String S) {
		Normal n = new Normal();
		Input I;
		if (S.toUpperCase().startsWith("SHOW")) {
			return KB.getRules() + "\n" + KB.getResults() + "\n" + KB.getSubs();
		}
		else if (S.toUpperCase().startsWith("HELP")) {
			return (applet ? appletmenu : menu);
		}
		else if (S.toUpperCase().startsWith("CLEAR")) {
			KB = new KnowBase();
			return "Clear";
		}

		else {
			I = Input.Parse(S);
			if (I.type == Token.ERROR) {
				return "INPUT ERROR check syntax";
			}
			I = n.Fix(I);
			String r = KB.add(I);
			if (r.equalsIgnoreCase("FAIL")) {
				return "Error in comand: " + I;
			}
			return r;
		}
		//    return "Okay";
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  argv  Description of Parameter 
	 */
	public static void main(String argv[]) {
		new Proof(argv);
	}

}



