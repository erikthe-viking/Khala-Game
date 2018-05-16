import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class server {
	  /**
     * Runs the application. Pairs up clients that connect.
	 * @throws IOException 
     */
	
	//server must keep track of game state 
	private kalah currentGame;
	private int pitsPerPlayer;
	private int seedsPerPit;
	private int timerValMils; 
	private boolean randomDistro;
	
	public server(int pitsPerPlayer, int seedsPerPit, int timerValMils, boolean randomDistro) {
		this.pitsPerPlayer = pitsPerPlayer;
		this.seedsPerPit = seedsPerPit;
		this.timerValMils = timerValMils;
		this.randomDistro = randomDistro;
	}
	
	//used for communication with clients (multithreaded)
	private class Player extends Thread {
		// game attributes
		int playerNum;	//am I player 1 or 2? (determined by order of client connections)
		Player opponent;
		
		// I/O
		Socket socket;
		BufferedReader reader;
		PrintWriter writer;
		
		
		public Player(Socket socket, int playerNum) {
			this.socket = socket;
			this.playerNum = playerNum;
			
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream(), true);
				
				writer.println("WELCOME");
			}
			catch (IOException e) {
				System.out.println("Player error: " + e);
			}
		}
		
		public void setOpponent(Player opponent) {
			this.opponent = opponent;
		}
		
		//overrides Thread.run(), processes client commands and plays the game
		public void run() {
			try {
				System.out.println("SERVER: all players connected!");
				
				//build the info message for clients
				String gameConfig = "INFO " + Integer.toString(pitsPerPlayer) + " " + Integer.toString(seedsPerPit)
				                            + " " + Integer.toString(timerValMils);
				
				//append first/second character
				if (playerNum == 1) {
					gameConfig += " F";
				}
				else {
					gameConfig += " S";
				}
				
				//append random/uniform distro character
				if (randomDistro) {
					gameConfig += " R";
					currentGame = new kalah(pitsPerPlayer, seedsPerPit, randomDistro);
					
					ArrayList<Integer> randomSeeds = currentGame.getP1pits();
					for (int i = 0; i < randomSeeds.size(); i++) {
						gameConfig += " " + Integer.toString(randomSeeds.get(i));
					}
				}
				else {
					gameConfig += " S";
					currentGame = new kalah(pitsPerPlayer, seedsPerPit, randomDistro);
				}
				
				
				//send to server 
				writer.println(gameConfig);
				
				//process client commands (game loop for each client)
				while (true) {
					
					String clientMessage = reader.readLine();
					
					//ignore empty commands
					if (!clientMessage.equals(null) && !clientMessage.equals("")) {
						if (clientMessage.startsWith("MOVE")) {
							System.out.println("SERVER: Client " + playerNum + " sent move: " + clientMessage.substring(5));

							int clientMove = Integer.parseInt(clientMessage.substring(5));

							if (currentGame.move(clientMove)) {
								System.out.println("SERVER: acknowledged client " + playerNum + "'s " + "move: " + clientMessage.substring(5));
								writer.println("OK");

								//send move to other client to keep their game in sync
								opponent.writer.println("MOVE " + clientMessage.substring(5));
								System.out.println("SERVER: Sent move " + clientMessage.substring(5) + " to client " + opponent.playerNum);
								
								//check for winner 
								currentGame.checkGameOver();
								if (currentGame.gameOver) {
									System.out.println("SERVER: GAME OVER");
									if (currentGame.winner == 0) {
										System.out.println("SERVER: Tie!");
										writer.println("TIE");
										opponent.writer.println("TIE");
									}
									else if (playerNum == currentGame.winner) {
										System.out.println("SERVER: Player " + playerNum + " wins!");
										writer.println("WINNER");
										opponent.writer.println("LOSER");
									}
									else {
										System.out.println("SERVER: Player " + playerNum + " wins!");
										writer.println("LOSER");
										opponent.writer.println("WINNER");
									}
								}
							} else {
								System.out.println("SERVER: rejected client's move: " + clientMessage.substring(5));
								writer.println("ILLEGAL");
							}
						} 
					}
				}
			}
			catch (IOException e) {
				System.out.println("Player error: " + e);
			}
			finally {
				try {
					socket.close();
				}
				catch (IOException e) {
					
				}
			}
		}
	}
	
	
    public static void main(String[] args) throws IOException  {
    	ServerSocket listener = new ServerSocket(42069);
    	try {
    		
    			server gameServer = new server(6, 4, 5000, false);
    			
    			server.Player player1 = gameServer.new Player(listener.accept(), 1);
    			server.Player player2 = gameServer.new Player(listener.accept(), 2);
    			
    			player1.setOpponent(player2);
    			player2.setOpponent(player1);
    			
    			player1.start();
    			player2.start();
    		
    	}
    	finally {
    		listener.close();
    	}
    }
}

 