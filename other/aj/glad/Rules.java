package aj.glad;

/**
 * Rules of arena and challenges. Allow killing, weapon types etc. need to join
 * rules.
 * 
 * @author judda
 * @created April 12, 2000
 */

public class Rules {
	// weapons allowed S,C,P,HTH (and)
	// weapons assigned S,C,P,HTH (and)
	// teams okay T/F (and)
	// number of teams allowed 2-# (min)
	// uneven teams allowed T/F (and)
	// require preformance +/- X% # (min)
	// allow death T/F (and)
	// require death T/F (ARENA)
	// auto surender # (ARENA)
	// challenge has rules
	// arena has rules
	//
	// union rules = least common denominator
	// challenge WAL=S,C,P,HTH WAS= T=F N=2 UT=F RP=5 RD=T
	// arena WAL=S WAS=S T=T N=5 UT=T RP=100 RD=F
	// result WAL=S WAS=S T=F N=2 UT=F RP=5
}
