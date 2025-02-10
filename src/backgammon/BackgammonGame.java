package backgammon;

import backgammon.expectiminimax.Expectiminimax;
import backgammon.montecarlotreesearch.MonteCarloTreeSearch;

public class BackgammonGame {
    public static void main(String[] args) {
        GameState currentState = new GameState();
        currentState.getBoard().printBoard();
        System.out.println("ROLLED: " + currentState.dice.diceValues());

        Playable expectiminimax = new Expectiminimax();
        Playable mcts = new MonteCarloTreeSearch();
        do {
            if (currentState.activePlayer == 1) {
                expectiminimax.playMove(currentState);
            } else {
                mcts.playMove(currentState);
            }

            currentState.board.printBoard();
            System.out.println("ROLLED: " + currentState.dice.diceValues());
        } while(!currentState.isGameOver());

        //Play against GNU Backgammon
        //BotAdapter.play(new Expectiminimax());
    }
}
