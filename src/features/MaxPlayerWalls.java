package features;

import quoridor.Game;
import quoridor.Player;

public class MaxPlayerWalls implements Feature{

	@Override
	public float evaluate(Game g, Player player) {
		return g.players.other(player).wallsLeft();
	}

}
