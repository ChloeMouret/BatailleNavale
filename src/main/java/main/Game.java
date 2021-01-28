package main;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import com.google.common.collect.Lists;
import static spark.Spark.*;
import static j2html.TagCreator.*;

public class Game {
	public static final List<Integer> SIZE_OF_BOATS = Lists.newArrayList(2,2);
	public static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
	static int nextUserNumber = 1; //Used for creating the next username
	
	
	public static Map<Session, String>  getUsernameMap() {
		return userUsernameMap;
	}
	
	//Sends a message from one user to all users, along with a list of current usernames
    /**
     * @param sender
     * @param message
     * broadcast message of sender in chat
     */
    public static void broadcastChatMessage(String sender, String message) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
            	JSONObject msg = new JSONObject()
                        .put("userMessage", createHtmlMessageFromSenderChat(sender, message))
                        .put("userlist", userUsernameMap.values());
                session.getRemote().sendString(String.valueOf(msg));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        );
    }
    
  //Builds a HTML element with a sender-name, a message
    private static String createHtmlMessageFromSenderChat(String sender, String message) {
        return article().with(
                b(sender + " :"),
                p(message)
        ).render();
    }
    
    
    public static void main(String[] args) {
    	staticFileLocation("/public"); //index.html is served at localhost:4567 (default port)
    	webSocket("/batailleNavale", GameWebSocketHandler.class);
        init();
    }
}
