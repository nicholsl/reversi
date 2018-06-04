/* By Josh Gerstein and Liz Nichols, 6/3/2018.
 * Controller functions as the event handler for the game.
 *
 */

package edu.carleton.gersteinj.reversi;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
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

    public void handle(MouseEvent mouseEvent) {
        EventTarget target = mouseEvent.getTarget();
        if (target.equals(boardView)) {
            mouseEvent.consume();
        }
        if (target instanceof GameSpace && boardView.getChildren().contains(target)) {
            GameSpace gameSpace = (GameSpace) target;
            Coordinates move = gameSpace.getLocation();
            updateWithMove(move);
        }
        if (mouseEvent.getSource() == undo) {
            model.undoMove();
        }
        updatePseudoObservers();
    }

    private void updateWithMove(Coordinates move) {
        try {
            model.applyMove(move);
            updatePseudoObservers();
        } catch (IllegalMoveException e) {
            System.out.println("Illegal Move: " + e.getReason());
        }
        alertIfCantPlay();
    }

    private void alertIfCantPlay() {
        Model.GameStatus gameStatus = model.determineOutcome();
        if (gameStatus.gameFinished) {
            System.out.println("");
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
