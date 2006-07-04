package aj.misc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Description of the Class
 * 
 * @author judda
 * @created April 12, 2000
 */
public class Viewer extends JPanel implements MouseListener, ActionListener,
		ItemListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Vector imageGroupsIndex = new Vector();

	Vector imageGroupNameList = new Vector();

	Vector imageGroupFileNameList = new Vector();

	Vector imageNameList = new Vector();

	Vector imageFileNameList = new Vector();

	JFrame f = new JFrame();

	int delay = -1;

	JTextField delayTextField = new JTextField(7);

	// JButton next = new JButton(">>"), prev = new JButton("<<");
	JComboBox myCombo = new JComboBox();

	JCheckBox fullSize = new JCheckBox("Full");

	JCheckBox porpSize = new JCheckBox("Porp", true);

	JComboBox myGroupCombo = new JComboBox();

	// Vector masterFileList;
	ImageIcon i = null;

	ImageIcon p = null;

	ImageIcon n = null;

	int pnum, nnum;

	int dis = 0;

	int size = 400;

	public void addFileList(String s[]) {
		for (int a = 0; a < s.length; a++) {
			addFile(s[a]);
		}
		imageGroupNameList.addElement(imageNameList);
		imageGroupFileNameList.addElement(imageFileNameList);
		imageGroupsIndex.addElement("all");
	}

	public void addFile(String s) {
		File f = new File(s);
		if (f.isDirectory()) {
			File l[] = f.listFiles();
			addGroup(f.getName(), f.listFiles());
			for (int b = 0; b < l.length; b++) {
				addFile(f.listFiles()[b].toString());
			}
		} else if (f.getName().indexOf("Thumbs") < 0) {
			imageNameList.addElement(f.getName());
			imageFileNameList.addElement(s);
		}
	}

	public void addGroup(String group, File flist[]) {
		Vector v = new Vector();
		Vector namelist = new Vector();
		// v.addElement(group);
		for (int a = 0; a < flist.length; a++) {
			if (!flist[a].isDirectory()
					&& flist[a].getName().indexOf("Thumbs") < 0) {
				v.addElement(flist[a]);
				namelist.addElement(flist[a].getName());
			}
			if (flist[a].isDirectory()) {
				addGroup(flist[a], v, namelist);
			}
		}
		imageGroupsIndex.addElement(group);
		imageGroupNameList.addElement(namelist);
		imageGroupFileNameList.addElement(v);
	}

	public void addGroup(File f, Vector filelist, Vector fileName) {
		File fl[] = f.listFiles();
		for (int a = 0; a < fl.length; a++) {
			if (fl[a].getName().startsWith("Thumbs"))
				continue;
			else if (fl[a].isDirectory())
				addGroup(fl[a], filelist, fileName);
			else {
				filelist.addElement(fl[a]);
				fileName.addElement(fl[a].getName());
			}
		}
	}

	public Viewer(String s[]) {
		addMouseListener(this);
		s = options(s);
		guiSetup();
		addFileList(s);
		// masterFileList=imageFileNameList;

		myGroupCombo.setModel(new DefaultComboBoxModel(imageGroupsIndex));
		myGroupCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int x = myGroupCombo.getSelectedIndex();
				myCombo.setModel(new DefaultComboBoxModel(
						(Vector) imageGroupNameList.elementAt(x)));
				imageNameList = (Vector) imageGroupNameList.elementAt(x);
				imageFileNameList = (Vector) imageGroupFileNameList
						.elementAt(x);
				dis = 0;
				setImage(dis);
			}
		});

		myCombo.setModel(new DefaultComboBoxModel(imageFileNameList));
		myCombo.addActionListener(this);
		setImage(0);
		new Thread() {
			public void run() {
				while (true) {
					try {
						int localDelay = Integer.parseInt(delayTextField
								.getText()) * 10;
						if (localDelay > 0) {
							Thread.sleep(localDelay);
							setNext();
						}
						if (localDelay <= 0)
							Thread.sleep(2000);
					} catch (Exception e) {
					}
				}
			}
		}.start();
	}

	public void setImage(int x) {
		if (x == nextNum(dis)) {
			p = i;
			i = n;
			n = new ImageIcon(imageFileNameList.elementAt(nextNum(x))
					.toString());
		} else if (x == prevNum(dis)) {
			n = i;
			i = p;
			p = new ImageIcon(imageFileNameList.elementAt(prevNum(x))
					.toString());
		} else {
			i = new ImageIcon(imageFileNameList.elementAt(x).toString());
			n = new ImageIcon(imageFileNameList.elementAt(nextNum(x))
					.toString());
			p = new ImageIcon(imageFileNameList.elementAt(prevNum(x))
					.toString());
		}
		checkBadImageFile();
		dis = x;
		repaint();
		myCombo.setSelectedIndex(dis);
	}

	public void checkBadImageFile() {
		if (i == null) {
			// TODO drop bad image files
			// remove current image from list
			// change index size
			// load new image
		}
	}

	public void paint(Graphics g) {
		if (i != null) {
			Image I = i.getImage();
			g.clearRect(0, 0, getWidth(), getHeight());
			if (porpSize.isSelected()) {
				int x = getWidth();
				int y = getHeight();
				int iwidth = I.getWidth(null);
				int iheight = I.getHeight(null);
				if (1.0 * iwidth / iheight != 1.0 * x / y) {
					if ((int) (1.0 * iwidth / iheight * y) < x) {
						x = (int) (1.0 * iwidth / iheight * y);
					} else if ((int) (1.0 * iheight / iwidth * x) < y) {
						y = (int) (1.0 * iheight / iwidth * x);
					}
				}
				int sx = 0;
				if (x < getWidth()) {
					sx = (getWidth() - x) / 2;
				}
				g.drawImage(I, sx, 0, x, y, null);
			} else if (fullSize.isSelected()) {
				g.drawImage(I, 0, 0, null);
			} else {
				g.drawImage(I, 0, 0, getWidth(), getHeight(), null);
			}
		}
	}

	public void setPrev() {
		setImage(prevNum(dis));
	}

	public void setNext() {
		setImage(nextNum(dis));
	}

	public int nextNum(int dis) {
		dis++;
		if (dis >= imageFileNameList.size()) {
			dis = 0;
		}
		return dis;
	}

	public int prevNum(int dis) {
		dis--;
		if (dis < 0) {
			dis = imageFileNameList.size() - 1;
		}
		return dis;
	}

	public void itemStateChanged(ItemEvent e) {
		repaint();
	}

	public void actionPerformed(ActionEvent AE) {
		if (AE.getSource() instanceof JComboBox) {
			setImage(myCombo.getSelectedIndex());
		}
		// else if (AE.getSource() == prev) {
		// setPrev();
		// }
		// else {
		// setNext();
		// }
	}

	public static void help() {
		System.out.println("FORMAT: java Viewer2 <image . . . .> [options]");
		System.out.println("-t# time delay auto next (100= 1 secs)");
		System.out.println("-s# set defalut size");
	}

	public static void main(String s[]) {
		if (s.length == 0) {
			help();
			System.exit(0);
		}
		new Viewer(s);
	}

	public void guiSetup() {
		f = new JFrame();
		f.getContentPane().setLayout(new BorderLayout());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// next.addActionListener(this);
		// prev.addActionListener(this);
		fullSize.addItemListener(this);
		porpSize.addItemListener(this);
		JPanel cont = new JPanel(new BorderLayout());//
		JPanel top = new JPanel(new FlowLayout());
		// cont.add(prev);
		top.add(myGroupCombo);
		top.add(myCombo);
		cont.add("North", top);
		// cont.add(next);
		JPanel bot = new JPanel(new FlowLayout());
		bot.add(fullSize);
		bot.add(porpSize);
		bot.add(new JLabel("Speed"));
		bot.add(delayTextField);
		cont.add("South", bot);
		delayTextField.setText("" + delay);
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add("North", cont);
		f.getContentPane().add("Center", this);
		f.setSize(new Dimension(size, size));
		f.setVisible(true);
	}

	public String[] options(String s[]) {
		Vector v = new Vector();
		for (int a = 0; a < s.length; a++) {
			if (s[a].startsWith("-")) {
				if (s[a].toUpperCase().indexOf("-T") == 0) {
					s[a] = s[a].substring(2);
					try {
						delay = (int) (Double.parseDouble(s[a]) * 10);
						delayTextField.setText("" + s[a] + "0");
					} catch (NumberFormatException nfe) {
					}
				}
				if (s[a].toUpperCase().indexOf("-S") == 0) {
					s[a] = s[a].substring(2);
					try {
						size = Integer.parseInt(s[a]);
					} catch (NumberFormatException nfe) {
					}
				}
			} else {
				v.addElement(s[a]);
			}
		}
		String ss[] = new String[v.size()];
		v.copyInto(ss);
		return ss;
	}

	public void mouseClicked(MouseEvent arg0) {
		setNext();
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

}
