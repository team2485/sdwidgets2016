package team2485.smartdashboard.extension;

import java.awt.Color;
import java.awt.Dimension;
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
	private static LidarPing[] scannerData = new LidarPing[150];
	private Thread renderThread;

	public BooleanProperty threadBooleanProperty = new BooleanProperty(this, "Test", true);
    public final IntegerProperty numberProperty = new IntegerProperty(this, "Number",0);
	private double distanceScalar = 0.005;
	
	private int scannerDataPosition = 0;
	private int scannerDataDirection = 0;

	@Override
	public void init() {
		
		
		 final Dimension size = new Dimension(300, 140);
	     this.setSize(size);
	     this.setPreferredSize(size);
	        
		for (int i = 0; i < scannerData.length; i++) {
			scannerData[i] = new LidarPing(0,0);
		}

		renderThread = new Thread(new Runnable() {

			private int prevDistance = 80;

			@Override
			public void run() {
				while (!shutdown) {
					if (threadBooleanProperty.getValue()) {
						counter++;
						if (counter > 150) {
							counter = -150;
						}
						prevDistance += (Math.random()-.5)*10;
						if (prevDistance < 10){
							prevDistance = 10;
						} else if (prevDistance > 90){
							prevDistance = 90;
						}
						setValue(Math.abs(counter) + "," + prevDistance);
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
		String newDataLevel1 = (String)(arg0);
		String [] newDataLevel1Array = newDataLevel1.split(":");
		for (int i = 0; i < newDataLevel1Array.length; i++) {
			String newDataLevel2 = newDataLevel1Array[i];
			String [] newDataLevel2Array = newDataLevel2.split(",");
			if (scannerDataPosition > 149){
				scannerDataPosition = 0;
			}
			scannerData[scannerDataPosition] = new LidarPing(Math.toRadians(Integer.parseInt(newDataLevel2Array[0])),Integer.parseInt(newDataLevel2Array[1]));
			scannerDataPosition++;
		}		

	}

	@Override
	protected void paintComponent(final Graphics gg) {
		 Graphics2D g = (Graphics2D)gg;
	     g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	     g.setColor(Color.green);
	     int width = Math.min(getWidth(), getHeight()) - 2;
	     g.fillOval(width/2 - width/80, width/2 - width/80, width/40, width/40);
	     for (int i = 0; i < scannerData.length ; i++) {
	    	 double distance = scannerData[i].getDistance() * width * distanceScalar;
	    	 double pingX = scannerData[i].getX() + width/2;
	    	 double pingY = scannerData[i].getY() + width/2;
	    	 g.fillOval((int)pingX - width/120, (int)pingY - width/120, width/60, width/60);
		}
	     g.setColor(Color.YELLOW);
	    double lineAngle    = scannerData[scannerDataPosition-1].getAngle();
	    double lineX = Math.sin(lineAngle)*width/2 + width/2;
    	double lineY = Math.cos(lineAngle)*width/2 + width/2;
	    g.drawLine(width/2, width/2, (int)lineX, (int)lineY); 
	    g.drawOval(0, 0, width, width);
		this.getParent().setBackground(new Color(0x111111));//Sets Background color to black
	}
}
