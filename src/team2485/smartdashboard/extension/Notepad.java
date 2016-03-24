package team2485.smartdashboard.extension;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.BooleanProperty;
import edu.wpi.first.smartdashboard.properties.Property;

public class Notepad extends StaticWidget implements Serializable {

	private static final long serialVersionUID = 1L;

	private static long UIDCount = 1;

	private long UID;
	private JTextPane textPane;

	@Override
	public void propertyChanged(Property arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {

		UID = UIDCount;

		UIDCount++;

		textPane = new JTextPane();

		textPane.setVisible(true);

		this.add(textPane);

		loadNotes();

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {

					saveNotes();

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void loadNotes() {
		File loadFile = null;

		try {
			loadFile = new File(getClass().getResource("/SavedNote-" + UID + ".txt").toURI());
		} catch (URISyntaxException | NullPointerException e) {
			e.printStackTrace();
			return;
		}

		Object loaded = null;

		FileInputStream f_in;
		try {

			f_in = new FileInputStream(loadFile.getCanonicalPath());
			ObjectInputStream obj_in = new ObjectInputStream(f_in);

			loaded = obj_in.readObject();
			obj_in.close();

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		Notepad casted = (Notepad) loaded;

		textPane = casted.textPane;
	}

	private void saveNotes() {

		File saveDir = null;

		try {
			saveDir = new File(getClass().getResource("/").toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
			
			System.out.println("Path: " + getClass().getResource("/"));
			
		}

		try {

			FileOutputStream fos = new FileOutputStream(saveDir.getCanonicalPath() + "/SavedNote-" + UID + ".txt");
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(this);
			oos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
