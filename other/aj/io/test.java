package aj.io;

public class test {
	public static void main(String s[]) {
		String code = "01";
		if (s.length > 0)
			code = s[0];
		for (int a = 1; a < s.length; a++) {
			System.out.println("Encode " + s[a] + " code =" + code);
			String h = Encode.encodeString(s[a], code);
			System.out.println(h);
			System.out.println("Dencode " + h);
			System.out.println(Encode.decodeString(h));
		}
	}
}
