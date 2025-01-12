package backgammon.montecarlotreesearch;

import backgammon.GameState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class Node {
    GameState state;
    Node parent;
    List<Node> children = new ArrayList<>();
    int visits = 0;
    double wins = 0.0;

    public Node(GameState state, Node parent) {
        this.state = state;
        this.parent = parent;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public Node bestChild() {
        return children.stream()
                .max(Comparator.comparingDouble(a -> a.visits))
                .orElse(null);
    }
}
