package features;

import quoridor.Game;
import quoridor.Player;

public class PositionDifference implements Feature {
	@Override
	public float evaluate(Game g, Player player) {
		int a = Math.abs(player.pawn().y - player.positions().getFirst().y);
		Player opp = g.players.other(player);
		int b = Math.abs(opp.pawn().y - opp.positions().getFirst().y);
		return a - b;
	}
}
