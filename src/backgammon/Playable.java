package backgammon;

@FunctionalInterface
public interface Playable {
    Move playMove(GameState state);
}
