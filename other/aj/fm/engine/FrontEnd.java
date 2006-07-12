package aj.fm.engine;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import aj.fm.Game;
import aj.fm.Monster;
import aj.fm.Wizard;

public class FrontEnd {
	
	private Game game;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new FrontEnd();
	}

	JPanel center=new JPanel(new BorderLayout());
	
	public FrontEnd() {
		game=new Game();
		JFrame jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(600,400);

		JMenuBar jb=new JMenuBar();
		jf.setJMenuBar(jb);
		
		JMenu menu=new JMenu("Menu");
		jb.add(menu);
		JMenuItem addWizard=new JMenuItem("addWizard");
		menu.add(addWizard);
		
		JPanel buttonPanel=new JPanel(new FlowLayout());
		JButton nextTurn=new JButton("Advance Turn");
		buttonPanel.add(nextTurn);
		
		jf.getContentPane().add("Center",center);
		jf.getContentPane().add("South",buttonPanel);
		jf.setVisible(true);
		
		addWizard.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				game.addNewWizard(Wizard.createRandomWizard());
				updateGui();
			}
		});
		
		nextTurn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				game.advanceTurn();
				updateGui();
			}
		});
	}
	


	private void updateGui() {
		center.removeAll();
		Vector v=game.getAllWizards();
//		System.out.println("size="+v.size());
		JPanel allWizards=new JPanel(new GridLayout(0,1));
		for (int a=0;a<v.size();a++) {
			Wizard w=(Wizard)v.elementAt(a);
			Vector targets=new Vector();
			targets.addElement(Wizard.TARGET_SELF);
			for (int b=0;b<game.getAllWizards().size();b++) {
				if (game.getAllWizards().elementAt(b)!=w) {
					targets.addElement(((Wizard)game.getAllWizards().elementAt(b)).getName());
				}
			}
			for (int b=0;b<game.getAllMonsters().size();b++) {
				targets.addElement(((Monster)game.getAllMonsters().elementAt(b)).getName());
			}
			targets.addElement(Wizard.TARGET_NO_ONE);
			WizardPanel wp=new WizardPanel(w,targets);
			allWizards.add(wp);
		}
		JPanel jp=new JPanel(new FlowLayout());
		jp.add(allWizards);
		
		center.add("Center",jp);
		center.validate();
		center.doLayout();
	}

}
