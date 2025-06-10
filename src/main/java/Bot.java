import java.util.*;

public class Bot {
    private final Configuration configuration;
    private final Board playerBoard;

    public Bot(Configuration configuration, Board playerBoard) {
        this.configuration = configuration;
        this.playerBoard = playerBoard;
    }

    public void shoot(Random random) {
        while (true) {
            Optional<Cell> optionalCell = playerBoard.getRandomAvailableCell(random);
            if (optionalCell.isEmpty()) {
                return;
            }

            Cell cell = optionalCell.get();
            Optional<Boolean> hitResult = playerBoard.shoot(cell.x(), cell.y());

            if (hitResult.isEmpty()) {
                continue;
            }

            if (hitResult.get()) {
                destroyConnectedShipParts(cell);
            } else {
                return;
            }
        }
    }

    private void destroyConnectedShipParts(Cell startCell) {
        Set<Cell> visited = new HashSet<>();
        Queue<Cell> queue = new LinkedList<>();
        queue.add(startCell);

        while (!queue.isEmpty()) {
            Cell cell = queue.poll();
            if (!visited.add(cell)) {
                continue;
            }

            shootIfPossible(cell);

            for (Direction dir : Direction.values()) {
                int nx = cell.x() + dir.dx;
                int ny = cell.y() + dir.dy;

                if (isUnrevealedShipDeck(nx, ny)) {
                    queue.add(playerBoard.getCell(nx, ny));
                }
            }
        }
    }

    private void shootIfPossible(Cell cell) {
        playerBoard.shoot(cell.x(), cell.y());
    }

    private boolean isUnrevealedShipDeck(int x, int y) {
        if (!isWithinBounds(x, y)) {
            return false;
        }

        Cell cell = playerBoard.getCell(x, y);
        return cell.getType() == CellType.SHIP;
    }

    private boolean isWithinBounds(int x, int y) {
        int size = configuration.gridSize();
        return x >= 0 && x < size && y >= 0 && y < size;
    }
}
