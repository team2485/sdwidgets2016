package team2485.smartdashboard.extension;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.color.ColorSpace;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.ColorProperty;
import edu.wpi.first.smartdashboard.properties.Property;

public class BackgroundColorSelector extends StaticWidget {

	public static final String NAME = "Background Color Selector";
	public final ColorProperty colorProp = new ColorProperty(this, "Background Color");

	private JLabel label;

	@Override
	public void propertyChanged(Property arg0) {
		getParent().setBackground(colorProp.getValue());
		label.setForeground(inverseColor(colorProp.getValue()));

		getParent().repaint();
	}

	@Override
	public void init() {
		label = new JLabel(NAME, JLabel.CENTER);
		
		add(label);
		
		Dimension size = label.getPreferredSize();
		
		size.height *= 2;

		setPreferredSize(size);
		setMaximumSize(size);
		setMinimumSize(size);

		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				if (getParent() != null) {
					label.setForeground(inverseColor(getParent().getBackground()));
					cancel();
				}
			}
		}, 0, 20);
	}

	private Color inverseColor(Color c) {

		float[] rgb = new float[] { c.getRed(), c.getGreen(), c.getBlue() };

		for (int i = 0; i < rgb.length; i++) {
			rgb[i] = 255 - rgb[i];
		}

		return new Color(rgb[0] / 255, rgb[1] / 255, rgb[2] / 255, c.getAlpha() / 255);
	}
}
