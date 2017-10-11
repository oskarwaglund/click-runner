package abstracts;
import java.util.Set;
import java.util.TreeSet;

public class Node implements Comparable<Node>{
		double x, y;
		
		double gScore;
		double fScore;
		Node bestFrom;
		
		Set<Node> neighbors;
		
		public Node(double x, double y){
			this.x = x;
			this.y = y;
			gScore = Double.MAX_VALUE;
			fScore = Double.MAX_VALUE;
			bestFrom = null;
			neighbors = new TreeSet<>();
		}
		
		public void addNeighbor(Node n) {
			neighbors.add(n);
		}
		
		public int compareTo(Node n) {
			if(x != n.x) {
				return (int)Math.signum(x - n.x);
			}
			return (int)Math.signum(y - n.y);
		}
		
		public static double distance(Node n1, Node n2) {
			return Math.hypot(n1.x-n2.x, n1.y-n2.y);
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public double getgScore() {
			return gScore;
		}

		public double getfScore() {
			return fScore;
		}

		public Node getBestFrom() {
			return bestFrom;
		}

		public Set<Node> getNeighbors() {
			return neighbors;
		}

		public void setX(double x) {
			this.x = x;
		}

		public void setY(double y) {
			this.y = y;
		}

		public void setgScore(double gScore) {
			this.gScore = gScore;
		}

		public void setfScore(double fScore) {
			this.fScore = fScore;
		}

		public void setBestFrom(Node bestFrom) {
			this.bestFrom = bestFrom;
		}

		public void setNeighbors(Set<Node> neighbors) {
			this.neighbors = neighbors;
		}
	}
