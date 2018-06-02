package edu.carleton.gersteinj.othello.views;

/**
 * Board state will be kept track of with an 8x8x3 matrix - columns, rows, content
 */
public class Board extends GridPane {

    private final BoardSkin skin;

    private final Square[][] squares = new Square[8][8];

    public Board(Game game) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j] = new Square(game);
            }
        }

    }
}
