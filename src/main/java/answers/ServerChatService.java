package answers;


import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import main.Game;
import main.Player;
import main.Webapp;

public class ServerChatService implements Service{
	@Override
	public void answer(Session user, JSONObject jsonMessage) {
		Player player = Webapp.getSessionPlayerMap().get(user);
    	Game game = Webapp.getPlayersGame().get(player);
		String msg = jsonMessage.get("message").toString();
		
		 //allow to put message from the server, hypothetical player 
        Player server = new Player("Server", 0);
		game.broadcastChatMessage(server, player.getName() + " " +msg);
	}
}
