package main;

import static spark.Spark.init;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;
import static spark.Spark.webSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import spark.ModelAndView;
import static spark.Spark.redirect;
import spark.Redirect;
import spark.template.velocity.VelocityTemplateEngine;

/**
 * @author chloe
 * handle every game
 */
public class Webapp {
	/**
	 * 
	 */
	public static Map<Integer, Game> listGames = new ConcurrentHashMap<>();
	public static ArrayList<Integer> listGameIdentity = new ArrayList();
	public static Map<Player, Game> playersGames = new ConcurrentHashMap<>();
	public static ArrayList<Player> waitingListNames = new ArrayList();
	public static BiMap<Session, Player> sessionPlayerMap = HashBiMap.create();
	
	//TODO : define player with identity instead of name
	
	/**
	 * @return listGames
	 */
	public static Map<Integer, Game> getListGames () {
		return listGames;
	}
	
	public static BiMap<Session, Player> getSessionPlayerMap(){
		return sessionPlayerMap;
	}
	
	public static ArrayList<Player> getWaitingListNames(){
		return waitingListNames;
	}
	
	public static Map<Player, Game> getPlayersGame() {
		return playersGames;
	}
	
	/**
	 * @param game
	 * @param gameIdentity
	 * add game in listGames
	 */
	public static void setListGames (Game game, Integer gameIdentity) {
		listGames.put(gameIdentity, game);
	}
	
	/**
	 * @return id for a new game
	 */
	public static Integer generateId() {
    	String str = "";
    	int max = 9;
    	int min = 0; 
    	for (int i=0; i<8; i++) {
    		str = str + ((int) (Math.random()*(max - min)));
    	}
    	Integer id = Integer.parseInt(str);
    	return id;
    }
	
	/**
	 * @param requestBody
	 * @return transform the form from post to a JSON
	 */
	public static JSONObject formToJSON(String requestBody) {
    	String newData = requestBody.replace("=", "' : '");
    	String newData2 = newData.replace("&", "', '");
    	String newData3 = "{ '" + newData2 + "' }";
    	return new JSONObject(newData3);
    }
	
	/**
	 * @param model
	 * @param templatePath
	 * @return the inital template when player has not the good code
	 */
	public static String redirectHome() {
		Map<String, Object> model = new HashMap<>();
        return new VelocityTemplateEngine().render(new ModelAndView(model, "public/index.html"));
    }
	
	
	
	/**
	 * Allow to start the game
	 */
    public static void playGame() {
    	staticFileLocation("/public/"); //index.html is served at localhost:4567 (default port)
    	webSocket("/socket", GameWebSocketHandler.class);
    	post("/game", (req, res) -> {
    		System.out.println("in post");
    		JSONObject jsonReq = formToJSON(req.body());
    		String username = jsonReq.getString("playerName");
    		Integer playerId = generateId();
    		Player player = new Player(username, playerId);
    		String requestType = jsonReq.getString("requestType");
    		if (requestType.equals("create")) {
    			Integer gameIdentity = generateId(); 
    			Game game = new Game(gameIdentity);
    			System.out.println(game);
    			setListGames(game, gameIdentity);
				getWaitingListNames().add(player);
    			listGameIdentity.add(gameIdentity);
    			playersGames.put(player, game);
//    			if (getListGames().size() ==2) {
//					System.out.println(listGames.get(listGameIdentity.get(0)).playersMap.size());
//					System.out.println(listGames.get(listGameIdentity.get(1)));
//				}
    			//TODO : il faut ajouter le GameIdentity au front 
    			System.out.println("GameIdentity : "+ gameIdentity);
        	    return game.render("public/game.html");
    		}
    		else if (requestType.equals("join")){
    			Integer gameIdJoin = jsonReq.getInt("gameId");
    			System.out.println(gameIdJoin);
    			if (getListGames().get(gameIdJoin) != null) {
    				System.out.println("enter if join");
    				Game game = getListGames().get(gameIdJoin);
    				getWaitingListNames().add(player);
    				playersGames.put(player, game);
    				System.out.println("Game 2nd player : " +game);
    	    	    return game.render("public/game.html");
    			}
    			else {
    				System.out.println("not good code");
    				Map<String, Object> model = new HashMap<>();
    				return redirectHome();
    			}
    		}
    		else throw new Exception("not create and not join");
    	});
    	//redirect.post("/game", "/game/00", Redirect.Status.SEE_OTHER);
        init();
    }
	
	
	/**
	 * @param args
	 * main
	 */
	public static void main(String[] args) {
		playGame();
	}
}
