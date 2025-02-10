package backgammon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public record Dice(List<Integer> diceValues) {
    private static final double DOUBLES_PROBABILITY = 1.0 / 36;
    private static final double SINGLES_PROBABILITY = 1.0 / 18;
    private static final int DIE_SIDES = 6;

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
        return (die1 == die2) ? DOUBLES_PROBABILITY : SINGLES_PROBABILITY;
    }

    public static Dice of() {
        Random rand = new Random();
        return new Dice(new ArrayList<>(List.of(rand.nextInt(1, DIE_SIDES + 1),
                rand.nextInt(1, DIE_SIDES + 1))));
    }
}
