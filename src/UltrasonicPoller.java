import lejos.nxt.UltrasonicSensor;


public class UltrasonicPoller extends Thread{
	private UltrasonicSensor us;
	private Claw cont;
	private SquareDriver driver;
	private int distanceCounter;
	
	public UltrasonicPoller(UltrasonicSensor us, Claw cont, SquareDriver driver) {
		this.us = us;
		this.cont = cont;
		this.driver = driver;
		this.distanceCounter = 0;
	}

	public void run() {
		driver.setSpeeds(Constants.FIND_BLOCK_SPEED, Constants.FIND_BLOCK_SPEED);
		while (!cont.blockGrabbed) {
			if (us.getDistance() < 10) {
				int distance = us.getDistance();
				driver.stop();
				cont.processUSData(distance);
				break;
			}
			
			if (cont.blockGrabbed) {
				break;
			}
			
			driver.rotateWithDirectReturn(45);
			while (driver.isRotating()) {
				if (us.getDistance() < 10) {
					int distance = us.getDistance();
					driver.stop();
					cont.processUSData(distance);
					driver.setRotating(false);
					break;
				}
				driver.checkMvt();
			}
			
			if (cont.blockGrabbed) {
				break;
			}

			driver.rotateWithDirectReturn(-90);
			while (driver.isRotating()) {
				if (us.getDistance() < 10) {
					int distance = us.getDistance();
					driver.stop();
					cont.processUSData(distance);
					driver.setRotating(false);
					break;
				}
				driver.checkMvt();
			}
			
			if (cont.blockGrabbed) {
				break;
			}

			//return to initial orientation
			driver.rotateCounter(47);
			if (distanceCounter > 27) {
				distanceCounter = 0;
				driver.rotateCounter(90);
			}
			else {
				distanceCounter += 10;
				driver.moveForward(8);
			}
			
			try { Thread.sleep(10); } catch(Exception e){}
		}
	}

}
