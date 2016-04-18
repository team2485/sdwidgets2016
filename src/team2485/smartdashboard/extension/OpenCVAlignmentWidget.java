//package team2485.smartdashboard.extension;
//
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.image.BufferedImage;
//import java.awt.image.DataBufferByte;
//import java.io.ByteArrayInputStream;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.lang.reflect.Array;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.logging.Level;
//
//import javax.imageio.ImageIO;
//
//import org.opencv.core.Core;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfPoint;
//import org.opencv.core.Rect;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//
//import edu.wpi.first.smartdashboard.gui.StaticWidget;
//import edu.wpi.first.smartdashboard.gui.elements.VideoStreamViewerExtension;
//import edu.wpi.first.smartdashboard.properties.BooleanProperty;
//import edu.wpi.first.smartdashboard.properties.Property;
//import edu.wpi.first.wpilibj.networktables.NetworkTable;
//import edu.wpi.first.wpilibj.tables.ITable;
//
//public class OpenCVAlignmentWidget extends VideoStreamViewerExtension {
//
//	public static final String NAME = "OpenCV Alignment Widget";
//
//	public final BooleanProperty alignLineProp = new BooleanProperty(this, "Show Alignment Line", true);
//	public final BooleanProperty targetBoxesProp = new BooleanProperty(this, "Show Targets", true);
//
//	private double[][] data;
//	private BufferedImage latestImage;
//	private boolean stop;
//
//	@Override
//	public void propertyChanged(Property arg0) {
//	}
//
//	@Override
//	public void init() {
//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//		stop = false;
//		new ImgProcThread().start();
//	}
//
//	@Override
//	protected void paintComponent(Graphics g) {
//		latestImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
//
//		Graphics2D g2d = latestImage.createGraphics();
//
//		super.paintComponent(g2d);
//
//		g.drawImage(latestImage, 0, 0, null);
//	}
//
//	private class ImgProcThread extends Thread {
//
//		private ImgProcThread() {
//			this.setPriority(MIN_PRIORITY);
//		}
//
//		@Override
//		public void run() {
//
//			while (!stop) {
//
//				Mat matImg = BufferedImageToMatPixels(latestImage);
//
//				Mat hlsImg = null;
//				Imgproc.cvtColor(matImg, hlsImg, Imgproc.COLOR_RGB2HLS);
//
//				ArrayList<Mat> hlsChannels = new ArrayList<Mat>();
//				Core.split(hlsImg, hlsChannels);
//
//				Mat cur = hlsChannels.get(0);
//				Mat low = cur.clone();
//				Mat high = cur.clone();
//
//				Imgproc.threshold(cur, low, 47, 180, Imgproc.THRESH_BINARY);
//				Imgproc.threshold(cur, high, 94, 180, Imgproc.THRESH_BINARY_INV);
//
//				Core.bitwise_and(low, high, cur);
//
//				hlsChannels.set(0, cur);
//
//				for (int i = 1; i < hlsChannels.size(); i++) {
//
//					cur = hlsChannels.get(i);
//					low = cur.clone();
//					high = cur.clone();
//
//					Imgproc.threshold(cur, low, 47, 180, Imgproc.THRESH_BINARY);
//					Imgproc.threshold(cur, high, 94, 180, Imgproc.THRESH_BINARY_INV);
//
//					Core.bitwise_and(low, high, cur);
//
//					hlsChannels.set(i, cur);
//				}
//
//				Mat thresholdedImage = cur.clone();
//
//				Core.bitwise_and(hlsChannels.get(0), hlsChannels.get(1), thresholdedImage);
//				Core.bitwise_and(thresholdedImage, hlsChannels.get(2), thresholdedImage);
//
//				Mat erodeMat = cur.clone();
//				Imgproc.erode(thresholdedImage, erodeMat, null, null, 0);
//
//				Mat dilateMat = cur.clone();
//				Imgproc.erode(erodeMat, dilateMat, null, null, 2);
//
//				ArrayList<MatOfPoint> listOfContours = new ArrayList<MatOfPoint>();
//				Imgproc.findContours(dilateMat, listOfContours, null, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
//
//				ArrayList<Rect> boundingRects = new ArrayList<Rect>();
//
//				for (MatOfPoint curContour : listOfContours) {
//					if (Imgproc.contourArea(curContour) > 50) {
//						boundingRects.add(Imgproc.boundingRect(curContour));
//					}
//				}
//
//				data = new double[4][boundingRects.size()];
//
//				for (int i = 0; i < boundingRects.size(); i++) {
//					Rect curRect = boundingRects.get(i);
//
//					data[0][i] = curRect.x;
//					data[1][i] = curRect.y;
//					data[2][i] = curRect.width;
//					data[3][i] = curRect.height;
//				}
//
//				ITable table = NetworkTable.getTable("GRIP").getSubTable("goals");
//
//				table.putValue("x", data[0]);
//				table.putValue("y", data[1]);
//				table.putValue("width", data[2]);
//				table.putValue("height", data[3]);
//
//				try {
//					Thread.sleep(1000 / 30);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//
//				Thread.yield();
//			}
//		}
//	}
//
//	public static Mat BufferedImageToMatPixels(BufferedImage image) {
//		byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
//		Mat m = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC(image.getColorModel().getNumComponents()));
//		m.put(0, 0, pixels);
//		return m;
//	}
//
//	@Override
//	protected void finalize() throws Throwable {
//		stop = true;
//		super.finalize();
//	}
//}
