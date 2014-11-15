import java.util.LinkedList;



public class Tiles {
	private final int BOARD_SIZE = 12;
	private int col;
	private int row;
	public boolean isVisited;	//used in BFS
	
	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}
	
	public LinkedList<Tiles> adjList;	//to represent a graph using adjacency list
	private static Arrow[] positions;
	
	//variable keeping track whether there are obstacles in the 4 tiles surrounding current tile
	private boolean [] obstacles = new boolean[4];
	//tile number from 1 to max size of board
	private int tileNumber;
	//variable keeping track whether or not tile is an obstacle
	private boolean isObstacle = false;
	
	public Tiles(int tileNumber, boolean isObstacle){
		adjList = new LinkedList<Tiles>();
		isVisited = false;
		this.tileNumber = tileNumber;
		this.isObstacle = isObstacle;
		for(int i=0; i<4; i++){
			this.obstacles[i] = false;
		}
		int count = 1;
		for(int i = 0; i < BOARD_SIZE; i++){
			for(int j = 0; j < BOARD_SIZE; j++){
				if(tileNumber == count){
					this.col = j;
					this.row = i;
				}
				count++;
			}
		}
	}
	
	public boolean[] getObstacles(){
		return obstacles;
	}
	
	
	public void setObstacles(int index, boolean yolo){
		if(index < 4){
			obstacles[index] = yolo;
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
	
	public void generatePos(int r, int c){ 
		positions = new Arrow[4];
		
		positions[0] = new Arrow(r, c, 'n');
		positions[1] = new Arrow(r, c, 'w');
		positions[2] = new Arrow(r, c, 's');
		positions[3] = new Arrow(r, c, 'e');
	}
	
	public Arrow[] getPositionsArrows(){
		return positions;
	}
}
