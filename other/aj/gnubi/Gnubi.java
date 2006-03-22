package aj.gnubi;

/*
 * Created on Jun 29, 2003
 *
 */

/**
 * @author winme
 *
 */

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JTextField;

public class Gnubi extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new Gnubi();
	}

	static Display dis=null;
	
	static Player player=new Player();
	static MapCell mapCell=MapCell.createRandom();

	public Gnubi() {
		super("Gnubi");
		dis=new Display();
		guiSetup();
		Vector v=mapCell.getAllBlocks();
		for (int a=0;a<v.size();a++) {
			Block b=(Block)v.elementAt(a);
			dis.add(b);
			dis.add(player);
			//dis.add((JComponent)player);
		}
		dis.repaint();
		Control c=new Control(mapCell,player);
		new Thread(c).start();
	}

	public void guiSetup() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
		setSize(new Dimension(600,400));
		dis.setLayout(null);
		getContentPane().add("Center",dis);
		JTextField tf=new JTextField();
		getContentPane().add("South",tf);
		//addKeyListener(player);
		tf.addKeyListener(player);
		setVisible(true);
	}

	
	
}
/*
 * gnubi -

infinate random map
  eachcell is maze
    maze openings in walls dependant on cell numbers


blocks - 
background
  solid
     s,m,l
     mountains, rocks, ice, houses,
  breakable
    s,m,l
  passthough
    door
     ladders, plaforms,vines,water, trampolines, cannons
volcano
tree

powers
  walk
  jump
  fly
  climb
  shoot
     -fire, ice, wind, knife, puch, yo-yo, arrows, feathers, magic, speed, energy
  absorb, eat guys


bad guys
  engery flys
main guys


#1
view screen (check)
  see blocks (check)
  see air (check)
  create random mapcell(Check)
  
#2
  display layout components
    all blocks as components added to display (check)
    blocks to type component (check)
    	add images to blocks (check)
    playerIcon component
    	add images to playericons
  define movement -
  	component sizes block=2,player=1
     dx=0-1 = centerx shift
     if (<0 move to new block)
   make move loop
   control walking
  make guy (check)
  move map


#3     
   map and mapcell
     each map cell has partial map.
     request next map cell
        new map cell
        all blocks shifted by subcell
        must shift all movables to new subsell loc
   keyboard interface -
     keyTyped = String
     keysHeld = String
     jump if Held or Typed
     move if Held or Typed
        do fall
   	  do jump motion
      do move
      do stop
   
   obstical interferance -
     move beyond +/-.5 dx requires opens space
     move beyond -.5 dy
     dy =-.5 with gravity
     
#4
	breakable blocks     
   multi - images
   
   add animation loop - 
     
     
*/
