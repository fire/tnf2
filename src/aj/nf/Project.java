package aj.nf;

import java.util.Vector;

/**
 *
 *@author     judda 
 *@created    July 21, 2000 
 */

public class Project {
	static int num=0;

	int cost,paid;
	ITThing itt;
	int id;
	boolean ready=true;

	public String getName() {return itt.getName();}

	public Project(int cost, int paid, ITThing itt) {
		num++;
		id=num;
		this.cost=cost;
		this.paid=paid;
		this.itt=itt;
		ready=false;
	}

	public Project(int id, int cost, int paid, ITThing itt) {
		if (id>=num) num=id+1;
		this.id=id;
		this.cost=cost;
		this.paid=paid;
		this.itt=itt;
	}

	public boolean pending() {return paid<cost;}

	public String getId() {return "P"+id;}

	public String getReport(String tick) {
		String s= "Project:"+getId()+" paid("+paid+"/"+cost+") ";
		if (itt instanceof Tech) {
			s+="Tech "+itt.getName();
		}
		else {
			s+=itt.display(tick);	
		}
		return s;
	}


	public String toSaveString() {
		return toGmlPair().toString();
	}
	public GmlPair toGmlPair() {
		Vector v = new Vector();
		v.addElement(new GmlPair("cost", cost+""));
		v.addElement(new GmlPair("paid", paid+"" ));
		v.addElement(new GmlPair("projid", id+"" ));
		v.addElement(itt.toGmlPair());
		return new GmlPair("project", v);
	}

	public static Project parse(GmlPair g) {
		GmlPair n= g.getOneByName("cost");
		int cost=0;
		if (n!=null) cost=(int)n.getDouble();
		n= g.getOneByName("paid");
		int paid=0;
		if (n!=null) paid=(int)n.getDouble();
		n= g.getOneByName("projid");
		int projid=0;
		if (n!=null) projid=(int)n.getDouble();

		ITThing itt=null;
		n= g.getOneByName("Tech");
		if (n!=null) itt=Tech.parse(n);
		n= g.getOneByName("ActiveDesign");
		if (n!=null) itt=ActiveDesign.parse(n);
		n= g.getOneByName("FacilityDesign");
		if (n!=null) itt=FacilityDesign.parse(n);

		return new Project(projid,cost,paid,itt);
	}

	public boolean isReady(){return ready;}	

	public void pay(String ticker) {
		paid++;
		ready=false;
		if (paid>=cost) {
			Corp c=Universe.getCorpByTick(ticker);
			if (c!=null) 
				c.addReport("Project finished "+getReport(ticker)+"\n");
			if (itt instanceof Tech) {
				Tech t=(Tech)Universe.getITThingByName(itt.getName());
				t.addPartner(ticker);
			}
			else {
				Universe.register(itt);
			}
		}
	}

}

