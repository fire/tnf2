
package aj.gnutella;


class Push extends Header {

	String servantid;
	int fileIndex;
	String ip;
	int port;

	public Push(byte[] b,byte[] payload) {
		super(b,payload);
		if (payload.length>15) {
			servantid=new String(payload,0,16);
		}
		fileIndex=fixByte(payload[16],payload[17],payload[18],payload[19]);
		ip=fixByte(payload[16])+"."+fixByte(payload[17])+"."+fixByte(payload[18])+"."+fixByte(payload[19]);
		port=fixByte(payload[24])+fixByte(payload[25])*256;
	}

	public String toString() {
		return "Push "+fileIndex+" to "+ip+":"+port+" requested by sId="+aj.io.Encode.encodeString(servantid,"0123456789ABCDEF").substring(17);
	}
}
