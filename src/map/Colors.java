package map;

import java.awt.Color;
import java.util.Map;
import java.util.TreeMap;

public class Colors {
	public static final Color WALL = new Color(100, 100, 100);
	public static final Color WALL_EDIT = Color.GREEN;
	public static final Color BACKGROUND = new Color(200, 190, 200);
	
	private static Map<Integer, Color> TEAM_COLORS;
	static {
		TEAM_COLORS = new TreeMap<>();
		TEAM_COLORS.put(1, Color.RED);
		TEAM_COLORS.put(2, Color.BLUE);
	}
	
	public static Color getTeamColor(int team) {
		if(TEAM_COLORS.containsKey(team)) {
			return TEAM_COLORS.get(team);
		} else {
			return Color.CYAN;
		}
	}
	
	public static final Color HEALTH_BG = Color.RED;
	public static final Color HEALTH_FG = Color.GREEN;
	
	public static final Color SELECTION = new Color(100, 100, 100, 20);
	public static final Color PATH = Color.YELLOW;
	
	public static final Color SHOOTER_SHOT = Color.BLACK;
	
	public static final Color TEXT = Color.WHITE;
	
	public static final Color MESH = Color.BLUE;
	public static final Color MESH_POINT = Color.YELLOW;
}
