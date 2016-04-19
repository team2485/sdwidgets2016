package team2485.smartdashboard.extension;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.BooleanProperty;
import edu.wpi.first.smartdashboard.properties.IntegerProperty;
import edu.wpi.first.smartdashboard.properties.NumberProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.properties.StringProperty;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class TrimWidget extends Widget implements ActionListener, KeyListener {

	public static final String NAME = "Value Trimmer";

	public static final DataType[] TYPES = { DataType.NUMBER };

	public final StringProperty dataName = new StringProperty(this,
			"Value Name", "Value");

	public final StringProperty leftButtonText = new StringProperty(this,
			"Left Button Text", "<--");
	public final StringProperty rightButtonText = new StringProperty(this,
			"Right Button Text", "-->");

	public final BooleanProperty inverted = new BooleanProperty(this,
			"Inverted", false);

	public final NumberProperty unitsPerClick = new NumberProperty(this,
			"Units Per Click", 1);

	private JButton leftButton;
	private JTextField trimValue;
	private JButton rightButton;
	private JLabel label;

	private double value;

	@Override
	public void propertyChanged(Property arg0) {

		if (arg0.equals(dataName)) {
			label.setText(dataName.getValue());
		}

		if (arg0.equals(leftButtonText)) {
			leftButton.setText(leftButtonText.getValue());
		}

		if (arg0.equals(rightButtonText)) {
			rightButton.setText(rightButtonText.getValue());
		}
	}

	@Override
	public void setValue(Object arg0) {
		value = (double) arg0;

		if (value != Double.parseDouble(trimValue.getText().trim())) {
			trimValue.setText(value + "");
		}
	}

	@Override
	public void init() {

		setLayout(new BorderLayout());

		leftButton = new JButton("<--");
		trimValue = new JTextField(value + "");
		rightButton = new JButton("-->");
		label = new JLabel(dataName.getValue());

		trimValue.setEditable(true);
		trimValue.setHorizontalAlignment(SwingConstants.CENTER);

		label.setHorizontalAlignment(SwingConstants.CENTER);

		leftButton.addActionListener(this);
		trimValue.addKeyListener(this);
		rightButton.addActionListener(this);

		add(leftButton, BorderLayout.LINE_START);
		add(trimValue, BorderLayout.CENTER);
		add(rightButton, BorderLayout.LINE_END);
		add(label, BorderLayout.PAGE_START);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(leftButton)) {
			value = inverted.getValue() ? value
					+ unitsPerClick.getValue().doubleValue() : value
					- unitsPerClick.getValue().doubleValue();
			trimValue.setText(value + "");
		}

		if (e.getSource().equals(rightButton)) {
			value = inverted.getValue() ? value
					- unitsPerClick.getValue().doubleValue() : value
					+ unitsPerClick.getValue().doubleValue();
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
				value = Double.parseDouble(trimValue.getText().trim());
			} catch (NumberFormatException ex) {
				return;
			}
		}

		NetworkTable.getTable("SmartDashboard").putNumber(dataName.getValue(),
				value);
	}
}
