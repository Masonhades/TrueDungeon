package me.Masonhades.truedungeon.structure;


public enum Direction {
    NORTH, EAST, SOUTH, WEST;

    public Direction opposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
        };
    }

    public Direction rotate(RoomRotation rotation) {
        return values()[(this.ordinal() + rotation.getMcRotation().ordinal()) % 4];
    }
}
