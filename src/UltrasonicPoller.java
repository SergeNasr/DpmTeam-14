import lejos.nxt.UltrasonicSensor;


public class UltrasonicPoller extends Thread{
	private UltrasonicSensor us;
	private UltrasonicController cont;
	private SquareDriver driver;
	
	public UltrasonicPoller(UltrasonicSensor us, UltrasonicController cont, SquareDriver driver) {
		this.us = us;
		this.cont = cont;
		this.driver = driver;
	}

	public void run() {
		driver.setSpeeds(100, 100);
		while (true) {
			if (us.getDistance() < 20) {
				int distance = us.getDistance();
				driver.stop();
				cont.processUSData(distance);
				break;
			}
			
			driver.rotateWithDirectReturn(30);
			while (driver.isRotating()) {
				if (us.getDistance() < 20) {
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
				if (us.getDistance() < 20) {
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
			driver.moveForward(12);
			
			try { Thread.sleep(10); } catch(Exception e){}
		}
	}

}
