package team2485.smartdashboard.extension;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.BooleanProperty;
import edu.wpi.first.smartdashboard.properties.Property;

public class Notepad extends Widget {

	private JTabbedPane tabbedPane;

	private BooleanProperty autoDelete = new BooleanProperty(this, "Auto-Delete Empty Tabs", true);

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

		try {
			loadNotes();
		} catch (IOException e) {
			addNewTab();
		}

		new UpdateThread().start();
	}

	private void addNewTab() {
		tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), "Note " + (tabbedPane.getSelectedIndex() + 1));

		JTextField newTextField = new JTextField();

		newTextField.setSelectedTextColor(Color.YELLOW);
		newTextField.setCaretColor(Color.YELLOW);

		tabbedPane.addTab("Create New Note", newTextField);
	}

	private void saveNotes() throws IOException {

		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(new File(getClass().getResource("SavedNotes.txt").toURI()));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}

		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

		for (int i = 0; i < tabbedPane.getTabCount() - 1; i++) {

			bufferedWriter.write("---NEW NOTE---");
			bufferedWriter.write(tabbedPane.getTitleAt(i));
			bufferedWriter.newLine();
			bufferedWriter.write(((JTextField) tabbedPane.getTabComponentAt(i)).getText());
			bufferedWriter.newLine();
		}
	}

	private void loadNotes() throws IOException {

		FileReader fileReader = null;
		try {
			fileReader = new FileReader(new File(getClass().getResource("SavedNotes.txt").toURI()));
		} catch (FileNotFoundException | URISyntaxException e) {
			e.printStackTrace();
		}

		BufferedReader bufferedReader = new BufferedReader(fileReader);

		String curLine = bufferedReader.readLine();

		while (curLine != null) {

			if (curLine.equals("---NEW NOTE---")) {
				addNewTab();
			} else {

				JTextField curField = (JTextField) tabbedPane.getTabComponentAt(tabbedPane.getTabCount() - 2);

				curField.setText(curField.getText() + curLine);
			}
		}
	}

	class UpdateThread extends Thread {

		@Override
		public void run() {
			while (tabbedPane.getTabCount() > 0) {

				if (tabbedPane.getSelectedIndex() == tabbedPane.getTabCount() - 1) {
					addNewTab();
				}

				if (autoDelete.getValue()) {

					for (int i = 0; i < tabbedPane.getTabCount() - 1; i++) {
						if (i != tabbedPane.getSelectedIndex()
								&& ((JTextField) tabbedPane.getTabComponentAt(i)).getText().equals("")) {
							
							tabbedPane.removeTabAt(i);
							i--;
							
							for (int j = i; j < tabbedPane.getTabCount() - 1; j++) {
								JTextField curText = (JTextField) tabbedPane.getSelectedComponent();
							
								tabbedPane.insertTab("Note " + j, null, curText, null, j);
								tabbedPane.removeTabAt(j + 1);
							}
						}
					}
				}

				try {
					saveNotes();
				} catch (IOException e) {
				}

				tabbedPane.repaint();

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
