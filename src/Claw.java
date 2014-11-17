import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;


public class Claw implements UltrasonicController {
	private NXTRegulatedMotor clawMotor;
	private UltrasonicSensor usSensorFront;
	private SquareDriver driver;
	
	public Claw(NXTRegulatedMotor clawMotor, UltrasonicSensor usSensorFront, SquareDriver driver) {
		clawMotor = clawMotor;
		usSensorFront =usSensorFront;
		driver = driver;
	}

	@Override
	public void processUSData(int distance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int readUSDistance() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
