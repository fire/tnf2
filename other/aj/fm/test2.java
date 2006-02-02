package aj.fm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class test2 {
	public static void main(String ssss[]) {
		try {
			BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
			Vector v=new Vector();
			String all="";
			while (true) {
				String s=br.readLine();
				if (s==null) break;
				v.addElement(s.trim());
				if (s.endsWith("*")) {
					all+=" node [name "+s.substring(0,s.length()-1)+" color red ] ";
				}
				else {
					all+=" node [name "+s+" color green ] ";
				}
			}
			for (int a=0;a<v.size();a++) {
				String p=v.elementAt(a).toString();
				if (!p.endsWith("*")) continue;
				p=p.substring(0,p.length()-1);
				for (int b=0;b<v.size();b++) {
					if (a==b) continue;
					String o=v.elementAt(b).toString();
					if (o.indexOf(p)==0) {
						all+=" link [ fromnode "+p+" tonode "+o+" ]";
//System.out.println("Compair "+p+" to "+o+" !!");
					}
//else System.out.println("Compair "+p+" to "+o+" fail");
				}
			}
			System.out.println(all);
		} catch (IOException ioe) {}
	}
}


/*
find roots.

starting at B how do you get to each spell

roots
PDFSWc

one hand roots,
	shows possible branches

two hand roots?
	show what can be done at the same time


spell interactions
	immune = protect = defeat
	A > B
	C > D

Shield(1) immune Stab(1)
Shield(1) immune MagicMissle(2)
Shield(1) immune MonsterAttack(3-6)
MagicMirror(2,2) immune FingerOfDeath(7)
ResistHeat(3) immune FireBall
ResistHeat(3) immune FireStorm
FireStorm immune FireElemental
Protection immune Stab(1)


*/
