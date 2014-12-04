
/*
 * Arrow is an available position for the robot
 * Arrow is usually defined by the row and column of the block where it is, and it's orientation (north, west, south. east)
 */

/**
 * 
 * @author Serge Nasr
 *
 */
public class Arrow {
	private int row, column;
	private char point;	//checks where arrow is poiting (north, east, south or west)
	/**
	 * An Arrow contains an arrow that is used as a cursor throughout localisation
	 */
	private Arrow next;
	private double x, y, theta;
	/**
	 * Arrow is usually defined by the row and column of the block where it is, and it's orientation (north, west, south. east)
	 * @param r The row of the tile nesting the arrow
	 * @param c The column of the tile nesting the arrow
	 * @param p The direction of the arrow; 'n', 'w', 's' or 'e;
	 */
	public Arrow(int r, int c, char p) {
		row = r;
		column = c;
		point = p;
		next = this;
	}
	/**
	 * Arrow can be defined by its x, y coordinates and orientation
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param theta The direction of the arrow
	 */
	public Arrow(double x, double y, double theta) {
		this.x = x;
		this.y = y;
		this.theta = theta;
	}
	/**
	 * Set the row of the arrow
	 * @param row The desired row for a tile
	 */
	public void setRow(int row) {
		this.row = row;
	}
	/**
	 * Set the column of the arrow
	 * @param column The desired column for a tile
	 */
	public void setColumn(int column) {
		this.column = column;
	}
	/**
	 * Set the direction of the arrow
	 * @param point The desired direction for a tile
	 */
	public void setPoint(char point) {
		this.point = point;
	}
	/**
	 * Get the x coordinate
	 * @return x value on a graph
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * Get the y coordinate
	 * @return y value on a graph
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * get the direction
	 * @return Value can either be 0,90,180 or 270
	 */
	public double getTheta() {
		return theta;
	}
	/**
	 * Get the cursor of the arrow
	 * @return an Arrow object
	 */
	public Arrow getNext() {
		return next;
	}
	/**
	 * Get the row
	 * @return row number from 0 to 11
	 */
	public int getRow() {
		return row;
	}
	/**
	 * Get the column
	 * @return column number from 0 to 11
	 */
	public int getColumn() {
		return column;
	}
	/**
	 * Get the direction
	 * @return a char that is either 'n', 's', 'w' or 'e'
	 */
	public char getPoint() {
		return point;
	}
	/**
	 * Converts a column to an x coordinate
	 * @param col The column of the tile nesting the arrow.
	 * @return the x coordinate of the arrow
	 */
	public static double columnToY (int col) {
		return (- 15 + 30 * col); 
	}
	/**
	 * Converts a row to a y coordinate
	 * @param row The row of the tile nesting the arrow.
	 * @return the y coordinate of the arrow
	 */
	public static double rowToX (int row) {
		return (-15 +30*row); 
	}
	/**
	 * Converts from char convention to angle
	 * @param direction The direction of the arrow
	 * @return The angle at which the robot is theoretically pointing.
	 */
	public static double headToTheta (char direction) {
		switch (direction) {
		case 'n':
			return 0;
		case 'w':
			return 270;
		case 's':
			return 180;
		case 'e':
			return 90;
		default:
			return -11111;	//error
		}
	}
	/**
	 * Converts from and angle to a direction
	 * @param theta The value of the angle at which the robot is theoretically pointing.
	 * @return The char representing the given angle ('n','s','w' or 'e')
	 */
	public static char thetaToHead (double theta) {
		if (theta == 0) {
			return 'n';
		}
		else if (theta == 90) {
			return 'e';
		}
		else if (theta == 180) {
			return 's';
		}
		else if (theta == 270) {
			return 'w';
		}
		else return 'x';//error
	}
}
