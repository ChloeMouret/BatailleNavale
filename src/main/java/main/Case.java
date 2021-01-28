package main;


public class Case {
	private boolean isBoat;
	private boolean hasBeenShot;
	private int boatId;   //=-1 de base, puis id du bateau si il y en a un
	
	/**
	 * @param boat
	 * @param shot
	 * @param broke
	 * create a case
	 */
	public Case() {
		this.isBoat = false;
		this.hasBeenShot = false;
		this.boatId = -1;
	}
	
	/**
	 * @return true if there is a boat on the case, false otherwise
	 */
	public boolean isBoat(){
		return this.isBoat;
	}
	
	/**
	 * @param id
	 * 
	 */
	public void setBoatId(int id) {
		this.boatId = id;
	}
	
	/**
	 * @return boat Id 
	 */
	public int getBoatId() {
		return this.boatId;
	}
	
	/**
	 * @return true if the case has been shot 
	 */
	public boolean hasBeenShot() {
		return this.hasBeenShot;
	}
	
//	/**
//	 * @return true is there is a broken boat on the case (= a boat + shot)
//	 */
//	public boolean brokenBoat() {
//		return this.brokenBoat;
//	}
	
	/**
	 * put a boat on the case 
	 */
	public void setBoat() {
		this.isBoat = true;
	}
	
	/**
	 * true if the case has been shot previously
	 */
	public void setHasBeenShot() {
		this.hasBeenShot = true;
	}
}
