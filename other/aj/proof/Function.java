package aj.proof;

import java.util.Vector;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 29, 2000 
 */
public class Function {
	Vector Termlist = new Vector();
	String val = null;


	/**
	 *  Constructor for the Function object 
	 *
	 *@param  val       Description of Parameter 
	 *@param  termlist  Description of Parameter 
	 */
	public Function(String val, Vector termlist) {
		if (val == null) {
			val = "ERROR";
		}
		if (termlist == null) {
			termlist = new Vector();
		}
		this.val = val;
		Termlist = termlist;
	}


	/**
	 *  Constructor for the Function object 
	 *
	 *@param  F  Description of Parameter 
	 */
	public Function(Function F) {
		Termlist = new Vector();
		for (int a = 0; a < F.Termlist.size(); a++) {
			Termlist.addElement(new Term((Term) F.Termlist.elementAt(a)));
		}
		val = new String(F.val);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  S  Description of Parameter 
	 */
	public void sub(Subst S) {
		int b;
		for (b = 0; b < Termlist.size(); b++) {
			Term T = (Term) Termlist.elementAt(b);
			T = S.change(T);
			Termlist.setElementAt(T, b);
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  T  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public boolean contains(Term T) {
		for (int a = 0; a < Termlist.size(); a++) {
			Term T2 = (Term) Termlist.elementAt(a);
			if (T2.equals(T)) {
				return true;
			}
			if (T2.contains(T)) {
				return true;
			}
		}
		return false;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  F  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public boolean equals(Function F) {
		if (!val.equals(F.val)) {
			return false;
		}
		if (Termlist.size() != F.Termlist.size()) {
			return false;
		}
		for (int a = 0; a < Termlist.size(); a++) {
			Term K;
			Term J;
			K = (Term) Termlist.elementAt(a);
			J = (Term) F.Termlist.elementAt(a);
			if (!K.equals(J)) {
				return false;
			}
		}
		return true;
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public String toString() {
		String t;
		t = "" + val + "(";
		for (int a = 0; a < Termlist.size(); a++) {
			t += Termlist.elementAt(a).toString();
			if (a + 1 < Termlist.size()) {
				t += ", ";
			}
		}
		t += ")";
		return t;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  S  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static Function Parse(String S) {
		return Parse(new Tokens(S));
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  T  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static Function Parse(Tokens T) {
		String val = "ERROR";
		Vector TermList = new Vector();
		if (T.currType() == Token.FUNCTION) {
			val = T.currVal();
			T.eat();
			if (T.currType() != Token.LPAREN) {
				return new Function("ERROR", TermList);
			}
			T.eat();
			while (T.currType() != Token.RPAREN && T.currType() != Token.ERROR) {
				Term t = Term.Parse(T);
				if (t.type == Token.ERROR) {
					return new Function("ERROR", TermList);
				}
				TermList.addElement(t);
				if (T.currType() == Token.COMA) {
					T.eat();
				}
			}
			if (T.currType() != Token.RPAREN) {
				return new Function("ERROR", TermList);
			}
			T.eat();
			return new Function(val, TermList);
		}
		return new Function("ERROR", TermList);
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  argv  Description of Parameter 
	 */
	public static void main(String argv[]) {
		String test = "#Funname(T1,t2,#iner())";
		System.out.println("Testing Function");
		System.out.println("entered <" + test + ">");
		Function f = Function.Parse(new Tokens(test));
		System.out.println("result <" + f + ">");
	}
}
