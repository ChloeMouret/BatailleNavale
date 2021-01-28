package answers;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import main.Game;

public class ServerChatService implements Service{
	@Override
	public void answer(Session user, JSONObject jsonMessage) {
		String msg = jsonMessage.get("message").toString();
		Game.broadcastChatMessage("Server", Game.getUsernameMap().get(user) + " " +msg);
	}
}
