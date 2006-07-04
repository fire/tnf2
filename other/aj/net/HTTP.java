package aj.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Description of the Class
 * 
 * @author judda
 * @created August 28, 2000
 */
public class HTTP implements Runnable {

	boolean goodfile = false;

	BufferedReader IN;

	OutputStream OUT;

	String fileName;

	String request;

	String log = "";

	Socket mysock;

	String IMGTEXT = "<IMG ALIGN=absbottom BORDER=0 SRC =\"internal-gopher-text\" ALT=\"[TXT]\"> ";

	String IMGBINARY = "<IMG ALIGN=absbottom BORDER=0 SRC =\"internal-gopher-binary\" ALT=\"[BIN]\"> ";

	String IMGIMAGE = "<IMG ALIGN=absbottom BORDER=0 SRC =\"internal-gopher-image\" ALT=\"[IMG]\"> ";

	String IMGUNKNOWN = "<IMG ALIGN=absbottom BORDER=0 SRC =\"internal-gopher-unknown\" ALT=\"[ ? ]\"> ";

	// index image content misx
	String info[][] = {
			{ ".html", IMGTEXT, "Content-type:  text/html ",
					"HyperText Document" },
			{ ".htm", IMGTEXT, "Content-type:  text/html ",
					"HyperText Document" },
			{ ".java", IMGTEXT, "Content-type:  text/plain", "Java Source Text" },
			{ ".txt", IMGTEXT, "Content-type:  text/plain", "Text File" },
			{ ".log", IMGTEXT, "Content-type:  text/plain", "Log File" },
			{ ".bat", IMGTEXT, "Content-type:  text/plain", "Batch File" },
			{ ".sh", IMGTEXT, "Content-type:  text/plain", "Shell Script" },
			{ ".doc", IMGTEXT, "Content-type:  text/word", "Microsoft Word Doc" },
			{ ".ps", IMGTEXT, "Content-type:  text/postscript",
					"Postscript Document" },
			{ ".pdf", IMGTEXT, "Content-type:  application/pdf", "Acrobat File" },
			{ ".zip", IMGBINARY, "Content-type:  application/zip",
					"Zip Compressed File" },
			{ ".gz", IMGBINARY, "Content-type:  application/zip",
					"Gzip Compressed File" },
			{ ".tar", IMGBINARY, "Content-type:  application/zip",
					"Tape Archive Record" },
			{ ".jar", IMGBINARY, "Content-type:  application/zip",
					"Jave Archive Record" },
			{ ".class", IMGBINARY, "Content-type:  application/class",
					"Java Class File" },
			{ ".wrl", IMGTEXT, "Content-type:  model/vrml", "VRLM World" },
			{ ".wrz", IMGTEXT, "Content-type:  x-world/x-vrml", "VRLM World" },
			{ ".exe", IMGBINARY, "Content-type:  file/exe", "Application File" },
			{ ".dll", IMGBINARY, "Content-type:  file/dll",
					"Dynamic Linked Library" },
			{ ".bmp", IMGIMAGE, "Content-type:  image/x-xbitmap", "Image" },
			{ ".gif", IMGIMAGE, "Content-type:  image/gif", "Image" },
			{ ".png", IMGIMAGE, "Content-type:  image/png", "Image" },
			{ ".jpg", IMGIMAGE, "Content-type:  image/jpeg", "Image" },
			{ ".jpeg", IMGIMAGE, "Content-type:  image/jpeg", "Image" },
			{ "", IMGUNKNOWN, "Content-type:  unknown/unknown", "" }
	// default
	};

	boolean logFile = false, logAccess = false;

	int MAXSENDBUFF = 200000;

	String root;

	/**
	 * Constructor for the HTTP object
	 * 
	 * @param socket
	 *            Description of Parameter
	 * @exception IOException
	 *                Description of Exception
	 */
	public HTTP(Socket socket) throws IOException {
		mysock = socket;
		IN = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		OUT = socket.getOutputStream();
		if (IN == null || OUT == null) {
			throw new IOException();
		}
		readHeader();
		log(socket);
	}

	/**
	 * Gets the Image attribute of the HTTP object
	 * 
	 * @param s
	 *            Description of Parameter
	 * @return The Image value
	 */
	public String getImage(String s) {
		for (int a = 0; a < info.length; a++) {
			if (s.toLowerCase().indexOf(info[a][0]) >= 0) {
				return info[a][1];
			}
		}
		return info[info.length][1];
	}

	/**
	 * Gets the Misc attribute of the HTTP object
	 * 
	 * @param s
	 *            Description of Parameter
	 * @return The Misc value
	 */
	public String getMisc(String s) {
		s = s.toLowerCase();
		for (int a = 0; a < info.length; a++) {
			if (s.indexOf(info[a][0]) >= 0) {
				return info[a][3];
			}
		}
		return info[info.length][3];
	}

	/**
	 * Gets the ContentType attribute of the HTTP object
	 * 
	 * @param s
	 *            Description of Parameter
	 * @return The ContentType value
	 */
	private String getContentType(String s) {
		s = s.toLowerCase();
		for (int a = 0; a < info.length; a++) {
			if (s.indexOf(info[a][0]) >= 0) {
				return info[a][2];
			}
		}
		return info[info.length][2];
		// default
	}

	/**
	 * Description of the Method
	 * 
	 * @exception IOException
	 *                Description of Exception
	 */
	private void readHeader() throws IOException {
		mysock.setSoTimeout(5);
		while (true) {
			String input = IN.readLine();
			if (input == null)
				throw new IOException();
			if (input.toUpperCase().startsWith("GET ")) {
				request = input;
				break;
			}
		}
		request = request.trim();
	}

	/**
	 * Main processing method for the HTTP object
	 */
	public void run() {
		// System.out.println("request="+request);
		while (request.indexOf("%") >= 0) {
			char c = '_';
			try {
				String sub = request.substring(request.indexOf("%") + 1).trim();
				int d1 = Integer.parseInt(sub.substring(0, 1));
				sub = sub.substring(1);
				int d2 = Integer.parseInt(sub.substring(0, 1));
				c = (char) (d1 * 16 + d2);
			} catch (NumberFormatException NFE) {
			}
			request = request.substring(0, request.indexOf("%")) + c
					+ request.substring(request.indexOf("%") + 3);
		}
		if (request.toUpperCase().startsWith("GET ")) {
			fileName = request.substring(request.indexOf(" ")).trim();
			if (fileName.toUpperCase().indexOf(" HTTP") >= 0) {
				fileName = fileName.substring(0,
						fileName.toUpperCase().lastIndexOf(" HTTP")).trim();
			}
		}
		while (fileName.indexOf("\\") >= 0) {
			fileName = fileName.substring(0, fileName.indexOf("\\")) + "/"
					+ fileName.substring(fileName.indexOf("\\") + 1);
		}
		while (fileName.indexOf("//") >= 0) {
			fileName = fileName.substring(0, fileName.indexOf("//")) + "/"
					+ fileName.substring(fileName.indexOf("//") + 2);
		}
		while (fileName.indexOf("../") >= 0) {
			String pre = fileName.substring(0, fileName.indexOf("../"));
			String post = fileName.substring(fileName.indexOf("../") + 3);
			if (pre.endsWith("/")) {
				pre = pre.substring(0, pre.length() - 1);
			}
			if (pre.lastIndexOf("/") >= 0) {
				pre = pre.substring(0, pre.lastIndexOf("/") + 1);
			}
			fileName = pre + post;
		}
		File file = new File(root + fileName);
		if (fileName.indexOf("..") >= 0) {
			fileName = "/";
			file = new File(root + fileName);
		}
		goodfile = true;
		if (file.isDirectory()) {
			sendDirectory();
		} else if (file.exists()) {
			sendFile();
		} else if (fileName.toLowerCase().indexOf("internal-gopher-") >= 0) {
			fileName = fileName.substring(fileName.lastIndexOf("-") + 1).trim()
					+ ".gif";
			sendInternalFile();
		} else if (!file.exists()) {
			sendNotFound("<" + root + fileName + ">");
			goodfile = false;
		}

		if (goodfile) {
			log(root + fileName + " (success!)");
		} else {
			log(root + fileName + " (fail!)");
		}
		log();
		try {
			// if (IN!=null) IN.close();
			// if (OUT!=null) OUT.close();
			if (mysock != null)
				mysock.close();
		} catch (IOException IOE) {
			IOE.printStackTrace();
		}

	}

	/**
	 * Description of the Method
	 * 
	 * @param sock
	 *            Description of Parameter
	 */
	public void log(Socket sock) {
		String host = sock.getInetAddress().getHostName();
		if (logAccess) {
			log += host;
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param file
	 *            Description of Parameter
	 */
	public void log(String file) {
		while (file.indexOf("\\") >= 0) {
			file = file.substring(0, file.indexOf("\\")) + "/"
					+ file.substring(file.indexOf("\\") + 1);
		}
		while (file.indexOf("//") >= 0) {
			file = file.substring(0, file.indexOf("//")) + "/"
					+ file.substring(file.indexOf("//") + 2);
		}
		if (logFile) {
			log = log + (log.equals("") ? "" : " ") + file;
		}
	}

	/**
	 * Description of the Method
	 */
	public void log() {
		log = log + " " + (new java.util.Date()).toString();
		if (logFile || logAccess) {
			System.out.println(log.trim());
		}
	}

	/**
	 * Description of the Method
	 */
	public void sendDirectory() {
		File file = new File(root + fileName);
		String dirList[] = file.list();
		while (fileName.startsWith("/")) {
			fileName = fileName.substring(1);
		}
		while (fileName.startsWith("\\")) {
			fileName = fileName.substring(1);
		}
		String body = "<html><body><h1>Directory of " + fileName
				+ "</h1>\n<pre>";
		body += "<a href=\"../\">Up to higher level directory</a>\n";
		if (dirList != null) {
			for (int a = 0; a < dirList.length; a++) {
				String subFile = root + fileName + dirList[a];
				File subFileFile = new File(subFile);
				if (subFileFile.isDirectory()) {
					dirList[a] += "/";
				}
				String IMG = getImage(subFile);
				if (subFileFile.isDirectory()) {
					IMG = "<IMG ALIGN=absbottom BORDER=0 SRC =\"internal-gopher-menu\" ALT=\"[DIR]\"> ";
				}
				String SIZE = ((int) (subFileFile.length() / 10.240) / 100.0)
						+ "K           ";
				if (subFileFile.length() > 1024 * 1024) {
					SIZE = ((int) (subFileFile.length() / 10.240 / 1024) / 100.0)
							+ "M           ";
				}
				SIZE = SIZE.substring(0, 10);
				String DATE = (new java.util.Date(subFileFile.lastModified()))
						.toString();
				String NAME;
				NAME = dirList[a].substring(0,
						Math.min(dirList[a].length(), 30)).trim();
				NAME += "</a>                                      ";
				NAME = NAME.substring(0, 35);
				String MISC = getMisc(subFile);
				body += "<a href=\"" + dirList[a] + "\">" + IMG + NAME + " "
						+ SIZE + " " + DATE + " " + MISC + "\n";
			}
		}
		body += "</pre></body></html>";

		String header = "HTTP/1.0 200 Document follows\n" + "Date: "
				+ (new java.util.Date().toString()) + "\n" + "Last-modified: "
				+ new java.util.Date(file.lastModified()) + "\n"
				+ getContentType(".html") + "\n" + "Content-length: "
				+ body.getBytes().length + "\n" + "\n";
		try {
			OUT.write((header + body).getBytes());
			OUT.flush();
			Thread.yield();
		} catch (IOException IOE) {
			IOE.printStackTrace();
			goodfile = false;
			// System.out.println("MyError: cannot send requested Directory
			// ("+fileName+")"+ IOE);
		}
	}

	/**
	 * Description of the Method
	 */
	public void sendInternalFile() {
		try {
			Class aClass = getClass();
			InputStream IN = aClass.getResourceAsStream(fileName);
			if (IN == null) {
				// mysock.close();
				goodfile = false;
				return;
			}

			String header = "HTTP/1.0 200 Document follows\n" + "Date: "
					+ (new java.util.Date().toString()) + "\n" +
					// "Last-modified: "+new
					// java.util.Date(file.lastModified())+"\n"+
					getContentType(fileName) + "\n" + "Content-length: "
					+ IN.available() + "\n" + "\n";
			OUT.write(header.getBytes());
			OUT.flush();

			byte buff[] = new byte[MAXSENDBUFF];
			while (true) {
				Thread.yield();
				int c = IN.read(buff);
				if (c == -1) {
					break;
				}
				OUT.write(buff, 0, c);
				OUT.flush();
				if (c < MAXSENDBUFF) {
					break;
				}
			}
			// if (IN!=null) IN.close();
			// if (OUT!=null) OUT.close();
			// if (mysock!=null) mysock.close();
		} catch (IOException IOE) {
			IOE.printStackTrace();
			goodfile = false;
			System.out.println("MyError: no internal file found (" + fileName
					+ ") " + IOE);
		}
	}

	/**
	 * Description of the Method
	 */
	public void sendFile() {
		File file = new File(root + "/" + fileName);
		FileInputStream IN = null;
		try {
			IN = new FileInputStream(file);
			String header = "HTTP/1.0 200 Document follows\n" + "Date: "
					+ (new java.util.Date().toString()) + "\n"
					+ "Last-modified: "
					+ new java.util.Date(file.lastModified()) + "\n"
					+ getContentType(fileName) + "\n" + "Content-length: "
					+ IN.available() + "\n" + "\n";
			OUT.write(header.getBytes());
			OUT.flush();
			byte buff[] = new byte[MAXSENDBUFF];
			while (true) {
				int c = IN.read(buff);
				Thread.yield();
				if (c == -1) {
					break;
				}
				OUT.write(buff, 0, c);
				OUT.flush();
				if (c < MAXSENDBUFF) {
					break;
				}
				c -= MAXSENDBUFF;
			}
			// IN.close();
			// OUT.close();
			// mysock.close();
		} catch (IOException IOE) {
			IOE.printStackTrace();
			goodfile = false;
			System.out.println("MyError: cannot send file (" + fileName + ") "
					+ IOE);
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param file
	 *            Description of Parameter
	 */
	public void sendNotFound(String file) {
		String body = "<HTML><HEAD><TITLE>404 Not Found</TITLE></HEAD>\n"
				+ "<BODY><H1>404 Not Found</H1>" + "File:" + file
				+ " not found </BODY></HTML>\n";
		String header = "HTTP/1.0 404\n" + getContentType(".html") + "\n"
				+ "Content-length: " + body.length() + "\n" + "\n";
		try {
			OUT.write((header + body).getBytes());
			OUT.flush();
			OUT.close();
		} catch (IOException IOE) {
			IOE.printStackTrace();
			goodfile = false;
			System.out.println("MyError: cannot send NOT FOUND page." + IOE);
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param old
	 *            Description of Parameter
	 * @param next
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public String moved(String old, String next) {
		String host = "";
		try {
			host = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException UHE) {
			UHE.printStackTrace();
		}
		String movedBody = "<HTML><HEAD><TITLE>301 Moved Permanently</TITLE></HEAD>"
				+ "<BODY><H1>Moved Permanently</H1>"
				+ "The document has moved <A HREF=\"http://"
				+ host
				+ "/"
				+ next + "\">here</A>.<P></BODY></HTML>";
		return "HTTP/1.1 301 Moved Permanently\n" + "Date: "
				+ (new java.util.Date().toString()) + "\n"
				+ "Location: http://" + host + "/" + next + "\n"
				+ "Connection: close\n" + "Content-Type: text/html\n"
				+ "Content-length: " + movedBody.length() + "\n" + "\n"
				+ movedBody;
	}

	/**
	 * A simple HTTP server. Only works for get. Doen'st support CGI. CGI -
	 * Process p=System.exec("c:\test.cgi"); post write here OutputStream
	 * o=p.getOutputStream(); o.write(web page); Process
	 * p=System.exec("c:\test.cgi"); get set enviorment String env[]=
	 * {"unknown","test"}; Process p=System.exec("c:\test.cgi",env); read input
	 * stream and write back on socket InputStream i=p.getInputStream();
	 * 
	 * @param s
	 *            Description of Parameter
	 */

	public static void main(String s[]) {
		if (s.length == 0 || s.length > 3) {
			System.out.println("FORMAT: java HTTP <port> [root [-fa]]");
			System.out.println("f=log files requested");
			System.out.println("a=log host accessing");
			System.exit(0);
		}
		int port = 80;
		ServerSocket serverSocket = null;

		try {
			port = Integer.parseInt(s[0]);
			serverSocket = new ServerSocket(port);
		} catch (NumberFormatException NFE) {
			NFE.printStackTrace();
			System.out.println("myError: Bad number in port (" + s[0] + ")"
					+ NFE);
			System.exit(0);
		} catch (IOException IOE) {
			IOE.printStackTrace();
			System.out.println("myError: Server Socket is busy " + IOE);
			System.exit(0);
		}
		String root = "";
		boolean logFile = false, logAccess = false;
		if (s.length > 1) {
			root = s[1];
		}
		if (s.length == 3) {
			logFile = s[2].toUpperCase().indexOf("F") >= 0;
			logAccess = s[2].toUpperCase().indexOf("A") >= 0;
			// logSuccess = s[2].toUpperCase().indexOf("S") >= 0;
		}
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				HTTP job = new HTTP(socket);
				job.root = root;
				job.logAccess = logAccess;
				job.logFile = logFile;
				new Thread(job).start();
				Thread.yield();
			} catch (IOException IOE) {
				IOE.printStackTrace();
				System.out.println("myError:  Cannot connect incomming socket "
						+ IOE);
			}
		}
	}
}
