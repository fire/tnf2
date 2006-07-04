package aj.gnubi;

import java.awt.Point;

public interface MapItem {
	public Point getCellPos();

	public void setCellPos(int x, int y);

	public void setCellPos(Point p);

	public double getXPos();

	public double getYPos();
}
