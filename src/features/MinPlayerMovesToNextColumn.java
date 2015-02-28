package features;

import java.util.LinkedList;

import quoridor.Game;
import quoridor.Move;
import quoridor.Player;

public class MinPlayerMovesToNextColumn implements Feature {
	@Override
	public float evaluate(Game g, Player player) {
		return g.shortestPath(g.players().other(player)).size(); 
	}
}
