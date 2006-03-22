package aj.proof;

import java.util.Vector;

/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    August 29, 2000 
 */
public class Tokens {
	Vector allTokens = new Vector();


	/**
	 *  Constructor for the Tokens object 
	 *
	 *@param  s  Description of Parameter 
	 */
	Tokens(String s) {
		s = s.trim();
		allTokens.removeAllElements();
		while (s.length() > 0) {
			if (s.toUpperCase().startsWith("ASSERT")) {
				s = s.substring(6, s.length()).trim();
				if (s.charAt(s.length() - 1) == '.') {
					s = s.substring(0, s.length() - 1).trim();
				}
				allTokens.addElement(new Token(Token.ASSERT, "ASSERT"));
			}
			else if (s.toUpperCase().startsWith("QUERY")) {
				s = s.substring(5, s.length()).trim();
				if (s.charAt(s.length() - 1) == '?') {
					s = s.substring(0, s.length() - 1).trim();
				}
				allTokens.addElement(new Token(Token.QUERY, "QUERY"));
			}
			else if (s.toUpperCase().startsWith("FORALL") || s.startsWith("-V")) {
				if (s.startsWith("-V")) {
					s = s.substring(2).trim();
				}
				else {
					s = s.substring(6).trim();
				}
				allTokens.addElement(new Token(Token.FORALL, "FORALL"));
			}
			else if (s.toUpperCase().startsWith("EXISTS") || s.startsWith("-E")) {
				if (s.startsWith("-E")) {
					s = s.substring(2).trim();
				}
				else {
					s = s.substring(6).trim();
				}
				allTokens.addElement(new Token(Token.EXISTS, "EXISTS"));
			}
			else if (s.toUpperCase().startsWith("OR") || s.startsWith("|") || s.startsWith("+")) {
				if (s.startsWith("+")) {
					s = s.substring(1).trim();
				}
				else if (s.startsWith("||")) {
					s = s.substring(2).trim();
				}
				else if (s.startsWith("|")) {
					s = s.substring(1).trim();
				}
				else {
					s = s.substring(2).trim();
				}
				allTokens.addElement(new Token(Token.CONNECTIVE, "OR"));
			}
			else if (s.toUpperCase().startsWith("AND") || s.startsWith("*") || s.startsWith("&")) {
				if (s.startsWith("*")) {
					s = s.substring(1).trim();
				}
				else if (s.startsWith("&&")) {
					s = s.substring(2).trim();
				}
				else if (s.startsWith("&")) {
					s = s.substring(1).trim();
				}
				else {
					s = s.substring(3).trim();
				}
				allTokens.addElement(new Token(Token.CONNECTIVE, "AND"));
			}
			else if (s.toUpperCase().startsWith("IFF") || s.startsWith("<=>")) {
				s = s.substring(3).trim();
				allTokens.addElement(new Token(Token.CONNECTIVE, "IFF"));
			}
			else if (s.toUpperCase().startsWith("IMP") || s.startsWith("==>") || s.startsWith("-->") || s.startsWith("->") || s.startsWith("=>")) {
				if (s.startsWith("=>") || s.startsWith("->")) {
					s = s.substring(2).trim();
				}
				else {
					s = s.substring(3).trim();
				}
				allTokens.addElement(new Token(Token.CONNECTIVE, "IMP"));
			}
			else if (s.startsWith("(")) {
				s = s.substring(1).trim();
				allTokens.addElement(new Token(Token.LPAREN, "LPAREN"));
			}
			else if (s.startsWith(")")) {
				s = s.substring(1).trim();
				allTokens.addElement(new Token(Token.RPAREN, "RPAREN"));
			}
			else if (s.startsWith("[")) {
				s = s.substring(1).trim();
				allTokens.addElement(new Token(Token.LBRACK, "LBRACK"));
			}
			else if (s.startsWith("]")) {
				s = s.substring(1).trim();
				allTokens.addElement(new Token(Token.RBRACK, "RBRACK"));
			}
			else if (s.startsWith("=")) {
				while (s.startsWith("=")) {
					s = s.substring(1).trim();
				}
				allTokens.addElement(new Token(Token.EQUALS, "EQUALS"));
			}
			else if (s.startsWith(":") || s.startsWith("|")) {
				while (s.startsWith(":") || s.startsWith("|")) {
					s = s.substring(1).trim();
				}
				allTokens.addElement(new Token(Token.COLON, "COLON"));
			}
			else if (s.startsWith(",")) {
				s = s.substring(1).trim();
				allTokens.addElement(new Token(Token.COMA, "COMA"));
			}
			else if (s.startsWith("~")) {
				s = s.substring(1).trim();
				allTokens.addElement(new Token(Token.NOT, "NOT"));
			}
			else if (s.startsWith("!")) {
				s = s.substring(1).trim();
				allTokens.addElement(new Token(Token.NOT, "NOT"));
			}
			else if (s.toUpperCase().startsWith("NOT")) {
				s = s.substring(3).trim();
				allTokens.addElement(new Token(Token.NOT, "NOT"));
			}
			else if (s.charAt(0) == '#') {
				if (s.indexOf("(") != -1) {
					String t = s.substring(0, s.indexOf("(")).trim();
					s = s.substring(s.indexOf("(")).trim();
					allTokens.addElement(new Token(Token.FUNCTION, t));
				}
				else {
					s = "";
					allTokens.addElement(new Token(Token.ERROR, "ERROR"));
				}
			}
			else if (s.charAt(0) == '?') {
				if (s.indexOf("(") != -1) {
					String t = s.substring(0, s.indexOf("(")).trim();
					s = s.substring(s.indexOf("(")).trim();
					allTokens.addElement(new Token(Token.PREDICATE, t));
				}
				else {
					s = "";
					allTokens.addElement(new Token(Token.ERROR, "ERROR"));
				}
			}
			else if (s.charAt(0) >= 'A' && s.charAt(0) <= 'Z') {
				int a;
				String t = "";
				for (a=0; a < s.length(); a++) {
					if (!numberOrCharacter(s.charAt(a))) {
						break;
					}
				}
				if (a == s.length()) {
					t = s;
					s = "";
				}
				else {
					t = s.substring(0, a).trim();
					s = s.substring(a).trim();
				}
				allTokens.addElement(new Token(Token.GROUND, t));
			}
			else if (s.charAt(0) >= 'a' && s.charAt(0) <= 'z') {
				int a;
				String t;
				for ( a=0; a < s.length(); a++) {
					if (!numberOrCharacter(s.charAt(a))) {
						break;
					}
				}
				if (a == s.length()) {
					t = s;
					s = "";
				}
				else {
					t = s.substring(0, a).trim();
					s = s.substring(a).trim();
				}
				allTokens.addElement(new Token(Token.VARIABLE, t));
			}
			else {
				s = "";
				allTokens.addElement(new Token(Token.ERROR, "ERROR"));
			}
			s = s.trim();
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public int currType() {
		if (allTokens.size() == 0) {
			return Token.ERROR;
		}
		Token t = (Token) allTokens.elementAt(0);
		return t.type;
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public String currVal() {
		if (allTokens.size() == 0) {
			return "ERROR";
		}
		Token t = (Token) allTokens.elementAt(0);
		return t.val;
	}


	/**
	 *  Description of the Method 
	 */
	public void eat() {
		if (allTokens.size() > 0) {
			allTokens.removeElementAt(0);
		}
	}


	/**
	 *  Description of the Method 
	 *
	 *@return    Description of the Returned Value 
	 */
	public String toString() {
		String t = "";
		for (int a=0; a < allTokens.size(); a++) {
			t += allTokens.elementAt(a).toString() + "\n";
		}
		return t;
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  a  Description of Parameter 
	 *@return    Description of the Returned Value 
	 */
	public boolean numberOrCharacter(char a) {
		return ((a >= 'a' && a <= 'z') || (a >= 'A' && a <= 'Z') || (a >= '0' && a <= '9'));
	}


	/**
	 *  Description of the Method 
	 *
	 *@param  argv  Description of Parameter 
	 */
	public static void main(String argv[]) {
		String text = "ASSERT [FORALL m : ?Man(m) => ?Mortal(m) ].";
		Tokens t = new Tokens(text);
		System.out.println(text);
		System.out.println(t);
	}

}
