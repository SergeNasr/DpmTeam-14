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
	private static NXTRegulatedMotor leftMotor = Motor.A;
	private static NXTRegulatedMotor rightMotor = Motor.C;
	private static NXTRegulatedMotor clawMotor = Motor.B;

	public static double currentX;
	public static double currentY;

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
		int [] map1 = {2,3,11,16,17,20,23,27,37,40,45,47,48,53,64};
		int [] map2 ={4,5,8,10,13,16,17,27,29,35,52,56,59,62,64};
		int [] map3 ={1,5,11,12,16,22,28,31,32,35,36,44,61,62,64};

		Map map = new Map(map2);

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

			// Odometer and Correction starts
			odo.start();
			odometryDisplay.start();
			odoCor.start();


			//orienteering
			Albert_Algo albert = new Albert_Algo(map, driver, usSensorFront, usSensorBack, odoCor);
			int[] data = albert.localize();

			//************************* TEST **********************
			//			int i = 0;
			//			while(true){
			//				if(i == 3){
			//					driver.rotateClockwise(90);
			//					i %= 3;
			//				} else {
			//					driver.moveForward(30);
			//					if (odoCor.clockCor) {
			//						System.out.println("clock " + odoCor.rotateClockAngle);
			//						driver.rotateCounter(odoCor.rotateClockAngle);
			//						odoCor.clockCor = false;
			//					}
			//					else if (odoCor.counterCor) {
			//						System.out.println("counter " + odoCor.rotateCounterAngle);
			//						driver.rotateClockwise(odoCor.rotateCounterAngle);
			//						odoCor.counterCor = false;
			//					}
			//					Delay.msDelay(200);
			//					i++;
			//				}
			//			}
			//*********************************************************

			leftMotor = Motor.C;
			rightMotor = Motor.A;
			driver = new SquareDriver(leftMotor, rightMotor);
			odoCor.setDriver(driver);

			// create graph and path
			GraphGenerator gg = new GraphGenerator(map);
			gg.createGraph();

			LCD.clear();

			int col = data[1];
			int row = Constants.MAZE_SIZE - 1 - data[2];

			double colToX = 15 + 30 * col;
			double rowToY = 15 + 30 * row;
			double currentTheta = -1;

			odo.setX(colToX);
			odo.setY(rowToY);

			if ((char)data[0] == 'n') {
				odo.setAngle(270);
				odo.setTheta(270);
				currentTheta = 270;
			}
			else if ((char)data[0] == 'e') {
				odo.setAngle(0);
				odo.setTheta(0);
				currentTheta =  0;
			}
			else if ((char)data[0] == 's') {
				odo.setAngle(90);
				odo.setTheta(90);
				currentTheta = 90;
			}
			else if ((char)data[0] == 'w') {
				odo.setAngle(180);
				odo.setTheta(180);
				currentTheta = 180;
			}

			// go from Tile #16 to Tile #4
			// TODO use gg.findTileId(row, col)
			int st = gg.findTileId(row, col);
			int de = gg.findTileId(5, 1);

			Tiles start = gg.getGraph().get(st);			// need to modify those values!! And value in Navigation for prevPos
			Tiles dest =  gg.getGraph().get(de);
			LinkedList<Tiles> path = gg.bfs(start, dest);

			// convert path to points
			Point[] pointPath = new Point[path.size() + 1];
			for (int i = 0; i < path.size(); i++) {	//removed first element because it is the current tile
				pointPath[i] = Point.convertTileToPoint(path.get(i));
			}
			pointPath[path.size()] = new Point(45, 195);

			Navigation navigator = new Navigation(driver, pointPath, odo, currentTheta, odoCor);

			navigator.go();
			
			while (!navigator.finishedNav);

			usPoller.prevRow = row;
			usPoller.prevCol = col;
			
			// claw grab and lift
			while (!claw.blockGrabbed) {
				usPoller.findBlock();
			}
			
			driver.rotateCounter(90);

			// go to final destination
			// TODO modify those values
			row = (int)usPoller.prevRow;
			col = (int)usPoller.prevRow;
			currentTheta = usPoller.currentTheta;

			st = gg.findTileId(row, col);
			de = gg.findTileId(0, 0);

			start = gg.getGraph().get(st);			// need to modify those values!! And value in Navigation for prevPos
			dest =  gg.getGraph().get(de);
			path = gg.bfs(start, dest);

			// convert path to points
			pointPath = new Point[path.size()];
			for (int i = 0; i < path.size(); i++) {	//removed first element because it is the current tile
				pointPath[i] = Point.convertTileToPoint(path.get(i));
			}

			navigator = new Navigation(driver, pointPath, odo, currentTheta, odoCor);
			navigator.finishedNav = false;
			navigator.go();

			while (!navigator.finishedNav);
			
			claw.dropObject();

		}

		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);

	}
}
