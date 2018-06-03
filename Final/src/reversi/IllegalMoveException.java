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
