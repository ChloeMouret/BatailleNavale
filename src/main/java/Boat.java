

public class Boat {
	private int id;
	private Key firstCase; 
	private int boatSize;
	private int direction;  //0 = vertical, 1=horizontal 
	private int status; //0 = no problem, 1 = touché , 2 = coulé
	private static int idCount = 0;
	
	/**
	 * @param firstCase
	 * @param boatSize
	 * @param direction
	 * create boat, status = 0 at the beginning of the game because boat intact at the beginning
	 */
	public Boat(Key firstCase, int boatSize, int direction) {
		this.id = idCount;
		idCount++;
		this.firstCase = firstCase; 
		this.boatSize = boatSize;
		this.direction = direction; 
		this.status = 0;
	}
	
	/**
	 * @return id of boat
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * @return boat first case 
	 */
	public Key getFirstCase() {
		return this.firstCase;
	}
	
	/**
	 * @return boat size
	 */
	public int getBoatSize() {
		return this.boatSize;
	}
	
	/**
	 * @return boat direction (0 = vertical, 1 = horizontal)
	 */
	public int getDirection() {
		return this.direction;
	}
	
	/**
	 * @return status of the boat, 0 = okey, 1 = toucher, 2 = couler
	 */
	public int getStatus() {
		return this.status;
	}
	
	
	/**
	 * @param newStatus
	 * change the status of a boat
	 */
	public void setStatus(int newStatus) {
		this.status = newStatus;
	}
}
