package aj.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * This is my chop up file program. It takes a large file and break it up into
 * smaller ones. Then it the files can be reconnected into the original. Usefull
 * for putting large files on a floppy disk.
 * 
 * @author judda
 * @created April 12, 2000
 */
public class Span {
	int chunkSize, totalSize;

	String Source, Dest;

	byte buff[];

	/**
	 * Constructor for the Span object
	 * 
	 * @param s
	 *            Description of Parameter
	 * @param d
	 *            Description of Parameter
	 * @param c
	 *            Description of Parameter
	 */
	public Span(String s, String d, String c) {
		Source = s;
		Dest = d;
		try {
			chunkSize = Integer.parseInt(c);
		} catch (NumberFormatException E) {
			error("ban chunk size");
		}
		totalSize = -1;
	}

	/**
	 * Constructor for the Span object
	 * 
	 * @param s
	 *            Description of Parameter
	 */
	public Span(String s) {
		readHeader(s);
	}

	/**
	 * Description of the Method
	 * 
	 * @param Fin
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public FileInputStream openForRead(String Fin) {
		try {
			return new FileInputStream(Fin);
		} catch (IOException IOE1) {
			error("Cannot Open input File");
		}
		return null;
	}

	/**
	 * Description of the Method
	 * 
	 * @param Fout
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public FileOutputStream openForWrite(String Fout) {
		try {
			return new FileOutputStream(Fout);
		} catch (IOException IOE1) {
			error("Cannot Open output File");
		}
		return null;
	}

	/**
	 * Description of the Method
	 * 
	 * @param Fin
	 *            Description of Parameter
	 */
	public void readHeader(String Fin) {
		try {
			BufferedReader BR = new BufferedReader(new FileReader(Fin));
			Dest = BR.readLine();
			Source = BR.readLine();
			chunkSize = Integer.parseInt(BR.readLine());
			totalSize = Integer.parseInt(BR.readLine());
		} catch (IOException IOE1) {
			error("bad unSpan index file");
		} catch (NumberFormatException NFE1) {
			error("bad unSpan index file number error");
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param Fout
	 *            Description of Parameter
	 */
	public void writeHeader(String Fout) {
		FileOutputStream OUT = openForWrite(Fout);
		try {
			String msg = Source + "\n" + Dest + "\n" + chunkSize + "\n"
					+ totalSize + "\n";
			OUT.write(msg.getBytes());
			OUT.flush();
			OUT.close();
		} catch (IOException IOE3) {
			error("error writing to Spans Header");
		}
	}

	/**
	 * Description of the Method
	 */
	public void divide() {
		FileInputStream IN = null;
		FileOutputStream OUT = null;
		IN = openForRead(Source);
		byte buff[];
		int count = 0;
		System.out
				.println("SPANNING: " + Source + " <chunk=" + chunkSize + ">");
		try {
			totalSize = IN.available();
			writeHeader(Dest);
			while (IN.available() > 0) {
				if (chunkSize > IN.available()) {
					chunkSize = IN.available();
				}
				buff = new byte[chunkSize];
				IN.read(buff);
				OUT = openForWrite(Dest + "" + (++count));
				OUT.write(buff);
				OUT.flush();
				OUT.close();
			}
		} catch (IOException IOE2) {
			error("Cannot read or write file");
		}
	}

	/**
	 * Description of the Method
	 */
	public void collect() {
		FileInputStream IN = null;
		FileOutputStream OUT = null;
		System.out.println("Unspanning: " + Dest);
		OUT = openForWrite(Dest);
		int total = 0;
		int count = 0;
		try {
			do {
				IN = openForRead(Source + (++count));
				total += IN.available();
				buff = new byte[IN.available()];
				IN.read(buff);
				OUT.write(buff);
				OUT.flush();
				IN.close();
			} while (total < totalSize);
			OUT.close();
		} catch (IOException IOE5) {
			error("Cannot read or write to file");
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param S
	 *            Description of Parameter
	 */
	public void error(String S) {
		System.out.println("myERROR: " + S);
		System.exit(0);
	}

	/**
	 * Description of the Method
	 * 
	 * @param argv
	 *            Description of Parameter
	 */
	public static void main(String argv[]) {
		if (argv.length > 3 || argv.length < 1) {
			System.out.println("Default chunk = 1.4 megs");
			System.out
					.println("FORMAT: java Span <sourcefile> <destfile> [chunkSize]");
			System.out.println("FORMAT: java Span <span_file>");
			System.exit(0);
		}
		if (argv.length == 2) {
			Span S = new Span(argv[0], argv[1], "" + 1400000);
			S.divide();
		} else if (argv.length == 3) {
			Span S = new Span(argv[0], argv[1], argv[2]);
			S.divide();
		} else {
			Span S = new Span(argv[0]);
			S.collect();
		}
	}
}
