package quoridor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import quoridor.Command.CommandType;
import util.Two;

/**
 * Game factory is in charge of input, creating games, loading games. 
 * 
 * <h2>Goals</h2>
 * <ul>
 * <li>Get Input.</li>
 * <li>Send the Input to get parsed.</li>
 * <li>Creates games and is able to load them from a file or initialize them from a list of moves.</li>
 * </ul> 
 * 
 * <h2>Implementation</h2>
 * <ul>
 * <li>Main Class of the program. Display prompts and ask for input.</li>
 * <li>Uses the Command class to parse a String into an actual Command.</li>
 * <li>Creates new and loaded Games, and initializes them with a Command.</li>
 * </ul>
 * 
 * 
 * @author Sacha BŽraud <sacha.beraud@gmail.com>
 *
 */

public class GameFactory {
	
	static Random random;

	/**
	 * Main Function of the program. It's where it all start.
	 * @param args Arguments passed to the main function of the program to trigger specific use of the program. Not used here.
	 */
	public static void main(String[] args){

		System.out.println("Welcome to Quoridor AssQuad666 !");
		random =  new Random();
		random.setSeed(System.currentTimeMillis());
		run3();
//		while(true){
//			run2();
//		}
	}
	
	private static float randomFloat(float range) {
		return (float) (Math.random() * (2 * range) - range);
	}

	public static void run3() {
		
		// create our population
		int popSize = 5;
		float hasFeature = 0.8f;
		float range = 3.0f;
		Subject[] pop;
		pop  = createPopulation(popSize, hasFeature, range);
		
		// print out initial weights
		for(Subject s : pop) {
			System.out.println(s.weights[0] + " " + s.weights[1] + " "  + s.weights[2] + " "  + s.weights[3] );
		}
		
		// go one generation
		pop = runOneGeneration(pop);
		pop = spawn(pop);
		for(Subject s : pop) {
			System.out.println(s.weights[0] + " " + s.weights[1] + " "  + s.weights[2] + " "  + s.weights[3] );
		}
	}
	
	private static Subject[] runOneGeneration(Subject[] pop) {
		for(Subject s : pop) s.fitnessFunction = 0;
		for(int i = 0; i < pop.length; i++) {
			for(int j = 0; j < pop.length; j++) {
				if(i == j) continue;
				if(playGame(pop[i], pop[j]) == 1) {
					pop[i].fitnessFunction++;
				} else
					pop[j].fitnessFunction++;
			}
		}
		return pop;
	}
	
	private static Subject[] spawn(Subject[] pop) {
		Subject[] nPop = new Subject[pop.length];
		Arrays.sort(pop, Collections.reverseOrder());
		nPop[0] = pop[0].clone();
		nPop[1] = reproduce(pop[1], pop[2], 0.2f, 0.2f, 0.5f, 3f);
		nPop[2] = reproduce(pop[1], pop[2], 0.2f, 0.2f, 0.5f, 3f);
		nPop[3] = reproduce(pop[1], pop[2], 0.2f, 0.2f, 0.5f, 3f);
		nPop[4] = reproduce(pop[3], pop[4], 0.2f, 0.2f, 0.5f, 3f);
		return nPop;
	}
	
	private static Subject reproduce(Subject s1, Subject s2, float mutation, float loseFeature, float featMutate, float featRange) {
		float[] m = s1.weights;
		float[] f = s2.weights;
		float[] c = new float[s1.weights.length];
		
		for(int i = 0; i < c.length; i++) {
			if(random.nextFloat() > 0.5) 
				c[i] = m[i];
			else 
				c[i] = f[i];
			if(c[i] == 0) {
				if(random.nextFloat() < mutation)
					c[i] = randomFloat(featRange);
			} else {
				if(random.nextFloat() < loseFeature) 
					c[i] = 0;
				else {
					c[i] += randomFloat(featMutate);
				}
			}
		}
		return new Subject(c);
	}

	private static Subject[] createPopulation(int popSize, float hasFeature, float range) {
		Subject[] pop = new Subject[popSize];
		// currently 4 features
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		for(int j = 0; j < popSize; j++) {
			float[] weights = new float[4];
			for(int i = 0; i < 4; i++) {
				if(random.nextFloat() < hasFeature) {
					weights[i] = (random.nextFloat() * 2 * range) - range;
				} else {
					weights[i] = 0.0f;
				}
			}
			pop[j] = new Subject(weights);
		}
		
		return pop;
	}
	
	private static int playGame(Subject subject, Subject subject2) {
		Two<Player> players = null;
		Player playerOne;
		Player playerTwo;
		playerOne = new AIPlayer("Computer 1", subject.weights);
		playerTwo = new AIPlayer("Computer 2", subject2.weights);
		players = Two.two(playerOne, playerTwo);
		String winner = newGame(players);
		if (winner.equals("Computer 1")) {
			return 1;
		} else if (winner.equals("Computer 2")) {
			return 2;
		}
		return 0;
	}
	
	public static void run2() {
		Two<Player> players = null;
		Player playerOne;
		Player playerTwo;
		playerOne = new AIPlayer("Computer 1", new float[] { 0.0f, -1f, 1f, 1f });
		playerTwo = new AIPlayer("Computer 2", new float[] { 0.0f, -1f, 1f, 1f });
		//playerTwo = new AIPlayer("Computer 2", new float[] { 0.0f, -0.8f, 1f, 1.2f });
		players = Two.two(playerOne, playerTwo);
		
		int[] gamesWon = new int[] {0, 0};
		
		for (int i = 0; i < 20; i++) {
			playerOne = new AIPlayer("Computer 1", new float[] { 0.0f, -1f, 1f, 1f });
			playerTwo = new AIPlayer("Computer 2", new float[] { 0.0f, -1f, 1f, 1f });
			//playerTwo = new AIPlayer("Computer 2", new float[] { 0.0f, -0.8f, 1f, 1.2f });
			players = Two.two(playerOne, playerTwo);
			String winner = newGame(players);
			if (winner.equals("Computer 1")) {
				gamesWon[0]++;
			} else if (winner.equals("Computer 2")) {
				gamesWon[1]++;
			}
		}
		
		System.out.println("Final score: " + "C1:" + gamesWon[0] + " C2:" + gamesWon[1]);
		
	}

	/**
	 * runs the Game Factory by getting input, calling some parsing and calling appropriate functions.
	 */
	public static void run(){
		Scanner input = new Scanner (System.in);
		Two<Player> players = null;
		System.out.println("Input a command:");
		String line = input.nextLine ().toLowerCase ();
		Command command = new Command(line);

		while(command.type().equals(CommandType.INVALID) 
				|| command.type().equals(CommandType.MOVE) 
				|| command.type().equals(CommandType.SAVE_GAME)
				|| command.type().equals(CommandType.UNDO)
				|| command.type().equals(CommandType.REDO)
				|| command.type().equals(CommandType.HELP)) {

			if (command.type().equals(CommandType.MOVE)){
				System.out.println("You need to make a new game before making a move, try again:");
			} else if(command.type().equals(CommandType.SAVE_GAME)){
				System.out.println("You need to make a new game before saving, try again:");
			} else if(command.type().equals(CommandType.UNDO)){
				System.out.println("You need to make a new game before using undo, try again:");
			} else if(command.type().equals(CommandType.REDO)){
				System.out.println("You need to make a new game before using redo, try again:");
			} else{
				System.out.println("Input a command:");
			}
			line = input.nextLine ().toLowerCase ();	
			command = new Command(line);
		}

		players = getPlayers();

		if(command.type().equals(CommandType.NEW_GAME)){
			newGame(players);

		} else if(command.type().equals(CommandType.LOAD_GAME)){
			loadGame(command.fileName(), players);

		} else if(command.type().equals(CommandType.NEW_WITH_MOVES)){
			newGameWithMoves(command.moves(), players);	
		} 


	}


	/**
	 * Get the players playing the game, as well as their type (AI or Human) and their name.
	 * @return a set of Two Players
	 */
	public static Two<Player> getPlayers(){
		Scanner input = new Scanner (System.in);
		Player playerOne;
		Player playerTwo;

		System.out.println("How many AI players in this game? Enter 0, 1, or 2.");
		String line = input.nextLine ().toLowerCase ();
		if(line.isEmpty())
			return getPlayers();


		if(line.charAt(0) == '0'){
			System.out.println("Enter your name, player one:");
			line = input.nextLine ().toLowerCase ();
			playerOne = new Human(line);
			System.out.println("Enter your name, player two:");
			line = input.nextLine ().toLowerCase ();
			playerTwo = new Human(line);
		} else if (line.charAt(0) == '1'){
			System.out.println("Enter your name:");
			line = input.nextLine ().toLowerCase ();
			playerOne = new Human(line);
			playerTwo = new AIPlayer("Computer");
			playerTwo.level = getAILevel(2);		
		} else if (line.charAt(0) == '2'){
			playerOne = new AIPlayer("Computer 1");
			playerOne.level = getAILevel(1);
			playerTwo = new AIPlayer("Computer 2");
			playerTwo.level = getAILevel(2);
		} else {
			System.out.println(line+" is not a valid number of AI players.");
			return getPlayers();
		}
		return Two.two(playerOne, playerTwo);
	}


	/**
	 * Get a level for an AI playing using stdin.
	 * @param player the AI playing
	 * @return a string representing the level of the AI: random, naive or pro.
	 */
	public static String getAILevel(int player){
		Scanner input = new Scanner (System.in);
		String line;
		System.out.println("What level is the AI "+player+" (random, naive or pro):");
		line = input.nextLine ().toLowerCase ();
		if(line.startsWith("random")){
			return "random";
		} else if(line.startsWith("naive")){
			return "naive";
		} else if(line.startsWith("pro")){
			return "pro";
		} else {
			System.out.println("Sorry, this is not a correct answer.");
			return getAILevel(player);
		}
	}



	/**
	 * Creates a new Game. Initializes it.
	 */
	public static String newGame(Two<Player> players) {
		System.out.println("Making a new game...");
		Game game = new Game(players);
		game.initGame(null);
		return game.play();
	}

	/**
	 * Creates a Game. Initializes it to the state given in the file.
	 * @param fileName The name of the file from which the game will be loaded.
	 */
	public static void loadGame(String fileName, Two<Player> players){
		System.out.println("Loading a game...");
		String line = readFromFile(fileName);
		if (line == null){
			return;
		}
		Command c = new Command(line);
		if(c.type().equals(CommandType.MOVES) || c.type().equals(CommandType.MOVE)){
			Validator v = new Validator();
			if(v.check(c.moves())){
				Game game = new Game(players);
				game.initGame(c.moves);
				game.play();
			} else {
				System.out.println("Invalid sequence of moves in the file.");
			}
		}

	}

	/**
	 * Creates a Game. Initializes it to the state given by a list of moves input in stdin.
	 * @param moves The moves input to which the game should be initialized.
	 */
	public static void newGameWithMoves(LinkedList<Move> moves, Two<Player> players){
		System.out.println("Making a new game with initialisation...");
		Validator v = new Validator();
		if (v.check(moves)){
			Game game = new Game(players);
			game.initGame(moves);
			game.play();
		} else {
			System.out.println("This not a valid sequence of moves.");
		}
	}




	/**
	 * Reads data from a given file.
	 * @param fileName the name of the file
	 * @return the first line from the file.
	 */
	public static String readFromFile(String fileName) {
		String DataLine = "";
		try {
			File inFile = new File(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(inFile)));

			DataLine = br.readLine();
			br.close();
		} catch (FileNotFoundException ex) {
			System.out.println("This file cannot be found.");
			return (null);
		} catch (IOException ex) {
			System.out.println("IO exception.");
			return (null);
		}
		return (DataLine);

	}
}
