package aj.proof;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 29, 2000 
 */
public class Term {
	int type = Token.ERROR;
	String val = "ERROR";
	Function fun = null;
	int varcount = 0;
	static int UNBOUND = -1;


	/**
	 *  Constructor for the Term object 
	 *
	 *@param  type  Description of Parameter 
	 *@param  val   Description of Parameter 
	 */
	public Term(int type, String val) {
		this.type = type;
		this.val = val;
	}


	/**
	 *  Constructor for the Term object 
	 *
	 *@param  type  Description of Parameter 
	 *@param  f     Description of Parameter 
	 */
	public Term(int type, Function f) {
		this.type = type;
		this.val = "ERROR";
		fun = f;
	}


	/**
	 *  Constructor for the Term object 
	 *
	 *@param  T  Description of Parameter 
	 */
	public Term(Term T) {
		type = T.type;
		val = new String(T.val);
		if (T.fun != null) {
			fun = new Function(T.fun);
		}
		varcount = T.varcount;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  T  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public boolean contains(Term T) {
		if (type != Token.FUNCTION) {
			return false;
		}
		else {
			return fun.contains(T);
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  E  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public boolean equals(Term E) {
		if (E.type != type) {
			return false;
		}
		if (E.type == Token.GROUND) {
			if (val.equals(E.val)) {
				return true;
			}
		}
		if (E.type == Token.VARIABLE) {
			if (val.equals(E.val) && varcount == E.varcount) {
				return true;
			}
		}
		if (E.type == Token.FUNCTION) {
			if (fun.equals(E.fun)) {
				return true;
			}
		}
		return false;
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public String toString() {
		String t;
		if (type == Token.GROUND) {
			t = "" + val + "";
		}
		else if (type == Token.VARIABLE) {
			t = val;
			if (varcount != UNBOUND) {
				t += varcount;
			}
			else {
				t += "UB!";
			}
		}
		else if (type == Token.FUNCTION) {
			t = fun.toString();
		}
		else {
			t = "Term ERROR";
		}
		return t;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  S  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static Term Parse(String S) {
		return Parse(new Tokens(S));
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  T  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public static Term Parse(Tokens T) {
		if (T.currType() == Token.GROUND || T.currType() == Token.VARIABLE) {
			String val = T.currVal();
			int type = T.currType();
			T.eat();
			return new Term(type, val);
		}
		else if (T.currType() == Token.FUNCTION) {
			int type = T.currType();
			return new Term(type, Function.Parse(T));
		}
		else {
			return new Term(Token.ERROR, "ERROR");
		}
	}
}
