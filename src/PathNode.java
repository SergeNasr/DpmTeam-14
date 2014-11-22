
//nodes inside the list of the current path of the robot
//each node corresponds to a movement
//store movement type (turn or forward) and if the robot is seeing an obstacle after the movement
public class PathNode {
	private String mvt;
	private int tilesAway;
	
	public PathNode(String m, int numTiles) {
		mvt = m;
		tilesAway = numTiles;
	}

	public String getMvt() {
		return mvt;
	}
	public int getNodeTilesAway(){
		return tilesAway;
	}
}