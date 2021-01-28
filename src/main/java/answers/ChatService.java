package answers;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import main.Game;

public class ChatService implements Service {

	@Override
	public void answer(Session user, JSONObject jsonMessage) {
		System.out.println("enter");
		String msg = jsonMessage.get("message").toString();
		Game.broadcastChatMessage(Game.userUsernameMap.get(user), msg);
	}

}
