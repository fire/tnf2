package aj.proof;

import java.util.Vector;

/**
 * Description of the Class
 * 
 * @author judda
 * @created August 29, 2000
 */
public class Normal {
	static Vector Varnames = new Vector();

	static Vector Varnextnumber = new Vector();

	// x:3, y:2
	static Vector BoundVars = new Vector();

	static Vector BoundVarsnumber = new Vector();

	// x:3,y:2,x:1
	static int numSkol = 0;

	/**
	 * Constructor for the Normal object
	 */
	public Normal() {
	}

	/**
	 * Description of the Method
	 * 
	 * @param I
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public Input Fix(Input I) {
		if (I.type == Token.ERROR) {
			return I;
		}
		if (I.type == Token.QUERY) {
			I.S.notted = !I.S.notted;
		}
		// System.out.println("orig: "+I.S);
		I.S = RemoveImp(I.S);
		// System.out.println("remove imp: "+I.S);
		I.S = PushDownNeg(I.S);
		// System.out.println("push negs: "+I.S);
		I.S = StandardVar(I.S);
		// System.out.println("standard vars: "+I.S);
		I.S = Skolomize(I.S, new Vector(), new Vector());
		// add second vector of replacement terms
		// System.out.println("skolomized: "+I.S);
		I.S = QualifiersLeft(I.S);
		// System.out.println("quant left: "+I.S);
		I.S = DistOverAnd(I.S);
		// System.out.println("dist ands: "+I.S);
		I.S = Flatten(I.S);
		// System.out.println("flattened: "+I.S);
		// remove self resolving clauses & (?man(x) | ~?man(x))
		return I;
	}

	/**
	 * Description of the Method
	 * 
	 * @param S
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public Sentence QualifiersLeft(Sentence S) {
		Vector Varlist;
		Varlist = RemoveQual(S, "FORALL");
		if (Varlist.size() > 0) {
			S.Lsent = new Sentence(S);
			S.type = Token.QUALIFIEDSENT;
			S.Qualifier = "FORALL";
			S.VarList = Varlist;
		}
		return S;
	}

	/**
	 * Description of the Method
	 * 
	 * @param S
	 *            Description of Parameter
	 * @param Qual
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public Vector RemoveQual(Sentence S, String Qual) {
		if (S.type == Token.ATOMICSENT) {
			return new Vector();
		} else if (S.type == Token.PARENSENT) {
			return RemoveQual(S.Lsent, Qual);
		} else if (S.type == Token.CONNECTSENT) {
			Vector T = RemoveQual(S.Lsent, Qual);
			Vector U = RemoveQual(S.Rsent, Qual);
			for (int a = 0; a < U.size(); a++) {
				T.addElement(U.elementAt(a));
			}
			return T;
		} else if (S.type == Token.QUALIFIEDSENT) {
			if (S.Qualifier.equals(Qual)) {
				Vector T = RemoveQual(S.Lsent, Qual);
				Vector U = new Vector();
				for (int a = 0; a < S.VarList.size(); a++) {
					U.addElement(S.VarList.elementAt(a));
				}
				for (int a = 0; a < T.size(); a++) {
					U.addElement(T.elementAt(a));
				}
				S.type = S.Lsent.type;
				S.connector = S.Lsent.connector;
				S.notted = S.Lsent.notted;
				S.Qualifier = S.Lsent.Qualifier;
				S.Atom = S.Lsent.Atom;
				S.VarList = S.Lsent.VarList;
				S.Rsent = S.Lsent.Rsent;
				S.Lsent = S.Lsent.Lsent;
				return U;
			} else {
				String NewQual;
				if (Qual.equals("FORALL")) {
					NewQual = "EXISTS";
				} else {
					NewQual = "FORALL";
				}
				Vector T = RemoveQual(S.Lsent, NewQual);
				for (int a = 0; a < T.size(); a++) {
					S.VarList.addElement(T.elementAt(a));
				}
				return new Vector();
			}
		} else {
			// ERROR case
			return new Vector();
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param ChangeTerms
	 *            Description of Parameter
	 * @param OrgTerm
	 *            Description of Parameter
	 * @param NewTerms
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public Vector SkolomizeTerms(Vector ChangeTerms, Vector OrgTerm,
			Vector NewTerms) {
		int found;
		for (int a = 0; a < ChangeTerms.size(); a++) {
			// for each old term
			Term T = (Term) ChangeTerms.elementAt(a);
			found = -1;
			for (int b = 0; b < OrgTerm.size(); b++) {
				Term U = (Term) OrgTerm.elementAt(b);
				if (T.val.equals(U.val) && T.varcount == U.varcount) {
					found = b;
				}
			}
			if (T.type == Token.VARIABLE) {
				if (found != -1) {
					ChangeTerms.setElementAt(new Term((Term) NewTerms
							.elementAt(found)), a);
				}
				// else System.out.println("VAR NOT FOUND");
			} else if (T.type == Token.FUNCTION) {
				T.fun.Termlist = SkolomizeTerms(T.fun.Termlist, OrgTerm,
						NewTerms);
			}
		}
		return ChangeTerms;
	}

	/**
	 * Description of the Method
	 * 
	 * @param S
	 *            Description of Parameter
	 * @param Termlist
	 *            Description of Parameter
	 * @param NewTermlist
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public Sentence Skolomize(Sentence S, Vector Termlist, Vector NewTermlist) {
		if (S.type == Token.ATOMICSENT) {
			S.Atom.Termlist = SkolomizeTerms(S.Atom.Termlist, Termlist,
					NewTermlist);
		}
		if (S.type == Token.PARENSENT) {
			S.Lsent = Skolomize(S.Lsent, Termlist, NewTermlist);
		}
		if (S.type == Token.CONNECTSENT) {
			S.Lsent = Skolomize(S.Lsent, Termlist, NewTermlist);
			S.Rsent = Skolomize(S.Rsent, Termlist, NewTermlist);
		}
		if (S.type == Token.QUALIFIEDSENT) {
			Vector hold = S.VarList;
			if (S.Qualifier.equals("FORALL")) {
				// add
				for (int a = 0; a < hold.size(); a++) {
					Termlist.addElement(hold.elementAt(a));
					NewTermlist.addElement(hold.elementAt(a));
				}
				S = Skolomize(S.Lsent, Termlist, NewTermlist);
				// remove FORALL
				// remove
				for (int a = 0; a < hold.size(); a++) {
					Termlist.removeElementAt(Termlist.size() - 1);
					NewTermlist.removeElementAt(NewTermlist.size() - 1);
				}
			} else {
				// add
				for (int a = 0; a < hold.size(); a++) {
					Vector funTermlist = new Vector();
					for (int b = 0; b < NewTermlist.size(); b++) {
						Term T = new Term((Term) NewTermlist.elementAt(b));
						if (T.type != Token.FUNCTION) {
							funTermlist.addElement(T);
						}
					}
					Function F = new Function("#S_" + numSkol, funTermlist);
					numSkol++;
					Term T = new Term(Token.FUNCTION, new Function(F));
					Termlist.addElement(hold.elementAt(a));
					NewTermlist.addElement(T);
				}
				S = Skolomize(S.Lsent, Termlist, NewTermlist);
				// remove EXISTS
				// remove
				for (int a = 0; a < hold.size(); a++) {
					Termlist.removeElementAt(Termlist.size() - 1);
					NewTermlist.removeElementAt(NewTermlist.size() - 1);
				}
			}
		}
		return S;
	}

	/**
	 * Description of the Method
	 * 
	 * @param S
	 *            Description of Parameter
	 * @param T
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public Sentence CanDist(Sentence S, Sentence T) {
		// System.out.println("CAN DIST S=**"+S+"** T=**"+T+"**");
		if (T.type == Token.PARENSENT) {
			T.Lsent = CanDist(T.Lsent, S);
			return T;
		} else if (S.type == Token.PARENSENT) {
			S.Lsent = CanDist(S.Lsent, T);
			return S;
		} else if (S.type == Token.ATOMICSENT && T.type == Token.ATOMICSENT) {
			return new Sentence(S, "OR", T);
		} else if (S.type == Token.ATOMICSENT && T.type == Token.CONNECTSENT) {
			return CanDist(T, S);
		} else if (S.type == Token.CONNECTSENT && T.type == Token.ATOMICSENT) {
			if (S.connector.equals("OR")) {
				return new Sentence(S, "OR", T);
			}
			Sentence V = new Sentence(Token.PARENSENT, new Sentence(CanDist(
					S.Rsent, T)));
			Sentence U = new Sentence(Token.PARENSENT, new Sentence(CanDist(
					S.Lsent, T)));
			// System.out.println(new Sentence (V,"AND",U));
			U = new Sentence(V, "AND", U);
			U = new Sentence(Token.PARENSENT, U);
			return U;
		} else {
			// if (S.type==Token.CONNECTSENT && T.type==Token.CONNECTSENT) {
			if (S.connector.equals("OR")) {
				return new Sentence(S, "OR", T);
			}
			Sentence V = new Sentence(Token.PARENSENT, new Sentence(S.Rsent,
					"OR", T));
			Sentence U = new Sentence(Token.PARENSENT, new Sentence(CanDist(
					S.Lsent, T)));
			// System.out.println(new Sentence (V,"AND",U));
			U = new Sentence(Token.PARENSENT, new Sentence(V, "AND", U));
			return U;
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param S
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public String Insidetype(Sentence S) {
		if (S.type == Token.ATOMICSENT) {
			return "ATOMICSENT";
		}
		if (S.type == Token.PARENSENT) {
			return Insidetype(S.Lsent);
		}
		if (S.type == Token.CONNECTSENT) {
			return S.connector;
		} else {
			return "ERROR";
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param S
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public boolean Dist(Sentence S) {
		if (S.type == Token.ERROR) {
			return true;
		} else if (S.type == Token.ATOMICSENT) {
			return true;
		} else if (S.type == Token.PARENSENT) {
			if (S.Lsent.type == Token.PARENSENT) {
				S.Lsent = new Sentence(S.Lsent.Lsent);
				return false;
			} else {
				return Dist(S.Lsent);
			}
		} else if (S.type == Token.CONNECTSENT) {
			if (!Dist(S.Lsent)) {
				return false;
			}
			if (!Dist(S.Rsent)) {
				return false;
			}
			if (Insidetype(S.Lsent).equals("ATOMICSENT")
					&& Insidetype(S.Rsent).equals("ATOMICSENT")) {
				return true;
			}
			if (Insidetype(S.Lsent).equals(S.connector)
					&& Insidetype(S.Rsent).equals(S.connector)) {
				return true;
			}
			if (Insidetype(S.Rsent).equals("AND") && S.connector.equals("OR")) {
				// System.out.println("Dist to Right");
				S.Lsent = new Sentence(CanDist(S.Rsent, S.Lsent));
				S.type = Token.PARENSENT;
				return false;
			} else if (Insidetype(S.Lsent).equals("AND")
					&& S.connector.equals("OR")) {
				// System.out.println("Dist to Left"+S);
				S.Lsent = new Sentence(CanDist(S.Lsent, S.Rsent));
				S.type = Token.PARENSENT;
				return false;
			}
			return true;
		}
		return true;
	}

	/**
	 * Description of the Method
	 * 
	 * @param S
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public Sentence DistOverAnd(Sentence S) {
		boolean done = false;
		while (!done) {
			done = Dist(S);
		}
		return S;
	}

	/**
	 * Description of the Method
	 * 
	 * @param S
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public boolean Flatter(Sentence S) {
		if (S.type == Token.ERROR) {
			return true;
		}
		if (S.type == Token.ATOMICSENT) {
			return true;
		}
		if (S.type == Token.PARENSENT) {
			while (S.Lsent.type == Token.PARENSENT) {
				S.Lsent = new Sentence(S.Lsent.Lsent);
			}
			return Flatter(S.Lsent);
		}
		if (S.type == Token.CONNECTSENT) {
			if (!Flatter(S.Lsent) || !Flatter(S.Rsent)) {
				return false;
			}
			if (S.Lsent.type == Token.PARENSENT) {
				if (Insidetype(S.Lsent).equals(S.connector)
						|| Insidetype(S.Lsent).equals("ATOMICSENT")) {
					S.Lsent = new Sentence(S.Lsent.Lsent);
					return false;
				}
				if (S.Lsent.type == Token.PARENSENT) {
					while (S.Lsent.Lsent.type == Token.PARENSENT) {
						S.Lsent.Lsent = new Sentence(S.Lsent.Lsent.Lsent);
					}
				}
			}
			if (S.Rsent.type == Token.PARENSENT) {
				if (Insidetype(S.Rsent).equals(S.connector)
						|| Insidetype(S.Rsent).equals("ATOMICSENT")) {
					S.Rsent = new Sentence(S.Rsent.Lsent);
					return false;
				}
				if (S.Rsent.type == Token.PARENSENT) {
					while (S.Rsent.Lsent.type == Token.PARENSENT) {
						S.Rsent.Lsent = new Sentence(S.Rsent.Lsent.Lsent);
					}
				}
			}
			if (S.Rsent.type == Token.CONNECTSENT) {
				Sentence V = S.Rsent;
				S.Lsent = new Sentence(S.Lsent, S.connector, V.Lsent);
				S.Rsent = V.Rsent;
				return false;
			}
			return true;
		}
		return true;
	}

	/**
	 * Description of the Method
	 * 
	 * @param S
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public Sentence Flatten(Sentence S) {
		boolean done = false;
		while (!done) {
			done = Flatter(S);
		}
		if (S.type == Token.PARENSENT) {
			// must be ATOMICSENT or CONNECTSENT
			S.type = S.Lsent.type;
			S.Rsent = S.Lsent.Rsent;
			S.connector = S.Lsent.connector;
			S.Atom = S.Lsent.Atom;
			S.Lsent = S.Lsent.Lsent;
		}
		return S;
	}

	/**
	 * Description of the Method
	 * 
	 * @param S
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public Sentence PushDownNeg(Sentence S) {
		if (S.notted) {
			if (S.type == Token.ATOMICSENT) {
				S.Atom.notted = !S.Atom.notted;
				S.notted = false;
			} else if (S.type == Token.CONNECTSENT) {
				S.notted = false;
				if (S.connector.equals("OR")) {
					S.connector = "AND";
				} else if (S.connector.equals("AND")) {
					S.connector = "OR";
				}
				S.Lsent.notted = !S.Lsent.notted;
				S.Rsent.notted = !S.Rsent.notted;
			} else if (S.type == Token.QUALIFIEDSENT) {
				S.notted = false;
				if (S.Qualifier.equals("FORALL")) {
					S.Qualifier = "EXIST";
				} else {
					S.Qualifier = "FORALL";
				}
				S.Lsent.notted = !S.Lsent.notted;
			} else if (S.type == Token.PARENSENT) {
				S.notted = false;
				S.Lsent.notted = !S.Lsent.notted;
			}
		}
		// PUSH DOWN INTO SUB SENTENCE
		if (S.type == Token.CONNECTSENT) {
			S.Lsent = PushDownNeg(S.Lsent);
			S.Rsent = PushDownNeg(S.Rsent);
		} else if (S.type == Token.QUALIFIEDSENT || S.type == Token.PARENSENT) {
			S.Lsent = PushDownNeg(S.Lsent);
		}
		return S;
	}

	/**
	 * Description of the Method
	 * 
	 * @param S
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public Sentence RemoveImp(Sentence S) {
		if (S.type == Token.ATOMICSENT) {
			;
		} else if (S.type == Token.PARENSENT || S.type == Token.QUALIFIEDSENT) {
			S.Lsent = RemoveImp(S.Lsent);
		} else if (S.type == Token.CONNECTSENT) {
			if (S.connector.equals("IFF")) {
				Sentence Temp1;
				Sentence Temp2;
				Temp1 = new Sentence(S.Lsent, "IMP", S.Rsent);
				Temp2 = new Sentence(S.Rsent, "IMP", S.Lsent);
				S.Lsent = new Sentence(Token.PARENSENT, Temp1);
				S.Rsent = new Sentence(Token.PARENSENT, Temp2);
				S.connector = "AND";
				S.Rsent = RemoveImp(S.Rsent);
				S.Lsent = RemoveImp(S.Lsent);
			} else if (S.connector.equals("IMP")) {
				S.Lsent.not();
				S.connector = "OR";
			}
		}
		return S;
	}

	/**
	 * Description of the Method
	 * 
	 * @param S
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public Sentence StandardVar(Sentence S) {
		if (S.type == Token.QUALIFIEDSENT) {
			DeclareVars(S.VarList);
			UpdateVarList(S.VarList);
			S.Lsent = StandardVar(S.Lsent);
			UndelcareVars(S.VarList);
		} else if (S.type == Token.PARENSENT || S.type == Token.CONNECTSENT) {
			S.Lsent = StandardVar(S.Lsent);
			if (S.type == Token.CONNECTSENT) {
				S.Rsent = StandardVar(S.Rsent);
			}
		} else if (S.type == Token.ATOMICSENT) {
			UpdateVarList(S.Atom.Termlist);
		}
		return S;
	}

	/**
	 * Description of the Method
	 * 
	 * @param VarList
	 *            Description of Parameter
	 */
	public void DeclareVars(Vector VarList) {
		for (int a = 0; a < VarList.size(); a++) {
			// for each Var declared
			Term T = (Term) VarList.elementAt(a);
			String Vname = T.val;
			int found = -1;
			for (int b = 0; b < Varnames.size(); b++) {
				// find match name or found=-1
				String name = (String) Varnames.elementAt(b);
				if (name.equals(Vname)) {
					found = b;
				}
			}
			int nextnum = 0;
			if (found == -1) {
				// not found add new string and set next to 1
				Varnames.addElement(Vname);
				Varnextnumber.addElement(new Integer(nextnum + 1));
			} else {
				// found increment nextnum
				Integer I = (Integer) Varnextnumber.elementAt(found);
				nextnum = I.intValue();
				I = new Integer(nextnum + 1);
				Varnextnumber.setElementAt(I, found);
			}
			BoundVars.addElement(Vname);
			// put new var on stack
			BoundVarsnumber.addElement(new Integer(nextnum));
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param VarList
	 *            Description of Parameter
	 */
	public void UpdateVarList(Vector VarList) {
		for (int a = 0; a < VarList.size(); a++) {
			// for each Var declared
			Term T = (Term) VarList.elementAt(a);
			if (T.type == Token.VARIABLE) {
				String Vname = T.val;
				int found = -1;
				for (int b = 0; b < BoundVars.size(); b++) {
					// find match name in BOUNDVAR
					String name = (String) BoundVars.elementAt(b);
					if (name.equals(Vname)) {
						found = b;
					}
				}
				int nextnum = Term.UNBOUND;
				if (found != -1) {
					Integer I = (Integer) BoundVarsnumber.elementAt(found);
					nextnum = I.intValue();
				}
				T.varcount = nextnum;
				VarList.setElementAt(T, a);
			} else if (T.type == Token.FUNCTION) {
				UpdateVarList(T.fun.Termlist);
				VarList.setElementAt(T, a);
			}
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param VarList
	 *            Description of Parameter
	 */
	public void UndelcareVars(Vector VarList) {
		int len = VarList.size();
		for (int a = 0; a < len; a++) {
			int pos = BoundVars.size() - 1;
			BoundVars.removeElementAt(pos);
			BoundVarsnumber.removeElementAt(pos);
		}
	}
}
