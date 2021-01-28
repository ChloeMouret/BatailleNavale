package answers;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

public interface Service {
	
	public static Integer SERVER_CHAT_CODE = 0;
	public static Integer CHAT_CODE = 1;
	public static Integer GAME_CODE = 2;
	
	public void answer(Session user, JSONObject jsonMessage);
	
	
	public static Service getInstance(int status) {
		if (status == SERVER_CHAT_CODE){
			return new ServerChatService();}
		else if (status == CHAT_CODE) {
		    return new ChatService();
		}
		else {
			throw new IllegalStateException();
		}
	}
}
