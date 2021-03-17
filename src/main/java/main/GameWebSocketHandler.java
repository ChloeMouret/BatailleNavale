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

	
	 
    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
    	Player player = Webapp.getWaitingListNames().get(0);
    	Game game = Webapp.getPlayersGame().get(player);
    	Webapp.getWaitingListNames().remove(0);
    	System.out.println(player.getName()+" id is : "+ player.getId());
    	Webapp.getSessionPlayerMap().put(session, player);
    	game.getPlayers().add(player);
    	
        //transmit info from back to JS
        game.transmitInfoToJS(session);
        
        //allow to put message from the server, hypothetical player 
        Player server = new Player("Server", 0);
        game.broadcastChatMessage(server, (player.getName() + " joined the game"));
        
        //if 2 players are connected it handles which one begins
        if (game.getPlayers().size()==2) {
        	game.firstPlayer();
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
    	Player player = Webapp.getSessionPlayerMap().get(session);
    	Game game = Webapp.getPlayersGame().get(player);
        Webapp.getSessionPlayerMap().remove(session);
        Webapp.getPlayersGame().remove(player);
        game.getPlayers().remove(player);
        
      //allow to put message from the server, hypothetical player 
        Player server = new Player("Server", 0);
        game.broadcastChatMessage(server, (player.getName() + " left the game"));
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
    	JSONObject json = new JSONObject(message);
    	String status = json.get("type").toString();
    	System.out.println("status is :" + status);
    	int statusInt = Integer.parseInt(status);
    	Service service = Service.getInstance(statusInt);
    	service.answer(user, json);
    }
}
