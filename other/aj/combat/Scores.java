package aj.combat;


public class Scores {

	private String opponents[]={"Asteroid"};
	private int myKillsCount[]={0};
	private int myDeathsCount[]={0};
	
	private String playerName;
	
	public Scores(String playerName) {
		this.playerName=playerName;
	}
	
	public void addDeath(String string) {
		System.out.println("add death found");
		boolean found=false;
		String t[]=new String[opponents.length+1];
		int c[]=new int[opponents.length+1];
		int k[]=new int[opponents.length+1];
		for (int a=0;a<opponents.length;a++) {
			t[a]=opponents[a];
			c[a]=myDeathsCount[a];
			k[a]=myKillsCount[a];
			if (opponents[a].equalsIgnoreCase(string)) {
				myDeathsCount[a]++;
				found=true;
				break;
			}
		}
		if (!found) {
			opponents=t;
			myDeathsCount=c;
			myKillsCount=k;
			opponents[opponents.length-1]=string;
			myDeathsCount[opponents.length-1]=1;
		}
	}

	public void addKill(String string) {
		boolean found=false;
		String t[]=new String[opponents.length+1];
		int c[]=new int[opponents.length+1];
		int k[]=new int[opponents.length+1];
		for (int a=0;a<opponents.length;a++) {
			t[a]=opponents[a];
			c[a]=myKillsCount[a];
			k[a]=myDeathsCount[a];
			if (opponents[a].equalsIgnoreCase(string)) {
				myKillsCount[a]++;
				found=true;
				break;
			}
		}
		if (!found) {
			opponents=t;
			myKillsCount=c;
			myDeathsCount=k;
			opponents[opponents.length-1]=string;
			myKillsCount[opponents.length-1]=1;
		}
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public String getScoreString() {
		String res="\nScores\n";
		for (int a=0;a<opponents.length;a++) {
			int k=myKillsCount[a];
			int d=myDeathsCount[a];
			res+=opponents[a]+" "+(k<10?"0":"")+k+"/"+(d<10?"0":"")+d+"\n";
		}
		return res;
	}

	public String getPlayerName() {
		return playerName;
	}

}
