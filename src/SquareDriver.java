import lejos.nxt.NXTRegulatedMotor;


public class SquareDriver {
	
	private boolean rotating;	// used in odometry correction
	private NXTRegulatedMotor leftMotor;
	private NXTRegulatedMotor rightMotor;

	public SquareDriver(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.rotating = false;
	}
	
	/**
	 * Return left motor
	 * @return NXTRegulatedMotor leftmotor
	 */
	public NXTRegulatedMotor getLeftMotor() {
		return leftMotor;
	}

	/**
	 * Return right motor
	 * @return NXTRegulatedMotor rightmotor
	 */
	public NXTRegulatedMotor getRightMotor() {
		return rightMotor;
	}

	/**
	 * 
	 * @return boolean that represents whether the robot is rotating
	 */
	public boolean isRotating() {
		return rotating;
	}
	
	/**
	 * Sets the rotating boolean value
	 * @param rotating
	 */
	public void setRotating(boolean rotating) {
		this.rotating = rotating;
	}

	/**
	 * converts distance value in cm to number of wheel rotations in degrees
	 * @param radius
	 * @param distance
	 * @return converted distance
	 */
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	/**
	 * convert angle in degrees to number of rotations of the wheel in degrees
	 * @param radius
	 * @param width
	 * @param angle
	 * @return
	 */
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	/**
	 * rotates the driver by theta degrees counter clockwise
	 * @param theta
	 */
	public void rotateCounter(double theta) {
		rotating = true;
		leftMotor.rotate(-convertAngle(Constants.RADIUS, Constants.WIDTH, theta), true);
		rightMotor.rotate(convertAngle(Constants.RADIUS, Constants.WIDTH, theta), false);
		rotating = false;
	}
	
	/**
	 * rotates the driver by theta degrees clockwise
	 * @param theta
	 */
	public void rotateClockwise(double theta) {
		rotating = true;
		leftMotor.rotate(convertAngle(Constants.RADIUS, Constants.WIDTH, theta), true);
		rightMotor.rotate(-convertAngle(Constants.RADIUS, Constants.WIDTH, theta), false);
		rotating = false;
	}
	
	/**
	 * moves the driver forward by dist (in cm)
	 * @param dist
	 */
	public void moveForward(double dist) {
		leftMotor.rotate(convertDistance(Constants.RADIUS, dist),true);
		rightMotor.rotate(convertDistance(Constants.RADIUS, dist),false);
	}
	
	/**
	 * sets the speeds of the left and right motors
	 * @param leftSpeed
	 * @param rightSpeed
	 */
	public void setSpeeds(int leftSpeed, int rightSpeed) {
		leftMotor.setSpeed(leftSpeed);
		rightMotor.setSpeed(rightSpeed);
	}
	
	/**
	 * rotates the driver and returns right away (without waiting for the movement to finish)
	 * @param theta
	 */
	public void rotateWithDirectReturn(double theta) {
		rotating = true;
		if (theta > 0) {	// counter
		leftMotor.rotate(-convertAngle(Constants.RADIUS, Constants.WIDTH, theta), true);
		rightMotor.rotate(convertAngle(Constants.RADIUS, Constants.WIDTH, theta), true);
		}
		else {	//clockwise
			theta = -theta;
			leftMotor.rotate(convertAngle(Constants.RADIUS, Constants.WIDTH, theta), true);
			rightMotor.rotate(-convertAngle(Constants.RADIUS, Constants.WIDTH, theta), true);
		}
	}
	
	/**
	 * sets the rotating value to false if both motors are not moving
	 */
	public void checkMvt() {
		if (!leftMotor.isMoving() && !rightMotor.isMoving()) {
			rotating = false;
		}
	}
	
	/**
	 * stops the driver from moving
	 */
	public void stop() {
		leftMotor.stop();
		rightMotor.stop();
	}
}
