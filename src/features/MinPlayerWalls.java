package features;

import quoridor.Game;
import quoridor.Player;

public class MinPlayerWalls implements Feature{

	@Override
	public float evaluate(Game g, Player player) {
		return player.wallsLeft();
	}

}
