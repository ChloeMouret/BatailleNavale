package answers;


import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import main.Boat;
import main.Case;
import main.Game;
import main.Key;
import main.Player;
import main.Webapp;

public class GameService implements Service{
	
	@Override
	public void answer(Session user, JSONObject jsonMessage) {
		Player player1 = Webapp.getSessionPlayerMap().get(user);
    	Game game = Webapp.getPlayersGame().get(player1);
		
		Integer column = jsonMessage.getInt("column");
		Integer line = jsonMessage.getInt("line");
		//String htmlChange ="";
		Player player2;
		if (player1.getId() == game.getPlayers().get(0).getId()) {
			player2 = game.getPlayers().get(1);
		}
		else {
			player2 = game.getPlayers().get(0);
		}
		player2.getBoard().getCase(column,  line).setHasBeenShot();
		System.out.println("Target board" +player2.getBoard().toStringTargetBoard());
		Integer status = 0; 
		String message = player1.getName() +" tire en (Col : "+column+", Ligne : "+line+")";
		if (player2.getBoard().getCase(column, line).isBoat()) {
			message = message + "\n" + "Touché ";
			Integer boatId = player2.getBoard().getCase(column, line).getBoatId();
			Boat boatTouched = player2.getBoard().getListBoat().get(boatId);
			changeBoatStatus(boatTouched, player2);
			status = boatTouched.getStatus();
			if (status == 2) {
				message = message + "Coulé ";
				if (! player2.getBoard().stillABoatOnBoard()) {
					game.endOfGame(player1, player2);
				}
			}
		}
		else {
			message = message + "\n Raté ";
		}
		Player server = new Player("Server", 0);
		game.broadcastChatMessage(server, message);
		changeColorCase(user, column, line, status);
	}
	
	
	
	public static void changeColorCase(Session session, Integer column, Integer line, Integer status) {
		System.out.println("enter changeColorCase");
		try {
			JSONObject msg = new JSONObject().put("type", "game").put("column", column).put("line", line).put("status", status);
			
	        session.getRemote().sendString(String.valueOf(msg));
		} catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	
	public static void changeBoatStatus(Boat b, Player p) {
		if (b.getStatus()==0) {             //si le bateau était intact avant le tir 
			if (b.getBoatSize()==1) {      //il est coulé si sa taille était de 1, donc statut = 2
				b.setStatus(2);
			}
			else {                             //le bateau est touché : statut = 1
				b.setStatus(1);
			}
		}
		else {                                 //le bateau était déjà touché, il faut vérifier qu'il n'est pas coulé 
			int size = b.getBoatSize();
			Key k = b.getFirstCase();
			int counter = 0;
			for (int i=0; i<size; i++) {
				if (b.getDirection()==0) {
					if (p.getBoard().getCase(k.getWidth(), k.getHeight()+i).hasBeenShot()) {
						counter ++; 
					}
				}
				else {
					if (p.getBoard().getCase(k.getWidth()+i, k.getHeight()).hasBeenShot()) {
						counter ++; 
					}
				}
			}
			if (counter == size) {             //si toutes les parties du bateau ont été touchées alors le bateau est coulé 
				b.setStatus(2);
			}                                  //sinon, ça ne change rien, le bateau est toujours touché 
		}
	}
}
