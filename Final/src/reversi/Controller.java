/* By Josh Gerstein and Liz Nichols, 6/3/2018.
 * Controller functions as the event handler for the game.
 *
 */

package edu.carleton.gersteinj.reversi;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Controller implements EventHandler<MouseEvent> {
    @FXML
    public BoardView boardView;
    public Button undo;
    public Text score;

    private Model model;
    private List<BoardPseudoObserver> pseudoObservers;

    public Controller() {

    }

    public void initialize() {
        this.model = new Model();
        pseudoObservers = new ArrayList<>();
        boardView.initialize(model.getNumCols(), model.getNumRows(), model.getBoardContents());
        pseudoObservers.add(boardView);
        updatePseudoObservers();
        updateCounts();
    }


    public void handle(MouseEvent mouseevent) {
        final Node source = (Node) mouseevent.getTarget();

        System.out.println(source.getId());
        //TODO: delete console logging and comments
        // Class sourceclass = source.getClass();

        if (source instanceof GameSpace) {
            System.out.println("this is a gamespace");
//            int moveX = Character.getNumericValue(source.getId().charAt(0));
//            int moveY = Character.getNumericValue(source.getId().charAt(1));
            Coordinates move = ((GameSpace) source).getLocation();
            try {
                model.applyMove(move);
                updatePseudoObservers();
                updateCounts();
            } catch (IllegalMoveException e) {
                System.out.println("NOT A VALID MOVE: " + e.getReason());
            }
        }

        if(mouseevent.getSource() == undo){
            System.out.println("hello");
            model.undoMove();
            updatePseudoObservers();
            updateCounts();

        }

    }

    //update all observed objects contained in pseudoobserver list from the boardstate
    private void updatePseudoObservers() {
        for (BoardPseudoObserver pseudoObserver : pseudoObservers) {
            pseudoObserver.update(model.getBoardContents());
        }
    }

    private void updateCounts() {
        int whitescore = model.countLocationsContaining(Content.WHITE);
        int blackscore = model.countLocationsContaining(Content.BLACK);

        score.setText("White has a score of :" + Integer.toString(whitescore)+"\n"+"\n"
        + "Black has a score of:" + Integer.toString(blackscore)+"\n");

    }
}
