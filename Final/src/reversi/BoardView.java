/* By Josh Gerstein and Liz Nichols, 6/3/2018.
 * BoardView is main view for the game, and
 * Is instantiated by Controller
 */

package edu.carleton.gersteinj.reversi;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


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
    void initialize(int numCols, int numRows, Content[][] BoardState) {
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

        setStyle("-fx-background-color: #53A548; -fx-grid-lines-visible: true");

        // canonical board notation is such that columns are lettered and rows are numbered
        String boardNotationString = "ABCDEFGH";


        //ADD GAMESPACES AT EACH NODE IN THE GRAPH PANE
        for (int i = 0; i < boardNotationString.length(); i++) {
            for (int j=0; j<8; j++) {
                GameSpace gamespace = new GameSpace(BoardState[i][j], new Coordinates(i, j));
                add(gamespace,i,j);
                GridPane.setHalignment(gamespace, HPos.CENTER);
            }

        }

    }

    //UPDATES CONTENT BASED ON BOARD STATE - RUN FROM THE PSEUDOOBSERVER
    public void update(Content[][] boardState) {
        //TODO

        for (Node child : getChildren()){
            if (child instanceof GameSpace){
                GameSpace gameSpace = (GameSpace) child;
                Coordinates location = gameSpace.getLocation();
                gameSpace.update(boardState[location.x][location.y]);
            }

        }


    }
}

