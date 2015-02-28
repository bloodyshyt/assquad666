package quoridor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import quoridor.Move.MoveType;
import util.Pair;
import util.Two;
import features.Feature;
import features.MaxPlayerMovesToNextColumn;
import features.MaxPlayerShortestPath;
import features.MinPlayerMovesToNextColumn;
import features.MinPlayerShortestPath;
import features.Position;
import features.PositionDifference;
import features.WallsDifference;

/**
 * AI is called to generate a move based on game state and a smartness of a
 * move.
 * 
 * <h2>Goals</h2>
 * <ul>
 * <li>Generates random, naive, or pro moves.</li>
 * </ul>
 * 
 */

public class BaseAI {

	Game game;
	Player player;

	static MaxPlayerShortestPath maxPlayerShortestPath = new MaxPlayerShortestPath();
	static MinPlayerShortestPath minPlayerShortestPath = new MinPlayerShortestPath();
	static WallsDifference difference2 = new WallsDifference();
	Feature[] features = new Feature[] { null, maxPlayerShortestPath,
			minPlayerShortestPath, difference2 };
	float[] weights;

	// float[] weights = new float[] { 0.0f, -1f, 1f, 1f };

	/**
	 * Constructor for AI. It requireds type Game to be passed in.
	 * 
	 * @param game
	 *            the game AI is required for.
	 */
	public BaseAI(Game game, float[] weights) {
		this.game = game;
		player = game.myTurn();
		this.weights = weights;
	}

	/**
	 * Creates a move based on the required intelligence of the AI
	 * 
	 * @return a Move
	 */
	public Move createMove() {
		Move move = null;
		move = proMove();
		return move;
	}

	/**
	 * Makes a move with alpha-beta pruning look ahead based on a heuristic
	 * 
	 * @return a Move
	 */
	private Move proMove() {
		Pair<Float, Move> result = maxValue(game.moves, 0,
				Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		return result._2();
	}

	private int desiredDepth = 3;

	/**
	 * The first part of the alpha-beta pruning
	 * 
	 * @param moves
	 *            a list of all the moves made so far
	 * @param currentSearchDepth
	 *            the current depth of the search
	 * @param alphaMax
	 *            the max value of alpha
	 * @param betaMin
	 *            the minimum value of beta
	 * @return a Pair of argument, the alpha value of the move and the move
	 */
	private Pair<Float, Move> maxValue(LinkedList<Move> moves,
			int currentSearchDepth, Float alphaMax, Float betaMin) {
		Game tempGame = createTempGame(moves);
		ArrayList<Move> moveList;
		Move bestMove;
		float value = Float.NEGATIVE_INFINITY;

		if (currentSearchDepth == desiredDepth || isGoalState(moves)) {
			ArrayList<Move> m = findPossibleMoves(tempGame);
			// any move?
			return Pair.pair(heuristic(moves), m.get(0));
		}

		moveList = findPossibleMoves(tempGame);
		bestMove = moveList.get(0);
		for (int i = 0; i < moveList.size(); i++) {
			Game tempGameTwo = createTempGame(moves);
			tempGameTwo.move(moveList.get(i), tempGameTwo.myTurn());

			value = minValue(tempGameTwo.moves, currentSearchDepth + 1,
					alphaMax, betaMin);
			if (value > alphaMax) {
				alphaMax = value;
				bestMove = moveList.get(i);
			}

			if (alphaMax >= betaMin) {
				return Pair.pair(alphaMax, bestMove);
			}
		}

		return Pair.pair(alphaMax, bestMove);
	}

	/**
	 * The second part of the alpha-beta pruning
	 * 
	 * @param moves
	 *            a list of all the moves made so far
	 * @param currentSearchDepth
	 *            the current depth of the search
	 * @param alphaMax
	 *            the max value of alpha
	 * @param betaMin
	 *            the minimum value of beta
	 * @return the minimum value of beta of type int
	 */
	private float minValue(LinkedList<Move> moves, int currentSearchDepth,
			float alphaMax, float betaMin) {
		Pair<Float, Move> value = null;
		if (currentSearchDepth == desiredDepth || isGoalState(moves)) {
			return heuristic(moves);
		}

		Game tempGame = createTempGame(moves);
		ArrayList<Move> moveList = findPossibleMoves(tempGame);
		for (int i = 0; i < moveList.size(); i++) {
			Game tempGameTwo = createTempGame(moves);
			tempGameTwo.move(moveList.get(i), tempGameTwo.myTurn());

			value = maxValue(tempGameTwo.moves, currentSearchDepth + 1,
					alphaMax, betaMin);
			betaMin = Math.min(value._1, betaMin);
			if (alphaMax >= betaMin) {
				return betaMin;
			}
		}

		return betaMin;
	}

	private float evaluate(Game tempGame, Player player, int[] features) {
		float score = 0;
		for (int i = 0; i < features.length; i++) {
			score += weights[features[i]]
					* this.features[features[i]].evaluate(tempGame, player);
		}
		return score;
	}

	/**
	 * The heuristic for alpha-beta pruning
	 * 
	 * @param moves
	 *            a list of all the moves played so far
	 * @return a float, the heuristic value of a move
	 */
	private float heuristic(LinkedList<Move> moves) {

		Game tempGame = createTempGame(moves);
		int[] f = new int[] { 1, 2, 3 };
		if (player.equals(game.players()._1())) {
			// max is player 1
			return (float) (evaluate(tempGame, tempGame.players()._1, f) + new Random()
					.nextFloat() * 0.1);
		} else {
			return (float) (evaluate(tempGame, tempGame.players()._2, f) + new Random()
					.nextFloat() * 0.1);
		}

	}

	/**
	 * Checks if the state of the moves is a winning game state
	 * 
	 * @param moves
	 *            the list of all moves made so far
	 * @return boolean true if it is a winning state, false if not
	 */
	private boolean isGoalState(LinkedList<Move> moves) {

		if (!moves.isEmpty()) {
			for (int i = 0; i < 9; i++) {
				if ((moves.getLast().coord().x() == i && moves.getLast()
						.coord().y() == 1)
						|| (moves.getLast().coord().x() == i && moves.getLast()
								.coord().y() == 9)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Checks if a wall placement is adjacent to a player
	 * 
	 * @param move
	 *            the wall placement
	 * @return true if it is adjacent, false if not
	 */
	private boolean isAdjacent(Move move) {
		Point player = game.players().other(game.myTurn()).pawn();
		int direction;

		if (game.myTurn().equals(game.players()._1())) {
			direction = 1;
		} else {
			direction = -1;
		}

		if (direction == -1) {
			if (move.coord().x() == player.x()
					&& move.coord().y() == player.y()
					|| (move.coord().x() == player.x() - 1
							&& move.coord().y() == player.y() && move
							.direction() == MoveType.HORIZONTAL)
					|| (move.coord().x() == player.x()
							&& move.coord().y() == player.y() - 1 && move
							.direction() == MoveType.VERTICAL)
					|| (move.coord().x() == player.x() + 1
							&& move.coord().y() == player.y() && move
							.direction() == MoveType.VERTICAL)
					|| (move.coord().x() == player.x() + 1
							&& move.coord().y() == player.y() - 1 && move
							.direction() == MoveType.VERTICAL)) {

				return true;
			}
		} else {
			if ((move.coord().x() == player.x()
					&& move.coord().y == player.y() - 1 && move.direction() == MoveType.VERTICAL)
					|| (move.coord().x() == player.x()
							&& move.coord().y == player.y() && move.direction() == MoveType.VERTICAL)
					|| (move.coord().x() == player.x() + 1
							&& move.coord().y == player.y() - 1 && move
							.direction() == MoveType.VERTICAL)
					|| (move.coord().x() == player.x() + 1
							&& move.coord().y == player.y() && move.direction() == MoveType.VERTICAL)
					|| (move.coord().x() == player.x() - 1
							&& move.coord().y() == player.y() && move
							.direction() == MoveType.HORIZONTAL)
					|| (move.coord().x() == player.x()
							&& move.coord().y() == player.y() + 1 && move
							.direction() == MoveType.HORIZONTAL)) {

				return true;
			}
		}

		return false;
	}

	/**
	 * Finds the distance of the wall placement to the opponent
	 * 
	 * @param move
	 *            the placement of the wall
	 * @return the int value of the straight line distance
	 */
	private int wallDistance(Move move, Game game, int i) {
		Point otherPlayer;

		if (i == 0) {
			otherPlayer = game.players().other(game.myTurn()).pawn();
		} else {
			otherPlayer = game.myTurn().pawn();
		}

		return (int) Math
				.sqrt(Math.pow((otherPlayer.x() - move.coord().x()), 2)
						+ Math.pow((otherPlayer.y() - move.coord().y()), 2));
	}

	/**
	 * Creates a copy of a game with the exact game state
	 * 
	 * @param moves
	 *            the list of all moves made so far
	 * @return a Game
	 */
	private Game createTempGame(LinkedList<Move> moves) {
		Player tempPl1 = new Human("Player 1");
		Player tempPl2 = new Human("Player 2");
		Game tempGame = new Game(Two.two(tempPl1, tempPl2));

		tempGame.initGame(null);
		for (int j = 0; j < moves.size(); j++) {
			tempGame.move(moves.get(j), tempGame.myTurn());
		}

		return tempGame;
	}

	/**
	 * Finds all the possible moves of the current player in a game
	 * 
	 * @param g
	 *            the game - type Game
	 * @return ArrayList of moves
	 */
	private ArrayList<Move> findPossibleMoves(Game g) {
		ArrayList<Move> possibleMoves = new ArrayList<Move>();
		ArrayList<Move> checkList = new ArrayList<Move>();
		Point current = g.myTurn.pawn();

		checkList.add(new Move(current.x() + 1, current.y(), MoveType.PAWN));
		checkList.add(new Move(current.x() + 2, current.y(), MoveType.PAWN));
		checkList.add(new Move(current.x(), current.y() + 1, MoveType.PAWN));
		checkList.add(new Move(current.x(), current.y() + 2, MoveType.PAWN));
		checkList.add(new Move(current.x() - 1, current.y(), MoveType.PAWN));
		checkList.add(new Move(current.x() - 2, current.y(), MoveType.PAWN));
		checkList.add(new Move(current.x(), current.y() - 1, MoveType.PAWN));
		checkList.add(new Move(current.x(), current.y() - 2, MoveType.PAWN));
		checkList
				.add(new Move(current.x() - 1, current.y() - 1, MoveType.PAWN));
		checkList
				.add(new Move(current.x() + 1, current.y() - 1, MoveType.PAWN));
		checkList
				.add(new Move(current.x() - 1, current.y() + 1, MoveType.PAWN));
		checkList
				.add(new Move(current.x() + 1, current.y() + 1, MoveType.PAWN));
		checkList
				.add(new Move(current.x() - 1, current.y() + 1, MoveType.PAWN));

		// add walls to checklist
		if (g.myTurn().wallsLeft() > 0) {
			for (int i = 1; i < 9; i++) {
				for (int j = 1; j < 9; j++) {
					checkList.add(new Move(j, i, MoveType.HORIZONTAL));
					checkList.add(new Move(j, i, MoveType.VERTICAL));
				}
			}
		}

		for (int i = 0; i < checkList.size(); i++) {
			if (g.isValid(checkList.get(i), g.myTurn())) {
				possibleMoves.add(checkList.get(i));
			}
		}

		return possibleMoves;
	}

}