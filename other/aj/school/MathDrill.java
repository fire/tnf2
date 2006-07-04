package aj.school;

import java.io.IOException;

public class MathDrill {
	static int Col = 10;

	int pnum = 120;

	int constnum = 2;

	int minnum = 0, maxnum = 9;

	boolean fixbot = false, fixtop = false, pos = false, fixany = false;

	String sign = "+";

	public static void main(String s[]) throws IOException {
		if (s.length == 0) {
			System.out.println("FORMAT: java aj.school.MathDrill [options] ");
			System.out
					.println("OPERATION (+,-,*,/)  = --,-+,-*,-/                (ie -*  default -+)");
			System.out
					.println("FIXED NUMBER         = -fixtop#,-fixbot#,-fixany# (ie -fixany2)");
			System.out.println("REQUIRE POSITIVE     = -positive ");
			System.out.println("WHOLE NUMBER ANSWERS = -positive ");
			System.out
					.println("NUMBER OF PROBLEMS   = -prob#       (ie -prob45 ) default -prob120");
			System.out
					.println("LARGEST NUMBER       = -max#        (ie -max9   ) default -max9   ");
			System.out
					.println("SMALLEST NUMBER      = -min#        (ie -min0   ) default -min0   ");
			// <-max#> <-min#> <-{+,-,*,/}> <-fixtop/fixbot/fixany#> <-prob#>
			// <-POSITIVE>");
			System.exit(0);
		}
		MathDrill mm = new MathDrill(s);
		mm.start();
	}

	public MathDrill(String s[]) {
		fixtop = false;
		fixbot = false;
		// <-max#> <-min#> <-{+,-,*,/}> <-fixtop/fixbot#> <-maxans#>");
		int a;
		for (a = 0; a < s.length; a++) {
			if (s[a].toUpperCase().startsWith("-FIXBOT")) {
				fixbot = true;
				constnum = Integer.parseInt(s[a].substring(7));
			}
			if (s[a].toUpperCase().startsWith("-FIXANY")) {
				fixany = true;
				constnum = Integer.parseInt(s[a].substring(7));
			} else if (s[a].toUpperCase().startsWith("-FIXTOP")) {
				fixtop = true;
				constnum = Integer.parseInt(s[a].substring(7));
			} else if (s[a].toUpperCase().startsWith("-PROB")) {
				pnum = Integer.parseInt(s[a].substring(5));
			} else if (s[a].toUpperCase().startsWith("-/")) {
				sign = "/";
			} else if (s[a].toUpperCase().startsWith("-POSITIVE")) {
				pos = true;
			} else if (s[a].toUpperCase().startsWith("-*")) {
				sign = "*";
			} else if (s[a].toUpperCase().startsWith("--")) {
				sign = "-";
			} else if (s[a].toUpperCase().startsWith("-+")) {
				sign = "+";
			} else if (s[a].toUpperCase().startsWith("-MIN")) {
				minnum = Integer.parseInt(s[a].substring(4));
			} else if (s[a].toUpperCase().startsWith("-MAX")) {
				maxnum = Integer.parseInt(s[a].substring(4));
			}
		}
	}

	public void start() {
		String Line1 = "", Line2 = "", Line3 = "";

		int a;
		for (a = 0; a < pnum; a++) {
			int T, B;
			T = (int) (Math.random() * (maxnum - minnum) + 1 + minnum);
			B = (int) (Math.random() * (maxnum - minnum) + 1 + minnum);

			if (fixany) {
				if (Math.random() * 2 > 1) {
					fixtop = true;
					fixbot = false;
				} else {
					fixtop = false;
					fixbot = true;
				}
			}
			if (fixtop) {
				T = constnum;
			} else if (fixbot) {
				B = constnum;
			}
			if (sign.equals("+")) {
			} else if (sign.equals("-")) {
				if (T - B < 0 && pos == true) {
					T = B + T;
				}
			} else if (sign.equals("*")) {
			} else if (sign.equals("/")) {
				if (pos == true) {
					T = B * T;
				}
			}

			Line1 += " " + T + "\t";
			Line2 += sign + B + "\t";
			Line3 += "-----\t";
			if (a % Col == Col - 1 || a == pnum - 1) {
				System.out.println(Line1);
				System.out.println(Line2);
				System.out.println(Line3);
				System.out.println();
				System.out.println();
				Line1 = "";
				Line2 = "";
				Line3 = "";
			}
		}
	}
}
