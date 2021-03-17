package answers;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import com.google.common.collect.BiMap;

import main.Game;
import main.Player;

public class NextPlayerService implements Service{

	@Override
	public void answer(Session user, JSONObject jsonMessage) {
		//BiMap<Session, Player> sessionPlayerMap = Game.sessionPlayerMap;
		Player currentPlayer = Game.sessionPlayerMap.get(user); 
		Integer nextPlayerId ;
		if (currentPlayer.getId() ==0) {
			nextPlayerId = 1;
		}
		else {
			nextPlayerId = 0; 
		}
		Player nextPlayer = Game.playersMap.get(nextPlayerId);
		Game.nextPlayer(user, Game.sessionPlayerMap.inverse().get(nextPlayer));
	}
	

}
