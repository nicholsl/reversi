package edu.carleton.gersteinj.reversi;

import javafx.fxml.FXML;

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
        boardView.initialize(model.getNumCols(), model.getNumRows());
        pseudoObservers.add(boardView);
        this.updatePseudoObservers();
    }

    private void updatePseudoObservers() {
        for (BoardPseudoObserver pseudoObserver : pseudoObservers) {
            pseudoObserver.update(model.getBoardContents());
        }
    }
}
