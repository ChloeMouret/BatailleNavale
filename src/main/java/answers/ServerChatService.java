package answers;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import main.Game;
import main.Player;

public class ServerChatService implements Service{
	@Override
	public void answer(Session user, JSONObject jsonMessage) {
		String msg = jsonMessage.get("message").toString();
		
		 //allow to put message from the server, hypothetical player 
        Player server = new Player("Server", 0);
		Game.broadcastChatMessage(server, Game.getUsernameMap().get(user) + " " +msg);
	}
}
