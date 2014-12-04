public class Point {
	private double x;
	private double y;
	/**
	 * A point in the graph that represents a location
	 * @param xCoord An x coordinate on the graph
	 * @param yCoord A y coordinate on the graph
	 */
	public Point(double xCoord, double yCoord){
		x = xCoord;
		y = yCoord;
	}
	/**
	 * Get the x coordinate of a point
	 * @return the x coordinate of a point
	 */
	public double getX(){
		return x;
	}
	/**
	 * Get the y coordinate of a point
	 * @return the y coordinate of a point
	 */
	public double getY(){
		return y;
	}
	/**
	 * Convert a Tile's row and column to a point.
	 * @param t A Tile object
	 * @return a point containing the x and y parameters of the tile.
	 */
	public static Point convertTileToPoint(Tiles t) {
		double x = 15 + 30 * t.getCol();
		double y = 15 + 30 * t.getRow();
		
		return new Point(x, y);
	}
}