package aj.proof;

/**
 * Description of the Class
 * 
 * @author judda
 * @created August 29, 2000
 */
public class Subst {
	boolean FAIL;

	Term o, n;

	/**
	 * Constructor for the Subst object
	 */
	public Subst() {
		FAIL = true;
	}

	/**
	 * Constructor for the Subst object
	 * 
	 * @param O
	 *            Description of Parameter
	 * @param N
	 *            Description of Parameter
	 */
	public Subst(Term O, Term N) {
		FAIL = false;
		o = new Term(O);
		n = new Term(N);
	}

	/**
	 * Description of the Method
	 * 
	 * @param s
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public boolean equals(Subst s) {
		return ((o.equals(s.o) && n.equals(s.n)) || FAIL == s.FAIL);
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Returned Value
	 */
	public String toString() {
		if (FAIL) {
			return "FAILED!";
		} else {
			return ("Change " + o + " to " + n);
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param C
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public Term change(Term C) {
		if (FAIL) {
			System.out.println("BAD SUB!");
		} else if (C.equals(o)) {
			return new Term(n);
		} else if (C.type == Token.FUNCTION) {
			C.fun.sub(new Subst(o, n));
		}
		return C;
	}
}
