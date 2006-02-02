package aj.iem;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import aj.misc.Stuff;

public class Plan implements ActionListener {
	
	int maxFields=10;
	JFrame jFrame;
	JTextField nameT[]=new JTextField[maxFields];
	JTextField valueT[]=new JTextField[maxFields];
	JTextField bidT[]=new JTextField[maxFields];
	JTextField askT[]=new JTextField[maxFields];
	JTextField bidQT[]=new JTextField[maxFields];
	JTextField askQT[]=new JTextField[maxFields];
	JTextField bidMargin=new JTextField("  0.50"),
		askMargin=new JTextField("  0.50"),
		bidRisk=new JTextField("  1.50"),
		askRisk=new JTextField("  1.50");
	JComboBox select;
	int activeFields=2;
	
	JPanel bidMarginRow=new JPanel(new FlowLayout());
	JPanel askMarginRow=new JPanel(new FlowLayout());
	JPanel marginManager=new JPanel(new BorderLayout());
	JPanel valueDisplay=new JPanel(new GridLayout(0,6));
	JPanel everything=new JPanel(new BorderLayout());

	public static void main(String s[]){
		new Plan();
	}
	
	public Plan() {
		setupGui();
	}

	public void updateGui() {
		valueDisplay.removeAll();
		valueDisplay.add(new JLabel("Name"));
		valueDisplay.add(new JLabel("QBid"));
		valueDisplay.add(new JLabel("Bid"));
		valueDisplay.add(new JLabel("Value"));
		valueDisplay.add(new JLabel("Ask"));
		valueDisplay.add(new JLabel("QAsk"));
		for (int a=0;a<activeFields;a++) {
			if (nameT[a]==null) nameT[a]=new JTextField();
			nameT[a].setText("Field"+(char)('a'+a));
			if (bidQT[a]==null) bidQT[a]=new JTextField();
			if (bidT[a]==null) bidT[a]=new JTextField();
			if (valueT[a]==null) {
				valueT[a]=new JTextField();
				valueT[a].addActionListener(this);
				valueT[a].setText(""+Stuff.trunc(1.0/activeFields,3));
			}
			if (askT[a]==null) askT[a]=new JTextField();
			if (askQT[a]==null) askQT[a]=new JTextField();
			valueDisplay.add(nameT[a]);		
			valueDisplay.add(bidQT[a]);
			valueDisplay.add(bidT[a]);
			valueDisplay.add(valueT[a]);
			valueDisplay.add(askT[a]);
			valueDisplay.add(askQT[a]);
		}
		int c=select.getSelectedIndex();
		if (c==0) {
			nameT[0].setText("MSFT_H");
			nameT[1].setText("MSFT_L");
		}
		else if (c==1) {
			nameT[0].setText("FR_UP");
			nameT[1].setText("FR_SAME");
			nameT[2].setText("FR_DOWN");
		}
		else if (c==2) {
			nameT[0].setText("AAPL");
			nameT[1].setText("IBM");
			nameT[2].setText("MSFT");
			nameT[3].setText("GSPC");
		}
		else if (c>2) {
			for (int a=0;a<c+2;a++) {
				nameT[a].setText("Field_"+(char)('a'+a));
			}
		}
		for (int a=activeFields;a<maxFields;a++) {
			if (nameT[a]!=null) nameT[a].setText("");
			if (bidQT[a]!=null) bidQT[a].setText("");
			if (bidT[a]!=null) bidT[a].setText("");
			if (valueT[a]!=null) valueT[a].setText("");
			if (askT[a]!=null) askT[a].setText("");
			if (askQT[a]!=null) askQT[a].setText("");
		}
		valueDisplay.revalidate();
	}
	
	public void setupGui() {
		bidMarginRow.add(bidRisk);
		bidMarginRow.add(new JLabel("Bid Risk"));
		bidMarginRow.add(bidMargin);
		bidMarginRow.add(new JLabel("Bid Margin"));

		askMarginRow.add(askRisk);
		askMarginRow.add(new JLabel("Ask Risk"));
		askMarginRow.add(askMargin);
		askMarginRow.add(new JLabel("Ask Margin"));

		marginManager.add("North",bidMarginRow);
		marginManager.add("Center",askMarginRow);
		String all[]={"2-MSFT_Price","3-FedPolicyB","4-Comp_Ret","5-","6-","7-","8-"};
		select=new JComboBox(all);
		marginManager.add("South",select);
		select.addActionListener(this);

		everything.add("North",marginManager);
		updateGui();
		everything.add("Center",valueDisplay);
		jFrame=new JFrame("Market Value Planning Tool");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );

		jFrame.getContentPane().add(everything);
		jFrame.setVisible(true);
//		jFrame.pack();
		jFrame.setSize(new Dimension(600,400));
	}
	
	public void actionPerformed(ActionEvent ae) {
		activeFields=select.getSelectedIndex()+2;
		updateGui();
		double v[]=new double[activeFields];
		double bv[]=new double[activeFields];
		double av[]=new double[activeFields];
		double sumv=0;
		for (int a=0;a<activeFields;a++) {
			v[a]=Math.min(1,Math.max(0,Double.parseDouble(Stuff.superTrim(valueT[a].getText(),",$ \t\n"))));
			sumv+=v[a];
		}

		double bdelta=Double.parseDouble(Stuff.superTrim(bidMargin.getText(),",$ \t\n"));
		double adelta=Double.parseDouble(Stuff.superTrim(askMargin.getText(),",$ \t\n"));

		double bRisk=Double.parseDouble(Stuff.superTrim(bidRisk.getText(),",$ \t\n"));
		double aRisk=Double.parseDouble(Stuff.superTrim(askRisk.getText(),",$ \t\n"));
		
		for (int a=0;a<activeFields;a++) {
			v[a]=v[a]/sumv;
			valueT[a].setText(""+Stuff.trunc(v[a],3));
			
			double tb=Math.max(0.001,Math.min(.999,v[a]*(1-bdelta)));			
			bidT[a].setText(""+Stuff.trunc(tb,3));
			double ta=Math.max(0.001,Math.min(.999,v[a]+v[a]*adelta));
			askT[a].setText(""+Stuff.trunc(ta,3));
			int bq=Math.min(100,(int)(bRisk/tb));
			bidQT[a].setText(""+Stuff.trunc(bq,3));
			int aq=Math.min(100,(int)(aRisk/(1-ta)));
			askQT[a].setText(""+Stuff.trunc(ta,3));
		}
		for (int a=0;a<activeFields;a++) {
//			System.out.println("bid "+nam1.getText()+" ~"+Stuff.trunc(ab,3)+" ~"+aaplQBid.getText()+" 1");
//			System.out.println("ask "+nam1.getText()+" ~"+Stuff.trunc(aa,3)+" ~"+aaplQAsk.getText()+" 1");
			
		}
	}
}