package aj.combat;

/**
 * Description of the Class
 * 
 * @author judda
 * @created April 12, 2000
 */
public class Thing {

	static int UPDATESPERSECOND = 30;

	double x, y, vx, vy, dir;

	int size;

	long time;

	String id;

	/**
	 * Description of the Method
	 * 
	 * @param t
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public boolean inside(Thing t) {
		double dx = (t.x - x);
		double dy = (t.y - y);
		double d = Math.sqrt(dx * dx + dy * dy);
		// double dx = size;
		// dy = t.size;
		// return size+t.size < Math.sqrt(dx * dx + dy * dy);
		return size + t.size > d;
	}

	/**
	 * Description of the Method
	 */
	public void updatePos() {
		long t = System.currentTimeMillis();
		double dt = 1.0 * (t - time) / UPDATESPERSECOND;
		if (dt < 1) {
			return;
		}
		time = t;
		x = x + vx * dt;
		y = y + vy * dt;
		if (x > MapView.ARENASIZE) {
			x = x - MapView.ARENASIZE;
		} else if (x < 0) {
			x = x + MapView.ARENASIZE;
		}
		if (y > MapView.ARENASIZE) {
			y = y - MapView.ARENASIZE;
		} else if (y < 0) {
			y = y + MapView.ARENASIZE;
		}
	}

	public void copyVals(Thing t) {
		x = t.x;
		y = t.y;
		vx = t.vx;
		vy = t.vy;
		dir = t.dir;
		size = t.size;
		time = t.time;
		id = t.id;
	}

	public String getId() {
		return id;
	}
}
