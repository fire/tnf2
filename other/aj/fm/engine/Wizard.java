package aj.fm.engine;

public class Wizard {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public final String handChoice[]={"P","D","F","W","S","V"};
	
	public static final int maxHitPoints=14;
	
	private int currHitPoints=maxHitPoints;
	
	private String rightTarget,leftTarget;
	
	private String nextRightGest=handChoice[0],nextLeftGest=handChoice[0];
	
	private String rightHandPattern = "";

	private String leftHandPattern = "";
	
	private String name;

	private String nextLeftSpell;

	private String nextRightSpell;
	
	public String getName() {return name;}

	public int getCurrentHitPoints() {
		return currHitPoints;
	}

	public String getRightPattern() {
		return rightHandPattern;
	}
	public String getLeftPattern() {
		return leftHandPattern;
	}

	public static Wizard createRandomWizard() {
		Wizard w=new Wizard();
		w.setName("Humphry the Great");
		return w;
		
	}

	private void setName(String string) {
		name=string;
	}

	public void setNextRightHand(String actionCommand) {
		nextRightGest=actionCommand;
		
	}
	public void setNextLeftHand(String actionCommand) {
		nextLeftGest=actionCommand;
	}

	public void applyNextGestures() {
		rightHandPattern+=nextRightGest;
		leftHandPattern+=nextLeftGest;
	}

	public String getNextRightGesture() {
		return nextRightGest;
	}
	public String getNextLeftGesture() {
		return nextLeftGest;
	}

	public void setNextLeftSpell(String string) {
		nextLeftSpell=string;
	}

	public void setNextRightSpell(String string) {
		nextRightSpell=string;
	}
	
}
