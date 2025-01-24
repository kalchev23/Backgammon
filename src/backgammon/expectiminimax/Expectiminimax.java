package backgammon.expectiminimax;

import backgammon.Dice;
import backgammon.GameState;
import backgammon.Heuristic;
import backgammon.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Expectiminimax {
    private enum Node {
        MAX,
        MIN,
        CHANCE
    }

    private static final Node[] NODES = {Node.MAX, Node.CHANCE, Node.MIN, Node.CHANCE};

    private static final int DEPTH_LIMIT = 2;

    private double expectiminimax(GameState state, int depth, int nodeIndex, int startPlayer) {
        if (depth == DEPTH_LIMIT || state.isGameOver()) {
            return Heuristic.calculateHeuristic(state, startPlayer);
        }

        if (NODES[nodeIndex] == Node.MAX) {
            Set<Move> possibleMoves = state.getPossibleMoves(startPlayer);
            if (!possibleMoves.isEmpty()) {
                double maxEval = -Double.MAX_VALUE;
                for (Move move : possibleMoves) {
                    Move revertMove = state.getRevertMove(state, move);
                    state.applyMove(state, move);
                    double eval = expectiminimax(state, depth + 1, (nodeIndex + 1) % 4, startPlayer);
                    state.applyMove(state, revertMove);
                    maxEval = Math.max(maxEval, eval);
                }
                return maxEval;
            }
            return -1.0;
        } else if (NODES[nodeIndex] == Node.MIN) {
            Set<Move> possibleMoves = state.getPossibleMoves(startPlayer);
            if (!possibleMoves.isEmpty()) {
                double minEval = Double.MAX_VALUE;
                for (Move move : possibleMoves) {
                    Move revertMove = state.getRevertMove(state, move);
                    state.applyMove(state, move);
                    double eval = expectiminimax(state, depth + 1, (nodeIndex + 1) % 4, startPlayer);
                    state.applyMove(state, revertMove);
                    minEval = Math.min(minEval, eval);
                }
                return minEval;
            }
            return 1.0;
        } else if (NODES[nodeIndex] == Node.CHANCE) {
            int[] dicePairs = Dice.PAIRS;
            double chancesSum = 0.0;
            for (int i = 0; i < dicePairs.length; i += 2) {
                state.setDice(new Dice(new ArrayList<>(List.of(dicePairs[i], dicePairs[i + 1]))));
                state.setActivePlayer((state.getActivePlayer() == 1) ? 2 : 1);
                chancesSum += expectiminimax(state, depth + 1, (nodeIndex + 1) % 4, startPlayer)
                        * Dice.probability(dicePairs[i], dicePairs[i + 1]); //Node.MIN, Node.MAX
            }
            return chancesSum;
        }

        return 0.0;
    }

    private Move findBestMove(GameState state, int startPlayer) {
        //MAX
        Set<Move> possibleMoves = state.getPossibleMoves(startPlayer);
        Move bestMove = null;
        if (!possibleMoves.isEmpty()) {
            double maxEval = -Double.MAX_VALUE;
            for (Move move : possibleMoves) {
                Move revertMove = state.getRevertMove(state, move);
                state.applyMove(state, move);
                Double eval = expectiminimax(state, 0, 1, startPlayer);
                state.applyMove(state, revertMove);
                if (eval.compareTo(maxEval) > 0) {
                    maxEval = eval;
                    bestMove = move;
                }
            }
        }
        return bestMove;
    }

    public Move playMove(GameState rootState) {
        int activePlayer = rootState.getActivePlayer();
        Move bestMove = findBestMove(rootState, activePlayer);
        if (bestMove != null) {
            rootState.applyMove(rootState, bestMove);
        }
        Random rand = new Random();
        rootState.setDice(new Dice(new ArrayList<>(List.of(rand.nextInt(1, 7),
                rand.nextInt(1, 7)))));
        rootState.setActivePlayer((activePlayer == 1) ? 2 : 1);

        return bestMove;
    }
}
