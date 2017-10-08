import java.util.Set;
import java.util.TreeSet;

class Node implements Comparable<Node>{
		double x, y;
		
		double gScore;
		double fScore;
		Node bestFrom;
		
		Set<Node> neighbors;
		
		Node(double x, double y){
			this.x = x;
			this.y = y;
			gScore = Double.MAX_VALUE;
			fScore = Double.MAX_VALUE;
			bestFrom = null;
			neighbors = new TreeSet<>();
		}
		
		void addNeighbor(Node n) {
			neighbors.add(n);
		}
		
		public int compareTo(Node n) {
			if(x != n.x) {
				return (int)Math.signum(x - n.x);
			}
			return (int)Math.signum(y - n.y);
		}
	}
