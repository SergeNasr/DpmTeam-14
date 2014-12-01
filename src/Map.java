/**
 * 
 * @author Eduardo Coronado-Montoya
 * @author Raluca Niti
 *
 */
public class Map {
	private static Tiles [][] tiles;
	private int [] obstacles;
	
	/**
	 * A map that takes into account obstacles surrounding it.
	 * @param blocks_tilenumber
	 */
	public Map(int [] blocks_tilenumber){
		
		this.obstacles = new int[blocks_tilenumber.length];
		for(int i=0; i< blocks_tilenumber.length; i++){
			this.obstacles[i] = blocks_tilenumber[i];
		}
		int count = 1;
		tiles = new Tiles[Constants.MAZE_SIZE][Constants.MAZE_SIZE];
		
		// giving a tile number to all the tiles and giving them default options
		Tiles tile;
		for(int i = 0; i < tiles.length; i++){
			for(int j = 0; j < tiles[i].length; j++){
				tile = new Tiles(count,false);
				tiles[i][j] = tile;
				count ++;
			}
		}
		
		// set the north, west, south, and east walls.
		for(int i = 0; i < 4; i++){
			setWalls(i);
		}
		// set the obstacles in the map AND the tiles surrounding them
		setBlocks();
		
		// generate arrows for all the blcks that are not obstacles
		for (int i = 0 ; i < Constants.MAZE_SIZE ; i++) {
			for (int j = 0 ; j < Constants.MAZE_SIZE ; j++) {
				if (!tiles[i][j].getIsObstacle()) {
					for(int k = 0; k < 4; k++){
						tiles[i][j].getPositionsArrows()[k] = new Arrow(setRowTile(i),j,setOrientation(k));
					}
				}
			}
		}
		
	}
		
	// 0 = north
	// 1 = west
	// 2 = south
	// 3 = east
	
	/**
	 * Translate number convention to char
	 * @param k
	 * @return a char representing a direction (0:north ; 1:west ; 2:south ; 3: east)
	 */
	public char setOrientation(int k){
		if(k==0) return 'n';
		if(k==1) return 'w';
		if(k==2) return 's';
		if(k==3) return 'e';
		return 'z';
	}
	/**
	 * The row translated from a 2D array to a graph
	 * @param row
	 * @return The row as seen from the origin
	 */
	public static int setRowTile(int row) {
		return Constants.MAZE_SIZE - 1 - row;
	}
	
	/**
	 * Create a square around the map representing the neighbouring walls
	 * @param orientation
	 */
	public void setWalls(int orientation){
		if(orientation > 3)
			return;
		if(orientation == 0){
			for(int i = 0; i < tiles.length; i++){
				tiles[0][i].setObstacles(orientation, true); 
			}
		} else if(orientation == 1){
			for(int i = 0; i < tiles.length; i++){
				tiles[i][0].setObstacles(orientation, true);
			}
		} else if(orientation == 2){
			for(int i = 0; i < tiles.length; i++){
				tiles[Constants.MAZE_SIZE-1][i].setObstacles(orientation, true);
			}
		} else{
			for(int i = 0; i < tiles.length; i++){
				tiles[i][Constants.MAZE_SIZE-1].setObstacles(orientation, true);
			}
		}
	}
	
	/**
	 * Set the obstacles and the tiles around them
	 */
	public void setBlocks(){
		for(int i = 0; i < obstacles.length; i++){
			for(int j = 0; j < tiles.length; j++){
				for(int k = 0; k < tiles.length; k++){
					if(obstacles[i] == tiles[j][k].getTileNumber()){
						tiles[j][k].setBlock();
						if (j == 0){
							if(k == 0){
								tiles[j+1][k].setObstacles(0, true);
								tiles[j][k+1].setObstacles(1, true);
							} else if (k == Constants.MAZE_SIZE-1){
								tiles[j+1][k].setObstacles(0, true);
								tiles[j][k-1].setObstacles(3, true);
							} else{
								tiles[j+1][k].setObstacles(0, true);
								tiles[j][k-1].setObstacles(3, true);
								tiles[j][k+1].setObstacles(1, true);
							}
						} else if(j == Constants.MAZE_SIZE-1){
							if(k == 0){
								tiles[j-1][k].setObstacles(2, true);
								tiles[j][k+1].setObstacles(1, true);
							} else if(k == Constants.MAZE_SIZE-1){
								tiles[j-1][k].setObstacles(2, true);
								tiles[j][k-1].setObstacles(3, true);
							} else{
								tiles[j-1][k].setObstacles(2, true);
								tiles[j][k-1].setObstacles(3, true);
								tiles[j][k+1].setObstacles(1, true);
							}
						} else{
							if(k == 0){
								tiles[j-1][k].setObstacles(2, true);
								tiles[j+1][k].setObstacles(0, true);
								tiles[j][k+1].setObstacles(1, true);
							} else if(k == Constants.MAZE_SIZE-1){
								tiles[j-1][k].setObstacles(2, true);
								tiles[j+1][k].setObstacles(0, true);
								tiles[j][k-1].setObstacles(3, true);
							} else{
								tiles[j-1][k].setObstacles(2, true);
								tiles[j+1][k].setObstacles(0, true);
								tiles[j][k+1].setObstacles(1, true);
								tiles[j][k-1].setObstacles(3, true);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Get array of obstacles from the map
	 * @return array of tile numbers
	 */
	public int[] getObstacles(){
		return this.obstacles;
	}
	
	
	/**
	 * Get map
	 * @return 2D array of Tiles
	 */
	public Tiles[][] getMap(){
		return tiles;
	}
	
	/**
	 * Print the map
	 * @param map
	 */
	private static void printMapObstacles(Map map){
		Tiles [][] temp = tiles;
		for(int i=0; i< Constants.MAZE_SIZE; i++){
			for(int j=0; j< Constants.MAZE_SIZE; j++){
				for(int k=0; k<4; k++){
					if(temp[i][j].getObstacles()[k])
						System.out.print("("+temp[i][j].getTileNumber()+", "+temp[i][j].coordinate(k)+"); ");
				}
				System.out.println(" ");
			}
		}
		
		for(int i=0; i< Constants.MAZE_SIZE; i++){
			for(int j=0; j< Constants.MAZE_SIZE; j++){
				for(int k=0; k<4; k++){
					if(!temp[i][j].getObstacles()[k])
						System.out.print("("+temp[i][j].getPositionsArrows(k).getNext().getColumn()+", "+
								temp[i][j].getPositionsArrows(k).getNext().getColumn() +"); ");
				}
				System.out.println(" ");
			}
		}
	}
}