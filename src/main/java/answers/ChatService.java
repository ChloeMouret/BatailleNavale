package answers;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import main.Game;
import main.Player;
import main.Webapp;

public class ChatService implements Service {

	@Override
	public void answer(Session session, JSONObject jsonMessage) {
		Player player = Webapp.getSessionPlayerMap().get(session);
    	Game game = Webapp.getPlayersGame().get(player);
		
		String msg = jsonMessage.get("message").toString();
		game.broadcastChatMessage(player, msg);
	}

}
