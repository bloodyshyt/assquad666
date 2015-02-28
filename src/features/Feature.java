package features;

import quoridor.Game;
import quoridor.Player;

public interface Feature {

	public float evaluate(Game g, Player player);
}
