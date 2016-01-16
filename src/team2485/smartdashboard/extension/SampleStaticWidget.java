package team2485.smartdashboard.extension;

import edu.wpi.first.smartdashboard.gui.*;
import edu.wpi.first.smartdashboard.properties.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

/**
 * Sample Widget
 * @author Aidan
 */
public class SampleStaticWidget extends StaticWidget {

	public static final String NAME = "Sample Static Widget";

    // SmartDashboard Properties
    public final IntegerProperty
            numberProperty = new IntegerProperty(this, "Number",0),
    		sizeProperty   = new IntegerProperty(this, "Size",100);

	public BooleanProperty
			threadBooleanProperty = new BooleanProperty(this, "Run Thread", true);

	private Thread renderThread;
	private boolean shutdown = false;

	private BufferedImage image;
	
	private int counter;

	private Font fontLarge, fontSmall;

	private Color warlordsYellow;


    @Override
    public void init() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/BlinkerResize.png"));
        } catch (IOException e) { }

        final Dimension size = new Dimension(400, 400);//Size
        this.setSize(size);
        this.setPreferredSize(size);
        
        
        

        renderThread = new Thread(new Runnable() {
			@Override
            public void run() {
                while (!shutdown) {
                	if (threadBooleanProperty.getValue()){
                		counter++;
                	}
                    repaint();
                    try {
                    	Thread.sleep(75);
                    } catch (InterruptedException e) { }       	
                }
            }
        }, "Widget");
        renderThread.start();
    }

    @Override
    public void propertyChanged(Property prprt) {
    }

    @Override
    protected void paintComponent(Graphics g) {
        final int width = sizeProperty.getValue(),
                  x = 0,
                  y = 0;
        
        warlordsYellow = new Color(228, 192, 37);//r,g,b
        
        g.setColor(Color.GREEN);
        //Sets the color to green
        
        fontLarge = new Font("BOOMBOX",  Font.BOLD, (width / 6)); 
        fontSmall = new Font("Consolas", Font.BOLD, (width / 15)); 
        g.setFont(fontLarge);
       //Sets to a font we like to use, sets the size based on width
        
        String text = "" + numberProperty.getValue(); 

        int xTextOffset = (int)((g.getFontMetrics(fontLarge).getStringBounds(text, g).getWidth()) / 2);
        int yTextOffset = (int)((g.getFontMetrics(fontLarge).getStringBounds(text, g).getHeight()) / 3);//3 because it includes a bit of extra space on top
        //Finds offsets so that we can control where the center of the text is, not the corner
		
        g.drawImage(image, x, y, x + width, y + width, null);
        
        g.drawString(text, width/2 - xTextOffset, width/2 + yTextOffset); 
        //X is negative and Y is positive because the text is drawn from the bottom right. 
      
        g.setColor(warlordsYellow);
        g.setFont(fontSmall);	
        g.drawString("" + counter, width/2 + xTextOffset, width/2 + yTextOffset);
        //putting the counter at the end of the main text
        
        
        this.getParent().setBackground(new Color(0x111111));//Sets Background color to black
    }

    @Override
    protected void finalize() throws Throwable {
        this.shutdown = true;
        super.finalize();
    }
}
