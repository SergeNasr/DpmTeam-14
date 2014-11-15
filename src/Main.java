import java.util.LinkedList;

import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;


public class Main {
	private static ColorSensor colorSensor = new ColorSensor(SensorPort.S4);
	private static NXTRegulatedMotor leftMotor = Motor.A;
	private static NXTRegulatedMotor rightMotor = Motor.C;
	
	private static final double InitPos = 15;
	
	public static void main(String[]args){
		// initialize all classes
		Odometer odo = new Odometer(InitPos, InitPos, leftMotor, rightMotor);
		SquareDriver driver = new SquareDriver(leftMotor, rightMotor);
		OdometryCorrection odoCor = new OdometryCorrection(odo, colorSensor, driver);
		
		// create map
		int [] obstacles = {1,7,8,14,21,34,56,67,78,89,90,100,112,113};
		Map map = new Map(obstacles);
		
		// TODO orienteering
		
		// create graph and path
		GraphGenerator gg = new GraphGenerator(map);
		gg.createGraph();
		
		Tiles start = gg.getGraph().get(1);			// need to modify those values!!
		Tiles dest =  gg.getGraph().get(2);
		LinkedList<Tiles> path = gg.bfs(start, dest);
		
		// convert path to points
		Point[] pointPath = new Point[path.size()];
		for (int i = 0; i < path.size(); i++) {
			pointPath[i] = Point.convertTileToPoint(path.get(i));
		}
		
		// travel/navigate
		Navigation navigator = new Navigation(driver,pointPath, odo);
		navigator.Navigate();
		
	}
}
