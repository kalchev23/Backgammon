package backgammon;

public class Heuristic {
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
        for (int i = 0; i < 24; i++) {
            if (boardArr[i][0] == 1) {
                if (i < 12) {
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
                pointsPlayer1 += (1 + i) / 5.0 * boardArr[i][0];
            } else if (boardArr[i][1] == 2) {
                pointsPlayer2 -= (24 - i) / 5.0 * boardArr[i][0];
            }
        }

        double heuristicPlayer1 = pointsPlayer1 + 10 * board.getCheckersOutPlayer1() + board.getHomePlayer1()
                - singlesFirstHalfPlayer1 - 20 * singlesSecondHalfPlayer1 + 15 * doublesPlayer1
                - 1 * board.getBarPlayer1() + 10 * primesPlayer1;
        double heuristicPlayer2 = pointsPlayer2 - 10 * board.getCheckersOutPlayer2() - board.getHomePlayer2()
                + singlesFirstHalfPlayer2 + 20 * singlesSecondHalfPlayer2 - 15 * doublesPlayer2
                + 1 * board.getBarPlayer2() - 10 * primesPlayer2;

        double heuristicValue = heuristicPlayer1 + heuristicPlayer2;
        return (startPlayer == 1) ? heuristicValue : -heuristicValue;
    }
}
