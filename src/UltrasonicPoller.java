import lejos.nxt.UltrasonicSensor;


public class UltrasonicPoller {
	private UltrasonicSensor us;
	private Claw cont;
	private SquareDriver driver;
	private int distanceCounter;
	private double prevPosX; 
	private double prevPosY;

	public UltrasonicPoller(UltrasonicSensor us, Claw cont, SquareDriver driver) {
		this.us = us;
		this.cont = cont;
		this.driver = driver;
		this.distanceCounter = 0;
	}

	public void findBlock() {
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

				driver.rotateWithDirectReturn(45);
				while (driver.isRotating()) {
					us.ping();
					System.out.println(us.getDistance());
					if (us.getDistance() < 10) {
						int distance = us.getDistance();
						driver.stop();
						cont.processUSData(distance);
						driver.setRotating(false);
						break;
					}
					driver.checkMvt();
				}

				driver.rotateWithDirectReturn(-90);
				while (driver.isRotating()) {
					us.ping();
					System.out.println(us.getDistance());
					if (us.getDistance() < 10) {
						int distance = us.getDistance();
						driver.stop();
						cont.processUSData(distance);
						driver.setRotating(false);
						break;
					}
					driver.checkMvt();
				}
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
