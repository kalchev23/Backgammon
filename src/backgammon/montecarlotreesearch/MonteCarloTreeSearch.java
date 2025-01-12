package backgammon.montecarlotreesearch;

import backgammon.Dice;
import backgammon.GameState;
import backgammon.Move;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MonteCarloTreeSearch {
    private static final int SIMULATION_LIMIT = 1_000; // Number of simulations per move.
    private static final int TIME_LIMIT = 5_000;

    private static Node uctSelect(Node node) {
        double c = Math.sqrt(2); // Exploration parameter.
        return node.children.stream()
                .max(Comparator.comparingDouble(a -> (a.wins / a.visits)
                        + c * Math.sqrt(Math.log(node.visits + 1) / (a.visits + 1))))
                .orElse(null);
    }

    private static Node select(Node node) {
        while (!node.isLeaf() && !node.state.isGameOver()) {
            node = uctSelect(node);
        }
        return node;
    }

    private static void expand(Node node, int startPlayer) {
        Set<Move> possibleMoves = node.state.getPossibleMoves(startPlayer);
        int inactivePlayer = (node.state.getActivePlayer() == 1) ? 2 : 1;
        Random rand = new Random();
        Dice newDice = new Dice(new ArrayList<>(List.of(rand.nextInt(1, 7), rand.nextInt(1, 7))));
        for (Move move : possibleMoves) {
            GameState state = new GameState(node.state);
            state.applyMove(state, move);
            state.setActivePlayer(inactivePlayer);
            state.setDice(newDice);

            Node child = new Node(state, node);
            node.children.add(child);
        }
    }

    private static double simulate(Node node, int startPlayer) {
        Random rand = new Random();
        GameState simulatedState = new GameState(node.state);
        while (!simulatedState.isGameOver()) {
            Set<Move> possibleMoves = simulatedState.getPossibleMoves(startPlayer);

            if (!possibleMoves.isEmpty()) {
                possibleMoves.stream()
                        .skip(rand.nextInt(possibleMoves.size()))
                        .findFirst()
                        .ifPresent(randomMove -> simulatedState.applyMove(simulatedState, randomMove));
            }
            /*simulatedState.board.printBoard();
            System.out.println(simulatedState.dice);
            System.out.println(simulatedState.activePlayer);*/
            //Opponent player PLAYS
            simulatedState.setDice(new Dice(new ArrayList<>(List.of(rand.nextInt(1, 7),
                    rand.nextInt(1, 7)))));
            simulatedState.setActivePlayer((simulatedState.getActivePlayer() == 1) ? 2 : 1);
        }
        return simulatedState.evaluateReward();
    }

    private static void backpropagate(Node node, double reward) {
        while (node != null) {
            node.visits++;
            node.wins += reward;
            reward = -reward; // Alternate reward for opponent.
            node = node.parent;
        }
    }

    private static Node mcts(GameState rootState) {
        Node rootNode = new Node(rootState, null);

        int simulationsCount = 0;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < TIME_LIMIT && simulationsCount < SIMULATION_LIMIT) {
        //for (int i = 0; i < SIMULATION_LIMIT; i++) {
            // 1. Selection
            Node selectedNode = select(rootNode);

            // 2. Expansion
            if (selectedNode.visits != 0) {
                expand(selectedNode, rootState.getActivePlayer());
            }

            // 3. Simulation/Rollout
            double reward = simulate(selectedNode, rootState.getActivePlayer());

            // 4. Backpropagation
            backpropagate(selectedNode, reward);

            simulationsCount++;
        }
        System.out.println("Simulations: " + simulationsCount);

        return rootNode.bestChild();
    }

    public static GameState playMove(GameState rootState) {
        Node bestMoveNode = mcts(rootState);

        if (bestMoveNode != null) {
            System.out.println("Best Move Reward: " + bestMoveNode.wins / bestMoveNode.visits);
            return bestMoveNode.state;
        }
        Random rand = new Random();
        rootState.setDice(new Dice(new ArrayList<>(List.of(rand.nextInt(1, 7),
                rand.nextInt(1, 7)))));
        rootState.setActivePlayer((rootState.getActivePlayer() == 1) ? 2 : 1);
        return rootState;
    }
}
