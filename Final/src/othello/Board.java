package edu.carleton.gersteinj.othello;

import java.util.ArrayList;
import java.util.List;

/**
 * Board state will be kept track of with an 8x8x3 matrix - columns, rows, content
 */
public class Board {
    /* For the sake of low memory usage, the board is stored as an 8x8 array of bytes.
     * Meanings of values are as follows:
     * 0 = empty space
     * 1 = black piece
     * 2 = white piece
     */
    private byte[][] state;
    private boolean blackToMove;

    /* Subclass for board coordinates. x and y go from 0 to 7 (8x8 board) */
    static class Coordinates {
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

        /* String representation of a coordinate is capital column letter (A-H) + row number (1-8).
         * E.g. A1 for top left corner
         */
        public String toString() {
            char colChar = (char) ('A' + x);
            return Integer.toString(y + 1) + colChar;
        }

        boolean isOnBoard() {
            return x >= 0 && x <= 7 && y >= 0 && y <= 7;
        }
    }

    Board() {
        blackToMove = true;
        // Set starting pieces
        state = new byte[8][8];
        state[4][3] = 1;
        state[3][4] = 1;
        state[3][3] = 2;
        state[4][4] = 2;
    }

    /*
       Compute and return the current coordinates at which the current player could place a piece.
     */
    public List<Coordinates> getAvailableMoves() {
        List<Coordinates> movesList = new ArrayList<>();
        for (byte i = 0; i < 8; i++) {
            for (byte j = 0; j < 8; j++) {
                Coordinates move = new Coordinates(i, j);
                if (isLegalMove(move)) {
                    movesList.add(move);
                }
            }
        }
        return movesList;
    }

    boolean isLegalMove(Coordinates move) {
        // Can't place piece on non-empty space
        if (!move.isOnBoard() || state[move.x][move.y] != 0) {
            return false;
        }
        // Assign variables to current player's color and opponent's color
        int playerPiece = blackToMove ? 1 : 2;
        int opponentPiece = 3 - playerPiece;
        Coordinates curLocation;
        // Check if there is, in any of the 8 directions, a line of the form:
        // current move coordinate -> <opponent's pieces> -> player's piece
        byte[][] directionVectors = new byte[][]{{-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}};
        for (byte[] direction : directionVectors) {
            boolean reachedOpponentPiece = false;
            curLocation = new Coordinates(move);
            curLocation.x += direction[0];
            curLocation.y += direction[1];
            while (curLocation.isOnBoard()) {
                byte curPiece = state[curLocation.x][curLocation.y];
                if (reachedOpponentPiece) {
                    if (curPiece == playerPiece) {
                        return true;
                    } else if (curPiece == opponentPiece) {
                        //noinspection UnnecessaryContinue
                        continue;
                    } else if (curPiece == 0) {
                        break;
                    }
                } else if (curPiece != opponentPiece) {
                    break;
                } else {
                    reachedOpponentPiece = true;
                }
                curLocation.x += direction[0];
                curLocation.y += direction[1];
            }
        }
        // If it didn't return true for any direction, then move is not legal.
        return false;
    }

    /*
     Applies the given move to the board's state. Return a boolean of whether or not the move was legal. If not legal
     move, state doesn't change.
     */
    public boolean applyMove(Coordinates move) {
        if (isLegalMove(move)){
            //TODO: actually apply move
            blackToMove = !blackToMove;
        }
        return isLegalMove(move);
    }

    public boolean isBlackToMove() {
        return blackToMove;
    }

}
