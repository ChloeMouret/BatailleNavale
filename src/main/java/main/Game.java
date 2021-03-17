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
	public static BiMap<Session, Player> sessionPlayerMap = HashBiMap.create();
	public static Map<Integer, String> waitingListNames = new ConcurrentHashMap<>();
	static int nextUserNumber = 1; //Used for creating the next username
	public static Integer GameIdentity;
	public static Map<Integer, Player> playersMap = new ConcurrentHashMap<>(); 
	
	public Game(Integer gameIdentity) {
		this.GameIdentity = gameIdentity;
	}
	
	public static Map<Session, Player>  getUsernameMap() {
		return sessionPlayerMap;
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
	
	public static void playerBoat(Player player, Integer column, Integer line, Integer direction) {
		Key boatKey = new Key(column, line); 
		int playerNumberOfBoats = player.getBoard().getListBoat().size();
		Boat boat = new Boat(boatKey, SIZE_OF_BOATS.get(playerNumberOfBoats), direction, player.getBoard().getListBoat().size()); 
		player.getBoard().addBoat(boat);
		Integer count = player.getBoard().getListBoat().size();
		Session session = sessionPlayerMap.inverse().get(player);
		Player otherPlayer; 
		if (player.getId() == 0) {
			otherPlayer = playersMap.get(1);
		}
		else {
			otherPlayer = playersMap.get(0);
		}
		Session otherSession = sessionPlayerMap.inverse().get(otherPlayer);
		if (count == SIZE_OF_BOATS.size()) {
			try {
				if (sessionPlayerMap.size() == 1) {
					JSONObject socketMessage = new JSONObject().put("type", "boats-ok").put("otherPlayerReady", "no");
					session.getRemote().sendString(String.valueOf(socketMessage));
				}
				else if (sessionPlayerMap.size() == 2){
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
	
	public static void endOfGame(Player winner, Player loser) {
		Session winnerSession = sessionPlayerMap.inverse().get(winner);
		Session loserSession = sessionPlayerMap.inverse().get(loser);
		try {
			JSONObject socketMessageWinner = new JSONObject().put("type", "winner");
			JSONObject socketMessageLoser = new JSONObject().put("type", "loser");
			winnerSession.getRemote().sendString(String.valueOf(socketMessageWinner));
			loserSession.getRemote().sendString(String.valueOf(socketMessageLoser));
		} catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void firstPlayer() {
		Random r1 = new Random();
		int idFirstPlayer = r1.nextInt(2);
		System.out.println("firstPlayer id is : "+idFirstPlayer);
		Session session = sessionPlayerMap.inverse().get(playersMap.get(idFirstPlayer));
		try {
			JSONObject socketMessage = new JSONObject().put("type", "turn");
			session.getRemote().sendString(String.valueOf(socketMessage));
		} catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void nextPlayer(Session oldSession, Session newSession) {
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
    public static void broadcastChatMessage(Player sender, String message) {
        sessionPlayerMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
            	JSONObject msg = new JSONObject()
            			.put("type", "message")
                        .put("userMessage", createHtmlMessageFromSenderChat(sender, message))
                        .put("userlist", sessionPlayerMap.values())
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
    
    
    public void addPlayer(String playerName) {
		waitingListNames.put(1, playerName);
		
    }
    
    public String render(Map<String, Object> model, String templatePath) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, templatePath));
    }
    
    
    public static void main(String[] args) {
    	
    }
}
