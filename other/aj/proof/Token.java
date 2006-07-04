package aj.proof;

/**
 * Description of the Class
 * 
 * @author judda
 * @created August 29, 2000
 */
public class Token {

	int type;

	String val;

	static int QUERY = 1, ASSERT = 2, CONNECTIVE = 3, FORALL = 4, EXISTS = 5,
			RBRACK = 6, LBRACK = 7, RPAREN = 8, LPAREN = 9, NOT = 10,
			EQUALS = 11, GROUND = 12, VARIABLE = 13, FUNCTION = 14,
			PREDICATE = 15, ERROR = 18, COMA = 16, COLON = 17;

	static int CONNECTSENT = 1, PARENSENT = 2, ATOMICSENT = 3,
			QUALIFIEDSENT = 4;

	/**
	 * Constructor for the Token object
	 * 
	 * @param t
	 *            Description of Parameter
	 * @param n
	 *            Description of Parameter
	 */
	public Token(int t, String n) {
		type = t;
		val = new String(n);
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Returned Value
	 */
	public String toString() {
		String t;
		switch (type) {
		case 1:
			t = "QUERY";
			break;
		case 2:
			t = "ASSERT";
			break;
		case 3:
			t = "CONNECTIVE";
			break;
		case 4:
			t = "FORALL";
			break;
		case 5:
			t = "EXISTS";
			break;
		case 6:
			t = "RBRACK";
			break;
		case 7:
			t = "LBRACK";
			break;
		case 8:
			t = "RPAREN";
			break;
		case 9:
			t = "LPAREN";
			break;
		case 10:
			t = "NOT";
			break;
		case 11:
			t = "EQUALS";
			break;
		case 12:
			t = "GROUND";
			break;
		case 13:
			t = "VARIABLE";
			break;
		case 14:
			t = "FUNCTION";
			break;
		case 15:
			t = "PREDICATE";
			break;
		case 16:
			t = "COMA";
			break;
		case 17:
			t = "COLON";
			break;
		default:
			t = "ERROR";
			break;
		}
		return ("Token type=" + t + " val=" + val);
	}
}
