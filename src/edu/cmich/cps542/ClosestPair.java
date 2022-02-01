package edu.cmich.cps542;

/**
 * Finds the closest pair of points on a cartesian plane from a given text file using both a bruteforce and an
 * efficient O(nlog(n)) solution; utilizes mergesort to efficiently sort the Point objects.
 * @author Mounika Chatla
 * @author David Kelley
 * @author Lucas Leodler
 */

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class ClosestPair {

	public static void main(String[] args) throws FileNotFoundException {

		/* load data from points.txt here */
		File pointsFile = new File("points.txt");
		Scanner sc = new Scanner(pointsFile);
		ArrayList<Point> points = new ArrayList<Point>();

		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			points.add(parseLine(line));
		}

		/* use your sort method here */
		ArrayList<Point> pointsSortedY = sortY(points);
		ArrayList<Point> pointsSortedX = sort(points);
		/* call efficientClosestPair here */
		PointPair pMin = efficientClosestPair(pointsSortedX, pointsSortedY);
	}

	/**
	 * Parses a text file line removing all whitespace and non-numeric characters, returning a new Point object
	 * containing an (x, y) coordinate
	 * @param line the line to parse for an (x, y) coordinate
	 * @return An object of type Point containing x and y respectively.
	 */
	public static Point parseLine(String line){
		line = line.replace(")", "")
				.replace("(", "")
				.replace(" ", "");
		String[] strPoints = line.split(",");

		return new Point(Double.parseDouble(strPoints[0]), Double.parseDouble(strPoints[1]));
	}

	/**
	 * Divide-and-conquer method for finding the closest pair of points within an ArrayList of Point objects.
	 * @param pointsXOrdered ArrayList of Point objects ordered by their X value
	 * @param pointsYOrdered ArrayList of Point objects ordered by their Y value
	 * @return The pair of points within the dataset which have the shortest distance between them
	 */
	public static PointPair efficientClosestPair(ArrayList<Point> pointsXOrdered, ArrayList<Point> pointsYOrdered) {
		// Base Case(s)
		int n = pointsXOrdered.size();
		if(n <= 3) {
			return bruteClosestPair(pointsXOrdered);
		}

		// Divide into subproblems
		int mid = (n - 1) / 2;
		PointPair deltaPair;
		double delta;
		ArrayList<Point> leftOfCenter = sliceArrayList(pointsXOrdered, 0, mid);
		ArrayList<Point> rightOfCenter = sliceArrayList(pointsXOrdered, mid, n);

		// Recursively divide the problem(s) until we reach the base-case.
		PointPair deltaLeft = efficientClosestPair(leftOfCenter, sortY(leftOfCenter));
		PointPair deltaRight = efficientClosestPair(rightOfCenter, sortY(rightOfCenter));
		// rightOfCenter[0] is the midpoint
		Point midPoint = rightOfCenter.get(0);
		if(deltaRight.distSqrdBetweenPoints() < deltaLeft.distSqrdBetweenPoints()) {
			deltaPair = deltaRight;
			delta = deltaRight.distSqrdBetweenPoints();
		} else {
			deltaPair = deltaLeft;
			delta = deltaLeft.distSqrdBetweenPoints();
		}


		// Combine subproblem solutions, iterate through candidate points looking for a smaller delta value.
		List<Point> eligiblePoints = cutSortedY(pointsYOrdered, midPoint, delta);
		for(int i = 0; i < eligiblePoints.size() - 1; i++) {
			for(int j = i+1; j < eligiblePoints.size(); j++) {
				// If we exceed the bounds of possible values break out of secondary loop
				if(eligiblePoints.get(j).y - eligiblePoints.get(i).y >= delta) {
					break;
				}
				double dist = distance(eligiblePoints.get(i), eligiblePoints.get(j));
				if(dist < delta && dist != 0.0) {
					delta = dist;
					deltaPair = new PointPair(eligiblePoints.get(i), eligiblePoints.get(j));
				}
			}
		}

		return deltaPair;
			
	}

	/**
	 * A helper method to iterate over an array of Y-ordered Point objects, finding candidates for a new shortest
	 * distance between two points (delta).
	 * @param pointsYOrdered ArrayList of Point objects ordered by their Y value
	 * @param midPoint The midpoint of the arraylist, which is the first element of the right half.
	 * @param delta The current shortest distance
	 * @return A list of points where midPoint.x minus their x value is less than delta (prospective new deltas)
	 */
	public static List<Point> cutSortedY(List<Point> pointsYOrdered, Point midPoint, double delta) {
		List<Point> eligiblePoints = new ArrayList<Point>();
		for (Point p1 : pointsYOrdered) {
			if (Math.abs(midPoint.x - p1.x) < delta) {
				eligiblePoints.add(p1);
			}
		}
		return eligiblePoints;
	}

	/**
	 * A method which finds the closest pairs of Point objects within an ArrayList in O(n^2) time.
	 * @param points An ArrayList of Point objects
	 * @return The pair of points with the shortest distance between them as a PointPair object.
	 */
	public static PointPair bruteClosestPair(List<Point> points) {
		double minDist = Double.MAX_VALUE;
		double dist;
		PointPair minDistPair = new PointPair(new Point(0, 0), new Point(1000000, 1000000));
		for(int i = 0; i < points.size() - 1; i++){
			for(int j = i + 1; j < points.size(); j++) {
				dist = distance(points.get(i), points.get(j));
				if(minDist > dist && dist != 0) {
					minDist = dist;
					minDistPair = new PointPair(points.get(i), points.get(j));
				}
			}
		}

		return minDistPair;

	}

	/**
	 * A method to begin the mergesort process according to the X values of the Point objects within the points
	 * ArrayList
	 * @param points An ArrayList of Point objects to be sorted
	 * @return The sorted points ArrayList
	 */
	public static ArrayList<Point> sort(ArrayList<Point> points) {
		if(points.size() < 2){
			return points;
		}
		int mid = points.size() / 2;
		ArrayList<Point> left = sliceArrayList(points, 0, mid);
		ArrayList<Point> right = sliceArrayList(points, mid, points.size());
		// Recursively divide into smaller partitions, then ascend the call stack merging them.
		right = sort(right);
		left = sort(left);

		ArrayList<Point> sorted = merge(left, right);
		
		return sorted;
	}

	/**
	 * A method to begin the mergesort process according to the Y values of the Point objects within the points
	 * ArrayList
	 * @param points An ArrayList of Point objects to be sorted by their Y values.
	 * @return The sorted points ArrayList.
	 */
	public static ArrayList<Point> sortY(ArrayList<Point> points) {
		if(points.size() < 2){
			return points;
		}
		int mid = points.size() / 2;
		ArrayList<Point> left = sliceArrayList(points, 0, mid);
		ArrayList<Point> right = sliceArrayList(points, mid, points.size());

		right = sortY(right);
		left = sortY(left);

		ArrayList<Point> sorted = mergeY(left, right);

		return sorted;
	}

	/**
	 * Recursive mergesort algorithm using ArrayList iterators to reduce the expense of getting values according
	 * to their index within an ArrayList. Sorts Point objects according to their X values.
	 * @param left The left half of an ArrayList
	 * @param right The right half of an ArrayList
	 * @return An ArrayList which is a sorted combination of the left and right halves.
	 */
	public static ArrayList<Point> merge(ArrayList<Point> left, ArrayList<Point> right){
		ArrayList<Point> sorted = new ArrayList<>();
		Iterator<Point> leftIterator = left.iterator();
		Iterator<Point> rightIterator = right.iterator();

		Point p1 = leftIterator.next();
		Point p2 = rightIterator.next();
		// Merge left and right side Point objects in ascending x value order
		for(;;){
			if(p1.x <= p2.x){
				sorted.add(p1);
				if(leftIterator.hasNext()){
					p1 = leftIterator.next();
				} else {
					sorted.add(p2);
					while(rightIterator.hasNext()) {
						sorted.add(rightIterator.next());
					}
					break;
				}
			} else {
				sorted.add(p2);
				if(rightIterator.hasNext()) {
					p2 = rightIterator.next();
				} else {
					sorted.add(p1);
					while(leftIterator.hasNext()) {
						sorted.add(leftIterator.next());
					}
					break;
				}
			}
		}
		return sorted;
	}
	/**
	 * Recursive mergesort algorithm using ArrayList iterators to reduce the expense of getting values according
	 * to their index within an ArrayList. Sorts Point objects by their Y values.
	 * @param left The left half of an ArrayList
	 * @param right The right half of an ArrayList
	 * @return An ArrayList which is a sorted combination of the left and right halves.
	 */
	public static ArrayList<Point> mergeY(ArrayList<Point> left, ArrayList<Point> right){
		ArrayList<Point> sorted = new ArrayList<>();
		Iterator<Point> leftIterator = left.iterator();
		Iterator<Point> rightIterator = right.iterator();

		Point p1 = leftIterator.next();
		Point p2 = rightIterator.next();
		// Merge left and right side Point objects in ascending y value order
		for(;;){
			if(p1.y <= p2.y){
				sorted.add(p1);
				if(leftIterator.hasNext()){
					p1 = leftIterator.next();
				} else {
					sorted.add(p2);
					while(rightIterator.hasNext()) {
						sorted.add(rightIterator.next());
					}
					break;
				}
			} else {
				sorted.add(p2);
				if(rightIterator.hasNext()) {
					p2 = rightIterator.next();
				} else {
					sorted.add(p1);
					while(leftIterator.hasNext()) {
						sorted.add(leftIterator.next());
					}
					break;
				}
			}
		}
		return sorted;
	}

	/**
	 * Method to slice an ArrayList of points from index start inclusively to index end exclusively,
	 * intended to replace ArrayList.sublist() which returns a List view rather than an ArrayList to fit
	 * within the given method signatures.
	 * @param points An ArrayList of Point objects
	 * @param start The index from which the slice should start inclusively
	 * @param end The index from which the slice should end exclusively
	 * @return The sliced ArrayList
	 */
	public static ArrayList<Point> sliceArrayList(ArrayList <Point> points, int start, int end) {
		ArrayList<Point> slicedPoints = new ArrayList<Point>();
		for(int i = start; i < end; i++) {
			slicedPoints.add(points.get(i));
		}
		return slicedPoints;
	}

	/**
	 * Calculates the distance between two Point objects
	 * @param p1 The first Point object
	 * @param p2 The second Point object
	 * @return The distance between the two Point objects.
	 */
	public static double distance(Point p1, Point p2) {
		return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
	}

}