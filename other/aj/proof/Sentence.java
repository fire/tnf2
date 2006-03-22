package aj.proof;

import java.util.Vector;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 29, 2000 
 */
public class Sentence {
	int type;
	boolean notted;
	Sentence Rsent, Lsent;
	String connector;
	Predicate Atom;
	String Qualifier;
	Vector VarList;


	//  public Sentence(int type, Predicate P) {
	//    Atom=new Predicate(P);
	//    this.type=type;
	//    this.type=Token.ATOMICSENT;
	//  }
	/**
	 *  Constructor for the Sentence object 
	 *
	 *@param  notted  Description of Parameter 
	 *@param  P       Description of Parameter 
	 */
	public Sentence(boolean notted, Predicate P) {
		Atom = new Predicate(P);
		this.type = Token.ATOMICSENT;
		this.notted = notted;
	}


	//  public Sentence(boolean notted, int type, Predicate P) {
	//    Atom=new Predicate(P);
	//    this.type=type;
	//    this.type=Token.ATOMICSENT;
	//    this.notted=notted;
	//  }
	/**
	 *  Constructor for the Sentence object 
	 *
	 *@param  type  Description of Parameter 
	 *@param  s     Description of Parameter 
	 */
	public Sentence(int type, Sentence s) {
		Lsent = Sentence.copy(s);
		//    this.type=type;
		this.type = Token.PARENSENT;
	}


	/**
	 *  Constructor for the Sentence object 
	 *
	 *@param  notted  Description of Parameter 
	 *@param  type    Description of Parameter 
	 *@param  s       Description of Parameter 
	 */
	public Sentence(boolean notted, int type, Sentence s) {
		Lsent = Sentence.copy(s);
		//    this.type=type;
		this.type = Token.PARENSENT;
		this.notted = notted;
	}


	/**
	 *  Constructor for the Sentence object 
	 *
	 *@param  S  Description of Parameter 
	 *@param  C  Description of Parameter 
	 *@param  T  Description of Parameter 
	 */
	public Sentence(Sentence S, String C, Sentence T) {
		Lsent = Sentence.copy(S);
		Rsent = Sentence.copy(T);
		type = Token.CONNECTSENT;
		connector = new String(C);
	}


	/**
	 *  Constructor for the Sentence object 
	 *
	 *@param  notted   Description of Parameter 
	 *@param  type     Description of Parameter 
	 *@param  qual     Description of Parameter 
	 *@param  VarList  Description of Parameter 
	 *@param  s        Description of Parameter 
	 */
	public Sentence(boolean notted, int type, String qual, Vector VarList, Sentence s) {
		Lsent = Sentence.copy(s);
		this.VarList = VarList;
		this.Qualifier = qual;
		//    this.type=type;
		this.type = Token.QUALIFIEDSENT;
		this.notted = notted;
	}


	/**
	 *  Constructor for the Sentence object 
	 *
	 *@param  S  Description of Parameter 
	 */
	public Sentence(Sentence S) {
		type = S.type;
		notted = S.notted;
		if (S.Atom != null) {
			Atom = new Predicate(S.Atom);
		}
		if (S.Lsent != null) {
			Lsent = Sentence.copy(S.Lsent);
		}
		if (S.Rsent != null) {
			Rsent = Sentence.copy(S.Rsent);
		}
		connector = S.connector;
		Qualifier = S.Qualifier;
		if (S.VarList != null) {
			VarList = (Vector) S.VarList.clone();
		}
	}


	/**
	 *  Constructor for the Sentence object 
	 */
	private Sentence() {
	}


	/**
	 *  Gets the Atomic attribute of the Sentence object 
	 *
	 *@return    The Atomic value 
	 */
	public boolean isAtomic() {
		return type == Token.ATOMICSENT;
	}


	/**
	 *  Gets the Paren attribute of the Sentence object 
	 *
	 *@return    The Paren value 
	 */
	public boolean isParen() {
		return type == Token.PARENSENT;
	}


	/**
	 *  Gets the Connect attribute of the Sentence object 
	 *
	 *@return    The Connect value 
	 */
	public boolean isConnect() {
		return type == Token.CONNECTSENT;
	}


	/**
	 *  Gets the Qualified attribute of the Sentence object 
	 *
	 *@return    The Qualified value 
	 */
	public boolean isQualified() {
		return type == Token.QUALIFIEDSENT;
	}


	/**
	 *  Gets the LeftSent attribute of the Sentence object 
	 *
	 *@return    The LeftSent value 
	 */
	public Sentence getLeftSent() {
		return Sentence.copy(Lsent);
	}


	/**
	 *  Gets the RightSent attribute of the Sentence object 
	 *
	 *@return    The RightSent value 
	 */
	public Sentence getRightSent() {
		return Sentence.copy(Rsent);
	}


	/**
	 *  Gets the ParenSent attribute of the Sentence object 
	 *
	 *@return    The ParenSent value 
	 */
	public Sentence getParenSent() {
		return Sentence.copy(Lsent);
	}


	/**
	 *  Description of the Method 
	 */
	public void not() {
		if (type == Token.ATOMICSENT) {
			Atom.notted = !Atom.notted;
		}
		else if (type == Token.PARENSENT || type == Token.QUALIFIEDSENT) {
			notted = !notted;
		}
		else if (type == Token.CONNECTSENT) {
			type = Token.PARENSENT;
			Lsent = new Sentence(Lsent, connector, Rsent);
			notted = true;
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public String toString() {
		String t = "";
		if (type == Token.ATOMICSENT) {
			t += Atom.toString();
		}
		else if (type == Token.PARENSENT) {
			t += "(" + Lsent.toString() + ")";
		}
		else if (type == Token.QUALIFIEDSENT) {
			t += "[" + Qualifier + " ";
			for (int a = 0; a < VarList.size(); a++) {
				t += VarList.elementAt(a).toString();
				if (a + 1 < VarList.size()) {
					t += ", ";
				}
			}
			t += ":";
			t += Lsent.toString();
			t += "]";
		}
		else if (type == Token.CONNECTSENT) {
			t += Lsent.toString() + " " + connector + " " + Rsent.toString();
		}
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
	public static Sentence copy(Sentence S) {
		Sentence SS = new Sentence();
		SS.type = S.type;
		SS.notted = S.notted;
		if (S.Atom != null) {
			SS.Atom = new Predicate(S.Atom);
		}
		if (S.Lsent != null) {
			SS.Lsent = Sentence.copy(S.Lsent);
		}
		if (S.Rsent != null) {
			SS.Rsent = Sentence.copy(S.Rsent);
		}
		SS.connector = S.connector;
		SS.Qualifier = S.Qualifier;
		if (S.VarList != null) {
			SS.VarList = (Vector) S.VarList.clone();
		}
		return SS;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  S  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static Sentence Parse(String S) {
		return Parse(new Tokens(S));
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  T  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static Sentence Parse(Tokens T) {
		boolean notted = false;
		if (T.currType() == Token.NOT) {
			notted = true;
			T.eat();
		}
		Sentence atom = ParseAtom(T);
		if (notted) {
			atom.not();
		}
		while (T.currType() == Token.CONNECTIVE) {
			String connector = T.currVal();
			T.eat();
			Sentence ant = Sentence.ParseAtom(T);
			atom = new Sentence(atom, connector, ant);
		}
		return atom;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  argv  Description of Parameter 
	 */
	public static void main(String argv[]) {
		String test[] = {"?pred(hi,You)", "?pred(hi,You)-->?Guys(Me,him)", 
				"((?pred(hi,You))-->a=b)", "~?Pred(hi,You)", "((B=t)-->~(T=t))", 
				"[FORALL x,y: ?man(x)-->~?Woman(y)]", 
				"(?p(X) and ?p(Y)) or ?p(Z)"};

		System.out.println("Testing Sentence");
		for (int a=0; a < test.length; a++) {
			System.out.println("entered <" + test[a] + ">");
			Sentence s = Sentence.Parse(test[a]);
			System.out.println("result <" + s + ">");
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  T  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	private static Sentence ParseAtom(Tokens T) {
		boolean notted = false;
		int type = Token.ERROR;

		if (T.currType() == Token.NOT) {
			notted = true;
			T.eat();
		}
		if (T.currType() == Token.LPAREN) {
			type = Token.PARENSENT;
			T.eat();
			//eat left paren
			Sentence s = Sentence.Parse(T);
			T.eat();
			//eat right paren
			return new Sentence(notted, type, s);
		}
		else if (T.currType() == Token.LBRACK) {
			type = Token.QUALIFIEDSENT;
			T.eat();
			//eat bracked
			String qual = T.currVal();
			T.eat();
			Vector VarList = new Vector();
			while (T.currType() == Token.VARIABLE) {
				Term t = Term.Parse(T);
				VarList.addElement(t);
				if (T.currType() == Token.COMA) {
					T.eat();
				}
			}
			T.eat();
			//eat colon or OR token
			Sentence s = Sentence.Parse(T);
			T.eat();
			//eat RBRACK
			return new Sentence(notted, type, qual, VarList, s);
		}
		else if (T.currType() == Token.GROUND || T.currType() == Token.VARIABLE || 
				T.currType() == Token.PREDICATE || T.currType() == Token.FUNCTION) {
			type = Token.ATOMICSENT;
			Predicate Atom = Predicate.Parse(T);
			Atom.notted = notted;
			notted = false;
			Sentence ss = new Sentence(notted, Atom);
			return ss;
		}
		else {
			return new Sentence();
		}
	}
}












