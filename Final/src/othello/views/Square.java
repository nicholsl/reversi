package edu.carleton.gersteinj.othello.views;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;

public class Square {
    enum State { EMPTY, BLACK, WHITE}

    private final SquareSkin skin;

    private javafx.beans.property.ReadOnlyObjectWrapper<State> state = new javafx.beans.property.ReadOnlyObjectWrapper<>(State.EMPTY);
    public javafx.beans.property.ReadOnlyObjectProperty<State> stateProperty() {
        return state.getReadOnlyProperty();
    }
    public State getState() {
        return state.get();
    }

    private final Game game;

    public Square(Game game){
        this.game = game;

        skin = new SquareSkin(this);
    }

}

class SquareSkin extends StackPane {

    static final Image blackImage = new Image(
            "http://icons.iconarchive.com/icons/double-j-design/origami-colored-pencil/128/green-cd-icon.png"
    );
    static final Image whiteImage = new Image(
            "http://icons.iconarchive.com/icons/double-j-design/origami-colored-pencil/128/blue-cross-icon.png"
    );

    private final ImageView imageView = new ImageView();

    SquareSkin(final Square square) {
        getStyleClass().add("square");

        imageView.setMouseTransparent(true);

        getChildren().setAll(imageView);
        setPrefSize(crossImage.getHeight() + 20, crossImage.getHeight() + 20);

        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                square.pressed();
            }
        });

        square.stateProperty().addListener(new ChangeListener<Square.State>() {
            @Override
            public void changed(ObservableValue<? extends Square.State> observableValue, Square.State oldState, Square.State state) {
                switch (state) {
                    case EMPTY:
                        imageView.setImage(null);
                        break;
                    case BLACK:
                        imageView.setImage(blackImage);
                        break;
                    case WHITE:
                        imageView.setImage(whiteImage);
                        break;
                }
            }
        });
    }
}

