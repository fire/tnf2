package aj.stock;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import aj.misc.Stuff;

class YahooTracker extends JPanel implements ActionListener{
	Vector skipDate=new Vector();
	String stopDate="21-Feb";
	String stopDate2="21-Feb";
 	JComboBox dataList;
	boolean iemmode=false;
	boolean log=false;
	boolean histPage=false;
	boolean nogui=false;
	boolean noreal=false;
	static String contTail="";
	static int displayMaxX=0;
	Vector allSymbols=new Vector();
	boolean original=false;
	boolean percent=false;
	boolean relitive=true;
	boolean single=false;
	boolean loadError=false;

	public JTextField statusText=new JTextField(8);
	JButton up=new JButton("Update");
	JFrame f=null;
	JPanel jp=null;
	
	Color c0=Color.red;
	Color c1=Color.blue;
	Color c2=Color.green;
	Color c3=Color.cyan;
	Color c4=Color.magenta;
	Color c5=Color.darkGray;
	Color c6=Color.gray;
	Color c7=Color.yellow;

	static boolean logALL=false;
	
	public static void main(String s[]) {
		new YahooTracker(s);
	}

	public void help() {
		System.out.println("Usage: java aj.stock.YahooTracker [options]");
		System.out.println("  <symbol> . . .");
		System.out.println("  -l log");
		System.out.println("  -A log ALL data received");
		System.out.println("  -s<date> (eg -s19-Nov) skip date");
		System.out.println("  -iem Iem stockes and dates");
		System.out.println("  -noreal No realtime quote");
		System.out.println("  -d<enddate> (eg \"-d19-Nov\")");
		System.out.println("  -nogui (why?)");
		System.exit(0);
	}


	public Date moveByDays(Date d, int x) {
 		return new Date(d.getTime()+(x)*1000*60*60*24);
	}

	public Date getLast3rdFriday(Date d) {
		d=moveByDays(d,-1);
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(d);
		Date res;

 		int mon=calendar.get(Calendar.MONTH)+1;
 		int dom=calendar.get(Calendar.DAY_OF_MONTH);
 		int dow=calendar.get(Calendar.DAY_OF_WEEK);

		if (dow<6 && dow!=0) {
		  res=moveByDays(d,-dow-1);
		} else if (dow==1) {
		  res=moveByDays(d,-2);
		} else if (dow==7){
		  res=moveByDays(d,-1);
		} else {
			res=d;
		}
		calendar.setTime(res);
 		mon=calendar.get(Calendar.MONTH)+1;
 		dom=calendar.get(Calendar.DAY_OF_MONTH);
 		dow=calendar.get(Calendar.DAY_OF_WEEK);
		while (dow!=6 || dom<15 || dom>21) {
			calendar.setTime(res);
 			mon=calendar.get(Calendar.MONTH)+1;
 			dom=calendar.get(Calendar.DAY_OF_MONTH);
 			dow=calendar.get(Calendar.DAY_OF_WEEK);
			if (dom<15 ) {
				res=moveByDays(res,-7);
			}
			else if (dom>21) {
			 	res=moveByDays(res,-7);
			}
		}
		return res;
	}

	public Date getNext3rdFriday(Date d) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(d);
		Date res;

 		int mon=calendar.get(Calendar.MONTH)+1;
 		int dom=calendar.get(Calendar.DAY_OF_MONTH);
 		int dow=calendar.get(Calendar.DAY_OF_WEEK);

		if (dow<6 && dow!=0) {
		  res=moveByDays(d,6-dow);
		} else if (dow==1) {
		  res=moveByDays(d,5);
		} else if (dow==7){
		  res=moveByDays(d,6);
		} else {
			res=d;
		}
		calendar.setTime(res);
 		mon=calendar.get(Calendar.MONTH)+1;
 		dom=calendar.get(Calendar.DAY_OF_MONTH);
 		dow=calendar.get(Calendar.DAY_OF_WEEK);
		while (dow!=6 || dom<15 || dom>21) {
			calendar.setTime(res);
 			mon=calendar.get(Calendar.MONTH)+1;
 			dom=calendar.get(Calendar.DAY_OF_MONTH);
 			dow=calendar.get(Calendar.DAY_OF_WEEK);
			if (dom<15 ) {
				res=moveByDays(res,7);
			}
			else if (dom>21) {
			 	res=moveByDays(res,7);
			}
		}
		return res;
	}

	public String getIEMEndDate2() {
		Date last3rdFriday=getLast3rdFriday(new Date());
		last3rdFriday=moveByDays(last3rdFriday,-1);
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(last3rdFriday);
 		return getDateString(calendar);
	}

	public String getDateString(Calendar calendar) {
		int mon=calendar.get(Calendar.MONTH)+1;
 		int dom=calendar.get(Calendar.DAY_OF_MONTH);
 		int dow=calendar.get(Calendar.DAY_OF_WEEK);
		int y=calendar.get(Calendar.YEAR);
 		contTail="_0"+(y-2000);
		String res=dom+"";
		if (mon==1) {res+="-Jan";contTail+="b";}
		if (mon==2) {res+="-Feb";contTail+="c";}
		if (mon==3) {res+="-Mar";contTail+="d";}
		if (mon==4) {res+="-Apr";contTail+="e";}
		if (mon==5) {res+="-May";contTail+="f";}
		if (mon==6) {res+="-Jun";contTail+="g";}
		if (mon==7) {res+="-Jul";contTail+="h";}
		if (mon==8) {res+="-Aug";contTail+="i";}
		if (mon==9) {res+="-Sep";contTail+="j";}
		if (mon==10) {res+="-Oct";contTail+="k";}
		if (mon==11) {res+="-Nov";contTail+="l";}
		if (mon==12) {res+="-Dec";contTail+="a";}
		return res;
	}
	
	public int getDateValue(String date) {
		if (date.equals(today)) return 1;
		String t[]=Stuff.getTokens(date,"-");
		int res=Integer.parseInt(t[0]);
		if (t.length>1) {
			if (t[1].equalsIgnoreCase("Jan")) res+=30;
			if (t[1].equalsIgnoreCase("Feb")) res+=30*2;
			if (t[1].equalsIgnoreCase("Mar")) res+=30*3;
			if (t[1].equalsIgnoreCase("Apr")) res+=30*4;
			if (t[1].equalsIgnoreCase("May")) res+=30*5;
			if (t[1].equalsIgnoreCase("Jun")) res+=30*6;
			if (t[1].equalsIgnoreCase("Jul")) res+=30*7;
			if (t[1].equalsIgnoreCase("Aug")) res+=30*8;
			if (t[1].equalsIgnoreCase("Sep")) res+=30*9;
			if (t[1].equalsIgnoreCase("Oct")) res+=30*10;
			if (t[1].equalsIgnoreCase("Nov")) res+=30*11;
			if (t[1].equalsIgnoreCase("Dec")) res+=30*12;
		}
		if (t.length>2) {
			res+=2000*Integer.parseInt(t[2]);
		}
		return res;
	}
	
	public String getIEMEndDate() {
		Date last3rdFriday=getLast3rdFriday(new Date());

		Date endDate=getNext3rdFriday(new Date());
		int bd=(int)((endDate.getTime()-last3rdFriday.getTime())/(1000*60*60*24));
		displayMaxX=bd/7*5+bd%7-(bd%7==6?-1:0);
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(last3rdFriday);
		return getDateString(calendar);
	}

	public YahooTracker(String s[]) {
		Vector v=allSymbols;
		if (s.length==0) help();
		for (int a=0;a<s.length;a++) {
			if (s[a].indexOf("?")>=0) {help();}
			else if (s[a].toUpperCase().startsWith("-IEM")) {
				System.out.println(";IEM mode!");
				iemmode=true;
				v.addElement("aapl");c0=Color.red;
				v.addElement("ibm");c1=Color.blue;
				v.addElement("msft");c2=Color.green;
				v.addElement("^gspc");c3=Color.cyan;
				stopDate=getIEMEndDate();
				stopDate2=getIEMEndDate2();
				System.out.println(";IEM beginDate = "+stopDate);
			}
			else if (s[a].toUpperCase().startsWith("-NOREAL")) {
				noreal=true;
			}
			else if (s[a].toUpperCase().startsWith("-A")) {
				logALL=true;
			}
			else if (s[a].toUpperCase().startsWith("-NOGUI")) {
				nogui=true;
			}
			else if (s[a].toUpperCase().startsWith("-S")) {
				skipDate.addElement(s[a].substring(2));
				System.out.println("Skip date created "+s[a].substring(2));
			}
			else if (s[a].toUpperCase().startsWith("-L")) {
				log=true;
				System.out.println(";Logging on");
			}
			else if (s[a].toUpperCase().startsWith("-D")) {
				stopDate2=stopDate=s[a].substring(2);
				System.out.println(";New beginDate ="+stopDate);
			}
			else v.addElement(s[a]);
		}

		String args[]=new String[v.size()];
		for (int a=0;a<args.length;a++) {
			args[a]=(String)v.elementAt(a);
			readSymbol(args[a]);
			if (log) System.out.println(";symbol data read");
		}
		if (log) System.out.println(";all data read");
		
		if (!nogui) {
			f=new JFrame();
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
			//f.setBackground(Color.white);
			jp=new JPanel(new BorderLayout());
			//jp.setBackground(Color.white);
			f.getContentPane().add(jp);
			//f.getContentPane().setBackground(Color.white);
			jp.add("Center",this);
			this.setBackground(Color.white);
			JPanel buttons=new JPanel(new FlowLayout());
			//buttons.setBackground(Color.white);
			JButton ori=new JButton("Original");
			JButton per=new JButton("Percent");
			JButton rel=new JButton("Relitive");
			JButton sin=new JButton("Single");
			statusText.setText("Ready");
			buttons.add(statusText);
			buttons.add(up);
			buttons.add(per);
			buttons.add(ori);
			buttons.add(rel);
			buttons.add(sin);
 			dataList = new JComboBox(args);
			dataList.setSelectedIndex(greatestValueIndex());
			System.out.println(";high value change stock ="+dataList.getItemAt(greatestValueIndex()));
			buttons.add(dataList);

			jp.add("South",buttons);
			up.addActionListener(this);
			per.addActionListener(this);
			ori.addActionListener(this);
			rel.addActionListener(this);
			sin.addActionListener(this);
			f.pack();
			f.setVisible(true);
		}
	}
	
	public void actionPerformed(ActionEvent ae) {
		String ac=ae.getActionCommand();
		if (ac.equals("Percent")) {
			percent=true;original=false;relitive=false;single=false;
		}
		if (ac.equals("Original")) {
			percent=false;original=true;relitive=false;single=false;
		}
		if (ac.equals("Update")) {
			up.setEnabled(false);
			new Thread() {
				public void run() {
					statusText.setText("Updating");
					statusText.setBackground(Color.lightGray);
					update();
					statusText.setText("Ready");
					statusText.setBackground(Color.white);
					up.setEnabled(true);
					repaint();
				}
			}.start();
		}
		if (ac.equals("Single")) {
			percent=false;original=false;relitive=false;single=true;
		}
		if (ac.equals("Relitive")) {
			percent=false;original=false;relitive=true;single=false;
		}
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.translate(10,10);
		g.drawRect(0,0,getWidth()-80,getHeight()-20);
		double maxY=0,minY=0;
		String relsym=(String)dataList.getSelectedItem();
		if (relsym==null) relsym="";
		minY=maxY=getValue(relsym,0);
		
		for (int a=0;a<allSymbols.size();a++) {
			String s=(String)allSymbols.elementAt(a);
			if (!s.equalsIgnoreCase(relsym) && single) continue;
			for (int b=0;b<allDates.size();b++) {
				maxY=Math.max(maxY,getValue(s,b));
				minY=Math.min(minY,getValue(s,b));				
			}
		}
		if (maxY==minY) return;
		double dy=(1.0*getHeight()-20)/(maxY-minY);
		double dx=(1.0*getWidth()-80)/displayMaxX;

		//draw vert lines
		int spread=displayMaxX/20;
		if (spread==0) spread =1;
		Vector symlist=allSym;
		for (int a=0;a<allDates.size();a++) {
			String dat=(String)allDates.elementAt(a);
			int drawa=getMaxIndex()-a-1;
			if (a%spread==0) {
				g.drawLine((int)(drawa*dx),0,(int)(drawa*dx),getHeight()-20);
				if (dat.equals(today)) continue;
				if (isFriday(dat)) {
					g.setColor(Color.red);
					g.drawLine((int)(drawa*dx),0,(int)(drawa*dx),getHeight()-20);
					g.drawLine((int)(drawa*dx+1),0,(int)(drawa*dx+1),getHeight()-20);
					g.setColor(Color.black);
					g.drawString(""+dat,(int)(drawa*dx),20);
				}
			}
		}
		//draw horizontal lines
		for (int a=0;a<getHeight()/40;a++) {
			int pos=a*40;
			double val=maxY-pos/dy;
			g.drawLine(0,pos,getWidth()-80,pos);
			g.drawString(""+Stuff.trunc(val,2),0,pos);
		}
		
		for (int a=0;a<getMaxIndex();a++) {
			for (int b=0;b<symlist.size();b++) {
				if (b==0) g.setColor(c0);
				if (b==1) g.setColor(c1);
				if (b==2) g.setColor(c2);
				if (b==3) g.setColor(c3);
				if (b==4) g.setColor(c4);
				if (b==5) g.setColor(c5);
				if (b==6) g.setColor(c6);
				if (b==7) g.setColor(c7);
				String sym=(String)symlist.elementAt(b);
				if (!sym.equalsIgnoreCase(relsym) && single) continue;
				double nex=getValue(sym,a);
				double las=nex;
				if (a!=0) las=getValue(sym,a-1);
				int drawa=getMaxIndex()-a;
				g.drawLine((int)((drawa)*dx),(int)((maxY-las)*dy),(int)((drawa-1)*dx),(int)((maxY-nex)*dy));
				g.drawLine((int)((drawa)*dx),(int)((maxY-las)*dy+1),(int)((drawa-1)*dx),(int)((maxY-nex)*dy+1));
				g.drawLine((int)((drawa)*dx-1),(int)((maxY-las)*dy),(int)((drawa-1)*dx-1),(int)((maxY-nex)*dy));
				g.drawLine((int)((drawa)*dx),(int)((maxY-las)*dy-1),(int)((drawa-1)*dx),(int)((maxY-nex)*dy-1));
				g.drawLine((int)((drawa)*dx+1),(int)((maxY-las)*dy),(int)((drawa-1)*dx+1),(int)((maxY-nex)*dy));
				String disval=Stuff.trunc(nex,2)+"0000";
				disval=(nex>=0?"+":"")+disval.substring(0,(nex<0?5:4));
				disval=""+disval+" "+sym;
				if (a==0) g.drawString(disval,getWidth()-80,(int)((maxY-nex)*dy));
				if (a==0) g.drawString(disval,getWidth()-80-1,(int)((maxY-nex)*dy));
				if (a==0) g.drawString(disval,getWidth()-80,(int)((maxY-nex)*dy-1));
			}
			
		}		
		g.translate(-10,-10);
	}
	
	public int greatestValueIndex() {
		int res=0;
		String relsym=(String)dataList.getSelectedItem();
		if (relsym==null) relsym="";
		double maxY=getValue(relsym,0);
		for (int a=0;a<allSymbols.size();a++) {
			String sym=(String)allSymbols.elementAt(a);
			if (sym.equalsIgnoreCase(relsym) && single) continue;
			if (getValue(sym,0)>maxY) {
				relsym=sym;
				maxY=getValue(sym,0);
			} 
		}
		for (int a=0;a<dataList.getItemCount();a++) {
			if (((String)dataList.getItemAt(a)).equalsIgnoreCase(relsym))	return a;
		}
		return 0;
	}

	public boolean isFriday(String s) {
		if (s==null) return false;
		if (s.indexOf("-")<0 || s.indexOf("-")==s.lastIndexOf("-")) return false;
		int d=Integer.parseInt(s.substring(0,s.indexOf("-")));
		String mo=s.substring(s.indexOf("-")+1,s.lastIndexOf("-"));
		int m=0;
		if (mo.equals("Feb")) m=1;
		if (mo.equals("Mar")) m=2;
		if (mo.equals("Apr")) m=3;
		if (mo.equals("May")) m=4;
		if (mo.equals("Jun")) m=5;
		if (mo.equals("Jul")) m=6;
		if (mo.equals("Aug")) m=7;
		if (mo.equals("Sep")) m=8;
		if (mo.equals("Oct")) m=9;
		if (mo.equals("Nov")) m=10;
		if (mo.equals("Dec")) m=11;
		int y=2000+Integer.parseInt(s.substring(s.lastIndexOf("-")+1));

		Calendar calendar = new GregorianCalendar();
		calendar.set(y,m,d);
 		return (calendar.get(Calendar.DAY_OF_WEEK)==6);
	}				

	public Dimension getPreferredSize() {
		return new Dimension(300,200);
	}

	public void update() {
		Vector symlist=getAllSymbols();
		if (iemmode) {
			c0=Color.gray;
			c1=Color.gray;
			c2=Color.gray;
			c3=Color.gray;
		}
		for (int a=0;a<symlist.size();a++) {
			String sym=(String)symlist.elementAt(a);
			statusText.setText("Updating "+sym);		
			if (sym.equalsIgnoreCase("^GSPC")) 
				read20MinQuote(sym,0);
			else 
				readRealTime(sym,0);
			if (iemmode && a==0)c0=Color.red;
			else if (iemmode && a==1)c1=Color.blue;
			else if (iemmode && a==2)c2=Color.green;
			else if (iemmode && a==3)c3=Color.cyan;
			repaint();
		}
	}

	public void readSymbol(String sym) {
		System.out.print(";Reading symbol "+sym.toUpperCase());
		if (sym.equalsIgnoreCase("^GSPC")) {
			System.out.print(".delay");
			read20MinQuote(sym,0);
			System.out.print(".");
		}
		else {
			System.out.print(".real");
			readRealTime(sym,0);
			System.out.print(".");
		} 
		System.out.print("hist");
		readHistorical(sym);
		System.out.println("."+getMaxIndex());
	}

	public void readRealTime(String sym,int realInd) {
		if (noreal) return;
		String loadUrl="http://finance.yahoo.com/q/ecn?s="+sym;
		if (log) System.out.println(";realtime connecting "+sym);
		String all=readUrl(loadUrl);
		if (logALL) System.out.println(all);

		all=aj.io.MLtoText.cutMLaddSpaces(all);
		all=aj.misc.Stuff.superTrim(all);

		while(all.indexOf(",")>=0) all=all.substring(0,all.indexOf(","))+all.substring(all.indexOf(",")+1);
		
		if (all.indexOf("Last Trade:")>=0) {
			all=all.substring(all.indexOf("Last Trade:")+11).trim();
			if (all.indexOf(" ")>=0) {
				try {
					double val=Double.parseDouble(all.substring(0,all.indexOf(" ")).trim());
					setValue(today,sym,val);
					if (log) System.out.println(";quote "+sym+" "+all.substring(0,all.indexOf(" ")).trim()+" rt"+" index="+realInd);
				} catch (NumberFormatException nfe) {
					System.out.println(";MyError: realtime1 not available for "+sym+" switching to 20min quote");
					read20MinQuote(sym,realInd);
				}
			}
		}
		else if (all.indexOf("Index Value:")>=0) {
			all=all.substring(all.indexOf("Index Value:")+11).trim();
			if (all.indexOf(" ")>=0) {
				try {
					double val=Double.parseDouble(all.substring(0,all.indexOf(" ")).trim());
					setValue(today,sym,val);
					if (log) System.out.println(";quote "+sym+" "+all.substring(0,all.indexOf(" ")).trim()+" rt"+" index="+realInd);
				} catch (NumberFormatException nfe) {
					System.out.println(";MyError: realtime2 not available for "+sym+" switching to 20min quote");
					read20MinQuote(sym,realInd);
				}
			}
		}
		else {
			System.out.println(";MyError: realtime3 not available for "+sym+" switching to 20min quote");
			read20MinQuote(sym,realInd);
		}
	}

	public void read20MinQuote(String sym,int realInd) {
		//get current quote
		String loadUrl="http://finance.yahoo.com/q?s="+sym;

		if (log) System.out.println(";20minQuote connecting "+sym);
		String all=readUrl(loadUrl);
		if (logALL) System.out.println(all);

		all=aj.io.MLtoText.cutMLaddSpaces(all);
		all=aj.misc.Stuff.superTrim(all);

		while(all.indexOf(",")>=0) all=all.substring(0,all.indexOf(","))+all.substring(all.indexOf(",")+1);
		
		if (all.indexOf("Last Trade:")>=0) {
			all=all.substring(all.indexOf("Last Trade:")+11).trim();
			if (all.indexOf(" ")>=0) {
				try {
					double val=Double.parseDouble(all.substring(0,all.indexOf(" ")).trim());
					setValue(today,sym,val);
					if (log) System.out.println(";20quote "+sym+" "+all.substring(0,all.indexOf(" ")).trim()+" rt"+" index="+realInd);
				} catch (NumberFormatException nfe) {
					System.out.println(";MyError: 20minQuote not available");
				}
			}
		}
		else if (all.indexOf("Index Value:")>=0) {
			all=all.substring(all.indexOf("Index Value:")+12).trim();
			if (all.indexOf(" ")>=0) {
				try {
					String res=all.substring(0,all.indexOf(" "));
					double val=Double.parseDouble(all.substring(0,all.indexOf(" ")).trim());
					setValue(today,sym,val);
					if (log) System.out.println(";20quote "+sym+" "+all.substring(0,all.indexOf(" ")).trim()+" rt"+" index="+realInd);
				} catch (NumberFormatException nfe) {
					System.out.println(";MyError: 20minQuote not available");
				}
			}
		}
	}


	public void readHistorical(String sym) {
		String loadUrl="http://finance.yahoo.com/q/hp?s="+sym;
		boolean done=false;
		if (log) System.out.println(";connecting "+sym);
		if (log) System.out.println(";downloading "+sym);
		String all=readUrl(loadUrl);
		if (logALL) System.out.println("ALL HIST received="+all);
		all=aj.io.MLtoText.cutMLaddSpaces(all);
		if (histPage) System.out.println(";ML free>>\n"+all);
		String mm[]=aj.misc.Stuff.getTokens(all,"\n \t");
		if (all.indexOf("Volume")>=0) {// csv download only
			all=all.substring(all.indexOf("Volume")+6).trim();
			if (histPage) System.out.println(";\"Volume\" cut \n"+all);
			mm=aj.misc.Stuff.getTokens(all,"\n, \t");
		}
		if (all.indexOf("Adj Close*")>=0) {// html download only
			all=all.substring(all.indexOf("Adj Close*")+10).trim();
			if (histPage) System.out.println(";\"Adj Close*\" cut \n"+all);
			mm=aj.misc.Stuff.getTokens(all,"\n \t");
		}
			int nextIndex=1;
			for (int a=0;a<mm.length-1;a++) {
				//mm[4] == actual cloas
				//mm[6] == dividend adjusted close
				if (mm[a].indexOf("-")!=mm[a].lastIndexOf("-")) {//ie Nov-19-2003
					if (mm[a+6].endsWith("*")) mm[a+6]=mm[a+6].substring(0,mm[a+6].length()-1);
					while(mm[a+6].indexOf(",")>=0) mm[a+6]=mm[a+6].substring(0,mm[a+6].indexOf(","))+mm[a+6].substring(mm[a+6].indexOf(",")+1);
					double val=0;
					try {
						val=Double.parseDouble(mm[a+6]);
					} catch (NumberFormatException nfe) {
						System.out.println(";MyError: bad number in >"+mm[a+4]);
						continue;
					}
					String  date=mm[a];
					boolean doSkip=false;
					for (int b=0;b<skipDate.size();b++) {
						String testDate=(String)skipDate.elementAt(b);
						if (mm[a].toUpperCase().indexOf(testDate.toUpperCase())>=0) {
							System.out.println(";Skip date detected");
							doSkip=true;
						}
					}
					if (doSkip) continue;
					setValue(date,sym,val);
					System.out.print(".");
					if (log) System.out.println(";quote "+sym+" "+mm[a+6]+" on "+mm[a]+" index="+nextIndex);
					if (mm[a].toUpperCase().indexOf(stopDate.toUpperCase())>=0) break;
					if (mm[a].toUpperCase().indexOf(stopDate2.toUpperCase())>=0) break;
					nextIndex++;
				}
			}
	}

	public String readUrl(String loadUrl){
		String all="";
		try {
			URL u=new URL(loadUrl);
			InputStream i=u.openStream();
				if (i==null) return "null inputstream";
				BufferedReader br=new BufferedReader(new InputStreamReader(i));
				while (true) {
					String t=br.readLine();
					if (t==null) break;
					all+=t.trim()+"\n";
				}
			} catch (IOException ioe) {
				System.out.println("MyError: cannot read url "+loadUrl);
			}
				
			return all;
	}
	
	String today="today";
	Vector allDates=new Vector();
	Vector allSym=new Vector();
	Vector allValues=new Vector();

	public void setValue(String date,String name,double value) {
		boolean found=false;
		//find all syms
		for (int a=0;a<allSym.size();a++) {
			String t=(String)allSym.elementAt(a);
			if (t.equalsIgnoreCase(name)) {
				found=true;
				break;
			}
		}
		if (!found) {
			allSym.addElement(name);
		}
		//		find unique dates
		found=false;
		for (int a=0;a<allDates.size();a++) {
			String t=(String)allDates.elementAt(a);
			if (t.equalsIgnoreCase(date)) {
				found=true;
				break;
			}
		}
		if (!found) {
			allDates.addElement(date);
		}
		//sort dates 0=today  30+?? = this year
		for (int a=0;a<allDates.size();a++) {
			String t=(String)allDates.elementAt(a);
			int tv=getDateValue(t);
			for (int b=a;b<allDates.size();b++) {
				String tt=(String)allDates.elementAt(a);
				int ttv=getDateValue(tt);
				if (ttv<tv) {
					allDates.removeElement(tt);
					allDates.removeElement(t);
					allDates.insertElementAt(tt,a);
					allDates.insertElementAt(t,b);
					break;
				}
			}
		}
		found=false;
		for (int a=0;a<allValues.size();a++) {
			Trip t=(Trip)allValues.elementAt(a);
			if (t.sym.equals(name) && t.date.equals(date)) {
				t.val=value;
				found=true;
			}
		}
		if (!found) {
			Trip t=new Trip(date,name,value);
			allValues.addElement(t);
		}
	}
	
	public int getDateIndex(String d) {
		for (int a=0;a<allDates.size();a++) {
			String t=(String)allDates.elementAt(a);
			if (t.equalsIgnoreCase(d)) return a;
		}
		return -1;
	}
	public Vector getAllSymbols() {return allSym;}
	public int getMaxIndex() {
		return allDates.size();
	}
	
	public double getValue(String sym,int index){
		if (index<0) return 0;
		if (index>=allDates.size()) return 0;
		String d=(String)allDates.elementAt(index);
		Trip t=null;
		boolean found=false;
		for (int a=0;a<allValues.size();a++) {
			t=(Trip)(allValues.elementAt(a));
			if (t.sym.equals(sym) && t.date.equals(d)) {
				found =true;break;
			}
		}
		if (!found && index<getMaxIndex()-1) return getValue(sym,index+1);
		if (t==null) return 0;
		if (original || single) {
			return t.val;
		}
		int maxindex=getMaxIndex()-1;
		String relStr="aapl";
		if (dataList!=null) relStr=(String)dataList.getSelectedItem();
		double relValStart=getRawValue(relStr,maxindex);
		double relVal=getRawValue(relStr,index);
		double currRelVal=relVal/relValStart;
		double startVal=getRawValue(sym,maxindex);
		double indexVal=t.val;
		double indexRelVal=(t.val/startVal-currRelVal)*100;
		double indexPerVal=(t.val/startVal)*100;
		//		return yd.getVal(original,single,percent,relitive,startVal,currRelVal);
		
		if (relitive) {
			return indexRelVal;//res=t.val/startVal-currRelVal;
		}
		if (percent) {
			return indexPerVal;
		}
		else {
			System.out.println(";MyError: invalid state");
			return 0;
		}
	}

	public double getRawValue(String sym,int index){
		if (index<0) index=0;
		if (index>=allDates.size()-1) index=allDates.size()-1;
		String d=(String)allDates.elementAt(index);
		for (int a=0;a<allValues.size();a++) {
			Trip t=(Trip)(allValues.elementAt(a));
			if (t.sym.equalsIgnoreCase(sym) && t.date.equalsIgnoreCase(d)) {
			//	System.out.println("Raw Value of "+sym+" at "+index+" is "+t.val);
				return t.val;
			}
		}
		if (index<getMaxIndex()-1) {
			return getValue(sym,index+1);
		}
		for (int a=allDates.size()-1;a>=0;a--) {
			String dd=(String)allDates.elementAt(a);
			for (int b=0;b<allValues.size();b++) {
				Trip t=(Trip)(allValues.elementAt(b));
				if (t.sym.equalsIgnoreCase(sym) && t.date.equalsIgnoreCase(dd)) {
				//	System.out.println("Raw Value of "+sym+" at "+index+" is "+t.val);
					return t.val;
				}
			}			
		}
		System.out.println("missed lookup FULL MISS for "+d+" with sym "+sym);
		return 1;
	}

}

class Trip {
	double val;
	String sym;
	String date;
	
	public Trip(String d,String s,double v){ val=v;sym=s;date=d;}
}