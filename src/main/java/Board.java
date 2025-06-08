import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Board {
    private final Configuration configuration;
    private final Cell[][] grid;
    private final boolean[][] revealed;
    private final Map<Cell, Ship> cell2ship;

    public Board(Configuration configuration, boolean isBot) {
        this.configuration = configuration;
        this.grid = initGrid();
        this.revealed = isBot ? new boolean[configuration.gridSize()][configuration.gridSize()] : null;
        this.cell2ship = new HashMap<>();
        placeShips();
    }

    public Optional<Boolean> shoot(int x, int y) {
        Cell cell = getCell(x, y);

        return switch (cell.getType()) {
            case MISS, HIT, SUNKEN -> Optional.empty();
            case EMPTY -> {
                handleMiss(x, y);
                yield Optional.of(false);
            }
            case SHIP -> {
                handleHit(x, y);
                yield Optional.of(true);
            }
            default -> throw new IllegalStateException("Undefined cell type: " + cell.getType());
        };
    }

    public boolean isRevealed(Cell cell) {
        return revealed != null && revealed[cell.y()][cell.x()];
    }

    public Cell getCell(int x, int y) {
        return grid[y][x];
    }

    public boolean allShipsSunk() {
        for (int y = 0; y < configuration.gridSize(); y++) {
            for (int x = 0; x < configuration.gridSize(); x++) {
                if (grid[y][x].getType() == CellType.SHIP) {
                    return false;
                }
            }
        }
        return true;
    }

    private Cell[][] initGrid() {
        Cell[][] grid = new Cell[configuration.gridSize()][configuration.gridSize()];
        for (int y = 0; y < configuration.gridSize(); y++) {
            for (int x = 0; x < configuration.gridSize(); x++) {
                grid[y][x] = new Cell(x, y, CellType.EMPTY);
            }
        }
        return grid;
    }

    private void placeShips() {
        ShipsPlacer shipsPlacer = new ShipsPlacer(configuration);
        shipsPlacer.placeShips(grid, cell2ship);
    }

    private void handleMiss(int x, int y) {
        markRevealed(x, y);
        grid[y][x].setType(CellType.MISS);
    }

    private void handleHit(int x, int y) {
        Cell cell = getCell(x, y);
        cell.setType(CellType.HIT);
        markRevealed(x, y);

        Ship ship = cell2ship.get(cell);
        if (ship != null && ship.isSunken()) {
            revealAroundSunkenShip(ship);
        }
    }

    private void revealAroundSunkenShip(Ship ship) {
        for (Cell c : ship.getDecks()) {
            c.setType(CellType.SUNKEN);
            revealSurroundingCells(c);
        }
    }

    private void revealSurroundingCells(Cell center) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int nx = center.x() + dx;
                int ny = center.y() + dy;

                if (isValidCoordinate(nx, ny)) {
                    Cell neighbor = getCell(nx, ny);
                    if (neighbor.getType() == CellType.EMPTY) {
                        neighbor.setType(CellType.MISS);
                        markRevealed(nx, ny);
                    }
                }
            }
        }
    }

    private boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < configuration.gridSize() &&
               y >= 0 && y < configuration.gridSize();
    }

    private void markRevealed(int x, int y) {
        if (revealed != null) {
            revealed[y][x] = true;
        }
    }
}
