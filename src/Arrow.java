
/*
 * Arrow is an available position for the robot
 * Arrow is usually defined by the row and column of the block where it is, and it's orientation (north, west, south. east)
 */

public class Arrow {
	private int row, column;
	private char point;	//checks where arrow is poiting (north, east, south or west)
	private Arrow next;
	private double x, y, theta;

	public Arrow(int r, int c, char p) {
		row = r;
		column = c;
		point = p;
		next = this;	//next points to the latest arrow for the robot to check
		/*
		 * When a robot turns or moves forward, our algorithm only check if the next arrow can be correctly updated
		 * Based on this result, our algorithm either removes the current arrow from the possible solutions or updates the "next" arrow
		 */
	}
	
	public Arrow(double x, double y, double theta) {
		this.x = x;
		this.y = y;
		this.theta = theta;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public void setPoint(char point) {
		this.point = point;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getTheta() {
		return theta;
	}

	public Arrow getNext() {
		return next;
	}

	public void setNext(Arrow next) {
		this.next = next;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public char getPoint() {
		return point;
	}

	//the following methods convert from row and column of block to X and Y coordinates
	//and the heading of the arrow to the theta angle
	
	public static double columnToY (int col) {
		return (- 15 + 30 * col); 
	}

	public static double rowToX (int row) {
		return (-15 +30*row); 
	}

	public static double headToTheta (char head) {
		switch (head) {
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
	
	public static int xToRow (int x) {
		switch (x) {
		case -15:
			return 0;
		case 15:
			return 1;
		case 45:
			return 2;
		case 75:
			return 3;
		default:
				return 1111; //error
		}
	}
	
	public static int yToCol (int y) {
		switch (y) {
		case -15:
			return 0;
		case 15:
			return 1;
		case 45:
			return 2;
		case 75:
			return 3;
		default:
				return 1111; //error
		}
	}
	
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
