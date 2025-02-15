package backgammon.montecarlotreesearch;

import backgammon.Dice;
import backgammon.GameState;
import backgammon.Heuristic;
import backgammon.Move;
import backgammon.Playable;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MonteCarloTreeSearch implements Playable {
    private static final int SIMULATION_LIMIT = 5_000; // Number of simulations per move.
    private static final int TIME_LIMIT = 5_000;
    private static final int THREADS_COUNT = 12;

    private static Node progressiveBiasSelect(Node node, int startPlayer) {
        double c = Math.sqrt(2); // Exploration parameter.

        return node.children.stream()
                .max(Comparator.comparingDouble(a -> (a.wins / a.visits)
                        + c * Math.sqrt(Math.log(node.visits + 1) / (a.visits + 1))
                        + Heuristic.calculateHeuristic(a.state, startPlayer) / (a.visits + 1)))
                .orElse(null);
    }

    private static Node select(Node node, int startPlayer) {
        while (!node.isLeaf() && !node.state.isGameOver()) {
            node = progressiveBiasSelect(node, startPlayer);
        }
        return node;
    }

    private static void expand(Node node, int startPlayer) {
        Set<Move> possibleMoves = node.state.getPossibleMoves(startPlayer);
        int inactivePlayer = (node.state.getActivePlayer() == 1) ? 2 : 1;
        Dice newDice = Dice.of();
        for (Move move : possibleMoves) {
            GameState state = new GameState(node.state);
            state.applyMove(state, move);
            state.setActivePlayer(inactivePlayer);
            state.setDice(newDice);

            Node child = new Node(state, node);
            node.children.add(child);
        }
    }

    private static double evaluateMove(GameState state, Move move, int startPlayer) {
        Move revertMove = state.getRevertMove(state, move);
        state.applyMove(state, move);
        double eval = Heuristic.calculateHeuristic(state, startPlayer);
        state.applyMove(state, revertMove);

        return eval;
    }

    private static double simulate(Node node, int startPlayer) {
        GameState simulatedState = new GameState(node.state);
        while (!simulatedState.isGameOver()) {
            Set<Move> possibleMoves = simulatedState.getPossibleMoves(startPlayer);

            if (!possibleMoves.isEmpty()) {
                possibleMoves.stream()
                        .max(Comparator.comparingDouble(m -> evaluateMove(simulatedState, m,
                                simulatedState.getActivePlayer())))
                        .ifPresent(m -> simulatedState.applyMove(simulatedState, m));
            }
            //Opponent player PLAYS
            simulatedState.switchPlayerTurn();
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
        ExecutorService executor = Executors.newFixedThreadPool(THREADS_COUNT);
        int simulationsPerThread = SIMULATION_LIMIT / THREADS_COUNT;

        // Define a task for each thread
        Runnable mctsTask = () -> {
            int simulationsCount = 0;
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < TIME_LIMIT && simulationsCount < simulationsPerThread) {
                // 1. Selection
                Node selectedNode;
                synchronized (rootNode) {
                    selectedNode = select(rootNode, rootState.getActivePlayer());
                    if (!selectedNode.state.isGameOver()) {
                        expand(selectedNode, rootState.getActivePlayer());
                    }
                }

                // 3. Simulation
                double reward = simulate(selectedNode, rootState.getActivePlayer());

                // 4. Backpropagation
                synchronized (rootNode) {
                    backpropagate(selectedNode, reward);
                }

                simulationsCount++;
            }
        };

        for (int t = 0; t < THREADS_COUNT; t++) {
            executor.execute(mctsTask);
        }

        executor.shutdown();

        //Wait for all tasks to complete
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return rootNode.bestChild();
    }

    @Override
    public Move playMove(GameState rootState) {
        Node bestMoveNode = mcts(rootState);

        Move bestMove = null;
        if (bestMoveNode != null) {
            //System.out.println("Best Move Reward: " + bestMoveNode.wins / bestMoveNode.visits);
            bestMove = bestMoveNode.state.getLastMove();
            rootState.applyMove(rootState, bestMove);
        }
        rootState.switchPlayerTurn();
        return bestMove;
    }
}
