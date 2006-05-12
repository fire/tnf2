package aj.bitbot;

import java.awt.Graphics;

public class Bot {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}
	
	private double x,y;
	private long lastMoveTime=-1;
	double dir;
	double speed;
	private GridGui parent;
	static private double UP=Math.PI*3/2,DOWN=Math.PI/2,RIGHT=0,LEFT=Math.PI;
	static private double dirChoice[]={UP,DOWN,LEFT,RIGHT};
	private char[][] gridmap;

	public Bot(GridGui parent) {
		this.parent=parent;
		this.dir=dirChoice[(int)(Math.random()*4)];
		this.speed=Math.random()*1+1;
		Grid grid=parent.getGrid();
		char gridmap[][]=grid.getGrid();
		while (gridmap[(int)y][(int)x]!=Grid.EMPTY) {
			this.x=(int)(Math.random()*parent.getCellSize());
			this.y=(int)(Math.random()*parent.getCellSize());			
		}
		getNewDir();
		String res="";
		if (dir==RIGHT) res+="RIGHT";
		if (dir==LEFT) res+="LEFT";
		if (dir==UP) res+="UP";
		if (dir==DOWN) res+="DOWN";

		System.out.println("test="+x+","+y+" d="+res);
	}
	
	public double getX() {return x;	}
	public double getY() {return y;	}

	public void move() {
		if (lastMoveTime==-1) lastMoveTime=System.currentTimeMillis();
		double dt=System.currentTimeMillis()-lastMoveTime;
		lastMoveTime=System.currentTimeMillis();
		Grid grid=parent.getGrid();
		gridmap=grid.getGrid();

		if (nextSquareOpen(x,y,dir)) {
			x=x+dt*Math.cos(dir)*speed/1000;
			y=y+dt*Math.sin(dir)*speed/1000;
		}
		else {
			getNewDir();
		}
		if (dir==UP || dir==DOWN) {
			x=(int)x;
		}
		if (dir==RIGHT || dir==LEFT) {
			y=(int)y;
		}
	}

	private boolean nextSquareOpen(double x,double y,double dir) {
		Grid grid=parent.getGrid();
		char gridmap[][]=grid.getGrid();
		int tx=(int)x,ty=(int)y;
		if (ty<0 || tx<0) return false;
		if (gridmap.length-1<ty || gridmap[ty].length-1<tx) return false;
		if (dir==RIGHT && ty>=0 && gridmap[ty].length>tx && gridmap[ty][tx+1]==Grid.EMPTY) return true;
		if (dir==DOWN && tx>=0 && gridmap.length>ty+1 && gridmap[ty+1][tx]==Grid.EMPTY) return true;
		if (dir==LEFT && ty>=0 && tx>=1 && gridmap[ty][tx-1]==Grid.EMPTY) return true;
		if (dir==UP && tx>=0 && ty>=0 && gridmap[ty][tx]==Grid.EMPTY) return true;
		return false;
	}

	private void getNewDir() {
		Grid grid=parent.getGrid();
		gridmap=grid.getGrid();
		
		double lastDir=dir;
		boolean canGoUp=false,canGoDown=false,canGoLeft=false,canGoRight=false;
		
		if (nextSquareOpen(x,y,RIGHT)) {//gridmap[yt].length-2>xt && gridmap[yt][xt]==Grid.EMPTY) {
			canGoRight=true;
		}
		if (nextSquareOpen(x,y,LEFT)) {//(int)(x)>1 &&gridmap[(int)(y+0)][(int)(x)-1]==Grid.EMPTY) {
			canGoLeft=true;
		}
		if (nextSquareOpen(x,y,DOWN)) {//gridmap.length-2>(int)(y)+1 && gridmap[(int)(y+1)][(int)(x+0)]==Grid.EMPTY) {
			canGoDown=true;
		}
		if (nextSquareOpen(x,y,UP)) {//(int)(y)>1 && gridmap[(int)(y)-1][(int)(x+0)]==Grid.EMPTY) {
			canGoUp=true;
		}

		if (dir==RIGHT) {
			if (canGoUp && canGoDown ) dir=(Math.random()<.5?UP:DOWN);
			else if (canGoUp) dir=UP;
			else if (canGoDown) dir=DOWN;
			else dir=LEFT;
		}
		else if (dir==LEFT) {
			if (canGoUp && canGoDown ) dir=(Math.random()<.5?UP:DOWN);
			else if (canGoUp) dir=UP;
			else if (canGoDown) dir=DOWN;
			else dir=RIGHT;
		}
		else if (dir==UP) {
			if (canGoRight && canGoLeft ) dir=(Math.random()<.5?RIGHT:LEFT);
			else if (canGoRight) dir=RIGHT;
			else if (canGoLeft) dir=LEFT;
			else dir=DOWN;
		}
		else if (dir==DOWN) {//going right
			if (canGoRight && canGoLeft ) dir=(Math.random()<.5?RIGHT:LEFT);
			else if (canGoRight) dir=RIGHT;
			else if (canGoLeft) dir=LEFT;
			else dir=UP;
		}
		String res="";
		if (lastDir==RIGHT) res+="from=right ";
		if (lastDir==LEFT) res+="from=left ";
		if (lastDir==UP) res+="from=up ";
		if (lastDir==DOWN) res+="from=down ";
		if (dir==RIGHT) res+=" to=RIGHT "+dir;
		if (dir==LEFT) res+=" to=LEFT "+dir;
		if (dir==UP) res+=" to=UP "+dir;
		if (dir==DOWN) res+=" to=DOWN "+dir;
		if (canGoUp) res+="up,";
		if (canGoDown) res+="down,";
		if (canGoRight) res+="right,";
		if (canGoLeft) res+="left";
//		System.out.println(" u="+canGoUp+" d="+canGoDown+" l="+canGoLeft+" r="+canGoRight+" "+res);
		if (dir!=lastDir) {
			System.out.println("dirChange "+res);
		}
	}

	public void draw(Graphics g, int scale) {
		g.translate((int)(x*scale),(int)(y*scale));
		g.drawOval(-scale/2,-scale/2,scale,scale);
		g.translate(-(int)(x*scale),-(int)(y*scale));
	}
}
