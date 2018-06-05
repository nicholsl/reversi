/* By Josh Gerstein and Liz Nichols, 6/3/2018.
 * Controller functions as the event handler for the game.
 *
 */

package edu.carleton.gersteinj.reversi;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Controller implements EventHandler<MouseEvent> {
    @FXML
    public BoardView boardView;
    public Button undo;
    public Text score;
    public Text toPlay;

    private Model model;
    private List<BoardPseudoObserver> pseudoObservers;
    private int whitescore;
    private int blackscore;

    public Controller() {

    }

    /**
     * initialize model
     */
    public void initialize() {
        this.model = new Model();
        pseudoObservers = new ArrayList<>();
        boardView.initialize(model.getNumCols(), model.getNumRows(), model.getBoardContents());
        pseudoObservers.add(boardView);
        updatePseudoObservers();
        updateCounts();
    }

    /**
     *
     * @param mouseEvent
     */
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

    /**
     *
     * @param move
     */
    private void updateWithMove(Coordinates move) {
        try {
            model.applyMove(move);
            updatePseudoObservers();
            updateCounts();
            currentPlayer();
        } catch (IllegalMoveException e) {
            System.out.println("Illegal Move: " + e.getReason());
        }
        alertIfCantPlay();
    }

    /**
     * generates an alert when a turn is skipped or when the game is over
     */
    private void alertIfCantPlay() {
        Model.GameStatus gameStatus = model.determineOutcome();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (gameStatus.gameFinished) {
            alert.setTitle("Game finsihed!");
            alert.setHeaderText("The game is over");
            alert.setContentText("white:" + Integer.toString(whitescore) + "black: "+ Integer.toString(blackscore));
        } else if (!gameStatus.gameFinished){

        }
    }

    /**
     * update all observed objects contained in pseudoobserver list from the boardstate
     */

    private void updatePseudoObservers() {
        for (BoardPseudoObserver pseudoObserver : pseudoObservers) {
            pseudoObserver.update(model.getBoardContents());
        }
    }

    /**
     * update counts of pieces
     */
    private void updateCounts() {
        whitescore = model.countLocationsContaining(Content.WHITE);
        blackscore = model.countLocationsContaining(Content.BLACK);

        score.setText("White has a score of :" + Integer.toString(whitescore)+"\n"+"\n"
        + "Black has a score of:" + Integer.toString(blackscore)+"\n");

    }

    /**
     * updates current player dialogue
     */
    private void currentPlayer(){
        String player = model.curPlayerString();
        toPlay.setText(player);

    }
}
