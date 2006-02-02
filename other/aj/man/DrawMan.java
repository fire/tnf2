package aj.man;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import aj.awt.SimpleWindowManager;

public class DrawMan extends Canvas implements KeyListener{
	public static void main(String s[]) { 
		Frame f=new Frame();
		f.add("Center",new DrawMan());
		f.setSize(new Dimension(400,250));
		f.setVisible(true);
		f.addWindowListener(new SimpleWindowManager());
	}

	public DrawMan() {
		addKeyListener(this);
	}

	public void keyPressed (KeyEvent e) {
	}
	
	public void keyReleased (KeyEvent e) {
	}

	

	public void keyTyped (KeyEvent e) {
		char c=e.getKeyChar();
		if (c=='k' || c=='K' || c=='8')  m.posPartUp();
		if (c=='j' || c=='J' || c=='2')  m.posPartDown();
		if (c=='l' || c=='L' || c=='6')  m.posPartRight();
		if (c=='h' || c=='H' || c=='4')  m.posPartLeft();
		if (c==' ' || c=='\t' ) m.posPartSwitch();
		if (c=='\n' || c=='\r') {m=m.next();}
		if (c=='n' || c=='N')  {ref=m.pos;m.createPos();}
		if (c=='s' || c=='S')  {m.saveAllPos();ref=new Pos(m.pos);}
		if (c=='q' || c=='Q' ||
		    c=='x' || c=='X')  System.exit(0);
		repaint();
	}

	Man m=new Man(1);
	Pos ref=new Pos(m.pos);

	public void paint(Graphics g) {
		for (int a=0;a<10;a++) {
			m.x=50+25*a;m.y=50;
			m.draw(g);
			Man mm=new Man(ref);
			Man mmm=Man.between(m.pos,mm.pos,a/9.0);
			m.x=50+25*a;m.y=100;
			mm.x=50+25*a;mm.y=100;
			mmm.x=50+25*a;mmm.y=150;
			if (m.pos.id<1) {
				m.draw(g);
				mm.draw(g);
				mmm.draw(g);
			}
			

		}
	}
}
