
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXT;
import lejos.nxt.NXTRegulatedMotor;

/*
* Odometer.java
*/

public class Odometer extends Thread {
	// robot position
	private static int lastTachoL;
	private static int lastTachoR;
	private static int nowTachoL;
	private static int nowTachoR;
	private double x, y, theta, angle;
	private double dirX, dirY;
	private double distL, distR, deltaD, deltaT, dX, dY;
	private double [] oldDH, dDH;
	private double maxY = 0.0;
	private static NXTRegulatedMotor leftMotor;
	private static NXTRegulatedMotor rightMotor;
	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;
	private static double wb = 15.8;
	private static double wr = 2.09;
	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer(double x, double y, NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
		leftMotor = leftMotor;
		rightMotor = rightMotor;
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();
		leftMotor.flt();
		rightMotor.flt();
		lastTachoL = leftMotor.getTachoCount();
		lastTachoR = rightMotor.getTachoCount();
		x = x;
		y = y;
		theta = 0.0;
		angle = 0.0;
		lock = new Object();
		oldDH = new double [2];
		dDH = new double [2];
		
		
	}
	
	public void getDisplacementAndHeading(double [] data) {
		int leftTacho, rightTacho;
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();
		
		data[0] = (leftTacho * wr + rightTacho * wr) *	Math.PI / 360.0;
		data[1] = (leftTacho * wr - rightTacho * wr) / wb;
	}
	
	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;
		
		LCD.clear();
		LCD.drawString("Odometer Demo",0,0,false);
		LCD.drawString("Current X ",0,4,false);
		LCD.drawString("Current Y ",0,5,false);
		LCD.drawString("Current T ",0,6,false);

		while (true) {
			updateStart = System.currentTimeMillis();
			// put (some of) your odometer code here
			getDisplacementAndHeading(dDH);
			dDH[0] -= oldDH[0];
			dDH[1] -= oldDH[1];
			synchronized (lock) {
				double oldTheta = angle + dDH[1];
				oldTheta = fixDegAngle(oldTheta);
				angle = (angle + dDH[1]);
				angle = fixDegAngle(angle);
				// don't use the variables x, y, or theta anywhere but here!
				
				//update position x and y based on change in wheel tachometers
				//formulas used come from course slides
				//theta is computed as a clockwise rotation from the +y axis
				nowTachoL = Motor.A.getTachoCount();
				nowTachoR = Motor.C.getTachoCount();
				distL = Math.PI*wr*(nowTachoL-lastTachoL)/180;
				distR = Math.PI*wr*(nowTachoR-lastTachoR)/180;
				lastTachoL = nowTachoL;
				lastTachoR = nowTachoR;
				deltaD = 0.5*(distL + distR);
				deltaT = (distL - distR)/wb;
				theta += deltaT;
				dX = deltaD * Math.sin(theta);
				dY = deltaD * Math.cos(theta);
				x = x + dX;
				y = y + dY;
							
				//display current x and y position in cm and theta in degrees
				LCD.drawInt((int)x, 4, 11,4);
				LCD.drawInt((int)y,4,11,5);
				LCD.drawInt((int)(angle*180/(Math.PI)),4,11,6);
			}
			
			oldDH[0] += dDH[0];
			oldDH[1] += dDH[1];
			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}
	
	// + direction means going towards origin or is inbound
	// - direction means going away from origin or is outbound
	public int direction(){
		if(dirX < 0 || dirY < 0)
			return 1;
		else if(dirX > 0 || dirY > 0)
			return -1;
		else
			return 0;
	}
	
	public static double fixDegAngle(double angle) {		
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);
		
		return angle % 360.0;
	}
	
	private double invTan(double x, double y){
		if(x==0){
			if(y<0)
				return Math.PI*3/2;
			else
				return Math.PI/2;
		}
		else if(y==0){
			if(x<0)
				return Math.PI;
			else
				return 0;
		}	
		else if(x>0)
			return Math.atan(y/x);
		else if(y>0)
			return Math.PI - Math.atan(y/x);
		else
			return Math.PI + Math.atan(y/x);
	}
	

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = angle;
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}
	
	public double getAngle() {
		double result;

		synchronized (lock) {
			result = angle;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				angle = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
	
	public void setAngle(double angle) {
		synchronized (lock) {
			this.angle = angle;
		}
	}
}