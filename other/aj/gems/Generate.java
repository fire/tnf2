/*
 * Created on Nov 30, 2005
 *
 */
package aj.gems;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author judda
 *
  */
public class Generate extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static void main(String[] args) {
		JFrame jf=new JFrame("Generate Gems");
		Generate g=new Generate();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.getContentPane().add("Center",g);
		jf.pack();
//		jf.setSize(280,280);
//		m.setSize(280,280);
		jf.setVisible(true);
	}
	
	int GEMSIZE=48;
	public Dimension getPreferredSize() {
		return new Dimension(GEMSIZE*2,GEMSIZE*2);
	}
	
	public void paint(Graphics g) {
		drawSquare(g,Color.red,GEMSIZE/2,GEMSIZE);
		g.translate(GEMSIZE,GEMSIZE);
		drawDiamond(g,Color.blue,GEMSIZE,GEMSIZE);
		g.translate(0,-GEMSIZE);
		drawHex(g,Color.green,GEMSIZE,GEMSIZE/2);
		g.translate(-GEMSIZE,GEMSIZE);
		drawNavet(g,Color.magenta,GEMSIZE,GEMSIZE/2);
	}
	
	public static void drawNavet(Graphics g,Color c,int h,int w) {
		if (h>w) {
			g.translate((int)((h-w)/2),0);
		}
		if (w>h) {
			g.translate(0,(int)((w-h)/2));			
		}
		int xs[]={0,(int)(w/2),w,(int)(w/2)};
		int ys[]={(int)(h/2),0,(int)(h/2),h};
		g.setColor(c.darker());
		g.fillPolygon(xs,ys,xs.length);
		g.setColor(c);
		g.drawPolygon(xs,ys,xs.length);
		g.drawLine((int)(w/2),0,(int)(w/2),h);
		g.drawLine(0,(int)(h/2),w,(int)(h/2));

		int xxs[]={(int)(1.0*w/4),(int)(1.0*w/2),(int)(1.0*w*3/4),(int)(1.0*w/2)};
		int yys[]={(int)(1.0*h/2),(int)(1.0*h/4),(int)(1.0*h/2),(int)(1.0*h*3/4)};
//		int xxs[]={(int)(w/4),(int)(w*3/4),(int)(w*3/4),(int)(w/4)};
//		int yys[]={(int)(h/4),(int)(h/4),(int)(h*3/4),(int)(h*3/4)};
		g.setColor(c.darker());
		g.fillPolygon(xxs,yys,xxs.length);
		g.setColor(c);
		g.drawPolygon(xxs,yys,xxs.length);
//		g.drawLine((int)(1.0*w/2),(int)(1.0*h/4),(int)(1.0*w/2),(int)(1.0*h*3/4));
//		g.drawLine((int)(1.0*w/4),(int)(1.0*h/2),(int)(1.0*w*3/4),(int)(1.0*h/2));
		
		if (h>w) {
			g.translate(-(int)((h-w)/2),0);
		}
		if (w>h) {
			g.translate(0,-(int)((w-h)/2));			
		}
	}
	
	public static void drawHex(Graphics g,Color c,int w,int h) {
		if (h>w) {
			g.translate((int)((h-w)/2),0);
		}
		if (w>h) {
			g.translate(0,(int)((w-h)/2));			
		}
		int xs[]={0,(int)(1.0*w/3),(int)(1.0*w*2/3),w,(int)(1.0*w*2/3),(int)(1.0*w/3)};
		int ys[]={(int)(1.0*h/2),h,h,(int)(1.0*h/2),0,0};
		g.setColor(c.darker());
		g.fillPolygon(xs,ys,xs.length);
		g.setColor(c);
		g.drawPolygon(xs,ys,xs.length);
		g.drawLine((int)(1.0*w/3),0,(int)(1.0*w*2/3),h);
		g.drawLine((int)(1.0*w*2/3),0,(int)(1.0*w/3),h);
		g.drawLine(0,(int)(1.0*h/2),w,(int)(1.0*h/2));

		int xxs[]={(int)(w/4),(int)(w/3),(int)(w*2/3),(int)(w*3/4),(int)(w*2/3),(int)(w/3)};
		int yys[]={(int)(h/2),(int)(h*3/4),(int)(h*3/4),(int)(h/2),(int)(h/4),(int)(h/4)};
		g.setColor(c.darker());
		g.fillPolygon(xxs,yys,xxs.length);
		g.setColor(c);
		g.drawPolygon(xxs,yys,xxs.length);
		
		if (h>w) {
			g.translate(-(int)((h-w)/2),0);
		}
		if (w>h) {
			g.translate(0,-(int)((w-h)/2));			
		}
	}
	
	public static void drawDiamond(Graphics g,Color c,int w,int h) {
		if (h>w) {
			g.translate((int)((h-w)/2),0);
		}
		if (w>h) {
			g.translate(0,(int)((w-h)/2));			
		}
		int xs[]={w/4,0,w/2,w,w*3/4};
		int ys[]={0,h/3,h,h/3,0};
		g.setColor(c.darker());
		g.fillPolygon(xs,ys,xs.length);
		g.setColor(c);
		g.drawPolygon(xs,ys,xs.length);
		g.drawLine(0,h/3,w,h/3);
		g.drawLine(w/4,0,w/2,h/3);
		g.drawLine(w*3/4,0,w/2,h/3);
		int xxs[]={0,w*2/5,w/2,(int)(w*(1-1.0*2/5)),w};
		int yys[]={h/3,h/2,h/3,h/2,h/3};
		g.drawPolyline(xxs,yys,xxs.length);
		g.drawLine(w*2/5,h/2,w/2,h);
		g.drawLine((int)(w*(1-1.0*2/5)),h/2,w/2,h);
		if (h>w) {
			g.translate(-(int)((h-w)/2),0);
		}
		if (w>h) {
			g.translate(0,-(int)((w-h)/2));			
		}
	}

	public static void drawSquare(Graphics g,Color c,int w,int h) {
		if (h>w) {
			g.translate((int)((h-w)/2),0);
		}
		if (w>h) {
			g.translate(0,(int)((w-h)/2));			
		}
		int xs[]={0,w,w,0};
		int ys[]={0,0,h,h};
		g.setColor(c.darker());
		g.fillPolygon(xs,ys,4);
		g.setColor(c);
		g.drawPolygon(xs,ys,4);
		g.drawLine(w,0,0,h);
		g.drawLine(0,0,w,h);
		int xxs[]={(int)(w/4),(int)(w*3/4),(int)(w*3/4),(int)(w/4)};
		int yys[]={(int)(h/4),(int)(h/4),(int)(h*3/4),(int)(h*3/4)};
		g.setColor(c.darker());
		g.fillPolygon(xxs,yys,4);
		g.setColor(c);
		g.drawPolygon(xxs,yys,4);

		
		if (h>w) {
			g.translate(-(int)((h-w)/2),0);
		}
		if (w>h) {
			g.translate(0,-(int)((w-h)/2));			
		}
	}
}
