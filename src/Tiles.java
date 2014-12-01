import java.util.LinkedList;

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
	 * 
	 * @param tileNumber
	 * @param isObstacle
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
	
	public boolean[] getObstacles(){
		return hasBlock;
	}
	
	
	public void setObstacles(int index, boolean yolo){
		if(index < 4){
			hasBlock[index] = yolo;
		}
		else return;
	}
	
	public int getTileNumber(){
		return tileNumber;
	}
	
	public boolean getIsObstacle(){
		return isObstacle;
	}
	public void setBlock(){
		isObstacle = true;
		for(int i = 0; i < 4 ; i++){
			hasBlock[i] = true;
		}
	}
		
	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}
	public int getCol(){
		return col;
	}
	public int getRow(){
		return row;
	}
	public void setCol(int xC){
		col = xC;
	}
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
	public boolean isNorth() {
		return hasBlock[0];
	}

	public boolean isEast() {
		return hasBlock[3];
	}

	public boolean isWest() {
		return hasBlock[1];
	}

	public boolean isSouth() {
		return hasBlock[2];
	}

	public void generatePos(int r, int c){
		positions = new Arrow[4];
		
		positions[0] = new Arrow(r, c, 'n');
		positions[1] = new Arrow(r, c, 'w');
		positions[2] = new Arrow(r, c, 's');
		positions[3] = new Arrow(r, c, 'e');
	}
	
	public Arrow getPositionsArrows(int index){
		if(index >= 0 || index <= 3)
			return positions[index];
		return null;
	}
	public Arrow []getPositionsArrows(){
		return positions;
	}
}