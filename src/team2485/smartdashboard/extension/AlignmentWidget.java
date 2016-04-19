package team2485.smartdashboard.extension;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import edu.wpi.first.smartdashboard.gui.elements.VideoStreamViewerExtension;
import edu.wpi.first.smartdashboard.properties.BooleanProperty;
import edu.wpi.first.smartdashboard.properties.NumberProperty;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.TableKeyNotDefinedException;

public class AlignmentWidget extends VideoStreamViewerExtension {

	public static final String NAME = "Logicless Alignment Widget";

	public final NumberProperty linePosBatter = new NumberProperty(this,
			"Batter Shot Line", 240);
	public final NumberProperty linePosLong = new NumberProperty(this,
			"Long Shot Line", 240);

	public final BooleanProperty pullFromNetwork = new BooleanProperty(this,
			"Match Lines From Trim", true);

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (pullFromNetwork.getValue()) {

			try {

				linePosBatter.setValue(NetworkTable.getTable("SmartDashboard")
						.getNumber("Batter Shot Alignment"));
				linePosLong.setValue(NetworkTable.getTable("SmartDashboard")
						.getNumber("Long Shot Alignment"));
			} catch (TableKeyNotDefinedException e) {
			}
		}

		double widthRatio = getWidth() / 320.0;

		double heightRatio = getHeight() / 240.0;

		Graphics2D g2d = (Graphics2D) g;

		g2d.setStroke(new BasicStroke(5));

		g2d.setColor(Color.RED);

		g2d.drawLine((int) (linePosBatter.getValue().intValue() * widthRatio),
				0, (int) (linePosBatter.getValue().intValue() * widthRatio),
				(int) (100 * heightRatio));

		g2d.setColor(Color.BLUE);

		g2d.drawLine((int) (linePosLong.getValue().intValue() * widthRatio),
				(int) (100 * heightRatio), (int) (linePosLong.getValue()
						.intValue() * widthRatio), getHeight());

	}
}
