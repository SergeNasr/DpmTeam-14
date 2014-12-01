import java.util.LinkedList;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.util.TextMenu;


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
	public static double currentTheta;
	
	private static final double InitPos = 15;
	private static int [] data;
	
	public static void main(String[]args){
		int buttonChoice;
		
		Odometer odo = new Odometer(InitPos, InitPos, leftMotor, rightMotor);	// TODO modify X and Y for testing
		OdometryDisplay odometryDisplay = new OdometryDisplay(odo);
		SquareDriver driver = new SquareDriver(leftMotor, rightMotor);
		OdometryCorrection odoCor = new OdometryCorrection(odo, colorSensorLeft, colorSensorRight, driver);
		Claw claw = new Claw(clawMotor, usSensorFront, driver);
		UltrasonicPoller usPoller = new UltrasonicPoller(usSensorFront, claw, driver);
		
		int [] map1 = {3,16,25,34,53,56,58,61,69,71,76,89,94,98,103,111,114,119,129,132,138,141};
		int [] map2 = {10,17,21,28,29,31,43,48,59,64,73,76,77,78,81,84,85,93,112,120,130,140};
		int [] map3 = {8,12,16,35,37,43,53,63,65,67,70,77,88,95,97,99,107,112,120,125,140,143};
		int [] map4 = {4,15,18,24,28,37,45,70,75,84,85,86,91,93,94,97,101,109,112,132,135,142};
		int [] map5 = {16,17,21,23,31,36,37,42,53,56,65,68,70,73,81,97,100,101,118,120,124,129};
		int [] map6 = {6,13,16,21,23,31,44,46,53,66,68,74,79,83,90,94,99,112,113,114,117,142};
		int [][] maps = {map1,map2,map3,map4,map5,map6};
		
		LCD.clear();
		String[] mapsMenu = {"Map 1", "Map 2", "Map 3", "Map 4", "Map 5", "Map 6"};
		TextMenu menu = new TextMenu(mapsMenu,1,"Maps menu");
		int mapSelected = menu.select();
		Map map = new Map(maps[mapSelected]);
		
		LCD.clear();
		String[] xCoord = {"-1","0","1","2","3","4","5","6","7","8","9","10"};
		TextMenu xMenu = new TextMenu(xCoord,1,"X Coordinate");
		int xSelected = xMenu.select();
		
		LCD.clear();
		String[] yCoord = {"-1","0","1","2","3","4","5","6","7","8","9","10"};
		TextMenu yMenu = new TextMenu(yCoord,1,"Y Coordinate");
		int ySelected = xMenu.select();
		
		int colDrop = Integer.parseInt(xCoord[xSelected]) + 1;
		int rowDrop = Constants.MAZE_SIZE - 2 - Integer.parseInt(yCoord[ySelected]);
		LCD.clear();
		LCD.drawString(mapsMenu[mapSelected], 0, 0);
		LCD.drawString(xCoord[xSelected], 0, 1);
		LCD.drawString(yCoord[ySelected], 0, 2);
		
		do {
			// clear the display
			LCD.clear();

			// ask the user whether the motors should drive in a square or float
			LCD.drawString("       | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString(" 	   | Start  ", 0, 2);
			LCD.drawString("	   |        ", 0, 3);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_RIGHT);
		
		if(buttonChoice == Button.ID_RIGHT){
			// Odometer and Correction starts
			odo.start();
			odometryDisplay.start();
			odoCor.start();
			
			Albert_Algo albert = new Albert_Algo(map, driver, usSensorFront, usSensorBack, odoCor);
			data = albert.localize();
			
			leftMotor = Motor.C;
			rightMotor = Motor.A;
			driver = new SquareDriver(leftMotor, rightMotor);
			odoCor.setDriver(driver);
			
			GraphGenerator gg = new GraphGenerator(map);
			gg.createGraph();

			LCD.clear();

			int col = data[1];
			int row = Constants.MAZE_SIZE - 1 - data[2];

			double colToX = 15 + 30 * col;
			double rowToY = 15 + 30 * row;
			
			odo.setX(colToX);
			odo.setY(rowToY);
			
			setOdo(odo);
			
			int st = gg.findTileId(row, col);
			int de = gg.findTileId(rowDrop, colDrop);

			Tiles start = gg.getGraph().get(st);			// need to modify those values!! And value in Navigation for prevPos
			Tiles dest =  gg.getGraph().get(de);
			LinkedList<Tiles> path = gg.bfs(start, dest);
			
			Point[] pointPath = new Point[path.size() + 1];
			for (int i = 0; i < path.size(); i++) {	//removed first element because it is the current tile
				pointPath[i] = Point.convertTileToPoint(path.get(i));
			}
			pointPath[path.size()] = new Point(45, 195);

			Navigation navigator = new Navigation(driver, pointPath, odo, currentTheta, odoCor);

			navigator.go();
			
			while (!navigator.finishedNav);
			
			claw.dropObject();
		}
	}

	private static void setOdo(Odometer odo){
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
	}
	}
