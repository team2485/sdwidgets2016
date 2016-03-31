                                          package team2485.smartdashboard.extension;

public class LidarPingTracker {

	private int arrayPosition;
	private int scanDirection;
	private LidarPing[] array;

	public LidarPingTracker(int length) {
		arrayPosition = length;
		scanDirection = 1;
		array = new LidarPing[length * 2];

//		for (int i = 0; i < array.length; i++) {
//			array[i] = new LidarPing(0, 0);
//		}
	}

	public void addPing(LidarPing ping) {
		arrayPosition += scanDirection;
		if (arrayPosition > array.length - 1){
			arrayPosition = array.length - 1;
		} else if (arrayPosition < 0){
			arrayPosition = 0;
		}
		array[arrayPosition] = ping;

		// System.out.println(ping.getAngle() + "," + ping.getDistance());
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
		int oldScanDirection = scanDirection;
		scanDirection = direction;

		if (oldScanDirection != scanDirection) {
			if (scanDirection == -1) {
				for (int i = arrayPosition; i < array.length; i++) {
					array[i] = null;
				}
			} else if (scanDirection == 1) {
				for (int i = arrayPosition; i > -1; i--) {
					array[i] = null;
				}
			}
		}
	}
}
