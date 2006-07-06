package aj.iem;

public class IEMOrder {
	boolean isBid = false;

	private String link;

	private Contract cc;

	int quant;

	int val;

	private double bval;

	public void setBundleVal(double d) {
		bval = d;
	}

	public IEMOrder(boolean ib, Contract c, int v, int q) {
		isBid = ib;
		cc = c;
		quant = q;
		val = v;
	}

	void setLink(String l) {
		link = l;
	}

	public String toString() {
		return "IEMOrder " + (isBid ? "BID" : "ASK") + " for " + cc.getName()
				+ " for " + quant + " at " + val;
	}

	void executeCancel(String s) {
		if (val == 0 || val == 1000)
			return;
		IEMTool.readAllSocket("GET /webex/" + link + " HTTP/1.0\r\n\r\n", "",
				"iemweb.biz.uiowa.edu", 80);
	}

	boolean zeroRiskBid() {
		if (isBid && val <= 1)
			return true;
		return false;
	}

	boolean zeroRiskAsk() {
		if (!isBid && val >= 999 + (bval > 1 ? 1000 : 0))
			return true;
		return false;
	}
}
