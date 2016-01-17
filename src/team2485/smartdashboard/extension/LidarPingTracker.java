package team2485.smartdashboard.extension;

public class LidarPingTracker {
	
	private int arrayPosition;
	private int scanDirection;
	private LidarPing[] array;
	
	public LidarPingTracker (int length){
		arrayPosition = 0;
		scanDirection = 1;
		array = new LidarPing[length];
		
		for (int i = 0; i < array.length; i++) {
			array[i] = new LidarPing(0,0);
		}
	}
	
	public void addPing(LidarPing ping){
		array[arrayPosition] = ping;
		arrayPosition += scanDirection;
	}
	
	public LidarPing getPing (int pos){
		return array[pos];
	}
	
	public int getLength(){
		return array.length;
	}
	
	public int getDirection(){
		return scanDirection;
	}
	
	public void switchDirection() {
		scanDirection *= -1;
	}

}
