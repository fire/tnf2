/*body raise 10 to -50   

0   
 \     _o
  |   |

 body twist 45 to -45
       /   \
-0-  0      0
    /         \

  arm raise    180 to -30
  
 0
 |\
 !
  arm extend 0 to 90
 O    o_
/|\  /|

  leg raise -30 to 100
  leg extend -10 to 80

  foot raise -150 to 0
  foot rotate-45 to 45
  toe raise 10 to -80
  toe extend 30 to -30
  
  raiseHand 80 to 0
  extendHand 10 to -90

  finger raise10 to -80
  finger twist 90 to -10
*/package aj.awt;
import java.awt.Canvas;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;

public class Body extends Canvas {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static int headSize = 14;
	
	static int hipSize = 14;
	
	static int backSize = 30;
	
	static int shoulderSize = 16;
	
	static int armSize = 12;
	
	static int foreArmSize = 12;
	
	static int handSize = 5;
	
	static int legSize = 18;
	
	static int lowerLegSize = 18;
	
	static int footSize = 4;
	
	static int neckSize = 4;
	double cx = 0, cy = 0;
	//centerpoint
	double ux = 0, uy = 0;
	//up vector
	double bodyRaise = 0;
	double bodyTwist = 0;
	double leftArmRaise = 0;
	double leftArmExtend = 0;
	double leftElboRaise = 0;
	double leftElboExtend = 0;
	double leftHandrRaise = 0;
	double leftHandTwist = 0;
	double leftLegRaise = 0;
	double leftLegExtend = 0;
	double leftKneeRaise = 0;
	double leftFootRaise = 0;
	double leftFootExtend = 0;
	
	public void paint (Graphics g) {
		g.translate (100, 100);
		Point lhip = new Point ((int) (cx - hipSize / 2), (int)cy);
		Point rhip = new Point ((int) (cx + hipSize / 2), (int)cy);
		Point back1 = new Point ((int)cx, (int)cy);
		Point back2 = new Point ((int)cx, (int) (cy - backSize));
		Point lknee = new Point (lhip.x, lhip.y + legSize);
		Point rknee = new Point (rhip.x, rhip.y + legSize);
		Point lfoot = new Point (lknee.x, lknee.y + lowerLegSize / 2);
		Point rfoot = new Point (rknee.x, rknee.y + lowerLegSize / 2);
		Point ltoe = new Point (lfoot.x - footSize, lfoot.y);
		Point rtoe = new Point (rfoot.x + footSize, rfoot.y);
		Point lshol = new Point ((int) (cx - shoulderSize / 2), back2.y);
		Point rshol = new Point ((int) (cx + shoulderSize / 2), back2.y);
		Point lelbo = new Point (lshol.x, lshol.y + armSize);
		Point relbo = new Point (rshol.x, rshol.y + armSize);
		Point lhand = new Point (lelbo.x, lelbo.y + foreArmSize);
		Point rhand = new Point (relbo.x, relbo.y + foreArmSize);
		Point neck = new Point (back2.x, back2.y - neckSize);
		g.drawOval (neck.x - headSize / 2, neck.y - headSize, headSize, headSize);
		g.drawOval (neck.x - headSize / 4 - headSize / 5 / 2, neck.y - headSize * 2 / 3, headSize / 5, headSize / 5);
		g.drawOval (neck.x + headSize / 4 - headSize / 5 / 2, neck.y - headSize * 2 / 3, headSize / 5, headSize / 5);
		g.drawLine (back2.x, back2.y, neck.x, neck.y);
		g.drawLine (back1.x, back1.y, back2.x, back2.y);
		g.drawLine (lhip.x, lhip.y, rhip.x, rhip.y);
		//g.setColor(Color.blue);
		g.drawLine (lhip.x, lhip.y, lknee.x, lknee.y);
		g.drawLine (rhip.x, rhip.y, rknee.x, rknee.y);
		//g.setColor(Color.red);
		g.drawLine (lknee.x, lknee.y, lfoot.x, lfoot.y);
		g.drawLine (rknee.x, rknee.y, rfoot.x, rfoot.y);
		//g.setColor(Color.green);
		g.drawLine (lfoot.x, lfoot.y, ltoe.x, ltoe.y);
		g.drawLine (rfoot.x, rfoot.y, rtoe.x, rtoe.y);
		g.drawLine (lshol.x, lshol.y, rshol.x, rshol.y);
		//g.setColor(Color.blue);
		g.drawLine (lshol.x, lshol.y, lelbo.x, lelbo.y);
		g.drawLine (rshol.x, rshol.y, relbo.x, relbo.y);
		//g.setColor(Color.red);
		g.drawLine (lelbo.x, lelbo.y, lhand.x, lhand.y);
		g.drawLine (relbo.x, relbo.y, rhand.x, rhand.y);
	}
	
	public static void main (String s[]) {
		Body b = new Body();
		Frame f = new Frame();
		f.setSize (200, 200);
		f.add ("Center", b);
		f.setVisible (true);
		f.addWindowListener (new SimpleWindowManager());
	}
	//  public Point3d rotate(double dx,double dy,double dz,double leny) {
	//  }
}
/*
Joint (3d point, at vector)

limits rotation X (raise/lower) , Y (twist), Z (extend)

*/
