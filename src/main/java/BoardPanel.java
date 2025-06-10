import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {
    private final Configuration configuration;
    private final Board board;
    private final boolean isPlayer;

    public BoardPanel(Configuration configuration, Board board, boolean isPlayer) {
        this.configuration = configuration;
        this.board = board;
        this.isPlayer = isPlayer;
        setPreferredSize(new Dimension(
            configuration.gridSize() * configuration.cellSize(),
            configuration.gridSize() * configuration.cellSize()
        ));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int y = 0; y < configuration.gridSize(); y++) {
            for (int x = 0; x < configuration.gridSize(); x++) {
                Cell cell = board.getCell(x, y);

                drawCellBackground(g, cell);
                drawCellBorder(g, cell);

                if (shouldDrawShip(cell)) {
                    drawShip(g, cell);
                }

                if (shouldDrawShot(cell)) {
                    drawShot(g, cell);
                }
            }
        }
    }

    private void drawCellBackground(Graphics g, Cell cell) {
        g.setColor(Color.BLUE);
        g.fillRect(
            cellToPixelX(cell),
            cellToPixelY(cell),
            configuration.cellSize(),
            configuration.cellSize()
        );
    }

    private void drawCellBorder(Graphics g, Cell cell) {
        g.setColor(Color.BLACK);
        g.drawRect(
            cellToPixelX(cell),
            cellToPixelY(cell),
            configuration.cellSize(),
            configuration.cellSize()
        );
    }

    private boolean shouldDrawShip(Cell cell) {
        return isPlayer && cell.getType() == CellType.SHIP;
    }

    private void drawShip(Graphics g, Cell cell) {
        int padding = (int) (0.1 * configuration.cellSize());
        int shipSize = configuration.cellSize() - 2 * padding;
        g.setColor(Color.GRAY);
        g.fillRect(
            cellToPixelX(cell) + padding,
            cellToPixelY(cell) + padding,
            shipSize,
            shipSize
        );
    }

    private boolean shouldDrawShot(Cell cell) {
        if (!isPlayer) {
            return board.isRevealedCell(cell) && isShot(cell);
        } else {
            return isShot(cell);
        }
    }

    private boolean isShot(Cell cell) {
        return cell.getType() == CellType.MISS || cell.getType() == CellType.HIT || cell.getType() == CellType.SUNKEN;
    }

    private void drawShot(Graphics g, Cell cell) {
        Color color;
        int padding;

        if (cell.getType() == CellType.MISS) {
            color = Color.WHITE;
            padding = (int) (0.3 * configuration.cellSize());
        } else if (cell.getType() == CellType.HIT) {
            color = Color.YELLOW;
            padding = (int) (0.2 * configuration.cellSize());
        } else if (cell.getType() == CellType.SUNKEN) {
            color = Color.RED;
            padding = (int) (0.1 * configuration.cellSize());
        } else {
            return;
        }

        int diameter = configuration.cellSize() - 2 * padding;
        g.setColor(color);
        g.fillOval(
            cellToPixelX(cell) + padding,
            cellToPixelY(cell) + padding,
            diameter,
            diameter
        );
    }

    private int cellToPixelX(Cell cell) {
        return cell.x() * configuration.cellSize();
    }

    private int cellToPixelY(Cell cell) {
        return cell.y() * configuration.cellSize();
    }
}
