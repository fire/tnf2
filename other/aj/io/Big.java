package aj.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Makes a file of anylength.
 * 
 * @author judda
 * @created April 12, 2000
 */
public class Big {
	char fill = '*';

	public Big() {
	}

	public Big(char c) {
		fill = c;
	}

	/**
	 * Actually makes the file. In 1meg chuncks.
	 */
	public void make(OutputStream O, int z) {
		byte b[] = {};
		int max = 100000;
		try {
			b = new byte[max];
			if (fill == '*') {
				for (int a = 0; a < max; a++) {
					b[a] = (byte) (Math.random() * 256);
				}
			} else if (fill == '+') {
				for (int a = 0; a < max; a++) {
					b[a] = (byte) (a);
				}
			} else {
				for (int a = 0; a < max; a++) {
					b[a] = (byte) fill;
				}
			}
			while (z > max) {
				z -= max;
				O.write(b);
			}
			b = new byte[z];
			if (fill == '*') {
				for (int a = 0; a < z; a++) {
					b[a] = (byte) (Math.random() * 256);
				}
			} else if (fill == '+') {
				for (int a = 0; a < z; a++) {
					b[a] = (byte) (a);
				}
			} else {
				for (int a = 0; a < z; a++) {
					b[a] = (byte) fill;
				}
			}
			O.write(b);
			O.close();
		} catch (IOException EE) {
			System.out.println("MyError: writing error");
		}
	}

	public static void main(String s[]) {
		if (s.length < 2 || s.length > 3) {
			System.out
					.println(" makes a file of specified size filled with random chars unless char is defined then file will contian only char! ");
			System.out.println("Format: java big <filename> <size> [char]");
			System.exit(0);
		}
		try {
			Big B;
			if (s.length == 2) {
				B = new Big();
			} else {
				B = new Big(s[2].charAt(0));
			}
			B.make(new FileOutputStream(s[0]), Integer.parseInt(s[1]));
		} catch (NumberFormatException E) {
			System.out.println("FILE SIZE must be a number");
		} catch (IOException EE) {
			System.out.println("FILE IO ERROR");
		}
	}
}
