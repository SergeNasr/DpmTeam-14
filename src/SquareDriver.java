import lejos.nxt.NXTRegulatedMotor;


public class SquareDriver {
	private static final double RADIUS = 2.05;
	private static final double WIDTH = 15.3;
	
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
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	public void rotateCounter(double theta) {
		rotating = true;
		leftMotor.rotate(-convertAngle(RADIUS, WIDTH, theta), true);
		rightMotor.rotate(convertAngle(RADIUS, WIDTH, theta), false);
		rotating = false;
	}
	
	public void rotateClockwise(double theta) {
		rotating = true;
		leftMotor.rotate(convertAngle(RADIUS, WIDTH, theta), true);
		rightMotor.rotate(-convertAngle(RADIUS, WIDTH, theta), false);
		rotating = false;
	}
	
	public void moveForward(double dist) {
		leftMotor.rotate(convertDistance(RADIUS, dist),true);
		rightMotor.rotate(convertDistance(RADIUS, dist),false);
	}
	
	public void setSpeeds(int leftSpeed, int rightSpeed) {
		leftMotor.setSpeed(leftSpeed);
		rightMotor.setSpeed(rightSpeed);
	}
	
}
