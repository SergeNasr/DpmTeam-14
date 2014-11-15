public class Point {
	private double x;
	private double y;
	
	public Point(double xCoord, double yCoord){
		x = xCoord;
		y = yCoord;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public static Point convertTileToPoint(Tiles t) {
		double x = 15 + 15 * t.getCol();
		double y = 15 + 15 * t.getRow();
		
		return new Point(x, y);
	}
}