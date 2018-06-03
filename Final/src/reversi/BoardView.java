package edu.carleton.gersteinj.reversi;

import javafx.fxml.FXML;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;


/**
 * This class is the main view for the game board.
 */
public class BoardView extends GridPane implements BoardPseudoObserver {
    @FXML
    private int squareSize;

    private int numCols;
    private int numRows;

    public void setSquareSize(int squareSize) {
        this.squareSize = squareSize;
    }

    public int getSquareSize() {
        return squareSize;
    }

    /**
     * Sort of like a constructor, except called from a controller instance.
     *
     * @param numCols
     * @param numRows
     */
    void initialize(int numCols, int numRows) {
        this.numCols = numCols;
        this.numRows = numRows;

        getStyleClass().add("game-grid"); //TODO: move this to css if possible

        for (int i = 0; i < numCols; i++) {
            ColumnConstraints column = new ColumnConstraints(squareSize);
            getColumnConstraints().add(column);

        }

        for (int i = 0; i < numRows; i++) {
            RowConstraints row = new RowConstraints(squareSize);
            getRowConstraints().add(row);
        }

        setStyle("-fx-background-color: blue; -fx-grid-lines-visible: true");


    }

    public void update(Content[][] boardState) {
        //TODO
    }


//
//    Group root = new Group();
//    Scene s = new Scene(root, 300, 300, Color.BLACK);
//
//    Rectangle rect = new Rectangle(25,25,250,250);


    //root.getChildren().add(r);


//    Boardskin(Model board);
//
//    private final Square[][] squares = new Square[8][8];
//
//    getStyleClass().add("board");
//
//    for(int i = 0; i < 8; i++) {
//        for (int j = 0; j < 8; j++) {
//            add(board.getSquare(i, j).getSkin(), i, j);
//        }
//    }
}

