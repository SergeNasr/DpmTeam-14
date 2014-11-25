import lejos.nxt.LCD;


public class Navigation extends Thread {
	private SquareDriver driver;
	private double prevPosX; 
	private double prevPosY; 
	private double currentTheta;
	private Odometer odo;
	private Point [] points;

	public Navigation(SquareDriver driver, Point [] destinations, Odometer odometer){
		// TODO change values of prevPosX and prevPosY after orienteering
		this.prevPosX = odometer.getX();
		this.prevPosY = odometer.getY();
		this.currentTheta = odometer.getAngle();
		this.driver = driver;
		this.points = destinations;
		this.odo = odometer;
	}

	public void run(){
		for(int i = 0; i < points.length; i++){
			// Set rotating speeds to motors to anticipate a rotation
			driver.setSpeeds(Constants.ROTATE_SPEED, Constants.ROTATE_SPEED);

			// Turn from current position to desired position
			double newTheta = direction(points[i]);
			double theta = currentTheta - newTheta;
			if (theta > 0) {
				if (theta == (3 * Math.PI / 2)){
					driver.rotateClockwise(90);
				}
				else { 
					driver.rotateCounter((theta * 180) / Math.PI);
				}
			}
			else if (theta < 0) {
				theta = -theta;
				if (theta == (3 * Math.PI / 2)) {
					driver.rotateCounter(90);
				}
				else {
					driver.rotateClockwise((theta * 180) / Math.PI);
				}
			}
			
			currentTheta = newTheta;

			// Go towards that position
			driver.setSpeeds(Constants.FORWARD_SPEED, Constants.FORWARD_SPEED);

			// ... until something prevents it to stop
			driver.moveForward(30);
		}

	}

	private double direction(Point point){
		double dx = point.getX() - prevPosX;
		double dy = point.getY() - prevPosY;
		prevPosX = point.getX();
		prevPosY = point.getY();

		double direction = 0.0;

		if(dx == 0 && dy > 0) {
			direction = (Math.PI / 2);
		}
		else if(dx == 0 && dy < 0) {
			direction = (3 * Math.PI / 2);
		}
		else if(dx > 0 && dy == 0) {
			direction = 0;
		}
		else if(dx < 0 && dy == 0) {
			direction = Math.PI;
		}

		return direction;	
	}
}