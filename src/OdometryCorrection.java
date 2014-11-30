import java.util.LinkedList;

import lejos.nxt.ColorSensor;
import lejos.nxt.Sound;
import lejos.util.Delay;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private ColorSensor colorSensorLeft;
	private ColorSensor colorSensorRight;
	private SquareDriver driver;

	public double rotateCounterAngle;
	public double rotateClockAngle;

	public boolean counterCor;
	public boolean clockCor;

	private boolean exitCorrection = false;

	public int distanceTacho;

	// constructor
	public OdometryCorrection(Odometer odometer, ColorSensor colorSensorLeft,
			ColorSensor colorSensorRight, SquareDriver driver) {
		this.odometer = odometer;
		this.colorSensorLeft = colorSensorLeft;
		this.colorSensorRight = colorSensorRight;
		this.driver = driver;
	}

	public void setExitCorrection(boolean exitCorrection) {
		this.exitCorrection = exitCorrection;
	}

	public void setDriver(SquareDriver driver) {
		this.driver = driver;
	}

	// run method (required for Thread)
	public void run() {
		Sound.setVolume(Sound.VOL_MAX);
		colorSensorLeft.setFloodlight(true);
		colorSensorRight.setFloodlight(true);

		int leftValue;
		int rightValue;
		int firstTacho = 0;
		int secondTacho = 0;
		distanceTacho = 0;
		boolean firstLineSeen = false;
		boolean leftSeen = false;
		boolean rightSeen = false;

		double startTime = 0;

		counterCor = false;
		clockCor = false;

		while (!exitCorrection) {
			if (!driver.isRotating()) {
				leftValue = colorSensorLeft.getLightValue();
				rightValue = colorSensorRight.getLightValue();

				if (leftValue < Constants.LIGHT_THRESHOLD && !leftSeen) {
					Sound.beep();
					if (!firstLineSeen) {
						startTime = System.currentTimeMillis();
						firstTacho = driver.getLeftMotor().getTachoCount();
						firstLineSeen = true;
						counterCor = true;
						clockCor = false;
					} else { // TODO the code doesn't consider an else case
						if (System.currentTimeMillis() - startTime < Constants.TIME_THRESHOLD) {
							secondTacho = driver.getRightMotor()
									.getTachoCount();
							distanceTacho = secondTacho;
							int tachoDif = secondTacho - firstTacho;
							double deltaDist = tachoDif
									* (2 * Math.PI * Constants.RADIUS) / 360;
							rotateClockAngle = Math.abs(Math
									.toDegrees(correctionAngle(deltaDist)));
						}
						firstLineSeen = false;
					}
					leftSeen = true;
					rightSeen = false;
					Delay.msDelay(50);
				}

				else if (rightValue < Constants.LIGHT_THRESHOLD && !rightSeen) {
					Sound.beep();
					if (!firstLineSeen) {
						startTime = System.currentTimeMillis();
						firstTacho = driver.getRightMotor().getTachoCount();
						firstLineSeen = true;
						clockCor = true;
						counterCor = false;
					} else {
						if (System.currentTimeMillis() - startTime < Constants.TIME_THRESHOLD) {
							secondTacho = driver.getLeftMotor().getTachoCount();
							distanceTacho = secondTacho;
							int tachoDif = secondTacho - firstTacho;
							double deltaDist = tachoDif
									* (2 * Math.PI * Constants.RADIUS) / 360;
							rotateCounterAngle = Math.abs(Math
									.toDegrees(correctionAngle(deltaDist)));
						}
						firstLineSeen = false;
					}
					leftSeen = false;
					rightSeen = true;
					Delay.msDelay(50);
				}
			}
		}
	}

	private double correctionAngle(double dist) {
		return Math.atan(dist / Constants.SENSORS_WIDTH);
	}

	static LinkedList<Integer> ma = new LinkedList<Integer>();
	public static int nbTrue = 0;
	public static int nbFalse = 0;

	public static void setMA(int data) {
		if (data >= Constants.LIGHT_TRASH_DATA)
			return;

		if (ma.size() < 5) {
			ma.add(data);
		} else {
			ma.remove(0);
			ma.add(ma.size() - 1, data);
		}
	}

	public static boolean differential() {
		int diff = 0;

		for (int i = 0; i < ma.size(); i++) {
			if (i != 0) {
				diff = Math.abs(ma.get(i) - ma.get(i - 1));
			}
			if (Math.abs(diff) > 10) {
				if (nbTrue == 0 && nbFalse == 1) {
					nbTrue++;
				} else if (nbTrue == 1 && nbFalse == 2) {
					nbTrue++;
				}
				return true;
			}
		}
		if (nbTrue == 0 && nbFalse == 0) {
			nbFalse++;
		} else if (nbTrue == 1 && nbFalse == 1) {
			nbFalse++;
		} else if (nbTrue == 2 && nbFalse == 2) {
			nbFalse++;
		}
		return false;
	}

	public static boolean sawLine() {
		if (nbFalse == 3) {
			nbFalse = 0;
			nbTrue = 0;
			return true;
		}
		return false;
	}

}