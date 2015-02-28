package features;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import quoridor.Game;
import quoridor.Human;
import quoridor.Move;
import quoridor.Player;
import quoridor.Point;
import quoridor.WeightedMove;
import quoridor.WeightedMoveComparator;
import quoridor.Move.MoveType;

public class MaxPlayerMovesToNextColumn implements Feature {

	@Override
	public float evaluate(Game g, Player player) {
		LinkedList<Move> movesToNextColumn = g.movesToNextColumn(player);
		return (float) Math.pow(movesToNextColumn.size(), -1);
	}
}
