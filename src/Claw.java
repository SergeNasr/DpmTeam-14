import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;


public class Claw implements UltrasonicController {
	private NXTRegulatedMotor clawMotor;
	private UltrasonicSensor usSensorFront;
	private SquareDriver driver;
	public boolean blockGrabbed;
	
	public Claw(NXTRegulatedMotor clawMotor, UltrasonicSensor usSensorFront, SquareDriver driver) {
		this.clawMotor = clawMotor;
		this.clawMotor.setSpeed(150);
		this.usSensorFront =usSensorFront;
		this.driver = driver;
		blockGrabbed = false;
	}

	public void processUSData(int distance) {
		driver.moveForward(- (20 - distance));
		
		clawMotor.rotate(-610);
		driver.moveForward(13);
		clawMotor.rotate(600);
		blockGrabbed = true;
	}
}
