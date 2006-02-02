
package aj.man;

import java.util.Vector;

import aj.misc.GmlPair;


public class Pos {
	static int count=1;

	int id;
	double headX,headY;
	double neckX,neckY;
	double relbX,relbY;
	double lelbX,lelbY;
	double rhandX,rhandY;
	double lhandX,lhandY;
	double waistX,waistY;
	double rkneeX,rkneeY;
	double lkneeX,lkneeY;
	double rfootX,rfootY;
	double lfootX,lfootY;
	
	public Pos(Pos p) {
		if (p==null) return;
		id=p.id;
		headX=p.headX;headY=p.headY;
		neckX=p.neckX;neckY=p.neckY;
		relbX=p.relbX;relbY=p.relbY;
		lelbX=p.lelbX;lelbY=p.lelbY;
		rhandX=p.rhandX;rhandY=p.rhandY;
		lhandX=p.lhandX;lhandY=p.lhandY;
		waistX=p.waistX;waistY=p.waistY;
		rkneeX=p.rkneeX;rkneeY=p.rkneeY;
		lkneeX=p.lkneeX;lkneeY=p.lkneeY;
		rfootX=p.rfootX;rfootY=p.rfootY;
		lfootX=p.lfootX;lfootY=p.lfootY;
	}

	private Pos() {
	}

	public String toSaveString() {
		if (id<1) {
			id=count;
			count++;
		}
		return toGmlPair().toString();
	}

	public GmlPair toGmlPair() {
		Vector v=new Vector();
		v.addElement(new GmlPair("id",id));
		Vector t=new Vector();
		t.addElement(new GmlPair("x",headX));
		t.addElement(new GmlPair("y",headY));
		v.addElement(new GmlPair("head",t));
		t=new Vector();
		t.addElement(new GmlPair("x",neckX));
		t.addElement(new GmlPair("y",neckY));
		v.addElement(new GmlPair("neck",t));
		t=new Vector();
		t.addElement(new GmlPair("x",relbX));
		t.addElement(new GmlPair("y",relbY));
		v.addElement(new GmlPair("relb",t));
		t=new Vector();
		t.addElement(new GmlPair("x",lelbX));
		t.addElement(new GmlPair("y",lelbY));
		v.addElement(new GmlPair("lelb",t));
		t=new Vector();
		t.addElement(new GmlPair("x",rhandX));
		t.addElement(new GmlPair("y",rhandY));
		v.addElement(new GmlPair("rhand",t));
		t=new Vector();
		t.addElement(new GmlPair("x",lhandX));
		t.addElement(new GmlPair("y",lhandY));
		v.addElement(new GmlPair("lhand",t));
		t=new Vector();
		t.addElement(new GmlPair("x",waistX));
		t.addElement(new GmlPair("y",waistY));
		v.addElement(new GmlPair("waist",t));
		t=new Vector();
		t.addElement(new GmlPair("x",rkneeX));
		t.addElement(new GmlPair("y",rkneeY));
		v.addElement(new GmlPair("rknee",t));
		t=new Vector();
		t.addElement(new GmlPair("x",lkneeX));
		t.addElement(new GmlPair("y",lkneeY));
		v.addElement(new GmlPair("lknee",t));
		t=new Vector();
		t.addElement(new GmlPair("x",rfootX));
		t.addElement(new GmlPair("y",rfootY));
		v.addElement(new GmlPair("rfoot",t));
		t=new Vector();
		t.addElement(new GmlPair("x",lfootX));
		t.addElement(new GmlPair("y",lfootY));
		v.addElement(new GmlPair("lfoot",t));
		t=new Vector();
		return new GmlPair("position",v);
	}

	public static Pos parse(GmlPair g) {
		Pos p=new Pos();
		GmlPair id=g.getOneByName("id");
		if (id!=null) p.id=(int)id.getDouble();
		if (p.id>=count) count=p.id+1;
		GmlPair h=g.getOneByName("head");
		if (h!=null) {
			p.headX=h.getOneByName("X").getDouble();
			p.headY=h.getOneByName("Y").getDouble();
		}
		h=g.getOneByName("neck");
		if (h!=null) {
			p.neckX=h.getOneByName("X").getDouble();
			p.neckY=h.getOneByName("Y").getDouble();
		}
		h=g.getOneByName("relb");
		if (h!=null) {
			p.relbX=h.getOneByName("X").getDouble();
			p.relbY=h.getOneByName("Y").getDouble();
		}
		h=g.getOneByName("lelb");
		if (h!=null) {
			p.lelbX=h.getOneByName("X").getDouble();
			p.lelbY=h.getOneByName("Y").getDouble();
		}
		h=g.getOneByName("lhand");
		if (h!=null) {
			p.lhandX=h.getOneByName("X").getDouble();
			p.lhandY=h.getOneByName("Y").getDouble();
		}
		h=g.getOneByName("rhand");
		if (h!=null) {
			p.rhandX=h.getOneByName("X").getDouble();
			p.rhandY=h.getOneByName("Y").getDouble();
		}
		h=g.getOneByName("waist");
		if (h!=null) {
			p.waistX=h.getOneByName("X").getDouble();
			p.waistY=h.getOneByName("Y").getDouble();
		}
		h=g.getOneByName("rknee");
		if (h!=null) {
			p.rkneeX=h.getOneByName("X").getDouble();
			p.rkneeY=h.getOneByName("Y").getDouble();
		}
		h=g.getOneByName("lknee");
		if (h!=null) {
			p.lkneeX=h.getOneByName("X").getDouble();
			p.lkneeY=h.getOneByName("Y").getDouble();
		}
		h=g.getOneByName("rfoot");
		if (h!=null) {
			p.rfootX=h.getOneByName("X").getDouble();
			p.rfootY=h.getOneByName("Y").getDouble();
		}
		h=g.getOneByName("lfoot");
		if (h!=null) {
			p.lfootX=h.getOneByName("X").getDouble();
			p.lfootY=h.getOneByName("Y").getDouble();
		}
		return p;
	}

	public static Pos between(Pos p1,Pos p2, double d) {
		Pos p=new Pos();
		p.id=0;

		p.headX= (p1. headX*d+p2.headX*(1-d));
		p.headY= (p1. headY*d+p2.headY*(1-d));

		p.neckX= (p1. neckX*d+p2.neckX*(1-d));
		p.neckY= (p1. neckY*d+p2.neckY*(1-d));

		p.relbX= (p1. relbX*d+p2.relbX*(1-d));
		p.relbY= (p1. relbY*d+p2.relbY*(1-d));

		p.lelbX= (p1. lelbX*d+p2.lelbX*(1-d));
		p.lelbY= (p1. lelbY*d+p2.lelbY*(1-d));

		p.rhandX=(p1.rhandX*d+p2.rhandX*(1-d));
		p.rhandY=(p1.rhandY*d+p2.rhandY*(1-d));

		p.lhandX=(p1.lhandX*d+p2.lhandX*(1-d));
		p.lhandY=(p1.lhandY*d+p2.lhandY*(1-d));

		p.waistX=(p1.waistX*d+p2.waistX*(1-d));
		p.waistY=(p1.waistY*d+p2.waistY*(1-d));

		p.rkneeX=(p1.rkneeX*d+p2.rkneeX*(1-d));
		p.rkneeY=(p1.rkneeY*d+p2.rkneeY*(1-d));

		p.lkneeX=(p1.lkneeX*d+p2.lkneeX*(1-d));
		p.lkneeY=(p1.lkneeY*d+p2.lkneeY*(1-d));

		p.lfootX=(p1.lfootX*d+p2.lfootX*(1-d));
		p.lfootY=(p1.lfootY*d+p2.lfootY*(1-d));

		p.rfootX=(p1.rfootX*d+p2.rfootX*(1-d));
		p.rfootY=(p1.rfootY*d+p2.rfootY*(1-d));
		p.fixLen();
		return p;
	}

	public static Pos between2(Pos p2,Pos p1, double d) {
		Pos p=new Pos();
		p.id=0;

		int headlen=4;
		int bodlen=16;
		int hiplen=13;
		int chinlen=11;
		int bicplen=10;
		int forarmlen=8;

		//double l=len(headX,headY,neckX,neckY);
		p.waistX=(p2.waistX*d+p1.waistX*(1-d));
		p.waistY=(p2.waistY*d+p1.waistY*(1-d));

		//l=len(waistX,waistY,neckX,neckY);
		double m1=mytan(p1.neckY-p1.waistY,p1.neckX-p1.waistX);
		double m2=mytan(p2.neckY-p2.waistY,p2.neckX-p2.waistX);
		double dm=m2-m1;
		if (dm>Math.PI) dm=dm-Math.PI*2;
		if (dm<-Math.PI) dm=dm+Math.PI*2;
		double m=m1+dm*d;
		p.neckX=(p.waistX+bodlen*Math.cos(m));
		p.neckY=(p.waistY+bodlen*Math.sin(m));

		m1=mytan(p1.headY-p1.neckY,p1.headX-p1.neckX);
		m2=mytan(p2.headY-p2.neckY,p2.headX-p2.neckX);
		dm=m2-m1;
		if (dm>Math.PI) dm=dm-Math.PI*2;
		if (dm<-Math.PI) dm=dm+Math.PI*2;
		m=m1+dm*d;
		p.headX=(p.neckX+headlen*Math.cos(m));
		p.headY=(p.neckY+headlen*Math.sin(m));

		m1=mytan(p1.relbY-p1.neckY,p1.relbX-p1.neckX);
		m2=mytan(p2.relbY-p2.neckY,p2.relbX-p2.neckX);
		dm=m2-m1;
		if (dm>Math.PI) dm=dm-Math.PI*2;
		if (dm<-Math.PI) dm=dm+Math.PI*2;
		m=m1+dm*d;
		p.relbX=(p.neckX+bicplen*Math.cos(m));
		p.relbY=(p.neckY+bicplen*Math.sin(m));

		m1=mytan(p1.lelbY-p1.neckY,p1.lelbX-p1.neckX);
		m2=mytan(p2.lelbY-p2.neckY,p2.lelbX-p2.neckX);
		dm=m2-m1;
		if (dm>Math.PI) dm=dm-Math.PI*2;
		if (dm<-Math.PI) dm=dm+Math.PI*2;
		m=m1+dm*d;
		p.lelbX=(p.neckX+bicplen*Math.cos(m));
		p.lelbY=(p.neckY+bicplen*Math.sin(m));

		m1=mytan(p1.rhandY-p1.relbY,p1.rhandX-p1.relbX);
		m2=mytan(p2.rhandY-p2.relbY,p2.rhandX-p2.relbX);
		dm=m2-m1;
		if (dm>Math.PI) dm=dm-Math.PI*2;
		if (dm<-Math.PI) dm=dm+Math.PI*2;
		m=m1+dm*d;
		p.rhandX=(p.relbX+forarmlen*Math.cos(m));
		p.rhandY=(p.relbY+forarmlen*Math.sin(m));

		m1=mytan(p1.lhandY-p1.lelbY,p1.lhandX-p1.lelbX);
		m2=mytan(p2.lhandY-p2.lelbY,p2.lhandX-p2.lelbX);
		dm=m2-m1;
		if (dm>Math.PI) dm=dm-Math.PI*2;
		if (dm<-Math.PI) dm=dm+Math.PI*2;
		m=m1+dm*d;
		p.lhandX=(p.lelbX+forarmlen*Math.cos(m));
		p.lhandY=(p.lelbY+forarmlen*Math.sin(m));

		m1=mytan(p1.rkneeY-p1.waistY,p1.rkneeX-p1.waistX);
		m2=mytan(p2.rkneeY-p2.waistY,p2.rkneeX-p2.waistX);
		dm=m2-m1;
		if (dm>Math.PI) dm=dm-Math.PI*2;
		if (dm<-Math.PI) dm=dm+Math.PI*2;
		m=m1+dm*d;
		p.rkneeX=(p.waistX+hiplen*Math.cos(m));
		p.rkneeY=(p.waistY+hiplen*Math.sin(m));

		m1=mytan(p1.lkneeY-p1.waistY,p1.lkneeX-p1.waistX);
		m2=mytan(p2.lkneeY-p2.waistY,p2.lkneeX-p2.waistX);
		dm=m2-m1;
		if (dm>Math.PI) dm=dm-Math.PI*2;
		if (dm<-Math.PI) dm=dm+Math.PI*2;
		m=m1+dm*d;
		p.lkneeX=(p.waistX+hiplen*Math.cos(m));
		p.lkneeY=(p.waistY+hiplen*Math.sin(m));

		m1=mytan(p1.lfootY-p1.lkneeY,p1.lfootX-p1.lkneeX);
		m2=mytan(p2.lfootY-p2.lkneeY,p2.lfootX-p2.lkneeX);
		dm=m2-m1;
		if (dm>Math.PI) dm=dm-Math.PI*2;
		if (dm<-Math.PI) dm=dm+Math.PI*2;
		m=m1+dm*d;
		p.lfootX=(p.lkneeX+chinlen*Math.cos(m));
		p.lfootY=(p.lkneeY+chinlen*Math.sin(m));

		m1=mytan(p1.rfootY-p1.rkneeY,p1.rfootX-p1.rkneeX);
		m2=mytan(p2.rfootY-p2.rkneeY,p2.rfootX-p2.rkneeX);
		dm=m2-m1;
		if (dm>Math.PI) dm=dm-Math.PI*2;
		if (dm<-Math.PI) dm=dm+Math.PI*2;
		m=m1+dm*d;
		p.rfootX=(p.rkneeX+chinlen*Math.cos(m));
		p.rfootY=(p.rkneeY+chinlen*Math.sin(m));
		return p;
	}


	public void fixLen() {
		int headlen=4;
		int bodlen=16;
		int hiplen=13;
		int chinlen=11;
		int bicplen=10;
		int forarmlen=8;

		double l=len(headX,headY,neckX,neckY);
		double m=mytan(headY-neckY,headX-neckX);
		headX=(neckX+headlen*Math.cos(m));
		headY=(neckY+headlen*Math.sin(m));
		l=len(relbX,relbY,neckX,neckY);
		m=mytan(relbY-neckY,relbX-neckX);
		relbX=(neckX+bicplen*Math.cos(m));
		relbY=(neckY+bicplen*Math.sin(m));

		l=len(lelbX,lelbY,neckX,neckY);
		m=mytan(lelbY-neckY,lelbX-neckX);
		lelbX=(neckX+bicplen*Math.cos(m));
		lelbY=(neckY+bicplen*Math.sin(m));

		l=len(lhandX,lhandY,lelbX,lelbY);
		m=mytan(lhandY-lelbY,lhandX-lelbX);
		lhandX=(lelbX+forarmlen*Math.cos(m));
		lhandY=(lelbY+forarmlen*Math.sin(m));

		l=len(rhandX,rhandY,relbX,relbY);
		m=mytan(rhandY-relbY,rhandX-relbX);
		rhandX=(relbX+forarmlen*Math.cos(m));
		rhandY=(relbY+forarmlen*Math.sin(m));

		l=len(waistX,waistY,neckX,neckY);
		m=mytan(waistY-neckY,waistX-neckX);
		waistX=(neckX+bodlen*Math.cos(m));
		waistY=(neckY+bodlen*Math.sin(m));

		l=len(rkneeX,rkneeY,waistX,waistY);
		m=mytan(rkneeY-waistY,rkneeX-waistX);
		rkneeX=(waistX+hiplen*Math.cos(m));
		rkneeY=(waistY+hiplen*Math.sin(m));

		l=len(lkneeX,lkneeY,waistX,waistY);
		m=mytan(lkneeY-waistY,lkneeX-waistX);
		lkneeX=(waistX+hiplen*Math.cos(m));
		lkneeY=(waistY+hiplen*Math.sin(m));

		l=len(lfootX,lfootY,lkneeX,lkneeY);
		m=mytan(lfootY-lkneeY,lfootX-lkneeX);
		lfootX=(lkneeX+chinlen*Math.cos(m));
		lfootY=(lkneeY+chinlen*Math.sin(m));

		l=len(rfootX,rfootY,rkneeX,rkneeY);
		m=mytan(rfootY-rkneeY,rfootX-rkneeX);
		rfootX=(rkneeX+chinlen*Math.cos(m));
		rfootY=(rkneeY+chinlen*Math.sin(m));
	}

	public static double mytan(double y,double x) {
		if (x==0 && y>0) return Math.PI/2;
		if (x==0 && y<=0) return -Math.PI/2;
		double res=Math.atan(1.0*y/x)+(x<0?Math.PI:0);
		if (res>Math.PI) res=res-Math.PI*2;
		if (res<-Math.PI) res=res+Math.PI*2;
		return res;
	}

	private double len(double x,double y,double x2,double y2) {
		return Math.pow((x-x2)*(x-x2)+(y-y2)*(y-y2),.5);
	}

	public String toString() {
		return " h("+headX+","+headY+") "+
		" n("+neckX+","+neckY+") "+
		" re("+relbX+","+relbY+") "+
		" le("+lelbX+","+lelbY+") "+
		" rh("+rhandX+","+rhandY+") "+
		" lh("+lhandX+","+lhandY+") "+
		" w("+waistX+","+waistY+") "+
		" rk("+rkneeX+","+rkneeY+") "+
		" lk("+lkneeX+","+lkneeY+") "+
		" rf("+rfootX+","+rfootY+") "+
		" lf("+lfootX+","+lfootY+") ";
	}
}
