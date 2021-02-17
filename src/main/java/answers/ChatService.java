package answers;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import main.Game;

public class ChatService implements Service {

	@Override
	public void answer(Session session, JSONObject jsonMessage) {
		String msg = jsonMessage.get("message").toString();
		Game.broadcastChatMessage(Game.sessionPlayerMap.get(session), msg);
	}

}
