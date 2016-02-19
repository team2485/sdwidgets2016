package edu.wpi.grip.smartdashboard;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.JComponent;

import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.grip.smartdashboard.GRIPReportList.Report;

/**
 * A Swing component that renders either an image or an error. This is used
 * inside of {@link GRIPExtension} to show the live video feed from GRIP.
 */
public class GRIPImage extends JComponent {

	private BufferedImage image = null;
	private String error = null;
	private GRIPReportList reportList = null;

	public GRIPImage() {
		super();

		setPreferredSize(new Dimension(640, 480));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	}

	/**
	 * Set the latest image to show and clear any error
	 */
	public synchronized void setImage(Image image) {
		this.image = (BufferedImage) image;
		this.error = null;
		EventQueue.invokeLater(this::repaint);
	}

	/**
	 * Set an error to show instead of an image
	 */
	public synchronized void setError(String error) {
		this.error = error;
		this.image = null;
		EventQueue.invokeLater(this::repaint);
	}

	public synchronized void setReportList(GRIPReportList reportList) {
		this.reportList = reportList;
	}

	@Override
	protected synchronized void paintComponent(Graphics g) {
		final Graphics2D g2d = ((Graphics2D) g);
		final int em = g2d.getFontMetrics().getHeight();

		if (image == null) {
			g2d.setColor(Color.PINK);
			g2d.fillRect(0, 0, getWidth(), getHeight());
			g2d.setColor(Color.BLACK);
			g2d.drawString(error == null ? "No image available" : error, em / 2, em);
		} else {
			final double aspectRatio = (double) image.getHeight(null) / image.getWidth(null);
			int x = 0, y = 0, width = getWidth(), height = getHeight();

			// Preserve the image's aspect ratio. If this component is too wide,
			// make the image less wide and center
			// it horizontally. If it's too tall, make the image shorter and
			// center it vertically.
			if (width * aspectRatio > height) {
				width = (int) (getHeight() / aspectRatio);
				x = (getWidth() - width) / 2;
			} else {
				height = (int) (getWidth() * aspectRatio);
				y = (getHeight() - height) / 2;
			}

			g2d.setColor(Color.BLACK);
			g2d.fillRect(0, 0, getWidth(), getHeight());
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

			Graphics2D imageG = (Graphics2D) image.getGraphics();

			imageG.setStroke(new BasicStroke(6));
			imageG.setColor(Color.GREEN);
			imageG.drawLine(image.getWidth() / 2, 0, image.getWidth() / 2, image.getHeight());

			imageG.setStroke(new BasicStroke(4));
			imageG.setColor(Color.YELLOW);
			imageG.drawLine((image.getWidth() / 2) + 25, 0, (image.getWidth() / 2) + 25, image.getHeight());
			imageG.drawLine((image.getWidth() / 2) - 25, 0, (image.getWidth() / 2) - 25, image.getHeight());

			imageG.setStroke(new BasicStroke(2));
			imageG.setColor(Color.RED);
			imageG.drawLine((image.getWidth() / 2) + 50, 0, (image.getWidth() / 2) + 50, image.getHeight());
			imageG.drawLine((image.getWidth() / 2) - 50, 0, (image.getWidth() / 2) - 50, image.getHeight());

			g2d.drawImage(image, x, y, width, height, null);

			// Scale anything drawn after this point so it lines up with the
			// image
			double scale = (double) width / image.getWidth(null);
			AffineTransform transform = g2d.getTransform();
			transform.translate(x, y);
			transform.scale(scale, scale);
			g2d.setTransform(transform);
			g2d.setStroke(new BasicStroke((float) (2 / scale)));

//			synchronized (reportList) {
//				for (GRIPReportList.Report report : reportList.getReports()) {
//					renderReport(g2d, report);
//				}
//			}
		}
	}

	/**
	 * Draw a single GRIP report based on the values stored in a subtable.
	 * <p>
	 * This method looks for groups of subtable values that look like either a
	 * line, blob, or contour report, and draws the appropriate visuals. The
	 * color and visibility of the drawing is based on the
	 * {@link GRIPReportList.Report} fields.
	 */
	private void renderReport(Graphics2D g2d, GRIPReportList.Report report) {
		// Do nothing if the report is set to not show
		if (!report.show) {
			return;
		}

		ITable table = report.table;
		g2d.setColor(report.color);

		if (containsAll(table, Arrays.asList("centerX", "centerY", "width", "height"))) {

			double[][] tableValues = new double[][] { getNumberArray(table, "centerX"),
					getNumberArray(table, "centerY"), getNumberArray(table, "width"), getNumberArray(table, "height") };

			for (int i = 0; i < tableValues[0].length; i++) {

				g2d.drawRect((int) (tableValues[0][i] - (tableValues[2][i] / 2)),
						(int) (tableValues[1][i] - (tableValues[3][i] / 2)), (int) tableValues[2][i],
						(int) tableValues[3][i]);

			}

		} else if (containsAll(table, Arrays.asList("x", "y", "size"))) {
			// If the subtable has three equal-length arrays called x, y, and
			// size, draw a circle for each element
			double[] x = getNumberArray(table, "x"), y = getNumberArray(table, "y"),
					size = getNumberArray(table, "size");
			if (x.length == y.length) {
				for (int i = 0; i < x.length; i++) {
					g2d.drawOval((int) (x[i] - size[i] / 2), (int) (y[i] - size[i] / 2), (int) size[i], (int) size[i]);
				}
			}
		}
	}

	private boolean containsAll(ITable table, java.util.List<String> keysToFind) {

		for (String curKey : keysToFind) {
			if (!table.containsKey(curKey)) {
				return false;
			}
		}
		return true;
	}

	private double[] getNumberArray(ITable table, String key) {

		Object o = table.getValue(key);

		if (!(o instanceof double[])) {
			throw new RuntimeException("Got an object that was not an array of doubles");
		}

		return (double[]) o;
	}
}
