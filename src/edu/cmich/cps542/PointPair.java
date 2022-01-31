package edu.cmich.cps542;

/** 
 * Represents two points in the Cartesian plane.  
 * 
 * @author eickholtj
 * @since 1-21-2021
 */
public class PointPair {

	Point a;
	Point b;
	
	PointPair(Point a, Point b) {
		this.a = a;
		this.b = b;
	}
	
	public double distBetweenPoints() {
		
		return Math.sqrt(distSqrdBetweenPoints());
	}
	
	public double distSqrdBetweenPoints() {
		
		return distanceSqrdBetweenPoints(this.a, this.b);
	}
	
	public static double distanceSqrdBetweenPoints(Point a, Point b) {
		return Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2);
	}

	public String toString() {
		String rValue = "Points in pair: " + a + ", " + b;
		
		return rValue;
	}
	
	public boolean equals(Point i, Point j, double tolerance) {
		if(((Math.sqrt(distanceSqrdBetweenPoints(this.a, i)) < tolerance) &&
				Math.sqrt(distanceSqrdBetweenPoints(this.b, j))	< tolerance) ||
				((Math.sqrt(distanceSqrdBetweenPoints(this.a, i)) < tolerance) &&
						Math.sqrt(distanceSqrdBetweenPoints(this.b, j))	< tolerance)) {
			return true;
		}
		else {
			return false;

		}
	}

}
