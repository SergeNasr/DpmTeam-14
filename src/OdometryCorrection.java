import lejos.nxt.ColorSensor;

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
		
		// TODO implement correction with 2 color sensors
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