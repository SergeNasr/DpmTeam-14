


public class Map {
	private static Tiles [][] tiles;
	private int [] obstacles;
	
	public static void main(String[]args){
		int [] obstacles = {1,7,8,14,21,34,56,67,78,89,90,100,112,113};
		Map map = new Map(obstacles);
		//printMapObstacles(map);
		
		GraphGenerator gg = new GraphGenerator(map);
		gg.createGraph();
		gg.printGraph();
	}
	public Map(int [] obstacles){
		
		this.obstacles = new int[obstacles.length];
		for(int i=0; i<obstacles.length; i++){
			this.obstacles[i] = obstacles[i];
		}
		int count = 1;
		tiles = new Tiles[12][12];
		
		
		for(int i = 0; i < tiles.length; i++){
			for(int j = 0; j < tiles[i].length; j++){
				tiles[i][j] = new Tiles(count,false);
				count ++;
			}
		}
		
		for(int i = 0; i< tiles.length; i++){
			for(int j = 0; j <tiles[i].length; j++){
				System.out.println("TileNumber = "+ tiles[i][j].getTileNumber() +"(x: " + tiles[i][j].getX() + ", y: " + tiles[i][j].getY() + ")");
			}
		}
		setWalls(0);
		setWalls(1);
		setWalls(2);
		setWalls(3);
		setBlocks();
		
		for (int i = 0 ; i < 12 ; i++) {
			for (int j = 0 ; j < 12 ; j++) {
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
				tiles[orientation][i].setObstacles(orientation, true); 
			}
		} else if(orientation == 1){
			for(int i = 0; i < tiles.length; i++){
				tiles[i][orientation - 1].setObstacles(orientation, true);
			}
		} else if(orientation == 2){
			for(int i = 0; i < tiles.length; i++){
				tiles[11][i].setObstacles(orientation, true);
			}
		} else{
			for(int i = 0; i < tiles.length; i++){
				tiles[i][11].setObstacles(orientation, true);
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
							} else if (k == 11){
								tiles[j+1][k].setObstacles(0, true);
								tiles[j][k-1].setObstacles(3, true);
							} else{
								tiles[j+1][k].setObstacles(0, true);
								tiles[j][k-1].setObstacles(3, true);
								tiles[j][k+1].setObstacles(1, true);
							}
						} else if(j == 11){
							if(k == 0){
								tiles[j-1][k].setObstacles(2, true);
								tiles[j][k+1].setObstacles(1, true);
							} else if(k == 11){
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
							} else if(k == 11){
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
	
	public Tiles[][] getTiles(){
		return tiles;
	}
	/*
	public Tiles getTile(int x, int y){
		for(int i = 0; i < tiles.length; i++){
			for(int j = 0; j < tiles.length; j++){
				Tiles temp = tiles[]
			}
		}
	}\
	*/
	//prints for each tile in which direction obstacles are located
	public static void printMapObstacles(Map map){
		Tiles [][] temp = map.getTiles();
		for(int i=0; i<12; i++){
			for(int j=0; j<12; j++){
				for(int k=0; k<4; k++){
					if(temp[i][j].getObstacles()[k])
						System.out.print("("+temp[i][j].getTileNumber()+", "+temp[i][j].coordinate(k)+"); ");
				}
				System.out.println(" ");
			}
		}
	}
}
