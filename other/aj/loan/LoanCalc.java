package aj.loan;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoanCalc implements ActionListener {
	public TextField loanTF,rateTF,numTF,payTF;
	public TextField startPrincTF,startIntTF;
	public TextField totalPayments,totalInterest;

	public LoanCalc() {
		Frame f=new Frame();
		f.addWindowListener(new aj.awt.SimpleWindowManager());
		
		loanTF=new TextField(15);
		rateTF=new TextField(5);
		numTF=new TextField(5);
		payTF=new TextField(8);

		startPrincTF=new TextField(8);
		startPrincTF.setEditable(false);
		Color cc=startPrincTF.getBackground().darker();
		startPrincTF.setBackground(cc);//Color.gray);
		startIntTF=new TextField(8);
		startIntTF.setEditable(false);
		startIntTF.setBackground(cc);//Color.gray);
		totalPayments=new TextField(12);
		totalPayments.setEditable(false);
		totalPayments.setBackground(cc);//Color.gray);
		totalInterest=new TextField(12);
		totalInterest.setEditable(false);
		totalInterest.setBackground(cc);//Color.gray);
		
		loanTF.setText("196600");
		rateTF.setText("7.875");
		numTF.setText("360");
		payTF.setText("-");
		Panel p=new Panel(new aj.awt.BoxLayout(0,2));
		p.add("North",new Label("Loan"));
		p.add("North",loanTF);
		p.add("North",new Label("Rate"));
		p.add("North",rateTF);
		p.add("North",new Label("Number of Payments"));
		p.add("North",numTF);
		p.add("North",new Label("Payment"));
		p.add("North",payTF);


		p.add("North",new Label("Payment Principel"));
		p.add("North",startPrincTF);
		p.add("North",new Label("Payment Interest"));
		p.add("North",startIntTF);
		p.add("North",new Label("TotalPayments"));
		p.add("North",totalPayments);
		p.add("North",new Label("TotalInterest"));
		p.add("North",totalInterest);
		f.add("Center",p);
		f.setVisible(true);
		f.pack();

		loanTF.addActionListener(this);
		rateTF.addActionListener(this);
		numTF.addActionListener(this);
		payTF.addActionListener(this);
	}

	String last="P";
	public void actionPerformed(ActionEvent ae) {
		double l=0;
		double r=0;
		int n=0;
		double p=0;
		String loanText,rateText,numText,payText;
		String t=loanTF.getText().trim();
		while (t.indexOf(",")>=0) t=t.substring(0,t.indexOf(","))+t.substring(t.indexOf(",")+1);
		if (t.startsWith("$")) t=t.substring(1);
		loanText=t;
		t=rateTF.getText().trim();
		while (t.indexOf(",")>=0) t=t.substring(0,t.indexOf(","))+t.substring(t.indexOf(",")+1);
		if (t.startsWith("$")) t=t.substring(1);
		rateText=t;
		t=numTF.getText().trim();
		while (t.indexOf(",")>=0) t=t.substring(0,t.indexOf(","))+t.substring(t.indexOf(",")+1);
		if (t.startsWith("$")) t=t.substring(1);
		numText=t;
		t=payTF.getText().trim();
		while (t.indexOf(",")>=0) t=t.substring(0,t.indexOf(","))+t.substring(t.indexOf(",")+1);
		if (t.startsWith("$")) t=t.substring(1);
		payText=t;
		
		try {
			boolean none=false;
			if (!loanText.equals("-") && !rateText.equals("-") &&
				!numText.equals("-") && !payText.equals("-")) {
				none=true;
			}
			if (loanText.equals("-") || (none && last.equals("L"))) {
				last="L";
				r=Math.max(0,Double.parseDouble(rateText)/100.0);
				n=(Math.max(1,Integer.parseInt(numText)));
				p=Double.parseDouble(payText);
				l=getLoan(r,n,p);
			}
			else if (rateText.equals("-") || (none && last.equals("R"))) {
				last="R";
				l=Double.parseDouble(loanText);
				n=Math.max(1,Integer.parseInt(numText));
				p=Double.parseDouble(payText);
				r=getRate(l,n,p);
			}
			else if (numText.equals("-") || (none && last.equals("N"))) {
				last="N";
				l=Double.parseDouble(loanText);
				r=Math.max(0,Double.parseDouble(rateText)/100);
				p=Double.parseDouble(payText);
				n=getNum(l,r,p);
			}
			else if (payText.equals("-") || (none && last.equals("P"))) {
				last="P";
				l=Double.parseDouble(loanText);
				r=Math.max(0,Double.parseDouble(rateText)/100);
				n=Math.max(1,Integer.parseInt(numText));
				p=getPayment(l,r,n);
			}
			else {
			}
			loanTF.setText(""+aj.misc.Stuff.money(l,2));
			rateTF.setText(""+aj.misc.Stuff.trunc(r*100.0,5));
			numTF.setText(""+n);
			payTF.setText(""+aj.misc.Stuff.money(p,2));
			result(l,r, n,p);
			double sp=p-l*r/12;
			double si=p-sp;
			startPrincTF.setText(""+aj.misc.Stuff.money(sp,2));
			startIntTF.setText(""+aj.misc.Stuff.money(si,2));
			totalPayments.setText(""+aj.misc.Stuff.money(p*n,2));
			totalInterest.setText(""+aj.misc.Stuff.money(p*n-l,2));
		} catch (NumberFormatException nfe) {
			System.out.println("number format exception found "+nfe);
		}
	}
	
	public static double getRemain(double loan, double rate, double num,double pay) {
		for (int a=0;a<num;a++) {
			loan=loan+loan*rate/12.0-pay;
		}
		return loan;
	}

	public static double getLoan(double rate, int num, double pay) {
		double best=pay;
		double remain=getRemain(best,rate,num,pay);
		int count=0;
		while (Math.abs(remain)>.01) {
			count++;
			if (remain<0)
				best=Math.max(best-remain/num/100,best*1.0001);
			if (remain>0)
				best=Math.min(best-remain/num/100,best*.9999);
			remain=getRemain(best,rate,num,pay);
		}
		//System.out.println("countd ="+count);
		return best;
	}

	public static double getRate(double loan, int num, double pay) {
		if (pay*num<loan) return -1;
		double best=0.001;
		double remain=getRemain(loan,best,num,pay);
		int count=0;
		while (Math.abs(remain)>pay/10) {
			count++;
			if (remain<0)
				best=best*1.001;
			if (remain>0)
				best=best*.999;
			remain=getRemain(loan,best,num,pay);
		}
		//System.out.println("countc = "+count);
		return best;
		
	}
	public static int getNum(double loan, double rate, double pay) {
		if (loan*rate/12>pay) return -1;
		int best=(int)(loan/pay);
		int count=0;
		double remain=getRemain(loan,rate,best,pay);
		while (remain>0) {
			count++;
			best=best+1;
			remain=getRemain(loan,rate,best,pay);
		}
		//System.out.println("countb = "+count);
		return best;
	}

	public static double getPayment(double loan, double rate, int num) {
		double best=loan;
		double remain=getRemain(loan,rate,num,best);
		int count=0;
		while (Math.abs(remain)>.01) {
			count++;
			if (remain<0)
				best=Math.min(best-remain/num/100.0,best*.9999);
			if (remain>0)
				best=Math.max(best-remain/num/100.0,best*1.0001);
			remain=getRemain(loan,rate,num,best);
		}
		//System.out.println("counta = "+count);
		return  best;
	}

	static boolean log=false;
	public static void main(String s[]) {
		if (s.length>0) {
			log=true;
			System.out.println("Usage: java aj.loan.LoanCalc [logon]");
		}
		new LoanCalc();
	}

	public void result(double l,double r, int n,double p) {
		double totalprin=0;
		double totalint=0;
		if (log) System.out.println("loan = "+aj.misc.Stuff.money(l,2)+" int ="+aj.misc.Stuff.trunc(r,4)+" payment = "+aj.misc.Stuff.money(p,2)+" term ="+n);
		for (int a=0;a<n;a++) {
			double totalpay=p*a;
			totalprin+=p-(l*r/12);
			totalint+=l*r/12;
			l=l+l*r/12-p;
			if (log) System.out.println("payment #"+(a+1)+" loan ballance"+aj.misc.Stuff.money(l,2)+" principal "+aj.misc.Stuff.money(p-(l*r/12),2)+"("+aj.misc.Stuff.money(totalprin,2)+") int="+aj.misc.Stuff.money(l*r/12,2)+"("+aj.misc.Stuff.money(totalint,2)+")");
		}
	}
/*
  principal &interest
  access loan
  morgage insurance         76
  home owners associations 100
*/
}
