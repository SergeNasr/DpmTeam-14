
public class Constants {
	/**
	 * Radius of the left and right wheels of the robot: {@value}cm
	 */
	public static final double RADIUS = 2.09;
	/**
	 * Width of the robot's two wheels : {@value}cm
	 */
	public static final double WIDTH = 11.3;
	/**
	 * Width between the robot's two light sensors: {@value}cm
	 */
	public static final double SENSORS_WIDTH = 14.2;
	/**
	 * Size of the square maze: {@value}
	 */
	public static final int MAZE_SIZE = 12;
	/**
	 * Speed of the robot when turning: {@value}degrees/second
	 */
	public static final int ROTATE_SPEED = 100;
	/**
	 * Speed of the robot when moving forward: {@value}degrees/second
	 */
	public static final int FORWARD_SPEED = 250;
	/**
	 * Speed for identifying the robot: {@value} degrees/second
	 */
	public static final int FIND_BLOCK_SPEED = 150;
	/**
	 * Value at which the light sensor sees a line: {@value}
	 */
	public static final int LIGHT_THRESHOLD = 45;
	/**
	 * Time allowed to find a line for the light sensors: {@value}ms
	 */
	public static final int TIME_THRESHOLD = 2000;
	/**
	 * Threshold of light value: {@value}
	 */
	public static final int LIGHT_TRASH_DATA = 250;
}
