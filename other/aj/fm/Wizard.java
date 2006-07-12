package aj.fm;

public class Wizard {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public final String handChoice[] = { "W", "F", "S", "D", "P", "w", "c",
			"V", "p", "-" };

	public final static String TARGET_SELF="Self";
	
	public final static String TARGET_NO_ONE="No_One";
	
	public static final int maxHitPoints = 14;

	private int currHitPoints = maxHitPoints;

	private String rightTarget, leftTarget;

	private String nextRightGest = handChoice[0], nextLeftGest = handChoice[0];

	private String rightHandPattern = "";

	private String leftHandPattern = "";

	private String name;

	private Spell nextLeftSpell;

	private Spell nextRightSpell;

	public String getName() {
		return name;
	}

	public int getCurrentHitPoints() {
		return currHitPoints;
	}

	public String getRightPattern() {
		return rightHandPattern;
	}

	public String getLeftPattern() {
		return leftHandPattern;
	}

	private static int count=0;
	public static Wizard createRandomWizard() {
		Wizard w = new Wizard();
		if (count==0) {
			w.setName("Humphry the Great");		count++;	
		}
		else if (count==1) {
			w.setName("Herman");count++;
		}
		else if (count==2) {
			w.setName("Spencer");count++;
		}
		return w;

	}

	private void setName(String string) {
		name = string;
	}

	public void setNextRightHand(String actionCommand) {
		nextRightGest = actionCommand;

	}

	public void setNextLeftHand(String actionCommand) {
		nextLeftGest = actionCommand;
	}

	public void applyNextGestures() {
		rightHandPattern += nextRightGest;
		leftHandPattern += nextLeftGest;
	}

	public String getNextRightGesture() {
		return nextRightGest;
	}

	public String getNextLeftGesture() {
		return nextLeftGest;
	}

	public void setNextLeftSpell(Spell sp) {
		nextLeftSpell = sp;
	}

	public void setNextRightSpell(Spell sp) {
		nextRightSpell = sp;
	}

	public Spell getRightSpell() {
		return nextRightSpell;
	}

	public Spell getLeftSpell() {
		return nextLeftSpell;
	}

	public void setLeftTarget(String string) {
		leftTarget=string;
	}
	public void setRightTarget(String string) {
		rightTarget=string;
	}

	public Object getRightTarget() {
		return rightTarget;
	}
	public Object getLeftTarget() {
		return leftTarget;
	}

}
