package TN_Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private int score;
    private Random rand;
    private HttpClient httpClient;

    // Stores the client's socket and creates the http client needed to interact with the API
    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        httpClient = HttpClient.newHttpClient();
    }

    public void run() {
        try {
            // Documenting the setup and client socket reception for this thread, while also configuring TCP communication
            System.out.printf("ClientHandler %s: Socket received, client handover successful. \n", this.getName());
            BufferedReader incoming = new BufferedReader( new InputStreamReader( clientSocket.getInputStream() ) );
            PrintWriter outgoing = new PrintWriter( clientSocket.getOutputStream(), true );

            // Confirmatory Handshake post-handover
            outgoing.println(this.getName());
            String response = incoming.readLine();

            // Closing socket if handshake fails
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
            rand = new Random();

            // Start of game
            while (true) {
                // Generates a random number between 1-100, sends it to the API to get trivia question
                int answer = rand.nextInt(100) + 1;
                String questionString = retrieveQuestion(answer);

                // Formats the question and sends it to the client
                questionString = String.format("Question %s: What is %s?", score+1, questionString);
                outgoing.println(questionString);

                // Checks whether client's answer matches the number, ending the session if it doesn't
                if (!incoming.readLine().equals("" + answer)) {
                    outgoing.printf("Incorrect, the  answer was %s! Nice try, your score was %s\n", answer, score);
                    System.out.printf("ClientHandler %s: Player finished with a score of %s. Terminating connection. (Client: %s:%d)\n",
                            this.getName(), score, clientSocket.getInetAddress(), clientSocket.getPort());
                    return;
                }

                // Increases the score and feeds back to the user if answer is correct
                score++;
                outgoing.printf("Correct! Your score is %s\n", score);
            }
        } catch (IOException ex) {
            System.out.printf("Exception thrown in %s. %s", this.getName(), ex.getMessage());
        }
    }

    // retrieveQuestion: Makes a http call to the numbersapi REST API.
    // In contrast to the client-server connection, this is unencrypted and only uses TCP.
    private String retrieveQuestion(int answer) {
        try {
            // Uses the "fragment" parameter to remove sentence formatting and allow easier use with a wider question/sentence
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://numbersapi.com/" + answer + "/trivia?fragment"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (URISyntaxException | InterruptedException | IOException e) {
            System.out.println("Failed to reach API: " + e);
            return "Sorry, the server is unable to generate questions right now, this is now a guessing game!";
        }
    }
}
