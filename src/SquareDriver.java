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
	
	public boolean isRotating() {
		return rotating;
	}
	
	public void setRotating(boolean rotating) {
		this.rotating = rotating;
	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	public void rotateCounter(double theta) {
		rotating = true;
		leftMotor.rotate(-convertAngle(Constants.RADIUS, Constants.WIDTH, theta), true);
		rightMotor.rotate(convertAngle(Constants.RADIUS, Constants.WIDTH, theta), false);
		rotating = false;
	}
	
	public void rotateClockwise(double theta) {
		rotating = true;
		leftMotor.rotate(convertAngle(Constants.RADIUS, Constants.WIDTH, theta), true);
		rightMotor.rotate(-convertAngle(Constants.RADIUS, Constants.WIDTH, theta), false);
		rotating = false;
	}
	
	public void moveForward(double dist) {
		leftMotor.rotate(convertDistance(Constants.RADIUS, dist),true);
		rightMotor.rotate(convertDistance(Constants.RADIUS, dist),false);
	}
	
	public void setSpeeds(int leftSpeed, int rightSpeed) {
		leftMotor.setSpeed(leftSpeed);
		rightMotor.setSpeed(rightSpeed);
	}
	
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
	
	public void checkMvt() {
		if (!leftMotor.isMoving() && !rightMotor.isMoving()) {
			rotating = false;
		}
	}
	
	public void stop() {
		leftMotor.stop();
		rightMotor.stop();
	}
}
