import java.util.*;

public class Board {
    private final Configuration configuration;
    private final Cell[][] grid;
    private final CellsInfo cellsInfo;
    private final Map<Cell, Ship> cell2ship;

    public Board(Configuration configuration) {
        this.configuration = configuration;
        this.grid = initGrid(configuration.gridSize());
        this.cellsInfo = initCellsInfo(grid, configuration.gridSize());
        this.cell2ship = placeShips(configuration, grid);
    }

    public Cell getCell(int x, int y) {
        return grid[y][x];
    }

    public Cell getRandomAvailableCell(Random random) {
        return cellsInfo.getRandomAvailableCell(random);
    }

    public boolean isRevealedCell(Cell cell) {
        return cellsInfo.isRevealedCell(cell);
    }

    public Optional<Boolean> shoot(int x, int y) {
        Cell cell = getCell(x, y);
        return switch (cell.getType()) {
            case MISS, HIT, SUNKEN -> Optional.empty();
            case EMPTY -> {
                handleMiss(cell);
                yield Optional.of(false);
            }
            case SHIP -> {
                handleHit(cell);
                yield Optional.of(true);
            }
            default -> throw new IllegalStateException("Undefined cell type: " + cell.getType());
        };
    }

    public boolean allShipsSunk() {
        for (int y = 0; y < configuration.gridSize(); y++) {
            for (int x = 0; x < configuration.gridSize(); x++) {
                if (getCell(x, y).getType() == CellType.SHIP) {
                    return false;
                }
            }
        }
        return true;
    }

    private static Cell[][] initGrid(int gridSize) {
        Cell[][] grid = new Cell[gridSize][gridSize];
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                Cell cell = new Cell(x, y, CellType.EMPTY);
                grid[y][x] = cell;
            }
        }
        return grid;
    }

    private static CellsInfo initCellsInfo(Cell[][] grid, int gridSize) {
        List<Cell> availableCells = new ArrayList<>(gridSize);
        boolean[][] revealed = new boolean[gridSize][gridSize];

        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                availableCells.add(grid[y][x]);
            }
        }

        return new CellsInfo(availableCells, revealed);
    }

    private static HashMap<Cell, Ship> placeShips(Configuration configuration, Cell[][] grid) {
        HashMap<Cell, Ship> cell2ship = new HashMap<>();
        ShipsPlacer shipsPlacer = new ShipsPlacer(configuration);
        shipsPlacer.placeShips(grid, cell2ship);
        return cell2ship;
    }

    private void handleMiss(Cell cell) {
        cell.setType(CellType.MISS);
        cellsInfo.updateInfoAboutCell(cell);
    }

    private void handleHit(Cell cell) {
        cell.setType(CellType.HIT);
        cellsInfo.updateInfoAboutCell(cell);
        Ship ship = cell2ship.get(cell);
        if (ship != null && ship.isSunken()) {
            revealAroundSunkenShip(ship);
        }
    }

    private void revealAroundSunkenShip(Ship ship) {
        for (Cell cell : ship.getDecks()) {
            cell.setType(CellType.SUNKEN);
            revealSurroundingCells(cell);
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
                        cellsInfo.updateInfoAboutCell(neighbor);
                    }
                }
            }
        }
    }

    private boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < configuration.gridSize() &&
               y >= 0 && y < configuration.gridSize();
    }

    private record CellsInfo(List<Cell> availableCells, boolean[][] revealed) {
        public Cell getRandomAvailableCell(Random random) {
            return availableCells.get(random.nextInt(availableCells.size()));
        }

        public boolean isRevealedCell(Cell cell) {
            return revealed[cell.y()][cell.x()];
        }

        public void updateInfoAboutCell(Cell cell) {
            availableCells.remove(cell);
            revealed[cell.y()][cell.x()] = true;
        }
    }
}
