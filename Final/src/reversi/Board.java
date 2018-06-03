package edu.carleton.gersteinj.reversi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/* Board is the part of the model that stores the current state of a game. It also stores the moves played in a game
 * with an instance of MoveSequence.
 */
class Board {
    enum Content {
        EMPTY, BLACK, WHITE;
        Content flipped(){
            if (this.equals(BLACK)) {
                return WHITE;
            } else if (this.equals(WHITE)) {
                return BLACK;
            } else {
                return EMPTY;
            }
        }
    }
    private Content[][] state;
    private boolean blackToMove;
    private MoveSequence moveSequence;
    private final int numRows;
    private final int numCols;


    /**
     * Default constructor, 8x8 board.
     * Initializes a board in the state that games start in (4 pieces in middle, black to move).
     */
    Board() {
        this(8, 8, new MoveSequence());
    }

    /**
     * Constructor. Initializes a board in the state that games start in (4 pieces in middle, black to move), then
     * applies each move from the given moveSequence in order. If ANY move in the move sequence is illegal, ignores
     * all subsequent moves.
     *
     * @param numRows: Number of rows the board will have
     * @param numCols: Number of columns the board will have
     * @throws IllegalArgumentException: If given negative or zero number of rows/columns.
     */
    Board(int numRows, int numCols, MoveSequence moveSequence) throws IllegalArgumentException {
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
            Arrays.fill(row, Content.EMPTY);
        }
        state[numCols/2][numRows/2 - 1] = Content.BLACK;
        state[numCols/2 - 1][numRows/2] = Content.BLACK;
        state[numCols/2 - 1][numRows/2 - 1] = Content.WHITE;
        state[numCols/2][numRows/2] = Content.WHITE;
        for (Coordinates move : moveSequence) {
            try {
                applyMove(move);
            } catch (IllegalMoveException e) {
                // Incredibly sophisticated error handling. Has effect of ignoring all moves after problematic one.
                return;
            }
        }
    }

    /* Return true if the given coordinate is on the board, false otherwise. */
    boolean isMoveOnBoard(Coordinates move) {
        return move.x >= 0 && move.x <= numCols - 1 && move.y >= 0 && move.y <= numRows - 1;
    }

    /* Return true if it's black's turn, false otherwise */
    boolean isBlackToMove() {
        return blackToMove;
    }

    /**
     * @return Color of player whose turn it is ("Black" or "White").
     */
    String curPlayer() {
        if (isBlackToMove()) {
            return "Black";
        } else {
            return "White";
        }
    }

    /**
     * @param location: location at which to check contents
     * @return a Content object, contents of the board at given location.
     * @throws ArrayIndexOutOfBoundsException: If given location is off the board.
     */
    private Content contentsAt(Coordinates location) throws ArrayIndexOutOfBoundsException {
        return state[location.x][location.y];
    }

    Content[][] getBoardContents() {
        return state;
    }

    /**
     * Compute and return the current coordinates at which the current player could place a piece.
     *
     * @return A Set of the available moves
     */
    Set<Coordinates> getAvailableMoves() {
        Set<Coordinates> availableMoves = new HashSet<>();
        for (byte i = 0; i < 8; i++) {
            for (byte j = 0; j < 8; j++) {
                Coordinates move = new Coordinates(i, j);
                if (isLegalMove(move)) {
                    availableMoves.add(move);
                }
            }
        }
        return availableMoves;
    }

    /**
     * @param move where new piece is/would be placed
     * @return a Set of all coordinates of opponent's pieces that would be flipped by given move.
     */
    private Set<Coordinates> getLocationsFlippedByMove(Coordinates move) {
        Content playerPiece = blackToMove ? Content.BLACK : Content.WHITE;
        Content opponentPiece = playerPiece.flipped();
        Coordinates curLocation;
        Set<Coordinates> flippedLocations = new HashSet<>();

        // Can't place piece on non-empty space, or off board, so no pieces will be flipped in this case.
        if (!isMoveOnBoard(move) || state[move.x][move.y] != Content.EMPTY) {
            return flippedLocations;
        }

        // Check if there is, in each of the 8 directions, a line of the form:
        // current move coordinate -> <opponent's pieces> -> player's piece
        byte[][] directionVectors = new byte[][]{{-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}};
        for (byte[] direction : directionVectors) {
            boolean reachedOpponentPiece = false;
            Set<Coordinates> flipCandidates = new HashSet<>();
            curLocation = new Coordinates(move);
            curLocation.x += direction[0];
            curLocation.y += direction[1];
            while (isMoveOnBoard(curLocation)) {
                Content curPiece = state[curLocation.x][curLocation.y];
                if (reachedOpponentPiece) {
                    if (curPiece == playerPiece) {
                        // Found a line of opponent pieces bookended by the current move and a friendly piece, add all
                        // the "candidates" to flippedLocations as they will actually be flipped.
                        flippedLocations.addAll(flipCandidates);
                    } else if (curPiece.equals(opponentPiece)) {
                        // Current piece is part of line of opponent pieces, add as a flip "candidate"
                        flipCandidates.add(curLocation);
                        continue;
                    } else if (curPiece.equals(Content.EMPTY)) {
                        // Line of opponent pieces ended with empty square, do not flip.
                        break;
                    }
                } else if (!curPiece.equals(opponentPiece)) {
                    // Adjacent piece is friendly or empty, no stones flipped (in current direction)
                    break;
                } else {
                    // Start tracking line of opponent pieces, add them as "candidates" to be flipped.
                    reachedOpponentPiece = true;
                    flipCandidates.add(curLocation);
                }
                curLocation.x += direction[0];
                curLocation.y += direction[1];
            }
        }
        return flippedLocations;
    }


    /**
     * In reversi, a move is legal if and only if it flips at least one of the opponent's pieces, so this function just
     * checks that.
     *
     * @param move: move to check legality of
     * @return boolean whether or not move is legal
     */
    private boolean isLegalMove(Coordinates move) {
        return getLocationsFlippedByMove(move).isEmpty();
    }

    /**
     * Applies the given move to the board's state.
     *
     * @throws IllegalMoveException if given move is illegal (then does not change state)
     */
    void applyMove(Coordinates move) throws IllegalMoveException {
        if (!isLegalMove(move)) {
            String reason = curPlayer() + " piece cannot be placed at " + move.toString();
            if (!isMoveOnBoard(move)) {
                reason += " because that would be off the board.";
            } else if (!state[move.x][move.y].equals(Content.EMPTY)) {
                reason += " because there is already a piece there.";
            } else {
                reason += " because that wouldn't flip any pieces.";
            }
            throw new IllegalMoveException(move, reason);
        } else {
            // Add move to move sequence
            moveSequence.addLast(move);
            // Flip each opponent piece
            for (Coordinates location : getLocationsFlippedByMove(move)) {
                state[location.x][location.y] = contentsAt(location).flipped();
            }
            blackToMove = !blackToMove;
        }
    }

}
