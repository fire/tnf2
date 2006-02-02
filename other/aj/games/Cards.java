package aj.games;

public class Cards {
	boolean dumb=true;//play don't bust only	

	public static void main(String s[]) {
		Cards c=new Cards();
		c.play();
	}

	public void play() {
		int dealerWin=0;
		int hands=0;
		int numberHandsToPlay=10000;
		for (int a=0;a<numberHandsToPlay;a++) {
			hands++;
			int dealerCards[]=new int [12];
			int playerCards[]=new int [12];
			dealerCards[0]=getCard();
			dealerCards[1]=getCard();
			playerCards[0]=getCard();
			playerCards[1]=getCard();
			int dealerCardCount=2;
			int playerCardCount=2;
			if (!playerStand(playerCards,dealerCards[1])) {
				if (playerDouble(playerCards,dealerCards[1])) {
					playerCards[playerCardCount]=getCard();
					playerCardCount=3;
				}
				else if (playerSplit(playerCards,dealerCards[1])) {
				}
				else {
					while (playerHit(playerCards,dealerCards[1])) {
						playerCards[playerCardCount]=getCard();
						playerCardCount++;
					}
				}
			}
			while (dealerMustHit(dealerCards)) {
				dealerCards[dealerCardCount]=getCard();
				dealerCardCount++;
			}
			if (firstHandWins(dealerCards,playerCards)) {
				dealerWin++;
			}
			System.out.println("Player= ("+display(playerCards)+")="+scoreHand(playerCards)+" Dealer=("+display(dealerCards)+")="+scoreHand(dealerCards)+" "+(firstHandWins(dealerCards,playerCards)?"DW":"PW"));
		}
		System.out.println("Games Played="+hands+" dealerWins="+dealerWin);
	}

	public boolean dealerMustHit(int c[]) {
		if (scoreHand(c)<16) return true;
		return false;
	}

	public boolean haveAce(int c[]) {
		for (int a=0;a<c.length;a++) {
			if (c[a]==11) return true;
		}
		return false;
	}

	public int scoreHand(int c[]) {
		int total=0;
		boolean haveAce=false;
		for (int a=0;a<c.length;a++) {
			if (c[a]<11) total+=c[a];
			else if (c[a]==11) {total+=1;haveAce=true;}
			else  {
				total+=10;
			}
		}
		if (total<=11 && haveAce) total+=10;
		return total;
	}

	public boolean playerStand(int c[],int d) {
		int sc=scoreHand(c);
		if (dumb) {
			if (sc>11) return true;
			else if (sc<=11) return false;
		}
		boolean ha=haveAce(c);
		//if (sc>11) return true;
		if (sc>18) return true;
		if (sc==18 && d==7) return true;
		if (!ha) {
			if (sc==12 && d>3 && d<7) return true;
			if (sc>13 && sc<17 && d<7) return true;
			if (sc>=17) return true;
		}
		else {
			if (sc==18 && (d==2 || d==6 || d==7)) return true;
		}
		//if (scoreHand(c)>16) return true;
		//if (d<=6 && scoreHand(c)<=17) return true;
		return false;
	}

	public boolean playerDouble(int c[],int d) {
		int sc=scoreHand(c);
		boolean ha=haveAce(c);
		if (dumb) {
			if (sc<=11) return true;
			if (sc>11) return false;
		}
		if (!ha && sc==9 && d>2 && d<7) return true;
		if (!ha && sc==10 && d<10) return true;
		if (!ha && sc==11 && d<11) return true;
		if (ha && sc==13 && d>4 && d<7) return true;
		if (ha && sc==14 && d>4 && d<7) return true;
		if (ha && sc==15 && d>3 && d<7) return true;
		if (ha && sc==16 && d>3 && d<7) return true;
		if (ha && sc==17 && d>2 && d<7) return true;
		if (ha && sc==18 && d>2 && d<7) return true;
		if (sc==10 && d<10) return true;
		return false;
	}
	public boolean playerSplit(int c[],int d) {
		return false;
	}
	public boolean playerHit(int c[],int d) {
		int sc=scoreHand(c);
		boolean ha=haveAce(c);
		if (dumb) {
			if (sc<=11) return true;
			if (sc>11) return false;
		}
		if (sc<=11) return true;
		if (!ha && sc==12 && (d==2 || d==3 || d>6)) return true;
		if (!ha && sc==13 && d>6) return true;
		if (ha && sc==12 && d!=5 && d!=6) return true;
		if (ha && sc==13 && d!=5 && d!=6) return true;
		if (ha && sc==14 && d!=4 && d!=5 && d!=6) return true;
		if (ha && sc==15 && d!=4 && d!=5 && d!=6) return true;
		if (ha && sc==16 && d!=4 && d!=5 && d!=6) return true;
		if (ha && sc==17 && (d==2 || d>6)) return true;
		if (ha && sc==18 && d>9) return true;
		//int myscore=scoreHand(c);
		//if (myscore<=11) return true;
		//if (d<7) return false;
		//if (d>7 && myscore<15) return true;
		return false;
	}
	public boolean firstHandWins(int c[],int d[]) {
		if (scoreHand(c)<=21 && (scoreHand(d)<scoreHand(c) || scoreHand(d)>21)) return true;
		return false;
	}
	
	int allCards[] =null;
	int next=-1;
	public int getCard() {
		if (allCards==null) {
			allCards=new int[52];
			for (int a=0;a<13;a++) {
				allCards[a]=a+1;
				allCards[a+13]=a+1;
				allCards[a+13*2]=a+1;
				allCards[a+13*3]=a+1;
			}
			next=-1;
		}
		if (next==-1) {//shuffel
			for (int a=0;a<500;a++) {
				int x=(int)(Math.random()*52);
				int y=(int)(Math.random()*52);
				int t=allCards[x];
				allCards[x]=allCards[y];
				allCards[y]=t;
			}
			next=0;
		}
		if (next<51){next++;return allCards[next-1];}
		else {
			next=-1;
			return allCards[51];
		}
	}
	public String display(int c[]) {
		String r="";
		for (int a=0;a<c.length;a++) {
			if (c[a]==0) {
				if (a==2) r+=" S";
				if (a>2) r+=" H";
				if (scoreHand(c)>21) r+="B";
				break;
			}
			if (r.length()>0) r+=" ";
			if (c[a]<11) r+=c[a];
			else if (c[a]==11) r+="A";
			else if (c[a]==12) r+="J";
			else if (c[a]==13) r+="Q";
			else if (c[a]==14) r+="K";
		}
		return r;
	}
}
/*
table
 delear
 players[]

game
  deck
  hands

hand
  player
  bet
  cards

player
  bank
  hand
*/
