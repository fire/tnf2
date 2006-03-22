package aj.gnubi;

/*
 * Created on Jun 29, 2003
 * 
 * @author winme
 *
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTextField;

public class Player extends JComponent implements MapItem, Movable, KeyListener  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int viewSize=12;
	Point cellPos=null;
	ImageIcon i=null;

	public int getViewSize() {return viewSize;}

	
	
	public Player() {
		setCellPos(2,2);
        i=new ImageIcon(Block.class.getResource("images/player.gif"));
//		try {
//			URL u=Block.class.getResource("player.gif");
//			InputStream i=u.openStream();
//			while(true) {
//				int c=i.read();
//				if (c==-1) break;
//				System.out.println("reading player.gif");
//			}
//		} catch (Exception e) {
//		}
	}

	public Point getCellPos() {return cellPos;}
	public int getCellX() {return (int)cellPos.getX();}
	public int getCellY() {return (int)cellPos.getY();}
	public void setCellPos(int x,int y) {cellPos=new Point(x,y);}
	public void setCellPos(Point p) {cellPos=p;}
	double xpos=.5,ypos=0;//0 center -.5 +.5 is moved max
	public double getXPos(){return xpos;}
	public double getYPos(){return ypos;}

	public void paint(Graphics g,double dw,double dh) {
//System.out.println("Paint player");
		if (i==null || g==null) return;
		Image img=i.getImage();
		g.setColor(Color.red);
		g.fillRect(0,0,(int)dw,(int)dh);
//		g.drawImage(img,(int)(dw*xpos),(int)(dh*ypos),(int)dw,(int)dh,null);
		g.drawImage(img,0,0,(int)dw,(int)dh,this);
	}


	String typed="";
	String pressed="";
	public void keyTyped(KeyEvent arg0) {
		typed+=(""+arg0.getKeyChar()).toUpperCase();
		JTextField tf=(JTextField)arg0.getSource();
		tf.setText("");
		//System.out.println("KEY FOUND!");
		//Main.dis.redraw();
		//Main.dis.repaint();
	}

	public void keyPressed(KeyEvent arg0) {
		//pressed+=(""+arg0.getKeyChar()).toUpperCase();
		//System.out.println("KEY FOUND!");
		
	}

	public void keyReleased(KeyEvent arg0) {
		//String c=(""+arg0.getKeyChar()).toUpperCase();
		//if (pressed.indexOf(c)>=0) {
			//pressed=pressed.substring(0,pressed.indexOf(c))+pressed.substring(pressed.indexOf(c)+1);
		//}
		//System.out.println("KEY FOUND!");
		
	}
	
	double xv=0,yv=0;
	double MAXVEL=.25;
	double ACCELL=.05;
	public void doMove() {
		if (typed.indexOf("L")>=0 || pressed.indexOf("L")>=0) xv=Math.max(0,xv+ACCELL); 
		if (typed.indexOf("H")>=0 || pressed.indexOf("H")>=0) xv=Math.min(0,xv-ACCELL); 
		if (typed.indexOf("K")>=0 || pressed.indexOf("K")>=0) yv=Math.min(0,yv-ACCELL);
		if (typed.indexOf("J")>=0 || pressed.indexOf("J")>=0) yv=Math.max(0,yv+ACCELL);
		yv=Math.max(-1*MAXVEL,Math.min(MAXVEL,yv));
		xv=Math.max(-1*MAXVEL,Math.min(MAXVEL,xv));
		xpos+=xv;ypos+=yv;
		if (xpos>.5){xpos=xpos-1;setCellPos((int)(cellPos.getX()+1),(int)(cellPos.getY()));}
		if (xpos<-.5){xpos=xpos+1;setCellPos((int)(cellPos.getX()-1),(int)(cellPos.getY()));}
		if (ypos>.5){ypos=ypos-1;setCellPos((int)(cellPos.getX()),(int)(cellPos.getY()+1));}
		if (ypos<-.5){ypos=ypos+1;setCellPos((int)(cellPos.getX()),(int)(cellPos.getY()-1));}
		typed="";
	}
	
//	up = k,arrow
//	down = j,arrow
//	right = l,arrow
//	left = h,arrow
//	jump = space
//	attack = esc,enter

}
