package aj.fm.engine;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class WizardPanel extends JPanel{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private Wizard wizard;

	public WizardPanel(Wizard w) {
		super(new GridLayout(2,0));
		//name  rightpat  nextr   target
		//curhp  leftpat   nextl   target
		
		wizard=w;
		JLabel name=new JLabel(w.getName());
		JLabel hp=new JLabel(""+w.getCurrentHitPoints()+"/"+Wizard.maxHitPoints);
		JTextField rpat=new JTextField(8);
		rpat.setText(w.getRightPattern());
		JTextField lpat=new JTextField(8);
		lpat.setText(w.getLeftPattern());
		String handChoice[]={">","P","D","C","W","S"};
		String targetChoice[]={"wizarda","monstera"};
		JComboBox rChoice=new JComboBox(handChoice);
		JComboBox lChoice=new JComboBox(handChoice);
		JComboBox trChoice=new JComboBox(targetChoice);
		JComboBox tlChoice=new JComboBox(targetChoice);
		
		String spells[]={};
		JComboBox rSpellChoice=new JComboBox(spells);
		JComboBox lSpellChoice=new JComboBox(spells);
		
		
		add(name);
		JPanel jp=new JPanel(new FlowLayout());
		add(new JLabel("Right",JLabel.LEFT));
		jp.add(rpat);
		add(jp);
		add(rChoice);
		add(new JLabel("Target",JLabel.LEFT));
		add(trChoice);
		add(rSpellChoice);

		add(hp);
		JPanel jp2=new JPanel(new FlowLayout());
		jp2.add(lpat);
		add(new JLabel("Left",JLabel.LEFT));
		add(jp2);
		add(lChoice);		
		add(new JLabel("Target",JLabel.LEFT));
		add(tlChoice);
		add(lSpellChoice);
	}
		
}
