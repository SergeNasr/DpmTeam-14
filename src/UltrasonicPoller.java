import lejos.nxt.UltrasonicSensor;


public class UltrasonicPoller extends Thread{
	private UltrasonicSensor us;
	private UltrasonicController cont;
	private SquareDriver driver;
	private int distanceCounter;
	
	public UltrasonicPoller(UltrasonicSensor us, UltrasonicController cont, SquareDriver driver) {
		this.us = us;
		this.cont = cont;
		this.driver = driver;
		this.distanceCounter = 0;
	}

	public void run() {
		driver.setSpeeds(150, 150);
		while (!cont.blockGrabbed) {
			if (us.getDistance() < 10) {
				int distance = us.getDistance();
				driver.stop();
				cont.processUSData(distance);
				break;
			}
			
			driver.rotateWithDirectReturn(30);
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

			driver.rotateWithDirectReturn(-60);
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

			//return to initial orientation
			driver.rotateCounter(33);
			distanceCounter += 12;
			if (distanceCounter > 30) {
				distanceCounter = 0;
				driver.rotateClockwise(90);
			}
			else {
				driver.moveForward(12);
			}
			
			try { Thread.sleep(10); } catch(Exception e){}
		}
	}

}
