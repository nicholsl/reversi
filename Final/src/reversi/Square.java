package edu.carleton.gersteinj.reversi;

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

    private final Model model;

    public Square(Model model){
        this.model = model;

        skin = new SquareSkin(this);
    }

    public void pressed(){

    }

}

