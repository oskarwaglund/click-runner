package abstracts;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import map.Wall;
import units.Unit;

public class KDTree {

	private static class XComparator implements Comparator<Unit> {
		@Override
		public int compare(Unit u1, Unit u2) {
			return (int) Math.signum(u1.getX() - u2.getX());
		}
	}

	private static class YComparator implements Comparator<Unit> {
		@Override
		public int compare(Unit u1, Unit u2) {
			return (int) Math.signum(u1.getY() - u2.getY());
		}
	}
	
	private static abstract class GetUnitValueFunction {
		abstract double getValue(Unit u);
	}
	
	private static class GetX extends GetUnitValueFunction {
		@Override
		double getValue(Unit u) {
			return u.getX();
		}
	}
	
	private static class GetY extends GetUnitValueFunction {
		double getValue(Unit u) {
			return u.getY();
		}
	}

	Node root;
	Unit closestUnit;
	double bestDistance;
	ArrayList<Wall> walls;
	
	static XComparator xC = new XComparator();
	static YComparator yC = new YComparator();

	static GetUnitValueFunction xF = new GetX();
	static GetUnitValueFunction yF = new GetY();
	
	private class Node {
		Node lower, upper;
		int depth;
		double splitValue;
		Unit unit;
		
		void insert(List<Unit> units, int depth) {
			this.depth = depth;
			switch (units.size()) {
			case 0:
				break;
			case 1:
				unit = units.get(0);
				break;
			default:
				Comparator<Unit> c;
				GetUnitValueFunction f;
				if (depth % 2 == 0) {
					c = xC;
					f = xF;
				} else {
					c = yC;
					f = yF;
				}
				units.sort(c);
				splitValue = f.getValue(units.get(units.size()/2));
				lower = new Node();
				upper = new Node();

				int splitIndex = 0;
				while(f.getValue(units.get(splitIndex)) <= splitValue && splitIndex < units.size()-1) {
					splitIndex++;
				}
				lower.insert(units.subList(0, splitIndex), depth + 1);
				upper.insert(units.subList(splitIndex, units.size()), depth + 1);
			}
		}

		void getClosestEnemy(Unit u) {
			if (isLeaf()) {
				checkBetter(u);
				return;
			}

			GetUnitValueFunction f;
			if(depth % 2 == 0) {
				f = xF;
			} else {
				f = yF;
			}
			
			if(f.getValue(u) <= splitValue) {
				lower.getClosestEnemy(u);
				if(f.getValue(u) + bestDistance >= splitValue) {
					upper.getClosestEnemy(u);
				}
			} else {
				upper.getClosestEnemy(u);
				if(f.getValue(u) - bestDistance <= splitValue) {
					lower.getClosestEnemy(u);
				}
			}
		}

		void checkBetter(Unit u) {
			if (unit != null) {
				double distance = u.distanceTo(unit);
				if (!unit.isDead() && distance <= bestDistance && u.sees(unit, KDTree.this.walls)) {
					bestDistance = distance;
					closestUnit = unit;
				}
			}
		}

		boolean isLeaf() {
			return lower == null && upper == null;
		}

		public void paint(Graphics g, int l, int r, int down, int top) {
			if (isLeaf())
				return;
			if (depth % 2 == 0) {
				g.drawLine((int) splitValue, top, (int) splitValue, down);
				if (lower != null) {
					lower.paint(g, l, (int)splitValue, down, top);
				}
				if (upper != null) {
					upper.paint(g, (int) splitValue, r, down, top);
				}
			} else {
				g.drawLine(l, (int) splitValue, r, (int) splitValue);
				if (lower != null) {
					lower.paint(g, l, r, down, (int) splitValue);
				}
				if (upper != null) {
					upper.paint(g, l, r, (int)splitValue, top);
				}
			}
		}

	}

	public KDTree(List<Unit> units, ArrayList<Wall> walls) {
		root = new Node();
		root.insert(units, 0);
		this.walls = walls;
	}

	public void paint(Graphics g) {
		g.setColor(new Color(250, 250, 250, 90));
		root.paint(g, 0, 1000, 0, 1000);
	}

	public Unit getClosestEnemy(Unit unit) {
		closestUnit = null;
		bestDistance = unit.visionRange();
		root.getClosestEnemy(unit);
		return closestUnit;
	}

}
