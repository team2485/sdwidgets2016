package team2485.smartdashboard.extension;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import edu.wpi.first.smartdashboard.gui.elements.VideoStreamViewerExtension;
import edu.wpi.first.smartdashboard.properties.NumberProperty;

public class AlignmentWidget extends VideoStreamViewerExtension {
	
	public static final String NAME = "Logicless Alignment Widget";

	NumberProperty linePos = new NumberProperty(this, "Line Location", 0.75);

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(Color.RED);
		g2d.setStroke(new BasicStroke(5));

		g2d.drawLine((int) ((double) linePos.getValue() * getWidth()), 0,
				(int) ((double) linePos.getValue() * getWidth()), getHeight());

	}
}
