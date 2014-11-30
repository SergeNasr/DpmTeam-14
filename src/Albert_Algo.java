import java.util.Arrays;
import java.util.LinkedList;

import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
/**
 * 
 * @author Eduardo Coronado-Montoya
 *
 */
public class Albert_Algo {
	private static SquareDriver driver;
	private static Tiles[][] mapTiles;
	private static UltrasonicSensor usSensorFront;
	private static UltrasonicSensor usSensorBack;
	private static final double TILEAWAY_0 = 25;
	private static final double TILEAWAY_1 = 55;
	private static final double TILEAWAY_2 = 85;
	private static LinkedList<Arrow> possibilities = new LinkedList<Arrow>();
	private static LinkedList<PathNode> currentPath;
	private static LinkedList<Integer> indexes = new LinkedList<Integer>();
	private static OdometryCorrection odoCor;
	private static double prevPosX;
	private static double prevPosY;
	
	
	// north = 0
	// west = 1
	// south = 2
	// east = 3
	private static Arrow cur;
	private static int ind;
	
	/**
	 * 
	 * @param row The row as seen from the origin
	 * @return The row for a 2D array
	 */
	public static int getRowMap(int row){
		return Constants.MAZE_SIZE - 1 - row;
	}
	
	/**
	 * 
	 * @param map The map it will localize on
	 * @param driver The driver that will allow it to move
	 * @param usSensorFront The front Ultrasonic sensor
	 * @param usSensorBack The back Ultrasonic sensor
	 */
	public Albert_Algo(Map map, SquareDriver driver, UltrasonicSensor usSensorFront, UltrasonicSensor usSensorBack, OdometryCorrection odocor) {
		odoCor = odocor;
		this.usSensorFront = usSensorFront;
		this.usSensorBack = usSensorBack;
		this.driver = driver;
		mapTiles = map.getMap();
		currentPath = new LinkedList<PathNode>();
	}
	
	/**
	 * 
	 * @param rlf The number representing forward(0), left(1), or right(3)
	 * @return The string value of the number
	 */
	public static String movementType(int rlf){
		if(rlf == 0){
			return "forward";
		} else if(rlf == 1){
			return "turnLeft";
		} else if(rlf == 3){
			return "turnRight";
		} else return null;
		
	}
	
	public static void applyCorrection(){
		if (odoCor.clockCor) {
			driver.rotateCounter(odoCor.rotateClockAngle);
			odoCor.clockCor = false;
		}
		else if (odoCor.counterCor) {
			driver.rotateClockwise(odoCor.rotateCounterAngle);
			odoCor.counterCor = false;
		}
	}
	/**
	 * 
	 * @return The int array containing the heading, the column and the row of the robot
	 */
	public int[] localize(){
		while (possibilities.size() == 0) {
			for (int i = 0; i < mapTiles.length; i++) {
				for (int j = 0; j < mapTiles[i].length; j++) {
					for (int k = 0; k < 4; k++) {
						if (!mapTiles[i][j].getIsObstacle()){
							possibilities.add(new Arrow(getRowMap(i), j, mapTiles[i][j]
									.getPositionsArrows(k).getPoint()));
						}
					}
				}
			}
			
			String mvtType = null;
			int data_front = numTilesAway(fwd_getFilteredData());
			int data_back = numTilesAway(back_getFilteredData());
			
			currentPath.add(new PathNode("forward",data_back));
			updatePositionsBack();
			data_front = numTilesAway(fwd_getFilteredData());
			currentPath.add(new PathNode("forward", data_front));
			updatePositions(true);
			if(data_front != 0){
				driver.setSpeeds(Constants.FORWARD_SPEED, Constants.FORWARD_SPEED);
				driver.moveForward(30);
				applyCorrection();
			}
			
			while (possibilities.size() > 1) {
				if (data_front == 0 || data_front == 1 || data_front == 2) {
					int best_ratio = getBestRatio(false);
					LCD.drawInt(best_ratio, 0, 0);
					if(best_ratio == 1){
						driver.setSpeeds(Constants.ROTATE_SPEED, Constants.ROTATE_SPEED);
						driver.rotateClockwise(90);
					} else if (best_ratio == 3){
						driver.setSpeeds(Constants.ROTATE_SPEED, Constants.ROTATE_SPEED);
						driver.rotateCounter(90);
					}
					
					data_back = numTilesAway(back_getFilteredData());
					data_front = numTilesAway(fwd_getFilteredData());
					mvtType = movementType(best_ratio);
					
					currentPath.add(new PathNode(mvtType, data_front));
					updatePositions(true);

					currentPath.add(new PathNode("forward", data_back));
					updatePositionsBack();
					
					currentPath.add(new PathNode("forward",data_front));
					updatePositions(true);
					if(data_front != 0){
						driver.setSpeeds(Constants.FORWARD_SPEED, Constants.FORWARD_SPEED);
						driver.moveForward(30);
						applyCorrection();
					}
				} else if (data_front == -1) {
					int best_ratio = getBestRatio(true);
					LCD.drawInt(best_ratio, 0, 0);
					mvtType = movementType(best_ratio);
					if(best_ratio == 1){
						driver.setSpeeds(Constants.ROTATE_SPEED, Constants.ROTATE_SPEED);
						driver.rotateClockwise(90);
					} else if (best_ratio == 3){
						driver.setSpeeds(Constants.ROTATE_SPEED, Constants.ROTATE_SPEED);
						driver.rotateCounter(90);
					}
					data_back = numTilesAway(back_getFilteredData());
					data_front = numTilesAway(fwd_getFilteredData());
					
					if(best_ratio == 0){
						currentPath.add(new PathNode(mvtType,data_back));
						updatePositionsBack();
						currentPath.add(new PathNode(mvtType,data_front));
						updatePositions(true);
						driver.setSpeeds(Constants.FORWARD_SPEED, Constants.FORWARD_SPEED);
						driver.moveForward(30);
						applyCorrection();
					} else {
						
						currentPath.add(new PathNode(mvtType, data_front));
						updatePositions(true);

						currentPath.add(new PathNode("forward", data_back));
						updatePositionsBack();
						
						currentPath.add(new PathNode("forward",data_front));
						updatePositions(true);
						if(data_front != 0){
							driver.setSpeeds(Constants.FORWARD_SPEED, Constants.FORWARD_SPEED);
							driver.moveForward(30);
							applyCorrection();
						}
					}
				}
			}
			//checkFoundValue();
		}
		Sound.beep();
		LCD.drawString(String.valueOf((char)possibilities.get(0).getPoint()), 0, 1);
		LCD.drawString(String.valueOf(possibilities.get(0).getColumn()), 0, 2);
		LCD.drawString(String.valueOf(possibilities.get(0).getRow()), 0, 3);
		
		return new int [] {possibilities.get(0).getPoint(), possibilities.get(0).getColumn(),possibilities.get(0).getRow()};
	}
	
	private void checkFoundValue(){
		if(possibilities.size() == 1){
			int data_front = numTilesAway(fwd_getFilteredData());
			int data_back = numTilesAway(back_getFilteredData());
			if (data_front != 0) {
				currentPath.add(new PathNode("forward",data_front));
				updatePositions(true);
				driver.moveForward(30);
				
				data_front = numTilesAway(fwd_getFilteredData());
				currentPath.add(new PathNode("forward", data_back));
				updatePositionsBack();
				currentPath.add(new PathNode("forward", data_front));
				updatePositions(true);
				if (data_front != 0) {
					driver.setSpeeds(Constants.FORWARD_SPEED, Constants.FORWARD_SPEED);
					driver.moveForward(30);
				}
				driver.rotateClockwise(90);
				
				data_front = numTilesAway(fwd_getFilteredData());
				data_back = numTilesAway(back_getFilteredData());
				currentPath.add(new PathNode("turnLeft",data_front));
				updatePositions(true);
				currentPath.add(new PathNode("forward", data_back));
				updatePositionsBack();
			} else {
				driver.rotateClockwise(90);
				data_front = numTilesAway(fwd_getFilteredData());
				data_back = numTilesAway(back_getFilteredData());
				currentPath.add(new PathNode("turnLeft",data_front));
				updatePositions(true);
				currentPath.add(new PathNode("forward", data_back));
				updatePositionsBack();
				if (data_front != 0) {
					driver.moveForward(30);
					
					driver.rotateClockwise(90);
					data_front = numTilesAway(fwd_getFilteredData());
					data_back = numTilesAway(back_getFilteredData());
					currentPath.add(new PathNode("turnLeft",data_front));
					updatePositions(true);
					currentPath.add(new PathNode("forward", data_back));
					updatePositionsBack();
				} else {
					driver.rotateClockwise(90);
					data_front = numTilesAway(fwd_getFilteredData());
					data_back = numTilesAway(back_getFilteredData());
					currentPath.add(new PathNode("turnLeft",data_front));
					updatePositions(true);
					currentPath.add(new PathNode("forward", data_back));
					updatePositionsBack();
					
					driver.moveForward(30);
					
					driver.rotateClockwise(90);
					data_front = numTilesAway(fwd_getFilteredData());
					data_back = numTilesAway(back_getFilteredData());
					currentPath.add(new PathNode("turnLeft",data_front));
					updatePositions(true);
					currentPath.add(new PathNode("forward", data_back));
					updatePositionsBack();
					
				}
				
			}
		}
	}
	/**
	 * 
	 * @return The average of 5 readings. Removes values that are false negatives form the calculation.
	 */
	private static int fwd_getFilteredData() {
		
		int [] collected = new int[5];
		
		for(int i = 0; i < 5 ;i++){
			usSensorFront.ping();
			collected[i] = usSensorFront.getDistance(); // read 5 values
		}
		int [] indexes = new int[5];
		
		int j = 0;
		for(int i = 0; i < collected.length ;i++){
			if(collected[i] > 200){
				indexes[j] = i;
				j++;
			}
		}
		j = 0;
		int sum = 0;
		for(int i = 0; i < collected.length;i++){
			if(i==indexes[j]){
				j++;
			} else {
				sum += collected[i];
			}
		}
		
		if(sum == 0){
			LCD.drawString("Forward: " + -1, 0, 5);
			return 255;
		} else {
			int distance = (int) (sum/(collected.length - (j)));
			LCD.drawString("Forward: " + String.valueOf(numTilesAway(distance)), 0, 5);
			return distance;
		}
	}
	/**
	 * 
	 * @param data The reading from the Ultrasonic sensor
	 * @return Separates data into 0, 1, 2 tiles away. If it doens't see a wall, method will return -1.
	 */
	private static int numTilesAway(double data) {
		if (data < TILEAWAY_0) {
			return 0;
		} else if (data < TILEAWAY_1) {
			return 1;
		} else if (data < TILEAWAY_2) {
			return 2;
		} else {
			return -1;
		}
	}
	
	/**
	 * 
	 * @return The average of 5 readings. Removes values that are false negatives form the calculation.
	 */
	public static double back_getFilteredData() {
		
		int [] collected = new int[5];
		
		for(int i = 0; i < 5 ;i++){
			usSensorBack.ping();
			collected[i] = usSensorBack.getDistance(); // read 5 values
		}
		int [] indexes = new int[5];
		
		int j = 0;
		for(int i = 0; i < collected.length ;i++){
			if(collected[i] > 200){
				indexes[j] = i;
				j++;
			}
		}
		j = 0;
		int sum = 0;
		for(int i = 0; i < collected.length;i++){
			if(i==indexes[j]){
				j++;
			} else {
				sum += collected[i];
			}
		}
		
		if(sum == 0){
			LCD.drawString("Back: " + -1, 0, 6);
			return 255;
		} else {
			int distance = (int) (sum/(collected.length - (j)));
			LCD.drawString("Back: " + String.valueOf(numTilesAway(distance)), 0, 6);
			return distance;
		}
	}
	/**
	 * 
	 * @param canGoForward If the robot sees infinity, then the robot can still go forward
	 * @return A number that is either 0, 1 or 3. This method calculates if it is better to go forward left or right.
	 */
	
	public static int getBestRatio(boolean canGoForward){
		if (possibilities.size() > 1) {
			int pool = possibilities.size(); 
			int poss_fwd = 0;
			double ratio_fwd = 0.0;
			double ratio_left = 0.0;
			double ratio_right = 0.0;
			int poss_left = 0;
			int poss_right = 0;
			for (int i = 0; i < possibilities.size(); i++) {
				if (canGoForward) {
					poss_fwd += updateTemp(0,i);
				}
				poss_left += updateTemp(1,i);
				poss_right += updateTemp(3,i);
			}
			
			ratio_left = Math.abs(((double)poss_left / pool) - 0.5);
			ratio_right = Math.abs(((double)poss_right / pool) - 0.5);
			ratio_fwd = Math.abs(((double)poss_fwd / pool) - 0.5);
			
			if (canGoForward) {
				double min = Math.min(ratio_fwd, Math.min(ratio_left, ratio_right));
				if (min == ratio_left) {
					return 1;
				} else if (min == ratio_right) {
					return 3;
				} else if (min == ratio_fwd) {
					return 0;
				}
			} else {
				double min = Math.min(ratio_left, ratio_right);
				if (min == ratio_left)
					return 1;
				else
					return 3;
			}
		}
		return -1;
	}
	/**
	 * 
	 * @param rlf An integer representing right, left, or forward.
	 * @param index An index to a possibility.
	 * @return 1 if the possibility crashed for the given direction rlf at the index given. 0 if it didn't crash.
	 */
	public static int updateTemp(int rlf, int index) {
		int count = 0;
		Arrow next = possibilities.get(index).getNext();
		int row = possibilities.get(index).getRow();
		int rowForMap = getRowMap(row);
		int col = possibilities.get(index).getColumn();
		char orientation = next.getPoint();
		if (orientation == 'n') {
			if (rlf == 0) {
				if (rowForMap - 3 < 0)
					count++;
				else if (mapTiles[rowForMap - 3][col].getIsObstacle())
					count++;
			} else if (rlf == 1) {
				if (col - 1 < 0)
					count++;
				else if (mapTiles[rowForMap][col - 1].getIsObstacle())
					count++;
			} else if (rlf == 3) {
				if (col + 1 >= Constants.MAZE_SIZE)
					count++;
				else if (mapTiles[rowForMap][col + 1].getIsObstacle())
					count++;
			}
		} else if (orientation == 'w') {
			if (rlf == 0) {
				if (col - 3 < 0)
					count++;
				else if (mapTiles[rowForMap][col - 3].getIsObstacle())
					count++;
			} else if (rlf == 3) {
				if (rowForMap - 1 < 0)
					count++;
				else if (mapTiles[rowForMap - 1][col].getIsObstacle())
					count++;
			} else if (rlf == 1) {
				if (rowForMap + 1 >= Constants.MAZE_SIZE)
					count++;
				else if (mapTiles[rowForMap + 1][col].getIsObstacle())
					count++;
			}
		} else if (orientation == 's') {
			if (rlf == 0) {
				if (rowForMap + 3 >= Constants.MAZE_SIZE)
					count++;
				else if (mapTiles[rowForMap + 3][col].getIsObstacle())
					count++;
			} else if (rlf == 3) {
				if (col - 1 < 0)
					count++;
				else if (mapTiles[rowForMap][col - 1].getIsObstacle())
					count++;
			} else if (rlf == 1) {
				if (col + 1 >= Constants.MAZE_SIZE)
					count++;
				else if (mapTiles[rowForMap][col + 1].getIsObstacle())
					count++;
			}
		} else if (orientation == 'e') {
			if (rlf == 0) {
				if (col + 3 >= Constants.MAZE_SIZE)
					count++;
				else if (mapTiles[rowForMap][col + 3].getIsObstacle())
					count++;
			} else if (rlf == 3) {
				if (rowForMap + 1 >= Constants.MAZE_SIZE)
					count++;
				else if (mapTiles[rowForMap + 1][col].getIsObstacle())
					count++;
			} else if (rlf == 1) {
				if (rowForMap - 1 < 0)
					count++;
				else if (mapTiles[rowForMap - 1][col].getIsObstacle())
					count++;
			}
		}
		return count;
	}
	/**
	 * 
	 * @param pos The direction at which the possibility is pointing.
	 * @param index The index of the list of possibilities.
	 * @param fromFrontSensor The boolean that checks if it is called from the front sensor or back.
	 * @return The index to remove if it crashed for the given data. -1 if it still a valid possibility.
	 */
	public static int getPositionToRemove(Arrow pos, int index,
			boolean fromFrontSensor) {

		PathNode mvt = currentPath.get(currentPath.size() - 1);
		Arrow next = possibilities.get(index).getNext();
		int row = next.getRow();
		int col = next.getColumn();
		int rowForMap = getRowMap(row);

		switch (getMovement(mvt.getMvt(), next.getPoint())) {
		case 0:
			if (mvt.getNodeTilesAway() == 0) {
				if (!mapTiles[rowForMap][col].isNorth())
					return index;
			} else if (mvt.getNodeTilesAway() == 1) {
				if (mapTiles[rowForMap][col].isNorth())
					return index;
				if (rowForMap - 1 > -1) {
					if (!mapTiles[rowForMap - 1][col].isNorth())
						return index;
				} else
					return index;
			} else if (mvt.getNodeTilesAway() == 2) {
				if (mapTiles[rowForMap][col].isNorth())
					return index;
				if (rowForMap - 1 > -1) {
					if (mapTiles[rowForMap - 1][col].isNorth())
						return index;
				} else
					return index;
				if (rowForMap - 2 > -1) {
					if (!mapTiles[rowForMap - 2][col].isNorth())
						return index;
				} else
					return index;
			} else {
				if (mapTiles[rowForMap][col].isNorth())
					return index;
				if (rowForMap - 1 > -1) {
					if (mapTiles[rowForMap - 1][col].isNorth())
						return index;
				} else
					return index;
				if (rowForMap - 2 > -1) {
					if (mapTiles[rowForMap - 2][col].isNorth())
						return index;
				} else
					return index;
			}
			break;
		case 1:
			if (mvt.getNodeTilesAway() == 0) {
				if (!mapTiles[rowForMap][col].isWest())
					return index;
			} else if (mvt.getNodeTilesAway() == 1) {
				if (mapTiles[rowForMap][col].isWest())
					return index;
				if (col - 1 > -1) {
					if (!mapTiles[rowForMap][col - 1].isWest())
						return index;
				} else
					return index;
			} else if (mvt.getNodeTilesAway() == 2) {
				if (mapTiles[rowForMap][col].isWest())
					return index;
				if (col - 1 > -1) {
					if (mapTiles[rowForMap][col - 1].isWest())
						return index;
				} else
					return index;
				if (col - 2 > -1) {
					if (!mapTiles[rowForMap][col - 2].isWest()) {
						return index;
					}
				} else
					return index;
			} else {
				if (mapTiles[rowForMap][col].isWest())
					return index;
				if (col - 1 > -1) {
					if (mapTiles[rowForMap][col - 1].isWest())
						return index;
				} else
					return index;
				if (col - 2 > -1) {
					if (mapTiles[rowForMap][col - 2].isWest())
						return index;
				} else
					return index;
			}
			break;
		case 2:
			if (mvt.getNodeTilesAway() == 0) {
				if (!mapTiles[rowForMap][col].isSouth())
					return index;
			} else if (mvt.getNodeTilesAway() == 1) {
				if (mapTiles[rowForMap][col].isSouth())
					return index;
				if (rowForMap + 1 < Constants.MAZE_SIZE) {
					if (!mapTiles[rowForMap + 1][col].isSouth())
						return index;
				} else
					return index;
			} else if (mvt.getNodeTilesAway() == 2) {
				if (mapTiles[rowForMap][col].isSouth()) {
					return index;
				}
				if (rowForMap + 1 < Constants.MAZE_SIZE) {
					if (mapTiles[rowForMap + 1][col].isSouth()) {
						return index;
					}
				} else {
					return index;
				}
				if (rowForMap + 2 < Constants.MAZE_SIZE) {
					if (!mapTiles[rowForMap + 2][col].isSouth()) {
						return index;
					}

				} else {
					return index;
				}
			} else {
				if (mapTiles[rowForMap][col].isSouth())
					return index;
				if (rowForMap + 1 < Constants.MAZE_SIZE) {
					if (mapTiles[rowForMap + 1][col].isSouth())
						return index;
				} else
					return index;
				if (rowForMap + 2 < Constants.MAZE_SIZE) {
					if (mapTiles[rowForMap + 2][col].isSouth())
						return index;
				} else
					return index;
			}
			break;
		case 3:
			if (mvt.getNodeTilesAway() == 0) {
				if (!mapTiles[rowForMap][col].isEast()) {
					return index;
				}
			} else if (mvt.getNodeTilesAway() == 1) {
				if (mapTiles[rowForMap][col].isEast()) {
					return index;
				}
				if (col + 1 < Constants.MAZE_SIZE) {
					if (!mapTiles[rowForMap][col + 1].isEast()) {
						return index;
					}
				} else
					return index;
			} else if (mvt.getNodeTilesAway() == 2) {
				if (mapTiles[rowForMap][col].isEast()) {
					return index;
				}
				if (col + 1 < Constants.MAZE_SIZE) {
					if (mapTiles[rowForMap][col + 1].isEast()) {
						return index;
					}
				} else {
					return index;
				}
				if (col + 2 < Constants.MAZE_SIZE) {
					if (!mapTiles[rowForMap][col + 2].isEast()) {
						return index;
					}
				} else {
					return index;
				}
			} else {
				if (mapTiles[rowForMap][col].isEast())
					return index;
				if (col + 1 < Constants.MAZE_SIZE) {
					if (mapTiles[rowForMap][col + 1].isEast())
						return index;
				} else
					return index;
				if (col + 2 < Constants.MAZE_SIZE) {
					if (mapTiles[rowForMap][col + 2].isEast())
						return index;
				} else
					return index;
			}
			break;
		default:
			return -2;
		}
		if (mvt.getMvt().equals("forward") && !(mvt.getNodeTilesAway() == 0)) {

			char orientation = next.getPoint();
			if (fromFrontSensor) {
				if (orientation == 'n') {
					possibilities.get(index).setRow(row + 1);
				} else if (orientation == 'w') {
					possibilities.get(index).setColumn(col - 1);
				} else if (orientation == 's') {
					possibilities.get(index).setRow(row - 1);
				} else if (orientation == 'e') {
					possibilities.get(index).setColumn(col + 1);
				}
			}
			possibilities.get(index).setPoint(next.getPoint());
		} else {
			
			possibilities.get(index).setPoint(translate(getMovement(mvt.getMvt(), next.getPoint())));
		}
		return -1;

	}
	/**
	 * treats the cases for the back sensor
	 */
	public static void updatePositionsBack(){
		for(int i = 0; i < possibilities.size();i++){
			char c = possibilities.get(i).getPoint();
			possibilities.get(i).setPoint(inverseDirection(c));
			
		}
		updatePositions(false);
		currentPath.remove(currentPath.size() - 1);
		for(int i = 0; i < possibilities.size(); i++){
			possibilities.get(i).setPoint(inverseDirection(possibilities.get(i).getPoint()));
		}
	}	
	/**
	 * This method removes all the possibilities if the method getPositionToRemove tells it to do so.
	 * @param fromFrontSensor Boolean taking care of the cases where it comes from the front sensor and when it doesn't.
	 */
	public static void updatePositions(boolean fromFrontSensor) {
		
		for (int i = 0; i < possibilities.size(); i++) {
			cur = possibilities.get(i);
			ind = getPositionToRemove(cur, i, fromFrontSensor);
			
			if (ind != -1) {
				indexes.add(ind);
			}
		}
		int error = 0;
		
		for(int j = 0; j < indexes.size(); j++){
			possibilities.remove(indexes.get(j) - error);
			error++;
		}
		indexes.clear();
	}
	
	public static char inverseDirection(char c){
		if(c == 'n') return 's';
		if(c == 'w') return 'e';
		if(c == 's') return 'n';
		if(c == 'e') return 'w';
		return 'o';
	}
	public static char translate(int index){
		if(index == 0) return 'n';
		if(index == 1) return 'w';
		if(index == 2) return 's';
		if(index == 3) return 'e';
		return 'o';
	}
	
	public static int getMovement(String nodeInput, char point){
		if(nodeInput.equals("turnRight")){
			if(point == 'n') return 3;
			if(point == 'w') return 0;
			if(point == 's') return 1;
			if(point == 'e') return 2;
			
		} else if(nodeInput.equals("turnLeft")){
			if(point == 'n') return 1;
			if(point == 'w') return 2;
			if(point == 's') return 3;
			if(point == 'e') return 0;
		} else if(nodeInput.equals("forward")){
			if(point == 'n') return 0;
			if(point == 'w') return 1;
			if(point == 's') return 2;
			if(point == 'e') return 3;
		}
		return -1;
	}
}