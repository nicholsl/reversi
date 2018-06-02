package edu.carleton.gersteinj.othello;
// Stores the history of a completed game.

import edu.carleton.gersteinj.othello.views.Board;

import java.util.LinkedList;
import java.util.List;

public class History {

    private List<Board.Coordinates> moveSequence;
    int whiteCount = 0;
    int blackCount = 0;
    String finalWinner;

    public History() {
        this(new LinkedList<Board.Coordinates>());
    }

    public History(List<Board.Coordinates> moveSequence) {
        this.moveSequence = moveSequence;
    }

    public List<Board.Coordinates> getMoveSequence() {
        return moveSequence;
    }

    public void setMoveSequence() {

    }

    public String getWhiteCount() {

    }

    public String setWhiteCount() {

    }

    public String toString() {
        String moveString = "";
        for (Board.Coordinates move : moveSequence){

        }
    }

}
