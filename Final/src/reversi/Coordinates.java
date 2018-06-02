package edu.carleton.gersteinj.othello;

/* Struct-like class to represent a coordinate on a board. x and y go from 0 to 7 (assuming an 8x8 board), but the
 * toString method uses the canonical representation for Reversi:
 * capital column letter (A-H) + row number (1-8). E.g. A1 for top left corner
 * Row 1 (y=0) is at the top of a board, Column A (x=0) is on the left of a board.
 */
public class Coordinates {
    byte x;
    byte y;

    Coordinates(byte x, byte y) {
        this.x = x;
        this.y = y;
    }

    Coordinates(Coordinates originalCoordinates) {
        this.x = originalCoordinates.x;
        this.y = originalCoordinates.y;
    }

    /* String representation as described above, a coordinate is capital column letter (A-H) + row number (1-8). */
    public String toString() {
        char colChar = (char) ('A' + x);
        return Integer.toString(y + 1) + colChar;
    }
}
