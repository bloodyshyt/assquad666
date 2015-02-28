package features;

import quoridor.Game;
import quoridor.Player;

public class Position implements Feature{

	@Override
	public float evaluate(Game g, Player player) {
		return player.goalDistance();
		//return Math.abs(player.pawn().y - player.positions().getFirst().y);
	}

}
