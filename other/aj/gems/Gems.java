/*
 * Created on Nov 27, 2005
 *
 */
package aj.gems;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * @author judda
 */

public class Gems extends JPanel { 
	public static int GOODSOUND=0;
	public static int WINSOUND=1;
	public static int INTROSOUND=2;
	public static int BADSOUND=3;
	
	static private JTextField scoreTextField=new JTextField(10);
	static private JButton restart=new JButton("Reset");
	static private JFrame jf=new JFrame("Gems");
	static Color diffColors[][] ={ 
			{Color.red,Color.blue,Color.green},
			{Color.red,Color.blue,Color.green,Color.cyan},
			{Color.red,Color.blue,Color.green,Color.cyan,Color.magenta},
			{Color.red,Color.blue,Color.green,Color.cyan,Color.magenta,Color.orange}, 
			{Color.red,Color.blue,Color.green,Color.cyan,Color.magenta,Color.orange,Color.gray} 
		};
	static private int gameDifficultyList[];
	static {
		gameDifficultyList=new int[diffColors.length];
		for (int a=0;a<diffColors.length;a++) {
			gameDifficultyList[a]=diffColors[a].length;
		}
		Cell.allColor=diffColors[0];
	}

	private boolean soundOn=false;//true;
	private Image back;
	private Vector backIconImages=null;//new Vector();
	private AudioClip winsound=null;
	private String soundFile[]={
			"buyitem.wav",
			"applaus2.wav",
			"gennie.wav",
			"splat.wav"};
	private AudioClip audioClip[];
	private Cell c[][]=null;
	private int MINGROUPCELLS=2;
	private int ICONSIZE=36;
	private int BORDERSIZE=20;
	private int FALLTIME=250;	
	private int emptyColCount=0;
	private int score=0;
	private Image i=null;
	private String currentPlayer=null;
	private long stime=0;
	private String gameDifficultyNameList[]={"Easy","Medium","Hard","Super","Crazy"};
	private int gameSizeList[]={10,15,20,25};
	private int GRIDSIZE=gameSizeList[0];
	private int difficulty=gameDifficultyList[0];
	private boolean grow=false;
	private String gameTypeList[]={"Grab-it","Flip-it","Memory"};
	private String gameName=gameTypeList[0];
	
	private String getGameType() {
		String dif=gameDifficultyNameList[0];
		for (int a=0;a<gameDifficultyList.length;a++) {
			if (gameDifficultyList[a]==difficulty) dif=gameDifficultyNameList[a];
		}
		return gameName+":"+dif+":"+GRIDSIZE+":"+grow;
	}
	
	private void playSound(int x) {
		if (soundOn)
		audioClip[x].play();
	}

	private void loadAudio() {
		audioClip=new AudioClip[soundFile.length];
		Class c=Gems.class;
		for (int a=0;a<soundFile.length;a++) {
			audioClip[a] = Applet.newAudioClip(c.getResource(soundFile[a]));
		}
	}

	private void loadNewBackground() {
		if (backIconImages==null) {
			backIconImages=new Vector();
			File f=new File(Gems.backImagesFolder);
			File ff[]=f.listFiles();
			for (int a=0;ff!=null && a<ff.length;a++) {
				if (ff[a].getAbsolutePath().toString().indexOf("Thumb")>=0) continue;
				ImageIcon jb=new ImageIcon(ff[a].getAbsolutePath());
				backIconImages.addElement(jb.getImage());		
			}
		}
		if (backIconImages.size()==0) return;
		back=(Image)backIconImages.elementAt((int)(Math.random()*backIconImages.size()));
	}
	
	private void showHighScoreDialog() {
			final JDialog jd=new JDialog(jf,"High Scores");

			jd.getContentPane().setLayout(new BorderLayout());
			Vector v=Score.getKnownUsers();
//			JPanel jp=new JPanel(new FlowLayout());
			JTable jt=new JTable(Score.getAllData(getGameType()),Score.getColNames());
			JTable pjt=new JTable(Score.getUserData(getGameType(),this.currentPlayer),Score.getColNames());
			
			jd.getContentPane().add("North",new JScrollPane(jt));
			jd.getContentPane().add("South",new JScrollPane(pjt));

			jd.pack();//(new Dimension(150,100));//();
			jd.setLocationRelativeTo(null);
			jd.setModal(true);
			jd.setVisible(true);
	}
	
	public static String scoreFile="c:/.gemscores.txt";
	public static String backImagesFolder="c:/.gembackgrounds/";
	
	public static void readConfig(String file) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			while (true) {
				String s=br.readLine();
				if (s==null) break;
				if (s.toUpperCase().startsWith("SCOREFILE:")) {
					scoreFile=s.substring("SCOREFILE:".length());
				}
				if (s.toUpperCase().startsWith("BACKIMAGES:")) {
					backImagesFolder=s.substring("BACKIMAGES:".length());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void readArgs(String s[]) {
		for (int a=0;a<s.length;a++) {
			if (s[a].indexOf("?")>=0 || s[a].equalsIgnoreCase("--help")) {
				System.out.println("Format java aj.gems.Gems [options] \n" +
						"  --help this\n" +
						"  --?    this\n" +
						"  -c<configfile>\n" +
						"      SCOREFILE:\n" +
						"      BACKIMAGES:");
			}
			else if (s[a].toUpperCase().startsWith("-C")) {
				readConfig(s[a].substring(2));
			}
		}
	}
	
	public static void main(String[] args) {
		if (args.length>0) {
			readArgs(args);
		}
		final Gems m=new Gems();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.getContentPane().add("Center",m);
		JPanel scorePanel=new JPanel(new FlowLayout());
		jf.getContentPane().add("North",scorePanel);
		scorePanel.add(new JLabel("Score:"));
		scorePanel.add(scoreTextField);
		scoreTextField.setEditable(false);
		scorePanel.add(restart);
		JMenuBar menuBar=new JMenuBar();
		JMenu menu;
		JMenuItem menuItem;
		menu=new JMenu("Settings");
		menuBar.add(menu);
		jf.setJMenuBar(menuBar);
		menuItem=new JMenu("Game");
		menu.add(menuItem);
			ButtonGroup group=new ButtonGroup();
			JMenuItem subItem;
			for (int a=0;a<m.gameTypeList.length;a++) {
				subItem=new JRadioButtonMenuItem(m.gameTypeList[a]);group.add(subItem);
				if (a==0) subItem.setSelected(true);
				menuItem.add(subItem);
				final String subGameType=m.gameTypeList[a];
				subItem.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						m.setGameType(subGameType);
					}});				
			}

		menuItem=new JMenu("Difficulty");
		menu.add(menuItem);
			group=new ButtonGroup();
			for (int a=0;a<m.gameDifficultyNameList.length;a++) {
				subItem=new JRadioButtonMenuItem(""+m.gameDifficultyNameList[a]);group.add(subItem);
				if (a==0) subItem.setSelected(true);
				menuItem.add(subItem);
				final String subval=m.gameDifficultyNameList[a];
				subItem.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						m.setGameDifficulty(subval);
					}});				
			}
			
		menuItem=new JMenu("Size");
		menu.add(menuItem);
			group=new ButtonGroup();
			for (int a=0;a<m.gameSizeList.length;a++) {
				subItem=new JRadioButtonMenuItem(""+m.gameSizeList[a]);group.add(subItem);
				if (a==0) subItem.setSelected(true);
				menuItem.add(subItem);
				final String subval=""+m.gameSizeList[a];
				subItem.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						m.setGameSize(subval);
					}});				
			}
		menuItem=new JRadioButtonMenuItem("Growth");
		menuItem.setSelected(m.grow);
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				m.grow=!m.grow;
				m.setGameOver(false);
			}});
		menu.add(menuItem);
		menuItem=new JRadioButtonMenuItem("Sound");
		menuItem.setSelected(m.soundOn);
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				m.soundOn=!m.soundOn;
			}});
		menu.add(menuItem);
		menuBar.add(menu);
		menu=new JMenu("Help");
		menuItem=new JMenuItem("High Scores");
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				m.showHighScoreDialog();
			}});
		menuItem=new JMenuItem("About");
		menu.add(menuItem);
		menuBar.add(menu);
		jf.pack();
		jf.setVisible(true);
		restart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				m.setGameOver(false);
		}});
	}
	
	private void setGameType(String s) {
		gameName=s;
		i=null;
		setGameOver(false);
	}

	private void setGameDifficulty(String x) {
		for (int a=0;a<gameDifficultyNameList.length;a++) {
			if (gameDifficultyNameList[a].equals(x)) {
				difficulty=gameDifficultyList[a];
				Cell.allColor=diffColors[a];
			}
		}
		i=null;
		setGameOver(false);
	}
	
	private void setGameSize(String x) {
		GRIDSIZE=Integer.parseInt(x);
//		size=GRIDSIZE;
		i=null;
		setGameOver(false);
	}

	public Dimension getPreferredSize() {
		return new Dimension(GRIDSIZE*ICONSIZE+BORDERSIZE*2,GRIDSIZE*ICONSIZE+BORDERSIZE*2);
	}

	public Gems() {
		loadNewBackground();
		loadAudio();
		final Gems m=this;
		doRandomCellPlacement();
		setGameOver(false);
		new Thread() {
			public void run() {
				int delay=40;
				while (true) {
					long stime=System.currentTimeMillis();
					m.repaint();
					try {
//						System.out.println(delay-(System.currentTimeMillis()-stime));
						Thread.sleep(delay-(System.currentTimeMillis()-stime));
					} catch (Exception e) {}
				}
			}}.start();
			
		this.addMouseListener(new MouseListener(){
				public void mouseExited(MouseEvent me) {}
				public void mousePressed(MouseEvent me) {
					m.doClickAction(me.getClickCount(),me.getPoint());
				}
				public void mouseEntered(MouseEvent me) {}
				public void mouseClicked(MouseEvent me) {
				}
				public void mouseReleased(MouseEvent me) {}
		});
	}

	private void doRandomCellPlacement() {		
		c=new Cell[GRIDSIZE][];
		for (int a=0;a<GRIDSIZE;a++) {
			c[a]=new Cell[GRIDSIZE];
			for (int b=0;b<GRIDSIZE;b++) {
				c[a][b]=new Cell();
				c[a][b].SIZE=ICONSIZE;
				c[a][b].set((int)(Math.random()*GRIDSIZE),(int)(Math.random()*GRIDSIZE));
				c[a][b].setMove(a,b,1000);
			}
		}
	}

	public Point getGridPosFromClickPos(Point pos) {
		int rx=this.getWidth();
		int ry=this.getHeight();
		int ax=this.getPreferredSize().width;
		int ay=this.getPreferredSize().height;
		double xs=1.0*rx/ax;
		double ys=1.0*ry/ay;
		//rescale to true pos
		pos=new Point((int)(pos.x/xs),(int)(pos.y/ys));
		// cut border boundary
		pos=new Point(pos.x-BORDERSIZE,pos.y-BORDERSIZE);
		// div by iconsize
//		pos=new Point((pos.x-ICONSIZE/2)/ICONSIZE,(pos.y-ICONSIZE/2)/ICONSIZE);
		pos=new Point((pos.x)/ICONSIZE,(pos.y)/ICONSIZE);
		if (pos.x<0) pos=new Point(0,pos.y);
		if (pos.y<0) pos=new Point(pos.x,0);
		if (pos.x>=GRIDSIZE) pos=new Point(GRIDSIZE-1,pos.y);
		if (pos.y>=GRIDSIZE) pos=new Point(pos.x,GRIDSIZE-1);
		return pos;	
	}
	
	private void addCellGroupScore(int x) {
		score+=x*100;
		if (x>3) score+=100*(x*.10*(2+x-3));
		scoreTextField.setText(score+"");
	}
	
	public void doClickAction(int count,Point pos) {
		if (stime==0) {
			stime=System.currentTimeMillis();
			System.out.println("0 time found");
		}
		Point pp=getGridPosFromClickPos(pos);
		Vector all=getCellGroupsAtPoint(pp);
		if (all.size()<MINGROUPCELLS) {
			playSound(Gems.BADSOUND);
			return;
		}
		else {
			playSound(Gems.GOODSOUND);
			addCellGroupScore(all.size());
			for (int a=0;a<all.size();a++) {
				Point p=(Point)all.elementAt(a);
				c[p.x][p.y].empty=true;
			}
			moveAllCellsDownIntoEmpties();
			all=getEmptyCols();
			for (int a=0;a<all.size()-emptyColCount+1;a++) {
				slideCellsIntoCenterEmptyColumbs(all);
				all=getEmptyCols();			
			}
			emptyColCount=all.size();
			int cc=getAllCellGroups(MINGROUPCELLS);
			if (cc==0) {
				setGameOver(true);
			}
			if (grow) {
				for (int x=0;x<GRIDSIZE;x++) {
					for (int y=0;y<GRIDSIZE;y++) {
						c[x][y].empty=false;

					}
				}
			}
		}
	}

	int getCurrentColorCount() {
		Vector v=new Vector();
		for (int a=0;a<GRIDSIZE;a++) {
			for (int b=0;b<GRIDSIZE;b++) {
				if (c[a][b].empty) continue;
				if (!v.contains(c[a][b].c)) v.add(c[a][b].c);
			}
		}
		return v.size();
	}
	
	int getCurrentCellCount() {
		int res=0;
		for (int a=0;a<GRIDSIZE;a++) {
			for (int b=0;b<GRIDSIZE;b++) {
				if (c[a][b].empty) continue;
				res++;
			}
		}
		return res;
	}
	
	private void showSaveScoreWindow() {
		final JDialog jd=new JDialog(jf,"Save Score");
		jd.getContentPane().setLayout(new BorderLayout());
		Vector v=Score.getKnownUsers();
		if (currentPlayer==null && v.size()>0) {
			currentPlayer=(String)v.elementAt(0);
		}
		JPanel jp=new JPanel(new FlowLayout());
		final int colorsLeft=getCurrentColorCount();
		final int cellsLeft=getCurrentCellCount();
		final long date=new Date().getTime();
		final int dtime=(int)(System.currentTimeMillis()-stime);

		jp.add(new JLabel("Score="+score+" "));
		jp.add(new JLabel("Colors Left="+colorsLeft+" "));
		jp.add(new JLabel("Cells Left="+cellsLeft+" "));
		jp.add(new JLabel("Time="+dtime/1000.0+" "));
		jp.add(new JLabel("Rank"));
		final JLabel rank=new JLabel("99th   99th");
		jp.add(rank);
		rank.setEnabled(false);
		jd.getContentPane().add("North",jp);
		jp=new JPanel(new FlowLayout());
		jp.add(new JLabel("Name"));
		final JComboBox userList=new JComboBox(v);		
		if (currentPlayer!=null) {
			userList.setSelectedIndex(v.indexOf(currentPlayer));
		}
		rank.setText(Score.getRank(getGameType(),currentPlayer,score));
		userList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer=(String)userList.getSelectedItem();
				rank.setText("<html><font size=4>"+Score.getRank(getGameType(),currentPlayer,score)+"</font></html>");
				
			}});
		jp.add(userList);
		JButton saveButton=new JButton("Save");
		if (v.size()==0) saveButton.setEnabled(false);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jd.setVisible(false);
				currentPlayer=(String)(userList.getSelectedItem());
				Score.create(currentPlayer,date,getGameType(),score,dtime,colorsLeft,cellsLeft,null);
				Score.saveFile();
			}});
		jp.add(saveButton);
		jd.getContentPane().add("Center",jp);
		jp=new JPanel(new FlowLayout());
		JButton newButton=new JButton("New User");
		jp.add(newButton);
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jd.setVisible(false);
				showNewUserDialog(date,getGameType(),score,dtime,colorsLeft,cellsLeft,null);
			}
		});
		jd.getContentPane().add("South",jp);
		jd.pack();//(new Dimension(150,100));//();
		jd.setLocationRelativeTo(null);
		jd.setModal(true);
		jd.setVisible(true);
	}

	public void showNewUserDialog(final long date, final String gametype, final int score, final int dtime, final int colorsLeft, final int cellsLeft, final String other) {
		final JDialog jd=new JDialog(jf,"Save Score");
		jd.getContentPane().setLayout(new BorderLayout());
		final JTextField nameField=new JTextField(12);
		JButton createButton=new JButton("Create");
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jd.setVisible(false);
				currentPlayer=nameField.getText();
				Score.create(currentPlayer,date,getGameType(),score,dtime,colorsLeft,cellsLeft,null);
				Score.saveFile();
				jd.setVisible(false);
			}});
		jd.getContentPane().add("North",nameField);
		jd.getContentPane().add("South",createButton);
		jd.pack();
		jd.setLocationRelativeTo(null);
		jd.setModal(true);
		jd.setVisible(true);
	}
	
	private void setGameOver(boolean x) {
		if (x) {
			playSound(Gems.WINSOUND);
			restart.setText("Restart");
			restart.setBackground(Color.green);
			showSaveScoreWindow();
		}
		else {
			restart.setText("Reset");
			restart.setBackground(Color.red);
			playSound(Gems.INTROSOUND);
			Cell.newShapes();
			loadNewBackground();
		}
		stime=System.currentTimeMillis();
		score=0;
		addCellGroupScore(0);
		doRandomCellPlacement();
	}

	private int getAllCellGroups(int x) {
		int count=0;
		for (int a=0;a<GRIDSIZE;a++) {
			for (int b=0;b<GRIDSIZE;b++) {
				Vector v=this.getCellGroupsAtPoint(new Point(a,b));
				if (v.size()>=x) count++;
			}
		}
		return count;
	}
	
	private void slideCellsIntoCenterEmptyColumbs(Vector v) {
		for (int a=0;a<v.size();a++) {
			Point p=(Point)v.elementAt(a);
			for (int b=0;b<GRIDSIZE;b++)  {
				if (p.x<GRIDSIZE/2+1) {
					this.moveCellsRightIntoEmpties(new Point(p.x,b));
				}
				else {
					this.moveCellLeftIntoEmties(new Point(p.x,b));
				}
				
			}
			v=getEmptyCols();
		}
	}
	
	private Vector getEmptyCols() {
		Vector res=new Vector();
		for (int a=0;a<GRIDSIZE;a++) {
			if (c[a][0].empty && c[a][GRIDSIZE-1].empty) {
				res.addElement(new Point(a,0));
			}
		}
		return res;
	}

	private void moveAllCellsRightIntoEmpties() {
		for (int a=1;a<GRIDSIZE;a++) {
			for (int b=0;b<GRIDSIZE;b++) {
				if (c[a][b].empty && !c[a-1][b].empty) 
					moveCellsRightIntoEmpties(new Point(a,b));
			}
		}
	}

	private void moveAlLCellsLeftIntoEmpties() {
		for (int a=GRIDSIZE-2;a>=0;a--) {
			for (int b=0;b<GRIDSIZE;b++) {
				if (c[a][b].empty && !c[a+1][b].empty) 
					moveCellLeftIntoEmties(new Point(a,b));
			}
		}
	}
	
	private void moveAllCellsDownIntoEmpties() {
		for (int b=0;b<GRIDSIZE;b++) {
			for (int a=0;a<GRIDSIZE;a++) {
				if (c[a][b].empty && (b-1<0 || !c[a][b-1].empty)) 
					moveCellsDownInto(new Point(a,b));
			}
		}
	}
	
	private void moveAllCellsUpIntoEmpties() {
		for (int b=GRIDSIZE-2;b>=0;b--) {
			for (int a=0;a<GRIDSIZE-1;a++) {
				if (c[a][b].empty && !c[a][b+1].empty) 
					moveCellsUpIntoEmpties(new Point(a,b));
			}
		}
	}
	
	private void moveCellsRightIntoEmpties(Point p) {
//		System.out.println("move right called "+p.x+" "+p.y);
		Cell t=c[p.x][p.y];
		for (int a=p.x-1;a>=0;a--) {
			c[a][p.y].setMove(a+1,p.y,FALLTIME);	
			c[a+1][p.y]=c[a][p.y];
		}
		c[0][p.y]=t;
		c[0][p.y].set(-1,p.y);
		c[0][p.y].setMove(0,p.y,FALLTIME);
	}
	
	private void moveCellLeftIntoEmties(Point p) {
//		System.out.println("move left called "+p.x+" "+p.y);
		Cell t=c[p.x][p.y];
		for (int a=p.x+1;a<GRIDSIZE;a++) {
			c[a][p.y].setMove(a-1,p.y,FALLTIME);	
			c[a-1][p.y]=c[a][p.y];
		}
		c[GRIDSIZE-1][p.y]=t;
		c[GRIDSIZE-1][p.y].set(GRIDSIZE,p.y);
		c[GRIDSIZE-1][p.y].setMove(GRIDSIZE-1,p.y,FALLTIME);
	}


	private void moveCellsDownInto(Point p) {
//		System.out.println("move down called "+p.x+" "+p.y);
		Cell t=c[p.x][p.y];
		for (int a=p.y-1;a>=0;a--) {
			if (a<0) continue;
			c[p.x][a].setMove(p.x,a+1,FALLTIME);	
			c[p.x][a+1]=c[p.x][a];
		}
		c[p.x][0]=t;
		c[p.x][0].set(p.x,-1);
		c[p.x][0].setMove(p.x,0,FALLTIME);
	}
	
	private void moveCellsUpIntoEmpties(Point p) {
		Cell t=c[p.x][p.y];
		for (int a=p.y+1;a<GRIDSIZE;a++) {
			c[p.x][a].setMove(p.x,a-1,FALLTIME);	
			c[p.x][a-1]=c[p.x][a];
		}
		c[p.x][GRIDSIZE-1]=t;
		c[p.x][GRIDSIZE-1].set(p.x,GRIDSIZE);
		c[p.x][GRIDSIZE-1].setMove(p.x,GRIDSIZE-1,FALLTIME);
	}
	
	public void paint(Graphics g) {
		if (i==null) {
			int x=this.getPreferredSize().width;
			int y=this.getPreferredSize().height;
			i=this.createImage(x,y);
		}
		
		Graphics G=i.getGraphics();
		G.clearRect(0,0,this.getPreferredSize().width,this.getPreferredSize().height);
		G.drawImage(back,0,0,this.getPreferredSize().width,this.getPreferredSize().height,this);
		G.translate(BORDERSIZE,BORDERSIZE);
		for (int a=0;a<c.length;a++) {
			for (int b=0;b<c[a].length;b++) {
				c[a][b].paint(G);
			}
		}
		g.drawImage(i,0,0,this.getWidth(),this.getHeight(),null);
	}	
	

	public Vector getCellGroupsAtPoint(Point pos) {
		int res=0;
		Vector all=new Vector();
		if (c[pos.x][pos.y].empty) return all;
		all.addElement(pos);
		Color col=c[pos.x][pos.y].c;
		for (int a=0;a<all.size();a++) {
			int b=1;
			Point tt=(Point)all.elementAt(a);
			int mx=tt.x+b,my=tt.y;
			if (mx>=0 && my>=0 && mx<GRIDSIZE && my<GRIDSIZE &&
					c[mx][my].c==col && !c[mx][my].empty && 
					!all.contains(new Point(mx,my))) {
				all.addElement(new Point(mx,my));
			}
			mx=tt.x;my=tt.y+b;
			if (mx>=0 && my>=0 && mx<GRIDSIZE && my<GRIDSIZE &&
					c[mx][my].c==col && !c[mx][my].empty &&  
					!all.contains(new Point(mx,my))) {
				all.addElement(new Point(mx,my));
			}
					
			b=-1;
			mx=tt.x+b;my=tt.y;
			if (mx>=0 && my>=0 && mx<GRIDSIZE && my<GRIDSIZE &&
					c[mx][my].c==col && !c[mx][my].empty && 
					!all.contains(new Point(mx,my))) {
				all.addElement(new Point(mx,my));
			}
			mx=tt.x;my=tt.y+b;
			if (mx>=0 && my>=0 && mx<GRIDSIZE && my<GRIDSIZE &&
					c[mx][my].c==col && !c[mx][my].empty &&  
					!all.contains(new Point(mx,my))) {
				all.addElement(new Point(mx,my));
			}			
		}
		return all;
	}
}

//Goal
//pop it 2 in a row block game
//rotate for 3 in a row block game
//memory select
//patern change toggle
//group drag shift

//options
//  gravity down,up,left,right
//  squeze left,right,top,bottem,horizon,middle

//groups connect cells
//select display
//back ground images

