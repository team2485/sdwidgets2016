package team2485.smartdashboard.extension;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Predicate;

import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.DoubleProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;

public class AltLidarScanner extends Widget {

	public static final DataType[] TYPES = { DataType.STRING };

	public final DoubleProperty distanceProperty = new DoubleProperty(this,
			"CM Per Pixel", 3);
	
	public final DoubleProperty numberOfRings = new DoubleProperty(this, "Number Of Rings", 4);

	private ArrayList<AltLidarPing> lidarPings;

	private double robotHeading = 0;

	private final int[][] robotShape = new int[][] { { 0, 5, -5 },
			{ 10, -10, -10 } };

	@Override
	public void propertyChanged(Property arg0) {

		double newDist = distanceProperty.getValue();

		AltLidarPing.SCALE_FACTOR = 1 / newDist;

	}

	@Override
	public void setValue(Object arg0) {

		repaint();

		String stringForm = (String) arg0;

		String[] stringValues = stringForm.split(":");

		for (int i = 1; i < stringValues.length; i++) {

			String curString = stringValues[i];

			String[] twoValues = curString.split(",");

			double value1 = Double.parseDouble(twoValues[0]);

			double value2 = Double.parseDouble(twoValues[1]);

			if (i == 1) {

				adjustPointsForMovement(value1);

				robotHeading += value2;

			} else {

				AltLidarPing newPing = new AltLidarPing(value1, value2);

				for (int j = 0; j < lidarPings.size(); j++) {

					AltLidarPing curPing = lidarPings.get(j);

					if (curPing.equals(newPing)) {
						lidarPings.remove(j);
						j--;
					}
				}

				lidarPings.add(newPing);
			}
		}

		repaint();
	}

	private void adjustPointsForMovement(double forwardMovement) {

		for (AltLidarPing lidarPing : lidarPings) {

			lidarPing.adjustForRobotMovement(forwardMovement);

		}
	}

	@Override
	public void paintComponent(Graphics g) {

		Dimension dimensions = new Dimension(
				Math.min(getWidth(), getHeight()) - 1, Math.min(getWidth(),
						getHeight()) - 1);

		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(Color.GREEN);

		g2d.translate(dimensions.width / 2, dimensions.height / 2);

		int numOfRings = (int) Math.round(numberOfRings.getValue());
		
		numberOfRings.setValue(numOfRings);

		for (int i = 1; i < numOfRings + 1; i++) {

			int radius = Math.min(getWidth(), getHeight()) / 2 / numOfRings * i;

			g2d.drawOval(-radius, -radius, radius * 2, radius * 2);

			g2d.drawString(i * radius + " CM", -10, radius - 10);

		}

		g2d.rotate(Math.toRadians(robotHeading));

		g2d.draw(new Polygon(robotShape[0], robotShape[1], robotShape[0].length));

		for (AltLidarPing curPing : lidarPings) {
			curPing.draw(g2d);
		}

		g2d.rotate(-Math.toRadians(robotHeading));

		g2d.setColor(Color.RED);

		if (lidarPings.isEmpty()) {
			g2d.drawString("NO DATA", 0, -50);
		} else {

			AltLidarPing latestPing = lidarPings.get(lidarPings.size() - 1);
			
			g2d.drawString((int) latestPing.getDistance() + " CM", -10, -20);

			if (latestPing.getX() == 0 && latestPing.getY() == 0) {

				boolean nack = true;

				for (AltLidarPing curPing : lidarPings) {

					if (curPing.getX() != 0 || curPing.getY() != 0) {
						nack = false;
						break;
					}
				}

				if (nack) {
					g2d.drawString("NACK", 0, -50);
				}

			} else {

				g2d.setColor(Color.GREEN);

				double angle = latestPing.getRelativeAngle();

				double length = Math.min(getWidth(), getHeight()) / 2;

				g2d.drawLine(
						0,
						0,
						(int) Math.round(Math.cos(Math.toRadians(angle))
								* length),
						(int) Math.round(Math.sin(Math.toRadians(angle))
								* length));

			}
		}
	}

	@Override
	public void init() {
		lidarPings = new ArrayList<AltLidarPing>();

		new PaintThread().start();

	}

	private class PaintThread extends Thread {

		@Override
		public void run() {

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			while (true) {

				repaint();

				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}