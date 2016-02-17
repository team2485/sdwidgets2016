package team2485.smartdashboard.extension;
import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.Property;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

import edu.wpi.first.smartdashboard.properties.BooleanProperty;
import edu.wpi.first.smartdashboard.properties.DoubleProperty;
import edu.wpi.first.smartdashboard.properties.IntegerProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;

public class RPMWidget extends Widget{

	public static final String NAME = "RPM Widget";
	public static final DataType[] TYPES = {DataType.STRING};
	private double scale = 1;
	private int x = 0;
	private int y = 0;
	private int w = 100;
	private int h = 500;
	private double wth = w*scale;
	private double hght = h*scale;
	private int numTicks = 20;
	private double tickSpacing = hght/numTicks;
	private double textOffset = 3;
	private double rpm = 1;
	private double rectBarY = (hght -(hght-((hght/numTicks)*rpm/2000)))+2;
	private double targetRPM = 3000;
	private int maxRPM = 5000;
	private Color transGreen;
	private Font text;
	private boolean singleValue = false;

	private final Property test = new BooleanProperty(this, "Test", false);
	

	
	

	@Override
	public void propertyChanged(Property arg0) {
		// TODO Auto-generated method stub
		repaint();
	}

	@Override
	public void setValue(Object arg0) {
		// TODO Auto-generated method stub
		//rpm = ((Number)arg0).doubleValue();
		String data = (String)(arg0);
		String [] dataArray;

		if(data.indexOf(",") == -1){
			singleValue = true;
		} else {
			dataArray = data.split(",");
			targetRPM = Integer.parseInt(dataArray[1]);
			singleValue = false;
			data = dataArray[0];
		}
		rpm = Double.parseDouble(data);
		repaint();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		final Dimension size = new Dimension((int)(wth + 1), (int)(hght + 1));//Size
		this.setSize(size);
		this.setPreferredSize(size);
		
		new Thread(new Runnable() {
            private double value;

			@Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                    if ((boolean) test.getValue()) {

                        value += 30;
                        setValue(value + "," + 1000);
                        if (value > 5000) {
                            value = 10;
                        }
                    }
                }
            }
        }).start();


	}

	@Override
	public void paintComponent(Graphics gg){
		final Graphics2D g = (Graphics2D) gg;
		x = 0;
		y = 0;
		//rpm = tempRpmDubProp.getValue();
		scale = (double)Math.min(getHeight(), getWidth()*3)/500;
		wth = w*scale;
		hght = h*scale;
		tickSpacing = hght/numTicks;
		rectBarY = (hght -(hght-((hght/numTicks)*rpm/(maxRPM/numTicks))))+2;
		text = new Font("Consolas", Font.BOLD, (int)(17*scale));
		transGreen = Color.GREEN;//new Color(0, 255, 0, 180);
		this.getParent().setBackground(Color.BLACK);
		g.setColor(Color.GREEN);
		g.setFont(text);
		g.drawRect(x, y, (int)(wth), (int)(hght));
		g.translate(x, y);
		g.setColor(transGreen);
		g.fillRect(1, (int)(hght - rectBarY + 1), (int)(wth - 1), (int)(hght - 1 -(hght - rectBarY)));
		g.translate(-x, -y);
		for(int i = 1; i < numTicks; i++){
			if(hght - Math.abs(rectBarY) >= y + (tickSpacing*i)){
				g.setColor(Color.GREEN);
			} else {
				g.setColor(Color.BLACK);
			}
			g.drawLine(x, (int)(y + (tickSpacing*i)), 
					(int)(x + (wth/4)), (int)(y + (tickSpacing*i)));
		}
		String tempRPM = String.valueOf((int)(rpm));
		g.setColor(Color.GREEN);
		if(hght - Math.abs(rectBarY) >= (hght/2 + (5*scale))){
			g.setColor(Color.GREEN);
		} else {
			if((rectBarY >= hght/2 + (4*scale))){
				System.out.println(rectBarY + ", " + (hght/2 + (5*scale)));
				g.setColor(transGreen);
				g.fillRect((int)(wth/4 + (4 * scale)), (int)(hght/2 + (5 * scale) - (17 * scale)), 
						(int)(65*scale), (int)(hght - rectBarY - hght/2 + (5*scale) - 17*scale + 25*scale));
			}
			g.setColor(Color.BLACK);
		}
		if(singleValue == false){
			g.drawString(tempRPM, (int)(wth/4 + (8*scale)), (int)(hght/2 + (5*scale)));
			g.translate(x, y);
			g.setColor(Color.WHITE);
			g.drawLine(0, (int)(hght - ((targetRPM/maxRPM)*hght)), (int)(wth), (int)(hght -((targetRPM/maxRPM)*hght)));
			g.setColor(Color.GREEN);
			g.drawString("Target RPM", (int)(wth + 5*scale), (int)(hght - ((targetRPM/maxRPM)*hght)));
			g.drawString(String.valueOf((int)(targetRPM)), (int)(wth + 5*scale), (int)(hght - ((targetRPM/maxRPM)*hght) + 20*scale));

//			if(500-rectBarY < (hght - ((targetRPM/maxRPM)*hght))){
//				g.setColor(Color.GREEN);
//			} else {
//				g.setColor(Color.RED);
//			}
//			g.fillRect((int)(wth + 70*scale), (int)(hght - ((targetRPM/maxRPM)*hght) + 7*scale), 15,15);
		}
	}
}
