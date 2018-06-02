package edu.carleton.gersteinj.reversi;

import java.util.ArrayList;
import java.util.List;

/* Board is the part of the model that stores the current state of a game. It also stores the moves played in a game
 * with an instance of MoveSequence.
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
    private MoveSequence moveSequence;
    private byte numRows = 8;
    private byte numCols = 8;





    /**
     * Constructor. Initializes a board in the state that games start in (4 pieces in middle, black to move).
     */
    Board() {
        blackToMove = true;
        // Set starting pieces
        state = new byte[8][8];
        state[4][3] = 1;
        state[3][4] = 1;
        state[3][3] = 2;
        state[4][4] = 2;
        moveSequence = new MoveSequence();
    }

    /*
       Compute and return the current coordinates at which the current player could place a piece.
     */
    List<Coordinates> getAvailableMoves() {
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
        // Can't place piece on non-empty space, or off board.
        if (!isMoveOnBoard(move) || state[move.x][move.y] != 0) {
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
            while (isMoveOnBoard(curLocation)) {
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
     * Applies the given move to the board's state. Return a boolean of whether or not the move was legal. If not legal
     * move, state doesn't change.
     */
    boolean applyMove(Coordinates move) {
        if (isLegalMove(move)) {
            //TODO: actually apply move
            blackToMove = !blackToMove;
        }
        return isLegalMove(move);
    }

    /* Return true if the given coordinate is on the board, false otherwise. */
    boolean isMoveOnBoard(Coordinates move){
        return move.x >= 0 && move.x <= numCols - 1 && move.y >= 0 && move.y <= numRows - 1;
    }

    boolean isBlackToMove() {
        return blackToMove;
    }

}
