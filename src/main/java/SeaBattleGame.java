import java.util.Optional;
import java.util.Random;

public class SeaBattleGame {
    private final Configuration configuration;
    private final Random random;
    private final Board playerBoard;
    private final Board botBoard;
    private boolean gameOver;

    public SeaBattleGame(Configuration configuration) {
        this.configuration = configuration;
        random = new Random(configuration.gameSeed());
        playerBoard = new Board(configuration, false);
        botBoard = new Board(configuration, true);
        gameOver = false;
    }

    public Board getPlayerBoard() {
        return playerBoard;
    }

    public Board getBotBoard() {
        return botBoard;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public String playerShoot(int x, int y) {
        Optional<Boolean> hit = botBoard.shoot(x, y);
        if (hit.isEmpty()) {
            return "...";
        }

        if (botBoard.allShipsSunk()) {
            gameOver = true;
            return "You win!";
        }

        if (!hit.get()) {
            botTurn();
            if (playerBoard.allShipsSunk()) {
                gameOver = true;
                return "Bot wins!";
            }
        }

        return hit.get() ? "Hit!" : "Miss!";
    }

    private void botTurn() {
        while (true) {
            Cell cell = playerBoard.getRandomAvailableCell(random);
            Optional<Boolean> hit = playerBoard.shoot(cell.x(), cell.y());
            if (hit.isPresent() && !hit.get()) {
                break;
            }
        }
    }
}
