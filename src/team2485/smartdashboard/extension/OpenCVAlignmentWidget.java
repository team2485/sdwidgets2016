package team2485.smartdashboard.extension;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.gui.elements.VideoStreamViewerExtension;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

public class OpenCVAlignmentWidget extends VideoStreamViewerExtension {

	@Override
	public void propertyChanged(Property arg0) {
	}

	@Override
	public void init() {
	}

	@Override
	protected void paintComponent(Graphics g) {

		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = img.createGraphics();

		super.paintComponent(g2d);

		Mat matImg = BufferedImageToMatPixels(img);

		Mat hlsImg = null;
		Imgproc.cvtColor(matImg, hlsImg, Imgproc.COLOR_RGB2HLS);

		ArrayList<Mat> hlsChannels = new ArrayList<Mat>();
		Core.split(hlsImg, hlsChannels);

		Mat cur = hlsChannels.get(0);
		Mat low = cur.clone();
		Mat high = cur.clone();

		Imgproc.threshold(cur, low, 47, 180, Imgproc.THRESH_BINARY);
		Imgproc.threshold(cur, high, 94, 180, Imgproc.THRESH_BINARY_INV);

		Core.bitwise_and(low, high, cur);

		hlsChannels.set(0, cur);

		for (int i = 1; i < hlsChannels.size(); i++) {

			cur = hlsChannels.get(i);
			low = cur.clone();
			high = cur.clone();

			Imgproc.threshold(cur, low, 47, 180, Imgproc.THRESH_BINARY);
			Imgproc.threshold(cur, high, 94, 180, Imgproc.THRESH_BINARY_INV);

			Core.bitwise_and(low, high, cur);
			
			hlsChannels.set(i, cur);
		}

		Mat thresholdedImage = cur.clone();

		Core.bitwise_and(hlsChannels.get(0), hlsChannels.get(1), thresholdedImage);
		Core.bitwise_and(thresholdedImage, hlsChannels.get(2), thresholdedImage);

		Mat erodeMat = cur.clone();
		Imgproc.erode(thresholdedImage, erodeMat, null, null, 0);

		Mat dilateMat = cur.clone();
		Imgproc.erode(erodeMat, dilateMat, null, null, 2);

		ArrayList<MatOfPoint> listOfContours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(dilateMat, listOfContours, null, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

		for (int i = 0; i < listOfContours.size(); i++) {
			if (Imgproc.contourArea(listOfContours.get(i)) < 50) {
				listOfContours.remove(i);
				i--;
			}
		}
	}

	private double[] getNumberArray(ITable table, String key) {

		Object o = table.getValue(key);

		if (!(o instanceof double[])) {
			throw new RuntimeException("Got an object that was not an array of doubles");
		}

		return (double[]) o;
	}

	public static Mat BufferedImageToMatPixels(BufferedImage image) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		Mat m = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC(image.getColorModel().getNumComponents()));
		m.put(0, 0, pixels);
		return m;
	}

	class VisionThread extends Thread {

		@Override
		public void run() {

		}
	}
}
