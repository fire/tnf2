package aj.awt.maze;



import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Vector;



public class DigMaze{



	public static void main(String s[]) {new DigMaze(s);} 



	String defaultstart="S",defaultend="E",defaultperm="P",defaultopen=" ",defaultfill="#";

	String start="",end="",perm="",open="",fill="";

	String inFile=null,outFile=null;	

	char map[][];

	boolean connect8=false;

	boolean chambers=false;

	double chamberRate=.01;

	boolean wall=false;

	boolean perfect4=false;



	public DigMaze(String s[]) {

		for (int a=0;a<s.length;a++) {

			String ss=s[a];

			if (ss.indexOf("?")>=0) {

				System.out.println("FORMAT: java DigMaze [-options]");

				System.out.println("	-i<filename>  Input file name");

				System.out.println("	-o<filename>  Output file name");

				System.out.println("	-s<sybmols>..  Starting symbols (0 or more)");

				System.out.println("	-e<symbols>..  Ending symbols (0 or more)");

				System.out.println("	-p<symbols>..  Perminate symbol (0 or more)");

				System.out.println("	-b<symbols>..  Blank/open symbol (0 or more)");

				System.out.println("    -f<symbols>..  Fill/barrior symbol (0 or more)");

				System.out.println("	-8      	   use 8 connect");

				System.out.println("	-4             Use perfect 4");

				System.out.println("	-c<rate>       Use chambers at <rate 0-1>");

				System.out.println("	-w             Leave wall around maze");

				return;

			}



			if (ss.startsWith("-")) ss=ss.substring(1);

			boolean nextarg=false;

			if (ss.length()==1 && s.length>a+1) nextarg=true;

			if (ss.toUpperCase().startsWith("I") && inFile==null) {

				if (!nextarg) inFile=ss.substring(1).trim();

				else {inFile=s[a+1];a++;}

			}

			if (ss.startsWith("4")) perfect4=true;

			if (ss.startsWith("8")) connect8=true;

			if (ss.toUpperCase().startsWith("W")) {

				wall=true;

//				System.out.println("Using walls");

			}

			if (ss.toUpperCase().startsWith("O") && outFile==null) {

				if (!nextarg) outFile=ss.substring(1).trim();

				else {outFile=s[a+1];a++;}

			}

			if (ss.toUpperCase().startsWith("S")) {

				if (!nextarg) start+=ss.substring(1);

				else {start+=s[a+1];a++;}

			}

			if (ss.toUpperCase().startsWith("E")) {

				if (!nextarg) end+=ss.substring(1);

				else {end+=s[a+1];a++;}

			}

			if (ss.toUpperCase().startsWith("F")) {

				if (!nextarg) fill+=ss.substring(1);

				else {fill+=s[a+1];a++;}

			}	

			if (ss.toUpperCase().startsWith("P")) {

				if (!nextarg) perm+=ss.substring(1);

				else {perm+=s[a+1];a++;}

			}

			if (ss.toUpperCase().startsWith("B")) {

				if (!nextarg) open+=ss.substring(1);

				else {open+=s[a+1];a++;}

			}

		}

		if (open.length()==0) open=defaultopen;

		if (start.length()==0) start=defaultstart;

		if (perm.length()==0) perm=defaultperm;

		if (end.length()==0) end=defaultend;

		if (fill.length()==0) fill=defaultfill;

		if (inFile!=null) readFile(inFile);

		else read(System.in);

		process();

		if (outFile!=null) writeFile(outFile);

		else write(System.out);

	}



	public void process() {

		Vector list=new Vector();

		//find and add starting points to list

		int a,b;

		open+=start;

		if (outFile!=null) {System.out.println("finding start points");}

		for (a=0;a<map.length;a++) {

			for (b=0;b<map[a].length;b++){

				if (start.indexOf(map[a][b])>=0) {

					list.addElement(new Point(a,b));

				}

				if (end.indexOf(map[a][b])<0 && perm.indexOf(map[a][b])<0 && open.indexOf(map[a][b])<0 && fill.indexOf(map[a][b])<0) 

					map[a][b]=fill.charAt(0);

			}

		}

		if (list.size()==0) {

			if (outFile!=null) System.out.println("Warning. Cannot find starting point.  Creating random one.");

			int x=(int)((map.length-(wall?2:0))*Math.random()+(wall?1:0));

			int y=(int)((map[x].length-(wall?2:0))*Math.random()+(wall?1:0));

			map[x][y]=start.charAt(0);

			list.addElement(new Point(x,y));

		}

		//find all ends

		Vector endList=new Vector();

		for (a=0;a<map.length;a++) for (b=0;b<map[a].length;b++) {

			if (end.indexOf(map[a][b])>=0) endList.addElement(new Point(a,b));

		}

		if (endList.size()==0) {

			if (outFile!=null) System.out.println("Warning. Cannot find ending point.  Creating random one.");

			int x=(int)((map.length-(wall?2:0))*Math.random()+(wall?1:0));

			int y=(int)((map[x].length-(wall?2:0))*Math.random()+(wall?1:0));

			map[x][y]=end.charAt(0);

			endList.addElement(new Point(x,y));

		}

		if (outFile!=null) {System.out.println("building maze");}

		//while list ! empt

		boolean foundend=false;

		while (list.size()>0) {

			int c=(int)(Math.random()*list.size());

			Point p=(Point)list.elementAt(c);

			foundend=expand(list,p)||foundend;

		}

		//restore ends

		for (a=0;a<endList.size();a++) {

			Point p=(Point)endList.elementAt(a);

			map[p.x][p.y]=end.charAt(0);

			foundend=foundend|(getAdjacent(p)>0);

		}

		if (!foundend && outFile!=null) {

			System.out.println("Warning. End unreachable.");

		}

	}	

	public boolean expand(Vector list,Point p){

//		System.out.println("Expanding "+p.x+","+p.y);

		int dir=(int)(connect8?Math.random()*8:(int)(Math.random()*4)*2);

		int tries=0;

		while (tries<(connect8?8:4)) {

			Point n=new Point(

p.x-(dir<2 || dir==7?1:0)+(dir>=3 && dir<=5?1:0),

p.y+(dir>=1 && dir<=3?1:0)-(dir>=5 && dir<=7?1:0));

			int ajd=getAdjacent(n);

			if (n.x+(wall?2:1)>map.length || n.x<(wall?1:0) || n.y+(wall?2:1)>map[n.x].length || 

					n.y<(wall?1:0) || perm.indexOf(map[n.x][n.y])>=0 || open.indexOf(map[n.x][n.y])>=0) {

				tries++;

				dir+=(connect8?1:2);

				if (dir>7) dir=dir-8;

				continue;

			}

//System.out.println("move has "+ajd+" opens adjacent");

			if (ajd<=1) {

				boolean found=false;

//System.out.println("new position ="+n.x+","+n.y);

				if (end.indexOf(map[n.x][n.y])<0){

					list.addElement(n);

				}

				else found=true;

				map[n.x][n.y]=open.charAt(0);

				if(perfect4) {

					Point n2=new Point(

						n.x-(dir<2 || dir==7?1:0)+(dir>=3 && dir<=5?1:0),

						n.y+(dir>=1 && dir<=3?1:0)-(dir>=5 && dir<=7?1:0));



					list.removeElement(n);

					if (!(n2.x+(wall?2:1)>map.length || n2.x<(wall?1:0) || n2.y+(wall?2:1)>map[n2.x].length || 

							n2.y<(wall?1:0) || perm.indexOf(map[n2.x][n2.y])>=0 || open.indexOf(map[n2.x][n2.y])>=0)) {

						map[n2.x][n2.y]=open.charAt(0);

						list.addElement(n2);

					}



				}

				return found;

			}

			else {

				tries++;

				dir+=(connect8?1:2);

				if (dir>7) dir=dir-8;

			}

		}

		if (tries>=(connect8?8:4)) {

			list.removeElement(p);

//System.out.println("position blocked ="+p.x+","+p.y);

		}

		return false;

	}

	public int getMove(Point n) {return 1;

	}

	public int getAdjacent(Point n) {

//		System.out.println("check adjacent count for n=<"+n.x+","+n.y+">");

		int count=0;

		for (int a=-1;a<2;a++) for (int b=-1;b<2;b++) {

			if (!connect8 && Math.abs(a)==Math.abs(b)) continue;

//System.out.println("checking a="+a+" b="+b+"  t=<"+(n.x+a)+","+(n.y+b)+">");

			if (map.length>a+n.x && n.x+a>=0 && map[n.x+a].length>n.y+b && n.y+b>=0) {

				if (open.indexOf(map[n.x+a][n.y+b])>=0) count++;

			}

		}

		return count;

	}



	public void readFile(String f) {

		try {

			read(new FileInputStream(f));

		} catch (IOException IOE) {

			System.out.println("MyError: cannot read file "+f+". "+IOE);

			System.exit(0);

		}

	}

	public void writeFile(String f) {

		try {

			FileOutputStream fo=new FileOutputStream(f);



			write(fo);

			fo.flush();

			fo.close();

		} catch (IOException ioe) {

			System.out.println("MyError: cannot write to file "+f+". "+ioe);

			System.exit(0);

		}

	}



	public void read(InputStream i) {

		try {

			Vector v=new Vector();

			BufferedReader br=new BufferedReader(new InputStreamReader(i));

			while (true) {

				String s=br.readLine();

				if (s==null) break;

				v.addElement(s);

			}

			map=new char[v.size()][];

			if (map.length==0) {

				System.out.println("MyError: error no data to read.");

				System.exit(0);

			}

			

			for (int a=0;a<v.size();a++) {

				map[a]=new char[v.elementAt(a).toString().length()];

				for (int b=0;b<map[a].length;b++) {

					map[a][b]=v.elementAt(a).toString().charAt(b);

				}

			}



		} catch (IOException ioe) {

			System.out.println("MyError: error in reading data"+ioe);

			System.exit(0);

		}

	}

	public void write(OutputStream o) {

		PrintStream pw=null;

		if (o==System.out) pw=System.out;

		else pw=new PrintStream(o);

		for (int a=0;a<map.length;a++) {

			pw.println(new String(map[a]));

		}

	}

}





/*

XXXXX

XXXXX

XXSXX

XX XX

XX XX



can move to S if

4 connect & closed 0 2 4 6 all but 1

8 connect & closed 1-8 all but 1

*/

