
package aj.gnutella;


class Query extends Header {
	int minspeed=0;
	String criteria="";

	GnuProtocol gp;
	public Query(byte[] b,byte[] payload,GnuProtocol gpp) {
		super(b,payload);
		gp=gpp;
		int payload2[]=new int[payload.length];
		for (int a=0;a<payload.length;a++) {
			if (payload[a]<0) payload2[a]=256+payload[a];
			else payload2[a]=payload[a];
		}
		if (payload.length>1) {
			minspeed=payload2[0]+256*payload2[1];
			criteria=new String(payload,2,payload.length-3);
		}
	}
	public String toString() {
		return "Query <"+criteria+">"+(minspeed>0?" minspeed of "+minspeed:"")+" desId="+aj.io.Encode.encodeString(desId,"0123456789ABCDEF").substring(17);
	}
}
