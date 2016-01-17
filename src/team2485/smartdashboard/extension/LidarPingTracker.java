package team2485.smartdashboard.extension;

public class LidarPingTracker {

	private int arrayPosition;
	private int scanDirection;
	private LidarPing[] array;

	public LidarPingTracker(int length) {
		arrayPosition = 10;
		scanDirection = 1;
		array = new LidarPing[length];

		for (int i = 0; i < array.length; i++) {
			array[i] = new LidarPing(0, 0);
		}
	}

	public void addPing(LidarPing ping) {
		array[arrayPosition] = ping;
		arrayPosition += scanDirection;
		//System.out.println(ping.getAngle() + "," + ping.getDistance());
	}

	public int getArrayPosition() {
		return arrayPosition;
	}

	public void setArrayPosition(int arrayPosition) {
		this.arrayPosition = arrayPosition;
	}

	public LidarPing getPing(int pos) {
		return array[pos];
	}

	public int getLength() {
		return array.length;
	}

	public int getDirection() {
		return scanDirection;
	}

	public void setDirection(int direction) {
		scanDirection = direction;

//		if (scanDirection == -1) {
//			for (int i = arrayPosition; i < array.length; i++) {
//				array[i] = new LidarPing(0, 0);
//			}
//		} else if (scanDirection == 1) {
//			for (int i = arrayPosition; i > -1; i--) {
//				array[i] = new LidarPing(0, 0);
//			}
//		}

	}
}
