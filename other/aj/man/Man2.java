/*
 * Created on Feb 2, 2006
 *
 */
package aj.man;

import java.awt.*;

/**
 * @author judda
 *
 */
public class Man2 {
	
	/*           (6)
	 *    o   (3)(5)(4)
	 *   /|\     (2)
	 *   / \  (0)   (1)
	 */
	int STAND=0;
	int posAllX[][]={{-10,10,0,-10,10,0,0}};
	int posAllY[][]={{ 0,0,-15, -15,-15,-30,-40}};
	
	int posx[]={-0 ,  20,  0, -10, 10,  0,  0};
	int posy[]={ 0 , -15,-15, -15,-15,-30,-40};
	
	int startpos[];
	int endpos[];
	long starttime;
	long endtime;
	
	public void draw(Graphics g) {
		g.drawLine(posx[0],posy[0],posx[2],posy[2]);//legleft
		g.drawLine(posx[1],posy[1],posx[2],posy[2]);//leftright
		g.drawLine(posx[2],posy[2],posx[5],posy[5]);//body
		g.drawLine(posx[3],posy[3],posx[5],posy[5]);//leftarm
		g.drawLine(posx[4],posy[4],posx[5],posy[5]);//rightarm
		int head=posy[5]-posy[6];
		System.out.println("head="+head);
		g.drawOval(posx[5]-head/2,posy[5]-head,head,head);//head
	}

}
