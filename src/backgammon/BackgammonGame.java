package backgammon;

import backgammon.expectiminimax.Expectiminimax;
import backgammon.montecarlotreesearch.MonteCarloTreeSearch;

public class BackgammonGame {
    public static void main(String[] args) {
        GameState currentState = new GameState();
        currentState.getBoard().printBoard();
        System.out.println(currentState.dice);
        System.out.println(currentState.activePlayer);

        Expectiminimax expectiminimax = new Expectiminimax();
        do {
            if (currentState.activePlayer == 1) {
                expectiminimax.playMove(currentState);
            } else {
                currentState = MonteCarloTreeSearch.playMove(currentState);
            }

            currentState.board.printBoard();
            System.out.println(currentState.dice);
            System.out.println(currentState.activePlayer);
        } while(!currentState.isGameOver());
    }
}
