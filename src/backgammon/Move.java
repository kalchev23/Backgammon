package backgammon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Move {
    private List<Action> actions;
    private int barPlayer1;
    private int barPlayer2;
    private int homePlayer1;
    private int homePlayer2;
    private int checkersOutPlayer1;
    private int checkersOutPlayer2;

    public Move(List<Action> actions, int homePlayer1, int homePlayer2, int barPlayer1, int barPlayer2,
                int checkersOutPlayer1, int checkersOutPlayer2) {
        this.actions = new ArrayList<>(actions);
        this.homePlayer1 = homePlayer1;
        this.homePlayer2 = homePlayer2;
        this.barPlayer1 = barPlayer1;
        this.barPlayer2 = barPlayer2;
        this.checkersOutPlayer1 = checkersOutPlayer1;
        this.checkersOutPlayer2 = checkersOutPlayer2;
    }

    public List<Action> getActions() {
        return actions;
    }

    public int getBarPlayer1() {
        return barPlayer1;
    }

    public int getBarPlayer2() {
        return barPlayer2;
    }

    public int getHomePlayer1() {
        return homePlayer1;
    }

    public int getHomePlayer2() {
        return homePlayer2;
    }

    public int getCheckersOutPlayer1() {
        return checkersOutPlayer1;
    }

    public int getCheckersOutPlayer2() {
        return checkersOutPlayer2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return Objects.equals(actions, move.actions) && barPlayer1 == move.barPlayer1 && barPlayer2 == move.barPlayer2
                && homePlayer1 == move.homePlayer1 && homePlayer2 == move.homePlayer2
                && checkersOutPlayer1 == move.checkersOutPlayer1 && checkersOutPlayer2 == move.checkersOutPlayer2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(actions, barPlayer1, barPlayer2, homePlayer1, homePlayer2,
                checkersOutPlayer1, checkersOutPlayer2);
    }
}
