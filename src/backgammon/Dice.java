package backgammon;

import java.util.List;
import java.util.Objects;

public record Dice(List<Integer> diceValues) {
    public static final int[] PAIRS = {
        1, 1,
        1, 2,
        1, 3,
        1, 4,
        1, 5,
        1, 6,
        2, 2,
        2, 3,
        2, 4,
        2, 5,
        2, 6,
        3, 3,
        3, 4,
        3, 5,
        3, 6,
        4, 4,
        4, 5,
        4, 6,
        5, 5,
        5, 6,
        6, 6
    };

    public Dice {
        //Doubles
        if (Objects.equals(diceValues.get(0), diceValues.get(1))) {
            Integer dieValue = diceValues.get(0);
            diceValues.add(dieValue);
            diceValues.add(dieValue);
        }
    }

    public static double probability(int die1, int die2) {
        return (die1 == die2) ? 1.0 / 36 : 1.0 / 18;
    }
}
