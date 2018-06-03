/* By Josh Gerstein and Liz Nichols, 6/3/2018.
 * Coordinates is a struct-like class to represent a coordinate on a board, equipped with a toString method and a static
 * array of "adjacency vectors" (explained below).
 */
package edu.carleton.gersteinj.reversi;

class Coordinates {
    int x;
    int y;

    /* If (x1, y1) is a coordinate pair not on edge of board, then adjacencyVectors is an array of all the vectors
     * u = <x2, y2> such that (x1, y1) + u = (x1 + x2, y1 + y2) is considered "adjacent" to (x1, y1) in Reversi.
     * Here, a vector is represented by a byte array of length 2
     */
    static byte[][] adjacencyVectors = {{-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}};


    /**
     * Default constructor, initializes coordinate pair (0,0)
     */
    Coordinates() {
        this( 0,  0);
    }

    /**
     * @param x: x value of coordinate pair
     * @param y: y value of coordinate pair
     */
    Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /* Constructor that initializes a copy of another Coordinates object */
    Coordinates(Coordinates originalCoordinates) {
        this.x = originalCoordinates.x;
        this.y = originalCoordinates.y;
    }

    /**
     * @return Canonical string representation of a Reversi board square:
     * capital column letter (A-H) + row number (1-8). E.g. A1 for top left corner
     * Row 1 (y=0) is at the top of a board, Column A (x=0) is on the left of a board.
     */
    public String toString() {
        char colChar = (char) ('A' + x);
        return Integer.toString(y + 1) + colChar;
    }
}

