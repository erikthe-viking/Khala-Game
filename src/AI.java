
import java.util.Random;
import java.util.*;

public class AI {
	
	/**
	 * 				AI is complete
	 * 
	 */
//PUSH TEST
	// Depth = counter to number of turns
	public int depth = 0;
	public node root;
	public int max_depth;
	public int board_size = 4;

	public AI() {
		
		
	}
	public AI(int score_1, int score_2, int size, int board) {
		 // initializes root node or starting board
    	root = new node(board_size,size);
        root.player_1_score = score_1;
        root.player_2_score = score_2;
        root.hole_size = size;
        board_size = board;
	}   
    public int utility(node x) {
    	 
    	return x.player_1_score - x.player_2_score;
    }
    public void get_board_state(Board x, node y) {
    	
    	// updates the node with score from the game
    	y.player_1_score = x.getP1score();
    	
    	y.player_2_score = x.getP2score();
    	// Updates the node with seeds per pit for each player side of the board
    	y.player_1 = x.getP1pits();
    	// System.out.println(y.player_1);
    	y.player_2 = x.getP2pits();
    	// Gets Circular Array
    	y.players = x.pits;
    	
    }
    public Boolean terminal_test(node x, int depth) {
    	// Leaf state = no playable seeds on your side
    	// Loop through both player1 and PLayer2 cells that have seed. Check to make sure they are greater than 0
    	// If all are 0 than leaf state
    	// Check to see if maximum depth is reached
    	if(depth == max_depth){

    		return true;
    	}
    	
    	else{
    		//change 4
			for(int i = 0; i < board_size; ++i) {
    			// Checks to see if the board has any playable pieces
    			if(x.player_1.get(i) > 0 || x.player_2.get(i) > 0) {
    			 
    				return true;
    				
    			}
    		}
    	}
    	return false;
    }  /*
    public node run(Board k,  int hole, int score_1, int score_2, int size, int board_size) {
    
      // In network client receive the node dont initialize
     // initialize_root(score_1,score_2,size,board_size);
      node  clone = new node(root);
      Boolean term = true;
    //  while(term == true) {
    	  // We need to loop through the run function until terminus is reached. The question is to loop in Kalah or in the function itself??
    	 
      	  // Updates the root node with the board state from kalah
      	  get_board_state(k,clone);
    	  
    	  // Finds the human player1's most recent move and moves it
    	  int hole_number = k.p1move;
    	  node y = move(clone,hole_number); 
          
    	  // AI makes decisions and returns a new board/node with that move completed
    	  int ai_choice = AI_move(y); 
          node ai = move(y,ai_choice);
          return ai;
         
    //  }
     
    //currently returns a board state
    }
     */
    public int int_run(Board k) {
        /*
         *   Returns the choice made by the AI with the current board state
         *
        */

       // In network client receive the node don't initialize
       // initialize_root(score_1,score_2,size,board_size);
    	
    	node clone = new node(root);
     
      	// We need to loop through the run function until terminus is reached. The question is to loop in Kalah or in the function itself??
      	 
        // Updates the root node with the board state from kalah
        get_board_state(k,clone);

      	// AI makes decisions and returns a new board/node with that move completed
      	int ai_choice = AI_move(clone); 
    	System.out.println(clone.players);
 
      	return ai_choice;

      }
    public node move(node x, int hole) {

    	node  y = new node(x);  
    	
    	int num = y.players.get(hole);
    	y.players.set(hole,0);
    	for(int i = 0; i < num; ++i) {
    	
    		if(hole+i == board_size) {
    			
    			y.player_2_score = y.player_2_score + 1;
    			
    		}
    		else if(hole+i == (2*board_size)+1) {
    			y.player_1_score = y.player_1_score + 1;
    		}
    		if(hole + i < (2*board_size)+1) {
    		
    			y.players.set(hole + i,y.players.get(hole + i)+1);
    			
    		}
    	}

    	return y;

        }
    public node move_2(node x, int hole) {
    // CREATE MOVE 2 function to rewtest
    	
    	node  y = new node(x); // Copy by reference issue?
    	
    	int num = y.player_1.get(hole);
    	
    	for(int i = 0; i < num; ++i) {
    	
    		if(hole+i == board_size-1) {
    			
    			y.player_2_score = y.player_2_score + 1;
    			hole = 0;
    		}
    		else if(hole+i > board_size-1) {
    			int temp = hole-(board_size - 1);
    			y.player_1.set(temp + i,y.player_2.get(temp + i)+1);
    		}
    		else if(hole+i < board_size) {
			
    			y.player_2.set(hole + i,y.player_2.get(hole + i)+1); //ADD OR SET for arraylist??????????????????????????????????????
    	
    		}
    		
    	}

    	y.player_2.set(hole,0);
    	
    	return y;

    }
    
    public int AI_move(node x) {
     
    	int best_move = -1;
    	int best_value = 1000000;// best value = min value
    	int index = 0;
    	int temp = 0;
  
    	// System.out.println(x.player_2.get(0));
    	// Loop through the possible boards to determine if any are empty
    	
    	for(int i = 0; i < board_size; ++i) {
    		
    		temp = x.players.get(i);
    		
			if(temp != 0) {
				
				node a = move(x,i);
    		
    			int v = min_value(a);
    			 
    			if(v < best_value) {
    				best_move = i;
    				best_value = v;
    			}
			}
	
    	}
    	System.out.println(best_move);
    	return best_move;
    }
    public int max_value(node x) {
    	
    	int v = 100000000;
    	int temp = 0;
   		int hole;
   		node y = new node();
   		
   		if(terminal_test(x,depth) == true) {
   			//System.out.println("Terminal Test Reached: ");
   			return utility(x);
   			
   		}
   		else {
   
			for(int i = 0; i < board_size; ++i) {
				
				node z = move(x, i);
				
				if(z.player_2_score > temp) {
					
					temp = z.player_2_score;
					hole = i;
					y = new node(z);
					
				}
			}
			System.out.println("Temp_return: ");
		
	   	 	int temp_return = min_value(y);
	   		System.out.println(temp_return);	
	   	 	if(temp_return > v){
	   	 		v = temp;
	   	 	}
	   	 	++depth;
			return v;
   		}
    
    }
    public int min_value(node x) {
    	// Lowest possible Min Value
    	int v = -100000000;
    	int temp = 0;
   		int hole;
   		node y = new node();
   		
   		//Checks to see if end game state has been reached
   		if(terminal_test(x,depth) == true) {
   			//System.out.println("Terminal Test Reached: ");
   			return utility(x);
   		}
   		else {
   			// Iterates through each pit
			for(int i = 0; i < board_size; ++i) {
				// Creates a new node z that takes the characteristics from the board created in move
				node z = move(x, i);
				
				if(z.player_2_score > temp) {
					
					temp = z.player_2_score;
					hole = i;
					y = new node(z); // Make sure the copy constructor works
					
				}
			}
			
	   	 	int temp_return = max_value(y);
	   	 	
			if(temp_return < v){
	   	 		v = temp;
	   	 	}
			++depth;
			return v;
   		}
   		
    }
 
}



