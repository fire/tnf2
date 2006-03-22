package aj.proof;

import java.util.Vector;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 29, 2000 
 */
public class Predicate {
	boolean notted = false;
	Vector Termlist = new Vector();
	String val = "ERROR";


	/**
	 *  Constructor for the Predicate object 
	 *
	 *@param  notted    Description of Parameter 
	 *@param  val       Description of Parameter 
	 *@param  Termlist  Description of Parameter 
	 */
	public Predicate(boolean notted, String val, Vector Termlist) {
		this.notted = notted;
		this.val = val;
		this.Termlist = Termlist;
	}


	/**
	 *  Constructor for the Predicate object 
	 *
	 *@param  P  Description of Parameter 
	 */
	public Predicate(Predicate P) {
		notted = P.notted;
		val = new String(P.val);
		Termlist = new Vector();
		if (P.Termlist == null) {
			P.Termlist = new Vector();
		}
		for (int a = 0; a < P.Termlist.size(); a++) {
			Termlist.addElement(new Term((Term) P.Termlist.elementAt(a)));
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  Sublist  Description of Parameter 
	 */
	public void sub(Vector Sublist) {
		for (int a = 0; a < Sublist.size(); a++) {
			Subst S = (Subst) Sublist.elementAt(a);
			for (int b = 0; b < Termlist.size(); b++) {
				Term T = (Term) Termlist.elementAt(b);
				T = S.change(T);
				Termlist.setElementAt(T, b);
			}
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  P  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public boolean equals(Predicate P) {
		if (notted != P.notted) {
			return false;
		}
		if (!val.equals(P.val)) {
			return false;
		}
		if (Termlist.size() != P.Termlist.size()) {
			return false;
		}
		for (int a = 0; a < Termlist.size(); a++) {
			Term K = (Term) Termlist.elementAt(a);
			Term J = (Term) P.Termlist.elementAt(a);
			if (!K.equals(J)) {
				return false;
			}
		}
		return true;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  P  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public boolean resolveswith(Predicate P) {
		if (P.val.equals(val) && P.Termlist.size() == Termlist.size() && 
				P.notted != notted) {
			return true;
		}
		else {
			return false;
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public String toString() {
		String t;
		t = "" + val + "(";
		int a;
		for (a = 0; a < Termlist.size(); a++) {
			t += Termlist.elementAt(a).toString();
			if (a + 1 < Termlist.size()) {
				t += ", ";
			}
		}
		t += ")";
		if (notted) {
			t = "~" + t;
		}
		return t;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  S  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static Predicate Parse(String S) {
		return Parse(new Tokens(S));
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  T  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static Predicate Parse(Tokens T) {
		boolean notted = false;
		Vector Termlist = new Vector();
		if (T.currType() == Token.PREDICATE) {
			//<pred> ( <termlist> )
			String val = T.currVal();
			T.eat();
			if (T.currType() != Token.LPAREN) {
				return new Predicate(false, "ERROR", null);
			}
			T.eat();
			Term t = Term.Parse(T);
			//read first term  MUS BE ONE
			Termlist.addElement(t);
			if (T.currType() == Token.COMA) {
				T.eat();
			}
			while (T.currType() != Token.RPAREN && T.currType() != Token.ERROR) {
				t = Term.Parse(T);
				Termlist.addElement(t);
				if (T.currType() == Token.COMA) {
					T.eat();
				}
			}
			if (T.currType() != Token.RPAREN) {
				return new Predicate(false, "ERROR", null);
			}
			T.eat();
			return new Predicate(notted, val, Termlist);
		}
		else {
			String val = "?Equals";
			// <term> '=' <term>
			Term t = Term.Parse(T);
			if (t.type == Token.ERROR) {
				return new Predicate(false, "EQ ERROR", null);
			}
			if (T.currType() != Token.EQUALS) {
				return new Predicate(false, "EQ ERROR", null);
			}
			T.eat();
			//eat equals sign
			Termlist.addElement(t);
			t = Term.Parse(T);
			if (t.type == Token.ERROR) {
				return new Predicate(false, "EQ ERROR", null);
			}
			Termlist.addElement(t);
			return new Predicate(false, val, Termlist);
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  argv  Description of Parameter 
	 */
	public static void main(String argv[]) {
		String test = "?Predname(T1,t2,#inerfun(G3,x))";
		System.out.println("Testing Predicate");
		System.out.println("entered <" + test + ">");
		Predicate p = Predicate.Parse(new Tokens(test));
		System.out.println("result <" + p + ">");
	}
}
