package aj.games;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import aj.awt.SimpleWindowManager;
import aj.misc.Stuff;

public class Anogram implements ActionListener {
  TextField use=new TextField(20),used=new TextField(20);
  TextArea work=new TextArea(20,20);
  Button check=new Button("Check");

  public static void main(String s[]) {
    new Anogram();
  }

  public Anogram() {
    Frame f=new Frame();
    f.addWindowListener(new SimpleWindowManager());
    Panel p=new Panel(new FlowLayout());
    p.add(use);
    p.add(used);
    p.add(check);
    f.add("North",p);
    f.add("Center",work);
    check.addActionListener(this);
    f.setSize(400,400);
    f.setVisible(true);
    used.setEditable(false);
use.setText(",./=?abcdefghijklmnopqrstuvwxyz");
  }

  public void actionPerformed(ActionEvent ae) {
    String s,t,u,ss;
System.out.println("check!");
    if (ae.getSource()==check) {
      s=use.getText();
      t=work.getText();
      u="";
      for (int a=0;a<s.length();a++) {
        ss=s.substring(a,a+1).toLowerCase();
        if (t.toLowerCase().indexOf(ss)<0) u+=ss;
      }
      t=Stuff.superTrim(t);
      used.setText(t.length()+": "+u);
    }
  }
  
}