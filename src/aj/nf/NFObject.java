package aj.nf;


/**
 *@author     judda 
 *@created    July 21, 2000 
 */
public interface NFObject {
	
	public String getId();
	public String getNick();
	
	public String getCorpTick();
	
	public void setCorpTick(String s);
	
	public Location getLocation();
	
	public void setLocation(Location l);
	
	public int getMass();
	
	public String displayHeader();
	public String display();
	
	public String toSaveString();
	public GmlPair toGmlPair();
	
	public boolean isMoveable();
	
	public double getValue();
	
	public String toScanString();
}

