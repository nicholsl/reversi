package edu.carleton.gersteinj.othello;

import java.util.LinkedList;

/*
 * MoveSequence is a LinkedList of Board.Coordinates objects, with an appropriately changed string representation
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
