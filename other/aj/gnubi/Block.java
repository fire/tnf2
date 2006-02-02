package aj.gnubi;

/*
 * Created on Jun 29, 2003
 * * @author winme
 *
 */
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class Block extends JComponent implements MapItem{
	ImageIcon i=null;

	public Block() {
        	i=new ImageIcon(Block.class.getResource("images/block.gif"));
//			try {
//				URL u=Block.class.getResource("images/player.gif");
//				InputStream i=u.openStream();
//				while(true) {
//					int c=i.read();
//					if (c==-1) break;
//					System.out.println("reading player.gif");
//				}
//			} catch (Exception e) {
//			}
	}

	public Dimension getPreferredSize(){return new Dimension(i.getIconWidth(),i.getIconHeight());}

	public void paint(Graphics g,double dw,double dh) {
		if (g==null || i==null) return;
		Image img=i.getImage();
		if (img==null) return;
		g.drawImage(img,0,0,(int)dw,(int)dh,this);
	}

	Point pos;

	public double getXPos(){return 0;}
	public double getYPos(){return 0;}
	public Point getCellPos() {return pos;}
	public int getCellX() {return (int)pos.getX();}
	public int getCellY() {return (int)pos.getY();}
	public void setCellPos(Point p) {pos=p;}
	public void setCellPos(int x,int y) {pos=new Point(x,y);}

	public boolean solid=false;
	public void setSolid(boolean b) {solid=b;}
	public boolean isSolid() {return solid;}

	public static void main(String s[]) {
		JFrame jf=new JFrame();
		jf.setSize(new Dimension(600,400));
		Block b=new Block();
		Block b2=new Block();
		b2.setBounds(50,50,50,50);
		b.setBounds(0,0,50,50);
		jf.getContentPane().setLayout(null);
		jf.getContentPane().add(b);
		jf.getContentPane().add(b2);
		jf.setVisible(true);
	}
}
