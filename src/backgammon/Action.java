package backgammon;

import java.util.Arrays;

public class Action {
    private int[] from;
    private int[] to;
    private static final int ACTION_SIZE = 3;

    public Action(int fromIndex, int fromCount, int fromPlayer, int toIndex, int toCount, int toPlayer) {
        if (fromIndex != -1) {
            from = new int[ACTION_SIZE];
            from[0] = fromIndex;
            from[1] = fromCount;
            from[2] = fromPlayer;
        }

        if (toIndex != -1) {
            to = new int[ACTION_SIZE];
            to[0] = toIndex;
            to[1] = toCount;
            to[2] = toPlayer;
        }
    }

    public int[] getFrom() {
        return from;
    }

    public int[] getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return Arrays.equals(from, action.from) && Arrays.equals(to, action.to);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(from);
        result = 31 * result + Arrays.hashCode(to);
        return result;
    }
}
