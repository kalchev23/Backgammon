package backgammon;

import java.util.List;

public class Board {
    private int[][] board; // [count, player] where player = 1 or 2

    private int barPlayer1;
    private int barPlayer2;
    private int homePlayer1;
    private int homePlayer2;
    private int checkersOutPlayer1;
    private int checkersOutPlayer2;

    private final Character player1Symbol;
    private final Character player2Symbol;

    private int maxRows;

    public Board() {
        board = new int[24][2];
        homePlayer1 = 5;
        homePlayer2 = 5;

        initializeBoard();

        player1Symbol = 'X';
        player2Symbol = 'O';

        maxRows = 5;
    }

    public Board(Board board) {
        this.board = new int[board.board.length][];
        for (int i = 0; i < board.board.length; i++) {
            this.board[i] = board.board[i].clone();
        }
        this.barPlayer1 = board.barPlayer1;
        this.barPlayer2 = board.barPlayer2;
        this.homePlayer1 = board.homePlayer1;
        this.homePlayer2 = board.homePlayer2;
        this.checkersOutPlayer1 = board.checkersOutPlayer1;
        this.checkersOutPlayer2 = board.checkersOutPlayer2;

        this.player1Symbol = board.player1Symbol;
        this.player2Symbol = board.player2Symbol;

        this.maxRows = board.maxRows;
    }

    private void initializeBoard() {
        //Player1 initial state
        board[0] = new int[]{2, 1};
        board[11] = new int[]{5, 1};
        board[16] = new int[]{3, 1};
        board[18] = new int[]{5, 1};

        //Player2 initial state
        board[23] = new int[]{2, 2};
        board[12] = new int[]{5, 2};
        board[7] = new int[]{3, 2};
        board[5] = new int[]{5, 2};
    }

    private void updateMaxRows() {
        boolean isChanged = false;
        int tempMaxRows = 5;
        for (int i = 0; i < 24; i++) {
            if (board[i][0] > 5) {
                tempMaxRows = Math.max(tempMaxRows, board[i][0]);
                isChanged = true;
            }
        }
        maxRows = isChanged ? tempMaxRows : 5;
    }

    private void printBoardLine(int rowNumber, int startIndex, int endIndex, boolean isTop, boolean hasOnBar) {
        List<Integer> indicesOfColumns = List.of(50, 46, 42, 38, 34, 30, 21, 17, 13, 9, 5, 1);
        StringBuilder line = new StringBuilder("|                       |   |                       |");
        for (int j = startIndex; j < endIndex; j++) {
            if (board[j][0] > rowNumber) {
                Character symbol = (board[j][1] == 1) ? player1Symbol : player2Symbol;
                int index = isTop ? j : endIndex - j - 1;
                line.setCharAt(indicesOfColumns.get(index), symbol);
            }
        }

        if (hasOnBar) {
            Character barSymbol = isTop ? player1Symbol : player2Symbol;
            line.setCharAt(26, barSymbol);
        }

        System.out.println(line);
    }

    public void printBoard() {
        updateMaxRows();

        System.out.println(" --------------------------------------------------- ");
        System.out.println("|12  11  10  9   8   7  |   | 6   5   4   3   2   1 |");

        int tempBarPlayer1 = barPlayer1;
        for (int row = 0; row < maxRows; row++) { //maxRows 5
            boolean hasOnBar = false;
            if (tempBarPlayer1 > 0 && (maxRows - tempBarPlayer1) <= row) {
                hasOnBar = true;
                tempBarPlayer1--;
            }
            printBoardLine(row, 0, 12, true, hasOnBar);
        }

        System.out.println(" ------------------------BAR------------------------- ");

        int tempBarPlayer2 = barPlayer2;
        for (int row = maxRows - 1; row >= 0; row--) { //maxRows 4
            boolean hasOnBar = false;
            if (tempBarPlayer2 > 0) {
                hasOnBar = true;
                tempBarPlayer2--;
            }
            printBoardLine(row, 12, 24, false, hasOnBar);
        }

        System.out.println("|13  14  15  16  17  18 |   | 19  20  21  22  23  24|");
        System.out.println(" --------------------------------------------------- ");

        System.out.printf("Out: Player 1 - %d, Player 2 - %d%n", checkersOutPlayer1, checkersOutPlayer2);
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public int getBarPlayer1() {
        return barPlayer1;
    }

    public void setBarPlayer1(int barPlayer1) {
        this.barPlayer1 = barPlayer1;
    }

    public int getBarPlayer2() {
        return barPlayer2;
    }

    public void setBarPlayer2(int barPlayer2) {
        this.barPlayer2 = barPlayer2;
    }

    public int getHomePlayer1() {
        return homePlayer1;
    }

    public void setHomePlayer1(int homePlayer1) {
        this.homePlayer1 = homePlayer1;
    }

    public int getHomePlayer2() {
        return homePlayer2;
    }

    public void setHomePlayer2(int homePlayer2) {
        this.homePlayer2 = homePlayer2;
    }

    public int getCheckersOutPlayer1() {
        return checkersOutPlayer1;
    }

    public void setCheckersOutPlayer1(int checkersOutPlayer1) {
        this.checkersOutPlayer1 = checkersOutPlayer1;
    }

    public int getCheckersOutPlayer2() {
        return checkersOutPlayer2;
    }

    public void setCheckersOutPlayer2(int checkersOutPlayer2) {
        this.checkersOutPlayer2 = checkersOutPlayer2;
    }
}
