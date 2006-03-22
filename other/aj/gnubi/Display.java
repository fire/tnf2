package aj.gnubi;

import java.awt.Graphics;
import java.awt.Image;
import java.util.Vector;

import javax.swing.JPanel;

public class Display extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int viewSize=6;
	Player player=new Player();

	public Display() {}

	Image buffer=null;

	public void redraw() {
		if (buffer==null || buffer.getWidth(this)!=getWidth() || buffer.getHeight(this)!=getHeight()) {
			buffer=createImage(getWidth(),getHeight());
		}
		double dw=getWidth()/viewSize;
		double dh=getHeight()/viewSize;
		Graphics G=buffer.getGraphics();
//		if (G==null) return;
		G.clearRect(0,0,getWidth(),getHeight());
		G.translate(getWidth()/2,getHeight()/2);
		int cx=Gnubi.player.getCellX(),cy=Gnubi.player.getCellY();
		Vector v=Gnubi.mapCell.getAllBlocks(cx,cy,Gnubi.player.getViewSize());
		for (int a=0;a<v.size();a++) {
			Block b=(Block)v.elementAt(a);
			int xp=b.getCellX()-cx,yp=b.getCellY()-cy;
			G.translate((int)(xp*dw-(dw*Gnubi.player.xpos)),(int)(yp*dh-(dh*Gnubi.player.ypos)));
			b.paint(G,dw,dh);
			G.translate((int)(-xp*dw+(dw*Gnubi.player.xpos)),(int)(-yp*dh+(dh*Gnubi.player.ypos)));
//			G.translate((int)(-xp*dw),(int)(-yp*dh));
		}	
		Gnubi.player.paint(G,dw,dh);
	}
	public void update(Graphics g) {paint(g);}
	public void paint(Graphics g) {
		if (g==null) return;
		if (buffer==null) { redraw();
		}
		g.drawImage(buffer,0,0,getWidth(),getHeight(),null);
	}
}
