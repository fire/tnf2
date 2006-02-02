package aj.robot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import aj.misc.Stuff;

/**
 *@author     judda 
 *@created    April 12, 2000 
 */

public class Arena extends JPanel implements ActionListener , Runnable, ChangeListener {
	static int MAXAGE=3;
	static int MAXROBOTAGE=1;
	static int MAXCANNONAGE=7;
	static int MAXSCANAGE=2;
	static int MAXEXPLOSIONAGE=2;
	static int BORDER=40;

	public double FRAMESTEP=1;
	static int countScaler=40;//.01 time units per update
	double SCALE=1.0/20;
	public double getScale(){return SCALE;}
	
	double STEP=.2;
	int SLEEPTIME=100;
	Vector list = new Vector();
	double maxtime=0;
	boolean go=true;

	double count=0;
	public double getArenaTime() {return count;}
	public void setArenaTime(double d) {count=d;}

	JButton stop=new JButton("Stop");
	JButton restart=new JButton("Continue");
	JSlider frameRate,arenaTime;
	JTextField frameRateText=new JTextField(5),arenaTimeText=new JTextField(5);
	JComboBox zoomMode;
	JComboBox centerMode;

	public static void main(String sss[]) {
		if (sss.length != 1) {
			System.out.println("FORMAT aj.robot.Arean <tracefile>");
			return;
		}
		Arena arena=new Arena();
		arena.readFile(sss[0]);
		JFrame JF=new JFrame("C++ robot trace viewer");
		JF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
		JPanel jp=arena.setUpGUI();
		JF.getContentPane().add("Center",jp);
		JF.setSize(400, 400);
		JF.setVisible(true);
		new Thread(arena).start();
	}

	public JPanel setUpGUI() {
		String zoomOptions[]={"Arena","Battle","Personal"};
		zoomMode=new JComboBox(zoomOptions);
		zoomMode.setSelectedIndex(0);
		Vector botList=new Vector();
		for (int a=0;a<bots.size();a++) {
			Robot r=(Robot)bots.elementAt(a);
			if (!botList.contains(r.getName())) botList.addElement(r.getName());
		}
		centerMode=new JComboBox(botList);
		centerMode.setSelectedIndex(0);
		JPanel jp=new JPanel(new BorderLayout());
//		jp.add("Center",this);
		JScrollPane zp=new JScrollPane(this);
		jp.add("Center",zp);

		JPanel control=new JPanel(new FlowLayout());
		stop.addActionListener(this);
		restart.addActionListener(this);
		restart.setEnabled(false);
		control.add(zoomMode);
		control.add(centerMode);
		
		control.add(stop);
		control.add(restart);
		jp.add("South",control);

		frameRate = new JSlider(JSlider.HORIZONTAL,1, 100, 50);
		frameRate.addChangeListener(this);		
	        frameRate.setMajorTickSpacing(20);
       		frameRate.setMinorTickSpacing(1);
		setSpeed(50);

		arenaTime = new JSlider(JSlider.HORIZONTAL,0, (int)(maxtime*countScaler), 0);
		arenaTime.addChangeListener(this);		
	        arenaTime.setMajorTickSpacing(countScaler*10);
       		arenaTime.setMinorTickSpacing(countScaler);

		JPanel top=new JPanel(new BorderLayout());
		JPanel tt=new JPanel(new FlowLayout());
		tt.add(new JLabel("FrameRate"));
		tt.add(frameRate);
		tt.add(frameRateText);
		top.add("North",tt);
		tt=new JPanel(new FlowLayout());
		tt.add(new JLabel("ArenaTime"));
		tt.add(arenaTime);
		tt.add(arenaTimeText);
		top.add("South",tt);
		jp.add("North",top);
		return jp;
	}

	public void setSpeed(int x) {
		frameRateText.setText(""+x);
		if (x>100) x=100;
		if (x<0) x=0;
		
		SLEEPTIME = (int)(200*((100-x)/100.0));
		FRAMESTEP = x/100.0;
	}

	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		if (frameRate==source) {
			int speed = (int)source.getValue();
			setSpeed(speed);
		}
		if (arenaTime==source) {
			int c = (int)source.getValue()/countScaler;
			count =c ;
			if (!running && count<maxtime) {
				stop.setText("Restart");
				restart.setEnabled(true);
			}
		}
	}

	boolean running=true;
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==restart) {
			running=!running;
			if (running) {
				stop.setText("Stop   ");
				restart.setEnabled(false);
			}
		}
		if (ae.getSource()==stop) {
			running=!running;
			if (running) {
				stop.setText("Stop   ");
				restart.setEnabled(false);
				count=0;
            			arenaTime.setValue((int)(count*countScaler));
            			arenaTimeText.setText(""+(int)(count*countScaler));
				
			}
			else {
				stop.setText("Restart");
				restart.setEnabled(true);
			}
		}
	}


	public void run() {
		long t=System.currentTimeMillis();
		while (true) {
			try {
				Thread.sleep(SLEEPTIME);
			} catch (Exception e) {
			}
			if (running) {
				count+=FRAMESTEP;
				if (count>maxtime)  {
					count=maxtime;
					stop.setText("Restart");
					running=false;
				}
				int c=(int)arenaTime.getValue();
            			if (c/countScaler<(int)count-1) arenaTime.setValue((int)(count*countScaler));
            			if (c/countScaler<(int)count-1) arenaTimeText.setText(""+(int)(count*countScaler));
			}
			repaint();
		}
	}

	Image I=null;
	Dimension oldsize=null;	
	//public void repaint(Graphics g) {
		//paint(g);
	//}
	public void paint(Graphics g) {
		if (g==null) return;
		if (I == null || oldsize==null || oldsize.height!=getSize().height || oldsize.width!=getSize().width) {
			Image iii= createImage (getSize().width, getSize().height);
			oldsize=getSize();
			I=iii;
		}
		Graphics G = I.getGraphics();
		G.clearRect (0, 0, getSize().width, getSize().height);
		Dimension d = getSize();
		G.translate(BORDER,BORDER);
		double dw = d.width-BORDER*2;
		double dh = d.height-BORDER*2;
		//System.out.println("screen w="+dw+" h="+dh);

		double minx=-1,maxx=-1,miny=-1,maxy=-1;
		boolean first=true;
		for (int a=0;a<list.size();a++) {
			ArenaItem ai=(ArenaItem)list.elementAt(a);
			if (ai instanceof Robot && ((Robot)(ai)).isVisible()) {
				if (first) {
					maxx=minx=ai.x;
					maxy=miny=ai.y;
					first=false;
				}
				minx=Math.min(minx,ai.x);
				miny=Math.min(miny,ai.y);
				maxx=Math.max(maxx,ai.x);
				maxy=Math.max(maxy,ai.y);
			}
		}
		//System.out.println("delta x="+dx+" dy="+dy+" actual width");
		//System.out.println("scale ="+SCALE+" or "+(1/SCALE)+" points per pixel");
		int cx=(int)(maxx+minx)/2,cy=(int)(maxy+miny)/2;
		if (zoomMode.getSelectedItem()==null || zoomMode.getSelectedItem().equals("Arena")) {
			cx=5000;cy=5000;
			minx=000;miny=000;
			maxx=10000;maxy=10000;
			//minx=1000;miny=1000;
			//maxx=9000;maxy=9000;
		}
		else if (zoomMode.getSelectedItem().equals("Battle")) {
		}
		else if (zoomMode.getSelectedItem().equals("Personal")) {
			String centerBot=(String)centerMode.getSelectedItem();
			for (int a=0;a<list.size();a++) {
				ArenaItem ai=(ArenaItem)list.elementAt(a);
				if (ai instanceof Robot && ((Robot)ai).isVisible() && (centerBot==null || ((Robot)ai).getName().equals(centerBot))) {
					Robot r=(Robot)ai;
					minx=r.x-600;
					maxx=r.x+600;
					miny=r.y-600;
					maxy=r.y+600;
					cx=(int)(r.x);cy=(int)(r.y);
				}
			}
		}


		double dx=maxx-minx;
		double dy=maxy-miny;
		SCALE=Math.min(dw/dx,dh/dy);

		int rcx=(int)(cx*SCALE),rcy=(int)(cy*SCALE);
		G.translate((int)(dw/2-rcx),(int)(dh/2-rcy));

		//System.out.println("new center cx="+cx*SCALE+" cy="+cy*SCALE);
//translate to center
		//G.translate((int)(-minx*SCALE),(int)(-miny*SCALE));
		int mm=(int)(10000*SCALE);
		G.setColor(Color.darkGray);
		G.drawLine(0,0,0,mm);
		G.drawLine(0,mm,mm,mm);
		G.drawLine(mm,mm,mm,0);
		G.drawLine(mm,0,0,0);
		for (int a=0;a<50+1;a++) {
			int m2=mm/50;
			if (m2<10) break;
			G.setColor(Color.cyan);
			G.drawLine(0,a*m2,mm,a*m2);
			G.drawLine(a*m2,0,a*m2,mm);
		}
		for (int a=0;a<10+1;a++) {
			int m2=mm/10;
			if (m2<10) break;
			G.setColor(Color.gray);
			G.drawLine(0,a*m2,mm,a*m2);
			G.drawLine(a*m2,0,a*m2,mm);
		}

		for (int a=0;a<list.size();a++) {
			ArenaItem ai=(ArenaItem)list.elementAt(a);
			ai.display(G);
		}
		g.drawImage (I, 0, 0, this);
	}


	Vector bots = new Vector();
	public void readFile(String fname) {
		//readfile ->displayitem list
		try {
			BufferedReader BR = new BufferedReader(new FileReader(fname));
			do {
				String s = BR.readLine();
				if (s==null) break;
				s=s.trim();
				String m[] = Stuff.getTokens(s);
				if (s.toUpperCase().indexOf("NAME")>=0 || m.length<2 || s.indexOf("---")>=0 || s.toUpperCase().indexOf("LOG")>=0) {
					continue;
				}
				try {
					maxtime=Stuff.parseDouble(m[1]);
				} catch (NumberFormatException nfe){ 
				}
				if (s.indexOf("Time=") >= 0) {
					//Time= 179.34490 (next two lines)
					bots.removeAllElements();
				}
				if (m.length>9 && m.length<14) {
						Robot r = new Robot(s,this);
						r.setTime(maxtime);
						list.addElement(r);
						bots.addElement(r);
				}
				if (s.indexOf("Explosion at") >= 0) {
					//  B Explosion at 9755,3996
						Explosion r = new Explosion(s,this);
						r.setTime(maxtime);
						list.addElement(r);
						continue;
				}
				if (s.indexOf("cannon(") >= 0) {
					//  B  178.35 cannon( 80,999) returns 1
					Cannon r = new Cannon(s,this);
					int q;
					for (q = 0; q < bots.size(); q++) {
						//resolve to robot x,y at time.
						Robot rr = (Robot) bots.elementAt(q);
						if (rr.getId().equalsIgnoreCase(r.getId())) {
							;
							r.setX(rr.getX(1));
							r.setY(rr.getY(1));
						}
					}
					r.setTime(maxtime);
					list.addElement(r);
				}
				if (s.indexOf("drive(") >= 0) {
				}
				//  A  178.55 drive(226,10) returns 1
				if (s.indexOf("scan(") >= 0) {
					//  A  178.42 scan(226,10) returns 0
					Scan r = new Scan(s,this);
					int q;
					for (q = 0; q < bots.size(); q++) {
						//resolve to robot avg x,y at time.
						Robot rr = (Robot) bots.elementAt(q);
						if (rr.getId().equals(r.getId())) {
							r.setX(rr.getX(1));
							r.setY(rr.getY(1));
						}
					}
					r.setTime(maxtime);
					list.addElement(r);
				}
			} while (true);
		}
		catch (IOException E) {
			System.out.println("myError: Bad file.");
			System.exit(0);
		}
		System.out.println("read "+list.size()+" Arena Items.  Found "+bots.size()+" robots.");
	}

	public static Color fade(Color c, double currTime, double eventTime,int maxAge) {
		double realage=(currTime-eventTime)/maxAge;
		double age=1-realage;
		age=age*age;
		//if (realage>.25) age=.5;
		//else if (realage>.5) age=.25;
		//else if (realage>1) age=.125;
		//else age=.0125;
		if (age>1) age=1;
		if (age<0) age=0;
		int r=(int)(c.getRed()*age+Color.lightGray.getRed()*(1-age));
		int g=(int)(c.getGreen()*age+Color.lightGray.getGreen()*(1-age));
		int b=(int)(c.getBlue()*age+Color.lightGray.getBlue()*(1-age));
		return new Color(r,g,b);
	}
}
