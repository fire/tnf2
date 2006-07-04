package aj.gnutella;

public class Ping extends Header {

	GnuProtocol gp;

	public Ping(byte[] b, byte[] payload, GnuProtocol gpp) {
		super(b, payload);
		gp = gpp;
	}

	public String toString() {
		return "Ping desId=0x"
				+ aj.io.Encode.encodeString(desId, "0123456789ABCDEF")
						.substring(17);
	}
}
