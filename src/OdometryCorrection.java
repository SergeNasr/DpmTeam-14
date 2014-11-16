import lejos.nxt.ColorSensor;
import lejos.nxt.Sound;

/* 
 * OdometryCorrection.java
 */

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private ColorSensor colorSensor;
	private SquareDriver driver;

	//distance from light sensor to center of rotation
	private static final double DISTANCE_SENSOR = 7;
	private static final int LIGHT_THRESHOLD = 45;
	private static final double FIRST_LINE = 30;

	// constructor
	public OdometryCorrection(Odometer odometer, ColorSensor colorSensor, SquareDriver driver) {
		this.odometer = odometer;
		this.colorSensor = colorSensor;
		this.driver = driver;
	}

	// run method (required for Thread)
	public void run() {
		colorSensor.setFloodlight(true);
		double previous_x = 15;
		double previous_y = 15;

		long correctionStart, correctionEnd;

		while (true) {
			correctionStart = System.currentTimeMillis();

			//apply correction if robot is not turning
			if(!driver.isRotating()){
				
				double currentTheta = odometer.getAngle();
				double current_x = odometer.getX();
				double current_y = odometer.getY();
				
				// put your correction code here
				if(colorSensor.getLightValue() < LIGHT_THRESHOLD){
					Sound.beep();
					double x = current_x;
					double y = current_y;
					
					//check if robot is traveling along the first side
					if(checkTheta(currentTheta, 0)){
						//robot reaches first line --> update position
						if(isFirstLine(current_x)){
							x = FIRST_LINE + DISTANCE_SENSOR;
						}
						//robot reaches second line ---> check whether to update position or not
						else {
							x = checkDistTraveled(previous_x , current_x) ? current_x : previous_x + 15 + DISTANCE_SENSOR;
						}
					}
					//check if robot is traveling along the second side
					else if(checkTheta(currentTheta, Math.PI / 2)){
						if(isFirstLine(current_y)){
							y = FIRST_LINE + DISTANCE_SENSOR;
						}
						else {
							y = checkDistTraveled(previous_y , current_y) ? current_y : previous_y + 15 + DISTANCE_SENSOR;
						}
						
					}
					//check if robot is traveling along the third side
					else if(checkTheta(currentTheta, Math.PI)){
						x = checkDistTraveled(previous_x , current_x) ? current_x : previous_x - 15 - DISTANCE_SENSOR;
					}
					//check if robot is traveling along the fourth side
					else if(checkTheta(currentTheta, 3 * Math.PI / 2)){
						y = checkDistTraveled(previous_y , current_y) ? current_y : previous_y - 15 - DISTANCE_SENSOR;
					}
					
					if(x != current_x) {
						odometer.setX(x);
						previous_x = x;
					}
					else {
						previous_x = current_x;
					}
					
					if(y != current_y) {
						odometer.setY(y);
						previous_y = y;
					}
					else {
						previous_y = current_y;
					}
					
				}


				// this ensure the odometry correction occurs only once every period
				correctionEnd = System.currentTimeMillis();
				if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
					try {
						Thread.sleep(CORRECTION_PERIOD
								- (correctionEnd - correctionStart));
					} catch (InterruptedException e) {
						// there is nothing to be done here because it is not
						// expected that the odometry correction will be
						// interrupted by another thread
					}
				}
			}
		}
	}

	//checks whether theta is almost equal to currentAngle
	public boolean checkTheta(double theta, double currentAngle){
		return Math.abs(theta - currentAngle) < (Math.PI / 4);
	}
	
	//checks if robot reached first line
	public boolean isFirstLine(double coord){
		if(Math.abs(coord - FIRST_LINE) < 10){
			return true;
		}
		return false;
	}
	
	//checks if distance traveled is almost equal to 30 cm 
	public boolean checkDistTraveled(double prev, double cur){
		double dist = Math.abs(cur - prev);
		if (dist > 29 && dist < 31) return true;
		return false;
	}
}