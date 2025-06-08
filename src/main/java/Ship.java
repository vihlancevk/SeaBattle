import java.util.ArrayList;
import java.util.List;

public class Ship {
    private final List<Cell> decks;

    public Ship() {
        this.decks = new ArrayList<>();
    }

    public List<Cell> getDecks() {
        return decks;
    }

    public void addDeck(Cell deck) {
        decks.add(deck);
    }

    public boolean isSunken() {
        for (Cell deck : decks) {
            if (deck.getType() == CellType.SHIP) {
                return false;
            }
        }
        return true;
    }
}
