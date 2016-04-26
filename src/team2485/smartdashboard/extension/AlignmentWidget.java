package team2485.smartdashboard.extension;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import edu.wpi.first.smartdashboard.gui.elements.VideoStreamViewerExtension;
import edu.wpi.first.smartdashboard.properties.BooleanProperty;
import edu.wpi.first.smartdashboard.properties.NumberProperty;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.TableKeyNotDefinedException;

public class AlignmentWidget extends VideoStreamViewerExtension {

	public static final String NAME = "Logicless Alignment Widget";

	public final NumberProperty linePosBatter = new NumberProperty(this, "Batter Shot Line", 240);
	public final NumberProperty linePosLong = new NumberProperty(this, "Long Shot Line", 240);

	public final BooleanProperty pullFromNetwork = new BooleanProperty(this, "Match Lines From Trim", true);

	private Timer saveTimer;
	private TimerTask timerTask;
	private Timer increaseFPSTimer;

	private boolean saveImage;
	private long matchTime;
	private boolean increaseFPS;
	private boolean dearGodStop;

	@Override
	public void init() {
		super.init();

		matchTime = System.currentTimeMillis();

		timerTask = new TimerTask() {
			@Override
			public void run() {
				saveImage = true;
			}
		};

		saveTimer = new Timer();
		saveTimer.scheduleAtFixedRate(timerTask, 0, 500);

		increaseFPS = false;
		dearGodStop = false;
	}

	@Override
	protected void paintComponent(Graphics g) {

		Graphics2D frameg2d = (Graphics2D) g;

		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

		Graphics2D imageg2d = image.createGraphics();

		super.paintComponent(imageg2d);

		if (pullFromNetwork.getValue()) {
			try {
				linePosBatter.setValue(NetworkTable.getTable("SmartDashboard").getNumber("Batter Shot Alignment"));
				linePosLong.setValue(NetworkTable.getTable("SmartDashboard").getNumber("Long Shot Alignment"));
			} catch (TableKeyNotDefinedException e) {
			}
		}

		frameg2d.drawImage(image, 0, 0, null);

		double widthRatio = getWidth() / 320.0;

		double heightRatio = getHeight() / 240.0;

		frameg2d.setStroke(new BasicStroke(5));

		frameg2d.setColor(Color.RED);

		frameg2d.drawLine((int) (linePosBatter.getValue().intValue() * widthRatio), 0,
				(int) (linePosBatter.getValue().intValue() * widthRatio), (int) (100 * heightRatio));

		frameg2d.setColor(Color.BLUE);

		frameg2d.drawLine((int) (linePosLong.getValue().intValue() * widthRatio), (int) (100 * heightRatio),
				(int) (linePosLong.getValue().intValue() * widthRatio), getHeight());

		try {
			if (NetworkTable.getTable("SmartDashboard").getBoolean("Increase Recording FPS", false)) {
				if (!increaseFPS) {
					increaseFPS = true;
					saveTimer.cancel();
					saveTimer.purge();
					saveTimer.scheduleAtFixedRate(timerTask, 0, 100);
					
					increaseFPSTimer.schedule(new TimerTask() {
						@Override
						public void run() {
							increaseFPS = false;
							saveTimer.cancel();
							saveTimer.purge();
							saveTimer.scheduleAtFixedRate(timerTask, 0, 500);
						}
					}, 3000);
				}
			}
		} catch (TableKeyNotDefinedException e) {
			increaseFPS = false;
		}

		if (dearGodStop) {
			if (saveImage || increaseFPS) {
				try {
					boolean isThereAProblem = saveImage(image);
					
					if (isThereAProblem) {
						dearGodStop = true;
					}
				} catch (Exception e) {
					dearGodStop = true;
				}
			}
		}
	}

	private boolean saveImage(BufferedImage img) throws Exception {
		saveImage = false;

		File output = new File("C:/Users/2485/Desktop/Match-" + matchTime);

		if (!output.exists()) {
			boolean good = output.mkdirs();

			if (!good) {
				return true;
			}
		}

		if (!output.isDirectory() || !output.canWrite()) {
			return true;
		}

		long curTimeInMatch = (System.currentTimeMillis() - matchTime);

		ImageIO.write(img, "png", new File(output, "Capture-" + curTimeInMatch));
		
		return false;
	}
}
