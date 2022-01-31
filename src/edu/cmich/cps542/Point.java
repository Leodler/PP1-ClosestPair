package edu.cmich.cps542;

/**
 * Represents a point on the Cartesian plane.  
 * 
 * @author eickholtj
 * @since 1-20-2021
 */
public class Point {

	double x;
	double y;
	
	Point(double x, double y) {
		this.x = x;
		this.y = y;
		
	}
	
	/*
	 * Generates a parenthetical representation for the point.
	 * 
	 * @return The coordinates of the points in parenthetical notation. 
	 */
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
}
