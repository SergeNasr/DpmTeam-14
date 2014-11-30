import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;


public class Claw {
	private NXTRegulatedMotor clawMotor;
	private SquareDriver driver;
	public boolean blockGrabbed;
	
	public Claw(NXTRegulatedMotor clawMotor, UltrasonicSensor usSensorFront, SquareDriver driver) {
		this.clawMotor = clawMotor;
		this.clawMotor.setSpeed(150);
		this.driver = driver;
		blockGrabbed = false;
	}

	public void processUSData(int distance) {
		blockGrabbed = true;
		driver.moveForward(- (22 - distance));
		clawMotor.rotate(-620);
		driver.moveForward(13);
		clawMotor.rotate(550);
	}
	
	public void dropObject() {
		clawMotor.rotate(-620);
		driver.moveForward(-20);
		clawMotor.rotate(550);
	}
}
