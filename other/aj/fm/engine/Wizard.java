package aj.fm.engine;

public class Wizard {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static final int maxHitPoints=14;
	
	private int currHitPoints=maxHitPoints;
	
	private String rightTarget,leftTarget;
	
	private String nextRightGest,nextLeftGest;
	
	private String rightHandPattern = "";

	private String leftHandPattern = "";
	
	private String name;
	
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
	
}
