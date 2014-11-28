import lejos.nxt.LCD;


public class Navigation extends Thread {
	private SquareDriver driver;
	private double prevPosX; 
	private double prevPosY; 
	private double currentTheta;
	private Odometer odo;
	private Point [] points;
	private OdometryCorrection odoCor;

	public Navigation(SquareDriver driver, Point [] destinations, Odometer odometer, double currentTheta, OdometryCorrection odoCor){
		// TODO change values of prevPosX and prevPosY after orienteering
		this.prevPosX = odometer.getX();
		this.prevPosY = odometer.getY();
		this.currentTheta = currentTheta;
		this.driver = driver;
		this.points = destinations;
		this.odo = odometer;
		this.odoCor = odoCor;
	}

	public void run(){
		prevPosX = points[0].getX();
		prevPosY = points[0].getY();
		
		for(int i = 1; i < points.length; i++){
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
			driver.moveForward(30);
			
			// apply angle correction
			if (odoCor.clockCor) {
				driver.rotateClockwise(odoCor.rotateClockAngle);
				double checkDistance = Math.abs((driver.getRightMotor().getTachoCount() - odoCor.distanceTacho) * (2 * Math.PI * Constants.RADIUS) /360);
				if (Math.abs(16 - checkDistance) > 2) {
					driver.moveForward(Math.abs(16 - checkDistance));
				}
				
				odoCor.clockCor = false;
			}
			else if (odoCor.counterCor) {
				driver.rotateCounter(odoCor.rotateCounterAngle);
				double checkDistance = Math.abs((driver.getLeftMotor().getTachoCount() - odoCor.distanceTacho) * (2 * Math.PI * Constants.RADIUS) /360);
				if (Math.abs(16 - checkDistance) > 2) {
					driver.moveForward(Math.abs(16 - checkDistance));
				}
				odoCor.counterCor = false;
			}
		}
		
		odoCor.setExitCorrection(true);

	}

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