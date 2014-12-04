import lejos.nxt.NXTRegulatedMotor;

/**
 * 
 * @author Serge Nasr
 * @author Eduardo Coronado-Montoya
 *
 */
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
	 * Check if the robot is rotating
	 * @return True if rotating, false if not
	 */
	public boolean isRotating() {
		return rotating;
	}
	
	/**
	 * Sets the rotating boolean value
	 * @param rotating True if rotating, false if not
	 */
	public void setRotating(boolean rotating) {
		this.rotating = rotating;
	}

	/**
	 * Converts distance value in cm to number of wheel rotations in degrees
	 * @param radius The radius of the wheel
	 * @param distance The distance in degrees/second
	 * @return converted distance
	 */
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	/**
	 * Convert angle in degrees to number of rotations of the wheel in degrees
	 * @param radius The radius of the wheel
	 * @param width The width between the two wheels of the robot
	 * @param angle The angle by which the robot has to turn
	 * @return The number of rotations of the wheel
	 */
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	/**
	 * Rotates the robot by some degrees in a counter clockwise fashion
	 * @param theta The number of degrees by which the robot has to turn
	 */
	public void rotateCounter(double theta) {
		rotating = true;
		leftMotor.rotate(-convertAngle(Constants.RADIUS, Constants.WIDTH, theta), true);
		rightMotor.rotate(convertAngle(Constants.RADIUS, Constants.WIDTH, theta), false);
		rotating = false;
	}
	
	/**
	 * Rotates the robot by some degrees in a clockwise fashion
	 * @param theta The number of degrees by which the robot has to turn
	 */
	public void rotateClockwise(double theta) {
		rotating = true;
		leftMotor.rotate(convertAngle(Constants.RADIUS, Constants.WIDTH, theta), true);
		rightMotor.rotate(-convertAngle(Constants.RADIUS, Constants.WIDTH, theta), false);
		rotating = false;
	}
	
	/**
	 * Moves the robot forward
	 * @param dist Amount by which the robot has to move
	 */
	public void moveForward(double dist) {
		leftMotor.rotate(convertDistance(Constants.RADIUS, dist),true);
		rightMotor.rotate(convertDistance(Constants.RADIUS, dist),false);
	}
	
	/**
	 * Sets the speeds of the left and right motors
	 * @param leftSpeed Speed for left motor
	 * @param rightSpeed Speed for right motor
	 */
	public void setSpeeds(int leftSpeed, int rightSpeed) {
		leftMotor.setSpeed(leftSpeed);
		rightMotor.setSpeed(rightSpeed);
	}
	
	/**
	 * Rotates the driver and returns right away (without waiting for the movement to finish)
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
	 * Sets the rotating value to false if both motors are not moving
	 */
	public void checkMvt() {
		if (!leftMotor.isMoving() && !rightMotor.isMoving()) {
			rotating = false;
		}
	}
	
	/**
	 * Stops the driver from moving
	 */
	public void stop() {
		leftMotor.stop();
		rightMotor.stop();
	}
}
