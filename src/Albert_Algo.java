import java.util.LinkedList;

import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;


public class Albert_Algo {
	private static Tiles[][] map;
	private static UltrasonicSensor fwd_sensor;
	private static UltrasonicSensor back_sensor;
	private static final double TILEAWAY_0 = 20.0;
	private static final double TILEAWAY_1 = TILEAWAY_0 + 30.0;
	private static final double TILEAWAY_2 = TILEAWAY_1 + 40.0;
	private static LinkedList<Arrow> possibilities = new LinkedList<Arrow>();
	private static LinkedList<PathNode> currentPath;
	private static int counter = 1;
	private static LinkedList<Arrow> memRatio = new LinkedList<Arrow>();
	private static final int MAZE_SIZE = 4;

	// north = 0
	// west = 1
	// south = 2
	// east = 3

	public static void main(String[] args) {
		Map maps = new Map(new int[] { 1, 7, 8, 14 });
		map = maps.getMap();
		fwd_sensor = new UltrasonicSensor(SensorPort.S1);
		back_sensor = new UltrasonicSensor(SensorPort.S2);
		fwd_sensor.off();
		back_sensor.off();

		currentPath = new LinkedList<PathNode>();
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				// System.out.print(counter + " ");
				for (int k = 0; k < 4; k++) {
					if (!map[i][j].getIsObstacle())
						possibilities.add(new Arrow(setRowTile(i), j, map[i][j]
								.getPositionsArrows(k).getPoint()));
				}
				System.out.println("(" + map[i][j].isNorth() + ", " + map[i][j].isWest() + ", " +
						map[i][j].isSouth() + ", " + map[i][j].isEast() + ")");
				
			}
		}
		currentPath.add(new PathNode("turnLeft", true, 2));

		for (int i = 0; i < possibilities.size(); i++) {
			System.out.print(possibilities.get(i).getPoint() + " ");
			System.out.print("(" + possibilities.get(i).getColumn() + ", "
					+ possibilities.get(i).getRow() + ")  ");
			// System.out.print("(" + possibilities.get(i).getNext().getColumn()
			// + ", " + possibilities.get(i).getNext().getRow() + ")");
			System.out.println("    " +getPositionToRemove(possibilities.get(i), i)
					+ " ");
		}
	}

	public static int setRowTile(int row) {
		return map.length - 1 - row;
	}

	public Albert_Algo(int[] blocks_tilenumber) {

		Map maps = new Map(blocks_tilenumber);
		map = maps.getMap();
		fwd_sensor = new UltrasonicSensor(1);
		back_sensor = new UltrasonicSensor(2);
		fwd_sensor.off();
		back_sensor.off();

		currentPath = new LinkedList<PathNode>();
		counter = 1;
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				for (int k = 0; k < 4; k++) {
					if (!map[i][j].getObstacles()[k]) {
						Arrow now = map[i][j].getPositionsArrows(k);
						now.setColumn(i);
						now.setRow(j);
						possibilities.add(now);
					}
				}
			}
		}
	}

	public boolean seesObject(int wallsAway) {
		if (wallsAway < 0)
			return false;
		return true;
	}

	public int algorithm() {
		String mvtType = null;
		// TODO move forward
		int data_front = 0;
		int data_back = 0;
		while (possibilities.size() > 1) {
			data_front = numTilesAway(fwd_getFilteredData());
			// TODO consider a variable move_back for the case where the robot
			// starts and needs to see back
			if (data_front == 0) {
				int bestRatio = calculateBestRatio(false);
				if (bestRatio != 1 || bestRatio != 3)
					System.out.println("error");
				else {
					// TODO turn to best ratio
					data_back = numTilesAway(back_getFilteredData());

					if (bestRatio == 1) {
						mvtType = "turnLeft";

					} else if (bestRatio == 3) {
						mvtType = "turnRight";
					}
					currentPath.add(new PathNode(mvtType,
							seesObject(data_front), data_front));

					if (mvtType.equals("turnLeft"))
						mvtType = "turnRight";
					else
						mvtType = "turnLeft";
					currentPath.add(new PathNode(mvtType,
							seesObject(data_back), data_back));
					// TODO some method that processes the 1 path node that has
					// been added and that removes
					// it afterwards as it is not part of the robot path.
				}
				// the robot is currently turned to the new position... no need
				// to change anything
			} else if (data_front == 1 || data_front == 2) {
				int bestRatio = calculateBestRatio(true);
				if (bestRatio != 0 || bestRatio != 1 || bestRatio != 3)
					System.out.println("Error");
				else {
					if (bestRatio == 1 || bestRatio == 3) {
						// TODO turn to best ratio
						data_back = numTilesAway(back_getFilteredData());

						if (bestRatio == 1) {
							mvtType = "turnLeft";

						} else if (bestRatio == 3) {
							mvtType = "turnRight";
						}
						currentPath.add(new PathNode(mvtType,
								seesObject(data_front), data_front));
						// TODO some method that processes objects far_away
						if (mvtType.equals("turnLeft"))
							mvtType = "turnRight";
						else
							mvtType = "turnLeft";
						currentPath.add(new PathNode(mvtType,
								seesObject(data_back), data_back));
						// TODO some method that processes the 1 path node that
						// has been added and that removes
						// it afterwards as it is not part of the robot path.
					} else {
						// TODO go forward
						mvtType = "forward";
						currentPath.add(new PathNode(mvtType,
								seesObject(data_front), data_front));
						// TODO some method that processes objects far_away
					}
				}
			} else { // move_fwd sees infinity i.e. -1
				int bestRatio = calculateBestRatio(true);
				if (bestRatio != 0 || bestRatio != 1 || bestRatio != 3)
					System.out.println("Error");
				else {
					if (bestRatio == 1 || bestRatio == 3) {
						// TODO turn to best ratio
						data_back = numTilesAway(back_getFilteredData());

						if (bestRatio == 1) {
							mvtType = "turnLeft";

						} else if (bestRatio == 3) {
							mvtType = "turnRight";
						}
						currentPath.add(new PathNode(mvtType,
								seesObject(data_front), data_front));
						// TODO some method that processes objects far_away
						if (mvtType.equals("turnLeft"))
							mvtType = "turnRight";
						else
							mvtType = "turnLeft";
						currentPath.add(new PathNode(mvtType,
								seesObject(data_back), data_back));
						// TODO some method that processes the 1 path node that
						// has been added and that removes
						// it afterwards as it is not part of the robot path.
					} else {
						// TODO go forward
						mvtType = "forward";
						currentPath.add(new PathNode(mvtType,
								seesObject(data_front), data_front));
						// TODO some method that processes objects far_away
					}
				}
			}
			updatePositions();
			counter++;
		}
		return 0;
	}

	public static double fwd_getFilteredData() {
		fwd_sensor.ping();
		double fwd_distance = fwd_sensor.getDistance();
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

	public static double back_getFilteredData() {
		back_sensor.ping();
		double back_distance = back_sensor.getDistance();
		return back_distance;
	}

	public static double calculateRelativeTurn() {
		return 0.0;
	}

	public int calculateBestRatio(boolean canGoForward) {
		if (possibilities.size() > 1) {
			int pool = possibilities.size();
			String mvt_right = "turnRight";
			String mvt_left = "turnLeft";
			double ratio_fwd = 0.0;
			if (canGoForward) {
				String mvt_fwd = "forward";
				PathNode temp_fwd = new PathNode(mvt_fwd, true, 1);
				currentPath.add(temp_fwd);
				updatePositions();
				int poss_fwd = possibilities.size();
				for (int i = memRatio.size() - 1; i >= 0; i--) {
					possibilities.add(memRatio.get(i));
					memRatio.clear();
				}
				currentPath.remove(currentPath.size() - 1);
				ratio_fwd = Math.abs((poss_fwd / pool) - 0.5);

			}

			PathNode temp_right = new PathNode(mvt_right, true, 1);
			PathNode temp_left = new PathNode(mvt_left, true, 1);

			currentPath.add(temp_right);
			updatePositions();
			int poss_right = possibilities.size();
			for (int i = memRatio.size() - 1; i >= 0; i--) {
				possibilities.add(memRatio.get(i));
				memRatio.clear();
			}
			currentPath.remove(currentPath.size() - 1);

			currentPath.add(temp_left);
			updatePositions();
			int poss_left = possibilities.size();
			for (int i = memRatio.size() - 1; i >= 0; i--) {
				possibilities.add(memRatio.get(i));
				memRatio.clear();
			}
			currentPath.remove(currentPath.size() - 1);

			double ratio_left = Math.abs((poss_left / pool) - 0.5);
			double ratio_right = Math.abs((poss_right / pool) - 0.5);

			if (canGoForward) {
				double min = Math.min(ratio_fwd,
						Math.min(ratio_left, ratio_right));
				if (min == ratio_fwd) {
					return 0;
				} else if (min == ratio_left) {
					return 1;
				} else if (min == ratio_right) {
					return 3;
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
	public static int getRowMap(int row){
		return MAZE_SIZE - 1 - row;
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
	public static int getPositionToRemove(Arrow pos, int index){
		
		PathNode mvt = currentPath.get(currentPath.size() - 1);
		Arrow next = pos.getNext();
		int row = next.getRow();
		int col = next.getColumn();
		
		switch(getMovement(mvt.getMvt(), next.getPoint())){
		case 0:
			if (mvt.getNodeTilesAway() == 0) {
				if (!map[getRowMap(row)][col].isNorth())
					return index;
			} else if (mvt.getNodeTilesAway() == 1) {
				if (map[getRowMap(row)][col].isNorth())
					return index;
				if (row + 1 < MAZE_SIZE) {
					if (!map[row + 1][col].isNorth())
						return index;
				} else
					return index;
			} else if (mvt.getNodeTilesAway() == 2) {
				if (map[getRowMap(row)][col].isNorth())
					return index;
				if (row + 1 < MAZE_SIZE) {
					if (map[row + 1][col].isNorth())
						return index;
				} else
					return index;
				if (row + 2 < MAZE_SIZE) {
					if (!map[row + 2][col].isNorth())
						return index;
				} else
					return index;
			} else {
				if (map[getRowMap(row)][col].isNorth())
					return index;
				if (row + 1 < MAZE_SIZE) {
					if (map[row + 1][col].isNorth())
						return index;
				} else
					return index;
				if (row + 2 < MAZE_SIZE) {
					if (map[row + 2][col].isNorth())
						return index;
				} else
					return index;
			}
			pos.setNext(map[getRowMap(row)][col].getPositionsArrows(0));
			break;
		case 1:
			if (mvt.getNodeTilesAway() == 0) {
				if (!map[getRowMap(row)][col].isWest())
					return index;
			} else if (mvt.getNodeTilesAway() == 1) {
				if (map[getRowMap(row)][col].isWest())
					return index;
				if (col - 1 > -1) {
					if (!map[row][col - 1].isWest())
						return index;
				} else
					return index;
			} else if (mvt.getNodeTilesAway() == 2) {
				if (map[getRowMap(row)][col].isWest())
					return index;
				if (col - 1 > -1) {
					if (map[row][col - 1].isWest())
						return index;
				} else
					return index;
				if (col - 2 > -1) {
					if (!map[row][col - 2].isWest())
						return index;
				} else
					return index;
			} else {
				if (map[getRowMap(row)][col].isWest())
					return index;
				if (col - 1 > -1) {
					if (map[row][col - 1].isWest())
						return index;
				} else
					return index;
				if (col - 2 > -1) {
					if (map[row][col - 2].isWest())
						return index;
				} else
					return index;
			}
			pos.setNext(map[getRowMap(row)][col].getPositionsArrows(1));
			break;
		case 2:
			if (mvt.getNodeTilesAway() == 0) {
				if (!map[getRowMap(row)][col].isSouth())
					return index;
			} else if (mvt.getNodeTilesAway() == 1) {
				if (map[getRowMap(row)][col].isSouth())
					return index;
				if (row - 1 > -1) {
					if (!map[row - 1][col].isSouth())
						return index;
				} else
					return index;
			} else if (mvt.getNodeTilesAway() == 2) {
				if (map[getRowMap(row)][col].isSouth())
					return index;
				if (row - 1 > -1) {
					if (map[row - 1][col].isSouth())
						return index;
				} else
					return index;
				if (row - 2 > -1) {
					if (!map[row - 2][col].isSouth())
						return index;
				} else
					return index;
			} else {
				if (map[getRowMap(row)][col].isNorth())
					return index;
				if (row - 1 > -1) {
					if (map[row - 1][col].isSouth())
						return index;
				} else
					return index;
				if (row - 2 > -1) {
					if (map[row - 2][col].isSouth())
						return index;
				} else
					return index;
			}
			pos.setNext(map[getRowMap(row)][col].getPositionsArrows(2));
			break;
		case 3:
			if (mvt.getNodeTilesAway() == 0) {
				if (!map[getRowMap(row)][col].isEast())
					return index;
			} else if (mvt.getNodeTilesAway() == 1) {
				if (map[getRowMap(row)][col].isEast())
					return index;
				if (col + 1 < MAZE_SIZE) {
					if (!map[row][col + 1].isEast())
						return index;
				} else
					return index;
			} else if (mvt.getNodeTilesAway() == 2) {
				if (map[getRowMap(row)][col].isEast())
					return index;
				if (col + 1 < MAZE_SIZE) {
					if (map[row][col + 1].isEast())
						return index;
				} else
					return index;
				if (col + 2 < MAZE_SIZE) {
					if (!map[row][col + 2].isEast())
						return index;
				} else
					return index;
			} else {
				if (map[getRowMap(row)][col].isEast())
					return index;
				if (col + 1 < MAZE_SIZE) {
					if (map[row][col + 1].isEast())
						return index;
				} else
					return index;
				if (col + 2 < MAZE_SIZE) {
					if (map[row][col + 2].isEast())
						return index;
				} else
					return index;
			}
			pos.setNext(map[getRowMap(row)][col].getPositionsArrows(3));
			break;
		}
		return -1;
		
	}

	public void newPosition(Arrow startPos) {
		// go through all the nodes of the currentPath
		// update position based on starting one
		PathNode mvt;
		int row, col;
		for (int i = 0; i < currentPath.size(); i++) {
			mvt = currentPath.get(i);
			if (mvt.getMvt().equals("turnLeft")) {
				if (startPos.getPoint() == 'n') {
					startPos.setPoint('w');
				} else if (startPos.getPoint() == 'w') {
					startPos.setPoint('s');
				} else if (startPos.getPoint() == 's') {
					startPos.setPoint('e');
				} else if (startPos.getPoint() == 'e') {
					startPos.setPoint('n');
				}
			} else if (mvt.getMvt().equals("turnRight")) {
				if (startPos.getPoint() == 'n') {
					startPos.setPoint('e');
				} else if (startPos.getPoint() == 'w') {
					startPos.setPoint('n');
				} else if (startPos.getPoint() == 's') {
					startPos.setPoint('w');
				} else if (startPos.getPoint() == 'e') {
					startPos.setPoint('s');
				}
			} else { // mvt.getMvt().equals("forward")
				if (startPos.getPoint() == 'n') {
					// movement along the positive x-axis
					// increment row
					row = startPos.getRow();
					startPos.setRow(row + 1);
				} else if (startPos.getPoint() == 'w') {
					// movement along the positive y-axis
					// decrement column
					col = startPos.getColumn();
					startPos.setColumn(col - 1);
				} else if (startPos.getPoint() == 's') {
					// movement along the negative x-axis
					// decrement row
					row = startPos.getRow();
					startPos.setRow(row - 1);
				} else if (startPos.getPoint() == 'e') {
					// movement along the negative y-axis
					// increment column
					col = startPos.getColumn();
					startPos.setColumn(col + 1);
				}
			}
		}
	}

	public void updatePositions() {
		Arrow cur;
		LinkedList<Integer> itemsToRemove = new LinkedList<Integer>();
		int ind;
		memRatio.clear();
		for (int i = 0; i < possibilities.size(); i++) { // for all possible
															// arrows, check if
															// the new position
															// of the robot
															// matches
			cur = possibilities.get(i);
			ind = getPositionToRemove(cur, i);

			if (ind != -1) {
				itemsToRemove.add(ind);
			}
		}
		int error = 0;
		for (Integer j : itemsToRemove) { // remove position which can't be the
											// starting one
			memRatio.add(possibilities.get(j - error)); // will get all the
														// elements from last to
														// some element i of
														// possibilities
			possibilities.remove(j - error);
			error++;
		}
	}

	public void take_data(boolean fromFrontSensor) {
		updatePositions();
		if (!fromFrontSensor)
			currentPath.remove(currentPath.size() - 1);

	}
}