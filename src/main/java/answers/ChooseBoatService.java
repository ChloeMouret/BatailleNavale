package answers;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import main.Game;
import main.Player;
import main.Webapp;

public class ChooseBoatService implements Service{
	
	@Override
	public void answer(Session user, JSONObject jsonMessage) {
		System.out.println("in chooseBoatService");
		Player player = Webapp.getSessionPlayerMap().get(user);
    	Game game = Webapp.getPlayersGame().get(player);
		Integer column = jsonMessage.getInt("column");
		Integer line = jsonMessage.getInt("line");
		Integer direction = jsonMessage.getInt("direction");
		//System.out.println("GameService : "+ column+","+line+","+direction+"");
		game.playerBoat(player, column, line, direction);
	}

}
