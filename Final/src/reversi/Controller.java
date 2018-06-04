package edu.carleton.gersteinj.reversi;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

public class Controller implements EventHandler<MouseEvent> {
    @FXML
    public BoardView boardView;

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
            } catch (IllegalMoveException e) {
                System.out.println("NOT A VALID MOVE: " + e.getReason());
            }
        }

//        if (source.getId()=='undo')
//            model.undoMove();


    }


    private void updatePseudoObservers() {
        for (BoardPseudoObserver pseudoObserver : pseudoObservers) {
            pseudoObserver.update(model.getBoardContents());
        }
    }
}
