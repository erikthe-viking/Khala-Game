import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class kalah {

	//----------------------- data members
    // 1 = human v human, 0 = human v AI, -1 = AI v AI

    Board board;

	int pitsPerSide, p1move, p2move, winner, moveCount;

	boolean player2Turn, gameOver;

    //--------------------------- constructor

    public kalah() {}
    
    public kalah(int startPitsPerSide, int startSeeds, boolean startRandomDistro)
    {
        this.board = new Board(startPitsPerSide, startSeeds, startRandomDistro);
        pitsPerSide = startPitsPerSide;
        gameOver = false;
        player2Turn = false;
        /*
        int random = ThreadLocalRandom.current().nextInt()%2;
        if(random == 0) {
        	player2Turn = true;
		} else {
			player2Turn = false;
		}
		*/

        winner = 0;

    }
    
    //create a game based on random seed values provided from server
    public kalah(int startSeeds, int startPitsPerSide, boolean startRandomDistro, ArrayList<Integer> pitValues)
    {
        this.board = new Board(startPitsPerSide,startSeeds,startRandomDistro);
        pitsPerSide = startPitsPerSide;
        gameOver = false;
        
        //set pit values from server data
        for (int i = 0; i < pitValues.size(); i++) {
        	board.pits.set(i, pitValues.get(i));
        	board.pits.set(2 * i + 1, pitValues.get(i));
        }

		player2Turn = false;

        /*
		int random = ThreadLocalRandom.current().nextInt()%2;
		if(random == 0) {
			player2Turn = true;
		} else {
			player2Turn = false;
		}
		*/

		winner = 0;

    }

	//---------------------- getter functions

	public ArrayList<Integer> getP1pits()
	{
		ArrayList<Integer> tmp = new ArrayList<Integer> (pitsPerSide);
		for (int i = 0; i<pitsPerSide ; i++) {
			tmp.add(board.pits.get(i));
		}
		return tmp;
	}

	public ArrayList<Integer> getP2pits()
	{
		ArrayList<Integer> tmp = new ArrayList<Integer> (pitsPerSide);
		for (int i = 0; i<pitsPerSide ; i++) {
			tmp.add(board.pits.get(i+(pitsPerSide+1)));
		}
		return tmp;
	}

	public int getP1score()
	{
		return board.getP1score() ;
	}

	public int getP2score()
	{
		return board.getP2score() ;
	}
	
	public ArrayList<Integer> getState() {
		return board.getState();
	}

	// ---------------------------- print to terminal function(s)

	public void printToTerminal() {
		// print board to terminal
		board.printTerminal();

		// prompt user
		System.out.print("Player ");
		if(player2Turn){
			System.out.print(2);
		}
		else {
			System.out.print(1);
		}
		System.out.print(" choose which pit to empty:");
	}

	// -------------------------- game management functions

	public boolean move(int playerInput){
        if (!gameOver) {

            if (playerInput >= 0 && playerInput < pitsPerSide && !player2Turn) {
                // check if pit is empty
                if (board.pits.get(playerInput) == 0) {

                    System.out.println("INVALID MOVE: Cannot empty pit that has no seeds.");
                    return false;
                }

                if (!board.Move(playerInput, player2Turn)) {
                	moveCount++;
                    player2Turn = !player2Turn;
                }

                System.out.println("VALID MOVE");

                p1move = playerInput;
                return true;
            } else if (playerInput > pitsPerSide && playerInput < 2 * playerInput + 1 && player2Turn) {

                // check if pit is empty
                if (board.pits.get(playerInput) == 0) {

                    System.out.println("INVALID MOVE: Cannot empty pit that has no seeds.");
                    return false;
                }

                if (!board.Move(playerInput, player2Turn)) {
                    player2Turn = !player2Turn;
                    moveCount++;
                }

                System.out.println("VALID MOVE");

                p2move = playerInput;
                return true;
            } else {

                System.out.println("INVALID MOVE: Cannot empty opponents pit.");

                return false;
            }
        }
        else {
            System.out.println("Game has ended.");
            return false;
        }
	}

	public void switchSides() {
		board.switchSides();
	}

	public void quitGame(){
	    gameOver = true;
	    System.out.println("GAME OVER");
    }

    public void checkGameOver() {

		ArrayList<Integer> p1pits = getP1pits();
		ArrayList<Integer> p2pits = getP2pits();



		// check if either side has all zeros

		boolean allZeroP1 = true;
		boolean allZeroP2 = true;

		for( int i = 0 ; i < p1pits.size() ; i++ ) {
			if( p1pits.get(i) != 0 ) {
				allZeroP1 = false;
			}
			if(!allZeroP1) {
				break;
			}
		}
		for( int i = 0 ; i < p2pits.size() ; i++ ) {
			if( p2pits.get(i) != 0 ) {
				allZeroP2 = false;
			}
			if(!allZeroP2) {
				break;
			}
		}



		// if either side contains all zero

		if ( allZeroP1 ) {

			gameOver = true;
			int count = 0;

			for( int i =  board.store1 + 1 ; i < board.store2 ; i++ ) {

				count += board.pits.get(i);
				board.pits.set(i,0);

			}

			board.pits.set(board.store2,getP2score()+count);
		}


		if ( allZeroP2 ) {

			gameOver = true;
			int count = 0;

			for( int i = 0 ; i < board.store1 ; i++ ) {

				count += board.pits.get(i);
				board.pits.set(i,0);

			}

			board.pits.set(board.store1,getP1score()+count);
		}

		if ( gameOver ) {
			if ( getP1score() > getP2score() ) {
				winner = 1;
			}
			else if ( getP1score() < getP2score() ){
				winner = 2;
			}
			else {
				winner = 0;
			}
		}

	}
    /*
	 public static void main(String args[])
	 {
	 	//Scanner scanner = new Scanner(System.in);

	 	//kalah newGame = new kalah(1, 6, true);
	 	//newGame.printToTerminal();
	    // Functions as Main.cpp
		//System.out.println( "Kalah" );
	 	
	 	//instantiate GUI (test)
	 	Interface GUI = new Interface();
		 /*
			Board k = new Board();
			AI robot = new AI(0,0,4,6);
			int value = robot.int_run(k);
			System.out.println(value);
			System.out.println("Dick");

	 }
*/
}
