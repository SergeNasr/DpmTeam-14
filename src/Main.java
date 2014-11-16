import java.util.LinkedList;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;


public class Main {
	private static ColorSensor colorSensor = new ColorSensor(SensorPort.S4);
	private static NXTRegulatedMotor leftMotor = Motor.A;
	private static NXTRegulatedMotor rightMotor = Motor.C;
	
	private static final double InitPos = 15;
	
	public static void main(String[]args){
		int buttonChoice;
		
		// initialize all classes
		Odometer odo = new Odometer(InitPos, InitPos, leftMotor, rightMotor);
		OdometryDisplay odometryDisplay = new OdometryDisplay(odo);
		SquareDriver driver = new SquareDriver(leftMotor, rightMotor);
		OdometryCorrection odoCor = new OdometryCorrection(odo, colorSensor, driver);
		
		// TODO create obstacles for each map and insert coordinates of location of the block
		
		// create map
		int [] obstacles = {1,7,8,14,21,34,56,67,78,89,90,100,112,113};
		Map map = new Map(obstacles);
		
		do {
			// clear the display
			LCD.clear();

			// ask the user whether the motors should drive in a square or float
			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString(" Float | Start  ", 0, 2);
			LCD.drawString("motors |        ", 0, 3);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_LEFT) {
			for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { Motor.A, Motor.C }) {
				motor.forward();
				motor.flt();
			}

			// start only the odometer and the odometry display
			odo.start();
			odometryDisplay.start();
			
		} else {
			
			// TODO orienteering
			
			// create graph and path
			GraphGenerator gg = new GraphGenerator(map);
			gg.createGraph();
			
			// Odometer and Correction starts
			odo.start();
			odometryDisplay.start();
			odoCor.start();
			
			Tiles start = gg.getGraph().get(1);			// need to modify those values!!
			Tiles dest =  gg.getGraph().get(2);
			LinkedList<Tiles> path = gg.bfs(start, dest);
			
			// convert path to points
			Point[] pointPath = new Point[path.size()];
			for (int i = 0; i < path.size(); i++) {
				pointPath[i] = Point.convertTileToPoint(path.get(i));
			}

			Navigation navigator = new Navigation(driver, pointPath, odo);
			// travel/navigate (in a different thread)
			// TODO need to pass values of the robots positions
			navigator.start();
			
			// TODO Claw/Grab-List block
				
		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
		
	}
}
