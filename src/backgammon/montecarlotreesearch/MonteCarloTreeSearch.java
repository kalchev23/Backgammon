package backgammon.montecarlotreesearch;

import backgammon.Dice;
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
        int inactivePlayer = (node.state.activePlayer == 1) ? 2 : 1;
        Random rand = new Random();
        Dice newDice = new Dice(new ArrayList<>(List.of(rand.nextInt(1, 7), rand.nextInt(1, 7))));
        for (Move move : possibleMoves) {
            GameState state = node.state.copy();
            state.applyMove(state, move);
            state.activePlayer = inactivePlayer;
            state.dice = newDice;

            Node child = new Node(state, node);
            node.children.add(child);
        }
    }

    private static double simulate(Node node, int startPlayer) {
        Random rand = new Random();
        GameState simulatedState = node.state.copy();
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
            simulatedState.dice = new Dice(new ArrayList<>(List.of(rand.nextInt(1, 7), rand.nextInt(1, 7))));
            simulatedState.activePlayer = (simulatedState.activePlayer == 1) ? 2 : 1;
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

    public static Node mcts(GameState rootState) {
        Node rootNode = new Node(rootState, null);

        int simulationsCount = 0;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < TIME_LIMIT && simulationsCount < SIMULATION_LIMIT) {
        //for (int i = 0; i < SIMULATION_LIMIT; i++) {
            // 1. Selection
            Node selectedNode = select(rootNode);

            // 2. Expansion
            if (selectedNode.visits != 0) {
                expand(selectedNode, rootState.activePlayer);
            }

            // 3. Simulation/Rollout
            double reward = simulate(selectedNode, rootState.activePlayer);

            // 4. Backpropagation
            backpropagate(selectedNode, reward);

            simulationsCount++;
        }
        System.out.println("Simulations: " + simulationsCount);

        return rootNode.bestChild();
    }

    public static void main(String[] args) {
        GameState currentState = new GameState();
        currentState.board.printBoard();
        System.out.println(currentState.dice);
        System.out.println(currentState.activePlayer);


        /*Node bestMoveNode = mcts(currentState);
        bestMoveNode.state.board.printBoard();
        System.out.println(bestMoveNode.state.dice);
        System.out.println("Best Move Reward: " + bestMoveNode.wins / bestMoveNode.visits);*/

        // Run MCTS to get the best move.
        Node bestMoveNode = null;
        Node previousMoveNode = null;
        do {
            previousMoveNode = bestMoveNode;
            bestMoveNode = mcts(currentState);
            if (bestMoveNode == null) {
                Random rand = new Random();
                currentState.activePlayer = (currentState.activePlayer == 1) ? 2 : 1;
                currentState.dice = new Dice(new ArrayList<>(List.of(rand.nextInt(1, 7), rand.nextInt(1, 7))));
                bestMoveNode = previousMoveNode;

                System.out.println(currentState.dice);
                System.out.println(currentState.activePlayer);
                continue;
            }
            currentState = bestMoveNode.state;

            bestMoveNode.state.board.printBoard();
            System.out.println("Best Move Reward: " + bestMoveNode.wins / bestMoveNode.visits);
            System.out.println();
            System.out.println(bestMoveNode.state.dice);
            System.out.println(bestMoveNode.state.activePlayer);
        }
        while (!bestMoveNode.state.isGameOver());
    }
}
