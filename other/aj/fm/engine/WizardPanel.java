package aj.fm.engine;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class WizardPanel extends JPanel{

	private Wizard wizard;

	public WizardPanel(Wizard w) {
		super(new BorderLayout());
		//name  rightpat  nextr   target
		//curhp  leftpat   nextl   target
		wizard=w;
		
		JLabel name=new JLabel(wizard.getName());
		JLabel hp=new JLabel(""+wizard.getCurrentHitPoints()+"/"+Wizard.maxHitPoints);
		JTextField rpat=new JTextField(8);
		rpat.setText(wizard.getRightPattern());
		JTextField lpat=new JTextField(8);
		lpat.setText(wizard.getLeftPattern());

		String targetChoice[]={"wizA","monA"};
		JComboBox rChoice=new JComboBox(wizard.handChoice);
		JComboBox lChoice=new JComboBox(wizard.handChoice);
		rChoice.setSelectedItem(wizard.getNextRightGesture());
		lChoice.setSelectedItem(wizard.getNextLeftGesture());
		JComboBox trChoice=new JComboBox(targetChoice);
		JComboBox tlChoice=new JComboBox(targetChoice);
		
		Vector rv=Spell.getAllSpellsByNearestToPattern(w.getRightPattern());
				
		String rspells[],lspells[];
		rspells=new String[rv.size()];
			for (int a=0;a<rv.size();a++) {
				Spell s=(Spell)rv.elementAt(a);
				int rem=s.getStepsLeft(w.getRightPattern());
				rspells[a]=rem+":"+s.getGesture().substring(s.getGesture().length()-rem)+":"+s.getName()+" "+s.getGesture();
		}
		
		Vector lv=Spell.getAllSpellsByNearestToPattern(w.getLeftPattern());
		lspells=new String[lv.size()];
		
		lspells=new String[lv.size()];
			for (int a=0;a<lv.size();a++) {
				Spell s=(Spell)lv.elementAt(a);
				int rem=s.getStepsLeft(w.getLeftPattern());
				lspells[a]=rem+":"+s.getGesture().substring(s.getGesture().length()-rem)+":"+s.getName()+" "+s.getGesture();
		}
		
		JComboBox rSpellChoice=new JComboBox(rspells);
		JComboBox lSpellChoice=new JComboBox(lspells);
		
		JPanel centRow=new JPanel(new FlowLayout());
		JPanel jp=new JPanel(new FlowLayout());
		centRow.add(new JLabel("Right",JLabel.LEFT));
		jp.add(rpat);
		centRow.add(jp);
		centRow.add(rChoice);
		centRow.add(new JLabel("Target",JLabel.LEFT));
		centRow.add(trChoice);
		centRow.add(rSpellChoice);
		add("Center",centRow);

		JPanel botRow=new JPanel(new FlowLayout());
		JPanel jp2=new JPanel(new FlowLayout());
		jp2.add(lpat);
		botRow.add(new JLabel("Left",JLabel.LEFT));
		botRow.add(jp2);
		botRow.add(lChoice);		
		botRow.add(new JLabel("Target",JLabel.LEFT));
		botRow.add(tlChoice);
		botRow.add(lSpellChoice);
		add("South",botRow);
		
		JPanel topRow=new JPanel(new FlowLayout());
		topRow.add(name);
		topRow.add(hp);
		add("North",topRow);
		
		rChoice.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox c=(JComboBox)e.getSource();
				wizard.setNextRightHand((String)c.getSelectedItem());
			}
		});
		lChoice.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox c=(JComboBox)e.getSource();
				wizard.setNextLeftHand((String)c.getSelectedItem());
			}
		});
		
		lSpellChoice.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox c=(JComboBox)e.getSource();
				wizard.setNextLeftSpell((String)c.getSelectedItem());
			}
		});

		rSpellChoice.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox c=(JComboBox)e.getSource();
				wizard.setNextRightSpell((String)c.getSelectedItem());
			}
		});

	}
		
}
