package aj.awt;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * This is a Dialog that has an okay or cancel option You add components to the
 * dialoge and after the okay is pressed you can process the info you need.
 * -last modified 1/7/98
 * 
 * @author judda
 * @created April 12, 2000
 */
public class OkDialog extends Dialog implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Description of the Field
	 */
	public Vector v = new Vector();

	Button okay = new Button("Okay"), cancel = new Button("Cancel");

	Panel listPanel, commandPanel;

	/**
	 * Constructor for the OkDialog object
	 * 
	 * @param F
	 *            Description of Parameter
	 * @param Title
	 *            Description of Parameter
	 * @param t
	 *            Description of Parameter
	 */
	public OkDialog(Frame F, String Title, boolean t) {
		super(F, Title, t);
		setLayout(new BorderLayout());
		listPanel = new Panel();
		commandPanel = new Panel();
		commandPanel.add(cancel);
		commandPanel.add(okay);
		cancel.addActionListener(this);
		okay.addActionListener(this);
		add("Center", listPanel);
		add("South", commandPanel);
		pack();
	}

	/**
	 * Constructor for the OkDialog object
	 * 
	 * @param F
	 *            Description of Parameter
	 * @param Title
	 *            Description of Parameter
	 */
	public OkDialog(Frame F, String Title) {
		super(F, Title, true);
		setLayout(new BorderLayout());
		listPanel = new Panel();
		commandPanel = new Panel();
		commandPanel.add(cancel);
		commandPanel.add(okay);
		cancel.addActionListener(this);
		okay.addActionListener(this);
		add("Center", listPanel);
		add("South", commandPanel);
		pack();
	}

	/**
	 * Sets the Components attribute of the OkDialog object
	 * 
	 * @param list
	 *            The new Components value
	 */
	public void setComponents(Component list[][]) {
		remove(listPanel);
		listPanel = new Panel(new GridLayout(list.length, 0));
		for (int a = 0; a < list.length; a++) {
			Panel temp = new Panel(new GridLayout(0, list[a].length));
			for (int b = 0; b < list[a].length; b++) {
				temp.add(list[a][b]);
			}
			listPanel.add(temp);
		}
		add("Center", listPanel);
		pack();
	}

	/**
	 * Description of the Method
	 * 
	 * @param AE
	 *            Description of Parameter
	 */
	public void actionPerformed(ActionEvent AE) {
		if (AE.getSource() == okay) {
			setVisible(false);
			processActionEvent(new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED, "okay"));
		} else {
			setVisible(false);
		}
	}

	/**
	 * Adds a feature to the ActionListener attribute of the OkDialog object
	 * 
	 * @param AL
	 *            The feature to be added to the ActionListener attribute
	 */
	public void addActionListener(ActionListener AL) {
		v.removeElement(AL);
		v.addElement(AL);
	}

	/**
	 * Description of the Method
	 * 
	 * @param AL
	 *            Description of Parameter
	 */
	public void removeActionListener(ActionListener AL) {
		v.removeElement(AL);
	}

	/**
	 * Description of the Method
	 * 
	 * @param AE
	 *            Description of Parameter
	 */
	public void processActionEvent(ActionEvent AE) {
		if (v == null) {
			return;
		}
		for (int a = 0; a < v.size(); a++) {
			ActionListener AL = (ActionListener) v.elementAt(a);
			AL.actionPerformed(AE);
		}
	}
}
