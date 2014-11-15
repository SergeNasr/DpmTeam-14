import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;



@SuppressWarnings("deprecation")
public class GraphGenerator {
	private Tiles[][] tile;
	private ArrayList<Tiles> graph;

	public ArrayList<Tiles> getGraph() {
		return graph;
	}

	public GraphGenerator(Map map) {
		tile = map.getTiles();
		graph = new ArrayList<Tiles>();
	}

	public void createGraph() {	//unfinished
		int rows = tile.length;
		int col = tile[0].length;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < col; j++) {
				// if tile[i][j] is not an obstacle
				if (!tile[i][j].getIsObstacle()) {
					// add neighbors that aren't obstacles to adjacency list of the tile
					if (i != 0 && !tile[i - 1][j].getIsObstacle()) {
						tile[i][j].adjList.add(tile[i - 1][j]);
					}

					if (i != rows - 1 && !tile[i + 1][j].getIsObstacle()) {
						tile[i][j].adjList.add(tile[i + 1][j]);
					}

					if (j != 0 && !tile[i][j - 1].getIsObstacle()) {
						tile[i][j].adjList.add(tile[i][j - 1]);
					}

					if (j != col - 1 && !tile[i][j + 1].getIsObstacle()) {
						tile[i][j].adjList.add(tile[i][j + 1]);
					}
				}
				
				// add tile to ArrayList<Tile> which represents the graph as an adjacency list
				graph.add(tile[i][j]);
			}
		}
	}
	
	public void printGraph() {
		for (int i = 0; i < graph.size(); i++) {
			for ( Tiles t : graph.get(i).adjList) {
				System.out.println("For tile "+ graph.get(i).getTileNumber() +  " " + t.getTileNumber());
			}
		}
	}
	
	public LinkedList<Tiles> bfs(Tiles start, Tiles dest) {
		HashMap<Tiles, Tiles> prev = new HashMap<Tiles, Tiles>();
		LinkedList<Tiles> path = new LinkedList<Tiles>();
		LinkedList<Tiles> q = new LinkedList<Tiles>();
		
		// add starting position
		q.add(start);
		start.setVisited(true);
		
		while (!q.isEmpty()) {
			Tiles current = (Tiles) q.remove(0);
			
			if (current.equals(dest)) {
				break;
			}
			else {
				for (Tiles neighbor : current.adjList) {
					if (!neighbor.isVisited()) {
						neighbor.setVisited(true);
						q.add(neighbor);
						prev.put(neighbor, current);
					}
				}
			}
		}
		
		// get shortest path
		for (Tiles tile = dest; tile != null; tile = prev.get(tile)) {
			path.add(0, tile);
		}
		
		return path;
	}
	
	
	public void printPath() {
		Tiles st = graph.get(1);
		Tiles end = graph.get(25);
		LinkedList<Tiles> pp = bfs(st, end);
		
		for (Tiles f : pp) {
			System.out.println(f.getTileNumber());
		}
	}
}
