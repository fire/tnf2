/*
 * Created on Nov 28, 2005
 *
 
 */
package aj.gems;

import java.awt.Color;
import java.awt.Graphics;

/**
 * @author judda
 *
 */
public class Cell {
	int startx,starty;
	int destx,desty;
	long destTime;
	long startTime;
	Color c;
	boolean moving=false;
	boolean empty=false;
	boolean selected=false;
	
	double rotate=Math.PI*2*Math.random();
	
	double sparkle=-1;
	int sparklex,sparkley;
	
	int SIZE=20;
	static Color allColor[]=Gems.diffColors[0]; 
	
	public Cell() {}

	static int MAXSHAPECOUNT=9;
	static int allShape[]=new int[12];
	static {
		for (int a=0;a<12;a++) {
			allShape[a]=(int)(Math.random()*MAXSHAPECOUNT);
		}
	}
	
	public static void newShapes() {
		for (int a=0;a<12;a++) {
			allShape[a]=(int)(Math.random()*MAXSHAPECOUNT);
		}		
	}
	
	public void paint(Graphics g) {
		if (empty) return;
		int drawx=destx,drawy=desty;
		if ((startx==destx && starty==desty) || System.currentTimeMillis()>destTime) {
			startx=destx;
			starty=desty;
			moving=false;
		}
		else {
			moving=true;
			long dt=destTime-startTime;
			long curr=-System.currentTimeMillis()+destTime;
//			System.out.println("time left="+1.0*curr/dt);
//			System.out.println("");
			drawx=(int)(destx-(destx-startx)*(1.0*curr/dt));
			drawy=(int)(desty-(desty-starty)*(1.0*curr/dt));
		}
		g.translate(drawx,drawy);

		for (int a=0;a<allColor.length;a++) {
			if (c==allColor[a]) {
				int s1=SIZE-2;
				int s2=SIZE*2/3-2;
				switch (allShape[a]) {
					case 0: Generate.drawHex(g,c,s1,s1);break;
//					case 1: Generate.drawHex(g,c,s2,s1);break;
					case 1: Generate.drawHex(g,c,s1,s2);break;
					case 2: Generate.drawDiamond(g,c,s1,s1);break;
//					case 4: Generate.drawDiamond(g,c,s2,s1);break;
					case 3: Generate.drawDiamond(g,c,s1,s2);break;
					case 4: Generate.drawSquare(g,c,s1,s1);break;
					case 5: Generate.drawSquare(g,c,s2,s1);break;
					case 6: Generate.drawSquare(g,c,s1,s2);break;
					case 7: Generate.drawNavet(g,c,s1,s1);break;
					case 8: Generate.drawNavet(g,c,s2,s1);break;
					default: Generate.drawNavet(g,c,s1,s2);
				}
			}
		}
//		if (c==allColor[0])
//			Generate.drawHex(g,c,SIZE-2,SIZE-2);
//		if (c==allColor[1])
//			Generate.drawDiamond(g,c,SIZE-2,SIZE-2);
//		if (c==allColor[2])
//			Generate.drawSquare(g,c,SIZE-2,SIZE-2);
//		if (c==allColor[3])
//			Generate.drawNavet(g,c,SIZE-2,SIZE-2);
		g.translate(-drawx,-drawy);

		int BLINKRATE=150;
		if (sparkle==-1 && Math.random()*BLINKRATE<1) {
			sparkle=1;
			sparklex=(int)(Math.random()*SIZE/2);
			sparkley=(int)(Math.random()*SIZE/2);
		}
		int BLINK=3;
		if (sparkle>BLINK) sparkle=-1;
		if (sparkle>0) {
			sparkle+=Math.random()/2;
			g.setColor(Color.white);
			g.drawLine((int)(drawx-SIZE/2+sparklex-sparkle%BLINK),(int)(drawy-SIZE/2+sparkley        ),(int)(drawx-SIZE/2+sparklex+sparkle%BLINK),(int)(drawy-SIZE/2+sparkley));
			g.drawLine((int)(drawx-SIZE/2+sparklex        ),(int)(drawy-SIZE/2+sparkley-sparkle%BLINK),(int)(drawx-SIZE/2+sparklex        ),(int)(drawy-SIZE/2+sparkley+sparkle%BLINK));
		}
//		if (sparkle>-1) {
//		}
		if (selected) {
			g.drawRect(drawx-SIZE/2,drawy-SIZE/2,SIZE,SIZE);
		}
		//		g.setColor(Color.black);
//		g.drawString(destx/SIZE+","+desty/SIZE,destx,desty);
	}
	
	public void set(int x,int y) {
//		if (!moving) {
			startx=x*SIZE;
			starty=y*SIZE;
//		}
		destx=startx;
		desty=starty;
		c=allColor[(int)(Math.random()*allColor.length)];
	}
	
	public void setMove(int dx,int dy,long dt) {
		if (startx!=destx || starty!=desty) {//!moving) {
			destTime+=dt*.5;
		}
		else {
			startx=destx;
			starty=desty;
			startTime=System.currentTimeMillis();
			destTime=System.currentTimeMillis()+dt+(int)(400*Math.random()-200);
		}
		destx=dx*SIZE;
		desty=dy*SIZE;
	}
	
	
}
