package edu.carleton.gersteinj.othello.views;

/**
 * Board state will be kept track of with an 8x8x3 matrix - columns, rows, content
 */
public class Board extends GridPane{
    /*
     Subclass for board coordinates
     */
    public class Coordinates {
        int x;
        int y;

        Coordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private int[][] state;
    public boolean blackToMove;

    /*
       Compute and return the current coordinates at which the current player could place a piece.
     */
    public Coordinates[] getAvailableMoves() {
        return new Coordinates[]{new Coordinates(0, 0)};
    }

    /*
     Applies the given move to the board's state. Return a boolean of whether or not the move was legal. If not legal
     move, state doesn't change.
     */
    public boolean applyMove(Coordinates move) {
        return false;
    }

}
