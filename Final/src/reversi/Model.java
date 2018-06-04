/* By Josh Gerstein and Liz Nichols, 6/3/2018.
 * Model is the part of the model that stores the current state of a game, along with the full sequence of moves that
 * have been played in that game.
 */

package edu.carleton.gersteinj.reversi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@SuppressWarnings("WeakerAccess")
        //TODO: Remove this line and delete/restrict methods as appropriate when done
class Model {
    private Content[][] state;
    private boolean blackToMove;
    private MoveSequence moveSequence;
    private final int numRows;
    private final int numCols;


    /**
     * Default constructor, 8x8 board.
     * Initializes a board in the starting position of Reversi (4 pieces in middle, black to move, no history).
     */
    Model() {
        this(8, 8, new MoveSequence());
    }

    /**
     * Constructor. Initializes a board in the starting position of Reversi (4 pieces in middle, black to move), then
     * applies each move from the given moveSequence in order. If ANY move in the move sequence is illegal, ignores
     * all subsequent moves.
     *
     * @param numCols: Number of columns the board will have
     * @param numRows: Number of rows the board will have
     * @throws IllegalArgumentException: If given negative or zero number of rows/columns.
     */
    Model(int numCols, int numRows, MoveSequence moveSequence) throws IllegalArgumentException {
        if (numCols < 1 || numRows < 1) {
            throw new IllegalArgumentException("A board must have positive number of rows and columns.");
        }
        this.numRows = numRows;
        this.numCols = numCols;
        this.moveSequence = moveSequence;
        blackToMove = true;
        // Set starting pieces
        state = new Content[numRows][numCols];
        for (Content[] row : state) {
            Arrays.fill(row, Content.UNPLAYABLE);
        }
        state[numCols / 2][numRows / 2 - 1] = Content.BLACK;
        state[numCols / 2 - 1][numRows / 2] = Content.BLACK;
        state[numCols / 2 - 1][numRows / 2 - 1] = Content.WHITE;
        state[numCols / 2][numRows / 2] = Content.WHITE;

        for (Coordinates move : moveSequence) {
            try {
                applyMove(move);
            } catch (IllegalMoveException e) {
                // Incredibly sophisticated error handling. Has effect of ignoring all moves after problematic one.
                return;
            }
        }

       reassignMoveAvailability();
    }

    /* Return true if the given coordinate is on the board, false otherwise. */
    boolean isMoveOnBoard(Coordinates move) {
        return move.x >= 0 && move.x <= numCols - 1 && move.y >= 0 && move.y <= numRows - 1;
    }

    int getNumCols() {
        return numCols;
    }

    int getNumRows() {
        return numRows;
    }

    /* Return true if it's black's turn, false otherwise */
    boolean isBlackToMove() {
        return blackToMove;
    }

    void alternateTurn() {
        blackToMove = !blackToMove;
    }

    /**
     * @return Color of player whose turn it is ("Black" or "White").
     */
    String curPlayerString() {
        if (isBlackToMove()) {
            return "Black";
        } else {
            return "White";
        }
    }

    /**
     * @return Content object that represents the current player's piece.
     */
    Content curPlayerPiece() {
        if (isBlackToMove()) {
            return Content.BLACK;
        } else {
            return Content.WHITE;
        }
    }

    /**
     * @param location: location at which to check contents
     * @return a Content object, contents of the board at given location.
     * @throws ArrayIndexOutOfBoundsException: If given location is off the board.
     */
    private Content contentOf(Coordinates location) {
        return state[location.x][location.y];
    }

    /**
     * @param location:   Coordinates of space to change
     * @param newContent: New value for the given location
     */
    private void setContentAtCoordinates(Coordinates location, Content newContent) {
        state[location.x][location.y] = newContent;
    }

    Content[][] getBoardContents() {
        return state;
    }

    MoveSequence getMoveSequence() {
        return moveSequence;
    }

    /**
     * Sets all empty spaces to PLAYABLE or UNPLAYABLE based on whether the player whose turn it is could play there.
     */
    private void reassignMoveAvailability() {
        for (int i = 0; i < numCols; i++) {
            for (int j = 0; j < numRows; j++) {
                Coordinates move = new Coordinates(i, j);
                if (isLegalMove(move)) {
                    setContentAtCoordinates(move, Content.PLAYABLE);
                } else if (contentOf(move).isEmpty()) {
                    setContentAtCoordinates(move, Content.UNPLAYABLE);
                }
            }
        }
    }

    /**
     * @param move where new piece is/would be placed
     * @return a Set of all coordinates of opponent's pieces that would be flipped by given move.
     */
    private Set<Coordinates> getLocationsFlippedByMove(Coordinates move) {
        Content opponentPiece = curPlayerPiece().flipped();
        Coordinates curLocation;
        Set<Coordinates> flippedLocations = new HashSet<>();

        // Can't place piece on non-empty space, or off board.
        if (!isMoveOnBoard(move) || !contentOf(move).isEmpty()) {
            return flippedLocations;
        }

        // Check if there is, in each of the 8 directions, a line of the form:
        // current move coordinate -> <at least one of opponent's pieces> -> player's piece.
        for (byte[] direction : Coordinates.adjacencyVectors) {
            boolean reachedOpponentPiece = false;
            Set<Coordinates> flipCandidates = new HashSet<>();
            curLocation = new Coordinates(move);
            curLocation.x += direction[0];
            curLocation.y += direction[1];
            // If a line of opponent pieces goes off end of board without reaching friendly piece, that line does does
            // not get flipped. That's why end condition for loop is <curLocation fell off board>
            while (isMoveOnBoard(curLocation)) {
                Content curPiece = contentOf(curLocation);
                if (reachedOpponentPiece) {
                    if (curPiece.equals(curPlayerPiece())) {
                        // Found a line of opponent pieces bookended by the current move and a friendly piece, add all
                        // the "candidates" to flippedLocations as they will actually be flipped.
                        flippedLocations.addAll(flipCandidates);
                        break;
                    } else if (curPiece.equals(opponentPiece)) {
                        // Current piece is part of line of opponent pieces, add as a flip "candidate"
                        flipCandidates.add(new Coordinates(curLocation));
//                        continue;
                    } else if (curPiece.isEmpty()) {
                        // Line of opponent pieces ended with empty square, do not flip.
                        break;
                    }
                } else if (!curPiece.equals(opponentPiece)) {
                    // Adjacent piece is friendly or empty, no stones flipped (in current direction)
                    break;
                } else {
                    // Start tracking line of opponent pieces, add them as "candidates" to be flipped.
                    reachedOpponentPiece = true;
                    flipCandidates.add(new Coordinates(curLocation));
                }
                curLocation.x += direction[0];
                curLocation.y += direction[1];
            }
        }
        if (!(flippedLocations.isEmpty())){
            System.out.println(flippedLocations);
        }
        //System.out.println(flippedLocations);
        return flippedLocations;
    }

    /**
     * In Reversi, a move is legal if and only if it flips at least one of the opponent's pieces, so this method just
     * checks that.
     *
     * @param move: move to check legality of
     * @return boolean whether or not move is legal
     */
    private boolean isLegalMove(Coordinates move) {
        return !(getLocationsFlippedByMove(move).isEmpty());
    }

    /**
     * @param c: the Content instance to count
     * @return number of board locations that contain the given content
     */
    int countLocationsContaining(Content c) {
        int total = 0;
        for (int i = 0; i < numCols; i++) {
            for (int j = 0; j < numCols; j++) {
                Coordinates location = new Coordinates(i, j);
                if (contentOf(location).equals(c)) {
                    total++;
                }
            }
        }
        return total;
    }

    /* Extremely simple struct-like subclass */
    static class GameStatus {
        final boolean gameFinished;
        final int blackScore;
        final int whiteScore;

        GameStatus(boolean gameFinished, int blackScore, int whiteScore) {
            this.gameFinished = gameFinished;
            this.blackScore = blackScore;
            this.whiteScore = whiteScore;
        }
    }

    /**
     * @return A GameStatus object that holds black's score, white's score, and a boolean of whether the game has
     * reached an end or not. (In Reversi, games end when neither player has any legal moves available.)
     */
    GameStatus determineOutcome() {
        int blackScore = countLocationsContaining(Content.BLACK);
        int whiteScore = countLocationsContaining(Content.WHITE);
        boolean gameFinished;
        int playableCount = countLocationsContaining(Content.PLAYABLE);
        if (playableCount > 0) {
            gameFinished = false;
        } else {
            // Check if other player has any available moves, then reverse the side effects that causes.
            alternateTurn();
            reassignMoveAvailability();
            gameFinished = countLocationsContaining(Content.PLAYABLE) == 0;
            alternateTurn();
            reassignMoveAvailability();
        }
        return new GameStatus(gameFinished, blackScore, whiteScore);
    }

    /**
     * Applies the given move to the board's state, and adds to move history.
     * Then, changes to other player's turn and calculates available moves for next player.
     *
     * @throws IllegalMoveException if given move is illegal (then does not change state)
     */
    void applyMove(Coordinates move) throws IllegalMoveException {
        if (!isLegalMove(move)) {
            // Throw IllegalMoveException with appropriate reason
            String reason = curPlayerString() + " piece cannot be placed at " + move.toString();
            if (!isMoveOnBoard(move)) {
                reason += " because that would be off the board.";
            } else if (!contentOf(move).isEmpty()) {
                reason += " because there is already a piece there.";
            } else {
                reason += " because that wouldn't flip any pieces.";
            }
            throw new IllegalMoveException(move, reason);
        } else {

            // Flip each opponent piece affected by move
            System.out.println(getLocationsFlippedByMove(move));
            for (Coordinates location : getLocationsFlippedByMove(move)) {
                setContentAtCoordinates(location, curPlayerPiece());
            }

            setContentAtCoordinates(move, curPlayerPiece());
            alternateTurn();
            // Add move to move sequence, reassign PLAYABLE/UNPLAYABLE
            moveSequence.addLast(move);
            reassignMoveAvailability();
        }
    }

    //Called by undo button, removes last move from the move sequence
    void undoMove(){
        moveSequence.removeLast();

    }
}
