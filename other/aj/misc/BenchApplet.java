package aj.misc;

import java.applet.Applet;
import java.awt.Button;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BenchApplet extends Applet implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	TextArea ta = new TextArea(10, 65);

	public void init() {
		try {
			String tm = getParameter("time");
			if (tm != null)
				Bench.OPTIME = 1000 * Integer.parseInt(tm);
		} catch (NumberFormatException NFE) {
			System.out.println("FORMAT: java aj.misc.Bench [<SEC>]");
			System.exit(0);
		}
		Button b = new Button("Calc it");
		b.addActionListener(this);
		add("Center", ta);
		add("South", b);
	}

	public void actionPerformed(ActionEvent ae) {
		new Bench();
		Bench.total = 0;
		String t = Bench.testRR() + "\n" + Bench.testSD() + "\n"
				+ Bench.testDD() + "\n" + Bench.testSY() + "\n"
				+ Bench.showResult() + "\n" + Bench.matrix();
		ta.setText("DO it\n" + t);
	}

}