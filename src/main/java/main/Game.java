package main;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;

import spark.ModelAndView;
import spark.Route;
import spark.Spark;
import spark.template.velocity.VelocityTemplateEngine;
import spark.utils.IOUtils;

import static spark.Spark.*;
import static j2html.TagCreator.*;

public class Game {
	public static final List<Integer> SIZE_OF_BOATS = Lists.newArrayList(2,1);
	static int nextUserNumber = 1; //Used for creating the next username
	public Integer gameIdentity;
	public ArrayList<Player> playersList = new ArrayList();
	
	public Game(Integer gameIdentity) {
		this.gameIdentity = gameIdentity;
	}
	
	public Integer gatGameIdentity () {
		return this.gameIdentity;
	}
	
	public ArrayList<Player> getPlayers() {
		return this.playersList;
	}
	
	//TODO : coté js : faire pour qu'on ne puisse entrer que des chiffres
	//TODO : connecter les tailles du back au js 
	//TODO : gerer les tailles de bateaux automatiquement 
	
	public static void transmitInfoToJS(Session session) {
		System.out.println("in transmit info to JS");
		try {
			JSONObject init = new JSONObject()
					.put("type", "init")
					.put("width", Board.BOARD_W_SIZE)
					.put("height", Board.BOARD_H_SIZE)
					.put("boats", SIZE_OF_BOATS);
			session.getRemote().sendString(String.valueOf(init));
		} catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public void playerBoat(Player player, Integer column, Integer line, Integer direction) {
		System.out.println("in playerBoat");
		Key boatKey = new Key(column, line); 
		int playerNumberOfBoats = player.getBoard().getListBoat().size();
		Boat boat = new Boat(boatKey, SIZE_OF_BOATS.get(playerNumberOfBoats), direction, player.getBoard().getListBoat().size()); 
		player.getBoard().addBoat(boat);
		Integer count = player.getBoard().getListBoat().size();
		Session session = Webapp.getSessionPlayerMap().inverse().get(player);
		Player otherPlayer; 
		System.out.println("1");
		if (count == SIZE_OF_BOATS.size()) {
			try {
				if (getPlayers().size() == 1) {
					JSONObject socketMessage = new JSONObject().put("type", "boats-ok").put("otherPlayerReady", "no");
					System.out.println("3");
					session.getRemote().sendString(String.valueOf(socketMessage));
				}
				else if (getPlayers().size() == 2){
					if (player.getId() == getPlayers().get(0).getId()) {
						System.out.println("1 bis");
						otherPlayer = getPlayers().get(1);
					}
					else {
						System.out.println("1 bis");
						otherPlayer = getPlayers().get(0);
					}
					System.out.println("2");
					Session otherSession = Webapp.getSessionPlayerMap().inverse().get(otherPlayer);
					if (otherPlayer.getBoard().getListBoat().size() == SIZE_OF_BOATS.size()) {
						JSONObject socketMessage = new JSONObject().put("type", "boats-ok").put("otherPlayerReady", "yes");
						session.getRemote().sendString(String.valueOf(socketMessage));
						otherSession.getRemote().sendString(String.valueOf(socketMessage));
					}
					else {
						JSONObject socketMessage = new JSONObject().put("type", "boats-ok").put("otherPlayerReady", "no");
						session.getRemote().sendString(String.valueOf(socketMessage));
					}
				}
				else throw new Exception("Number of players not good");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(player.getBoard().toString());
	}
	
	public static void handleWebsocket() {
		webSocket("/socket", GameWebSocketHandler.class);
		init();
	}
	
	public void endOfGame(Player winner, Player loser) {
		Session winnerSession = Webapp.getSessionPlayerMap().inverse().get(winner);
		Session loserSession = Webapp.getSessionPlayerMap().inverse().get(loser);
		try {
			JSONObject socketMessageWinner = new JSONObject().put("type", "winner");
			JSONObject socketMessageLoser = new JSONObject().put("type", "loser");
			winnerSession.getRemote().sendString(String.valueOf(socketMessageWinner));
			loserSession.getRemote().sendString(String.valueOf(socketMessageLoser));
		} catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public void firstPlayer() {
		Random r1 = new Random();
		int idFirstPlayer = r1.nextInt(2);
		System.out.println("firstPlayer id is : "+ getPlayers().get(idFirstPlayer));
		Session session = Webapp.getSessionPlayerMap().inverse().get(getPlayers().get(idFirstPlayer));
		try {
			JSONObject socketMessage = new JSONObject().put("type", "turn");
			session.getRemote().sendString(String.valueOf(socketMessage));
		} catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public void nextPlayer(Session oldSession, Session newSession) {
		try {
			JSONObject socketMessageNextPlayer = new JSONObject().put("type", "turn");
			JSONObject socketMessageAncientPlayer = new JSONObject().put("type", "not-turn");
			newSession.getRemote().sendString(String.valueOf(socketMessageNextPlayer));
			oldSession.getRemote().sendString(String.valueOf(socketMessageAncientPlayer));
		} catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	//Sends a message from one user to all users, along with a list of current usernames
    /**
     * @param sender
     * @param message
     * broadcast message of sender in chat
     */
    public void broadcastChatMessage(Player sender, String message) {
        Webapp.getSessionPlayerMap().keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
            	JSONObject msg = new JSONObject()
            			.put("type", "message")
                        .put("userMessage", createHtmlMessageFromSenderChat(sender, message))
                        .put("userlist", getPlayers())
                        .put("boat", SIZE_OF_BOATS);
                session.getRemote().sendString(String.valueOf(msg));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
  //Builds a HTML element with a sender-name, a message
    private static String createHtmlMessageFromSenderChat(Player sender, String message) {
    	System.out.println("");
    	String senderName = sender.getName();
        return article().with(
                b(senderName + " :"),
                p(message)
        ).render();
    }
    
    
    public String render(String templatePath) {
    	Map<String, Object> model = new HashMap<>();
        return new VelocityTemplateEngine().render(new ModelAndView(model, templatePath));
    }
    
    
    public static void main(String[] args) {
    	
    }
}
