package backgammon;

import java.util.List;
import java.util.Objects;

public record Dice(List<Integer> diceValues) {
    public Dice {
        //Doubles
        if (Objects.equals(diceValues.get(0), diceValues.get(1))) {
            Integer dieValue = diceValues.get(0);
            diceValues.add(dieValue);
            diceValues.add(dieValue);
        }
    }
}
