/* By Josh Gerstein and Liz Nichols, 6/3/2018.
 * Controller functions as the event handler for the game.
 *
 */

package edu.carleton.gersteinj.reversi;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

public class Controller implements EventHandler<MouseEvent> {
    @FXML
    public BoardView boardView;
    public Button undo;

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
            System.out.println("hello");
            model.removeLastfromMoveSequence();
            MoveSequence newMoveSequence = model.getMoveSequence();
            this.model = new Model(model.getNumCols(), model.getNumRows(), newMoveSequence);

            boardView.initialize(model.getNumCols(), model.getNumRows(), model.getBoardContents());
        }


    }

    private void updateWithMove(Coordinates move) {
        try {
            model.applyMove(move);
            updatePseudoObservers();
        } catch (IllegalMoveException e) {
            System.out.println("Illegal Move: " + e.getReason());
        }
        checkGameFinished();
    }

    private void checkGameFinished() {
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
}
