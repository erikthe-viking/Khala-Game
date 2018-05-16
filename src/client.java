import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public class client {
	
	//NETWORKING MEMBERS 
	//connection
	private Socket socket;
	
	//read from/write to server (communication)
	private BufferedReader reader;
	private PrintWriter writer;
	
	//INTERFACE COMPONENTS
	private ArrayList<PitButton> gameButtons;
	private JLabel statusLabel;
	private JFrame mainFrame;
	
	//GAME COMPONENTS
	int playerNum;
	int lastMove;
	int pitCount;	//used to filter moves locally (client-side)
	kalah currentGame;
	
	//instantiate a client, attempt a connection, and frame out the window 
	public client(String serverAddress, int portNum) throws Exception {
		//establish connection
		socket = new Socket(serverAddress, portNum);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new PrintWriter(socket.getOutputStream(), true);
		
		//build game window 
		gameButtons = new ArrayList<PitButton>();
		
		mainFrame = new JFrame("Player " + playerNum);
		mainFrame.setSize(1280, 720); //FIX: these are temporarily hard-coded
	}
	
	//draw the game's buttons in the main window 
	public void buildBoardButtons(int pitCount, int seedsPerPit) {
		this.pitCount = pitCount;
		int store1Index = pitCount;
		int store2Index = 2 * pitCount + 1;
		int boardSize = 2 * pitCount + 2; //count both players' pits and both houses
		
		
				//parent container for all game rendering (board, status, etc.)
				JPanel parentPanel = new JPanel(new BorderLayout());
				//panel responsible for rendering the board -- more will be added later 
				JPanel gamePanel = new JPanel(new GridBagLayout());
				gamePanel.setBackground(Color.DARK_GRAY);
				
				//status label
				statusLabel = new JLabel("Player 1's turn.");
				statusLabel.setHorizontalTextPosition(SwingConstants.CENTER);
				statusLabel.setFont(statusLabel.getFont().deriveFont(32.0f));
				
				parentPanel.add(statusLabel, BorderLayout.NORTH);
				
				//constraints used to define the grid for board layout
				GridBagConstraints boardConstraints = new GridBagConstraints();
				boardConstraints.fill = GridBagConstraints.HORIZONTAL;
				
				//starting coordinates for board layout (start at leftmost pit of player 1's board half)
				boardConstraints.gridx = 0;
				boardConstraints.gridy = 1;
				
				//button dimensions 
				boardConstraints.ipadx = 20;
				boardConstraints.ipady = 60; 
				
				//space between each button
				boardConstraints.insets = new Insets(10, 10, 10, 10);
				
				//get the initial board state for drawing 
				
				for (int i = 0; i < boardSize; i++) {
					PitButton tempButton = new PitButton(i, 4);
					
					//define button visuals
					tempButton.setBorderPainted(false);
					tempButton.setText(Integer.toString(seedsPerPit));	
					tempButton.setBackground(Color.LIGHT_GRAY);
					
					//define which pit the current button represents 
					tempButton.boardIndex = i;
					
					//register event handler 
					tempButton.addActionListener(new buttonListener()); 
					
					if (i < store1Index) {
						//draw player 1's half
						boardConstraints.gridx++;
						boardConstraints.anchor = GridBagConstraints.NORTH;
						
						gamePanel.add(tempButton, boardConstraints);
					}
					else if (i == store1Index) {
						tempButton.isHouse = true;
						tempButton.numSeeds = 0;
						tempButton.setText("0");
						
						//draw player 1's store
						boardConstraints.gridx++;
						boardConstraints.gridy--;
						boardConstraints.gridheight = 2;	//stretch to fill both rows
						boardConstraints.fill = GridBagConstraints.VERTICAL;
						
						gamePanel.add(tempButton, boardConstraints);
					}
					else if (i > store1Index && i < store2Index) {
						//draw player 2's half (right to left)
						boardConstraints.gridx--;
						boardConstraints.gridheight = 1;
						
						gamePanel.add(tempButton, boardConstraints);
					}
					else {
						tempButton.isHouse = true;
						tempButton.numSeeds = 0;
						tempButton.setText("0");
						
						//draw player 2's store
						boardConstraints.gridx--;
						boardConstraints.gridheight = 2;
						boardConstraints.fill = GridBagConstraints.VERTICAL;
						
						gamePanel.add(tempButton, boardConstraints);
					}
					gameButtons.add(tempButton);
				}
				
				parentPanel.add(gamePanel);
				mainFrame.add(parentPanel);
				mainFrame.setVisible(true);
	}
	
	
	public void buildBoardButtons(int pitCount, ArrayList<String> randomSeeds) {
		int store1Index = pitCount;
		int store2Index = 2 * pitCount + 1;
		int boardSize = 2 * pitCount + 2; //count both players' pits and both houses
		
		
		//parent container for all game rendering (board, status, etc.)
				JPanel parentPanel = new JPanel(new BorderLayout());
				//panel responsible for rendering the board -- more will be added later 
				JPanel gamePanel = new JPanel(new GridBagLayout());
				gamePanel.setBackground(Color.DARK_GRAY);
				
				//status label
				statusLabel = new JLabel("Player 1's turn.");
				statusLabel.setHorizontalTextPosition(SwingConstants.CENTER);
				statusLabel.setFont(statusLabel.getFont().deriveFont(32.0f));
				
				parentPanel.add(statusLabel, BorderLayout.NORTH);
				
				//constraints used to define the grid for board layout
				GridBagConstraints boardConstraints = new GridBagConstraints();
				boardConstraints.fill = GridBagConstraints.HORIZONTAL;
				
				//starting coordinates for board layout (start at leftmost pit of player 1's board half)
				boardConstraints.gridx = 0;
				boardConstraints.gridy = 1;
				
				//button dimensions 
				boardConstraints.ipadx = 20;
				boardConstraints.ipady = 60; 
				
				//space between each button
				boardConstraints.insets = new Insets(10, 10, 10, 10);
				
				//get the initial board state for drawing 
				
				for (int i = 0; i < boardSize; i++) {
					PitButton tempButton = new PitButton(i, 4);
					
					//define button visuals
					tempButton.setBorderPainted(false);
					tempButton.setBackground(Color.LIGHT_GRAY);
					
					//define which pit the current button represents 
					tempButton.boardIndex = i;
					
					//register event handler 
					tempButton.addActionListener(new buttonListener()); 
					
					if (i < store1Index) {
						//draw player 1's half
						boardConstraints.gridx++;
						boardConstraints.anchor = GridBagConstraints.NORTH;
						
						gamePanel.add(tempButton, boardConstraints);
					}
					else if (i == store1Index) {
						tempButton.isHouse = true;
						tempButton.numSeeds = 0;
						//draw player 1's store
						boardConstraints.gridx++;
						boardConstraints.gridy--;
						boardConstraints.gridheight = 2;	//stretch to fill both rows
						boardConstraints.fill = GridBagConstraints.VERTICAL;
						
						gamePanel.add(tempButton, boardConstraints);
					}
					else if (i > store1Index && i < store2Index) {
						//draw player 2's half (right to left)
						boardConstraints.gridx--;
						boardConstraints.gridheight = 1;
						
						gamePanel.add(tempButton, boardConstraints);
					}
					else {
						tempButton.isHouse = true;
						tempButton.numSeeds = 0;
						//draw player 2's store
						boardConstraints.gridx--;
						boardConstraints.gridheight = 2;
						boardConstraints.fill = GridBagConstraints.VERTICAL;
						
						gamePanel.add(tempButton, boardConstraints);
					}
					gameButtons.add(tempButton);
				}
				
				//set button labels based on random distro
				for (int i = 0; i < randomSeeds.size(); i++) {
					//set player 1's pits
					gameButtons.get(i).setText(randomSeeds.get(i));
					//set player 2's pits
					gameButtons.get(2 * i + 1).setText(randomSeeds.get(i));
				}
				
				parentPanel.add(gamePanel);
				mainFrame.add(parentPanel);
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainFrame.setVisible(true);
	}
	
	public void drawBoard() {
		ArrayList<Integer> gameState = currentGame.getState();
		for (int i = 0; i < gameState.size(); i++) {
			gameButtons.get(i).setText(Integer.toString(gameState.get(i)));
		}
		
		//check game over
		if (currentGame.gameOver) {
			switch(currentGame.winner) {
				case 0:
					statusLabel.setText("Tie!");
					break;
				case 1:
					statusLabel.setText("Player 1 wins!");
					break;
				case 2:
					statusLabel.setText("Player 2 wins!");
					break;
			}
			return; //don't update player labels if game is over
		}
		
		//update status label
		if (currentGame.player2Turn) {
			statusLabel.setText("Player 2's turn.");
		}
		else {
			statusLabel.setText("Player 1's turn.");
		}
	}
	
	public void play() throws Exception {
		String serverResponse;
		
		try {
			while (true) {
				//get response from server 
				serverResponse = reader.readLine();
				if (!serverResponse.equals(null) && !serverResponse.equals("")) {
					if (serverResponse.startsWith("WELCOME")) {
						System.out.println("CLIENT " + playerNum + ": Connected to server.");
					} 
					
					//get game configuration from server to set up client's board locally 
					else if (serverResponse.startsWith("INFO")) {
						System.out.println("CLIENT " + playerNum + ": Received game info from server.");
						//get game information and draw board 

						//split game parameters by spaces
						String[] gameParams = serverResponse.split(" ");

						//extract params from array (index 0 is "INFO" command
						int pitCount = Integer.parseInt(gameParams[1]);
						int seedsPerPit = Integer.parseInt(gameParams[2]);
						int timeLimit = Integer.parseInt(gameParams[3]);

						String gameOrder = gameParams[4]; //where do we use this?
						String randomDistro = gameParams[5];

						//do we go first or second?
						if (gameOrder.equals("F")) {
							playerNum = 1;
							mainFrame.setTitle("Player 1");
						} else {
							playerNum = 2;
							mainFrame.setTitle("Player 2");
						}

						//check for random distro flag 
						if (randomDistro.equals("S")) {
							buildBoardButtons(pitCount, seedsPerPit);
							currentGame = new kalah(pitCount, seedsPerPit, false);
						} else {
							//random values from server
							ArrayList<String> randomSeeds = new ArrayList<String>();

							//there's probably a better way to do this... -- FIX
							for (int i = 6; i < 6 + pitCount; i++) {
								randomSeeds.add(gameParams[i]);
							}

							ArrayList<Integer> randomSeedsInts = new ArrayList<Integer>();
							for (int i = 0; i < randomSeeds.size(); i++) {
								randomSeedsInts.add(Integer.parseInt(randomSeeds.get(i)));
							}

							buildBoardButtons(pitCount, randomSeeds);
							currentGame = new kalah(0, pitCount, false, randomSeedsInts);
						}
						//tell server that we're set!
						drawBoard();
						System.out.println("CLIENT " + playerNum + ": Ready to play! (player2Turn == " + currentGame.player2Turn + ")");
						writer.println("READY");
					}  
					
					//if server sends opponent's move, issue it so the boards stay in sync
					else if (serverResponse.startsWith("MOVE")) {
						System.out.println("CLIENT " + playerNum + ": Received opponent's move(" + serverResponse.substring(5) + ") from server.");
						int move = Integer.parseInt(serverResponse.substring(5));
						
						if (currentGame.move(move)) {
							System.out.println("CLIENT " + playerNum + ": Issued opponent's move(" + move + ") locally.");
							currentGame.checkGameOver();
							drawBoard();
						}
					}
					
					//if we get the okay from server, issue the move locally to keep boards in sync 
					else if (serverResponse.startsWith("OK")) {
						System.out.println("CLIENT " + playerNum + ": received OK from server, issuing move " + lastMove + " locally.");
						if (currentGame.move(lastMove)) {
							System.out.println("CLIENT " + playerNum + ": Move successfully played. Drawing board.");
							currentGame.checkGameOver();
							drawBoard();
						}
					}
					
					//game over messages from server
					else if (serverResponse.startsWith("WINNER")) {
						System.out.println("CLIENT " + playerNum + ": winner!");
						currentGame.checkGameOver();
						drawBoard();
					}
					else if (serverResponse.startsWith("LOSER")) {
						System.out.println("CLIENT " + playerNum + ": loser!");
						currentGame.checkGameOver();
						drawBoard();
					}
				}
			}
		}
		finally {
			socket.close();
		}
	}
	
	//checks if a move is valid locally before sending it to the server 
	public boolean moveIsValid(int index) {
		if (playerNum == 1) {
			if (index < pitCount) {
				return true;
			}
			return false;
		}
		else {	//player 2 
			if (index > pitCount && index < 2 * pitCount + 1) {
				return true;
			}
			return false;
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		String serverAddress = JOptionPane.showInputDialog("Enter IP address to connect to: ");
		
		client playerClient = new client(serverAddress, 42069);
		playerClient.play();	
	}

	private class buttonListener implements ActionListener {
		//main handler for sending moves to game & updating board in window
		public void actionPerformed(ActionEvent e) {
			//get the widget which raised the event
			Object sourceObj = e.getSource(); 
			
			//check if widget is a button
			if (sourceObj instanceof JButton) {
				//cast the obj which raised the event
				PitButton sourceButton = (PitButton) sourceObj; 
				
				if (sourceButton.isHouse) {
					return; 	//players cannot interact with a house
				}
				else {
					//print local move message 
					System.out.println("CLIENT " + playerNum + ": Player " + playerNum + " clicked pit "
							+ sourceButton.boardIndex);
					
					if (moveIsValid(sourceButton.boardIndex)) {
						//send the move to server
						writer.println("MOVE " + sourceButton.boardIndex);
						System.out.println("CLIENT " + playerNum + ": sent move " + sourceButton.boardIndex + " to server.");
						
						lastMove = sourceButton.boardIndex;
					}	
					else {
						//if the client clicks any invalid spot 
						JOptionPane.showMessageDialog(mainFrame, "Invalid move (you are player " + playerNum + ").");
						System.out.println("CLIENT " + playerNum + ": invalid move (" + sourceButton.boardIndex + ") played locally."
								+ " Move not sent to server.");
					}
				}
			}
		}
	}
	
	private class PitButton extends JButton {
		//button used to represent pits/houses
		
		private int boardIndex; //the pit's position on the board (starting from p1 left house)
		private int numSeeds;   //seed count for current pit/house
		private boolean isHouse;
		
		public PitButton(int index, int seeds) {
			this.boardIndex = index;
			this.numSeeds = seeds;
			this.isHouse = false;
		}
	}
}
