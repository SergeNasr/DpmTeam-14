public class Map {
	private static Tiles [][] tiles;
	private int [] obstacles;
	private static final int MAZE_SIZE = 4;
	
	/*
	public static void main(String[]args){
		int [] obstacles = {1,7,8,14};
		Map map = new Map(obstacles);
		printMapObstacles(map);
		
	}*/
	
	public Map(int [] blocks_tilenumber){
		
		this.obstacles = new int[blocks_tilenumber.length];
		for(int i=0; i< blocks_tilenumber.length; i++){
			this.obstacles[i] = blocks_tilenumber[i];
		}
		int count = 1;
		tiles = new Tiles[MAZE_SIZE][MAZE_SIZE];
		
		
		Tiles tile;
		for(int i = 0; i < tiles.length; i++){
			for(int j = 0; j < tiles[i].length; j++){
				tile = new Tiles(count,false);
				tiles[i][j] = tile;
				count ++;
			}
		}
		
		for(int i = 0; i < 4; i++){
			setWalls(i);
		}
		setBlocks();
		
		for (int i = 0 ; i < MAZE_SIZE ; i++) {
			for (int j = 0 ; j < MAZE_SIZE ; j++) {
				if (!tiles[i][j].getIsObstacle()) {
					tiles[i][j].generatePos(i, j);
				}
			}
		}
	}
		
	// 0 = north
	// 1 = west
	// 2 = south
	// 3 = east
	
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
				tiles[MAZE_SIZE-1][i].setObstacles(orientation, true);
			}
		} else{
			for(int i = 0; i < tiles.length; i++){
				tiles[i][MAZE_SIZE-1].setObstacles(orientation, true);
			}
		}
	}
	
	//sets obstacle blocks in map
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
							} else if (k == MAZE_SIZE-1){
								tiles[j+1][k].setObstacles(0, true);
								tiles[j][k-1].setObstacles(3, true);
							} else{
								tiles[j+1][k].setObstacles(0, true);
								tiles[j][k-1].setObstacles(3, true);
								tiles[j][k+1].setObstacles(1, true);
							}
						} else if(j == MAZE_SIZE-1){
							if(k == 0){
								tiles[j-1][k].setObstacles(2, true);
								tiles[j][k+1].setObstacles(1, true);
							} else if(k == MAZE_SIZE-1){
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
							} else if(k == MAZE_SIZE-1){
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
	public int[] getObstacles(){
		return this.obstacles;
	}
	
	public Tiles[][] getMap(){
		return tiles;
	}
	
	//prints for each tile in which direction obstacles are located
	public static void printMapObstacles(Map map){
		Tiles [][] temp = tiles;
		for(int i=0; i< MAZE_SIZE; i++){
			for(int j=0; j< MAZE_SIZE; j++){
				for(int k=0; k<4; k++){
					if(temp[i][j].getObstacles()[k])
						System.out.print("("+temp[i][j].getTileNumber()+", "+temp[i][j].coordinate(k)+"); ");
				}
				System.out.println(" ");
			}
		}
	}
}