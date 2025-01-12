package backgammon;

import java.util.ArrayList;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class GameState {
    Board board;
    Dice dice;
    int activePlayer; // 1 or 2
    boolean isTerminal; // True if the game is over.
    double reward = 0.0;

    public GameState() {
        board = new Board();
        Random rand = new Random();
        dice = new Dice(new ArrayList<>(List.of(rand.nextInt(1, 7), rand.nextInt(1, 7))));
        activePlayer = 1;
        isTerminal = false;
    }

    public GameState(GameState state) {
        this.board = new Board(state.board);
        this.dice = state.dice;
        this.activePlayer = state.activePlayer;
        this.isTerminal = state.isTerminal;
        this.reward = state.reward;
    }

    private int getTargetPoint(int from, int die) {
        return (activePlayer == 1) ? from + die : from - die;
    }

    public Set<Move> getPossibleMoves(int startPlayer) {
        Set<Move> possibleMoves = new HashSet<>(128);

        int diceCount = dice.diceValues().size();
        boolean[] usedDice = new boolean[diceCount];
        if (board.getCheckersOutPlayer1() == 15 || board.getCheckersOutPlayer2() == 15) {
            isTerminal = true;
            reward = (startPlayer == activePlayer) ? 0.0 : 1.0;
            return possibleMoves;
        }
        //If all checkers home BEARING OFF
        boolean isBearingOff = false;
        if (activePlayer == 1) {
            if (board.getHomePlayer1() + board.getCheckersOutPlayer1() == 15) {
                isBearingOff = true;
            }
        } else {
            if (board.getHomePlayer2() + board.getCheckersOutPlayer2() == 15) {
                isBearingOff = true;
            }
        }

        Set<Move> possibleMovesOneDieOnly = new HashSet<>();
        generateMoves(this, possibleMoves, possibleMovesOneDieOnly, new ArrayList<>(),
                usedDice, isBearingOff);

        //No possible move with both dice. Get single moves
        if (possibleMoves.isEmpty()) {
            possibleMoves = possibleMovesOneDieOnly;
        }

        return possibleMoves;
    }

    private int countAtHigherPositionInHome(GameState state, int position) {
        int[][] newBoard = state.board.getBoard();
        int count = 0;
        for (int i = position + 1; i < 6; i++) {
            if (newBoard[i][1] == activePlayer) {
                count += newBoard[i][0];
            }
        }

        for (int i = position - 1; i > 17; i--) {
            if (newBoard[i][1] == activePlayer) {
                count += newBoard[i][0];
            }
        }

        return count;
    }

    private boolean isMoveLegal(GameState state, int from, int to, boolean isBearingOff) {
        // Out of bounds
        if (to < 0 || to >= 24) {
            if (to < -1 || to > 24) {
                return isBearingOff && countAtHigherPositionInHome(state, from) == 0;
            }
            return isBearingOff;
        }
        int[][] newBoard = state.board.getBoard();
        return newBoard[to][1] == activePlayer || newBoard[to][0] <= 1;
    }

    private boolean move(GameState move, int from, int to, boolean isFromBar,
                                             boolean isOpponentHit, int i) {
        int inactivePlayer = (activePlayer == 1) ? 2 : 1;

        int activePlayerHome;
        int inactivePlayerHome;
        int activePlayerBar;
        int inactivePlayerBar;
        if (activePlayer == 1) {
            activePlayerHome = move.board.getHomePlayer1();
            inactivePlayerHome = move.board.getHomePlayer2();
            activePlayerBar = move.board.getBarPlayer1();
            inactivePlayerBar = move.board.getBarPlayer2();
            if (from <= 17 && to > 17) {
                activePlayerHome += i;
            }
        } else {
            activePlayerHome = move.board.getHomePlayer2();
            inactivePlayerHome = move.board.getHomePlayer1();
            activePlayerBar = move.board.getBarPlayer2();
            inactivePlayerBar = move.board.getBarPlayer1();
            if (from >= 6 && to < 6) {
                activePlayerHome += i;
            }
        }

        activePlayerBar -= isFromBar ? i : 0;

        int[][] newBoard = move.board.getBoard();
        if ((newBoard[to][1] == inactivePlayer && newBoard[to][0] == 1) || isOpponentHit) {
            //Hit/Revive opponent's blot
            newBoard[to][0] -= i;
            newBoard[to][1] = inactivePlayer;
            inactivePlayerBar += i;

            if (activePlayer == 1) {
                if (to < 6) {
                    inactivePlayerHome -= i;
                }
            } else {
                if (to > 17) {
                    inactivePlayerHome -= i;
                }
            }

            isOpponentHit = true;
        }

        if (i == 1) {
            if (!isFromBar) {
                newBoard[from][0]--;
                if (newBoard[from][0] == 0) {
                    newBoard[from][1] = 0;
                }
            }
            newBoard[to][0]++;
            newBoard[to][1] = activePlayer;
        } else if (i == -1) {
            if (!isFromBar) {
                newBoard[from][0]++;
                newBoard[from][1] = activePlayer;
            }
            newBoard[to][0]--;
            if (newBoard[to][0] == 0) {
                newBoard[to][1] = 0;
            }
        }

        if (activePlayer == 1) {
            move.board.setHomePlayer1(activePlayerHome);
            move.board.setHomePlayer2(inactivePlayerHome);
            move.board.setBarPlayer1(activePlayerBar);
            move.board.setBarPlayer2(inactivePlayerBar);
        } else {
            move.board.setHomePlayer2(activePlayerHome);
            move.board.setHomePlayer1(inactivePlayerHome);
            move.board.setBarPlayer2(activePlayerBar);
            move.board.setBarPlayer1(inactivePlayerBar);
        }
        move.board.setBoard(newBoard);

        return isOpponentHit;
    }

    private Map.Entry<Action, Boolean> makeMove(GameState move, int from, int to, boolean isFromBar) {
        //Move on empty position, on my checkers, on opponents blot
        boolean isOpponentHit = move(move, from, to, isFromBar, false, 1);
        int[][] newBoard = move.board.getBoard();

        Action action = isFromBar ? new Action(-1, -1, -1,
                to, newBoard[to][0], newBoard[to][1]) :
                new Action(from, newBoard[from][0], newBoard[from][1],
                        to, newBoard[to][0], newBoard[to][1]);

        return new AbstractMap.SimpleEntry<>(action, isOpponentHit);
    }

    private void undoMove(GameState move, int from, int to, boolean isFromBar, boolean isOpponentHit) {
        //Undo move on empty position, on my checkers, on opponents blot

        move(move, from, to, isFromBar, isOpponentHit, -1);
    }

    private int[][] moveOut(GameState move, int from, int i) {
        if (activePlayer == 1) {
            move.board.setCheckersOutPlayer1(move.board.getCheckersOutPlayer1() + i);
            move.board.setHomePlayer1(move.board.getHomePlayer1() - i);
        } else {
            move.board.setCheckersOutPlayer2(move.board.getCheckersOutPlayer2() + i);
            move.board.setHomePlayer2(move.board.getHomePlayer2() - i);
        }

        int[][] newBoard = move.board.getBoard();
        newBoard[from][0] -= i;
        if (i == 1 && newBoard[from][0] == 0) {
            newBoard[from][1] = 0;
        }
        else if (i == -1 && newBoard[from][0] == 1) {
            newBoard[from][1] = activePlayer;
        }
        move.board.setBoard(newBoard);

        return newBoard;
    }

    private Action makeMoveOut(GameState move, int from) {
        //Bear off checkers
        int[][] newBoard = moveOut(move, from, 1);

        return new Action(from, newBoard[from][0], newBoard[from][1], -1, -1, -1);
    }

    private void undoMoveOut(GameState move, int from) {
        //Undo bear off checkers
        moveOut(move, from, -1);
    }

    private void generateMoves(GameState state, Set<Move> possibleMoves,
                               Set<Move> possibleMovesOneDieOnly, List<Action> currentMoves,
                               boolean[] usedDice, boolean isBearingOff) {
        if (usedDice.length == 2 && (usedDice[0] && usedDice[1])) {
            possibleMoves.add(new Move(currentMoves, state.board.getHomePlayer1(), state.board.getHomePlayer2(),
                    state.board.getBarPlayer1(), state.board.getBarPlayer2(), state.board.getCheckersOutPlayer1(),
                    state.board.getCheckersOutPlayer2()));
            //possibleMoves.add(currentMoves.getLast().copy());
            return;
        }
        if (usedDice.length == 4 && (usedDice[0] && usedDice[1] && usedDice[2] && usedDice[3])) {
            possibleMoves.add(new Move(currentMoves, state.board.getHomePlayer1(), state.board.getHomePlayer2(),
                    state.board.getBarPlayer1(), state.board.getBarPlayer2(), state.board.getCheckersOutPlayer1(),
                    state.board.getCheckersOutPlayer2()));
            //possibleMoves.add(currentMoves.getLast().copy());
            return;
        }

        for (int i = 0; i < dice.diceValues().size(); i++) {
            if (usedDice[i]) {
                continue;
            }

            boolean isMoveMade = false;
            int activePlayerBar = activePlayer == 1 ? state.board.getBarPlayer1() : state.board.getBarPlayer2();
            if (activePlayerBar > 0) {
                //Player has checkers on the bar
                int homeStartPoint = (activePlayer == 1) ? -1 : 24;
                int targetPoint = getTargetPoint(homeStartPoint, dice.diceValues().get(i));
                if (isMoveLegal(state, homeStartPoint, targetPoint, false)) {
                    Map.Entry<Action, Boolean> actionPair = state.makeMove(
                            state, homeStartPoint, targetPoint, true);

                    Action action = actionPair.getKey();
                    boolean isOpponentHit = actionPair.getValue();

                    currentMoves.add(action);
                    usedDice[i] = true;

                    generateMoves(state, possibleMoves, possibleMovesOneDieOnly,
                            currentMoves, usedDice, false);

                    currentMoves.removeLast();
                    usedDice[i] = false;
                    state.undoMove(state, homeStartPoint, targetPoint,
                            true, isOpponentHit);
                    isMoveMade = true;
                }
            } else {
                for (int point = 0; point < 24; point++) {
                    if (state.board.getBoard()[point][1] == state.activePlayer) {
                        int targetPoint = getTargetPoint(point, dice.diceValues().get(i));
                        if (isMoveLegal(state, point, targetPoint, isBearingOff)) {
                            boolean isMoveOut = false;
                            Map.Entry<Action, Boolean> actionPair;
                            Action action;
                            boolean isOpponentHit = false;
                            if (targetPoint < 0 || targetPoint >= 24) {
                                action = state.makeMoveOut(state, point);
                                isMoveOut = true;
                            } else {
                                actionPair = state.makeMove(state, point, targetPoint, false);
                                action = actionPair.getKey();
                                isOpponentHit = actionPair.getValue();
                            }

                            currentMoves.add(action);
                            usedDice[i] = true;

                            generateMoves(state, possibleMoves, possibleMovesOneDieOnly,
                                    currentMoves, usedDice, isBearingOff);

                            currentMoves.removeLast();
                            usedDice[i] = false;
                            if (isMoveOut) {
                                state.undoMoveOut(state, point);
                            } else {
                                state.undoMove(state, point, targetPoint, false, isOpponentHit);
                            }
                            isMoveMade = true;
                        }
                    }
                }
            }

            if (!isMoveMade && ((usedDice.length == 2  && (usedDice[0] || usedDice[1]))
                    || (usedDice.length == 4 && (usedDice[0] || usedDice[1] || usedDice[2] || usedDice[3])))) {
                possibleMovesOneDieOnly.add(new Move(currentMoves, state.board.getHomePlayer1(),
                        state.board.getHomePlayer2(), state.board.getBarPlayer1(), state.board.getBarPlayer2(),
                        state.board.getCheckersOutPlayer1(), state.board.getCheckersOutPlayer2()));
                    /*for (GameState move : currentMoves) {
                        possibleMovesOneDieOnly.add(move.copy());
                    }*/
                return;
            }
        }
    }

    public void applyMove(GameState state, Move move) {
        int[][] board = state.board.getBoard();
        for (Action action : move.getActions()) {
            int[] from = action.getFrom();
            if (from != null) {
                board[from[0]][0] = from[1];
                board[from[0]][1] = from[2];
            }

            int[] to = action.getTo();
            if (to != null) {
                board[to[0]][0] = to[1];
                board[to[0]][1] = to[2];
            }
        }
        state.board.setBoard(board);
        state.board.setHomePlayer1(move.getHomePlayer1());
        state.board.setHomePlayer2(move.getHomePlayer2());
        state.board.setBarPlayer1(move.getBarPlayer1());
        state.board.setBarPlayer2(move.getBarPlayer2());
        state.board.setCheckersOutPlayer1(move.getCheckersOutPlayer1());
        state.board.setCheckersOutPlayer2(move.getCheckersOutPlayer2());
    }

    public Move getRevertMove(GameState state, Move move) {
        List<Action> revertActions = new ArrayList<>();

        int[][] board = state.board.getBoard();
        for (Action action : move.getActions()) {
            int[] from = action.getFrom();
            int[] revertFrom = {-1, -1, -1};
            if (from != null) {
                revertFrom[0] = from[0];
                revertFrom[1] = board[from[0]][0];
                revertFrom[2] = board[from[0]][1];
            }

            int[] to = action.getTo();
            int[] revertTo = {-1, -1, -1};
            if (to != null) {
                revertTo[0] = to[0];
                revertTo[1] = board[to[0]][0];
                revertTo[2] = board[to[0]][1];
            }

            revertActions.add(new Action(revertFrom[0], revertFrom[1], revertFrom[2],
                    revertTo[0], revertTo[1], revertTo[2]));
        }

        return new Move(revertActions, state.board.getHomePlayer1(), state.board.getHomePlayer2(),
                state.board.getBarPlayer1(), state.board.getBarPlayer2(), state.board.getCheckersOutPlayer1(),
                state.board.getCheckersOutPlayer2());
    }

    public boolean isGameOver() {
        return isTerminal;
    }

    public double evaluateReward() {
        return reward;
    }

    public int getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(int activePlayer) {
        this.activePlayer = activePlayer;
    }

    public void setDice(Dice dice) {
        this.dice = dice;
    }

    public Board getBoard() {
        return board;
    }
}
