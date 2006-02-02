package aj.games;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

public class InputListener implements Runnable {
	BufferedReader br;
	public InputListener(BufferedReader br) {this.br=br;}
	Vector al=new Vector();
	public void addActionListener(ActionListener all) {
		if (!al.contains(all))  al.addElement(all);
	}
	public void removeActionListener(ActionListener all) {
		al.removeElement(all);
	}
	public void run() {
		while (true) {
			try {
			String s=br.readLine();
			if (s==null) {performAction("connection_closed");break;}
			else performAction(s);
			} catch (IOException IOE) {
				performAction("connection_closed");
				break;
			}
		}
	}
	public void performAction(String s) {
		Vector v=(Vector)al.clone();
		for (int a=0;a<v.size();a++) {
			ActionListener all=(ActionListener)al.elementAt(a);
			all.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,s));

		}
	}
}
