package aj.io;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
/**
 *  Encrypts data based on key. Simple XOR encryption. 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class Crypt {
	
	static int MAXCHUNCK = 100000;
	
	public Crypt (String argv[]) {
		FileInputStream FI;
		FileOutputStream FO;
		byte b[];
		if (argv.length < 1 || argv.length > 3) {
			System.out.println ("Format Crypt <key> [<sfile> [<dfile>]] ");
			System.out.println ("<sfile> old: reads and writes to same file");
			System.out.println ("<sfile> <dfile>: reads and writes any size file");
			System.exit (0);
		}
		if (argv.length == 3) {
			if (argv[1].equals (argv[2])) {
				System.out.println ("Source and Destination cannot be the same:  see java Crypt");
			}
		}
		byte[]Key = new byte[argv[0].length()];
		Key = argv[0].getBytes();
		//This line for JDK1.1
		try {
			if (argv.length == 1) {
				System.out.write (encode (Key, read (System.in)));
			}
			else if (argv.length == 2) {
				FI = new FileInputStream (argv[1]);
				if (FI.available() > MAXCHUNCK) {
					System.out.println ("File too large, for help use: java Crypt");
					System.exit (0);
				}
				b = encode (Key, read (FI));
				new FileOutputStream (argv[1]).write (b);
			}
			else if (argv.length == 3) {
				FI = new FileInputStream (argv[1]);
				FO = new FileOutputStream (argv[2]);
				while (FI.available() > MAXCHUNCK) {
					b = encode (Key, read (FI, MAXCHUNCK));
					FO.write (b);
				}
				b = encode (Key, read (FI));
				FO.write (b);
				FO.close();
			}
		}
		catch (IOException E) {
			System.out.println ("Error in Crypt");
			System.exit (0);
		}
	}
	
	public static void main (String argv[])throws IOException {
		new Crypt (argv);
	}
	
	private static byte[]encode (byte Key[], byte b[]) {
		for (int a = 0; a < b.length; a++) {
			b[a] = (byte) (( ~b[a] | Key[a % Key.length]) & (b[a] | ~Key[a % Key.length]));
		}
		return b;
	}
	
	private static byte[]read (InputStream I, int size)throws IOException {
		byte b[];
		b = new byte[size];
		I.read (b);
		return b;
	}
	
	private static byte[]read (InputStream I)throws IOException {
		byte b[];
		b = new byte[I.available()];
		I.read (b);
		return b;
	}
}

