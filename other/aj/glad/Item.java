package aj.glad;


/**
 *  Description of the Class 
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public abstract class Item {
	int weight, cost;
	int maxhitpoints;
	int hitpoints;
	String name;


	/**
	 *  Constructor for the Item object 
	 */
	public Item() {
	}


	/**
	 *  Sets the Name attribute of the Item object 
	 *
	 *@param  s  The new Name value 
	 */
	public void setName(String s) {
		name = s;
	}


	/**
	 *  Sets the Weight attribute of the Item object 
	 *
	 *@param  a  The new Weight value 
	 */
	public void setWeight(int a) {
		weight = a;
	}


	/**
	 *  Sets the Cost attribute of the Item object 
	 *
	 *@param  a  The new Cost value 
	 */
	public void setCost(int a) {
		cost = a;
	}


	/**
	 *  Sets the MaxHitPoints attribute of the Item object 
	 *
	 *@param  a  The new MaxHitPoints value 
	 */
	public void setMaxHitPoints(int a) {
		maxhitpoints = a;
	}


	/**
	 *  Sets the HitPoints attribute of the Item object 
	 *
	 *@param  a  The new HitPoints value 
	 */
	public void setHitPoints(int a) {
		hitpoints = a;
	}


	/**
	 *  Gets the Name attribute of the Item object 
	 *
	 *@return    The Name value 
	 */
	public String getName() {
		return name;
	}


	/**
	 *  Gets the Weight attribute of the Item object 
	 *
	 *@return    The Weight value 
	 */
	public int getWeight() {
		return weight;
	}


	/**
	 *  Gets the Cost attribute of the Item object 
	 *
	 *@return    The Cost value 
	 */
	public int getCost() {
		return cost;
	}


	/**
	 *  Gets the MaxHitPoints attribute of the Item object 
	 *
	 *@return    The MaxHitPoints value 
	 */
	public int getMaxHitPoints() {
		return maxhitpoints;
	}


	/**
	 *  Gets the HitPoints attribute of the Item object 
	 *
	 *@return    The HitPoints value 
	 */
	public int getHitPoints() {
		return hitpoints;
	}


	/**
	 *  Gets the RepairCost attribute of the Item object 
	 *
	 *@return    The RepairCost value 
	 */
	public int getRepairCost() {
		return 2 * cost * (1 - hitpoints / Math.max(maxhitpoints, 1));
	}
}
