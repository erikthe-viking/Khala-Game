#include <iostream>
#include <iomanip>
#include <sstream>
#include <vector>

using namespace std;

class Game_manager {

private:
//DATA MEMBERS:
	int houses_per_side;
	
	// 1 = human v human, 0 = human v AI, -1 = AI v AI
	int game_mode;
	
	// 0 = player one, 1 = player 2
	bool player_turn;
	bool game_over;
	
	int total_seeds;
	int score1;
	int score2;
	
	vector<int> houses;
	
public:	
//MEMBER FUNCTIONS:
	Game_manager(int houses_per_side, int players);
	
	void start_game();
	
	void move(int player, int house);
	
	void display_board();
	
	void quit_game();
};
