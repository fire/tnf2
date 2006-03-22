package aj.games;

public class Bet {
	public static void main(String s[]) {
		int samples=100;
		double rounds=1000;
		double chance=1-1.0/36;
		double payRate=35;
		double addRate=.01;

		for (int b=0;b<samples;b++) {
			double bank=10000;
			double startBet=1;
			double currBet=startBet;
			double numBet=0;
			double totalBet=0;
			double averageBet=0;
			double maxBet=0;
	
			boolean loseDouble=false;
			boolean loseAddRate=true;
			boolean loseAddOne=false;
			boolean loseTwoStart=false;
			boolean winReset=true;

			boolean busted=false;
			for (int a=0;a<rounds;a++) {
				numBet++;
				totalBet+=currBet;
				if (currBet>bank) {busted=true;break;}
				if (Math.random()>chance) {
					bank+=currBet*payRate;
					if (winReset) currBet=startBet;
				}
				else {
					bank-=currBet;
					if (loseDouble) currBet*=2;
					if (loseAddRate) currBet=Math.max(currBet+1,currBet+currBet*addRate);
					if (loseAddOne) currBet=currBet+1;
					if (loseTwoStart) currBet=startBet*2;
				}
				maxBet=Math.max(maxBet,currBet);
			}
			averageBet=totalBet/numBet;
			System.out.println("bank="+bank+" numBet="+numBet+" averageBet="+averageBet+" maxBet="+maxBet+" "+(busted?"Busted!":""));
		}
	}
}
