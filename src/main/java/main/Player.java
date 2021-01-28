package main;


import java.util.Scanner;

public class Player {
	private String name; 
	private int id;
	private int idCount = 0; 
	private Board board;
	
	/**
	 * @param n
	 * Player constructor
	 */
	public Player(String n) {
		this.name = n;
		this.id = idCount;
		idCount ++;
		this.board = new Board();
	}
	
	/**
	 * @return board of the player 
	 */
	public Board getBoard() {
		return this.board;
	}
	
	/**
	 * @return player's id
	 */
	public int getId() {
		return this.id;
	}
	
	
	/**
	 * @return player's name
	 */
	public String getName() {
		return this.name;
	}
	
	
	
	/**
	 * player chooses its boats
	 * @param player 
	 * @param board 
	 */
	public void chooseBoats() {
		System.out.println(this.getName());
		System.out.println("Vous allez devoir choisir l'emplacement de vos bateaux en définissant la première case du bateau ainsi"
				+ " que sa direction. Pour cela, voici la représentation de la game : ");
		System.out.println(this.getBoard());
		System.out.println("Il y a " + Game.SIZE_OF_BOATS.size() +" bateau(x) par joueurs, de tailles : ");
		for (int j=0; j<Game.SIZE_OF_BOATS.size(); j++) {
			System.out.println("Bateau "+ (j+1)+" : "+Game.SIZE_OF_BOATS.get(j));
		}
		for (int i=0; i<Game.SIZE_OF_BOATS.size(); i++) {
			System.out.println("Votre bateau n°" +(i+1)+ " est-il vertical (0) ou horizontal (1)?");
			Scanner scannerDir = new Scanner(System.in);
			int direction = scannerDir.nextInt();
			System.out.println("Entrez la position en X de votre bateau n°" + (i+1) + ": ");
			Scanner scannerX = new Scanner(System.in);
			int positionX = scannerX.nextInt();
			System.out.println("Entrez la position en Y de votre bateau n°" + (i+1) + ": ");
			Scanner scannerY = new Scanner(System.in);
			int positionY = scannerY.nextInt();
			this.getBoard().addBoat(new Boat(new Key(positionX, positionY), Game.SIZE_OF_BOATS.get(i), direction));
			System.out.println("Voici l'emplacement de vos bateaux : ");
			System.out.println(this.getBoard());
			System.out.println();
		}
	}
	
	
	
	
	/**
	 * @param args
	 * main method
	 */
	public static void main(String[] args) {
		
	}
}
