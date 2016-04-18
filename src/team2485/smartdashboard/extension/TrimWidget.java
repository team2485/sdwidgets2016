package team2485.smartdashboard.extension;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.properties.StringProperty;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class TrimWidget extends Widget implements ActionListener, KeyListener {
	
	public static final String NAME = "Value Trimmer";

	public static final DataType[] TYPES = { DataType.NUMBER };

	public final StringProperty dataName = new StringProperty(this,
			"Value Name", "Value");

	private JButton trimLeft;
	private JTextField trimValue;
	private JButton trimRight;

	private int value;

	@Override
	public void propertyChanged(Property arg0) {
	}

	@Override
	public void setValue(Object arg0) {
		value = (int) arg0;
	}

	@Override
	public void init() {

		setLayout(new BorderLayout());

		trimLeft = new JButton("<--");
		trimValue = new JTextField(value + "");
		trimRight = new JButton("-->");

		trimValue.setEditable(true);
		trimValue.setHorizontalAlignment(JTextField.CENTER);

		trimLeft.addActionListener(this);
		trimValue.addKeyListener(this);
		trimRight.addActionListener(this);

		add(trimLeft, BorderLayout.LINE_START);
		add(trimValue, BorderLayout.CENTER);
		add(trimRight, BorderLayout.LINE_END);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(trimLeft)) {
			value--;
			trimValue.setText(value + "");
		}

		if (e.getSource().equals(trimRight)) {
			value++;
			trimValue.setText(value + "");
		}

		NetworkTable.getTable("SmartDashboard").putNumber(dataName.getValue(),
				value);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getSource().equals(trimValue)) {
			
			try {
				value = Integer.parseInt(trimValue.getText().trim());
			} catch (NumberFormatException ex) {
				return;
			}
		}
		
		NetworkTable.getTable("SmartDashboard").putNumber(dataName.getValue(),
				value);
	}
}
