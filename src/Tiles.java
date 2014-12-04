import java.util.LinkedList;
/**
 * 
 * @author Eduardo Coronado-Montoya
 *
 */
public class Tiles {
	
	private int col;
	private int row;
	
	public LinkedList<Tiles> adjList;	//to represent a graph using adjacency list
	public boolean isVisited;	//used in BFS

	private static Arrow[] positions;
	
	//variable keeping track whether there are hasBlock in the 4 tiles surrounding current tile
	private boolean [] hasBlock = new boolean[4];
	//tile number from 1 to max size of board
	private int tileNumber;
	//variable keeping track whether or not tile is an obstacle
	private boolean isObstacle = false;
	
	/**
	 * The tiles inside the programmed maps
	 * @param tileNumber The unique id of the tile
	 * @param isObstacle If the tile is a block, this will be true, else false.
	 */
	public Tiles(int tileNumber, boolean isObstacle){
		adjList = new LinkedList<Tiles>();
		this.isVisited = false;
		this.tileNumber = tileNumber;
		this.isObstacle = isObstacle;
		for(int i=0; i<4; i++){
			this.hasBlock[i] = false;
		}
		int count = 1;
		for(int i = 0; i < Constants.MAZE_SIZE; i++){
			for(int j = 0; j< Constants.MAZE_SIZE; j++){
				if(tileNumber == count){
					this.col = j;
					this.row = i;
				}
				count++;
			}
		}
		positions = new Arrow[4];
	}
	/**
	 * Get if the tile is an obstacle
	 * @return True if an obstacle, false otherwise.
	 */
	public boolean[] getObstacles(){
		return hasBlock;
	}
	
	/**
	 * Sets the north, west, south or east parameters of a tile
	 * @param index 0 if north, 1, if west, 2 if south, and 3 if east
	 * @param hasObstacle True if the tile has an obstacle right next to the direction pointed by the int index.
	 */
	public void setObstacles(int index, boolean hasObstacle){
		if(index < 4){
			hasBlock[index] = hasObstacle;
		}
		else return;
	}
	
	/**
	 * Get the unique id of a tile
	 * @return The int representing the unique id.
	 */
	public int getTileNumber(){
		return tileNumber;
	}
	/**
	 * See if a tile is an obstacle
	 * @return True if the tile is an obstacle, false otherwise.
	 */
	public boolean getIsObstacle(){
		return isObstacle;
	}
	/**
	 * Method that sets for a tile that is a block, all its orientations to true.
	 */
	public void setBlock(){
		isObstacle = true;
		for(int i = 0; i < 4 ; i++){
			hasBlock[i] = true;
		}
	}
	/**
	 * Method that checks if a tile was visited	
	 * @return True if tile was visited, false otherwise.
	 */
	public boolean isVisited() {
		return isVisited;
	}
	/**
	 * Toggle the boolean that defines if a tile was visited or not.
	 * @param isVisited The parameter setting if a tile was visited or not.
	 */
	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}
	/**
	 * Get the column of a tile
	 * @return A number from 0 to MAZE_SIZE - 1
	 */
	public int getCol(){
		return col;
	}
	/**
	 * Get the row of a tile
	 * @return A number from 0 to MAZE_SIZE - 1
	 */
	public int getRow(){
		return row;
	}
	/**
	 * Set the column of a tile
	 * @param xC A number from 0 to MAZE_SIZE - 1
	 */
	public void setCol(int xC){
		col = xC;
	}
	/**
	 * Set the row of a tile
	 * @param yC A number from 0 to MAZE_SIZE - 1
	 */
	public void setRow(int yC){
		row = yC;
	}
	
	public String coordinate(int index){
		if(index == 0)
			return "North";
		if(index == 1)
			return "West";
		if(index == 2)
			return "South";
		if (index == 3)
			return "East";
		else
			return "You're drunk, go home";
	}
	/**
	 * Get the north orientation of a tile
	 * @return True, if the tile has a block or a wall to its north
	 */
	public boolean isNorth() {
		return hasBlock[0];
	}
	/**
	 * Get the east orientation of a tile
	 * @return True, if the tile has a block or a wall to its east
	 */
	public boolean isEast() {
		return hasBlock[3];
	}
	/**
	 * Get the west orientation of a tile
	 * @return True, if the tile has a block or a wall to its west
	 */
	public boolean isWest() {
		return hasBlock[1];
	}
	/**
	 * Get the south orientation of a tile
	 * @return True, if the tile has a block or a wall to its south
	 */
	public boolean isSouth() {
		return hasBlock[2];
	}
	/**
	 * Generate the 4 arrows of possibilities of a tile
	 * @param r The row of the current tile
	 * @param c The column of the current tile
	 */
	public void generatePos(int r, int c){
		positions = new Arrow[4];
		
		positions[0] = new Arrow(r, c, 'n');
		positions[1] = new Arrow(r, c, 'w');
		positions[2] = new Arrow(r, c, 's');
		positions[3] = new Arrow(r, c, 'e');
	}
	/**
	 * Get the arrow of a tile at a given direction
	 * @param index 0 if north, 1 if west, 2 if south, 3 if east
	 * @return The arrow of the desired direction
	 */
	public Arrow getPositionsArrows(int index){
		if(index >= 0 || index <= 3)
			return positions[index];
		return null;
	}
	/**
	 * Get all the arrows of a tile
	 * @return An array of Arrow objects
	 */
	public Arrow []getPositionsArrows(){
		return positions;
	}
}