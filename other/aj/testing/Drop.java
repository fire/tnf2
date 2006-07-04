/*
 * Created on Jun 28, 2004
 *
 */
package aj.testing;

import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 * @author judda
 * 
 */
public class Drop {
	JFrame jf = new JFrame();

	JTextField text = new JTextField("drop here");

	public static void main(String[] args) {
		new Drop();
	}

	public Drop() {
		text.setToolTipText("Type in URL here or drag from browser to here.");
		new DropTarget(text, DnDConstants.ACTION_COPY_OR_MOVE,
				new DropTargetListener() {
					public void drop(DropTargetDropEvent dtde) {
						dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
						Transferable tr = dtde.getTransferable();
						if (tr == null)
							return;
						try {
							DataFlavor stringFlavor = DataFlavor.stringFlavor;
							if (dtde.isDataFlavorSupported(stringFlavor)) {
								String name = (String) tr
										.getTransferData(stringFlavor);
								System.out.println(name);
								text.setText(name);
								dtde.acceptDrop(DnDConstants.ACTION_LINK);
								dtde.dropComplete(true);
							} else {
								dtde.rejectDrop();
							}
						} catch (Exception e) {
						}
					}

					public void dragEnter(DropTargetDragEvent dtde) {
						dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
					}

					public void dragExit(DropTargetEvent dte) {
					}

					public void dragOver(DropTargetDragEvent dtde) {
					}

					public void dropActionChanged(DropTargetDragEvent dtde) {
					}

				});
		jf.getContentPane().add("Center", text);
		jf.setSize(new Dimension(200, 100));
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
