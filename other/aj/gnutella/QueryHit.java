package aj.gnutella;

import java.util.Vector;

class QueryHit extends Header {

	int numHits = 0;

	int port = 0;

	String ipaddress = "";

	int speed = 0;

	Vector results = new Vector();

	String servantid = "";

	GnuProtocol gp;

	public QueryHit(byte[] b, byte[] payload, GnuProtocol gpp) {
		super(b, payload);
		gp = gpp;
		int payload2[] = new int[payload.length];
		for (int a = 0; a < payload.length; a++) {
			if (payload[a] < 0)
				payload2[a] = 256 + payload[a];
			else
				payload2[a] = payload[a];
		}
		numHits = payload2[0];
		port = payload2[1] + 256 * payload2[2];
		ipaddress = payload2[3] + "." + payload2[4] + "." + payload2[5] + "."
				+ payload2[6];
		speed = fixByte(payload[7], payload[8], payload[9], payload[10]);
		servantid = new String(payload, payload.length - 16, 16);
		int offset = 11;
		for (int a = 0; a < numHits; a++) {
			if (offset >= payload.length - 8) {
				System.out.println("Query Hit problem.  Bad reslut set");
				// System.out.println(aj.io.Encode.encodeString(new
				// String(payload),"0123456789ABCDEF"));
				break;
			}
			int fileindex = fixByte(payload[offset + 0], payload[offset + 1],
					payload[offset + 2], payload[offset + 3]);
			int filesize = fixByte(payload[offset + 4], payload[offset + 5],
					payload[offset + 6], payload[offset + 7]);
			offset += 8;
			String name = "";
			String other = "";
			int nullcount = 0;
			while (offset < payload.length - 2
					&& !(payload2[offset] == 0 && payload2[offset + 1] == 0)) {
				if (payload2[offset] == 0)
					nullcount++;
				else if (nullcount == 0)
					name += (char) payload2[offset];
				else {
					other += (char) payload2[offset];
				}
				offset++;
			}
			QueryResult qq = new QueryResult(fileindex, filesize, name, other);
			results.addElement(qq);
		}
	}

	public String toString() {
		String rs = "QueryHit "
				+ results.size()
				+ " of "
				+ numHits
				+ " results at "
				+ ipaddress
				+ ":"
				+ port
				+ " speed of "
				+ speed
				+ " to sId "
				+ aj.io.Encode.encodeString(servantid, "0123456789ABCDEF")
						.substring(17)
				+ " Reply to "
				+ aj.io.Encode.encodeString(desId, "0123456789ABCDEF")
						.substring(17);
		for (int a = 0; a < results.size(); a++) {
			rs += "\n";
			rs += "  " + results.elementAt(a);
		}
		return rs;
	}

}

class QueryResult {
	int fileid;

	int filesize;

	String filename;

	String other;

	public QueryResult(int id, int siz, String na, String ot) {
		fileid = id;
		filesize = siz;
		filename = na;
		other = ot;
	}

	public String toString() {
		return "id=" + fileid + " size=" + filesize + " name=" + filename
				+ (other.length() > 0 ? " *other avail" : "");
	}
}
