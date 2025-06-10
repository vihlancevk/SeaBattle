import java.util.Random;

public enum Direction {
    LEFT(-1, 0),
    UP(0, -1),
    RIGHT(1, 0),
    DOWN(0, 1);

    final int dx;
    final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public static Direction random(Random random) {
        return values()[random.nextInt(values().length)];
    }

    public static Direction getNextDirection(Direction direction) {
        return switch (direction) {
            case LEFT -> UP;
            case UP -> RIGHT;
            case RIGHT -> DOWN;
            case DOWN -> LEFT;
        };
    }
}
