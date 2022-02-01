package edu.cmich.cps542;

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

		while(sc.hasNextLine()) {
			String line = sc.nextLine();
			points.add(parseLine(line));
		}

		/* use your sort method here */
		ArrayList<Point> pointsSortedY = sortY(points);
		ArrayList<Point> pointsSortedX = sort(points);
		/* call efficientClosestPair here */
		PointPair pMin = efficientClosestPair(pointsSortedX, pointsSortedY);
		System.out.println(pMin);
		System.out.println(Double.toString(pMin.distSqrdBetweenPoints()));
		System.out.println();
		PointPair pMinBruteforce = bruteClosestPair(points);
		System.out.println(pMinBruteforce);
		System.out.println(pMinBruteforce.distSqrdBetweenPoints());

	}

	public static Point parseLine(String line){
		line = line.replace(")", "")
				.replace("(", "")
				.replace(" ", "");
		String[] strPoints = line.split(",");

		return new Point(Double.parseDouble(strPoints[0]), Double.parseDouble(strPoints[1]));
	}

	public static PointPair efficientClosestPair(ArrayList<Point> pointsXOrdered, ArrayList<Point> pointsYOrdered) {
		// Base Case(s)
		int n = pointsXOrdered.size();
		if(n <= 3) return bruteClosestPair(pointsXOrdered);

		// Divide into subproblems
		int mid = (n - 1) / 2;
		PointPair deltaPair;
		double delta;
		ArrayList<Point> leftOfCenter = sliceArrayList(pointsXOrdered, 0, mid);
		ArrayList<Point> rightOfCenter = sliceArrayList(pointsXOrdered, mid, n);

		PointPair deltaLeft = efficientClosestPair(leftOfCenter, sortY(leftOfCenter));
		PointPair deltaRight = efficientClosestPair(rightOfCenter, sortY(rightOfCenter));
		Point midPoint = rightOfCenter.get(0);
		if(deltaRight.distSqrdBetweenPoints() < deltaLeft.distSqrdBetweenPoints()) {
			deltaPair = deltaRight;
			delta = deltaRight.distSqrdBetweenPoints();
		} else {
			deltaPair = deltaLeft;
			delta = deltaLeft.distSqrdBetweenPoints();
		}


		// Combine subproblem solutions
		List<Point> eligiblePoints = cutSortedY(pointsYOrdered, midPoint, delta);
		for(int i = 0; i < eligiblePoints.size() - 1; i++) {
			for(int j = i+1; j < eligiblePoints.size(); j++) {
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

	public static List<Point> cutSortedY(List<Point> pointsYOrdered, Point midPoint, double delta) {
		List<Point> eligiblePoints = new ArrayList<Point>();
		Iterator<Point> sortedYIterator = pointsYOrdered.iterator();
		while(sortedYIterator.hasNext()){
			Point p1 = sortedYIterator.next();
			if(Math.abs(midPoint.x - p1.x) < delta) {
				eligiblePoints.add(p1);
			}
		}
		return eligiblePoints;
	}
	
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
	
	
	public static ArrayList<Point> sort(ArrayList<Point> points) {
		if(points.size() < 2){
			return points;
		}
		int mid = points.size() / 2;
		ArrayList<Point> left = sliceArrayList(points, 0, mid);
		ArrayList<Point> right = sliceArrayList(points, mid, points.size());

		right = sort(right);
		left = sort(left);

		ArrayList<Point> sorted = merge(left, right);
		
		return sorted;
	}
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

	public static ArrayList<Point> merge(ArrayList<Point> left, ArrayList<Point> right){
		ArrayList<Point> sorted = new ArrayList<>();
		Iterator<Point> leftIterator = left.iterator();
		Iterator<Point> rightIterator = right.iterator();

		Point p1 = leftIterator.next();
		Point p2 = rightIterator.next();

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

	public static ArrayList<Point> mergeY(ArrayList<Point> left, ArrayList<Point> right){
		ArrayList<Point> sorted = new ArrayList<>();
		Iterator<Point> leftIterator = left.iterator();
		Iterator<Point> rightIterator = right.iterator();

		Point p1 = leftIterator.next();
		Point p2 = rightIterator.next();

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

	public static ArrayList<Point> sliceArrayList(ArrayList <Point> points, int start, int end) {
		ArrayList<Point> slicedPoints = new ArrayList<Point>();
		for(int i = start; i < end; i++) {
			slicedPoints.add(points.get(i));
		}
		return slicedPoints;
	}

	public static double distance(Point p1, Point p2) {
		return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
	}

}