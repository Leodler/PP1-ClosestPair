package edu.cmich.cps542;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class ClosestPair {

	public static void main(String[] args) throws FileNotFoundException {
	
		/* load data from points.txt here */
		File pointsFile = new File("points2.txt");
		Scanner sc = new Scanner(pointsFile);
		List<Point> points = new ArrayList<Point>();

		while(sc.hasNextLine()) {
			String line = sc.nextLine();
			points.add(parseLine(line));
		}

		/* use your sort method here */
		List<Point> pointsSortedY = sortY(points);
		List<Point> pointsSortedX = sort(points);
		/* call efficientClosestPair here */
		PointPair pMin = efficientClosestPair(pointsSortedX, pointsSortedY);

	}

	public static Point parseLine(String line){
		line = line.replace(")", "")
				.replace("(", "")
				.replace(" ", "");
		String[] strPoints = line.split(",");
		return new Point(Double.parseDouble(strPoints[0]), Double.parseDouble(strPoints[1]));
	}

	public static PointPair efficientClosestPair(List<Point> pointsXOrdered, List<Point> pointsYOrdered) {
		// Base Case(s)
		int n = pointsXOrdered.size();
		if(n == 2) return new PointPair(pointsXOrdered.get(0), pointsXOrdered.get(1));
		if(n == 3) return bruteClosestPair(pointsXOrdered);

		// Divide into subproblems
		int mid = n / 2;
		PointPair deltaPair = new PointPair(new Point(0, 0), new Point(0, 0));
		Point midPoint = pointsXOrdered.get(mid);
		double deltaLeft = efficientClosestPair(pointsXOrdered.subList(0,mid), sortY(pointsXOrdered.subList(0, mid))).distSqrdBetweenPoints();
		double deltaRight = efficientClosestPair(pointsXOrdered.subList(mid,n), sortY(pointsXOrdered.subList(mid,n))).distSqrdBetweenPoints();
		double delta = Double.min(deltaLeft, deltaRight);
		System.out.println("Delta: " + delta);


		// Combine subproblem solutions
		List<Point> eligiblePoints = cutSortedY(pointsYOrdered, midPoint, delta);
		for(int i = 0; i < eligiblePoints.size(); i++) {
			for(int j = 1; j < eligiblePoints.size(); j++) {
				double pairDist = distance(eligiblePoints.get(i), eligiblePoints.get(j));
				if(pairDist < delta) {
					delta = pairDist;
					deltaPair = new PointPair(eligiblePoints.get(i), eligiblePoints.get(j));
				}
			}
		}

		return deltaPair;
			
	}

	public static List<Point> cutSortedY(List<Point> pointsYOrdered, Point midPoint, double delta) {
		List<Point> eligiblePoints = new ArrayList<Point>();
		double max = midPoint.x + delta;
		double min = midPoint.x - delta;
		Iterator<Point> sortedYIterator = pointsYOrdered.iterator();
		while(sortedYIterator.hasNext()){
			Point p1 = sortedYIterator.next();
			if(p1.y < min) continue;
			if(p1.y > max) break;
			eligiblePoints.add(p1);
		}
		return eligiblePoints;
	}
	
	public static PointPair bruteClosestPair(List<Point> points) {
		double minDist = Double.MAX_VALUE;
		double dist;
		PointPair minDistPair = new PointPair(new Point(0, 0), new Point(0, 0));
		for(int i = 0; i < points.size() - 1; i++){
			for(int j = i + 1; j < points.size(); j++) {
				dist = distance(points.get(i), points.get(j));
				minDist = Math.min(dist, minDist);
				minDistPair = new PointPair(points.get(i), points.get(j));
			}
		}

		return minDistPair;

	}
	
	
	public static List<Point> sort(List<Point> points) {
		if(points.size() < 2){
			return points;
		}
		int mid = points.size() / 2;
		List<Point> left = points.subList(0, mid);
		List<Point> right = points.subList(mid, points.size());

		right = sort(right);
		left = sort(left);

		List<Point> sorted = merge(left, right);
		
		return sorted;
	}
	public static List<Point> sortY(List<Point> points) {
		if(points.size() < 2){
			return points;
		}
		int mid = points.size() / 2;
		List<Point> left = points.subList(0, mid);
		List<Point> right = points.subList(mid, points.size());

		right = sortY(right);
		left = sortY(left);

		List<Point> sorted = mergeY(left, right);

		return sorted;
	}

	public static List<Point> merge(List<Point> left, List<Point> right){
		List<Point> sorted = new ArrayList<>();
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

	public static List<Point> mergeY(List<Point> left, List<Point> right){
		List<Point> sorted = new ArrayList<>();
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

	public static double distance(Point p1, Point p2) {
		return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
	}

}