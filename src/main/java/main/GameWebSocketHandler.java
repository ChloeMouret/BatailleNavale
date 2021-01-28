package main;


import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import answers.Service;

@WebSocket
public class GameWebSocketHandler {
	private String user; 
	private String column;
	private String line; 

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
    	String username = "User" + Game.nextUserNumber++;
        Game.userUsernameMap.put(user, username);
        Game.broadcastChatMessage("Server", (username + " joined the chat"));
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = Game.userUsernameMap.get(user);
        Game.userUsernameMap.remove(user);
        Game.broadcastChatMessage("Server", "" + username + " left the game");
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
    	JSONObject json = new JSONObject(message);
    	String status = json.get("type").toString();
    	int statusInt = Integer.parseInt(status);
    	Service service = Service.getInstance(statusInt);
    	service.answer(user, json);
    	
//    	if (statusInt == 0) {
//    		String msg = json.get("message").toString();
//    		Game.broadcastChatMessage("Server", Game.userUsernameMap.get(user) + " " +msg);
//    	}
//    	else {
//    		String msg = json.get("message").toString();
//    		Game.broadcastChatMessage(Game.userUsernameMap.get(user), msg);
//    	}
    }
}
