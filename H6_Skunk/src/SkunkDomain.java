import java.util.ArrayList;
import edu.princeton.cs.introcs.*;

public class SkunkDomain
{
	private static final int SKUNK_DOUBLE_SUM = 2;
	private static final int SKUNK_DEUCE_SUM = 3;
	public SkunkUI skunkUI;
	public UI ui;
	public int numberOfPlayers;
	public String[] playerNames;
	public ArrayList<Player> players;
	public int kitty;

	public Player activePlayer;
	public int activePlayerIndex;

	public boolean wantsToQuit;
	public boolean oneMoreRoll;

	public Dice skunkDice;

	public SkunkDomain(SkunkUI ui)
	{
		this.skunkUI = ui;
		this.ui = ui; // hide behind the interface UI
		
		this.playerNames = new String[20];
		this.players = new ArrayList<Player>();
		this.skunkDice = new Dice();
		this.wantsToQuit = false;
		this.oneMoreRoll = false;
	}

	public boolean run()
	{
		ui.println("Welcome to Skunk 0.47\n");

		String numberPlayersString = skunkUI.promptReadAndReturn("How many players?");
		this.numberOfPlayers = Integer.parseInt(numberPlayersString);

		for (int playerNumber = 0; playerNumber < numberOfPlayers; playerNumber++)
		{
			ui.print("Enter name of player " + (playerNumber + 1) + ": ");
			playerNames[playerNumber] = StdIn.readLine();
			this.players.add(new Player(50));
		}
		activePlayerIndex = 0;
		activePlayer = players.get(activePlayerIndex);

		ui.println("Starting game...\n");
		boolean gameNotOver = true;

		while (gameNotOver)
		{
			ui.println("Next player is " + playerNames[activePlayerIndex] + ".");
			activePlayer.setTurnScore(0);
			
			boolean wantsToRoll = getRollChoice(); // 1st Refactor -> Extract method getRollChoice()
			
			while (wantsToRoll)
			{
				activePlayer.setRollScore(0);
				skunkDice.roll();
				if (isDoubleSkunk()) // 4th refactor --> extract method isDoubleSkunk()
				{
					ui.println("Two Skunks! You lose the turn, zeroing out both turn and game scores and paying 4 chips to the kitty");
					kitty += 4;
					activePlayer.scoreSkunkRoll(4); // 6th refactor --> Create scoreSkunkRoll method in Player class. 
					activePlayer.setGameScore(0);
					wantsToRoll = false;
					break;
				}
				else if (isSkunkDeuce())  // 3rd refactor --> extract method isSkunkDeuce()
				{
					ui.println(
							"Skunks and Deuce! You lose the turn, zeroing out the turn score and paying 2 chips to the kitty");
					kitty += 2;
					activePlayer.scoreSkunkRoll(2); // 6th refactor --> Create scoreSkunkRoll method in Player class. 
					wantsToRoll = false;
					break;
				}
				else if (isRegularSkunk())  // 2nd refactor --> extract method isRegularSkunk()
				{
					ui.println("One Skunk! You lose the turn, zeroing out the turn score and paying 1 chip to the kitty");
					kitty += 1;
					activePlayer.scoreSkunkRoll(2); // 6th refactor --> Create scoreSkunkRoll method in Player class. 
					wantsToRoll = false;
					break;

				}

				activePlayer.setRollScore(skunkDice.getLastRoll());
				activePlayer.setTurnScore(activePlayer.getTurnScore() + skunkDice.getLastRoll());
				ui.println(
						"Roll of " + skunkDice.toString() + ", gives new turn score of " + activePlayer.getTurnScore());

				wantsToRoll = getRollChoice(); // part of 1st refactor --> Extract method getRollChoice()

			}

			ui.println("End of turn for " + playerNames[activePlayerIndex]);
			ui.println("Score for this turn is " + activePlayer.getTurnScore() + ", added to...");
			ui.println("Previous game score of " + activePlayer.getGameScore());
			activePlayer.setGameScore(activePlayer.getGameScore() + activePlayer.getTurnScore());
			ui.println("Gives new game score of " + activePlayer.getGameScore());

			ui.println("");
			if (activePlayer.getGameScore() >= 100)
				gameNotOver = false;

			ui.println("Scoreboard: ");
			ui.println("Kitty has " + kitty + " chips.");
			ui.println("Player name -- Turn score -- Game score -- Total chips");
			ui.println("-----------------------");

			for (int i = 0; i < numberOfPlayers; i++)
			{
				ui.println(playerNames[i] + " -- " + players.get(i).getTurnScore() + " -- " + players.get(i).getGameScore()
						+ " -- " + players.get(i).getNumberChips());
			}
			ui.println("-----------------------");

			ui.println("Turn passes to right...");

			activePlayerIndex = (activePlayerIndex + 1) % numberOfPlayers;
			activePlayer = players.get(activePlayerIndex);

		}
		// last round: everyone but last activePlayer gets another shot

		ui.println("**** Last turn for all... ****");

		for (int i = activePlayerIndex, count = 0; count < numberOfPlayers-1; i = (i++) % numberOfPlayers, count++)
		{
			ui.println("Last turn for player " + playerNames[activePlayerIndex] + "...");
			activePlayer.setTurnScore(0);

			String wantsToRollStr = ui.promptReadAndReturn("Roll? y or n");
			boolean wantsToRoll = 'y' == wantsToRollStr.toLowerCase().charAt(0);

			while (wantsToRoll)
			{
				skunkDice.roll();
				ui.println("Roll is " + skunkDice.toString() + "\n");

				if (isDoubleSkunk())
				{
					ui.println("Two Skunks! You lose the turn, zeroing out both turn and game scores and paying 4 chips to the kitty");
					kitty += 4;
					activePlayer.setNumberChips(activePlayer.getNumberChips() - 4);
					activePlayer.setTurnScore(0);
					activePlayer.setGameScore(0);
					wantsToRoll = false;
					break;
				}
				else if (isSkunkDeuce())
				{
					ui.println(
							"Skunks and Deuce! You lose the turn, zeroing out the turn score and paying 2 chips to the kitty");
					kitty += 2;
					activePlayer.setNumberChips(activePlayer.getNumberChips() - 2);
					activePlayer.setTurnScore(0);
					wantsToRoll = false;

				}
				else if (isRegularSkunk())
				{
					ui.println("One Skunk!  You lose the turn, zeroing out the turn score and paying 1 chip to the kitty");
					kitty += 1;
					activePlayer.setNumberChips(activePlayer.getNumberChips() - 1);
					activePlayer.setTurnScore(0);
					wantsToRoll = false;
				}
				else
				{
					activePlayer.setTurnScore(activePlayer.getRollScore() + skunkDice.getLastRoll());
					ui.println("Roll of " + skunkDice.toString() + ", giving new turn score of "
							+ activePlayer.getTurnScore());

					ui.println("Scoreboard: ");
					ui.println("Kitty has " + kitty);
					ui.println("Player name -- Turn score -- Game score -- Total chips");
					ui.println("-----------------------");

					for (int pNumber = 0; pNumber < numberOfPlayers; pNumber++)
					{
						ui.println(playerNames[pNumber] + " -- " + players.get(pNumber).turnScore + " -- "
								+ players.get(pNumber).getGameScore() + " -- " + players.get(pNumber).getNumberChips());
					}
					ui.println("-----------------------");

					wantsToRollStr = ui.promptReadAndReturn("Roll again? y or n");
					wantsToRoll = 'y' == wantsToRollStr.toLowerCase().charAt(0);
				}

			}

			activePlayer.setTurnScore(activePlayer.getRollScore() + skunkDice.getLastRoll());
			ui.println("Final roll of " + skunkDice.toString() + ", giving final game score of "
					+ activePlayer.getRollScore());

		}

		int winner = 0;
		int winnerScore = 0;

		for (int player = 0; player < numberOfPlayers; player++)
		{
			Player nextPlayer = players.get(player);
			ui.println("Final game score for " + playerNames[player] + " is " + nextPlayer.getGameScore());
			if (nextPlayer.getGameScore() > winnerScore)
			{
				winner = player;
				winnerScore = nextPlayer.getGameScore();
			}
		}

		ui.println(
				"Game winner is " + playerNames[winner] + " with score of " + players.get(winner).getGameScore());
		players.get(winner).setNumberChips(players.get(winner).getNumberChips() + kitty);
		ui.println("Game winner earns " + kitty + " chips , finishing with " + players.get(winner).getNumberChips());

		ui.println("\nFinal scoreboard for this game:");
		ui.println("Player name -- Game score -- Total chips");
		ui.println("-----------------------");

		for (int pNumber = 0; pNumber < numberOfPlayers; pNumber++)
		{
			ui.println(playerNames[pNumber] + " -- " + players.get(pNumber).getGameScore() + " -- "
					+ players.get(pNumber).getNumberChips());
		}

		ui.println("-----------------------");
		return true;
	}

	private boolean isDoubleSkunk() {
		return skunkDice.getLastRoll() == SKUNK_DOUBLE_SUM; // Refactor constant. SKUNK_DOUBLE_SUM is int 2
	}

	private boolean isSkunkDeuce() {
		return skunkDice.getLastRoll() == SKUNK_DEUCE_SUM; // Refactor constant. SKUNK_DEUCE_SUM is int 3
	}

	private boolean isRegularSkunk() {
		return skunkDice.getDie1().getLastRoll() == 1 || skunkDice.getDie2().getLastRoll() == 1;
	}

	private boolean getRollChoice() {
		String wantsToRollStr = ui.promptReadAndReturn("Roll? y or n"); // TODO Auto-generated method stub
		return 'y' == wantsToRollStr.toLowerCase().charAt(0);
	}

}
