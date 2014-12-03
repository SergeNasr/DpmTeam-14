

public class Navigation {
	private SquareDriver driver;
	private double prevPosX; 
	private double prevPosY; 
	private double currentTheta;
	private Odometer odo;
	private Point [] points;
	private OdometryCorrection odoCor;
	
	public boolean finishedNav = false;

	public Navigation(SquareDriver driver, Point [] destinations, Odometer odometer, double currentTheta, OdometryCorrection odoCor){
		this.prevPosX = odometer.getX();
		this.prevPosY = odometer.getY();
		this.currentTheta = currentTheta;
		this.driver = driver;
		this.points = destinations;
		this.odo = odometer;
		this.odoCor = odoCor;
	}

	/**
	 * returns previous X position
	 * @return prevPosX
	 */
	public double getPrevPosX() {
		return prevPosX;
	}

	/**
	 * returns previous Y position
	 * @return prevPosY
	 */
	public double getPrevPosY() {
		return prevPosY;
	}

	/**
	 * navigates the robot along the Point array
	 */
	public void go(){
		prevPosX = points[0].getX();
		prevPosY = points[0].getY();
		
		for(int i = 1; i < points.length; i++) {
			// Set rotating speeds to motors to anticipate a rotation
			driver.setSpeeds(Constants.ROTATE_SPEED, Constants.ROTATE_SPEED);

			// Turn from current position to desired position
			double newTheta = direction(points[i]);
			double theta = currentTheta - newTheta;
			currentTheta = newTheta;
			
			if (theta > 0) {
				if (theta == 270){
					driver.rotateClockwise(90);
				}
				else { 
					driver.rotateCounter(theta);
				}
			}
			else if (theta < 0) {
				theta = -theta;
				if (theta == 270) {
					driver.rotateCounter(90);
				}
				else {
					driver.rotateClockwise(theta);
				}
			}

			// Go towards that position
			driver.setSpeeds(Constants.FORWARD_SPEED, Constants.FORWARD_SPEED);

			// ... until something prevents it to stop
			driver.moveForward(30.32);
			
			// apply angle correction
			if (odoCor.clockCor) {
				driver.rotateClockwise(odoCor.rotateClockAngle);
				odoCor.clockCor = false;
			}
			else if (odoCor.counterCor) {
				driver.rotateCounter(odoCor.rotateCounterAngle);
				odoCor.counterCor = false;
			}
		}
		
		//odoCor.setExitCorrection(true);
		finishedNav = true;
	}

	/**
	 * returns the direction the robot should be pointing at when going to Point point
	 * @param point
	 * @return direction in degrees (0, 90, 180, 270)
	 */
	private double direction(Point point){
		double dx = point.getX() - prevPosX;
		double dy = point.getY() - prevPosY;
		prevPosX = point.getX();
		prevPosY = point.getY();

		double direction = 0.0;
		if(Math.abs(dx) < 5 && dy > 0) {
			direction = 90;
		}
		else if(Math.abs(dx) < 5 && dy < 0) {
			direction = 270;
		}
		else if(dx > 0 && Math.abs(dy) < 5) {
			direction = 0;
		}
		else if(dx < 0 && Math.abs(dy) < 5) {
			direction = 180;
		}
		else {
			direction = currentTheta;
		}

		return direction;	
	}
}