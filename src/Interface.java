import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;

import java.util.ArrayList;

public class Interface {
	//**PRIVATE DATA MEMBERS**//
	private JFrame mainFrame;
	private JPanel pieRulePanel;

	//Window components (for playing/representing the board & game state)
	private ArrayList<PitButton> gameButtons;
	private JLabel statusLabel;
	private JButton pieRule;

	//current game to render - set in constructor by reference 
	private kalah currentGame;
	private String lastMove; 	//stores last move played (by either player) for manager access
	Boolean allowInput;

	//**PUBLIC MEMBER FUNCTIONS**//
	public Interface(kalah currentGame) {	//pitCount refers to # of pits per player 
		this.currentGame = currentGame;
		allowInput = false;
		drawGameWindow();

		lastMove = "-1";

	}

	public void setGame(kalah game) {
		this.currentGame = game;
	}

	public void setInput(Boolean allowInput) {
		this.allowInput = allowInput;
	}
  
	public void setLastInput(String lastInput) { this.lastMove = lastInput; }

	public String getLastInput() {

		return this.lastMove;
	}

	public Boolean inputEnabled() {
		return this.allowInput;
	}

	public void drawGameWindow() {
		//calculate position of player 1 and 2's stores (make these class members?)
		int pitCount = currentGame.pitsPerSide;

		int store1Index = pitCount;
		int store2Index = 2 * pitCount + 1;
		int boardSize = 2 * pitCount + 2; //count both players' pits and both houses


		gameButtons = new ArrayList<PitButton>();

		mainFrame = new JFrame("Test Window");
		mainFrame.setSize(1280, 720); //FIX: these are temporarily hard-coded
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//parent container for all game rendering (board, status, etc.)
		JPanel parentPanel = new JPanel(new BorderLayout());
		//panel responsible for rendering the board -- more will be added later
		JPanel gamePanel = new JPanel(new GridBagLayout());
		gamePanel.setBackground(Color.DARK_GRAY);

		//JPanel pieRulePanel = new JPanel();

        // pie rule button
        /*
        pieRule = new JButton();
        pieRule.addActionListener(new buttonListener());

        pieRule.setBorderPainted(false);
        pieRule.setText("Switch sides?");
        //pieRule.setBackground(Color.LIGHT_GRAY);
        pieRule.setFont(pieRule.getFont().deriveFont(32.0f));
        pieRule.setVisible(true);

        pieRulePanel.add(pieRule);
        pieRulePanel.setVisible(true);

        parentPanel.add(pieRulePanel, BorderLayout.EAST);
        */

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
		ArrayList<Integer> initState = currentGame.getState();

		for (int i = 0; i < boardSize; i++) {
			PitButton tempButton = new PitButton(i, 4);

			//define button visuals
			tempButton.setBorderPainted(false);
			tempButton.setText(Integer.toString(initState.get(i)));
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

		parentPanel.add(gamePanel);
		mainFrame.add(parentPanel);
		mainFrame.setVisible(true);
	}

	public void displayPieRule() {

	    pieRulePanel = new JPanel();

        pieRule = new JButton();
        pieRule.addActionListener(new buttonListener());

        pieRule.setBorderPainted(false);
        pieRule.setText("Switch sides?");
        //pieRule.setBackground(Color.LIGHT_GRAY);
        pieRule.setFont(pieRule.getFont().deriveFont(32.0f));
        pieRule.setVisible(true);

        pieRulePanel.add(pieRule);
        pieRulePanel.setVisible(true);
        pieRule.setVisible(true);

        mainFrame.add(pieRulePanel, BorderLayout.EAST);


    }

    public void hidePieRule() {
	    pieRule.setVisible(false);
        pieRulePanel.setVisible(false);
    }

	public void blinkPit(int index) {
		for (int i = 0; i < 3; i++) {

			gameButtons.get(index).setBackground(Color.GREEN);
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gameButtons.get(index).setBackground(Color.LIGHT_GRAY);
		}
	}

	public void renderBoard(ArrayList<Integer> boardData) {
		//update seed count
		for (int i = 0; i < boardData.size(); i++) {
			gameButtons.get(i).setText( Integer.toString(boardData.get(i)) );
		}

		//show/hide pie rule button


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

	//**EVENT HANDLERS**//
	private class buttonListener implements ActionListener {
		//main handler for sending moves to game & updating board in window
		public void actionPerformed(ActionEvent e) {
			//get the widget which raised the event
			Object sourceObj = e.getSource();

			//check if widget is a button
			if (sourceObj instanceof PitButton) {
				//cast the obj which raised the event
				PitButton sourceButton = (PitButton) sourceObj;

				if (sourceButton.isHouse) {
					return; 	//players cannot interact with a house
				}
				else {
					//check if game is over
					renderBoard(currentGame.getState());

					//do move
					//currentGame.move(sourceButton.boardIndex);
					if (!allowInput) {
						return;
					}


					lastMove = Integer.toString(sourceButton.boardIndex);


					//update board post-move
					//currentGame.checkGameOver();
					//renderBoard(currentGame.getState());
				}
			}
			else if (sourceObj instanceof JButton) {

			    JButton sourceButton = (JButton) sourceObj;

			    renderBoard(currentGame.getState());

			    if(!allowInput) {
			        return;
                }

			    lastMove = "pie";

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