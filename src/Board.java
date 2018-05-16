import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Board {


    // Data members
    static int minSeeds = 1;
    static int maxSeeds = 10;
    int store1;
    int store2;
    int totalSeeds;
    int seedPerPit;
    int pitsPerSide;
    ArrayList<Integer> pits;
    
    public Board() {
    	pits = new ArrayList<Integer>();
    	// Does Board Constructor 
    	for(int i = 0; i < 2*pitsPerSide+2;++i) {
    		pits.add(seedPerPit);
    	}
    	store1 = 0;
    	store2 = 0;

    	seedPerPit = 4;
    }

    //---------------------- getter functions

    public ArrayList<Integer> getP1pits()
    {
        ArrayList<Integer> tmp = new ArrayList<Integer> (pitsPerSide);
        for (int i = 0; i < pitsPerSide ; i++) {
            tmp.add(pits.get(i));
        }
        return tmp;
    }

    public ArrayList<Integer> getP2pits()
    {
        ArrayList<Integer> tmp = new ArrayList<Integer> (pitsPerSide);
        for (int i = 0; i < pitsPerSide ; i++) {
            tmp.add(pits.get(i+(pitsPerSide+1)));
        }
        return tmp;
    }

    public int getP1score()
    {
        return pits.get(store1) ;
    }

    public int getP2score()
    {
        return pits.get(store2) ;
    }

    public ArrayList<Integer> getState() {
        return this.pits;
    }


    // ----------------------- Constructor
    public Board(int startPitsPerSide, int startSeeds, boolean distribution) {
      
        pits = new ArrayList<Integer>(2*startPitsPerSide+2);

        for (int i = 0 ; i < 2*startPitsPerSide+2 ; i++) {
            pits.add(0);
        }

        seedPerPit = startSeeds;
        pitsPerSide = startPitsPerSide;
        totalSeeds = 8*startPitsPerSide;
        store1 = startPitsPerSide;
        store2 = 2*startPitsPerSide+1;
        DistributeSeeds(distribution);
    }
    
  
  
  // ------------------------ Copy Constructor

    public Board(Board oldBoard) {
        this.store1 = oldBoard.store1;
        this.store2 = oldBoard.store2;
        this.pitsPerSide = oldBoard.pitsPerSide;
        this.pits = oldBoard.pits;
    }

    //------------------------ Member functions

    public void switchSides() {
        ArrayList<Integer> newPits = new ArrayList<>(pits.size());

        for ( int i = 0 ; i < store1 ; i++ ) {
            newPits.add(getP2pits().get(i));
        }

        newPits.add(getP1score());

        for ( int i = 0 ; i < store1 ; i++ ) {
            newPits.add(getP1pits().get(i));
        }

        newPits.add(getP2score());

        pits = newPits;
    }

    public void DistributeSeeds(boolean distribution){

        // random distro
        if (distribution) {

            int seedsLeft = totalSeeds/2;
            int currentPitSeeds;

            for ( int i = 0 ; i < store1; i++) {

                currentPitSeeds = ThreadLocalRandom.current().nextInt(minSeeds, maxSeeds);
                if(currentPitSeeds > 7) {
                    maxSeeds = maxSeeds-2;
                }

                if ( seedsLeft != 0 ) {

                    if (seedsLeft > currentPitSeeds) {
                        pits.set(i,currentPitSeeds);
                        seedsLeft -= currentPitSeeds;
                    }
                    else {
                        pits.set(i,seedsLeft);
                        seedsLeft = 0;
                    }
                }
            }

            // if there are left over seeds:
            int currentPit = 0;
            while (seedsLeft > 0) {
                if(currentPit == store1) {
                    currentPit = 0;
                }
                pits.set( currentPit, pits.get(currentPit) + 1 );
                seedsLeft--;
                currentPit++;
            }

            // mirror seeds
            currentPit = 0;
            for ( int i = store1+1 ; i < store2 ; i++ ) {
                pits.set(i,pits.get(currentPit));
                currentPit++;
            }
        }

        // even distro
        else {
            for ( int i = 0 ; i < pits.size() ; i++) {
                if ( i != store1 && i != store2 ) {
                    pits.set(i, seedPerPit);
                }
            }
        }
    }

    public boolean isPlayerSide(int index, boolean player2turn){
    	if(player2turn){
            if (index > store1 && index < store2) {
                return true;
            }
            else{
                return false;
            }
        }
        else {
            if (index >= 0 && index < store1) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    public boolean Move(int pitNumber, boolean player2Turn){

        // normal play == 0, extra move == 1, capture == 2
        boolean goAgain = false;

        int seedsRemaining = pits.get(pitNumber);
        int iterator = pitNumber+1;

        if ( (pitNumber >= 0 && pitNumber < store1) || (pitNumber > store1 && pitNumber < store2) ) {

            pits.set(pitNumber,0);

            while (seedsRemaining > 0) {

                // check for special cases after the last seed placed
                if (seedsRemaining == 1) {

                    if (player2Turn) {
                        if (iterator == store1) {
                            iterator++;
                        }
                        else if (iterator == store2) {
                            goAgain = true;
                        }

                        if (isPlayerSide(iterator,player2Turn) && pits.get(iterator) == 0) {
                            //insert capture function here
                        	capture(iterator);
                        }
                        else {
                            pits.set(iterator, pits.get(iterator) + 1);
                        }
                        seedsRemaining--;

                    }
                    else {
                        if (iterator == store2) {
                            iterator = 0;
                        }
                        else if (iterator == store1) {
                            goAgain = true;
                        }

                        if (isPlayerSide(iterator,player2Turn) && pits.get(iterator) == 0) {
                            //insert capture function here
                        	capture(iterator);
                        }
                        else {
                            pits.set(iterator, pits.get(iterator) + 1);
                        }
                        seedsRemaining--;
                    }

                }

                else {

                    if (iterator == store1 && player2Turn) {
                        iterator++;
                    }
                    else if (iterator == store2 && !player2Turn) {
                        iterator = 0;
                    }
                    else {

                        seedsRemaining--;
                        pits.set(iterator, pits.get(iterator) + 1);
                        iterator++;

                        // bound the iterator
                        if (iterator > store2) {
                            iterator = 0;
                        }
                    }
                }
            }

            return goAgain;
        }



        else {

            System.out.println("Board cannot move seeds from this pit.");

            return goAgain;
        }
    }
    
    public void capture(int index) {
    	//index refers to the resultant position of the player's last move
		int captureFromIndex = pits.size() - (index + 2);  //+2 to account for both houses 
		
		//take seeds from target house and set its count to zero
		int seedsCaptured = pits.get(captureFromIndex);
		if (seedsCaptured == 0) {
			pits.set(index, 1);
			return; //exit if there are no seeds to capture
		}
		pits.set(captureFromIndex, 0);
		pits.set(index, 0); //zero the last spot the player landed in
		
		if (index > pitsPerSide) { //capture from player 1
			pits.set(store2, pits.get(store2) + seedsCaptured + 1);	//+1 to account for player's seed
		}
		else { //capture from player 2
			pits.set(store1, pits.get(store1) + seedsCaptured + 1);	
		}
    }

    public void printTerminal(){
        System.out.print("--------------------------\n|  |     Player 2     |  |\n|  |12 11 10  9  8  7 |  |\n|  |");

        for (int i = store2-1 ; i > store1 ; i--) {
            System.out.print("("+pits.get(i)+")");
        }

        System.out.print("|  |\n| " + pits.get(store2) + "|" + "\t\t          " + "|" + pits.get(store1) + " |\n|  |");

        for (int i = 0 ; i < store1 ; i++) {
            System.out.print("("+pits.get(i)+")");
        }

        System.out.print( "|  |\n|  | 1  2  3  4  5  6 |  |\n|  |     Player 1     |  |\n--------------------------\n");
    }

    // test Board for Gitsd

}