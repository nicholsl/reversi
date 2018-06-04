/* By Josh Gerstein and Liz Nichols, 6/3/2018.
 * MoveSequence stores a linkedlist of moves that have been played
 * in that game. It is used to initialize a board
 */

package edu.carleton.gersteinj.reversi;

import java.io.Serializable;
import java.util.LinkedList;

/*
 * MoveSequence is a LinkedList of Model.Coordinates objects, with an appropriately changed string representation
 */
public class MoveSequence extends LinkedList<Coordinates> {
    /* Instead of returning [item1,item2,...,itemN], returns item1item2item3...itemN */
    public String toString() {
        StringBuilder moveString = new StringBuilder();
        for (Coordinates move : this) {
            moveString.append(move.toString());
        }
        return moveString.toString();
    }
}
