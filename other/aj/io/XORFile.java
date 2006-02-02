package aj.io;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 *@author     judda 
 *@created    April 12, 2000 
 */
public class XORFile {
	
	public static void main (String s[])throws IOException {
		if (s.length != 3) {
			System.out.println ("format: java XORFile <source1> <source2> <dest>");
			System.out.println ("assums source fils are same length.  Read all files at once.");
			System.out.println ("modify - any length source file.  Dest file = max length source1 source2");
			System.out.println ("read chunck at a time.  Great for one time pad perfect encryption.");
			System.exit (0);
		}
		System.out.println ("XORFile start");
		FileInputStream f = new FileInputStream (s[0]);
		FileInputStream g = new FileInputStream (s[1]);
		byte b[] = new byte[f.available()];
		byte c[] = new byte[g.available()];
		f.read (b);
		g.read (c);
		int max = Math.max (b.length, c.length);
		byte d[] = new byte[max];
		int a;
		for (a = 0; a < max; a++) {
			d[a] = (byte) ((b[a % b.length] | ~c[a % c.length]) & ( ~b[a % b.length] | c[a % c.length]));
		}
		FileOutputStream h = new FileOutputStream (s[2]);
		h.write (d);
		System.out.println ("XORFile end");
	}
}

