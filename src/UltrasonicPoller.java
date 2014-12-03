import lejos.nxt.UltrasonicSensor;


public class UltrasonicPoller {
	private UltrasonicSensor us;
	private Claw cont;
	private SquareDriver driver;
	private int distanceCounter;
	public double prevRow; 
	public double prevCol;
	public double currentTheta;

	public UltrasonicPoller(UltrasonicSensor us, Claw cont, SquareDriver driver) {
		this.us = us;
		this.cont = cont;
		this.driver = driver;
		this.distanceCounter = 0;
	}

	/**
	 * Method used to find a block once the robot is the the 2x2 square that contains blocks
	 */
	public void findBlock() {
		currentTheta = 90;
		driver.setSpeeds(Constants.FIND_BLOCK_SPEED, Constants.FIND_BLOCK_SPEED);
		for (int i = 0; i < 4; i++) {
			if (!cont.blockGrabbed) {

				us.ping();
				if (us.getDistance() < 10) {
					int distance = us.getDistance();
					driver.stop();
					cont.processUSData(distance);
					break;
				}

//				driver.rotateWithDirectReturn(30);
//				while (driver.isRotating()) {
//					us.ping();
//					if (us.getDistance() < 20) {
//						int distance = us.getDistance();
//						driver.stop();
//						cont.processUSData(distance);
//						driver.setRotating(false);
//					}
//					driver.checkMvt();
//				}
//				
//				driver.setSpeeds(Constants.FIND_BLOCK_SPEED, Constants.FIND_BLOCK_SPEED);
//				driver.rotateCounter(30);
			}

			if (distanceCounter >= 30) {
				distanceCounter = 0;
				driver.rotateCounter(90);
				
				if (prevRow == 6 && prevCol == 1) {
					prevRow = 7;
					currentTheta = 180;
				}
				else if (prevRow == 7 && prevCol == 1) {
					prevCol = 0;
					currentTheta = 270;
				}
				else if (prevRow == 7 && prevCol == 0) {
					prevRow = 6;
					currentTheta = 0;
				}
				else if (prevRow == 6 && prevCol == 0) {
					prevCol = 1;
					currentTheta = 90;
				}
				
				if (cont.blockGrabbed) {
					break;
				}
				
			}
			else {
				if (!cont.blockGrabbed) {
					distanceCounter += 10;
					driver.moveForward(10);
				}
				else {
					distanceCounter += 30;
					driver.moveForward(30);
				}
			}

			try { Thread.sleep(10); } catch(Exception e){}
		}
	}

}
