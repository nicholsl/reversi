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

        String boardNotationString = "abcdefgh";

        for (int i = 0; i < boardNotationString.length(); i++) {
            for (int j=0; j<8; j++) {
//                Circle redCirc = new Circle(4.0, Color.RED);
//                redCirc.setId(Integer.toString(i*10+j));
//                add(redCirc,i, j);
                GameSpace gamespace = new GameSpace(BoardState[i][j]);
                gamespace.setId((Character.toString(boardNotationString.charAt(i))+Integer.toString(j+1)));
                add(gamespace,i,j);
                GridPane.setHalignment(gamespace, HPos.CENTER);
            }

        }
//        addEventFilter(MouseEvent.MOUSE_CLICKED, event -> System.out.println(event.getTarget()));
        addEventFilter(MouseEvent.MOUSE_CLICKED, event -> Controller.handle());



    }

    public void update(Content[][] boardState) {
        //TODO
        System.out.println(boardState);

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

