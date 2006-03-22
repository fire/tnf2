/*
 * Created on Jan 31, 2006
 *
 */
package aj.testing;

/**
 * @author judda
 *
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FractPict2 extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int xsize=256,ysize=256;
	static int minFocus=256;
	static JButton jb=new JButton("Next");
	
	public static void main(String[] args) {
		JFrame jf=new JFrame("FractPict2");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(new Dimension(xsize,ysize));
		FractPict2 fp=new FractPict2();
		jf.getContentPane().add("Center",fp);
		jf.getContentPane().add("North",jb);
		jf.setVisible(true);		
	}
	
	public FractPict2() {
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (minFocus==0) return;
				minFocus/=2;
				redraw();
				repaint();
				}});
	}
	
	public void redraw() {
		if (i==null) return;
		Graphics G=i.getGraphics();
		G.setColor(Color.black);
		G.clearRect(0,0,xsize,ysize);
		for (int f=xsize;f>minFocus;f/=2) {
			for (int xx=0;xx<xsize/f;xx++) {
				if (xx%2==0) {
//					System.out.println("skip row"+xx);
					continue;
				}
				for (int yy=0;yy<ysize/f;yy++) {		
					if (yy%2==0) {
//						System.out.println("skip col"+yy);
						continue;
					}
					all[xx*f][yy*f]=blend(xx*f,yy*f,f);
					all[xx*f][yy*f]=Color.black;
					G.setColor(all[xx*f][yy*f]);
//					G.fillRect(xx*f-f,yy*f-f,xx*f,yy*f);
					G.drawRect(xx*f,yy*f,xx*f+f,yy*f+f);
				}
			}
		}
	}
	
	Image i=null;
	Color[][] all;
	
	public void paint(Graphics g) {
		if (i==null) {
			i=this.createImage(xsize,ysize);
			if (all==null) {
				all=new Color[xsize][];
				for (int xx=0;xx<xsize;xx++) {
					all[xx]=new Color[ysize];
					for (int a=0;a<ysize;a++) {
//						all[xx][a]=new Color(xx%256*xx/xsize,xx%256*xx/xsize,xx%256*xx/xsize);
						all[xx][a]=randColor();
					}
				}
			}
			redraw();
		}
		g.drawImage(i,0,0,this.getWidth(),this.getHeight(),null);
	}

	public Color randColor() {
		return (Math.random()<.5?Color.white:Color.black);
//		return new Color((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256));
	}
	
	public Color blend(int x,int y,int f) {
//		System.out.println("blend called "+x+" "+y+" "+f);
		Color c1,c2,c3,c4;
		if (x-f<0 ) c1=randColor();
		else c1=all[x-f][y];
		if (x+f>xsize-1 ) c2=randColor();
		else c2=all[x+f][y];
		if (y-f<0 ) c3=randColor();
		else c3=all[x][y-f];
		if (y+f>ysize-1) c4=randColor();
		else c4=all[x][y+f];

		double r1=Math.random();
		double r2=Math.random();
		double r3=Math.random();
		double r4=Math.random();
		double l=r1+r2+r3+r4;
		r1=r1/l;
		r2=r2/l;
		r3=r3/l;
		r4=r4/l;
		int r=(int)(c1.getRed()*r1  +c2.getRed()*r2  +c3.getRed()*r3  +c4.getRed()*r4);
		int g=(int)(c1.getGreen()*r1+c2.getGreen()*r2+c3.getGreen()*r3+c4.getGreen()*r4);
		int b=(int)(c1.getBlue()*r1 +c2.getBlue()*r2 +c3.getBlue()*r3 +c4.getBlue()*r4);
		all[x][y]=new Color(r,g,b);
//		all[x][y]=c2;
		return all[x][y];
	}
}
