package main;


import java.util.HashMap;

public class Board {
	private final HashMap<Key, Case> map;
	private final HashMap<Integer, Boat> listBoat;
	/**
	 * The width size of the board is fixed for the entire game for all boards the same
	 */
	public static final int BOARD_W_SIZE = 5;
	/**
	 * The height size of the board is fixed for the entire game for all boards the same
	 */
	public static final int BOARD_H_SIZE = 5;
	
	
	
	/**
	 * @param w
	 * @param h
	 * create board with width w and height h, and listBoat empty
	 * @throws Exception 
	 */
	public Board() {
		this.listBoat = new HashMap<Integer,Boat>();
		this.map = new HashMap<Key, Case>();
		for (int i=0;i<BOARD_W_SIZE;i++) {
			for (int j=0; j<BOARD_H_SIZE;j++) {
				try {
					this.put(i,j, new Case());
				}
				catch(Exception e) {
					// SHOULD NOT HAPPEN
				}
			}
		}
	}
	
//	/**
//	 * @param size
//	 * create board with width=height=size
//	 * @throws Exception 
//	 */
//	public Board(int size) {
//		this(size, size);
//	}
	
	/**
	 * @return list of boats defined by their ids
	 */
	public HashMap<Integer, Boat> getListBoat(){
		return this.listBoat;
	}
	
	/**
	 * @return HashMap with Key and Case 
	 */
	public HashMap<Key, Case> getMap() {
		return this.map;
	}
	
	/**
	 * @param h
	 * @param w
	 * @return the case of height h and width w 
	 */
	public Case getCase(int w, int h) {
		return this.getMap().get(new Key(w,h));
	}
	
	/**
	 * @return width of board
	 */
	public int getWidth() {
		return BOARD_W_SIZE;
	}
	
	/**
	 * @return height of board
	 */
	public int getHeight() {
		return BOARD_H_SIZE;
	}
	
	/**
	 * @param h 
	 * @param w
	 * @param val
	 * @throws Exception
	 * add a case of board on height h and width w 
	 */
	public void put(int w, int h, Case val) throws Exception {
		if (0 <= w && w <= BOARD_W_SIZE-1) {
			if (0 <= h && h <= BOARD_H_SIZE -1) {
				map.put(new Key(w, h), val);
				return;
			}
		}
		throw new Exception("Not in good range");
	}
	
	/**
	 * @param k
	 * @param val
	 * @throws Exception
	 * add a case with key k on board
	 */
	public void put(Key k, Case val) throws Exception {
		put(k.getHeight(), k.getWidth(), val);
	}
	
	/**
	 * @param k
	 * @param boatSize
	 * @param direction (0 if vertical and 1 if horizontal)
	 * add boat on board 
	 * @return true if succeed to place boat
	 */
	//TODO verify that the position of boat chose by the player is in range 
	public boolean addBoat(Key k, int boatSize, int direction){
		if (direction == 0) {     //vertical
			boolean vertical = addBoatVertically(k, boatSize);
			if (vertical) {
				return true;
			}
			else {
				return false;
			}
		}
		else {                    //horizontal
			boolean horizontal = addBoatHorizontally(k, boatSize);
			if (horizontal) {
				return true;
			}
			else {
				return false; 
			}
		}
	}
	
	/**
	 * @param boat
	 */
	public void addBoat(Boat boat) {
		boolean succeedBoat = addBoat(boat.getFirstCase(), boat.getBoatSize(), boat.getDirection());
		if (succeedBoat) {
			this.getListBoat().put(boat.getId(), boat);
			for (int i=0; i<boat.getBoatSize(); i++) {
				if (boat.getDirection() == 0) {
					this.getCase(boat.getFirstCase().getWidth(), boat.getFirstCase().getHeight()+i).setBoat();
					this.getCase(boat.getFirstCase().getWidth(), boat.getFirstCase().getHeight()+i).setBoatId(boat.getId());
				}
				else {
					this.getCase(boat.getFirstCase().getWidth() + i, boat.getFirstCase().getHeight()).setBoat();
					this.getCase(boat.getFirstCase().getWidth() + i, boat.getFirstCase().getHeight()).setBoatId(boat.getId());
				}
			}
			
		}
	}
	
	/**
	 * @param k
	 * @param boatSize
	 * add boat on board in case direction of boat is vertical
	 * @return true if no boat already, false if there is a boat
	 */
	//TODO : verify that boat in range
	public boolean addBoatVertically(Key k, int boatSize){
		if (noBoatVertically(k, boatSize)) {
			for (int i=0; i<boatSize; i++) {
				this.getCase(k.getWidth(), k.getHeight()+i).setBoat();
			}
			return true;
		}
		else {
			System.out.println("already a boat there");
			return false; 
		}
	}
	
	/**
	 * @param k
	 * @param boatSize
	 * add boat on board in case direction of boat is horizontal
	 * @return true if no boat, false if already boat
	 * @throws Exception 
	 */
	//TODO : verify that boat in range
	public boolean addBoatHorizontally(Key k, int boatSize){
		if (noBoatHorizontally(k, boatSize)) {
			for (int i=0; i<boatSize; i++) {
				this.getCase(k.getWidth()+i, k.getHeight()).setBoat();;
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * @param k
	 * @return case bellow case with key k
	 */
	public Case getNextVertical(Key k) {
		int w = k.getWidth();
		int h = k.getHeight();
		return this.getCase(w, h+1);
	}
	
	/**
	 * @param k
	 * @return case on the right of case with key k
	 */
	public Case getNextHorizontal(Key k) {
		int w = k.getWidth();
		int h = k.getHeight();
		return this.getCase(w+1, h);
	}

	
	/**
	 * @param k
	 * @param boatSize
	 * @param firstBlock
	 * @return true if no boat vertically and false is there is already a boat. 
	 * Verifies on each case of the potential boat 
	 */
	public boolean noBoatVertically(Key k, int boatSize){
		int i=0;
		Case c = map.get(k);
		while (i !=boatSize) {
			if (! c.isBoat()) {
				i ++;
				c = getNextVertical(k);
			}
			else {
				return false; 
			}
		}
		return true;
	}
	
	
	/**
	 * @param k
	 * @param boatSize
	 * @param firstBlock
	 * @return true if no boat horizontally and false is there is already a boat. 
	 * Verifies on each case of the potential boat 
	 */
	public boolean noBoatHorizontally(Key k, int boatSize){
		int i=0;
		Case c = map.get(k);
		while (i !=boatSize) {
			if (! c.isBoat()) {
				i ++;
				c = getNextHorizontal(k);
			}
			else {
				return false; 
			}
		}
		return true;
	}
	
	
	/**
	 * @return true if the player has at least one boat that has not sunk
	 */
	public boolean stillABoatOnBoard() {
		for (Boat b: this.getListBoat().values()) {
			if (b.getStatus() != 2) {
				return true;
			}
		}
		return false;
	}

	
	
	@Override
	public String toString() {
		String grille= "Y|X | ";
		for (int k=0; k<BOARD_W_SIZE; k++) {
			grille += k+" ";
		}
		grille += "\n";
		grille += "----";
		for (int k=0; k<BOARD_W_SIZE; k++) {
			grille += "---";
		}
		grille += "\n";
		for (int i=0; i<BOARD_H_SIZE; i++) {
			grille += " "+i+"  | ";
			for (int j=0; j<BOARD_W_SIZE; j++) {
				if (this.getCase(j, i).isBoat()) {
					grille += "1 ";
				}
				else {
					grille += "0 ";
				}
			}
			grille += "\n";
		}
		return grille;
	}
	
	/**
	 * @return string of target board
	 */
	public String toStringTargetBoard() {
		String grille= "Y|X | ";
		for (int k=0; k<BOARD_W_SIZE; k++) {
			grille += k+" ";
		}
		grille += "\n";
		grille += "----";
		for (int k=0; k<BOARD_W_SIZE; k++) {
			grille += "---";
		}
		grille += "\n";
		for (int i=0; i<BOARD_H_SIZE; i++) {
			grille += " "+i+"  | ";
			for (int j=0; j<BOARD_W_SIZE; j++) {
				if (this.getCase(j, i).hasBeenShot()) {
					if (this.getCase(j, i).isBoat()) {     //Case has been shot and there is a boat in it
						grille += "X ";
					}
					else {                                 //Case has been shot but there is no boat in it
						grille += "- ";
					}
				}
				else {                                     //Case has not been shot yet
					grille += "0 ";
				}
			}
			grille += "\n";
		}
		return grille;
	}
	
	/**
	 * @param args
	 * test
	 */
	public static void main(String[] args) {
		Board board = new Board();
		System.out.println(board);   //appelle automatiquement la méthode toString
		System.out.println();
		
		//Test addBoat
		Boat boatTest = new Boat(new Key(4,0), 2, 0, 0);
		board.addBoat(boatTest);
		int boatId = boatTest.getId();
		System.out.println(board);
		System.out.println();
		System.out.println("TEST ADD BOAT");
		System.out.println("Shoud be true : "+ board.getCase(4, 0).isBoat());
		System.out.println("Shoud be true : "+ board.getCase(4, 1).isBoat());
		System.out.println("Shoud be false : "+ board.getCase(4, 2).isBoat());
		System.out.println(board.getCase(4, 0).getBoatId() == boatId);
		System.out.println(board.getCase(4, 1).getBoatId() == boatId);
		System.out.println(board.getCase(4, 2).getBoatId() == -1);
		System.out.println();
		int count1 = 0;
		
		//Test toStringTargetBoat
		System.out.println("TEST TO STRING TARGET BOARD");
		System.out.println(board.toStringTargetBoard());
		board.getCase(4, 0).setHasBeenShot();
		board.getCase(0, 0).setHasBeenShot();
		System.out.println(board.toStringTargetBoard());
		
		//Test creation of board
		
		boolean var2 = board.getCase(1,1).hasBeenShot();
		if (var2 == false ) {
			count1 ++;
		}
		
		System.out.println("Should be false : " + var2);
		boolean var3 = board.getCase(1,1).isBoat();
		if (var3 == false ) {
			count1 ++;
		}
		System.out.println("Should be false : " + var3);
		System.out.println("Creation of board : "+ count1 + "/2");
		
		
		//Test addBoatHorizontally
		int count2 = 0;
		board.addBoatHorizontally(new Key(1,1), 2);
		System.out.println(board);
		boolean var4 = board.getCase(1,1).isBoat();
		if (var4) {
			count2 ++;
		}
		System.out.println("Should be true : " + var4);
		boolean var5 = board.getCase(2,1).isBoat();
		if (var5) {
			count2 ++;
		}
		System.out.println("Should be true : " + var5);
		boolean var6 = board.getCase(3,1).isBoat();
		if (! var6) {
			count2 ++;
		}
		System.out.println("Should be false : " + var6);
		boolean var7 = board.getCase(4,1).isBoat();
		if (! var7) {
			count2 ++;
		}
		System.out.println("Should be false : " + var7);
		System.out.println("AddBoatHorizontally : "+ count2 + "/4");
		
		
		//Test addBoatVertically
		boolean var11 = board.noBoatVertically(new Key(2,3), 2);
		System.out.println("testNoBoatVertically should be true : "+ var11);
		board.addBoatVertically(new Key(2,3), 2);
		System.out.println(board);
		int count3 = 0; 
		boolean var8 = board.getCase(2,4).isBoat();
		if (var8) {
			count3 ++;
		}
		System.out.println("Should be true : " + var8);
		boolean var9 = board.getCase(3,4).isBoat();
		if (! var9) {
			count3 ++;
		}
		System.out.println("Should be false : " + var9);
		System.out.println("AddBoatVertically : "+count3+"/2");
		
		
		//Test stillABoatOnBoard
		System.out.println(board.stillABoatOnBoard());
		board.addBoat(new Boat(new Key(0,3), 1, 1, 10));
		board.listBoat.get(0).setStatus(2);
		System.out.println(board.stillABoatOnBoard());
		board.listBoat.get(1).setStatus(2);
		System.out.println(board.stillABoatOnBoard());
	}
}
