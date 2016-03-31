package team2485.smartdashboard.extension;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.Property;

public class RedDot extends StaticWidget {

	public static final String NAME = "Red Dot";

	@Override
	public void propertyChanged(Property arg0) {
	}

	@Override
	public void init() {

		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				repaint();
			}
		}, 0, (long) (1000.0 / 30));
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.RED);

		g.fillOval(0, 0, getWidth(), getHeight());
	}
}
