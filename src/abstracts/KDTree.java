package abstracts;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import units.Unit;

public class KDTree {

	private class XComparator implements Comparator<Unit> {
		@Override
		public int compare(Unit u1, Unit u2) {
			return (int) Math.signum(u1.getX() - u2.getX());
		}
	}

	private class YComparator implements Comparator<Unit> {
		@Override
		public int compare(Unit u1, Unit u2) {
			return (int) Math.signum(u1.getY() - u2.getY());
		}
	}

	int treeDepth;
	Node root;
	Unit closestUnit;
	double bestDistance;

	private class Node {
		Node lower, upper;
		int depth;
		Unit unit;

		void insert(List<Unit> units, int depth) {
			this.depth = depth;
			KDTree.this.treeDepth = Math.max(KDTree.this.treeDepth, depth);
			switch (units.size()) {
			case 0:
				break;
			case 1:
				unit = units.get(0);
				break;
			default:
				Comparator<Unit> c;
				if (depth % 2 == 0) {
					c = new XComparator();
				} else {
					c = new YComparator();
				}
				units.sort(c);
				int splitIndex = units.size() / 2;
				lower = new Node();
				upper = new Node();

				unit = units.get(splitIndex);
				lower.insert(units.subList(0, splitIndex), depth + 1);
				upper.insert(units.subList(splitIndex + 1, units.size()), depth + 1);
			}
		}

		void getClosestEnemy(Unit u) {
			if (isLeaf()) {
				checkBetter(u);
				return;
			}

			if (depth % 2 == 0) {
				if (u.getX() < unit.getX()) {
					lower.getClosestEnemy(u);
					if (u.getX() + bestDistance >= unit.getX()) {
						upper.getClosestEnemy(u);
					}
				} else {
					upper.getClosestEnemy(u);
					if (u.getX() - bestDistance <= unit.getX()) {
						lower.getClosestEnemy(u);
					}
				}
			} else {
				if (u.getY() < unit.getY()) {
					lower.getClosestEnemy(u);
					if (u.getY() + bestDistance >= unit.getY()) {
						upper.getClosestEnemy(u);
					}
				} else {
					upper.getClosestEnemy(u);
					if (u.getY() - bestDistance <= unit.getY()) {
						lower.getClosestEnemy(u);
					}
				}
			}
			checkBetter(u);
		}

		void checkBetter(Unit u) {
			if (unit != null) {
				double distance = u.distanceTo(unit);
				if (unit.getTeam() != u.getTeam() && distance <= u.visionRange()) {
					if (distance < bestDistance) {
						bestDistance = distance;
						closestUnit = unit;
					}
				}
			}

		}

		boolean isLeaf() {
			return lower == null && upper == null;
		}

		public void paint(Graphics g, int l, int r, int down, int top) {
			if (unit == null)
				return;
			if (depth % 2 == 0) {
				g.drawLine((int) unit.getX(), top, (int) unit.getX(), down);
				if (lower != null) {
					lower.paint(g, l, (int) unit.getX(), down, top);
				}
				if (upper != null) {
					upper.paint(g, (int) unit.getX(), r, down, top);
				}
			} else {
				g.drawLine(l, (int) unit.getY(), r, (int) unit.getY());
				if (lower != null) {
					lower.paint(g, l, r, down, (int) unit.getY());
				}
				if (upper != null) {
					upper.paint(g, l, r, (int) unit.getY(), top);
				}
			}
		}

	}

	public KDTree(ArrayList<Unit> units) {
		root = new Node();
		root.insert(units, 0);
	}

	public void paint(Graphics g) {
		g.setColor(new Color(250, 250, 250, 90));
		root.paint(g, 0, 1000, 0, 1000);
	}

	public Unit getClosestEnemy(Unit unit) {
		bestDistance = Double.MAX_VALUE;
		root.getClosestEnemy(unit);
		return closestUnit;
	}

}
