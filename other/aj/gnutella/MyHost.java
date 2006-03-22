package aj.gnutella;

import java.io.Serializable;

import aj.misc.Stuff;

public class MyHost implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int LOSTRECONNECTTIME=6000;
	static int RECONNECTTIME=60000;
	static int RETRYFAILTIME=30000;
	static int NEVERCONNECTED=2000;

	byte ipb[]=new byte[4];
	int port=0;

	int totalConnectAttempts=0;
	double successRate=1;
	int longestConnect=0;
	long lastTry=0;

	int connectLost=0;
	int connectCount=0;
	int connectFailCount=0;
	int numshareFiles=0;
	int numshareMegabytes=0;
	int numhops=-1;
	long lastSeenTime=0;
	long lastFailConnectTime=0;
	long lastConnectTime=0;
	long lastDisconnectTime=0;
	long lastConnectLostTime=0;

	public String toSaveString() {
		return getIp()+":"+port+" "+totalConnectAttempts+" "+Stuff.trunc(successRate,2)+" "+longestConnect+" "+lastTry;
	}


	public static MyHost parse(String s) {
		String tt[]=aj.misc.Stuff.getTokens(s,": ");
		if (tt.length<2) return null;
		try {
			String ip=tt[0];
			int port=Integer.parseInt(tt[1]);
			if (tt.length==6) {
				int att=Integer.parseInt(tt[2]);
				double rate=Double.parseDouble(tt[3]);
				int ctime=Integer.parseInt(tt[4]);
				long last=Long.parseLong(tt[5]);
				return new MyHost(ip,port,att,rate,ctime,last);
			}
			else if (tt.length==4) {
				int cr=Integer.parseInt(tt[2]);
				int ctime=Integer.parseInt(tt[3]);
				return new MyHost(ip,port,cr,ctime);
			}
		} catch (NumberFormatException nfe) {
		}
		return null;
	}

	public MyHost(String ip,int port,int att, double rate, int ctime,long last) {
		setIp(ip);
		this.port=port;
		connectCount=(int)(att*rate);
		connectFailCount=(int)(att*(1-rate));
		lastConnectTime=last;
		lastDisconnectTime=last;
		totalConnectAttempts=att;
		successRate=rate;
		longestConnect=ctime;
		lastTry=last;
	}

	public MyHost(String ip,int port,int cr, int ctime) {
		setIp(ip);
		this.port=port;
		if (cr>0) connectCount=cr;
		else if (cr<0) connectFailCount=-cr;

		long currtime=System.currentTimeMillis();
		if (cr>0) {
			lastConnectTime=currtime-100000;
			lastDisconnectTime=currtime-100000+ctime*1000;
		}
	}

	public void setIp(String i) {
		if (i==null) i="";
		if (i.indexOf("/")>=0) i=i.substring(i.indexOf("/"));
		String s[]=aj.misc.Stuff.getTokens(i,".");
		if (s.length!=4) return;
		for (int a=0;a<4;a++) {
			try {
				ipb[a]=(byte)Integer.parseInt(s[a]);
			} catch (NumberFormatException nfe) {
				ipb[a]=0;
			}
		}
	}

	public String getIp() {
		return Header.fixByte(ipb[0])+"."+Header.fixByte(ipb[1])+"."+Header.fixByte(ipb[2])+"."+Header.fixByte(ipb[3]);
	}

	public boolean tryConnect() {
		long currtime=System.currentTimeMillis();
		if (lastConnectTime+RECONNECTTIME>currtime) {
			return false;
		}
		if (lastConnectLostTime+LOSTRECONNECTTIME>currtime) {
			return false;
		}
		if (lastFailConnectTime+RETRYFAILTIME>currtime) {
			return false;
		}
		if ((lastConnectTime!=0 && lastFailConnectTime!=0 && lastConnectLostTime!=0) || lastSeenTime+NEVERCONNECTED>currtime) {
			return false;
		}
		return true;
	}
	static int MAXRATE=5;

	public boolean badRate() {
		if  (totalConnectAttempts>5) {
			if (successRate<.25) return true;
			if (longestConnect<10) return true;
		}
		return false;
	}

	public int getRate() {
		if (connectCount-connectFailCount>MAXRATE) return MAXRATE;
		else if (connectCount-connectFailCount<-1*MAXRATE) return -1*MAXRATE;
		else return (connectCount-connectFailCount)%MAXRATE;
	}

	public void goodConnect() {
		lastConnectTime=System.currentTimeMillis();
		lastDisconnectTime=lastConnectTime-2;
		connectCount++;

		lastTry=System.currentTimeMillis();
		successRate=(successRate*totalConnectAttempts+1)/(totalConnectAttempts+1);
		totalConnectAttempts++;
	}
	
	public void failedConnect() {
		lastFailConnectTime=System.currentTimeMillis();
		connectFailCount++;
		lastTry=System.currentTimeMillis();
		successRate=(successRate*totalConnectAttempts)/(totalConnectAttempts+1);
		totalConnectAttempts++;
	}

	static int MINCONNECTTIMEFORLOST=2000;

	public void connectLost() {
		lastDisconnectTime=lastConnectLostTime=System.currentTimeMillis();
		connectLost++;
		if (lastDisconnectTime+MINCONNECTTIMEFORLOST<lastConnectTime) failedConnect();
		else {
			int t=(int)((lastDisconnectTime-lastConnectTime)/1000);
			longestConnect=Math.max(longestConnect,t);
			lastTry=System.currentTimeMillis();
			successRate=(successRate*totalConnectAttempts+1)/(totalConnectAttempts+1);
			totalConnectAttempts++;
		}
	}


	public void foundPong(Pong p) {
		lastSeenTime=System.currentTimeMillis();
		if (p!=null ){
			numhops=p.hops;	
			numshareFiles=p.numshareFiles;
			numshareMegabytes=p.numshareKilobytes/1000;
		}
	}

	public MyHost(String i,int por,Pong p) {
		lastSeenTime=System.currentTimeMillis();//same as pong
		if (p!=null ){
			numshareFiles=p.numshareFiles;
			numshareMegabytes=p.numshareKilobytes/1000;
		}
		if (i==null) i="";
		if (i.indexOf("/")>=0) {
			i=i.substring(i.indexOf("/")+1);
		}
		port=por;
		String s[]=aj.misc.Stuff.getTokens(i,".");
		if (s.length<4) {
			System.out.println("bad ip "+i);
			ipb[0]=0;ipb[1]=0;ipb[2]=0;ipb[3]=0;
			return;
		}
		for (int a=0;a<4;a++) {
			try {
				ipb[a]=(byte)Integer.parseInt(s[a]);
			} catch (NumberFormatException nfe) {
				System.out.println("Bad number in IP position "+a+" of "+s[a]+" in ip of "+i);
				ipb[a]=0;
			}
		}
	}

	public String toString() {
		long maxtime=Math.max(lastConnectTime,Math.max(lastFailConnectTime,lastConnectLostTime));
		String vis="";
		long dismaxtime=(System.currentTimeMillis()-maxtime)/1000;
		if (maxtime<lastSeenTime && lastConnectTime==0 && lastFailConnectTime==0 && lastConnectLostTime==0 ) {
			vis="Never Age 0";
		}
		else if (maxtime==lastConnectLostTime) vis="LOST Age "+dismaxtime;
		else if (maxtime==lastConnectTime) vis="Recent Age "+dismaxtime;
		else if (maxtime==lastFailConnectTime) vis="FAILED Age "+dismaxtime;
		String share="files:"+numshareFiles+" at "+numshareMegabytes+"MB";
		if (numshareFiles==0) share="";
		if (numhops>0) share=" hops:"+numhops+" "+share;
		return getIp()+":"+port+" "+vis+" connect ("+connectCount+"/"+connectFailCount+") "+share;
	}
	
	public boolean equals(MyHost mh) {
		for (int a=0;a<4;a++) {
			if (ipb[a]!=mh.ipb[a]) return false;
		}
		return true;
	}
}

