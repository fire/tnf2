package aj.proof;

import java.util.Vector;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 29, 2000 
 */
public class KnowBase {
	Vector Known;
	//vector of vectors or predicates that are OR connected
	int Assertsize;
	int ENDSIZE;

	String rules = "";
	String results = "";
	String subs = "";
	static boolean verbose = false, showsub = false;


	/**
	 *  Constructor for the KnowBase object 
	 */
	public KnowBase() {
		Known = new Vector();
	}


	/**
	 *  Gets the Rules attribute of the KnowBase object 
	 *
	 *@return    The Rules value 
	 */
	public String getRules() {
		makeRules();
		return rules;
	}


	/**
	 *  Gets the Results attribute of the KnowBase object 
	 *
	 *@return    The Results value 
	 */
	public String getResults() {
		return results;
	}


	/**
	 *  Gets the Subs attribute of the KnowBase object 
	 *
	 *@return    The Subs value 
	 */
	public String getSubs() {
		return subs;
	}


	/**
	 *  Gets the Duplicate attribute of the KnowBase object 
	 *
	 *@param  v1  Description of Parameter 
	 *@param  v2  Description of Parameter 
	 *@return     The Duplicate value 
	 */
	public boolean isDuplicate(Vector v1, Vector v2) {
		if (v1.size() != v2.size()) {
			return false;
		}
		int a;
		int b;
		for (a = 0; a < v1.size(); a++) {
			Predicate P1 = (Predicate) v1.elementAt(a);
			boolean found = false;
			for (b = 0; b < v2.size(); b++) {
				Predicate P2 = (Predicate) v2.elementAt(b);
				if (P1.equals(P2)) {
					found = true;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  I  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public String add(Input I) {
		results = "";
		subs = "";
		ENDSIZE = Known.size();
		String S = include(I.S);
		if (I.type == Token.QUERY && S.equalsIgnoreCase("Okay")) {
			String r = Resolve();
			while (Known.size() > ENDSIZE) {
				Known.removeElementAt(Known.size() - 1);
			}
			return r;
		}
		else {
			//      int QUERYSIZE=Known.size();
			//      String r=Resolve();
			//      while (Known.size()>QUERYSIZE)
			//        Known.removeElementAt(Known.size()-1);
			//      if (r.equalsIgnoreCase("True")) {
			//        while (Known.size()>ENDSIZE)
			//          Known.removeElementAt(Known.size()-1);
			//        return "Fail: Contradiction entered";
			//      }
			//      if (!S.equalsIgnoreCase("Okay")) return "Fail";
			return S;
		}
	}


	////////////////RESULUTION//////////////////////////////
	/**
	 *  Description of the Method 
	 *
	 *@param  T1  Description of Parameter 
	 *@param  T2  Description of Parameter 
	 *@return     Description of the Returned Value 
	 */
	public Vector Unify(Vector T1, Vector T2) {
		T1 = (Vector) T1.clone();
		T2 = (Vector) T2.clone();
		Vector Ans = new Vector();
		if (T1.size() != T2.size()) {
			Ans.setElementAt(new Subst(), 0);
			//FAIL
			return Ans;
		}
		Term t1;
		Term t2;
		int a;
		int b;
		for (a = 0; a < T1.size(); a++) {
			t1 = (Term) T1.elementAt(a);
			t2 = (Term) T2.elementAt(a);
			if (t1.equals(t2)) {
				return new Vector();
			}
			//return nill
			if (t1.contains(t2) || t2.contains(t1)) {
				Ans.insertElementAt(new Subst(), 0);
				//FAIL
				return Ans;
			}
			else if (t1.type == Token.VARIABLE && t2.type == Token.VARIABLE) {
				Ans.addElement(new Subst(t1, t2));
			}
			else if (t1.type == Token.VARIABLE && t2.type == Token.GROUND) {
				Ans.addElement(new Subst(t1, t2));
			}
			else if (t1.type == Token.VARIABLE && t2.type == Token.FUNCTION) {
				if (t2.contains(t1)) {
					Ans.insertElementAt(new Subst(), 0);
					//FAIL
					return Ans;
				}
				else {
					Ans.addElement(new Subst(t1, t2));
				}
			}
			else if (t2.type == Token.VARIABLE && t1.type == Token.GROUND) {
				Ans.addElement(new Subst(t2, t1));
			}
			else if (t2.type == Token.VARIABLE && t1.type == Token.FUNCTION) {
				if (t1.contains(t2)) {
					Ans.insertElementAt(new Subst(), 0);
					//FAIL
					return Ans;
				}
				else {
					Ans.addElement(new Subst(t2, t1));
				}
			}
			else if (t2.type == Token.FUNCTION && t1.type == Token.FUNCTION) {
				if (!t2.fun.val.equals(t1.fun.val)) {
					//only unify function of same name
					Ans.insertElementAt(new Subst(), 0);
					//FAIL
					return Ans;
				}
				Vector Ans2 = Unify(t2.fun.Termlist, t1.fun.Termlist);
				for (int cc = 0; cc < Ans2.size(); cc++) {
					Subst SS = (Subst) Ans2.elementAt(cc);
					if (SS.FAIL) {
						Ans.insertElementAt(new Subst(), 0);
						//FAIL
						return Ans;
					}
					else {
						Ans.addElement(SS);
					}
				}
			}
			else {
				Ans.insertElementAt(new Subst(), 0);
				//FAIL
				return Ans;
			}
			if (Ans.size() > 0) {
				Subst SSS = (Subst) Ans.elementAt(Ans.size() - 1);
				for (b = a + 1; b < T1.size(); b++) {
					Term L = (Term) T1.elementAt(b);
					L = new Term(SSS.change(L));
					T1.setElementAt(L, b);
					L = (Term) T2.elementAt(b);
					L = new Term(SSS.change(L));
					T2.setElementAt(L, b);
				}
			}
			//apply substitiutions on E1 and E2??
		}
		return Ans;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  V  Description of Parameter 
	 *@param  T  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public boolean CanResolve(Vector V, Vector T) {
		Predicate P1;
		Predicate P2;
		int a;
		int b;
		boolean OK = false;
		for (a = 0; a < V.size() && !OK; a++) {
			P1 = (Predicate) V.elementAt(a);
			for (b = 0; b < T.size() && !OK; b++) {
				P2 = (Predicate) T.elementAt(b);
				if (P2.resolveswith(P1)) {
					OK = true;
					Vector Ans = Unify(P1.Termlist, P2.Termlist);
					for (int c = 0; c < Ans.size(); c++) {
						Subst S = (Subst) Ans.elementAt(c);
						if (S.FAIL) {
							OK = false;
						}
					}
				}
			}
		}
		//    System.out.println("CAN RESOLVE?"+OK);
		//    System.out.println("V="+showWWF(V));
		//    System.out.println("T="+showWWF(T));
		return OK;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  Sublist  Description of Parameter 
	 */
	public void makeResultSub(Vector Sublist) {
		int a;
		results = "";
		subs = "";
		for (a = ENDSIZE; a < Known.size(); a++) {
			Vector V = (Vector) Known.elementAt(a);
			results += "STEP" + (a - ENDSIZE + 1) + ": " + showWWF(V) + "\n";
		}
		subs += "sublist=" + Sublist + "\n";
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  V        Description of Parameter 
	 *@param  Sublist  Description of Parameter 
	 *@return          Description of the Returned Value 
	 */
	public Vector Dosubs(Vector V, Vector Sublist) {
		//System.out.println("DOING SUBS FOR WWF=>"+showWWF(V));
		int a;
		for (a = 0; a < V.size(); a++) {
			Predicate P = (Predicate) V.elementAt(a);
			P.sub(Sublist);
			V.setElementAt(P, a);
		}
		//System.out.println("RESULT=>"+showWWF(V));
		return V;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  V  Description of Parameter 
	 *@param  T  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public Vector UnifyWFF(Vector V, Vector T) {
		Predicate P1;
		Predicate P2;
		Vector subit = UnifyWWFSUBS(V, T);
		Vector Ans = new Vector();
		int a;
		int b;
		int Vpos = -1;
		int Tpos = -1;
		for (a = 0; a < V.size() && Vpos == -1; a++) {
			P1 = (Predicate) V.elementAt(a);
			for (b = 0; b < T.size() && Tpos == -1; b++) {
				P2 = (Predicate) T.elementAt(b);
				if (P2.resolveswith(P1)) {
					Vector test = Unify(P1.Termlist, P2.Termlist);
					if (test.size() == 0) {
						Vpos = a;
						Tpos = b;
					}
					else {
						Subst SS = (Subst) test.elementAt(0);
						if (!SS.FAIL) {
							Vpos = a;
						}
						Tpos = b;
					}
				}
			}
		}
		for (a = 0; a < V.size(); a++) {
			if (a != Vpos) {
				Ans.addElement(new Predicate((Predicate) V.elementAt(a)));
			}
		}
		for (a = 0; a < T.size(); a++) {
			if (a != Tpos) {
				Ans.addElement(new Predicate((Predicate) T.elementAt(a)));
			}
		}
		//System.out.println("NEW SENT="+showWWF(Ans));
		Ans = Dosubs(Ans, subit);
		return Ans;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  V  Description of Parameter 
	 *@param  T  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public Vector UnifyWWFSUBS(Vector V, Vector T) {
		Predicate P1;
		Predicate P2;
		int a;
		int b;
		for (a = 0; a < V.size(); a++) {
			P1 = (Predicate) V.elementAt(a);
			for (b = 0; b < T.size(); b++) {
				P2 = (Predicate) T.elementAt(b);
				if (P2.resolveswith(P1)) {
					Vector test = Unify(P1.Termlist, P2.Termlist);
					if (test.size() == 0) {
						return test;
					}
					Subst SS = (Subst) test.elementAt(0);
					if (!SS.FAIL) {
						return test;
					}
				}
			}
		}
		Vector Ans = new Vector();
		Ans.addElement(new Subst());
		//error should never get here
		System.err.println("ERROR UnifyWWFSUBS");
		return Ans;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  V  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public Vector cleanUp(Vector V) {
		int a;
		int b;
		for (a = 0; a < V.size(); a++) {
			for (b = a + 1; b < V.size(); b++) {
				Predicate P1 = (Predicate) V.elementAt(a);
				Predicate P2 = (Predicate) V.elementAt(b);
				if (P1.equals(P2)) {
					V.removeElement(P2);
					b--;
					continue;
				}
				else if (P1.resolveswith(P2)) {
					return new Vector();
				}
			}
		}
		return V;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  V  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public Vector Resolve(Vector V) {
		//System.out.println("resolveing "+showWWF(V));
		int a;
		Vector Sublist = new Vector();
		Vector Work;
		boolean found = false;
		for (a = 0; a < Known.size(); a++) {
			Sublist = new Vector();
			Work = (Vector) Known.elementAt(a);
			if (Work == V) {
				continue;
			}
			if (Work.size() == 0) {
				//        System.out.println("answer comming 0");
				return Sublist;
			}
			if (CanResolve(V, Work)) {
				//System.out.println("resolveing "+showWWF(V)+" on "+showWWF(Work));
				Vector nextWork = UnifyWFF(V, Work);
				Sublist = UnifyWWFSUBS(V, Work);
				nextWork = cleanUp(nextWork);
				if (!newRule(nextWork)) {
					continue;
				}
				if (nextWork.size() == 0) {
					//          System.out.println("answer comming 1");
					return Sublist;
				}
				Vector ans = Resolve(nextWork);
				if (ans.size() == 0) {
					//          System.out.println("answer comming 2");
					return Sublist;
				}
				Subst SS = (Subst) ans.elementAt(0);
				if (!SS.FAIL) {
					for (int b = 0; b < ans.size(); b++) {
						if (!Sublist.contains(ans.elementAt(b))) {
							Sublist.addElement(ans.elementAt(b));
						}
					}
					//          System.out.println("answer comming 3");
					return Sublist;
				}

			}
		}
		Sublist.insertElementAt(new Subst(), 0);
		//FAIL
		//    System.out.println("answer comming 4");
		return Sublist;
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public String Resolve() {
		int a;
		for (a = ENDSIZE; a < Known.size(); a++) {
			Vector Work = (Vector) Known.elementAt(a);
			Vector Ans = Resolve(Work);
			if (Ans.size() == 0) {
				makeResultSub(Ans);
				return "True";
			}
			Subst subst = (Subst) Ans.elementAt(0);
			if (!subst.FAIL) {
				makeResultSub(Ans);
				return "True";
			}
		}
		return "Unprovable";
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  V  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public boolean newRule(Vector V) {
		V = (Vector) V.clone();
		int a;
		int b;
		for (a = 0; a < Known.size(); a++) {
			Vector v2 = (Vector) Known.elementAt(a);
			if (isDuplicate(V, v2)) {
				//        System.out.println("DUPLICAT RULE FOUND SKIPING "+showWWF(V));
				return false;
			}
		}
		//System.out.println("NEW RULE ASSERTED "+showWWF(V));
		for (a = 0; a < V.size(); a++) {
			for (b = a + 1; b < V.size(); b++) {
				Predicate P1 = (Predicate) V.elementAt(a);
				Predicate P2 = (Predicate) V.elementAt(b);
				if (P1.equals(P2)) {
					V.removeElement(P2);
					b--;
					continue;
				}
				else {
					Vector v1 = new Vector();
					Vector v2 = new Vector();
					v1.addElement(P1);
					v2.addElement(P2);
					if (CanResolve(v1, v2)) {
						V.removeElement(P1);
						V.removeElement(P2);
						a = -1;
						break;
					}
				}
			}
		}
		if (V.size() != 0) {
			Known.addElement(V);
		}
		return true;
	}


	////////////////////////////////////////////////////////
	/**
	 *  Description of the Method 
	 *
	 *@param  S  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public String include(Sentence S) {
		//System.out.println("including "+S);
		Vector T = new Vector();
		if (S.type == Token.ATOMICSENT) {
			//System.out.println("atom");
			T.addElement(S.Atom);
			newRule(T);
			return "Okay";
		}
		else if (S.type == Token.PARENSENT) {
			return include(S.Lsent);
		}
		else if (S.type == Token.CONNECTSENT) {
			if (S.connector.equals("OR")) {
				//System.out.println("connector or");
				while (S.type == Token.CONNECTSENT) {
					T.addElement(S.Rsent.Atom);
					S = S.Lsent;
				}
				T.addElement(S.Atom);
				newRule(T);
				return "Okay";
			}
			else {
				//System.out.println("connector and");
				String s2;
				//System.out.println("connector and");
				String s1;
				s2 = include(S.Lsent);
				s1 = include(S.Rsent);
				if (s1.equalsIgnoreCase("Okay") && s2.equalsIgnoreCase("Okay")) {
					return "Okay";
				}
				else {
					return "Fail";
				}
			}
		}
		else {
			return "Fail";
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  V  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public String showSUB(Vector V) {
		int a;
		System.out.println("Sub List =>");
		String S = "";
		for (a = 0; a < V.size(); a++) {
			Subst P = (Subst) V.elementAt(a);
			S += "" + P;
			if (a < V.size() - 1) {
				S += ",";
			}
		}
		return S;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  V  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public String showWWF(Vector V) {
		int a;
		String S = "";
		for (a = 0; a < V.size(); a++) {
			Predicate P = (Predicate) V.elementAt(a);
			S += "" + P;
			if (a < V.size() - 1) {
				S += " OR ";
			}
		}
		return S;
	}


	/**
	 *  Description of the Method 
	 */
	public void makeRules() {
		int a;
		int b;
		rules = "";
		for (a = 0; a < Known.size(); a++) {
			rules += "RULE" + (a + 1) + "> " + showWWF((Vector) Known.elementAt(a)) + "\n";
		}
	}
}
