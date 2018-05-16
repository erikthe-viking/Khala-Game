import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.concurrent.*;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;


public class Manager {
	//MENU SCREEN COMPONENTS
	private JFrame menuFrame;

	AI robot;
	AI robot_2;

	private JButton newGameButton;
	private JSpinner pitCountInput;
	private JSpinner timeLimitInput;
	private JComboBox<String> systemTypesInput;
	private JComboBox<String> gameModesInput;
	private JCheckBox randomDistroCheck;
	private JCheckBox enableTimerCheck;

	//GAME MANAGER COMPONENTS -- FIX: add GUI?
	private Interface GUI;
	private kalah currentGame; //current game to render - FIX: remove this from main; instantiate in drawGameBoard using params
	private int pitCount; 	   //pits per side
	private int gameMode;      //current mode (pass into kalah)
	private int timeLimit;    //move time (in seconds)
	private boolean timed;
	private boolean gameStart;

	private boolean pieRule;


	private boolean p1timeOut;
	private boolean p2timeOut;


	private String network;

	private String nextMove;

	public Manager() {	//pitCount refers to # of pits per player

		menuFrame = new JFrame("Mancala");
		menuFrame.setSize(320, 200);

		//parent container for settings widgets
		JPanel settingsPanel = new JPanel();
		settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

		//pit count input
		JPanel pitCountInputPanel = new JPanel(new BorderLayout());
		JLabel pitCountInputLabel = new JLabel("Pits per player");

		settingsPanel.add(pitCountInputLabel, BorderLayout.WEST);
		pitCountInput = new JSpinner(new SpinnerNumberModel(6, 1, 10, 1));

		pitCountInputPanel.add(pitCountInput, BorderLayout.EAST);
		settingsPanel.add(pitCountInputPanel);
    
		//time limit input
		JPanel timeLimitInputPanel = new JPanel(new BorderLayout());
		JLabel timeLimitInputLabel = new JLabel("Turn time limit (seconds)");

		settingsPanel.add(timeLimitInputLabel, BorderLayout.WEST);
		timeLimitInput = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));

		timeLimitInputPanel.add(timeLimitInput, BorderLayout.EAST);
		settingsPanel.add(timeLimitInputPanel);

		//enable timer check box
		JPanel timeLimitCheckPanel = new JPanel(new BorderLayout());
		enableTimerCheck = new JCheckBox("Enforce move time limit");
		enableTimerCheck.setHorizontalTextPosition(SwingConstants.LEFT);

		timeLimitCheckPanel.add(enableTimerCheck, BorderLayout.EAST);
		settingsPanel.add(timeLimitCheckPanel);

		//random seed distribution check box
		JPanel randomDistroCheckPanel = new JPanel(new BorderLayout());
		randomDistroCheck = new JCheckBox("Randomly distribute starting seeds");
		randomDistroCheck.setHorizontalTextPosition(SwingConstants.LEFT);

		randomDistroCheckPanel.add(randomDistroCheck, BorderLayout.EAST);
		settingsPanel.add(randomDistroCheckPanel);

		//system type input
		JPanel systemTypeInputPanel = new JPanel(new BorderLayout());
		JLabel systemTypeInputLabel = new JLabel("System type");

		String[] systemTypes = {"Local","Client","Host"};
		systemTypesInput = new JComboBox<String>(systemTypes);

		systemTypeInputPanel.add(systemTypeInputLabel, BorderLayout.WEST);
		systemTypeInputPanel.add(systemTypesInput, BorderLayout.EAST);

		settingsPanel.add(systemTypeInputPanel);

		//game mode input
		JPanel gameModesInputPanel = new JPanel(new BorderLayout());
		JLabel gameModesInputLabel = new JLabel("Game mode");

		String[] gameModes = {"Player vs. Player", "Player vs. AI"};
		gameModesInput = new JComboBox<String>(gameModes);

		gameModesInputPanel.add(gameModesInputLabel, BorderLayout.WEST);
		gameModesInputPanel.add(gameModesInput, BorderLayout.EAST);
		settingsPanel.add(gameModesInputPanel);


		//button to start a new game
		newGameButton = new JButton("New Game");
		newGameButton.addActionListener(new newGameBtnListener());
		settingsPanel.add(newGameButton);


		menuFrame.add(settingsPanel);
		menuFrame.setVisible(true);

		gameStart = false;
	}

	public kalah getCurrentGame() {
		return this.currentGame;
	}

	public int getMove() {
		return currentGame.moveCount;
	}

	// event handlers
	private class newGameBtnListener implements ActionListener { //event handler for new game button on main menu
		public void actionPerformed(ActionEvent e) {

			String dropDownInput = (String)gameModesInput.getSelectedItem();
			Boolean useRandomDistro = false;


			//enable random seed distribution
			if (randomDistroCheck.isSelected()) {
				useRandomDistro = true;
			}


			//enable move timer & set timer length
			if (enableTimerCheck.isSelected()) {
				timed = true;
				timeLimit = (Integer)timeLimitInput.getValue();
			}
			else {
				timed = false;
			}


			//set game mode from dropdown
			if (dropDownInput.equals("Player vs. Player")) {
				gameMode = 1;
			}
			else if (dropDownInput.equals("Player vs. AI")) {
				gameMode = 0;
			}
			else {
				gameMode = -1;
			}


			//set network mode from dropdown
			if (systemTypesInput.equals("Local")) {
				network = "Local";
			}
			else if (systemTypesInput.equals("Client")) {
				network = "Client";
			}
			else {
				network = "Host";
			}

			//set pit count from input & pass to game
			pitCount = (Integer)pitCountInput.getValue();
			currentGame = new kalah(pitCount, 4, useRandomDistro);
			menuFrame.setVisible(false);

			GUI = new Interface(currentGame);

			robot = new AI(0,0,4,currentGame.pitsPerSide);
			robot_2 = new AI(0,0,4,currentGame.pitsPerSide);



			GUI.renderBoard(currentGame.getState());

			pieRule = false;
			p1timeOut = false;
			p2timeOut = false;
      
			gameStart = true;

		}
	}


	// start game

	public void startGame() {

		while(!currentGame.gameOver) {
			requestMove();
		}
		GUI.renderBoard(currentGame.getState());
	}



	// get move from GUI
	public String getGUImove() {

		GUI.setInput(true);

		while( GUI.getLastInput() == "-1" ) {

			try {
				Thread.sleep(5);
			} catch (InterruptedException e){
				e.printStackTrace();
			}
		}

		GUI.setInput(false);

		String move = GUI.getLastInput();

		GUI.setLastInput("-1");

		return move;
    
	}

	public String getGUImoveTimed() {


		ExecutorService service = Executors.newSingleThreadExecutor();

		try {
			Runnable r = new Runnable() {
				@Override
				public void run() {


					GUI.setInput(true);

					while( GUI.getLastInput() == ("-1") ) {
						try {
							Thread.sleep(5);
						} catch (InterruptedException e){
							e.printStackTrace();
						}
					}

					GUI.setInput(false);

					nextMove = GUI.getLastInput();

					GUI.setLastInput("-1");
				}
			};

			Future<?> f = service.submit(r);

			f.get(timeLimit, TimeUnit.SECONDS);
		}
		catch (final InterruptedException e) {
			e.printStackTrace();
			nextMove = "-1";
		}
		catch (final TimeoutException e) {
			// the time is up

			currentGame.gameOver = true;

			if(currentGame.player2Turn) {
				p2timeOut = true;
				currentGame.winner = 1;
			}
			else {
				p1timeOut = true;
				currentGame.winner = 2;
			}

			nextMove = ("-1");
		}
		catch (final ExecutionException e) {
			e.printStackTrace(); // an exception within the runnable task
			nextMove = "-1";
		}
		finally {
			service.shutdown();
		}

		return nextMove;
	}

	// can return other types
	// prints to terminal for now
	public String requestMove() {

		//currentGame.printToTerminal();

		String move = "100";

		if(getMove()==2){
			GUI.hidePieRule();
		}

		if(getMove()==1){
			GUI.displayPieRule();
		}

		currentGame.checkGameOver();
		GUI.renderBoard(currentGame.getState());


		if (gameMode == 0) {

			// AI move
			if (!currentGame.player2Turn) {
				try {
					Thread.sleep(1000);
					move = Integer.toString(robot.int_run(currentGame.board));
					GUI.blinkPit(Integer.parseInt(move));
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// Player move
			else {
				// take input
				if (timed) {
					move = getGUImoveTimed();
				}
				else {
					move = getGUImove();
				}
			}

		}

		else {

			if (timed) {
				move = getGUImoveTimed();
			}
			else {
				move = getGUImove();
			}

		}


		// handle game over
		if(move == "-1") {
			return "";
		}


		// enforce pie rule
		if(move == "pie") {

			currentGame.switchSides();

			currentGame.moveCount++;

			GUI.renderBoard(currentGame.getState());

			return "P";
		}


		// check move
		Boolean validMove = currentGame.move(Integer.parseInt(move));

		if(!validMove) {
			requestMove();
		}

		return move;

	}

	public static void main(String[] args)
			throws InterruptedException {

		Manager manager = new Manager();

		while ( !manager.gameStart ) {
			Thread.sleep(5);
		}

		manager.startGame();

	}
}