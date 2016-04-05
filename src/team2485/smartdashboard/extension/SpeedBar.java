package team2485.smartdashboard.extension;

import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.Property;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import edu.wpi.first.smartdashboard.properties.BooleanProperty;
import edu.wpi.first.smartdashboard.properties.DoubleProperty;
import edu.wpi.first.smartdashboard.properties.IntegerProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;

public class SpeedBar extends Widget{
	
	/**
	 * @author 
	 * Jason
	 */
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "Speed Bar Widget";
	public static final DataType[] TYPES = {DataType.NUMBER};
	private double scale = 1;
	private int x = 100;
	private int y = 100;
	private int w = 100;
	private int h = 500;
	private double wth = w*scale;
	private double hght = h*scale;
	private double tickSpacing = hght/20;
	private double speed = 1; //finishhhhhhhhhhhhhhhhhhhhhhhhhhhh
	private double rectBarY = ((hght -(hght-((hght/20)*speed))) + 2);
	private Color transGreen;
	private Font text;
	
	
	private final Property test = new BooleanProperty(this, "Test", false);
	
	@Override
	public void propertyChanged(Property arg0) {
		// TODO Auto-generated method stub
		repaint();
	}

	@Override
	public void setValue(Object arg0) {
		// TODO Auto-generated method stub
		speed = ((Number)arg0).doubleValue();
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

                        value += .2;
                        setValue(value);
                        if (value > 15) {
                            value = 1;
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
		//speed = tempSpeedDubProp.getValue();
		scale = (double)Math.min(getHeight(), getWidth()*5)/500;
		wth = w*scale;
		hght = h*scale;
		tickSpacing = hght/20;
		rectBarY = (hght -(hght-((hght/20)*speed)))+2;
		text = new Font("Consolas", Font.BOLD, (int)(17*scale));
//		this.getParent().setBackground(Color.BLACK);
		transGreen = Color.GREEN;//new Color(0, 255, 0, 180);
		g.setColor(Color.GREEN);
		g.setFont(text);
		g.drawRect(x, y, (int)(wth), (int)(hght));	
		g.translate(x, y);
		g.setColor(transGreen);
		g.fillRect(1, (int)(hght - rectBarY + 1), (int)(wth - 1), (int)(hght - 1 -(hght - rectBarY)));
		g.translate(-x, -y);
		for(int i = 1; i < 20; i++){
			if(hght - Math.abs(rectBarY) >= y + (tickSpacing*i)){
				g.setColor(Color.GREEN);
			} else {
				g.setColor(Color.BLACK);
			}
			g.drawLine((int)(x + wth*0.75), (int)(y + (tickSpacing*i)), 
					(int)(x + wth), (int)(y + (tickSpacing*i)));
		}
		
		
		String tempSpeed = String.valueOf(speed);
		g.setColor(Color.GREEN);
		if(hght - Math.abs(rectBarY) >= (hght/2 + (5*scale))){
			g.setColor(Color.GREEN);
		} else {
			g.setColor(transGreen);
			g.fillRect((int)(wth/4 - (17 * scale)), (int)(hght/2 + (5 * scale) - (17 * scale)), 
					(int)(65*scale), (int)(hght - rectBarY - hght/2 + (5*scale) - 17*scale + 25*scale + 1));
			g.setColor(Color.BLACK);
		}
		g.drawString(tempSpeed, (int)(wth/4 - (17*scale)), (int)(hght/2 + (5*scale)));
	}
}
