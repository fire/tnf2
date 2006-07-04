package aj.man;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import aj.misc.GmlPair;

public class Man {
	int x, y;// center

	static Pos positions[];

	static int headsize = 6;

	Pos pos;

	public Man(int p) {
		if (positions == null)
			readAllPos();
		for (int a = 0; a < positions.length; a++) {
			if (positions[a].id == p) {
				pos = positions[a];
			}
		}
	}

	public Man next() {
		Pos p = pos;
		for (int a = 0; a < positions.length; a++) {
			if (positions[a] == p) {
				if (positions.length > a + 1)
					p = positions[a + 1];
				else
					p = positions[0];
				break;
			}
		}
		return new Man(p);
	}

	public Man(Pos p) {
		if (positions == null)
			readAllPos();
		pos = p;
	}

	public Man() {
		if (positions == null)
			readAllPos();
	}

	public void saveAllPos() {
		part = -1;
		try {
			PrintWriter pw = new PrintWriter(new FileWriter("pos.gml"));
			pw.println("all [ ");
			for (int a = 0; a < positions.length; a++) {
				pw.println(positions[a].toSaveString());
			}
			pw.println("]");
			pw.flush();
			pw.close();
			System.out.println("good save");
		} catch (IOException ioe) {
			System.out.println("Bad save " + ioe);
		}

	}

	public void readAllPos() {
		try {
			String allpos = "";
			Class aClass = getClass();
			InputStream IN = aClass.getResourceAsStream("pos.gml");
			BufferedReader br = new BufferedReader(new InputStreamReader(IN));
			while (true) {
				String s = br.readLine();
				if (s == null)
					break;
				allpos += s + " ";
			}
			GmlPair g = aj.misc.GmlPair.parse(allpos);
			GmlPair po[] = g.getAllByName("position");
			positions = new Pos[po.length];
			for (int a = 0; a < po.length; a++) {
				positions[a] = Pos.parse(po[a]);
			}
			System.out.println("Parsed " + positions.length);
		} catch (IOException ioe) {
			System.out.println("Bad parse " + ioe);
		}
	}

	public void draw(Graphics g) {
		if (g == null)
			return;
		if (pos.id < 1)
			g.setColor(Color.darkGray);
		else
			g.setColor(Color.black);
		// head
		g.fillOval((int) (x + pos.headX - headsize / 2), (int) (y + pos.headY
				- headsize / 2 - 1), headsize - 1, headsize + 2);
		// neck - waste
		g.drawLine((int) (x + pos.neckX), (int) (y + pos.neckY),
				(int) (x + pos.waistX), (int) (y + pos.waistY));
		g.drawLine((int) (x + pos.neckX), (int) (y + pos.neckY),
				(int) (x + pos.relbX), (int) (y + pos.relbY));
		g.drawLine((int) (x + pos.neckX), (int) (y + pos.neckY),
				(int) (x + pos.lelbX), (int) (y + pos.lelbY));
		g.drawLine((int) (x + pos.relbX), (int) (y + pos.relbY),
				(int) (x + pos.rhandX), (int) (y + pos.rhandY));
		g.drawLine((int) (x + pos.lelbX), (int) (y + pos.lelbY),
				(int) (x + pos.lhandX), (int) (y + pos.lhandY));
		g.drawLine((int) (x + pos.waistX), (int) (y + pos.waistY),
				(int) (x + pos.rkneeX), (int) (y + pos.rkneeY));
		g.drawLine((int) (x + pos.waistX), (int) (y + pos.waistY),
				(int) (x + pos.lkneeX), (int) (y + pos.lkneeY));
		g.drawLine((int) (x + pos.rkneeX), (int) (y + pos.rkneeY),
				(int) (x + pos.rfootX), (int) (y + pos.rfootY));
		g.drawLine((int) (x + pos.lkneeX), (int) (y + pos.lkneeY),
				(int) (x + pos.lfootX), (int) (y + pos.lfootY));
		if (part != -1) {
			if (part == 0)
				g.drawOval((int) (x + pos.neckX - 2),
						(int) (y + pos.neckY - 2), 4, 4);
			if (part == 1)
				g.drawOval((int) (x + pos.relbX - 2),
						(int) (y + pos.relbY - 2), 4, 4);
			if (part == 2)
				g.drawOval((int) (x + pos.rhandX - 2),
						(int) (y + pos.rhandY - 2), 4, 4);
			if (part == 3)
				g.drawOval((int) (x + pos.lelbX - 2),
						(int) (y + pos.lelbY - 2), 4, 4);
			if (part == 4)
				g.drawOval((int) (x + pos.lhandX - 2),
						(int) (y + pos.lhandY - 2), 4, 4);
			if (part == 5)
				g.drawOval((int) (x + pos.waistX - 2),
						(int) (y + pos.waistY - 2), 4, 4);
			if (part == 6)
				g.drawOval((int) (x + pos.rkneeX - 2),
						(int) (y + pos.rkneeY - 2), 4, 4);
			if (part == 7)
				g.drawOval((int) (x + pos.rfootX - 2),
						(int) (y + pos.rfootY - 2), 4, 4);
			if (part == 8)
				g.drawOval((int) (x + pos.lkneeX - 2),
						(int) (y + pos.lkneeY - 2), 4, 4);
			if (part == 9)
				g.drawOval((int) (x + pos.lfootX - 2),
						(int) (y + pos.lfootY - 2), 4, 4);
			if (part == 10)
				g.drawOval((int) (x + pos.headX - 2),
						(int) (y + pos.headY - 2), 4, 4);
			g.drawLine(x - 30, y + 30, x + 30, y + 30);
		}
	}

	public static Man between(Pos p1, Pos p2, double d) {
		Man mr = new Man();
		if (p1 == null || p2 == null)
			return null;
		p1 = Pos.between2(p1, p2, d);
		mr = new Man(p1);
		return mr;
	}

	public static Man between(int pos1, int pos2, double d) {
		Pos p1 = null;
		Pos p2 = null;
		Man mr = new Man();
		for (int a = 0; a < positions.length; a++) {
			if (positions[a].id == pos1) {
				p1 = positions[a];
			}
		}
		for (int a = 0; a < positions.length; a++) {
			if (positions[a].id == pos1) {
				p2 = positions[a];
			}
		}
		if (p1 == null || p2 == null)
			return null;
		p1 = Pos.between2(p1, p2, d);
		mr = new Man(p1);
		return mr;
	}

	public String toString() {
		return "Man " + pos;
	}

	public static void main(String s[]) {
		Man m = Man.between(1, 2, 0);
		System.out.println("m=" + m);
		m = Man.between(1, 2, 1);
		System.out.println("m=" + m);
		m = Man.between(1, 2, .5);
		System.out.println("m=" + m);
	}

	int part = -1;

	public void posPartUp() {
		if (part == -1) {
			part = 0;
			return;
		}
		if (part == 0)
			pos.neckY--;
		if (part == 1)
			pos.relbY--;
		if (part == 2)
			pos.rhandY--;
		if (part == 3)
			pos.lelbY--;
		if (part == 4)
			pos.lhandY--;
		if (part == 5)
			pos.waistY--;
		if (part == 6)
			pos.rkneeY--;
		if (part == 7)
			pos.rfootY--;
		if (part == 8)
			pos.lkneeY--;
		if (part == 9)
			pos.lfootY--;
		if (part == 10)
			pos.headY--;
		pos.fixLen();
	}

	public void posPartDown() {
		if (part == -1) {
			part = 0;
			return;
		}
		if (part == 0)
			pos.neckY++;
		if (part == 1)
			pos.relbY++;
		if (part == 2)
			pos.rhandY++;
		if (part == 3)
			pos.lelbY++;
		if (part == 4)
			pos.lhandY++;
		if (part == 5)
			pos.waistY++;
		if (part == 6)
			pos.rkneeY++;
		if (part == 7)
			pos.rfootY++;
		if (part == 8)
			pos.lkneeY++;
		if (part == 9)
			pos.lfootY++;
		if (part == 10)
			pos.headY++;
		pos.fixLen();
	}

	public void posPartRight() {
		if (part == -1) {
			part = 0;
			return;
		}
		if (part == 0)
			pos.neckX++;
		if (part == 1)
			pos.relbX++;
		if (part == 2)
			pos.rhandX++;
		if (part == 3)
			pos.lelbX++;
		if (part == 4)
			pos.lhandX++;
		if (part == 5)
			pos.waistX++;
		if (part == 6)
			pos.rkneeX++;
		if (part == 7)
			pos.rfootX++;
		if (part == 8)
			pos.lkneeX++;
		if (part == 9)
			pos.lfootX++;
		if (part == 10)
			pos.headX++;
		pos.fixLen();
	}

	public void posPartLeft() {
		if (part == -1) {
			part = 0;
			return;
		}
		if (part == 0)
			pos.neckX--;
		if (part == 1)
			pos.relbX--;
		if (part == 2)
			pos.rhandX--;
		if (part == 3)
			pos.lelbX--;
		if (part == 4)
			pos.lhandX--;
		if (part == 5)
			pos.waistX--;
		if (part == 6)
			pos.rkneeX--;
		if (part == 7)
			pos.rfootX--;
		if (part == 8)
			pos.lkneeX--;
		if (part == 9)
			pos.lfootX--;
		if (part == 10)
			pos.headX--;
		pos.fixLen();
	}

	public void posPartSwitch() {
		if (part == -1) {
			part = 0;
			return;
		}
		part++;
		if (part > 10)
			part = 0;
	}

	public void createPos() {
		if (pos.id == -1 || part == -1)
			return;
		pos = new Pos(pos);
		pos.id = -1;
		Pos pp[] = new Pos[positions.length + 1];
		for (int a = 0; a < positions.length; a++)
			pp[a] = positions[a];
		pp[pp.length - 1] = pos;
		positions = pp;
	}
}
// add toes
// add fist
// add sholders
// add face
