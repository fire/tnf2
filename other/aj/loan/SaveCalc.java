package aj.loan;
import aj.misc.Stuff;


public class SaveCalc  {
	public static void main (String s[]) {
		if (s.length<6) {
System.out.println(" <start ball> <interest rate> <mon dep> <rate dep increase> <inflat> <num months |  -years>");
			return;
		}
		double bal=Double.parseDouble(s[0]);
		double rate=Double.parseDouble(s[1]);
		rate=rate/100.0/12;
		double dep=Double.parseDouble(s[2]);
		double depRate=Double.parseDouble(s[3]);
		depRate=depRate/100.0/12;
		double inf=Double.parseDouble(s[4]);
		inf=inf/100.0/12;
		int mon=Integer.parseInt(s[5]);
		if (mon<0) {mon=mon*-12;}
		double totalDep=0;
System.out.println("Begin Ball="+bal+" rate="+Stuff.trunc(rate*12*100,2)+"%/year dep="+dep);
//System.out.println("Begin "+0+" months Bal="+Stuff.money(bal,2)+" dep="+Stuff.money(dep,2)+" totalDep="+Stuff.money(totalDep,2));
		for (int a=0;a<mon;a++) {
			dep+=dep*(depRate-inf);
			totalDep+=dep;
			bal+=bal*(rate-inf)+dep;
if (a%12==0) System.out.println(a+" months Bal="+Stuff.money(bal,2)+" dep="+Stuff.money(dep,2)+" totalDep="+Stuff.money(totalDep,2));
		}
System.out.println("Final "+mon+" months Bal="+Stuff.money(bal,2)+" dep="+Stuff.money(dep,2)+" totalDep="+Stuff.money(totalDep,2));
	}
}

