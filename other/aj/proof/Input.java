package aj.proof;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * Description of the Class
 * 
 * @author judda
 * @created August 29, 2000
 */
public class Input {
	int type;

	Sentence S;

	/**
	 * Constructor for the Input object
	 * 
	 * @param type
	 *            Description of Parameter
	 * @param s
	 *            Description of Parameter
	 */
	public Input(int type, Sentence s) {
		this.type = type;
		S = s;
	}

	/**
	 * Constructor for the Input object
	 * 
	 * @param I
	 *            Description of Parameter
	 */
	public Input(Input I) {
		type = I.type;
		S = new Sentence(I.S);
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Returned Value
	 */
	public String toString() {
		String t;
		if (type == Token.ASSERT) {
			t = "ASSERT " + S.toString() + ".";
		} else if (type == Token.QUERY) {
			t = "QUERY " + S.toString() + "?";
		} else {
			t = "Input-ERROR";
		}
		return t;
	}

	/**
	 * Description of the Method
	 * 
	 * @param S
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static Input Parse(String S) {
		return Parse(new Tokens(S));
	}

	/**
	 * Description of the Method
	 * 
	 * @param T
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static Input Parse(Tokens T) {
		int type;
		Sentence S = null;
		if (T.currType() == Token.ASSERT) {
			type = Token.ASSERT;
		} else if (T.currType() == Token.QUERY) {
			type = Token.QUERY;
		} else {
			type = Token.ERROR;
		}
		T.eat();
		if (type != Token.ERROR) {
			S = Sentence.Parse(T);
		}
		return new Input(type, S);
	}

	/**
	 * Description of the Method
	 * 
	 * @param argv
	 *            Description of Parameter
	 * @exception IOException
	 *                Description of Exception
	 */
	public static void main(String argv[]) throws IOException {
		Input I;
		String S;
		Normal n = new Normal();
		BufferedReader dstr = new BufferedReader(new InputStreamReader(
				System.in));
		while (1 == 1) {
			System.out.print("PROOF>");
			S = dstr.readLine();
			if (S.toUpperCase().equals("QUIT")) {
				System.exit(0);
			}
			I = Input.Parse(S);
			// I=n.Fix(I);
			if (I.type == Token.QUERY) {
				I.S.notted = !I.S.notted;
			}
			I.S = n.RemoveImp(I.S);
			System.out.println("RMIMP-" + I);
			I.S = n.PushDownNeg(I.S);
			System.out.println("NEGDOW-" + I);
			I.S = n.StandardVar(I.S);
			System.out.println("STDVAR-" + I);
			I.S = n.Skolomize(I.S, new Vector(), new Vector());
			// add second vector of replacement terms
			System.out.println("SKO-" + I);
			I.S = n.QualifiersLeft(I.S);
			// System.out.println("QUALFT-"+I);
			// I.S=n.DistOverAnd(I.S);
			System.out.println("DISAND-" + I);
			I.S = n.Flatten(I.S);
			System.out.println("FLAT-" + I);
		}
	}
}
