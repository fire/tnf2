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
	
	int posAllX[][]={};
	int posAllY[][]={};
	
//	int STAND=0;
//	int posx[]={-8, 8  , -7 ,  7,    0,  -7,   7,  -8,   8, 0,  0};
//	int posy[]={ 0, 0 , -10 ,-10 , -25, -27, -27,  -20,-20, -40,-50};
	int STAND=0;
	int posx[]={-8, 8  , -7 ,  7,    0,  -7,   7,  -8,   8, 0,  0};
	int posy[]={ 0, 0 , -10 ,-10 , -25, -27, -27,  -20,-20, -40,-50};
	
	int startpos[];
	int endpos[];
	long starttime;
	long endtime;
	
	public void draw(Graphics g) {
		g.drawLine(posx[0],posy[0],posx[2],posy[2]);//legleft
		g.drawLine(posx[1],posy[1],posx[3],posy[3]);//leftright
		g.drawLine(posx[2],posy[2],posx[4],posy[4]);//legleft
		g.drawLine(posx[3],posy[3],posx[4],posy[4]);//leftright
		g.drawLine(posx[4],posy[4],posx[9],posy[9]);//body
		g.drawLine(posx[5],posy[5],posx[7],posy[7]);//leftarm
		g.drawLine(posx[6],posy[6],posx[8],posy[8]);//rightarm
		g.drawLine(posx[5],posy[5],posx[9],posy[9]);//leftarm
		g.drawLine(posx[6],posy[6],posx[9],posy[9]);//rightarm
		int head=posy[9]-posy[10];
//		System.out.println("head="+head);
		g.drawOval(posx[9]-head/2,posy[9]-head,head,head);//head
	}

}


/*
 * stand
 * walk
 * kick high
 * kick low
 * punch high
 * punch low
 * block high
 * block low
 * trip (fall feet)
 * dive (fall head back)
 * walk
 * climb
 * duck 
 * jump up
 * jump accross
 * 
 *    o  
 *   /|\
 *   / \
*/
