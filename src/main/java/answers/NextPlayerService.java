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
    	System.out.println("in answer");
		Player nextPlayer ;
		if (currentPlayer.getId() == game.getPlayers().get(0).getId()) {
			nextPlayer = game.getPlayers().get(1);
		}
		else {
			nextPlayer = game.getPlayers().get(0); 
		}
		game.nextPlayer(user, Webapp.getSessionPlayerMap().inverse().get(nextPlayer));
	}
	

}
