package aj.gnutella;

public class Header {
	byte b[];

	byte payload[];

	String desId;

	byte payloadType;

	byte ttl = 0;

	byte hops = 0;

	int payloadLength = 0;

	static byte MAXTTL = 10;// adjust incomming ttl and hops

	static int numTTLFixes = 0;

	public Header(byte[] bb, byte[] ppayload) {
		b = new byte[bb.length];
		payload = new byte[ppayload.length];
		for (int a = 0; a < bb.length; a++)
			b[a] = bb[a];
		for (int a = 0; a < payload.length; a++)
			payload[a] = ppayload[a];
		desId = new String(b, 0, 16);
		payloadLength = (b[19] < 0 ? 256 + b[19] : b[19])
				+ (b[20] < 0 ? 256 + b[20] : b[20]) * 256
				+ (b[21] < 0 ? 256 + b[21] : b[21]) * 256 * 256
				+ (b[22] < 0 ? 256 + b[22] : b[22]) * 256 * 256 * 256;
		payloadType = b[16];
		ttl = b[17];
		hops = b[18];
		if (ttl + hops > MAXTTL) {
			numTTLFixes++;
			// System.out.println("Fixing MAXTTL hops:"+hops+"+
			// ttl:"+ttl+"="+(hops+ttl)+" to "+MAXTTL);
			ttl = (byte) (MAXTTL - hops);
			if (ttl < 0) {
				hops = (byte) (hops - ttl);
				ttl = 0;
			}
		}
	}

	public Header forward() {
		Header p = new Header(b, payload);
		p.b[17]--;
		p.ttl = (byte) (this.ttl - 1);
		// if (p.ttl<1) return null;
		p.b[18]++;
		p.hops = (byte) (this.hops + 1);
		return p;
	}

	public boolean equals(Header h) {
		if (h.payloadType != payloadType || h.desId != desId
				|| h.payload.length != payload.length)
			return false;
		for (int a = 0; a < payload.length; a++) {
			if (payload[a] != h.payload[a])
				return false;
		}
		return true;
	}

	public static int fixByte(byte d) {
		return (d < 0 ? 256 + d : 0 + d);
	}

	public static int fixByte(byte a, byte b, byte c, byte d) {
		return fixByte(a) + 256 * fixByte(b) + 256 * 256 * fixByte(c) + 256
				* 256 * 256 * fixByte(d);
	}

	public String toHex() {
		return "Hex 0x"
				+ aj.io.Encode.encodeString(new String(b), "0123456789ABCDEF")
						.substring(17)
				+ " "
				+ aj.io.Encode.encodeString(new String(payload),
						"0123456789ABCDEF").substring(17);
	}
}
