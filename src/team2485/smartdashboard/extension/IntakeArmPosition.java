package team2485.smartdashboard.extension;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.BooleanProperty;
import edu.wpi.first.smartdashboard.properties.NumberProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;

public class IntakeArmPosition extends Widget {

	public static final String NAME = "Intake Arm Position Widget";
	public static final DataType[] TYPES = { DataType.STRING };

	private static final double DEFAULT_FLOOR_ANGLE = 0.1;
	private double curAngle = DEFAULT_FLOOR_ANGLE;

//	private final Property floorPosProp = new NumberProperty(this, "Floor Angle", DEFAULT_FLOOR_ANGLE);
//	private final Property curAngleProp = new NumberProperty(this, "Cur Angle", DEFAULT_FLOOR_ANGLE);

	private double floorPos = DEFAULT_FLOOR_ANGLE;
	private double intakePos = (DEFAULT_FLOOR_ANGLE + 0.12) % 1;
	private double upPos = (DEFAULT_FLOOR_ANGLE + 0.352) % 1;

	private final double MAX_ERROR = 5 / 360.0;

	private BufferedImage armImage;
	private final int pivotX = 33, pivotY = 131;

	@Override
	public void propertyChanged(Property arg0) {
//		intakePos = ((double) floorPosProp.getValue() + 0.12) % 1;
//		curAngle = (double) curAngleProp.getValue();

		repaint();
	}
	
	/*
	 * Expects values separated by a comma:
	 * 
	 * Current Angle,Encoder Reading for Floor,Reading for Intake,Reading for Full Up
	 */
	@Override
	public void setValue(Object arg0) {
		String input = (String) arg0;

		String[] data = input.split(",");

		curAngle = Double.parseDouble(data[0]);
		floorPos = Double.parseDouble(data[1]);
		intakePos = Double.parseDouble(data[2]);
		upPos = Double.parseDouble(data[3]);
	}

	@Override
	public void init() {

		try {
			armImage = ImageIO.read(getClass().getResource("/team2485/smartdashboard/extension/res/intakeImage.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Dimension startDimension = new Dimension(200, 400);

		setSize(startDimension);
		setPreferredSize(startDimension);

		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				repaint();
			}
		}, 0, (long) (1000.0 / 60));
	}

	@Override
	public void paintComponent(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(Color.YELLOW);
		g2d.setFont(new Font("BOOMBOX", Font.PLAIN, 35));

		FontMetrics fontMetrics = g2d.getFontMetrics();
		
		boolean isOnFloor = false;
		if (floorPos > 0.1) {
			if (curAngle < floorPos
					&& Math.abs(curAngle - floorPos) < 0.1) {
				isOnFloor = true;
			}
		} else {
			if (curAngle < floorPos
					|| Math.abs((curAngle - 1) - floorPos) < 0.1) {
				isOnFloor = true;
			}
		}

		boolean isFullUp = false;
		if (upPos < 0.9) {
			if (curAngle > upPos
					&& Math.abs(curAngle - upPos) < 0.1) {
				isFullUp = true;
			}
		} else {
			if (curAngle > upPos
					|| Math.abs((curAngle + 1) - upPos) < 0.1) {
				isFullUp = true;
			}
		}

		if (isOnFloor) {
			g2d.drawString("ON FLOOR", 10, getHeight() - 10);
		} else if (isFullUp) {
			g2d.drawString("FULL BACK", getWidth() - fontMetrics.stringWidth("FULL BACK") - 10,
					fontMetrics.getHeight());
		}

		int imageX = (int) (getWidth() * 0.1);
		int imageY = (int) (getHeight() * 0.25);

		double scaleFactor = (getWidth() - imageX) / (double) (armImage.getWidth() + armImage.getHeight());

		g2d.translate(pivotX + imageX, pivotY + imageY);
		g2d.rotate(-(curAngle - intakePos) * 6.28319f);
		g2d.scale(scaleFactor, scaleFactor);
		g2d.drawImage(armImage, -pivotX, -pivotY, null);
	}
}