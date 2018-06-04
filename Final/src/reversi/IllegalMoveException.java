/* By Josh Gerstein and Liz Nichols, 6/3/2018.
 * Illegal move exception for when player attempts invalid move
 */

package edu.carleton.gersteinj.reversi;

class IllegalMoveException extends Exception {
    private Coordinates attemptedMove;
    private String reason;

    IllegalMoveException(Coordinates attemptedMove, String reason) {
        this.attemptedMove = attemptedMove;
        this.reason = reason;
    }

    String getReason() {
        return reason;
    }

    Coordinates getLocation() {
        return attemptedMove;
    }
}
