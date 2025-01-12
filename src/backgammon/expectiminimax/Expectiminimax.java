package backgammon.expectiminimax;

import backgammon.GameState;
import backgammon.Move;
import backgammon.Dice;
import backgammon.Board;

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

    private double calculateHeuristic(GameState state, int startPlayer) {
        Board board = state.getBoard();
        int singlesPlayer1 = 0;
        int singlesPlayer2 = 0;
        double pointsPlayer1 = 0.0;
        double pointsPlayer2 = 0.0;
        int[][] boardArr = board.getBoard();
        for (int i = 0; i < 24; i++) {
            if (boardArr[i][0] == 1) {
                if (boardArr[i][1] == 1) {
                    singlesPlayer1++;
                } else if (boardArr[i][1] == 2) {
                    singlesPlayer2++;
                }
            }

            if (boardArr[i][1] == 1) {
                pointsPlayer1 += (1 + i) / 5.0 * boardArr[i][0];
            } else if (boardArr[i][1] == 2) {
                pointsPlayer2 -= (24 - i) / 5.0 * boardArr[i][0];
            }
        }

        double heuristicPlayer1 = pointsPlayer1 + 10 * board.getCheckersOutPlayer1() + board.getHomePlayer1()
                - 5 * singlesPlayer1 - 15 * board.getBarPlayer1();
        double heuristicPlayer2 = pointsPlayer2 - 10 * board.getCheckersOutPlayer2() - board.getHomePlayer2()
                + 5 * singlesPlayer2 + 15 * board.getBarPlayer2();

        double heuristicValue = heuristicPlayer1 + heuristicPlayer2;
        return (startPlayer == 1) ? heuristicValue : -heuristicValue;
    }

    private double expectiminimax(GameState state, int depth, int nodeIndex, int startPlayer) {
        if (depth == DEPTH_LIMIT || state.isGameOver()) {
            return calculateHeuristic(state, startPlayer);
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

    public void playMove(GameState rootState) {
        int activePlayer = rootState.getActivePlayer();
        Move bestMove = findBestMove(rootState, activePlayer);
        if (bestMove != null) {
            rootState.applyMove(rootState, bestMove);
        }
        Random rand = new Random();
        rootState.setDice(new Dice(new ArrayList<>(List.of(rand.nextInt(1, 7),
                rand.nextInt(1, 7)))));
        rootState.setActivePlayer((activePlayer == 1) ? 2 : 1);
    }
}
