#include "game_manager.h"

using namespace std;

Game_manager::Game_manager(int house_per_side, int mode) {
	
	game_mode = mode;
	houses_per_side = house_per_side;
	player_turn = false;
	game_over = false;
	for( int i = 0 ; i < 2*house_per_side ; i++) {
		houses.push_back(4);
	}
	
	// create players (to be used later for AI)
	
	start_game();
}

void Game_manager::start_game() {

	cout << "/---------------start game : 1-----------------/" << endl;
	
	string input;
	int house;
	stringstream ss;
	
	
	display_board();
	
	
	// prompt player
	
	cout << "Player " << (1+(int)player_turn) << ":\nWhich house will you empty? (1-6)" << endl;
	
	
		
	// take player input and move
	// add error handling
	while ( cin >> input ) {
				
		if( input == "quit") {
			quit_game();
			break;
		}
		
		// change input to integer 
		
		ss << input;
		ss >> house;
		ss.flush();		
		
		// check input
		if (house < houses_per_side && house > 0 ) {
			
			// decide whose turn and move
			if ( player_turn ) {
				move(2, house);
			}
			else {
				move(1, house);
			}
			
		}
		
		else {
			
			cout << "Error: input not acceptable" << endl;
			quit_game();
			break;
			
		}
		
		// check if game over
		if( game_over ) {
			cout << "Game over" << endl;
			quit_game();
			break;
		}
		
		display_board();
		
		// prompt player
		cout << "Player " << (1+(int)player_turn) << ":\nWhich house will you empty? ";
		if(player_turn) {
			cout << "(7-12)" << endl;
		}
		else {
			cout << "(1-6)" << endl;
		}
		
	}
}

void Game_manager::move(int player, int house) {
	
	int curr_house = house-1;
	int seeds = houses[house-1];
	houses[house-1] = 0;
	curr_house++;
	
	while(seeds>0){
		
		//if we hit the second players pit
		if(curr_house == houses.size()) {
			curr_house = 0;
			
			if(player == 2){
				score2++;
				seeds--;
				
				if(seeds == 0){
					break;
				}
			}
			
			else {
				houses[curr_house]++;
				curr_house++;
			}
		}
		
		
		//if we hit the first players pit
		else if(curr_house == houses.size()/2) {
			
			if(player == 1) {
				score1++;
				seeds--;
			}
			else {
				houses[curr_house]++;
				curr_house++;
			}
		}
		
		
		
		//if we hit neither pit
		else {
			houses[curr_house]++;
			curr_house++;
		}
	}
	
	player_turn = !player_turn;
	
	// add stuff based on where the last seed landed
}


void Game_manager::display_board() {
	
	cout << "--------------------------\n|  |     Player 2     |  |\n|  |" ;
	cout << "12 11 10  9  8  7 " ;
	cout << "|  |\n|  |" ;
	
	for(int i = houses.size()-1 ; i > houses.size()/2-1 ; i--) {
		
		cout << "(" << houses[i] << ")" ;
	}
	
	cout << "|  |\n| " << score2 << "|" << "\t\t      " << "|" << score1 << " |\n|  |" ;
	
	for(int i = 0 ; i < houses.size()/2 ; i++) {
		cout << "(" << houses[i] << ")" ;
	}
	
	
	cout << "|  |\n|  |";
	cout << " 1  2  3  4  5  6 " ;
	cout << "|  |\n|  |     Player 1     |  |\n--------------------------\n" << endl ;
}


void Game_manager::quit_game() {

	cout << "/---------------quit game : 1------------------/" << endl;

}
