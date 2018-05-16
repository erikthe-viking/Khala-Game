import java.util.*;

// Node represents the whole Board 
// Node allows for custom sizes
public class node {
	
	   // Individual item count of each "bucket" create an arraylist
	   public ArrayList<Integer> player_1;
	   public ArrayList<Integer> player_2;
	   public ArrayList<Integer> players;
	   
	   public int hole_size;
	   public int board_size;
	   //Player Score
	   public int player_1_score;
	   public int player_2_score;
       // Constructor
       node(){
    	   
    	 player_1 = new ArrayList<Integer>(board_size);
    	 player_2 = new ArrayList<Integer>(board_size);
    	 players = new ArrayList<Integer>(board_size);
 
       }
       node(int size, int hole){
    	 board_size = size;
    	 hole_size = hole;
      	 player_1 = new ArrayList<Integer>(board_size);
      	 player_2 = new ArrayList<Integer>(board_size);
      	 players = new ArrayList<Integer>(board_size);
      	 
      	   for(int i = 0; i < board_size; ++i) {
             	// Populates the board 
             	player_1.add(hole_size);
             	player_2.add(hole_size);
             	
             }
      	   for(int i = 0; i < board_size; ++i) {
      		   
      		   players.add(hole_size);
      	   }
   
         }
       // Copy Constructor
       node(node another) {
    	   
    	    this.player_1 = another.player_1;  
    	    this.player_2 = another.player_2;
    	    this.players = another.players;
    	    this.player_1_score = another.player_1_score;
    	    this.player_2_score = another.player_2_score;
    	    
       }
      
}
