package team2485.smartdashboard.extension;



public class LidarPing {
	private double x, y;

	public LidarPing(double angle, double distance){
		this.x = Math.sin(angle) * distance;
		this.y = Math.cos(angle) * distance;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	
	public double getAngle() {
		return (Math.atan2(x, y));
	}
	
	public double getDistance(){
		return (Math.sqrt((x*x)+(y*y)));
	}
	
}

