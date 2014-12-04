
//nodes inside the list of the current path of the robot
//each node corresponds to a movement
//store movement type (turn or forward) and if the robot is seeing an obstacle after the movement
/**
 * 
 * @author Eduardo Coronado-Montoya
 * @author Serge Nasr
 *
 */
public class PathNode {
	private String mvt;
	private int tilesAway;
	/**
	 * Node of the path used by the robot that takes as input a vector of movement and a number of tiles it is away from at that movement
	 * @param m The movement type : "turnLeft", "turnRight" and "forward"
	 * @param numTiles The number of tiles away the robot is located from the sensor (0,1,2, or -1)
	 */
	public PathNode(String m, int numTiles) {
		mvt = m;
		tilesAway = numTiles;
	}
	/**
	 * Get the movement of the robot
	 * @return String representing the movement
	 */
	public String getMvt() {
		return mvt;
	}
	/**
	 * Get the number of tiles between the robot and an object/wall
	 * @return an int that is either 0,1,2, or -1(infinity as seen from the sensor) tiles away
	 */
	public int getNodeTilesAway(){
		return tilesAway;
	}
}