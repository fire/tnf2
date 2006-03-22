package aj.misc;


public class Bench {	
	static int OPTIME=10*1000;

	public  static void main(String s[]) throws Exception {
		if (s.length==1) {
			try {
				OPTIME=1000*Integer.parseInt(s[0]);
			} catch (NumberFormatException NFE) {
				System.out.println("FORMAT: java aj.misc.Bench [<SEC>]");
				System.exit(0);
			}
		}
		if (s.length>1) {
			System.out.println("FORMAT: java aj.misc.Bench [<SEC>]");
			System.exit(0);
		}
		System.out.println(testRR());
		System.out.println(testSD());
		System.out.println(testDD());
		System.out.println(testSY());
		System.out.println(showResult());
		System.out.println(matrix());
		System.exit(0);//must close the applet stuff
	}
	static  String testRR() {
		long cc=0;
		long t=OPTIME+System.currentTimeMillis();
		while (t>System.currentTimeMillis()) {
			for (int a=0;a<OPTIME*100;a++) {
				cc++;
			}
		}
		return show("2xRANDOM ",cc);
	}
	static  String testSD() {
		long cc=0;
		long t=OPTIME+System.currentTimeMillis();

		while (t>System.currentTimeMillis()) {
			for (int a=0;a<OPTIME*100;a++) {
				cc++;
			}
		}
		return show("SAME DOUB",cc);
	}
	static  String testDD() {
		double d=Math.random()+1,e=Math.random()+1;
		long cc=0;
		long t=OPTIME+System.currentTimeMillis();
		while (t>System.currentTimeMillis()) {
			for (int a=0;a<OPTIME*100;a++) {
				cc++;
				d=d*e;
			}
		}
		return show("diff DOUB",cc);
	}
	static  String testSY() {
		long cc=0;
		long t=OPTIME+System.currentTimeMillis();
		while (t>System.currentTimeMillis()) {
			for (int a=0;a<OPTIME*100;a++) {
				cc++;
			}
		}
		return show("SYS only ",cc);
	}

	static String showResult() {
		long cc=total/4;
		String res="";
		if (cc>1000000000) res+=(cc/1000000)/1000.0+"B";
		else if (cc>1000000) res+=(cc/1000)/1000.0+"M";
		else if (cc>1000) res+=cc/1000.0+"K";
		else res+=cc;
		String tr="Overall Benchmark: "+OPTIME/1000+" secs result in "+res+" operations";
		res="";
		cc=total/4*1000/OPTIME;
		if (cc>1000000000) res+=(cc/1000000)/1000.0+"B";
		else if (cc>1000000) res+=(cc/1000)/1000.0+"M";
		else if (cc>1000) res+=cc/1000.0+"K";
		else res+=cc;
		tr+="\nOverall Benchmark: "+res+" operations per second";
		total=0;
		return tr;
	}
	static long total=0;
	static  String show(String s,long cc) {
		total+=cc;
		String res="";
		if (cc>1000000000) res+=(cc/1000000)/1000.0+"B";
		else if (cc>1000000) res+=(cc/1000)/1000.0+"M";
		else if (cc>1000) res+=cc/1000.0+"K";
		else res+=cc;
		return "Benchmark: "+s+" "+OPTIME/1000+" secs result in "+res+" operations";
	}

	public static String matrix() {
int MAX=12;
		double m[][][]=new double[MAX][MAX][MAX];
		int a,b,c,d,e,f;
		for (a=0;a<MAX;a++) 
		for (b=0;b<MAX;b++) 
		for (c=0;c<MAX;c++) 
			m[a][b][c]=Math.random()+1;
		long t=System.currentTimeMillis();
		for (a=0;a<MAX;a++) 
		for (b=0;b<MAX;b++) 
		for (c=0;c<MAX;c++) 
		for (d=0;d<MAX;d++) 
		for (e=0;e<MAX;e++) 
		for (f=0;f<MAX;f++) {
			m[a][b][c]=m[c][b][a]*m[d][e][f]*m[b][c][a]*m[e][f][d];
		}
		long cc=(long)Math.pow(MAX,6)*1000/(System.currentTimeMillis()-t);
		String res="";
		if (cc>1000000000) res+=(cc/1000000)/1000.0+"B";
		else if (cc>1000000) res+=(cc/1000)/1000.0+"M";
		else if (cc>1000) res+=cc/1000+"K";
		else res+=cc;
		return "Benchmark Matrix of 12: "+(System.currentTimeMillis()-t)/1000+" secs "+res+" ops/sec";
	}

}