Joshua Gerstein, Liz Nichols
05/25/2018

We will make a single-player Othello game that you play against a computer player. 

MVC is appropriate because there is a view that the user interfaces with, a model that keeps track of the rules of gameplay, and a controller that updates the game-state based on user and ai inputs.

Model will consist of the rules of gameplay, the (bad) othello ai, the scoring rules, the available legal moves.

- Board state will be kept track of with an 8x8x3 matrix - columns, rows, content

- Move history will be recorded using a sequence of labelled moves 

- method - Find Available moves

- method - Find new board state after move

- method - flip (changes the state of each piece slot to the opposite state)

- method - determine whose turn it is from board state

Views will consist of the board state

Classes will be:

board, piece, board location, 

Controller will:
- initialize the board
- determine whether the game is over