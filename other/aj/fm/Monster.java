package aj.fm;

public class Monster {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private String name;
	
	private String colors[] = { "green", "pink", "red", "blue", "yellow",
			"purple", "grey", "white", "orange", "magenta" };

	private Wizard master = null;

	private int maxHp = 0;

	private String monsterTypeName;

	private static int monsterCount[] = { 2, 0, 0, 0, 0, 0 };

	private String[] allMonsterTypeNames = { "Gobblin", "Oger", "Troll",
			"Giant", "Fire_Elemental", "Ice_Elemental" };

	public Object getName() {
		return name;
	}

}
