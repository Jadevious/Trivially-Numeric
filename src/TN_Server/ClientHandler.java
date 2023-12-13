package TN_Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private int score;
    private boolean endGame;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            // Documenting the setup and client socket reception for this thread
            System.out.printf("ClientHandler %s: Socket received, client handover successful. \n", this.getName());
            BufferedReader incoming = new BufferedReader( new InputStreamReader( clientSocket.getInputStream() ) );
            PrintWriter outgoing = new PrintWriter( clientSocket.getOutputStream(), true );	// outgoing

            // Confirmatory Handshake
            outgoing.println(this.getName());
            String response = incoming.readLine();

            if (!response.equals("acknowledged")) {
                System.out.printf("ClientHandler %s: Handshake failed. Terminating connection. (Client: %s:%d)\n",
                        this.getName(), clientSocket.getInetAddress(), clientSocket.getPort());
                return;
            }

            System.out.printf("ClientHandler %s: Handshake successful. (Client: %s:%d) \n",
                    this.getName(), clientSocket.getInetAddress(), clientSocket.getPort());

            // Waiting for client to respond with "begin"
            incoming.readLine();

            score = 0;
            endGame = false;

            // TODO: Begin game
            while (!endGame) {
                String questionString = retrieveQuestion();
                String answer = questionString.split(" ")[0];

                questionString = "Question " + (score+1) + ": " + questionString.replace(answer, "What") + "?";

                outgoing.println(questionString);

                if (!incoming.readLine().equals(answer)) {
                    endGame = true;

                }
                else {
                    score++;
                    outgoing.printf("Correct! Your score is %s\n", score);
                }

            }

            outgoing.printf("Incorrect, nice try! Your score was %s\n", score);

        } catch (IOException ex) {
            System.out.printf("Exception thrown in %s. %s", this.getName(), ex.getMessage());
        }
    }

    private String retrieveQuestion() {
        // TODO: Call API to retrieve question
        return "100000 is the number of thunderstorms that occur in the USA every year, of which 10% are classified as severe.";
    }
}
