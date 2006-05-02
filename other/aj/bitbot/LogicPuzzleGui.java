/*
 * Created on May 1, 2006
 *
 */
package aj.bitbot;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class LogicPuzzleGui extends JPanel{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame jf=new JFrame();
		jf.setSize(300,400);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		LogicPuzzleGui lp=new LogicPuzzleGui();
//		jf.getContentPane().setLayoutManager(new BorderLayout());
		jf.getContentPane().add("Center",lp);
		jf.setVisible(true);
		// TODO Auto-generated method stub

	}
	
	LogicPuzzle lp=null;
	public LogicPuzzleGui() {
		lp=new LogicPuzzle(50,50,110);
		lp.setGates();
		lp.show();
	}

	public void paint(Graphics g) {
		g.setColor(Color.white);
		g.clearRect(0,0,getWidth(),getHeight());

		String grid[][]=lp.getGrid();
		int BOARDER=20;
		int dw=(getWidth()-BOARDER)/grid[0].length;
		int dh=(getHeight()-BOARDER)/grid.length;
		g.translate(BOARDER/2,BOARDER/2);
		for (int a=0;a<grid.length;a++) {
			for (int b=0;b<grid[a].length;b++) {
				g.translate(b*dw,a*dh);
					drawOpen(g,dw,dh,grid[a][b]);
				g.translate(-b*dw,-a*dh);
			}
		}
	}

	private void drawOpen(Graphics g, int width,int height,String shape) {
		Color outline=Color.black;
		Color wire=Color.GRAY;
		if (shape==LogicPuzzle.OPEN) {
			g.setColor(wire);
			g.drawLine(-width/2,0,width/2,0);
		}
		else if (shape==LogicPuzzle.CLOSED) {
			g.setColor(wire);
			g.drawLine(-width/2,0,0,0);
			g.drawLine(0,-height/2,0,+height/2);
			g.drawLine(width/8,-height/4,width/8,height/4);
			g.drawLine(width/4,-height/8,width/4,height/8);
		}
		else if (shape==LogicPuzzle.BRANCH) {
			g.setColor(wire);
			g.drawLine(-width/2,0,width/2,height);
			g.drawLine(-width/2,0,width/2,-height);
//			g.drawLine(i+width/2,j+height,i+width,j+height);
//			g.drawLine(i,j,i+width/2,j-height);
//			g.drawLine(i+width/2,j-height,i+width,j-height);
		}
		else if (shape==LogicPuzzle.AND || shape==LogicPuzzle.OR || shape==LogicPuzzle.NOR || shape==LogicPuzzle.XOR || shape==LogicPuzzle.NAND) {
			g.setColor(wire);
			g.fillArc(-width/2,-height/2,width/2+1,height+1,-90,180);
			if (shape==LogicPuzzle.OR || shape==LogicPuzzle.NOR || shape==LogicPuzzle.XOR ) {
				g.setColor(Color.white);
				g.fillArc(-width/2,-height/2,width/3,height,-90,180);
			}
			g.setColor(outline);
			g.drawArc(-width/2,-height/2,width/2,height,-90,180);
			if (shape!=LogicPuzzle.OR && shape!=LogicPuzzle.NOR && shape!=LogicPuzzle.XOR ) {
				g.drawLine(-width/4,-height/2,-width/4,height/2);
			}
			else {
				g.drawArc(-width/2,-height/2,width/3,height,-70,140);
			}

			if (shape==LogicPuzzle.NOR || shape==LogicPuzzle.NAND) {
				g.setColor(wire);
				g.fillOval(0,-height/6,width/12+1,height/3+1);
				g.setColor(outline);
				g.drawOval(0,-height/6,width/12,height/3);
			}
			if (shape==LogicPuzzle.XOR) {
				g.drawArc(-width/2-width/24,-height/2,width/3,height,-70,140);				
			}
			g.setColor(wire);
			g.drawLine(-width/2,-height,-width/3,-height/3);
			g.drawLine(-width/3,-height/3,-width*1/8,-height/3);
			g.drawLine(-width/2,height,-width/3,height/3);
			g.drawLine(-width/3,height/3,-width*1/8,height/3);
			g.drawLine(0,0,width/2,0);
//			g.drawOval(-width/2,-height/2,width,height);

		}
	}
}
