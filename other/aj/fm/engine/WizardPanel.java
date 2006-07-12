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

import aj.fm.Spell;
import aj.fm.Wizard;

public class WizardPanel extends JPanel{

	private Wizard wizard;

	
	private JLabel warning=new JLabel();
	private JComboBox rightHandChoice=new JComboBox();
	private JComboBox leftHandChoice=new JComboBox();
	private JComboBox rightTargetChoice=new JComboBox();
	private JComboBox leftTargetChoice=new JComboBox();
	private JComboBox rightSpellChoice=new JComboBox();
	private JComboBox leftSpellChoice=new JComboBox();
	private JLabel name=new JLabel();
	private JLabel hp=new JLabel();
	private JTextField rightPatternField=new JTextField(8);
	private JTextField leftPatternField=new JTextField(8);
	private Vector rightAvailableSortedSpells=new Vector();
	private Vector leftAvailableSortedSpells=new Vector();

	
	public WizardPanel(Wizard w,Vector targets) {
		super(new BorderLayout());
		wizard=w;
		rightAvailableSortedSpells=Spell.getAllSpellsByNearestToPattern(wizard.getRightPattern());
		leftAvailableSortedSpells=Spell.getAllSpellsByNearestToPattern(wizard.getLeftPattern());

		name.setText(wizard.getName());
		hp.setText(""+wizard.getCurrentHitPoints()+"/"+Wizard.maxHitPoints);
		
		rightPatternField.setText(wizard.getRightPattern());
		leftPatternField.setText(wizard.getLeftPattern());

//		String targetChoice[]={"wizA","monA"};
		rightHandChoice.removeAllItems();
		leftHandChoice.removeAllItems();
		for (int a=0;a<wizard.handChoice.length;a++) {
			rightHandChoice.addItem(wizard.handChoice[a]);
			leftHandChoice.addItem(wizard.handChoice[a]);
		}
		rightTargetChoice.removeAllItems();
		leftTargetChoice.removeAllItems();
		for (int a=0;a<targets.size();a++) {
			rightTargetChoice.addItem(targets.elementAt(a).toString());
			leftTargetChoice.addItem(targets.elementAt(a).toString());
		}
		
		rightSpellChoice.removeAllItems();
			for (int a=0;a<rightAvailableSortedSpells.size();a++) {
				Spell s=(Spell)rightAvailableSortedSpells.elementAt(a);
				int rem=s.getStepsLeft(w.getRightPattern());
				rightSpellChoice.addItem(rem+":"+s.getGesture().substring(s.getGesture().length()-rem)+":"+s.getName()+" "+s.getGesture());
		}		
			
		leftSpellChoice.removeAllItems();
			for (int a=0;a<leftAvailableSortedSpells.size();a++) {
				Spell s=(Spell)leftAvailableSortedSpells.elementAt(a);
				int rem=s.getStepsLeft(w.getLeftPattern());
				leftSpellChoice.addItem(rem+":"+s.getGesture().substring(s.getGesture().length()-rem)+":"+s.getName()+" "+s.getGesture());
		}
		

		JPanel topRow=new JPanel(new FlowLayout());
		topRow.add(name);
		topRow.add(hp);
		topRow.add(warning);
		add("North",topRow);
		
		JPanel centRow=new JPanel(new FlowLayout());
		JPanel jp=new JPanel(new FlowLayout());
		centRow.add(new JLabel("Right",JLabel.LEFT));
		jp.add(rightPatternField);
		centRow.add(jp);
		centRow.add(rightHandChoice);
		centRow.add(new JLabel("Target",JLabel.LEFT));
		centRow.add(rightTargetChoice);
		centRow.add(rightSpellChoice);
		add("Center",centRow);

		JPanel botRow=new JPanel(new FlowLayout());
		JPanel jp2=new JPanel(new FlowLayout());
		jp2.add(leftPatternField);
		botRow.add(new JLabel("Left",JLabel.LEFT));
		botRow.add(jp2);
		botRow.add(leftHandChoice);		
		botRow.add(new JLabel("Target",JLabel.LEFT));
		botRow.add(leftTargetChoice);
		botRow.add(leftSpellChoice);
		add("South",botRow);
				
		rightHandChoice.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox c=(JComboBox)e.getSource();
				wizard.setNextRightHand((String)c.getSelectedItem());
				validateHand();
			}
		});
		leftHandChoice.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox c=(JComboBox)e.getSource();
				wizard.setNextLeftHand((String)c.getSelectedItem());
				validateHand();
			}
		});
		
		leftSpellChoice.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox c=(JComboBox)e.getSource();
				Spell sp=getSpellFromMultiNameString((String)c.getSelectedItem());
				wizard.setNextLeftSpell(sp);
				String next=sp.getNextStep(wizard.getLeftPattern());
				leftHandChoice.setSelectedItem(next);
				if (next.equals("c") || next.equals("w") || next.equals("p")) {
					rightHandChoice.setSelectedItem(next);
				}
				else if (rightHandChoice.getSelectedItem().toString()!=rightHandChoice.getSelectedItem().toString().toUpperCase()) {
					rightHandChoice.setSelectedItem("-");
				}
				if (sp.getDefaultTarget()==Wizard.TARGET_SELF) {
					leftTargetChoice.setSelectedItem(Wizard.TARGET_SELF);				
				}
				else if (sp.getDefaultTarget()==Wizard.TARGET_NO_ONE) {
					leftTargetChoice.setSelectedItem(Wizard.TARGET_NO_ONE);										
				}
				else if (leftTargetChoice.getSelectedIndex()==0 || leftTargetChoice.getSelectedIndex()==leftTargetChoice.getItemCount()-1){
					leftTargetChoice.setSelectedIndex(1);															
				}
			}

		});

		rightSpellChoice.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox c=(JComboBox)e.getSource();
				Spell sp=getSpellFromMultiNameString((String)c.getSelectedItem());
				wizard.setNextRightSpell(sp);
				String next=sp.getNextStep(wizard.getRightPattern());
				rightHandChoice.setSelectedItem(next);
				if (next.equals("c") || next.equals("w") || next.equals("p")) {
					leftHandChoice.setSelectedItem(next);
				}
				else if (leftHandChoice.getSelectedItem().toString()!=leftHandChoice.getSelectedItem().toString().toUpperCase()) {
					leftHandChoice.setSelectedItem("-");
				}

				if (sp.getDefaultTarget()==Wizard.TARGET_SELF) {
					rightTargetChoice.setSelectedItem(Wizard.TARGET_SELF);				
				}
				else if (sp.getDefaultTarget()==Wizard.TARGET_NO_ONE) {
					rightTargetChoice.setSelectedItem(Wizard.TARGET_NO_ONE);										
				}
				else if (rightTargetChoice.getSelectedIndex()==0 || rightTargetChoice.getSelectedIndex()==rightTargetChoice.getItemCount()-1){
					rightTargetChoice.setSelectedIndex(1);															
				}
			}
		});
		
		rightTargetChoice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				wizard.setRightTarget((String)rightTargetChoice.getSelectedItem());
			}			
		});
		leftTargetChoice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				wizard.setLeftTarget((String)leftTargetChoice.getSelectedItem());
			}			
		});

		for (int a=0;a<targets.size();a++) {
			if (targets.elementAt(a).toString().equals(wizard.getRightTarget())) {
				rightTargetChoice.setSelectedIndex(a);
			}
			if (targets.elementAt(a).toString().equals(wizard.getLeftTarget())) {
				leftTargetChoice.setSelectedIndex(a);
			}
		}
		for (int rs=0;rs<rightAvailableSortedSpells.size();rs++) {
			Spell s=(Spell)rightAvailableSortedSpells.elementAt(rs);
			if (s==wizard.getRightSpell()) {
				rightSpellChoice.setSelectedIndex(rs);				
				break;
			}
		}
		for (int ls=0;ls<leftAvailableSortedSpells.size();ls++) {
			Spell s=(Spell)leftAvailableSortedSpells.elementAt(ls);
			if (s==wizard.getLeftSpell()) {
				leftSpellChoice.setSelectedIndex(ls);		
				break;
			}
		}

	}
	
	private void validateHand() {
		if (leftHandChoice.getSelectedItem().toString().equalsIgnoreCase("P") && rightHandChoice.getSelectedItem().toString().equalsIgnoreCase("P")) {
			leftHandChoice.setSelectedItem("p");
			rightHandChoice.setSelectedItem("p");
			rightSpellChoice.setSelectedIndex(0);
			leftSpellChoice.setSelectedIndex(0);
		}
		else {
			
		}
		if (leftHandChoice.getSelectedItem().toString().equalsIgnoreCase("V") && rightHandChoice.getSelectedItem().toString().equalsIgnoreCase("V")) {
			leftHandChoice.setSelectedItem("-");			
		}

	}
	
	private Spell getSpellFromMultiNameString(String string) {
		Vector v=Spell.getAllSpells();
		for (int a=0;a<v.size();a++) {
			Spell sp=(Spell)v.elementAt(a);
			if (string.indexOf(sp.getName())>=0) {
				return sp;
			}
		}
		return null;
	}
	
	
}
