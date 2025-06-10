import java.util.Map;
import java.util.Random;

public class ShipsPlacer {
    private static final int MAX_ATTEMPTS = 10;

    private final Configuration configuration;
    private final Random random;

    public ShipsPlacer(Configuration configuration) {
        this.configuration = configuration;
        this.random = new Random();
    }

    public void placeShips(Cell[][] grid, Map<Cell, Ship> cell2ship) {
        placeNDeckShips(grid, cell2ship, 1, 4);
        placeNDeckShips(grid, cell2ship, 2, 3);
        placeNDeckShips(grid, cell2ship, 3, 2);
        placeNDeckShips(grid, cell2ship, 4, 1);
    }

    private void placeNDeckShips(Cell[][] grid, Map<Cell, Ship> cell2ship, int numberOfShips, int numberOfDecks) {
        for (int i = 0; i < numberOfShips; i++) {
            placeNDeckShip(grid, cell2ship, numberOfDecks);
        }
    }

    private void placeNDeckShip(Cell[][] grid, Map<Cell, Ship> cell2ship, int numberOfDecks) {
        boolean placed = false;
        int nAttempts = 0;
        while (!placed && nAttempts < MAX_ATTEMPTS) {
            int x = random.nextInt(configuration.gridSize());
            int y = random.nextInt(configuration.gridSize());
            Direction dir = Direction.random(random);

            if (canPlaceNDeckShip(grid, numberOfDecks, x, y, dir)) {
                Ship ship = new Ship();
                for (int i = 0; i < numberOfDecks; i++) {
                    int nx = x + dir.dx * i;
                    int ny = y + dir.dy * i;
                    Cell cell = grid[ny][nx];
                    cell.setType(CellType.SHIP);
                    ship.addDeck(cell);
                    cell2ship.put(cell, ship);
                }
                placed = true;
            }

            nAttempts++;
        }
    }

    private boolean canPlaceNDeckShip(Cell[][] grid, int numberOfDecks, int x, int y, Direction dir) {
        for (int i = 0; i < numberOfDecks; i++) {
            int nx = x + dir.dx * i;
            int ny = y + dir.dy * i;

            if (nx < 0 || nx >= configuration.gridSize() || ny < 0 || ny >= configuration.gridSize()) {
                return false;
            }

            if (!canPlaceDeck(grid, nx, ny)) {
                return false;
            }
        }
        return true;
    }

    private boolean canPlaceDeck(Cell[][] grid, int x, int y) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < configuration.gridSize() && ny >= 0 && ny < configuration.gridSize()) {
                    if (!isAvailableCell(grid, nx, ny)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isAvailableCell(Cell[][] grid, int x, int y) {
        return grid[y][x].getType() == CellType.EMPTY;
    }
}
