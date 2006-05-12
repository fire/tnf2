package aj.bitbot;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JFrame;

public class GridGui {
	public static void main(String s[]) {
		new GridGui();
	}

	static final private int CELLSIZE=13;
	private Grid grid;
	private Player player;
	private Vector bots=new Vector();
	private int numActiveBots=1;
	GridPanel gridPanel;
	
	public GridGui() {
		grid=new Grid(1,1,1,CELLSIZE);
		System.out.println("g="+grid);
		player=new Player(CELLSIZE/2,CELLSIZE/2,Math.random()*Math.PI*2);//Math.random()*CELLSIZE,Math.random()*CELLSIZE,Math.random()*Math.PI*2);
		initilizeBots();
		
		gridPanel=new GridPanel(this);

		JFrame jf=new JFrame("GridGui");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(600,400);
		jf.getContentPane().setLayout(new BorderLayout());
		jf.getContentPane().add("Center",gridPanel);
		jf.setVisible(true);
		
		startThreads();
	}

	private void startThreads() {
		new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					for (int a=0;a<bots.size();a++) {
						Bot bot=(Bot)bots.elementAt(a);
						bot.move();
					}
					gridPanel.repaint();
				}
			}
					}.start();
		
	}

	private void initilizeBots() {
		while (bots.size()<numActiveBots) {
			Bot bot=new Bot(this);
			if (grid.getGrid()[(int)bot.getY()][(int)bot.getX()]==Grid.EMPTY) {
				bots.addElement(bot);
			}
		}
		// TODO Auto-generated method stub
		
	}

	public Player getPlayer() {
		return player;
	}

	public Grid getGrid() {
		return grid;
	}

	public Vector getBots() {
		return bots;
	}

	public double getCellSize() {
		return CELLSIZE;
	}
}
