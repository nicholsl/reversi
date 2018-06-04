/* By Josh Gerstein and Liz Nichols, 6/3/2018.
 * Content defines states that a space on the board could be in (Black stone, white stone
 * playable, and unplayable, as well as a flip function for content to flip content from black to white
 */

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