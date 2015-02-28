package features;

import quoridor.Game;
import quoridor.Player;

public class MinPlayerShortestPath implements Feature {

	@Override
	public float evaluate(Game g, Player player) {
		return g.shortestPath(g.players().other(player)).size(); 
	}

}
