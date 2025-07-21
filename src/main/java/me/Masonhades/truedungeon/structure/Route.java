package me.Masonhades.truedungeon.structure;

public class Route {
    private final Direction entrance;
    private final Direction exit;

    public Route(Direction entrance, Direction exit) {
        this.entrance = entrance;
        this.exit = exit;
    }

    public Direction getEntrance() {
        return entrance;
    }

    public Direction getExit() {
        return exit;
    }

    @Override
    public String toString() {
        return entrance + " → " + exit;
    }
}
