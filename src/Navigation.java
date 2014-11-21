
public class Navigation extends Thread {
	private SquareDriver driver;
	private double prevPosX; 
	private double prevPosY; 
	private double currentTheta = Math.PI/2;
	private Odometer odo;
	private Point [] points;

	public Navigation(SquareDriver driver, Point [] destinations, Odometer odometer){
		// TODO change values of prevPosX and prevPosY after orienteering
		this.prevPosX = odometer.getX();
		this.prevPosY = odometer.getY();
		this.driver = driver;
		this.points = destinations;
		this.odo = odometer;
	}

	public void run(){
		for(int i = 0; i < points.length; i++){
			// Set rotating speeds to motors to anticipate a rotation
			driver.setSpeeds(Constants.ROTATE_SPEED, Constants.ROTATE_SPEED);

			// Turn from current position to desired position
			double theta = calculatedAngle(points[i]);
			if (theta > 0) {
				driver.rotateClockwise(theta);
			}
			else {
				theta = -theta;
				driver.rotateCounter(theta);

			}

			// Go towards that position
			driver.setSpeeds(Constants.FORWARD_SPEED, Constants.FORWARD_SPEED);

			// ... until something prevents it to stop
			driver.moveForward(distanceTo(points[i]));

			// using odometer
			prevPosX = odo.getX();
			prevPosY = odo.getY();

		}

	}

	private double calculatedAngle(Point point){
		double direction = direction(point);
		double turnBy;

		if(currentTheta > direction){
			if(currentTheta - direction > Math.PI){
				turnBy = -(2*Math.PI - (currentTheta - direction));
			} else {
				turnBy = (currentTheta - direction);
			}
		} else{ // currentTheta < direction
			if(direction - currentTheta > Math.PI){
				turnBy = (2*Math.PI - (direction - currentTheta));
			} else {
				turnBy = -(direction - currentTheta);
			}
		}
		currentTheta = direction;
		turnBy = turnBy * 360 / (2*Math.PI);
		return turnBy;
	}

		private double direction(Point point){
			double dx = point.getX() - prevPosX;
			double dy = point.getY() - prevPosY;
			double direction = 0.0;
			
			if(dx > 0 && dy > 0)
				direction = Math.atan(Math.abs(dy/dx));
			else if(dx > 0 && dy < 0)
				direction = 2*Math.PI - Math.atan(Math.abs(dy/dx));
			else if(dx < 0 && dy < 0)
				direction = Math.PI + Math.atan(Math.abs(dy/dx));
			else if(dx < 0 && dy > 0)
				direction = Math.PI - Math.atan(Math.abs(dy/dx));
			else if(dx == 0 && dy > 0)
				direction = Math.PI/2;
			else if(dx == 0 && dy < 0)
				direction = Math.PI*(3/2);
			else if(dx > 0 && dy == 0)
				direction = 0;
			else if(dx < 0 && dy == 0)
				direction = Math.PI;
			return direction;	
		}

	public double distanceTo(Point point){
		double dx = point.getX() - prevPosX;
		double dy = point.getY() - prevPosY;

		return Math.sqrt(dx*dx + dy*dy);
	}
}