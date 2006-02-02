
package aj.gnutella;


public class Pong extends Header{

	int port;//2 bytes
	String ip;//4 bytes
	int numshareFiles;//4 bytes
	int numshareKilobytes;//4 bytes in kbytes

	GnuProtocol gp;
	public Pong(byte[] b,byte[] payload,GnuProtocol gpp) {
		super(b,payload);
		gp=gpp;
		int payload2[]=new int[payload.length];
		for (int a=0;a<payload.length;a++) {
			if (payload[a]<0) payload2[a]=256+payload[a];
			else payload2[a]=payload[a];
		}
		port=payload2[0]+payload2[1]*256;
		ip=payload2[2]+"."+payload2[3]+"."+payload2[4]+"."+payload2[5];
		numshareFiles=payload2[6]+payload2[7]*256+payload2[8]*256*256+payload2[9]*256*256*256;
		numshareKilobytes=payload2[10]+payload2[11]*256+payload2[12]*256*256+payload2[13]*256*256*256;
	}

	public String toString() {
		return "Pong "+ip+":"+port+" sharing "+numshareKilobytes+"K bytes in "+numshareFiles+" files.  Reply to desId="+aj.io.Encode.encodeString(desId,"0123456789ABCDEF").substring(17);
	}
}

