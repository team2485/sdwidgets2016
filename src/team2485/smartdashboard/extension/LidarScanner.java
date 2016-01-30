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
import edu.wpi.first.smartdashboard.properties.DoubleProperty;
import edu.wpi.first.smartdashboard.properties.IntegerProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;

public class LidarScanner extends Widget {

	public static final String NAME = "2485 Scanner";
	public static final DataType[] TYPES = { DataType.STRING };

	private int counter = 0;
	private boolean shutdown = false;
	private static LidarPingTracker scannerData = new LidarPingTracker(1600);
	private Thread renderThread;

	public BooleanProperty threadBooleanProperty = new BooleanProperty(this, "Test", false);
    public final DoubleProperty distanceProperty = new DoubleProperty(this, "Distance Scale", 0.0005);

	@Override
	public void init() {
		
		
		 final Dimension size = new Dimension(300, 140);
	     this.setSize(size);
	     this.setPreferredSize(size);

		renderThread = new Thread(new Runnable() {

			private double prevDistance1 = 80;
			private double prevDistance2 = 80;

			@Override
			public void run() {
				while (!shutdown) {
					if (threadBooleanProperty.getValue()) {
						counter++;
						if (counter > 48) {
							counter = -48;
						}
						prevDistance1 = prevDistance2 + (Math.random()-.5)*10;
						prevDistance2 = prevDistance1 + (Math.random()-.5)*10;
						if (prevDistance2 < 30){
							prevDistance2 = 30;
						} else if (prevDistance2 > 90){
							prevDistance2 = 90;
						}
						if (counter != 0){
							setValue(counter/Math.abs(counter) + ":" + Math.abs(counter) + "," + prevDistance1 + ":" + Math.abs(counter)+1 + "," + prevDistance2);
						}
					}
					repaint();
					try {
						Thread.sleep(75);
					} catch (InterruptedException e) {
					}
				}
			}
		}, "Lidar Scanner Widget");
		renderThread.start();
		   
	}

	@Override
	public void propertyChanged(Property arg0) {

	}

	@Override
	public void setValue(Object arg0) {
		String newDataLevel1 = (String)(arg0);
		String [] newDataLevel1Array = newDataLevel1.split(":");
		scannerData.setDirection(Integer.parseInt(newDataLevel1Array[0]));
		for (int i = 2; i < newDataLevel1Array.length; i++) {
			String newDataLevel2 = newDataLevel1Array[i];
			System.out.println(newDataLevel2);
			String [] newDataLevel2Array = newDataLevel2.split(",");
			scannerData.addPing(new LidarPing(-1 * Math.toRadians(Double.parseDouble(newDataLevel2Array[0])),Double.parseDouble(newDataLevel2Array[1])));
			
		}		
		repaint();
	}

	@Override
	protected void paintComponent(final Graphics gg) {
		 Graphics2D g = (Graphics2D)gg;
	     g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	     g.setColor(Color.green);
	     int width = Math.min(getWidth(), getHeight()) - 2;
	     g.fillOval(width/2 - width/80, width/2 - width/80, width/40, width/40);
	     for (int i = 0; i < scannerData.getLength(); i++) {
	    	 if (scannerData.getPing(i) != null){
	    		 //double distance = scannerData.getPing(i).getDistance() * width * distanceScalar;
	    		 double pingX = scannerData.getPing(i).getX() * distanceProperty.getValue() * width + width/2;
	    		 double pingY = scannerData.getPing(i).getY() * distanceProperty.getValue() * width + width/2;
	    		 g.fillOval((int)pingX - width/240, (int)pingY - width/240, width/120, width/120);
	    		 //System.out.println(scannerData.getPing(scannerData.getArrayPosition()).getAngle());
	    	 }	
		}
	     g.setColor(Color.YELLOW);
	    double lineAngle    = scannerData.getPing(scannerData.getArrayPosition()).getAngle();
	    double lineX = Math.sin(lineAngle)*width/2 + width/2;
    	double lineY = Math.cos(lineAngle)*width/2 + width/2;
	    g.drawLine(width/2, width/2, (int)lineX, (int)lineY); 
	    g.drawOval(0, 0, width, width);
		this.getParent().setBackground(new Color(0x111111));//Sets Background color to black
	}
}
