import lejos.nxt.ColorSensor;
import lejos.nxt.Sound;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private ColorSensor colorSensorLeft;
	private ColorSensor colorSensorRight;
	private SquareDriver driver;

	//distance from light sensor to center of rotation
	private static final double DISTANCE_SENSOR = 7;
	private static final int LIGHT_THRESHOLD = 45;
	private static final double FIRST_LINE = 30;

	// constructor
	public OdometryCorrection(Odometer odometer, ColorSensor colorSensorLeft, ColorSensor colorSensorRight, SquareDriver driver) {
		this.odometer = odometer;
		this.colorSensorLeft = colorSensorLeft;
		this.colorSensorRight = colorSensorRight;
		this.driver = driver;
	}

	// run method (required for Thread)
	public void run() {
		Sound.setVolume(Sound.VOL_MAX);
		colorSensorLeft.setFloodlight(true);
		colorSensorRight.setFloodlight(true);

		int leftValue;
		int rightValue;
		long firstTime = 0;
		long secondTime = 0;
		boolean firstLineSeen = false;

		while (true) {
			leftValue = colorSensorLeft.getLightValue();
			rightValue = colorSensorRight.getLightValue();
			
			if ((leftValue < LIGHT_THRESHOLD || rightValue < LIGHT_THRESHOLD) && !firstLineSeen) {
				firstTime = System.currentTimeMillis() / 1000;
				firstLineSeen = true;
				Sound.beep();
			} else if (leftValue < LIGHT_THRESHOLD && firstLineSeen) {
				// left wheel is trailing
				Sound.beep();
				secondTime = System.currentTimeMillis() / 1000;
				int deltaDistance = distanceDifference(firstTime, secondTime);

				if (deltaDistance > 2) {
					driver.setSpeeds(Constants.FORWARD_SPEED, 0);
					driver.moveForward(deltaDistance);
					driver.setSpeeds(Constants.FORWARD_SPEED, Constants.FORWARD_SPEED);
				}

				// reset firstLineSeen
				firstLineSeen = false;

			}
			else if (rightValue < LIGHT_THRESHOLD && firstLineSeen) {
				// right wheel is trailing
				Sound.beep();
				secondTime = System.currentTimeMillis() / 1000;
				int deltaDistance = distanceDifference(firstTime, secondTime);

				if (deltaDistance > 2) {
					driver.setSpeeds(0, Constants.FORWARD_SPEED);
					driver.moveForward(deltaDistance);
					driver.setSpeeds(Constants.FORWARD_SPEED, Constants.FORWARD_SPEED);
				}

				// reset firstLineSeen
				firstLineSeen = false;
			}
		}
	}

	private int distanceDifference(long firstTime, long secondTime) {

		int deltaTime = (int) (secondTime - firstTime);
		int deltaDistance = Constants.FORWARD_SPEED * deltaTime;

		return deltaDistance;
	}

	//checks whether theta is almost equal to currentAngle
	public boolean checkTheta(double theta, double currentAngle){
		return Math.abs(theta - currentAngle) < (Math.PI / 4);
	}

	//checks if distance traveled is almost equal to 30 cm 
	public boolean checkDistTraveled(double prev, double cur){
		double dist = Math.abs(cur - prev);
		if (dist > 29 && dist < 31) return true;
		return false;
	}
}