package team2485.smartdashboard.extension;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.Property;

public class Notepad extends Widget {

	private JTabbedPane tabbedPane;

	@Override
	public void propertyChanged(Property arg0) {

	}

	@Override
	public void setValue(Object arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {

		tabbedPane = new JTabbedPane();
		
		tabbedPane.setBackground(new Color(0x111111));
		
		tabbedPane.addTab("", new JTextField());

		addNewTab();

		new UpdateThread().start();
	}

	private void addNewTab() {
		tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), "Note " + (tabbedPane.getSelectedIndex() + 1));

		JTextField newTextField = new JTextField();
		
		newTextField.setSelectedTextColor(Color.YELLOW);
		newTextField.setCaretColor(Color.YELLOW);
		
		tabbedPane.addTab("Create New Note", newTextField);
	}

	class UpdateThread extends Thread {

		@Override
		public void run() {
			while (tabbedPane.getTabCount() > 0) {

				if (tabbedPane.getSelectedIndex() == tabbedPane.getTabCount() - 1) {
					addNewTab();
				}
				
				tabbedPane.repaint();
				
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
			}
		}
	}
}
