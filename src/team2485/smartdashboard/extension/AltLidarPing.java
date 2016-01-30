package team2485.smartdashboard.extension;

import java.awt.Graphics2D;

public class AltLidarPing {

	private static final int RADIUS = 2;

	private static final int PINGS_PER_DEGREE = 2;
	
	public static double SCALE_FACTOR = .25;

	private double relativeAngle;

	private double relativeX, relativeY;

	private double distance;

	public AltLidarPing(double angle, double dist) {
		
		dist *= SCALE_FACTOR;

		relativeAngle = angle;

		distance = dist;

		relativeX = Math.cos(Math.toRadians(angle)) * dist;
		relativeY = Math.sin(Math.toRadians(angle)) * dist;
	}

	public void draw(Graphics2D g2d) {

		g2d.fillOval((int) Math.round(relativeX - RADIUS), (int) Math.round(relativeY - RADIUS), RADIUS * 2,
				RADIUS * 2);
	}

	public void changeDistance(double deltaDist) {
		distance += deltaDist;
	}

	public void adjustForRobotMovement(double robotMoveDist) {
		relativeY += robotMoveDist;
	}

	@Override
	public boolean equals(Object other) {

		if (other instanceof AltLidarPing) {

			return Math.round(this.relativeAngle * PINGS_PER_DEGREE) == Math
					.round(((AltLidarPing) other).relativeAngle * PINGS_PER_DEGREE);
		}

		return false;
	}
	
	public int getX() {
		return (int) Math.round(relativeX);
	}
	
	public int getY() {
		return (int) Math.round(relativeY);
	}
	
	public double getRelativeAngle() {
		return relativeAngle;
	}
	
	public double getDistance() {
		return distance;
	}
}
