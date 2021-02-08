package answers;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import main.Game;

public class ChooseBoatService implements Service{
	
	@Override
	public void answer(Session user, JSONObject jsonMessage) {
		Integer column = jsonMessage.getInt("column");
		Integer line = jsonMessage.getInt("line");
		Integer direction = jsonMessage.getInt("direction");
		//System.out.println("GameService : "+ column+","+line+","+direction+"");
		Game.playerBoat(Game.getUsernameMap().get(user), column, line, direction);
	}

}
