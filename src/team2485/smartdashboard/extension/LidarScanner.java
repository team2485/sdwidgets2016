package team2485.smartdashboard.extension;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import org.jfree.util.ArrayUtilities;

import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.BooleanProperty;
import edu.wpi.first.smartdashboard.properties.IntegerProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;

public class LidarScanner extends Widget {

	public static final String NAME = "2485 Scanner";
	public static final DataType[] TYPES = { DataType.STRING };

	private int counter = 0;
	private boolean shutdown = false;
	private static int[][] scannerData = new int[100][2];
	private Thread renderThread;

	public BooleanProperty threadBooleanProperty = new BooleanProperty(this, "Test", true);
    public final IntegerProperty numberProperty = new IntegerProperty(this, "Number",0);
	private int distanceScalar;

	@Override
	public void init() {
		for (int i = 0; i < scannerData.length; i++) {
			scannerData[i][0] = 0;
			scannerData[i][1] = 0;
		}

		renderThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (!shutdown) {
					if (threadBooleanProperty.getValue()) {
						counter++;
						if (counter > 150) {
							counter = 0;
						}
						setValue(counter);
					}
					repaint();
					try {
						Thread.sleep(75);
					} catch (InterruptedException e) {
					}
				}
			}
		}, "Widget");
		renderThread.start();

	}

	@Override
	public void propertyChanged(Property arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValue(Object arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void paintComponent(final Graphics gg) {
		 Graphics2D g = (Graphics2D)gg;
	     g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	     for (int i = 0; i < scannerData.length ; i++) {
	    	 double angle    = scannerData[i][0];
	    	 double distance = scannerData[i][1] * width * distanceScalar;
	    		     
	    		     
	    		     angle = Math.toRadians(angle);
	    		     double pingX = width/2 + Math.sin(angle)*distance;
	    		     g.fillOval(x, y, width, height);
		}
	}
}
