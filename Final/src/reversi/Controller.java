package edu.carleton.gersteinj.reversi;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    @FXML
    private BoardView boardView;

    private Model model;
    private List<BoardPseudoObserver> pseudoObservers;

    public void initialize() {
        this.model = new Model();
        pseudoObservers = new ArrayList<>();
        boardView.initialize(model.getNumCols(), model.getNumRows(),model.getBoardContents());
        pseudoObservers.add(boardView);
        this.updatePseudoObservers();
    }

    public void handle(MouseEvent mouseevent){
        model.applyMove()

    }


    private void updatePseudoObservers() {
        for (BoardPseudoObserver pseudoObserver : pseudoObservers) {
            pseudoObserver.update(model.getBoardContents());
        }
    }
}
