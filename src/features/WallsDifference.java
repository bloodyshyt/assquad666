package features;

import java.util.LinkedList;

import quoridor.Game;
import quoridor.Move;
import quoridor.Player;

public class WallsDifference implements Feature {


	@Override
	public float evaluate(Game g, Player player) {
		Player p = player;
		Player o = g.players().other(player);
		return p.wallsLeft() - o.wallsLeft();
	}

}
