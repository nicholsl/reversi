package edu.carleton.gersteinj.reversi;

public enum Content {
    PLAYABLE, BLACK, WHITE, UNPLAYABLE; //TODO: Propagate this change.

    Content flipped(){
        if (this.equals(BLACK)) {
            return WHITE;
        } else if (this.equals(WHITE)) {
            return BLACK;
        } else {
            return this;
        }
    }

    boolean isEmpty(){
        return (this.equals(PLAYABLE) || this.equals(UNPLAYABLE));
    }
}