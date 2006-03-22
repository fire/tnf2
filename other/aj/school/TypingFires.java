/*
 * Created on Jan 11, 2006
 *
 */
package aj.school;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import aj.misc.Bnf;

/**
 * @author judda
 *
  */

/*
 * progress
 *   #1 sign in by name
 *   #2 begin at level 1
 *   #3 advance after X (about 10) tries.  if too much time or errors, return later, after 3 advance return required auto return
 *   rewards (every 10 minutes = 10 minutes fun)
 *      replay show earned
 *      save show (best time)
 *      free shoot - 100  (any left, any right move, space shoot?)
 *      mouse shoot - 100
 *      dodge bombs - 100
 */
//TODO menu - sign in
//TODO save score - name:level:errors:time:randomseed
//TODO make words
//TODO make levels
//TODO make rewards
//TODO   save show - from random seed (random delay)


public class TypingFires extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int size=300;
	private Vector fireWorkList=new Vector();
	static int numFireWorks=20;
	static double gravity=.0001;
	static double launchspeed=.18;
	static int MAXRAD=6;
	static int REFRESH=1000/30;

	
	JLabel follow=new JLabel();
	JTextField type=new JTextField();
	String followText=null;

	int launchCount=0;
	boolean doNotResetLevel=true;//false;
	Image i=null;

	private void resetLevel() {
		doNotResetLevel=true;
		launchCount=0;
		String gml="gmlfile [" +
		"game1 \"<level1><level1><level1><level1><level1><level1><level1><level1><level1><level1><level1>\""+
		"game2 \"<level2><level2><level2><level2><level2><level2><level2><level2><level2><level2><level2>\""+
		"game3 \"<level3><level3><level3><level3><level3><level3><level3><level3><level3><level3><level3>\""+
		"game4 \"<level4><level4><level4><level4><level4><level4><level4><level4><level4><level4><level4>\""+
		"level1 \"<left1><left1><left1><left1> \"" +
		"level1 \"<left1><left1><left1><left1><left1> \"" +
		"level2 \"<right1><right1><right1><right1> \"" +
		"level2 \"<right1><right1><right1><right1><right1> \"" +
		"level3 \"<level1>\"" +
		"level3 \"<level2>\"" +
		"level4 \"<homekey><homekey><homekey><homekey> \"" +
		"level4 \"<homekey><homekey><homekey><homekey><homekey> \"" +
		"homekey \"<left1>\"" +
		"homekey \"<right1>\"" +		
		"left1 \"s\"" +
		"left1 \"d\"" +
		"left1 \"f\"" +
		"right1 \"j\"" +
		"right1 \"k\"" +
		"right1 \";\"" +
		"right1 \"l\"" +
		//level5 words  sad dad mad lad jad sds jkj klk asa sas sds l;l;
		//level6 left2 qwertg
		//words
		//level7 right2 hyuiop
		//words
		//level8 left3 zxcvb
		//words
		//level9 right3 bnm,.
		//words
		//with capitals
		//level10 nums1 12345
		//level11 nums2 67890
		"rightintro \"asdf asdf asdf fdsa fdsa fsda adsf\"" +
		"rightintro \"jkl; jkl; ;lkj ;lkj ;ljk ;ljk ;klj\"" +
//		"
		" ]";
		Bnf bnf;
		try {
			bnf = new aj.misc.Bnf(aj.misc.GmlPair.parse(gml));
			followText=bnf.process("<game3>");
			follow.setText(followText);
			try {
				type.getDocument().remove(0,type.getDocument().getLength());
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	


	public static void main(String args [])  {
		JFrame jf=new JFrame();
		final TypingFires tf=new TypingFires();
		jf.getContentPane().add("Center",tf);
		JPanel keys=new JPanel(new BorderLayout());
		tf.type.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				tf.doMatch();
			}
			public void removeUpdate(DocumentEvent e) {
				tf.doMatch();
			}
			public void changedUpdate(DocumentEvent e) {
			}});
		Font font=new Font("Courier", Font.PLAIN, 35);
		tf.follow.setFont(font);
		font=new Font("Courier", Font.PLAIN, 2);
		tf.type.setFont(font);
//		keys.add("North",tf.follow);
		jf.getContentPane().add("North",tf.follow);
		keys.add("South",tf.type);
		jf.getContentPane().add("South",keys);
		jf.pack();
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tf.resetLevel();
		FireWork.setRecreate(false);
		for (int a=0;a<numFireWorks;a++) {
			tf.addFireWork();
		}
	}
	
	private void doMatch() {
		String match[]=aj.misc.Stuff.getTokens(type.getText()," ");
		String look[]=aj.misc.Stuff.getTokens(followText," ");
		int count=0;
		String space="&nbsp;";
		String res="<html><body>";
		doNotResetLevel=false;
		for (int a=0;a<match.length && a<look.length;a++) {
			if (look[a].equals(match[a])) {
				count++;
				res+="<b>"+look[a]+"</b>"+space;
//				System.out.println("word "+a+" match!");
			}
			else {
				doNotResetLevel=true;
//				System.out.println("word "+a+" nomatch!");
				for (int b=0;b<look[a].length();b++) {
					if (match[a].length()>b && look[a].charAt(b)!=match[a].charAt(b)) {
//						System.out.println("letter "+b+" nomatch!("+look[a].charAt(b)+")");
						res+="<strike><font color=red>"+look[a].charAt(b)+"</font></strike>";
					}
					else if (match.length>a+1 && match[a].length()<b) {
						res+="<strike><font color=red>"+look[a].charAt(b)+"</font></strike>";					
					}
					else {
//						System.out.println("match.len="+match[a].length());
//						System.out.println("look.len="+look[a].length());
//						System.out.println("letter "+b+" match!("+match[a].charAt(b)+")");
						if (b<match[a].length())	
							res+="<b>"+look[a].charAt(b)+"</b>";
						else
							res+=""+look[a].charAt(b)+"";
					}
				}
				res+=""+space;
			}
		}
		for (int a=0;a<look.length-match.length;a++) {
			doNotResetLevel=true;
			res+=look[a+match.length]+""+space;
		}
		if (match.length>look.length) doNotResetLevel=false;
		if (match.length==look.length && match[match.length-1].length()>=look[match.length-1].length()) doNotResetLevel=false;
		res+="</body></html>";
		follow.setText(res);
		if (count>launchCount) {
			launchCount=count;
			int cc=(int)(Math.random()*4+2);
			for (int a=0;a<cc;a++) addFireWork();
		}
	}
	
	public TypingFires() {
		setPreferredSize(new Dimension(size*3/2,size));
		final TypingFires ft=this;
		new Thread() {
			public void run() {
				while (true) {
					try {
						long usedTime=0;
						Thread.sleep(REFRESH-usedTime);
						long stime=System.currentTimeMillis();
						moveAll();
						usedTime=System.currentTimeMillis()-stime;
						if (!doNotResetLevel) {
							resetLevel();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ft.repaint();
				}
			}
		}.start();
	}

	private void addFireWork() {
		FireWork fw=new FireWork();
		fw.setRandomStart(size,size*3/2,launchspeed/4,launchspeed,MAXRAD);
		fireWorkList.addElement(fw);		
//		try {
//			Thread.sleep((int)(Math.random()*30));
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
	
	private void moveAll() {
		for (int a=0;a<fireWorkList.size();a++) {
			FireWork fw=(FireWork)fireWorkList.elementAt(a);
			fw.move();
			if (fw.expired()) fireWorkList.removeElement(fw);
		}
		Vector v=(Vector)fireWorkList.clone();
		for (int a=0;a<fireWorkList.size();a++) {
			FireWork fw=(FireWork)fireWorkList.elementAt(a);
			if (fw.dy>0 && !fw.hasExploded() && !fw.isExplodedPart()) {
				Vector vv=fw.explode();
				v.addAll(vv);
//				v.removeElement(fw);
			}
		}
		fireWorkList=v;
	}
	
	public void paint(Graphics g) {
		int x=this.getWidth();
		int y=this.getHeight();
		if (i==null) {
			i=this.createImage(size,size*3/2);
		}
		Graphics G=i.getGraphics();
		G.setColor(Color.black);
		G.fillRect(0,0,i.getWidth(this),i.getHeight(this));
		for (int a=0;a<fireWorkList.size();a++) {
			FireWork fw=(FireWork)fireWorkList.elementAt(a);
			fw.draw(G);
		}
		g.drawImage(i,0,0,x,y,this);
	}
}
