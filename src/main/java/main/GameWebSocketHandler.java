package main;


import static spark.Spark.get;
import static spark.Spark.staticFileLocation;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import answers.Service;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

@WebSocket
public class GameWebSocketHandler {
	private Player player; 
	private String column;
	private String line; 

	
	 
    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
    	String username = Game.waitingListNames.get(1);
    	Game.waitingListNames.remove(1);
    	Player player = new Player(username, Game.playersMap.size());
    	System.out.println(player.getName()+" id is : "+ player.getId());
        Game.sessionPlayerMap.put(session, player);
        Game.playersMap.put(player.getId(), player);
        
        //transmit info from back to JS
        Game.transmitInfoToJS(session);
        
        //allow to put message from the server, hypothetical player 
        Player server = new Player("Server", 0);
        Game.broadcastChatMessage(server, (username + " joined the game"));
        
        //if 2 players are connected it handles which one begins
        if (Game.playersMap.size()==2) {
        	Game.firstPlayer();
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        String username = Game.sessionPlayerMap.get(player).getName();
        Game.sessionPlayerMap.remove(session);
        
      //allow to put message from the server, hypothetical player 
        Player server = new Player("Server", 0);
        Game.broadcastChatMessage(server, (username + " left the game"));
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
    	JSONObject json = new JSONObject(message);
    	String status = json.get("type").toString();
    	int statusInt = Integer.parseInt(status);
    	Service service = Service.getInstance(statusInt);
    	service.answer(user, json);
    }
}
