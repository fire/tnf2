package aj.nf;


/**
 *
 *@author     judda 
 *@created    July 21, 2000 
 */
public interface ITThing {
	
	public String getName();
	
	public boolean isPublic();
	
	public boolean isPartner(String tic);
	
	public void addPartner(String tic);
	
	public void removePartner(String tic);
	
	public double getLeasePrice();
	public void payLease();
	
	public void setLeasePrice(double lp);
	
	public boolean isLeased();
	
	public String display();
	
	public String display(String tic);
	
	public String toSaveString();
	
	public GmlPair toGmlPair();
	
	public String getId();
}

