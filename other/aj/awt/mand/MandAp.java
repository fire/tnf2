package aj.awt.mand;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import aj.awt.SimpleWindowManager;
/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class MandAp implements ActionListener {
	//applet params = size focus depth colors
	static String suffix = ".mand", imgSuffix = ".jpg";
	Mand myMand;
	MenuBar mb = new MenuBar();
	Menu mFile = new Menu ("File");
	MenuItem miNew = new MenuItem ("New");
	MenuItem miLoad = new MenuItem ("Load Location");
	MenuItem miSave = new MenuItem ("Save Location");
	MenuItem miSaveImage = new MenuItem ("Save Image");
	MenuItem miExit = new MenuItem ("Exit");
	Menu mEdit = new Menu ("Edit");
	MenuItem miSettings = new MenuItem ("Settings");
	MenuItem miPosition = new MenuItem ("Position");
	Frame F = new Frame();
	
	public static void main (String argv[]) {
		new MandAp (argv);
	}
	
	public MandAp (String argv[]) {
		miNew.addActionListener (this);
		miLoad.addActionListener (this);
		miSave.addActionListener (this);
		miSaveImage.addActionListener (this);
		miExit.addActionListener (this);
		miSettings.addActionListener (this);
		miPosition.addActionListener (this);
		mb.add (mFile);
		mFile.add (miNew);
		mFile.add (miLoad);
		mFile.add (miSave);
		mFile.add (miSaveImage);
		mFile.add (miExit);
		mb.add (mEdit);
		mEdit.add (miSettings);
		mEdit.add (miPosition);
		F.setMenuBar (mb);
		F.setLayout (new BorderLayout());
		ScrollPane SP = new ScrollPane();
		myMand = new Mand (SP);
		SP.add (myMand);
		F.add ("Center", SP);
		F.add ("South", myMand.TXstatus);
		F.setSize (new Dimension (600, 400));
		F.addWindowListener (new SimpleWindowManager());
		F.setVisible (true);
		if (argv.length == 0 || argv[0].indexOf ("?") >= 0) {
			System.out.println ("Format: java aj.awt.mand.MandAp [<mandfile.txt>]");
		}
		else {
			myMand.loadFile (argv[0]);
		}
	}
	
	public void actionPerformed (ActionEvent actionEvent) {
		if (actionEvent.getSource() == miNew) {
			myMand.setDefaultPosition();
		}
		if (actionEvent.getSource() == miLoad) {
			FileDialog fd = new FileDialog (F, "Load Location", FileDialog.LOAD);
			fd.setFile ("*" + suffix);
			fd.setVisible(true);//();
			String string = fd.getDirectory() + fd.getFile();
			if (string == null)return;
			myMand.loadFile (string);
		}
		if (actionEvent.getSource() == miSave) {
			FileDialog fd = new FileDialog (F, "Save Location", FileDialog.SAVE);
			fd.setFile ("*" + suffix);
			fd.setVisible(true);//how();
			String string = fd.getDirectory() + fd.getFile();
			myMand.TXstatus.setText ("Saving " + string);
			if (string == null)return;
			try {
				FileOutputStream fop = new FileOutputStream (string);
				String descr = myMand.getGml();
				fop.write (descr.getBytes());
				fop.flush();
				fop.close();
			}
			catch (IOException IOE183) {
				System.out.println ("MyError: Cannot Save description file. Save abort.");
				myMand.TXstatus.setText ("Error Saving " + string);
				return;
			}
			myMand.TXstatus.setText (string + " Saved. Ready.");
		}
		if (actionEvent.getSource() == miSaveImage) {
			FileOutputStream fo = null;
			FileDialog fd = new FileDialog (F, "Save Image", FileDialog.SAVE);
			fd.setFile ("*" + imgSuffix);
			fd.setVisible(true);//();
			String string = fd.getDirectory() + fd.getFile();
			if (string == null)return;
			myMand.TXstatus.setText ("Saving " + string);
			try {
				fo = new FileOutputStream (string);
			}
			catch (FileNotFoundException FNFE) {
				System.out.println ("MyError: Bad file. Save abort.");
				myMand.TXstatus.setText ("Error Saving " + string);
				return;
			}
			try {
				myMand.encode (fo);
				fo.close();
			}
			catch (IOException IOE) {
				System.out.println ("MyError: Cannot write Bad file. Save abort.");
				myMand.TXstatus.setText ("Error Saving " + string);
				return;
			}
			myMand.TXstatus.setText (string + " Saved. Ready.");
		}
		if (actionEvent.getSource() == miExit) {
			System.exit (0);
		}
		if (actionEvent.getSource() == miSettings) {
			myMand.settingFrame.setVisible(true);//();
		}
		if (actionEvent.getSource() == miPosition) {
			myMand.positionFrame.setVisible(true);//();
		}
	}
}
/*
edit
  location
  settings

location x,y,x,y
*/
