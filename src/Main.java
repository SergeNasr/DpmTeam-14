import java.util.LinkedList;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;


public class Main {
	private static ColorSensor colorSensorLeft = new ColorSensor(SensorPort.S4);
	private static ColorSensor colorSensorRight = new ColorSensor(SensorPort.S2);
	private static final SensorPort usPortOne = SensorPort.S1;
	private static final SensorPort usPortThree = SensorPort.S3;
	private static UltrasonicSensor usSensorFront = new UltrasonicSensor(usPortThree);
	private static UltrasonicSensor usSensorBack = new UltrasonicSensor(usPortOne);
	private static NXTRegulatedMotor leftMotor = Motor.C;
	private static NXTRegulatedMotor rightMotor = Motor.A;
	private static NXTRegulatedMotor clawMotor = Motor.B;

	private static final double InitPos = 15;

	public static void main(String[]args){
		int buttonChoice;

		// initialize all classes
		Odometer odo = new Odometer(InitPos, InitPos, leftMotor, rightMotor);	// TODO modify X and Y for testing
		OdometryDisplay odometryDisplay = new OdometryDisplay(odo);
		SquareDriver driver = new SquareDriver(leftMotor, rightMotor);
		OdometryCorrection odoCor = new OdometryCorrection(odo, colorSensorLeft, colorSensorRight, driver);
		Claw claw = new Claw(clawMotor, usSensorFront, driver);
		UltrasonicPoller usPoller = new UltrasonicPoller(usSensorFront, claw, driver);

		// create map
		int[] mapTest = {1, 7, 8, 14};
		int [] map1 = {2,3,11,16,17,20,23,27,37,40,45,48,53,64};
		int [] map2 ={4,5,8,10,13,16,17,27,29,35,52,56,59,62,64};
		int [] map3 ={1,5,11,12,16,22,28,31,32,35,36,44,61,62,64};

		// TODO create map depending on test
		Map map = new Map(mapTest);

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
			LCD.clear();

			// orienteering
			//Albert_Algo albert = new Albert_Algo(map, driver, usSensorFront, usSensorBack);
			//int [] data = albert.localize();

			// create graph and path
			GraphGenerator gg = new GraphGenerator(map);
			gg.createGraph();

			// Odometer and Correction starts
			odo.start();
			//odometryDisplay.start();
			//odoCor.start();
			
			// TODO convert row, col and poiting direction and then set odometer X and Y
			odo.setX(105);
			odo.setY(105);
			odo.setAngle(3 * Math.PI / 2);

			// go from Tile #16 to Tile #4
			// TODO use gg.findTileId(row, col)
			int st = gg.findTileId(3, 3);
			int de = gg.findTileId(0, 3);
			
			Tiles start = gg.getGraph().get(st);			// need to modify those values!! And value in Navigation for prevPos
			Tiles dest =  gg.getGraph().get(de);
			LinkedList<Tiles> path = gg.bfs(start, dest);

			// convert path to points
			Point[] pointPath = new Point[path.size() - 1];
			for (int i = 0; i < path.size() - 1; i++) {	//removed first element because it is the current tile
				pointPath[i] = Point.convertTileToPoint(path.get(i + 1));
				//System.out.println(pointPath[i].getX() + " " + pointPath[i].getY());
			}

			Navigation navigator = new Navigation(driver, pointPath, odo);
//			//			
//			//			Thread[] threads = new Thread[2];
//			//			threads[0] = navigator;
//			//			threads[1] = usPoller;
//			//
//			//			// travel/navigate (in a different thread)
//			//			// TODO need to pass values of the robots positions
			navigator.start();
			//			
			//			try {
			//	            navigator.join();
			//	        } catch (InterruptedException e) {
			//	            System.out.println("Error in thread join");
			//	        }
			//	         
			//			// claw grab and lift
			//			usPoller.start();
			//
			//			try {
			//	            navigator.join();
			//	        } catch (InterruptedException e) {
			//	            System.out.println("Error in thread join");
			//	        }
			//	         
			//			// claw grab and lift
			//			usPoller.start();

		}

		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);

	}
}
