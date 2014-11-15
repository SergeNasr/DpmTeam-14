import java.util.LinkedList;



public class Tiles {
	
	private int x;
	private int y;
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
		for(int i = 0; i < 4; i++){
			for(int j = 0; j<4; j++){
				if(tileNumber == count){
					this.x = j;
					this.y = i;
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
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public void setX(int xC){
		x = xC;
	}
	public void setY(int yC){
		y = yC;
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
