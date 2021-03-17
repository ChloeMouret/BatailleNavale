package main;

import static spark.Spark.init;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;
import static spark.Spark.webSocket;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

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
	
	
	/**
	 * @return listGames
	 */
	public static Map<Integer, Game> getListGames () {
		return listGames;
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
	public static String redirectHome(Map<String, Object> model, String templatePath) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, templatePath));
    }
	
	
	
	/**
	 * Allow to start the game
	 */
    public static void playGame() {
    	staticFileLocation("/public/"); //index.html is served at localhost:4567 (default port)
    	webSocket("/socket", GameWebSocketHandler.class);
    	post("/game", (req, res) -> {
    		System.out.println("in post");
    		String newPlayerForm = req.body();
    		JSONObject jsonReq = formToJSON(newPlayerForm);
    		String username = jsonReq.getString("playerName");
    		String requestType = jsonReq.getString("requestType");
    		if (requestType.equals("create")) {
    			Integer gameIdentity = generateId(); 
    			Game game = new Game(gameIdentity);
    			setListGames(game, gameIdentity);
    			game.addPlayer(username);
    			//TODO : il faut ajouter le GameIdentity au front 
    			System.out.println("GameIdentity : "+ gameIdentity);
    			Map<String, Object> model = new HashMap<>();
        	    return game.render(model, "public/game.html");
    		}
    		else if (requestType.equals("join")){
    			Integer gameIdJoin = jsonReq.getInt("gameId");
    			System.out.println(gameIdJoin);
    			if (getListGames().get(gameIdJoin) != null) {
    				System.out.println("enter if join");
    				Game game = getListGames().get(gameIdJoin);
    				game.addPlayer(username);
    				Map<String, Object> model = new HashMap<>();
    	    	    return game.render(model, "public/game.html");
    			}
    			else {
    				System.out.println("not good code");
    				Map<String, Object> model = new HashMap<>();
    				return redirectHome(model, "public/index.html");
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
