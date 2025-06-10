import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SeaBattleSwing extends JFrame {
    private final Configuration configuration;
    private final SeaBattleGame game;
    private final BoardPanel playerPanel;
    private final BoardPanel botPanel;
    private final JLabel statusLabel;

    public SeaBattleSwing(Configuration configuration) {
        this.configuration = configuration;

        setTitle("Sea Battle - Player vs Bot");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        game = new SeaBattleGame(this.configuration);

        playerPanel = new BoardPanel(this.configuration, game.getPlayerBoard(), true);
        botPanel = new BoardPanel(this.configuration, game.getBotBoard(), false);

        botPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (game.isGameOver()) {
                    return;
                }

                int x = e.getX() / configuration.cellSize();
                int y = e.getY() / configuration.cellSize();

                String result = game.playerShoot(x, y);
                statusLabel.setText(result);

                playerPanel.repaint();
                botPanel.repaint();
            }
        });

        statusLabel = new JLabel("Your turn! Click on Bot's grid.");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel boardsPanel = new JPanel(new FlowLayout());
        boardsPanel.add(playerPanel);
        boardsPanel.add(botPanel);

        add(boardsPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        Configuration configuration = new Configuration(10, 100);
        SwingUtilities.invokeLater(() -> new SeaBattleSwing(configuration));
    }
}
