package aj.nf;
/*
Note require special compile at 1.1.8 or older and jar of
UniVar
UniVarApplet
*/

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UniVarApplet extends Applet implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static TextField arg1=new TextField(10);
	private static TextField arg2=new TextField(10);
	private static TextField arg3=new TextField(10);
	private static TextField arg4=new TextField(10);
	private static TextField arg5=new TextField(10);
	private static TextField work=new TextField(10);
	private static Button doit=new Button("Compute");
	private static TextField res=new TextField(10);

	public UniVarApplet() {
		Panel top=new Panel(new GridLayout(0,2));
		top.add(new Label("UniVar Playground"));
		top.add(new Label("version 1.1"));
		top.add(new Label("$arg1="));
		top.add(arg1);
		top.add(new Label("$arg2="));
		top.add(arg2);
		top.add(new Label("$arg3="));
		top.add(arg3);
		top.add(new Label("$arg4="));
		top.add(arg4);
		top.add(new Label("$arg5="));
		top.add(arg5);
	
		setLayout(new BorderLayout());
		add("North",top);
	
		Panel bot=new Panel(new GridLayout(3,0));
		bot.add(doit);
		bot.add(work);
		bot.add(res);
	
		doit.addActionListener(this);
	
		add("South",bot);
  	}

	public void actionPerformed(ActionEvent a) {
		System.out.println("action!");
		String w=work.getText();
		w=sub(w,"$arg1",arg1.getText());
		w=sub(w,"$arg2",arg2.getText());
		w=sub(w,"$arg3",arg3.getText());
		w=sub(w,"$arg4",arg4.getText());
		w=sub(w,"$arg5",arg5.getText());
		w=UniVar.getValue(w)+"";
		while(w.indexOf("\"") >= 0) {
			w = w.substring(0, w.indexOf("\"")) + w.substring(w.indexOf("\"") + 1);
		}
		res.setText(w);
		System.out.println(w);
	}

	private String sub(String s,String old,String old2) {
		old=old.toLowerCase();//ARG1 == arg1
		//old2=old2.toLowerCase();// Test_Val != test_val
		if (old2.indexOf("$")>=0) return s;
		while(s.toLowerCase().indexOf(old) >= 0) {
			s = s.substring(0, s.toLowerCase().indexOf(old)) + old2 + s.substring(s.toLowerCase().indexOf(old) + old.length());
		}
		return s;
	}

}
