package backgammon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The class is responsible for making a connection with GNU Backgammon through sockets.
 * It is used to test the performance of the AI algorithms(MonteCarloTreeSearch and Expectiminimax).
 */
public class BotAdapter {
    private static final int PORT = 2029; // Port for the bot server

    private static String parseBestMove(Move bestMove) {
        if (bestMove == null) {
            return "";
        }

        StringBuilder parsedBestMove = new StringBuilder();
        for (Action action : bestMove.getActions()) {
            int[] from = action.getFrom();
            int[] to = action.getTo();
            String fromIndex = (from == null) ? "bar" : String.valueOf(from[0] + (24 - 2 * from[0]));
            String toIndex = (to == null) ? "off" : String.valueOf(to[0] + (24 - 2 * to[0]));
            parsedBestMove.append(fromIndex).append("/").append(toIndex).append(" ");
        }
        return parsedBestMove.toString().strip();
    }

    public static void play(Playable playable) {
        System.out.println("Starting Backgammon Bot Socket Server...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            // Wait for GNU Backgammon to connect
            Socket socket = serverSocket.accept();
            System.out.println("GNU Backgammon connected!");

            // Setup input and output streams
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from GNU Backgammon: " + inputLine);
                String[] arr = inputLine.split(":");
                Board board = new Board();
                int[][] boardArr = new int[24][2];
                int j = 23;
                for (int i = 0; i < 24; i++) {
                    int count = Integer.parseInt(arr[i + 7]);
                    int player = 0;
                    if (count < 0) {
                        player = 2;
                        count = Math.abs(count);
                    } else if (count > 0) {
                        player = 1;
                    }
                    boardArr[j--] = new int[]{count, player};
                }
                int homePlayer2 = 0;
                for (int i = 0; i < 6; i++) {
                    if (boardArr[i][1] == 2) {
                        homePlayer2 += boardArr[i][0];
                    }
                }
                int homePlayer1 = 0;
                for (int i = 18; i < 24; i++) {
                    if (boardArr[i][1] == 1) {
                        homePlayer1 += boardArr[i][0];
                    }
                }
                board.setBoard(boardArr);
                board.setHomePlayer1(homePlayer1);
                board.setHomePlayer2(homePlayer2);
                board.setBarPlayer1(Integer.parseInt(arr[31]));
                board.setBarPlayer2(Math.abs(Integer.parseInt(arr[6])));
                board.setCheckersOutPlayer1(Integer.parseInt(arr[45]));
                board.setCheckersOutPlayer2(Integer.parseInt(arr[46]));

                int die1 = Integer.parseInt(arr[33]);
                int die2 = Integer.parseInt(arr[34]);
                if (die1 == 0 && die2 == 0) {
                    out.println("roll");
                    continue;
                }
                GameState state = new GameState(board, die1, die2);
                state.getBoard().printBoard();


                Move bestMove = playable.playMove(state);

                // Send the move back to GNU Backgammon
                String parseBestMove = parseBestMove(bestMove);
                out.println(parseBestMove);
                System.out.println("Sent to GNU Backgammon: " + parseBestMove(bestMove));
            }

            System.out.println("Connection closed by GNU Backgammon.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
