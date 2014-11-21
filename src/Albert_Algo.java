import java.util.LinkedList;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;

public class Albert_Algo {
	private SquareDriver driver;
	private static Tiles[][] mapTiles;
	private UltrasonicSensor usSensorFront;
	private UltrasonicSensor usSensorBack;
	private static final double TILEAWAY_0 = 16.0;
	private static final double TILEAWAY_1 = TILEAWAY_0 + 30.0;
	private static final double TILEAWAY_2 = TILEAWAY_1 + 30.0;
	private static LinkedList<Arrow> possibilities = new LinkedList<Arrow>();
	private static LinkedList<PathNode> currentPath;
	static LinkedList<Character> mem = new LinkedList<Character>();
	// north = 0
	// west = 1
	// south = 2
	// east = 3

	public static int getRowMap(int row){
		return Constants.MAZE_SIZE - 1 - row;
	}
	
	public Albert_Algo(Map map, SquareDriver driver, UltrasonicSensor usSensorFront, UltrasonicSensor usSensorBack) {
		this.usSensorFront = usSensorFront;
		this.usSensorBack = usSensorBack;
		this.driver = driver;
		mapTiles = map.getMap();

		currentPath = new LinkedList<PathNode>();
		for (int i = 0; i < mapTiles.length; i++) {
			for (int j = 0; j < mapTiles[i].length; j++) {
				// System.out.print(counter + " ");
				for (int k = 0; k < 4; k++) {
					if (!mapTiles[i][j].getIsObstacle()){
						possibilities.add(new Arrow(getRowMap(i), j, mapTiles[i][j]
								.getPositionsArrows(k).getPoint()));
						//possibilities.add(map[i][j].getPositionsArrows(k));
					}
				}
			}
		}
		
		for (int i = 0; i < possibilities.size(); i++){
			possibilities.get(i).getNext().setPoint(possibilities.get(i).getPoint());
			possibilities.get(i).getNext().setRow(possibilities.get(i).getRow());
			possibilities.get(i).getNext().setColumn(possibilities.get(i).getColumn());
			
		}
		usSensorFront.off();
		usSensorBack.off();
	}

	public boolean seesObject(int wallsAway) {
		if (wallsAway < 0)
			return false;
		return true;
	}
	
	public void test(){
		int data_front = numTilesAway(fwd_getFilteredData());
		int data_back = numTilesAway(back_getFilteredData());
		LCD.clear();
		LCD.drawString(String.valueOf(data_back), 0, 1);
		LCD.drawString(String.valueOf(data_front), 0, 2);
	}
	
	public int algorithm() {
		String mvtType = null;
		int data_front = 0;
		int data_back = 0;
		driver.moveForward(30);
		while (possibilities.size() > 1) {
			
			data_front = numTilesAway(fwd_getFilteredData());
			LCD.clear();
			LCD.drawString(String.valueOf(data_front), 0, 2);
			currentPath.add(new PathNode("forward", true, data_front));
			updatePositions(true);
			data_back = numTilesAway(back_getFilteredData());
			currentPath.add(new PathNode("forward", true, data_back));
			updatePositionsBack(0);
			LCD.drawString(String.valueOf(data_back), 0, 1);
			
			if (data_front == 0) {
				int bestRatio = getBestRatio(false);
				if (bestRatio != 1 && bestRatio != 3){
					LCD.clear();
					LCD.drawString("Hello 1", 0, 1);
				}
				else {
					data_back = numTilesAway(back_getFilteredData());
					LCD.drawString(String.valueOf(data_back), 0, 1);
					data_front = numTilesAway(fwd_getFilteredData());
					if (bestRatio == 1) {
						driver.rotateCounter(90);
						mvtType = "turnLeft";

					} else if (bestRatio == 3) {
						driver.rotateClockwise(90);
						mvtType = "turnRight";
					}
					currentPath.add(new PathNode(mvtType, seesObject(data_front), data_front));
					updatePositions(true);
					
					if (mvtType.equals("turnLeft")){
						mvtType = "turnRight";
						currentPath.add(new PathNode(mvtType,seesObject(data_back), data_back));
						updatePositionsBack(3);
					}
					else{
						mvtType = "turnLeft";
						currentPath.add(new PathNode(mvtType,seesObject(data_back), data_back));
						updatePositionsBack(1);
					}
				}
			} else if (data_front == 1 || data_front == 2) {
				int bestRatio = getBestRatio(true);
				if (bestRatio != 0 && bestRatio != 1 && bestRatio != 3){
					LCD.clear();
					LCD.drawString("Hello 2", 0, 1);
				}
				else {
					if (bestRatio == 1 || bestRatio == 3) {
						data_back = numTilesAway(back_getFilteredData());
						LCD.drawString(String.valueOf(data_back), 0, 1);
						
						if (bestRatio == 1) {
							driver.rotateCounter(90);
							mvtType = "turnLeft";

						} else if (bestRatio == 3) {
							driver.rotateClockwise(90);
							mvtType = "turnRight";
						}
						currentPath.add(new PathNode(mvtType, seesObject(data_front), data_front));
						updatePositions(true);
						
						if (mvtType.equals("turnLeft")){
							mvtType = "turnRight";
							currentPath.add(new PathNode(mvtType,seesObject(data_back), data_back));
							updatePositionsBack(3);
						}
						else{
							mvtType = "turnLeft";
							currentPath.add(new PathNode(mvtType,seesObject(data_back), data_back));
							updatePositionsBack(1);
						}
						
					} else {
						driver.moveForward(30);
						mvtType = "forward";
						currentPath.add(new PathNode(mvtType, seesObject(data_front), data_front));
						updatePositions(true);
					}
				}
			} else { // move_fwd sees infinity i.e. -1
				int bestRatio = getBestRatio(true);
				if (bestRatio != 0 && bestRatio != 1 && bestRatio != 3){
					LCD.clear();
					LCD.drawString("Hello 3", 0, 1);
				}
				else {
					if (bestRatio == 1 || bestRatio == 3) {
						data_back = numTilesAway(back_getFilteredData());
						LCD.drawString(String.valueOf(data_back), 0, 1);
						
						if (bestRatio == 1) {
							driver.rotateCounter(90);
							mvtType = "turnLeft";

						} else if (bestRatio == 3) {
							driver.rotateClockwise(90);
							mvtType = "turnRight";
						}
						currentPath.add(new PathNode(mvtType,seesObject(data_front), data_front));
						updatePositions(true);
						
						if (mvtType.equals("turnLeft")){
							mvtType = "turnRight";
							currentPath.add(new PathNode(mvtType,seesObject(data_back), data_back));
							updatePositionsBack(3);
						}
						else{
							mvtType = "turnLeft";
							currentPath.add(new PathNode(mvtType,seesObject(data_back), data_back));
							updatePositionsBack(1);
						}
					} else {
						driver.moveForward(30);
						mvtType = "forward";
						currentPath.add(new PathNode(mvtType,
								seesObject(data_front), data_front));
						updatePositions(true);
					}
				}
			}
		}
		if(possibilities.size() == 1){
			Sound.beep();
			LCD.clear();
			LCD.drawString(String.valueOf(possibilities.get(0).getColumn()) + " " 
			+ String.valueOf(possibilities.get(0).getRow()), 0, 1);
		}
		else {LCD.clear();
		LCD.drawString("fail",0,2);}
		driver.stop();
		return 0;
		
	}

	public double fwd_getFilteredData() {
		usSensorFront.ping();
		double fwd_distance = usSensorFront.getDistance();
		return fwd_distance;
	}

	private int numTilesAway(double data) {
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

	public double back_getFilteredData() {
		usSensorBack.ping();
		double back_distance = usSensorBack.getDistance();
		return back_distance;
	}

	public static double calculateRelativeTurn() {
		return 0.0;
	}
	
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
			
			//System.out.print(ratio_left + " ");
			//System.out.print(ratio_right + " ");
			//System.out.print(ratio_fwd + " ");
			
			
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
	
	public static int updateTemp(int rlf, int index){
		int count = 0;
		Arrow next = possibilities.get(index).getNext();
		int row = possibilities.get(index).getRow();
		int rowForMap = getRowMap(row);
		int col = possibilities.get(index).getColumn();
		char orientation = next.getPoint();
		if (orientation == 'n') {
			if (rlf == 0) {
				if (rowForMap - 1 < 0) count ++;
				else if (mapTiles[rowForMap-1][col].getIsObstacle()) count ++;
			} else if (rlf == 1){
				if(col - 1 < 0 )count++;
				else if(mapTiles[rowForMap][col-1].getIsObstacle()) count ++;
			} else if (rlf == 3) {
				if(col + 1 >= Constants.MAZE_SIZE) count++;
				else if(mapTiles[rowForMap][col+1].getIsObstacle()) count ++;
			}
		} else if (orientation == 'w') {
			if (rlf == 0) {
				if(col - 1 < 0)	count++;
				else if(mapTiles[rowForMap][col-1].getIsObstacle()) count ++;
			} else if (rlf == 3) {
				if(rowForMap - 1 < 0) count++;
				else if(mapTiles[rowForMap-1][col].getIsObstacle()) count ++;
			} else if (rlf == 1){
				if(rowForMap + 1 >= Constants.MAZE_SIZE) count++;
				else if(mapTiles[rowForMap+1][col].getIsObstacle()) count ++;
			}
		} else if (orientation == 's') {
			if (rlf == 0){
				if(rowForMap + 1 >= Constants.MAZE_SIZE) count++;
				else if(mapTiles[rowForMap+1][col].getIsObstacle()) count ++;
			} else if (rlf == 3) {
				if(col - 1 < 0) count++;
				else if(mapTiles[rowForMap][col-1].getIsObstacle()) count ++;
			} else if (rlf == 1) {
				if(col + 1 >= Constants.MAZE_SIZE) count++;
				else if(mapTiles[rowForMap][col+1].getIsObstacle()) count ++;
			}
		} else if (orientation == 'e') {
			if (rlf == 0) {
				if(col + 1 >= Constants.MAZE_SIZE) count++;
				else if(mapTiles[rowForMap][col+1].getIsObstacle()) count ++;
			} else if (rlf == 3) {
				if(rowForMap + 1 >= Constants.MAZE_SIZE) count ++;
				else if(mapTiles[rowForMap+1][col].getIsObstacle()) count++;
			} else if (rlf == 1) {
				if(rowForMap - 1 < 0) count++;
				else if(mapTiles[rowForMap-1][col].getIsObstacle()) count ++;
			}
		}
		return count;
	}
	public static int getPositionToRemove(Arrow pos, int index, boolean fromFrontSensor){
		
		PathNode mvt = currentPath.get(currentPath.size() - 1);
		Arrow next = possibilities.get(index).getNext();
		int row = next.getRow();
		int col = next.getColumn();
		int rowForMap = getRowMap(row);
		
		switch(getMovement(mvt.getMvt(), next.getPoint())){
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
				if (col - 1 > - 1) {
					if (mapTiles[rowForMap][col - 1].isWest())
						return index;
				} else
					return index;
				if (col - 2 > -1) {
					if (!mapTiles[rowForMap][col - 2].isWest()){
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
				if (mapTiles[rowForMap][col].isSouth()){
					return index;
				}
				if (rowForMap + 1 < Constants.MAZE_SIZE) {
					if (mapTiles[rowForMap + 1][col].isSouth()){
						return index;
					}
				} else{
					return index;
				}
				if (rowForMap + 2 < Constants.MAZE_SIZE) {
					if (!mapTiles[rowForMap + 2][col].isSouth()){
						return index;
					}
					
				} else {
					return index;
				}
			} else {
				if (mapTiles[rowForMap][col].isNorth())
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
				if (!mapTiles[rowForMap][col].isEast()){
					return index;
				}
			} else if (mvt.getNodeTilesAway() == 1) {
				if (mapTiles[rowForMap][col].isEast()){
					return index;
				}
				if (col + 1 < Constants.MAZE_SIZE) {
					if (!mapTiles[rowForMap][col + 1].isEast()){
						return index;
					}
				} else
					return index;
			} else if (mvt.getNodeTilesAway() == 2) {
				if (mapTiles[rowForMap][col].isEast()){
					return index;
				}
				if (col + 1 < Constants.MAZE_SIZE) {
					if (mapTiles[rowForMap][col + 1].isEast()){
						return index;
					}
				} else {
					return index;
				}
				if (col + 2 < Constants.MAZE_SIZE) {
					if (!mapTiles[rowForMap][col + 2].isEast()){
						return index;
					}
				} else{
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
		default: return -2;
		}
		if(mvt.getMvt().equals("forward") && !(mvt.getNodeTilesAway() == 0)){
			
			char orientation = next.getPoint();
			if(fromFrontSensor){
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
			possibilities.get(index).setPoint(translate(getMovement(mvt.getMvt(),next.getPoint())));
		}
		return -1;
		
	}

	public static void updatePositions(boolean fromFrontSensor) {
		Arrow cur;
		LinkedList<Integer> itemsToRemove = new LinkedList<Integer>();
		int ind;
		for (int i = 0; i < possibilities.size(); i++) {
			cur = possibilities.get(i);
			ind = getPositionToRemove(cur, i,fromFrontSensor);
			if (ind != -1) {
				itemsToRemove.add(ind);
			}
		}
		int error = 0;
		for (Integer j : itemsToRemove) { 
			if(!fromFrontSensor) {
				possibilities.get(j-error).setPoint((mem.get(j-error)));
				possibilities.get(j-error).getNext().setPoint((mem.get(j-error)));
			}
			possibilities.remove(j - error);
			error++;
		}
		mem.clear();
	}
	
	// This method is called with the type of turn the back sensor should do
	public static void updatePositionsBack(int rlf){
		mem = new LinkedList<Character>();
		for(int i = 0; i < possibilities.size();i++){
			char c = possibilities.get(i).getPoint();
			mem.add(c);
			if(rlf == 1){
				possibilities.get(i).setPoint(translate(getMovement("turnLeft",c)));
			} else if(rlf == 3){
				possibilities.get(i).setPoint(translate(getMovement("turnRight",c)));
			} else if(rlf == 0){
				possibilities.get(i).setPoint(inverseDirection(c));
			} else {
				//System.out.println("Problem in updating positions in the back");
			}
			
		}
		updatePositions(false);
		currentPath.remove(currentPath.size() - 1);
		for(int i = 0; i < possibilities.size(); i++){
			//System.out.println("Hello " + possibilities.get(i).getNext().getPoint() + " " + possibilities.get(i).getPoint());
			possibilities.get(i).setPoint(inverseDirection(possibilities.get(i).getPoint()));
			//System.out.println("Bye " + possibilities.get(i).getNext().getPoint() + " " + possibilities.get(i).getPoint());
			
		}
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