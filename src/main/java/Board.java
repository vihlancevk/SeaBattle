import java.util.*;

public class Board {
    private final Configuration configuration;
    private final BoardState boardState;
    private final Map<Cell, Ship> cell2ship;

    public Board(Configuration configuration, boolean isBot) {
        this.configuration = configuration;
        this.boardState = initBoardState(configuration.gridSize(), isBot);
        this.cell2ship = new HashMap<>();
        placeShips();
    }

    public Cell getCell(int x, int y) {
        return boardState.getCell(x, y);
    }

    public Cell getRandomAvailableCell(Random random) {
        return boardState.getRandomAvailableCell(random);
    }

    public boolean isRevealed(Cell cell) {
        return boardState.isRevealed(cell);
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

    public boolean allShipsSunk() {
        return boardState.allShipsSunk(configuration.gridSize());
    }

    private BoardState initBoardState(int gridSize, boolean isBot) {
        Cell[][] grid = new Cell[gridSize][gridSize];
        List<Cell> cells = !isBot ? new ArrayList<>() : null;
        boolean[][] revealed = isBot ? new boolean[gridSize][gridSize] : null;
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                Cell cell = new Cell(x, y, CellType.EMPTY);
                grid[y][x] = cell;
                if (cells != null) {
                    cells.add(cell);
                }
            }
        }
        return new BoardState(grid, cells, revealed);
    }

    private void placeShips() {
        ShipsPlacer shipsPlacer = new ShipsPlacer(configuration);
        shipsPlacer.placeShips(boardState.grid(), cell2ship);
    }

    private void handleMiss(int x, int y) {
        setRevealed(x, y);
        boardState.getCell(x, y).setType(CellType.MISS);
    }

    private void handleHit(int x, int y) {
        Cell cell = getCell(x, y);
        cell.setType(CellType.HIT);
        setRevealed(x, y);

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
                        setRevealed(nx, ny);
                    }
                }
            }
        }
    }

    private boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < configuration.gridSize() &&
               y >= 0 && y < configuration.gridSize();
    }

    private void setRevealed(int x, int y) {
        boardState.setRevealed(x, y);
    }

    private record BoardState(Cell[][] grid, List<Cell> availableCells, boolean[][] revealed) {
        public Cell getCell(int x, int y) {
            return grid[y][x];
        }

        public Cell getRandomAvailableCell(Random random) {
            if (availableCells == null) {
                throw new IllegalStateException("Impossible call this method for player board");
            }
            return availableCells.get(random.nextInt(availableCells.size()));
        }

        public boolean isRevealed(Cell cell) {
            return revealed != null && revealed[cell.y()][cell.x()];
        }

        public void setRevealed(int x, int y) {
            if (availableCells != null) {
                Cell cell = getCell(x, y);
                availableCells.remove(cell);
            }
            if (revealed != null) {
                revealed[y][x] = true;
            }
        }

        public boolean allShipsSunk(int gridSize) {
            for (int y = 0; y < gridSize; y++) {
                for (int x = 0; x < gridSize; x++) {
                    if (grid[y][x].getType() == CellType.SHIP) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
