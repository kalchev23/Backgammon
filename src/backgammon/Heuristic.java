package backgammon;

public class Heuristic {
    private static final int CHECKERS_OUT_MULTIPLIER = 10;
    private static final int SINGLES_SECOND_HALF_MULTIPLIER = 20;
    private static final int DOUBLES_MULTIPLIER = 15;
    private static final int BAR_MULTIPLIER = 18;
    private static final int PRIMES_MULTIPLIER = 10;
    private static final double POSITION_MULTIPLIER = 5.0;

    public static double calculateHeuristic(GameState state, int startPlayer) {
        Board board = state.getBoard();
        int singlesFirstHalfPlayer1 = 0;
        int singlesSecondHalfPlayer1 = 0;
        int singlesFirstHalfPlayer2 = 0;
        int singlesSecondHalfPlayer2 = 0;
        double pointsPlayer1 = 0.0;
        double pointsPlayer2 = 0.0;
        int primesPlayer1 = 0;
        int primesPlayer2 = 0;
        int consecutivePlayer1 = 0;
        int consecutivePlayer2 = 0;
        int doublesPlayer1 = 0;
        int doublesPlayer2 = 0;
        int[][] boardArr = board.getBoard();
        for (int i = 0; i < Board.BOARD_CAPACITY; i++) {
            if (boardArr[i][0] == 1) {
                if (i < Board.HALF_BOARD_CAPACITY) {
                    if (boardArr[i][1] == 1) {
                        singlesFirstHalfPlayer1++;
                    } else if (boardArr[i][1] == 2) {
                        singlesSecondHalfPlayer2++;
                    }
                } else {
                    if (boardArr[i][1] == 1) {
                        singlesSecondHalfPlayer1++;
                    } else if (boardArr[i][1] == 2) {
                        singlesFirstHalfPlayer2++;
                    }
                }
            }

            if (boardArr[i][0] >= 2) {
                if (boardArr[i][1] == 1) {
                    doublesPlayer1++;
                    consecutivePlayer1++;
                    if (consecutivePlayer1 >= 2) {
                        primesPlayer1++;
                    }
                } else if (boardArr[i][1] == 2) {
                    doublesPlayer2++;
                    consecutivePlayer2++;
                    if (consecutivePlayer2 >= 2) {
                        primesPlayer2++;
                    }
                }
            } else {
                consecutivePlayer1 = 0;
                consecutivePlayer2 = 0;
            }

            if (boardArr[i][1] == 1) {
                pointsPlayer1 += (1 + i) / POSITION_MULTIPLIER * boardArr[i][0];
            } else if (boardArr[i][1] == 2) {
                pointsPlayer2 -= (Board.BOARD_CAPACITY - i) / POSITION_MULTIPLIER * boardArr[i][0];
            }
        }

        double heuristicPlayer1 = pointsPlayer1 + CHECKERS_OUT_MULTIPLIER * board.getCheckersOutPlayer1()
                + board.getHomePlayer1() - singlesFirstHalfPlayer1
                - SINGLES_SECOND_HALF_MULTIPLIER * singlesSecondHalfPlayer1 + DOUBLES_MULTIPLIER * doublesPlayer1
                - BAR_MULTIPLIER * board.getBarPlayer1() + PRIMES_MULTIPLIER * primesPlayer1;
        double heuristicPlayer2 = pointsPlayer2 - CHECKERS_OUT_MULTIPLIER * board.getCheckersOutPlayer2()
                - board.getHomePlayer2() + singlesFirstHalfPlayer2
                + SINGLES_SECOND_HALF_MULTIPLIER * singlesSecondHalfPlayer2 - DOUBLES_MULTIPLIER * doublesPlayer2
                + BAR_MULTIPLIER * board.getBarPlayer2() - PRIMES_MULTIPLIER * primesPlayer2;

        double heuristicValue = heuristicPlayer1 + heuristicPlayer2;
        return (startPlayer == 1) ? heuristicValue : -heuristicValue;
    }
}
