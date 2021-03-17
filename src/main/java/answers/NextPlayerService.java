package answers;


import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import com.google.common.collect.BiMap;

import main.Game;
import main.Player;
import main.Webapp;

public class NextPlayerService implements Service{

	@Override
	public void answer(Session user, JSONObject jsonMessage) {
		Player currentPlayer = Webapp.getSessionPlayerMap().get(user);
    	Game game = Webapp.getPlayersGame().get(currentPlayer);
    	
		Integer nextPlayerId ;
		if (currentPlayer.getId() == game.getPlayers().get(0).getId()) {
			nextPlayerId = game.getPlayers().get(1).getId();
		}
		else {
			nextPlayerId = game.getPlayers().get(0).getId(); 
		}
		Player nextPlayer = game.getPlayers().get(nextPlayerId);
		game.nextPlayer(user, Webapp.getSessionPlayerMap().inverse().get(nextPlayer));
	}
	

}
